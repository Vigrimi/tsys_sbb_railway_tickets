package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TimetableRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class RwStationService {
    private final RwStationRepository rwStationRepository;
    private final TimetableRepository timetableRepository;
    private final String endStationRU = "-=КОНЕЧНАЯ=-";
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public RwStationService(RwStationRepository rwStationRepository,TimetableRepository timetableRepository
            ,KafkaProducerService kafkaProducerService) {
        this.rwStationRepository = rwStationRepository;
        this.timetableRepository = timetableRepository;
        this.kafkaProducerService = kafkaProducerService;
    }
    //-------------------------------------------------------
    public String getAllStationsNamesStr() {
        ArrayList<RailwayStationBahnhof> allStationsList = rwStationRepository.findAll();
        ArrayList<String> allStationsNamesList = new ArrayList<>();
        for (RailwayStationBahnhof st : allStationsList) {
            allStationsNamesList.add(st.getNameRailwayStationBahnhof());
        }
        Collections.sort(allStationsNamesList);
        String allStationsNames = "Список актуальных станций: ";
        if (allStationsNamesList.isEmpty()){
            allStationsNames = "В базу не внесено ни одной станции!";
        } else {
            for (String rwStation : allStationsNamesList) {
                allStationsNames = allStationsNames + rwStation + "; ";
            }
        }
        return allStationsNames.trim();
    }

    public int serviceAddNewRwstationNameHandler(String rwstationName){
        if (rwstationName == null || rwstationName.isBlank() || rwstationName.isEmpty())
        { // "result", "ОШИБКА! Название станции не указано!"
            return IntConstants.RWSTATION_NAME_NOT_INPUTED.getDigits();
        } else {
            rwstationName = rwstationName.toUpperCase();
            RailwayStationBahnhof newStation = new RailwayStationBahnhof();
            newStation.setNameRailwayStationBahnhof(rwstationName);
            LOGGER.info(LocalDateTime.now() + "\n ---------newStation->" + newStation);
            try {
                RailwayStationBahnhof stationFromDB = rwStationRepository.findByRwStationName(rwstationName);
                LOGGER.info(LocalDateTime.now() + "\n ---------trainFromDB->" + stationFromDB);
                if (stationFromDB != null){
                    // "error", "ОШИБКА! Название такой станции уже есть в базе!"
                    return IntConstants.RWSTATION_NAME_ALREADY_EXISTS_IN_DB.getDigits();
                } else {
                    // проверять бы точно записался?
                    rwStationRepository.save(newStation);
                    // "success", "ПОЗДРАВЛЯЕМ! Новое название станции успешно сохранено в базе!"
                    return IntConstants.NEW_RWSTATION_NAME_SAVED_IN_DB.getDigits();
                }
            } catch (Exception se){
                // "error", "ОШИБКА! Что-то пошло не так %() название станции НЕ сохранилось в базе!"
                return IntConstants.NEW_RWSTATION_NAME_SMTHNG_GOES_WRONG.getDigits();
            }
        }
    }

    public ArrayList<ScheduleOnRwstationDto> getScheduleOnRwstationHandler(String rwstationName){
        LOGGER.info("---start----------------------------------getScheduleOnRwstationHandler-------");
        ArrayList<ScheduleOnRwstationDto> schedulesDtoList = new ArrayList<>();
        // искать станцию в БД - нужен будет айди
        RailwayStationBahnhof stationFromDB = rwStationRepository.findByRwStationName(rwstationName);
        LOGGER.info(LocalDateTime.now() + "\n --------getUserScheduleOnRwstationHandler---stationFromDB->" + stationFromDB);

        if (stationFromDB == null){
            ScheduleOnRwstationDto schedule = new ScheduleOnRwstationDto();
            schedule.setTrainNumber("N/A");
            schedule.setPreviousStationName("N/A");
            schedule.setCurrentStationName("N/A");
            schedule.setNextStationName("N/A");
            schedule.setCurrentStationArrTime("N/A");
            schedule.setCurrentStationDepTime("N/A");
            schedulesDtoList.add(schedule);
        } else {
            long rwstationId = stationFromDB.getId();
            // взять из базы все расписания по станции
            ArrayList<TimetableZeitplan> schedulesListFromDB =
                    timetableRepository.findAllByCurrentRwstationId(rwstationId);
            LOGGER.info("=======getScheduleOnRwstationHandler==================schedulesListFromDB:\n"
                    + schedulesListFromDB);
            // перебрать каждое расписание из БД в ДТО
            if (schedulesListFromDB.isEmpty()){ // станция есть в БД, но по ней нет расписания
                ScheduleOnRwstationDto schedule = new ScheduleOnRwstationDto();
                schedule.setTrainNumber             ("по");
                schedule.setPreviousStationName     ("данной");
                schedule.setCurrentStationArrTime   ("станции");
                schedule.setCurrentStationDepTime   ("нет");
                schedule.setNextStationName         ("поездов");
                schedule.setCurrentStationName("N/A");
                schedulesDtoList.add(schedule);
            } else {
                for (TimetableZeitplan timetable : schedulesListFromDB) {
                    ScheduleOnRwstationDto schedule = new ScheduleOnRwstationDto();
                    schedule.setTrainNumber(timetable.getTrainIdZugId().getNumberTrainNummerZug());
                    if(timetable.getPreviousRwstationIdBahnhofId().getNameRailwayStationBahnhof()
                            .equals(timetable.getCurrentRwstationIdBahnhofId().getNameRailwayStationBahnhof())){
                        schedule.setPreviousStationName("=--->");
                        schedule.setCurrentStationArrTime("=--->");
                        schedule.setNextStationName(timetable.getNextRwstationIdBahnhofId().getNameRailwayStationBahnhof());
                        schedule.setCurrentStationDepTime(timetable.getTrainDepartureTimeZugesAbfahrtszeit());
                    } else
                    if (timetable.getCurrentRwstationIdBahnhofId().getNameRailwayStationBahnhof()
                            .equals(timetable.getNextRwstationIdBahnhofId().getNameRailwayStationBahnhof())){
                        schedule.setPreviousStationName(timetable.getPreviousRwstationIdBahnhofId().getNameRailwayStationBahnhof());
                        schedule.setCurrentStationArrTime(timetable.getTrainArrivalTimeZugesAnkunftszeit());
                        schedule.setCurrentStationDepTime("----");
                        schedule.setNextStationName(endStationRU);
                    } else {
                        schedule.setPreviousStationName(timetable.getPreviousRwstationIdBahnhofId().getNameRailwayStationBahnhof());
                        schedule.setCurrentStationName(timetable.getCurrentRwstationIdBahnhofId().getNameRailwayStationBahnhof());
                        schedule.setNextStationName(timetable.getNextRwstationIdBahnhofId().getNameRailwayStationBahnhof());
                        schedule.setCurrentStationArrTime(timetable.getTrainArrivalTimeZugesAnkunftszeit());
                        schedule.setCurrentStationDepTime(timetable.getTrainDepartureTimeZugesAbfahrtszeit());
                    }
                    schedulesDtoList.add(schedule);
                }
            }
        }
        LOGGER.info("---end----------------------------------getScheduleOnRwstationHandler-------");
        return schedulesDtoList;
    }

    public RailwayStationBahnhof getRwStationFmDBByName(String rwstationName){
        RailwayStationBahnhof trainFmDB = new RailwayStationBahnhof();
        if (rwstationName == null || rwstationName.isBlank() || rwstationName.isEmpty()){
            trainFmDB = null;
        } else
            trainFmDB = rwStationRepository.findByRwStationName(rwstationName);

        return trainFmDB;
    }
}
