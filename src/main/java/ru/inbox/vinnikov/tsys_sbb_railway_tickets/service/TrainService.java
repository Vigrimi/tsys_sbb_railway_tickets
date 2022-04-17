package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RwStationsTrainSequence;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationsTrainSequenceRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class TrainService {
    private final TrainRepository trainRepository;
    private final TimetableService timetableService;
    private final SeatInTrainService seatInTrainService;
    private final KafkaProducerService kafkaProducerService;
    private final RwStationsTrainSequenceRepository sequenceRepository;

    @Autowired
    public TrainService(TrainRepository trainRepository,TimetableService timetableService,SeatInTrainService
            seatInTrainService,KafkaProducerService kafkaProducerService,RwStationsTrainSequenceRepository
            sequenceRepository) {
        this.trainRepository = trainRepository;
        this.timetableService = timetableService;
        this.seatInTrainService = seatInTrainService;
        this.kafkaProducerService = kafkaProducerService;
        this.sequenceRepository = sequenceRepository;
    }
    //---------------------------------------------------------------
    public String getAllTrainsNumbersStr() {
        ArrayList<TrainZug> allTrainsList = trainRepository.findAll();
        String allTrainsNames = "Список номеров поездов уже имеющихся в базе: ";
        if (allTrainsList.isEmpty()){
            allTrainsNames = "В базу не внесено ни одного поезда!";
        } else {
            for (TrainZug trainZug : allTrainsList) {
                allTrainsNames = allTrainsNames + trainZug.getNumberTrainNummerZug() + "; ";
            }
        }
        return allTrainsNames.trim();
    }

    public int serviceAddNewTrainHandler(String trainNumber, int passengersCapacity,
                                         String[] inputedData, int inputedRouteQtyStations){
        long trainId = 0L;
        int result = -2;
        int successTimetableSavedInDB = 0;
        int errorNoTrainNumber = 1;
        int errorTrainNumberAlreadyExistInDB = 2;
        int successTrainNumberSaved = 3;
        int errorMystique = 4;

        // добавляем каждый новый поезд с рейсами на 20 ДНЕЙ!!!!!!!!! IntConstants VOYAGE_LENGTH(20),
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);

        TrainZug newTrainFromDB = new TrainZug();

        // внести в базу новый номер поезда
        if (trainNumber == null || trainNumber.isBlank() || trainNumber.isEmpty())
        {
            result = errorNoTrainNumber; // "error", "ОШИБКА! Номер поезда не указан!"
        } else {
            TrainZug newTrain = new TrainZug();
            newTrain.setNumberTrainNummerZug(trainNumber);
            newTrain.setPassengersCapacityPassagierkapazitat(passengersCapacity);
            LOGGER.info(LocalDateTime.now() + "\n ------serviceAddNewTrainHandler--------newTrain->" + newTrain);
            try {
                TrainZug trainFromDB = trainRepository.findByNumberTrain(trainNumber);
                LOGGER.info(LocalDateTime.now() + "\n ---serviceAddNewTrainHandler-------trainFromDB->" + trainFromDB);
                if (trainFromDB != null){
                    // "error", "ОШИБКА! Номер такого поезда уже есть в базе!"
                    result = errorTrainNumberAlreadyExistInDB;
                } else {
                    trainRepository.save(newTrain);
                    // "success", "ПОЗДРАВЛЯЕМ! Новый поезд успешно сохранён в базе!"
                    result = successTrainNumberSaved;
                    // надо получить айди нового записанного поезда
                    newTrainFromDB = trainRepository.findByNumberTrain(trainNumber);
                    trainId = newTrainFromDB.getId();
                    // если айди вновь внесённого поезда из БД = 0, то новый поезд не внесён в БД
                    if (trainId == 0){
                        result = errorMystique; // "error", "ОШИБКА! Что-то пошло не так %() Поезд НЕ сохранился в базе!"
                    }
                }
            } catch (Exception se){
                // "error", "ОШИБКА! Что-то пошло не так %() Поезд НЕ сохранился в базе!"
                result = errorMystique;
            }
        }
        // если новый номер поезда удачно внесён, то вносим расписание поезда
        LOGGER.info("--------successTrainNumberSaved = 3-----serviceAddNewTrainHandler result:" + result);
        if (result == successTrainNumberSaved){ // тут result = 3
            // если с расписанием всё ок, то станет result = 0 или будет равен коду ошибки
            result = timetableService.serviceAddNewScheduleHandler(inputedData,newTrainFromDB,inputedRouteQtyStations);
        }
        // если result = 0, значит новое расписание удачно сохранилось, и надо внести в базу подробности по каждому
        // месту в поезде на каждую дату каждого рейса
        if (result == successTimetableSavedInDB && newTrainFromDB != null){
            resultDto = seatInTrainService.saveNewSeatsInDB(newTrainFromDB);
            if (resultDto.getResultsInt() != IntConstants.SUCCESS_INT.getDigits()){
                result = errorMystique;
            } else {
                // отправить в Кафку информацию со списком станций, по которым произошли изменения
                LOGGER.info("-----------------kafkaProducerService.sendStringMsg start " + LocalDateTime.now());
                // варинат1: отправлять команду другому серверу, и в зависиомсти от неё будет что-то выполняться
                // на приёмной стороне
//                kafkaProducerService.sendStringMsg(1L, Results.KAFKA_REFRESH_SCHEDULE.getResultText());

                // варинат2: отправлять конкретный текст для обработки стороной получения
                RwStationsTrainSequence trainSequence = sequenceRepository.findBySequenceTrainNumber(newTrainFromDB
                        .getNumberTrainNummerZug());
                String notice = "ВНИМАНИЕ! По данной станции произошли изменения в расписании. Появился новый проходящий " +
                        "поезд расписании: " + trainSequence.getSequenceRwStations() + ". Обновите поиск для корректного " +
                        "отображения информации по станции.";
                kafkaProducerService.sendStringMsg(1L, notice);

                LOGGER.info("-----------------kafkaProducerService.sendStringMsg finish");
            }
        }

        // если расписание удачно сохранилось, вернётся код успеха = 0, если расписание не сохранилось - удалить
        // из БД поезд и вернуть код ошибки
        if (result != successTimetableSavedInDB){
            trainRepository.delete(newTrainFromDB);
        }
        LOGGER.info("-------serviceAddNewTrainHandler-----success=0----- result:" + result);
        return result;
    }

    public TrainZug getTrainFmDBByTrainNumber(String trainNumber){
        TrainZug trainFmDB = new TrainZug();
        if (trainNumber == null || trainNumber.isBlank() || trainNumber.isEmpty()){
            trainFmDB = null;
        } else
            trainFmDB = trainRepository.findByNumberTrain(trainNumber);

     return trainFmDB;
    }
}
