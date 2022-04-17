package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.SequenceDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RwStationsTrainSequence;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationsTrainSequenceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class RwStationsTrainSequenceService {
    private final RwStationRepository rwStationRepository;
    private final RwStationsTrainSequenceRepository sequenceRepository;

    @Autowired
    public RwStationsTrainSequenceService(RwStationRepository rwStationRepository,RwStationsTrainSequenceRepository sequenceRepository) {
        this.rwStationRepository = rwStationRepository;
        this.sequenceRepository = sequenceRepository;
    }

    //-------------------------------------------------------------------------
    public ResultDto findFmToRwstationHandler(String rwstationNameFrom, String rwstationNameTo, String timeFrom,
                                              String timeTo, ArrayList<RwStationsTrainSequence> fullTrainsSequencesAList){
        LOGGER.info("-----------------ResultDto findFmToRwstationHandler started");
//        System.out.println("-------------fullTrainsSequencesAList:" + fullTrainsSequencesAList);
        RailwayStationBahnhof rwstationNameFromFmDB = new RailwayStationBahnhof();
        RailwayStationBahnhof rwstationNameToFmDB = new RailwayStationBahnhof();
        // проверка введённых данных
        // проверить, что введённые названия станций есть в базе
        if (rwstationNameFrom == null || rwstationNameFrom.isEmpty() || rwstationNameFrom.isBlank()){
            rwstationNameFromFmDB = null;
        } else {
            rwstationNameFrom = rwstationNameFrom.trim().toUpperCase();
            rwstationNameFromFmDB = rwStationRepository.findByRwStationName(rwstationNameFrom);
        }

        if (rwstationNameTo == null || rwstationNameTo.isEmpty() || rwstationNameTo.isBlank()){
            rwstationNameToFmDB = null;
        } else {
            rwstationNameTo = rwstationNameTo.trim().toUpperCase();
            rwstationNameToFmDB = rwStationRepository.findByRwStationName(rwstationNameTo);
        }
        timeFrom = timeFrom.trim();
        timeTo = timeTo.trim();
        // перевести полученное время из String в int
        ResultDto resultDtoTimeFrom = getTimeFmStringToInt(timeFrom);
        int timeFromIntInputed = resultDtoTimeFrom.getResultsInt();
        ResultDto resultDtoTimeTo = getTimeFmStringToInt(timeTo);
        int timeToIntInputed = resultDtoTimeTo.getResultsInt();

        ResultDto resultDtoFmTo = new ResultDto();
        // собирать список ошибок
        ArrayList<String> resultsEnumListFmTo = new ArrayList<>();
        resultDtoFmTo.setResultsEnumList(resultsEnumListFmTo);
        // итоговый список для вывода запроса
        ArrayList<SequenceDto> sequenceDtoFmToAList = new ArrayList<>();
        resultDtoFmTo.setSequenceDtoAList(sequenceDtoFmToAList);
        // проверка на ошибки вводимых данных
        if (rwstationNameFromFmDB == null){
            // "error", "ОШИБКА! Введённое название станции отправления отсутствует в базе!"
            resultsEnumListFmTo.add(Results.ERROR_RWSTATION_NAME_FROM_MISSED_IN_DB.getResultText());
        } else if (rwstationNameToFmDB == null){
            // "error", "ОШИБКА! Введённое название станции назначения отсутствует в базе!"
            resultsEnumListFmTo.add(Results.ERROR_RWSTATION_NAME_TO_MISSED_IN_DB.getResultText());
        } else if (resultDtoTimeFrom.getResultsInt() == IntConstants.ERROR_INT.getDigits()){
            // если в проверке времени были ошибки, то добавить их в общий список ошибок
            ArrayList<String> buffer = resultDtoTimeFrom.getResultsEnumList();
            resultsEnumListFmTo.addAll(buffer);
        } else if (resultDtoTimeTo.getResultsInt() == IntConstants.ERROR_INT.getDigits()){
            // если в проверке времени были ошибки, то добавить их в общий список ошибок
            ArrayList<String> buffer = resultDtoTimeTo.getResultsEnumList();
            resultsEnumListFmTo.addAll(buffer);
        } else if (fullTrainsSequencesAList.isEmpty()){
            // "error", "ОШИБКА! В базе следований поездов нет ни одной записи!"
            resultsEnumListFmTo.add(Results.ERROR_SEQUENCE_DB_IS_EMPTY.getResultText());
        } else if (resultsEnumListFmTo.isEmpty()){ // не было ошибок, надо выполнить запрос
            // перебрать все последовательности и найти те, в которых присутствуют обе станции
            boolean successOrNot = false;
            for (RwStationsTrainSequence sequence : fullTrainsSequencesAList) {
                if (sequence.getSequenceRwStations().contains(rwstationNameFromFmDB.getNameRailwayStationBahnhof())
                && sequence.getSequenceRwStations().contains(rwstationNameToFmDB.getNameRailwayStationBahnhof())){
                    // разобрать последовательность станций на элементы массива
                    // на вход приходит String типа: ZURICH;12:23;12:23;GENEVA;23:50;23:58;BASEL;04:04;04:04
                    String[] elementsOfSequence = sequence.getSequenceRwStations().split(";");
                    // если обе станции есть, то надо проверить, что искомая станция отправления раньше, чем назначения
                    int indexFrom = -1;
                    int indexTo = -2;
                    int sequenceTimeFrom = -3;
                    int sequenceTimeTo = -4;
                    int foundSmthng = 0;
                    for (int i = 0; i < elementsOfSequence.length; i++) {
                        if (elementsOfSequence[i].equals(rwstationNameFromFmDB.getNameRailwayStationBahnhof())){
                            indexFrom = i;
                            sequenceTimeFrom = Integer.parseInt(elementsOfSequence[i+2].replaceAll(":",""));
                            foundSmthng++;
                        } else if (elementsOfSequence[i].equals(rwstationNameToFmDB.getNameRailwayStationBahnhof())){
                            indexTo = i;
                            sequenceTimeTo = Integer.parseInt(elementsOfSequence[i+2].replaceAll(":",""));
                            foundSmthng++;
                            break;
                        }
                    }
                    // правильно: если индекс станции ОТ/From меньше чем индекс станции ДО/To
                    // если нашлась хоть одна последовательность по названию станций, то foundSmthng будет два или больше
                    if (foundSmthng > 1){
                        if (indexFrom < indexTo){
                            // проверить временные интервалы - попадаем ли
                            if (timeFromIntInputed <= sequenceTimeFrom && sequenceTimeFrom != -3){
                                if (timeToIntInputed >= sequenceTimeTo && sequenceTimeTo != -4){
                // найден искомый вариант поезда, проходящего от станции A до станции B в заданный промежуток времени
                                    int setStationName = 0;
                                    int setTimeArr = 1;
                                    int setTimeDep = 2;
                                    SequenceDto sequenceDtoBlank = new SequenceDto();
                                    sequenceDtoBlank.setTrainNumber("----------> НАЙДЕН");
                                    sequenceDtoBlank.setRwstationName("---------- ИСКОМЫЙ");
                                    sequenceDtoBlank.setArrivalTime("---------- ВАРИАНТ");
                                    sequenceDtoBlank.setDepartureTime("---- ПОЕЗДА <------");
                                    sequenceDtoFmToAList.add(sequenceDtoBlank);
                                    for (int i = indexFrom; i <= (indexTo+2); i+=3) {
                                        SequenceDto sequenceDto = new SequenceDto();
                                        sequenceDto.setTrainNumber(sequence.getSequenceTrainNumber());
                                        sequenceDto.setRwstationName(elementsOfSequence[i+setStationName]);
                                        sequenceDto.setArrivalTime(elementsOfSequence[i+setTimeArr]);
                                        sequenceDto.setDepartureTime(elementsOfSequence[i+setTimeDep]);
                                        sequenceDtoFmToAList.add(sequenceDto);
                                        successOrNot = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LOGGER.info("-----------ResultDto sequenceDtoFmToAList:" + sequenceDtoFmToAList);
            if (successOrNot){
                resultsEnumListFmTo.add(Results.SUCCESS_SEQUENCE_FROM_TO.getResultText());
                LOGGER.info("---------------ResultDto--------success-------resultDtoFmTo:" + resultDtoFmTo);
            } else {
                resultsEnumListFmTo.add(Results.ERROR_SEQUENCE_FROM_TO_NOT_FOUND.getResultText());
                SequenceDto sequenceDtoBlank = new SequenceDto();
                sequenceDtoBlank.setTrainNumber("=----------> НЕ НАЙДЕН");
                sequenceDtoBlank.setRwstationName("*** ИСКОМЫЙ");
                sequenceDtoBlank.setArrivalTime("*** ВАРИАНТ");
                sequenceDtoBlank.setDepartureTime("*** ПОЕЗДА <---=");
                sequenceDtoFmToAList.add(sequenceDtoBlank);
                LOGGER.info("---------------ResultDto-------Not-success-------resultDtoFmTo:" + resultDtoFmTo);
            }
        }
        LOGGER.info("-----------------ResultDto findFmToRwstationHandler finished");
        return resultDtoFmTo;
    }

    public String[] getDepTimeByDepStationAndArrTimeByArrStationFmRepository(String stationDep, String stationArr
            , TrainZug trainZug){
        String[] result = new String[2];
        RwStationsTrainSequence trainSequence = sequenceRepository
                .findBySequenceTrainNumber(trainZug.getNumberTrainNummerZug());
        String sequence = trainSequence.getSequenceRwStations();
        String[] sequenceDetails = sequence.split(";");
        for (int i = 0; i < sequenceDetails.length; i++) {
            if (sequenceDetails[i].equals(stationDep)){
                result[0] = sequenceDetails[i+2];
            }
            if (sequenceDetails[i].equals(stationArr)){
                result[1] = sequenceDetails[i+1];
            }
        }
        return result;
    }

    public String[] getDepTimeByDepStationAndArrTimeByArrStationFmSequence(String stationDep, String stationArr
            , String sequence){
        String[] result = new String[2];
        String[] sequenceDetails = sequence.split(";");
        for (int i = 0; i < sequenceDetails.length; i++) {
            if (sequenceDetails[i].equals(stationDep)){
                result[0] = sequenceDetails[i+2];
            }
            if (sequenceDetails[i].equals(stationArr)){
                result[1] = sequenceDetails[i+1];
            }
        }
        return result;
    }

    public ResultDto getTimeFmStringToInt(String timeString){
        LOGGER.info("****************---ResultDto getTimeFmStringToInt started");
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);
        // проверка формата введённого времени
        if (timeString == null || timeString.isBlank() || timeString.isEmpty()){
            // "error", "ОШИБКА! Вы не ввели время!"
            resultsEnumList.add(Results.ERROR_TIME_WAS_NOT_INPUTED.getResultText());
            resultDto.setResultsInt(IntConstants.ERROR_INT.getDigits());
        } else
        if (timeString.length() != IntConstants.TIME_DIGITS_LENGTH.getDigits() || !timeString.contains(":")){
            // "error", "ОШИБКА! Введённое время введено в неправильном формате!"
            resultsEnumList.add(Results.ERROR_TIME_WRONG_FORMAT.getResultText());
            resultDto.setResultsInt(IntConstants.ERROR_INT.getDigits());
        } else {
            // проверить, что время в пределах 00:00-23:59
            String timeForInt = timeString.replaceAll(":","");
            int timeBuffer = IntConstants.ERROR_INT.getDigits();
            try {
                timeBuffer = Integer.parseInt(timeForInt);
            } catch (NumberFormatException nfe){
                // "error", "ОШИБКА! Введённое время введено в неправильном формате!"
                resultsEnumList.add(Results.ERROR_TIME_WRONG_FORMAT.getResultText());
                resultDto.setResultsInt(IntConstants.ERROR_INT.getDigits());
                nfe.printStackTrace();
            }

            if (timeBuffer >= IntConstants.ALL_DAY_TIME_24HOURS.getDigits()){
                // "error", "ОШИБКА! Введённое время находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                resultsEnumList.add(Results.ERROR_TIME_OUT_OF_RANGE_24HOURS.getResultText());
                resultDto.setResultsInt(IntConstants.ERROR_INT.getDigits());
            } else {
                // успешно, время введено в правильном формате
                resultsEnumList.add(Results.SUCCESS_TIME_FORMAT_IS_CORRECT.getResultText());
                resultDto.setResultsInt(timeBuffer);
            }
        }
        LOGGER.info("****************---ResultDto getTimeFmStringToInt finished");
        return resultDto;
    }
}
