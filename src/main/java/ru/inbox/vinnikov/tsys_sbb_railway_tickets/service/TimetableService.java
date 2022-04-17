package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RwStationsTrainSequence;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationsTrainSequenceRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TimetableRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces.DateAndTimeHandler;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class TimetableService implements DateAndTimeHandler {
    private final RwStationRepository rwStationRepository;
    private final TimetableRepository timetableRepository;
    private final RwStationsTrainSequenceRepository rwStationsTrainSequenceRepository;

    @Autowired
    public TimetableService(RwStationRepository rwStationRepository,TimetableRepository timetableRepository
            ,RwStationsTrainSequenceRepository rwStationsTrainSequenceRepository) {
        this.rwStationRepository = rwStationRepository;
        this.timetableRepository = timetableRepository;
        this.rwStationsTrainSequenceRepository = rwStationsTrainSequenceRepository;
    }
    //--------------------------------------------------------------
    public int serviceAddNewScheduleHandler(String[] inputedData, TrainZug newTrainFromDB,
                                                          int inputedRouteQtyStations){
        LOGGER.info("------------serviceAddNewScheduleHandler begins----------");
        String resultString = "";
        // коды ошибок для атрибута модели - перенести в константы и собирать ошибки в Лист
        int resultSchedule = -1;
        int indexStations = 0;
        int fullSuccess = 0;
        int errorNoDepartSt = 5;
        int errorNoNextFmDepartSt = 6;
        int errorTimeDepartSt = 7;
        int errorArrTimeMidSt = 8;
        int errorDepTimeMidSt = 9;
        int errorMidStArrTimeOutOf24h = 10;
        int errorMidStDepTimeOutOf24h = 11;
        int errorWrongArrDepTimeMidSt = 12;
        int errorDepStTimeDepOutOf24h = 13;
        int errorNoDepartStDB = 14;
        int errorNoDepartStTime = 15;
        int errorMystiqueTimeDep = 16;
        int errorNoMidStDepTime = 17;
        int errorNoMidStArrTime = 18;
        int errorNoEndSt = 19;
        int errorTwoEqualSt = 20;
        int errorNoEndStTime = 21;
        int errorTimeEndSt = 22;
        int errorNoDepNextFmDepartSt = 23;
        int errorNoMidNextFmDepartSt = 24;
        int errorMystique = 25;
        int errorMystiqueTime = 26;
        int errorMystiqueTimeArr = 27;
        int errorArrStTimeDepOutOf24h = 28;
        int TIME_DIGITS_LENGTH = 5;
        int ALL_DAY_TIME_24HOURS = 2400;
        int DIGITS_22_OCLOCK = 2200;
        int DIGITS_02_OCLOCK = 200;

        ArrayList<TimetableZeitplan> timetablesList = new ArrayList<>();
        ArrayList<RailwayStationBahnhof> rwStationsList = new ArrayList<>();
        String sequenceOfRwStations = "";

        // перебираем введённые сотрудником данные по станциям следования поездом
        for (int i = 0; i < inputedData.length; i+=3) {
            TimetableZeitplan timetableZeitplan = new TimetableZeitplan();
            // !!!!!!!!!!!!!!!!!!!!!!!! станция отправления
            if (i == 0){ // станция отправления
                LOGGER.info("--------------станция отправления----i:" + i);

                if (inputedData[i] == null || inputedData[i].isBlank() || inputedData[i].isEmpty()){
                    // "error", "ОШИБКА! Вы не ввели станцию отправления!"
                    resultSchedule = errorNoDepartSt;
                    break;
                }
                timetableZeitplan.setTrainIdZugId(newTrainFromDB);
                String inputedDepartureStation = inputedData[i].toUpperCase();
                RailwayStationBahnhof rwDepartureStationFmDB =
                        rwStationRepository.findByRwStationName(inputedDepartureStation);
                // если не нашлось, станция из БД будет нал
                if (rwDepartureStationFmDB == null){
                    // "error", "ОШИБКА! Введённая станция отправления не найдена в базе!"
                    resultSchedule = errorNoDepartStDB;
                    break;
                }
                rwStationsList.add(rwDepartureStationFmDB);
                // так как это станция отправления, то текущая и предварит станции в расписании одинаковые
                timetableZeitplan.setPreviousRwstationIdBahnhofId(rwDepartureStationFmDB);
                timetableZeitplan.setCurrentRwstationIdBahnhofId(rwDepartureStationFmDB);
                sequenceOfRwStations = sequenceOfRwStations + rwDepartureStationFmDB.getNameRailwayStationBahnhof() + ";";
                // надо взять следующую станцию
                String inputedNextStationFmDeparture = "";
                if ( (inputedRouteQtyStations - rwStationsList.size() ) > 1 ){
                    inputedNextStationFmDeparture = inputedData[i+2];
                    if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                            inputedNextStationFmDeparture.isBlank()){
                        // "error", "ОШИБКА! Вы не ввели следующую после отправления станцию!"
                        resultSchedule = errorNoDepNextFmDepartSt;
                        break;
                    }
                } else if ( (inputedRouteQtyStations - rwStationsList.size() ) == 1 ){
                    inputedNextStationFmDeparture = inputedData[(inputedData.length-2)];
                    if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                            inputedNextStationFmDeparture.isBlank()){
                        // "error", "ОШИБКА! Вы не ввели конечную станцию!"
                        resultSchedule = errorNoEndSt;
                        break;
                    }
                }

                inputedNextStationFmDeparture = inputedNextStationFmDeparture.toUpperCase();
                RailwayStationBahnhof rwNextStationFmDepartureFmDB =
                        rwStationRepository.findByRwStationName(inputedNextStationFmDeparture);
                // если не нашлось, станция из БД будет нал
                if (rwNextStationFmDepartureFmDB == null){
                    // "error", "ОШИБКА! Введённая следующая после отправления станция не найдена в базе!"
                    resultSchedule = errorNoNextFmDepartSt;
                    break;
                }
                // следующая станция
                rwStationsList.add(rwNextStationFmDepartureFmDB);
                timetableZeitplan.setNextRwstationIdBahnhofId(rwNextStationFmDepartureFmDB);
                indexStations++;

                // обработка времени
                String time = inputedData[i+1];
                // проверка формата введённого времени
                if (time == null || time.isBlank() || time.isEmpty()){
                    // "error", "ОШИБКА! Вы не ввели время отправления у станции отправления!"
                    resultSchedule = errorNoDepartStTime;
                    break;
                }
                if (time.length() != TIME_DIGITS_LENGTH || !time.contains(":")){
                    // "error", "ОШИБКА! Введённое время отправления со станции отправления введено в неправильном формате!"
                    resultSchedule = errorTimeDepartSt;
                    break;
                }
                // проверить, что время в пределах 00:00-23:59
                String timeForInt = time.replaceAll(":","");
                int timeDep = -1;
                try {
                    timeDep = Integer.parseInt(timeForInt);
                } catch (NumberFormatException nfe){
                    // "error", "ОШИБКА! Введённое время отправления со станции отправления введено в неправильном формате!"
                    resultSchedule = errorTimeDepartSt;
                    nfe.printStackTrace();
                    break;
                }
                if (timeDep == -1){
                    // "error", "ОШИБКА! Что-то пошло не так с введённым временем отправления по станции отправления!"
                    resultSchedule = errorMystiqueTimeDep;
                    break;
                }
                if (timeDep >= ALL_DAY_TIME_24HOURS){
                    // "error", "ОШИБКА! Введённое время отправления со станции отправления находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                    resultSchedule = errorDepStTimeDepOutOf24h;
                    break;
                }
                // установить время прибытия и отправления - они равны
                timetableZeitplan.setTrainArrivalTimeZugesAnkunftszeit(time);
                timetableZeitplan.setTrainDepartureTimeZugesAbfahrtszeit(time);
                sequenceOfRwStations = sequenceOfRwStations + time + ";" + time + ";";
                // все данные в расписание по станции отправления введены
                timetablesList.add(timetableZeitplan);
                LOGGER.info("------------serviceAddNewScheduleHandler timetablesList:" + timetablesList);
            }
            // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! промежуточные станции
            else if (i < (inputedData.length-2)){ // промежуточные станции
                if (i == 3) i = 2; // первая станция после станции отправления - не было времени прибытия
                LOGGER.info("--------------промежуточные станции----i:" + i);
                if (inputedData[i] == null && inputedData[i+1] == null && inputedData[i+2] == null){
                    LOGGER.info("--------------промежуточные станции----всё нал, пропускаем");
                } else {
                    timetableZeitplan.setTrainIdZugId(newTrainFromDB);
                    // так как это станция промежуточная, то текущая станция это взять из Листа c этим индексом,
                    // а предыдущая станция в расписании - надо взять из Листа с (индекс минус 1)
                    if (indexStations == 0) indexStations = 1;

                    timetableZeitplan.setPreviousRwstationIdBahnhofId(rwStationsList.get(indexStations-1));
                    timetableZeitplan.setCurrentRwstationIdBahnhofId(rwStationsList.get(indexStations));
                    sequenceOfRwStations = sequenceOfRwStations + timetableZeitplan.getCurrentRwstationIdBahnhofId()
                            .getNameRailwayStationBahnhof() + ";";
                    //          indexStations++;
                    // надо взять следующую станцию
                    String inputedNextStationFmDeparture = "";
                    if ( (inputedRouteQtyStations - rwStationsList.size() ) > 1 ){
                        inputedNextStationFmDeparture = inputedData[i+3];
                        if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                                inputedNextStationFmDeparture.isBlank()){
                            // "error", "ОШИБКА! Вы не ввели промежуточную станцию!"
                            resultSchedule = errorNoMidNextFmDepartSt;
                            break;
                        }
                    } else if ( (inputedRouteQtyStations - rwStationsList.size() ) == 1 ){
                        inputedNextStationFmDeparture = inputedData[(inputedData.length-2)];
                        if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                                inputedNextStationFmDeparture.isBlank()){
                            // "error", "ОШИБКА! Вы не ввели конечную станцию!"
                            resultSchedule = errorNoEndSt;
                            break;
                        }
                    }

                    inputedNextStationFmDeparture = inputedNextStationFmDeparture.toUpperCase();
                    RailwayStationBahnhof rwNextStationFmDepartureFmDB =
                            rwStationRepository.findByRwStationName(inputedNextStationFmDeparture);
                    // если не нашлось, станция из БД будет нал
                    if (rwNextStationFmDepartureFmDB == null){
                        // "error", "ОШИБКА! Введённая промежуточная станция не найдена в базе!"
                        resultSchedule = errorNoNextFmDepartSt;
                        break;
                    }
                    // следующая станция
                    rwStationsList.add(rwNextStationFmDepartureFmDB);
                    timetableZeitplan.setNextRwstationIdBahnhofId(rwNextStationFmDepartureFmDB);
                    indexStations++;

                    // время прибытия и отправления
                    String timeArr = inputedData[i+1];
                    String timeDep = inputedData[i+2];
                    if (timeArr == null || timeArr.isBlank() || timeArr.isEmpty()){
                        // "error", "ОШИБКА! Вы не ввели время прибытия у промежуточной станции!"
                        resultSchedule = errorNoMidStDepTime;
                        break;
                    }
                    if (timeDep == null || timeDep.isBlank() || timeDep.isEmpty()){
                        // "error", "ОШИБКА! Вы не ввели время отправления у промежуточной станции!"
                        resultSchedule = errorNoMidStArrTime;
                        break;
                    }
                    // проверка формата введённого времени
                    if (timeArr.length() != TIME_DIGITS_LENGTH || !timeArr.contains(":")){
                        // "error", "ОШИБКА! Введённое время прибытия на промежуточную станцию введено в неправильном формате!"
                        resultSchedule = errorArrTimeMidSt;
                        break;
                    }
                    if (timeDep.length() != TIME_DIGITS_LENGTH || !timeDep.contains(":")){
                        // "error", "ОШИБКА! Введённое время отправления с промежуточной станции введено в неправильном формате!"
                        resultSchedule = errorDepTimeMidSt;
                        break;
                    }

                    String timeArrTime = timeArr.replaceAll(":","");
                    String timeDepTime = timeDep.replaceAll(":","");
                    LOGGER.info("-------------timeArrTime--timeDepTime-:" + timeArrTime + "**" + timeDepTime);
                    int timeArrTime1 = -1;
                    int timeDepTime1 = -2;
                    try {
                        timeArrTime1 = Integer.parseInt(timeArrTime);
                    } catch (NumberFormatException nfe){
                        // "error", "ОШИБКА! Введённое время прибытия на промежуточную станцию введено в неправильном формате!"
                        resultSchedule = errorArrTimeMidSt;
                        nfe.printStackTrace();
                        break;
                    }
                    try {
                        timeDepTime1 = Integer.parseInt(timeDepTime);
                    } catch (NumberFormatException nfe){
                        // "error", "ОШИБКА! Введённое время отправления с промежуточной станции введено в неправильном формате!"
                        resultSchedule = errorDepTimeMidSt;
                        nfe.printStackTrace();
                        break;
                    }
                    if (timeArrTime1 >= ALL_DAY_TIME_24HOURS){
                        // "error", "ОШИБКА! Введённое время прибытия на промежуточную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                        resultSchedule = errorMidStArrTimeOutOf24h;
                        break;
                    }
                    if (timeDepTime1 >= ALL_DAY_TIME_24HOURS){
                        // "error", "ОШИБКА! Введённое время отправления с промежуточной станции находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                        resultSchedule = errorMidStDepTimeOutOf24h;
                        break;
                    }
                    if (timeArrTime1 != -1 && timeDepTime1 != -2){
                        if (timeArrTime1 > timeDepTime1){
                            // проверка по часу 23:55-00:15 - это не ошибка, что время прибытия раньше, чем время отправления
                            if (timeArrTime1 >= DIGITS_22_OCLOCK && timeDepTime1 <= DIGITS_02_OCLOCK){
                                LOGGER.info("----------время приб и отпр норм, через полночь, timeArrTime1:" +
                                        timeArrTime1 + ", timeDepTime1:" + timeDepTime1);
                            } else {
                                // "error", "ОШИБКА! Введённое время отправления с промежуточной станции раньше, чем
                                // время прибытия!"
                                resultSchedule = errorWrongArrDepTimeMidSt;
                                break;
                            }
                        }
                    } else {
                        // "error", "ОШИБКА! Что-то пошло не так с введённым временем прибытия и отправления по промежуточной станции!"
                        resultSchedule = errorMystiqueTime;
                        break;
                    }

                    // установить время прибытия и отправления по промежуточной станции
                    timetableZeitplan.setTrainArrivalTimeZugesAnkunftszeit(timeArr);
                    timetableZeitplan.setTrainDepartureTimeZugesAbfahrtszeit(timeDep);
                    sequenceOfRwStations = sequenceOfRwStations + timeArr + ";" + timeDep + ";";
                    // все данные в расписание по станции отправления введены
                    timetablesList.add(timetableZeitplan);
                    LOGGER.info("------------serviceAddNewScheduleHandler timetablesList:" + timetablesList);
                }
            }
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! конечная станция
            else if (i == (inputedData.length-2)) { // конечная станция
                LOGGER.info("--------------конечная станция----i:" + i);
                if (inputedData[i] == null || inputedData[i].isEmpty() || inputedData[i].isBlank()){
                    // "error", "ОШИБКА! Вы не ввели конечную станцию!"
                    resultSchedule = errorNoEndSt;
                    break;
                }
                timetableZeitplan.setTrainIdZugId(newTrainFromDB);

                // так как это станция конечная, то текущая и следующая станции в расписании одинаковые
                timetableZeitplan.setCurrentRwstationIdBahnhofId(rwStationsList.get(indexStations));
                timetableZeitplan.setNextRwstationIdBahnhofId(rwStationsList.get(indexStations));
                sequenceOfRwStations = sequenceOfRwStations + timetableZeitplan.getCurrentRwstationIdBahnhofId()
                        .getNameRailwayStationBahnhof() + ";";
                // надо взять предыдущую станцию из Листа
                timetableZeitplan.setPreviousRwstationIdBahnhofId(rwStationsList.get(indexStations-1));
                // обработка времени
                String time = inputedData[i+1];
                // проверка формата введённого времени
                if (time == null || time.isBlank() || time.isEmpty()){
                    // "error", "ОШИБКА! Вы не ввели время прибытия на конечную станцию!"
                    resultSchedule = errorNoEndStTime;
                    break;
                }
                if (time.length() != TIME_DIGITS_LENGTH || !time.contains(":")){
                    // "error", "ОШИБКА! Введённое время прибытия на конечную станцию введено в неправильном формате!"
                    resultSchedule = errorTimeEndSt;
                    break;
                }
                // проверить, что время в пределах 00:00-23:59
                String timeForInt = time.replaceAll(":","");
                int timeDep = -1;
                try {
                    timeDep = Integer.parseInt(timeForInt);
                } catch (NumberFormatException nfe){
                    // "error", "ОШИБКА! Введённое время прибытия на конечную станцию введено в неправильном формате!"
                    resultSchedule = errorTimeEndSt;
                    nfe.printStackTrace();
                    break;
                }
                if (timeDep == -1){
                    // "error", "ОШИБКА! Что-то пошло не так с введённым временем прибытия на конечную станцию!"
                    resultSchedule = errorMystiqueTimeArr;
                    break;
                }
                if (timeDep >= ALL_DAY_TIME_24HOURS){
                    // "error", "ОШИБКА! Введённое время прибытия на конечную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                    resultSchedule = errorArrStTimeDepOutOf24h;
                    break;
                }
                // установить время прибытия и отправления - они равны
                timetableZeitplan.setTrainArrivalTimeZugesAnkunftszeit(time);
                timetableZeitplan.setTrainDepartureTimeZugesAbfahrtszeit(time);
                sequenceOfRwStations = sequenceOfRwStations + time + ";" + time;
                // все данные в расписание по станции отправления введены
                timetablesList.add(timetableZeitplan);
                LOGGER.info("------------serviceAddNewScheduleHandler timetablesList:" + timetablesList);
            }
        }
        LOGGER.info("--TOTAL-----serviceAddNewScheduleHandler-----timetablesList:" + timetablesList);
        LOGGER.info("--TOTAL-----serviceAddNewScheduleHandler-----rwStationList:" + rwStationsList);

        // проверка, подряд две станции не могут быть одинаковыми
        for (int i = 1; i < rwStationsList.size(); i++) {
            if (rwStationsList.get(i).getNameRailwayStationBahnhof()
                    .equalsIgnoreCase(rwStationsList.get(i-1).getNameRailwayStationBahnhof())){
                // "error", "ОШИБКА! Введённая последовательность станций неправильная: две станции назначения подряд не могут быть одинаковыми!"
                resultSchedule = errorTwoEqualSt;
                break;
            }
        }

        // сохранить расписания в БД, если нет ошибок
        // всё ОК: --TOTAL-----serviceAddNewScheduleHandler-----resultSchedule:-1
        LOGGER.info("--сохранить расписания в БД, если нет ошибок(-1)-----serviceAddNewScheduleHandler-----resultSchedule:" + resultSchedule);
        if (resultSchedule == -1){
            try {
                for (TimetableZeitplan timetable : timetablesList) {
                    timetableRepository.save(timetable);
                }
                // "success", "ПОЗДРАВЛЯЮ! Новый поезд удачно сохранён в базу! Расписание и маршрут следования нового
                // поезда также удачно сохранены в базу!"
                resultSchedule = fullSuccess;
            } catch (Exception se){
                // "error", "ОШИБКА! Что-то пошло не так %() Поезд и его маршрут НЕ сохранились в базе!"
                resultSchedule = errorMystique;
                se.printStackTrace();
            }
        }
        // если расписание удачно сохранилось, то сохранить в базу последовательность станций
        RwStationsTrainSequence stationsTrainSequence = new RwStationsTrainSequence();
        if (resultSchedule == fullSuccess){
            stationsTrainSequence.setSequenceTrainNumber(newTrainFromDB.getNumberTrainNummerZug());
            stationsTrainSequence.setSequenceRwStations(sequenceOfRwStations);
            try {
                rwStationsTrainSequenceRepository.save(stationsTrainSequence);
            } catch (Exception se){
                // "error", "ОШИБКА! Что-то пошло не так %() Поезд и его маршрут НЕ сохранились в базе!"
                resultSchedule = errorMystique;
                se.printStackTrace();
            }
        }

        LOGGER.info("--TOTAL-----serviceAddNewScheduleHandler-----stationsTrainSequence:" + stationsTrainSequence);
        LOGGER.info("--TOTAL-----serviceAddNewScheduleHandler-----resultSchedule:" + resultSchedule);
        return resultSchedule;
    }

    public boolean timeAvailableToBuyTicket(String trainSequence,String rwstationDeparture){
        long tenMinutesBeforeDeparture = -10L;
        String[] trainSequenceArray = trainSequence.split(";");
        boolean available = true;
        String timeDepFmSequence = null;
        for (int i = 0; i < trainSequenceArray.length; i++) {
            if (trainSequenceArray[i].equalsIgnoreCase(rwstationDeparture)){
                timeDepFmSequence = trainSequenceArray[i+1];
                break;
            }
        }
        LocalTime timeDeparture = null;
        if (timeDepFmSequence != null){
            timeDeparture = getLocalTimeFromString(timeDepFmSequence);
        }
        LocalTime timeNow = LocalTime.now();
        if (timeDeparture == null){
            available = false;
        } else if (timeDeparture.isBefore(timeNow)){ // поезд уже ушёл
            LOGGER.info("---------------поезд уже ушёл:" + timeDeparture.isBefore(timeNow));
            available = false;
        } else if ( !timeDeparture.isBefore(timeNow) && // менее десяти минут до отправления - билет не купить
                ChronoUnit.MINUTES.between(timeDeparture, timeNow) >= tenMinutesBeforeDeparture ){
            LOGGER.info("------------менее десяти минут:" + ChronoUnit.MINUTES.between(timeDeparture, timeNow));
            available = false;
        }
        LOGGER.info("------------available:" + available);
        return available;
    }
}
