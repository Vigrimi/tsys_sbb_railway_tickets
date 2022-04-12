package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.*;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationsTrainSequenceRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.SeatInTrainRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class SeatInTrainService {
    private final SeatInTrainRepository seatInTrainRepository;
    private final RwStationsTrainSequenceRepository trainSequenceRepository;

    @Autowired
    public SeatInTrainService(SeatInTrainRepository seatInTrainRepository,RwStationsTrainSequenceRepository
            trainSequenceRepository){
        this.seatInTrainRepository = seatInTrainRepository;
        this.trainSequenceRepository = trainSequenceRepository;
    }
    //-----------------------------------------------------------------------
    public ResultDto saveNewSeatsInDB(TrainZug newTrainFromDB){
        LOGGER.info("--------------SeatInTrainService---------saveNewSeatsInDB-----started");
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);

        String trainNumber = newTrainFromDB.getNumberTrainNummerZug();
        int trainCapacity = newTrainFromDB.getPassengersCapacityPassagierkapazitat();
        LocalDate dateNow = LocalDate.now();
        // достать из базы нужное расписание по номеру поезда
        RwStationsTrainSequence trainSequence = new RwStationsTrainSequence();
        try {
            trainSequence = trainSequenceRepository.findBySequenceTrainNumber(trainNumber);
        } catch (Exception e){
            // ERROR_DB_MYSTIQUE("ОШИБКА! Что-то пошло не так %() с подключением к базе!")
            resultsEnumList.add(Results.ERROR_DB_MYSTIQUE.getResultText());
            resultDto.setResultsInt(IntConstants.ERROR_INT.getDigits());
            e.printStackTrace();
        }
        String validSequence = trainSequence.getSequenceRwStations();
        // добавлять в базу каждое место поезда в каждом рейсе - с рейсами на 10 ДНЕЙ!!!!!!!!
        for (long i = 0; i <= IntConstants.VOYAGE_LENGTH.getDigits(); i++) {
            LocalDate dateForTrainVoyage = dateNow.plusDays(i); // "yyyy-MM-dd"
            String voyageNumber = dateForTrainVoyage.toString() + "-" + trainNumber;
            ArrayList<SeatInTrain> seatsOneVoyageAList = new ArrayList<>();
            // на каждое место в поезде сохраняем данные
            for (int j = 1; j <= trainCapacity; j++) {
                SeatInTrain seatInTrain = new SeatInTrain();
                seatInTrain.setVoyageNumber(voyageNumber);
                seatInTrain.setSeatTrainSequence(trainSequence);
                seatInTrain.setTrainSeatNumber(j);
                seatInTrain.setSeatSequenceRwStations(validSequence);
                // накапливаем, чтобы класть в БД сразу все места в одном рейсе
                seatsOneVoyageAList.add(seatInTrain);
            }
            try {
                // класть в БД сразу все места в одном рейсе
                seatInTrainRepository.saveAllAndFlush(seatsOneVoyageAList);
                resultDto.setResultsInt(IntConstants.SUCCESS_INT.getDigits());
            } catch (Exception e){
                // TODO класть в лист ошибок seatInTrain который не сохранился, потом пробовать сохранить снова
                // ERROR_DB_MYSTIQUE("ОШИБКА! Что-то пошло не так %() с подключением к базе!")
                resultsEnumList.add(Results.ERROR_DB_MYSTIQUE.getResultText());
                resultDto.setResultsInt(IntConstants.ERROR_INT.getDigits());
                e.printStackTrace();
            }
        }

        if (resultDto.getResultsInt() != IntConstants.ERROR_INT.getDigits() && resultsEnumList.isEmpty()){
            // SUCCESS_NEW_SEATS_SAVED("УСПЕШНО! Новые места сохранены в базе!")
            resultsEnumList.add(Results.SUCCESS_NEW_SEATS_SAVED.getResultText());
            resultDto.setResultsInt(IntConstants.SUCCESS_INT.getDigits());
        }
        LOGGER.info("--------------SeatInTrainService---------saveNewSeatsInDB-----finished");
        return resultDto;
    }

    public ArrayList<Integer> getAvailableSeatsToBuyByVoyage(ArrayList<SeatInTrain> seatsInTrainByVoyage
            ,RailwayStationBahnhof stationDepFmDB,RailwayStationBahnhof stationArrFmDB){
        ArrayList<Integer> availableSeatsToBuy = new ArrayList<>();
        for (SeatInTrain seatInTrain : seatsInTrainByVoyage) {
            if (seatInTrain.getSeatSequenceRwStations().contains(stationDepFmDB.getNameRailwayStationBahnhof()) &&
                    seatInTrain.getSeatSequenceRwStations().contains(stationArrFmDB.getNameRailwayStationBahnhof())){
                availableSeatsToBuy.add(seatInTrain.getTrainSeatNumber());
            }
        }
        return availableSeatsToBuy;
    }

    public boolean sequenceIsAvailableToBuy(String trainSequence,String nameStationDep,String nameStationArr){
        boolean available = false;
        int indexDep = IntConstants.ERROR_INT.getDigits();
        int indexArr = IntConstants.ERROR_INT.getDigits();
        String[] sequenceArray = trainSequence.split(";");
        for (int i = 0; i < sequenceArray.length; i++) {
            if (sequenceArray[i].equals(nameStationDep)){
                indexDep = i;
            }
            if (sequenceArray[i].equals(nameStationArr)){
                indexArr = i;
                break;
            }
        }
        if (indexDep != IntConstants.ERROR_INT.getDigits() && indexArr != IntConstants.ERROR_INT.getDigits()){
            if (indexDep < indexArr){
                available = true;
            }
        }
        return available;
    }

    @Transactional
    public void removeStationsFmSeatSequence(TicketFahrkarte ticketBoughtFmDB){
        String voyageNumber = ticketBoughtFmDB.getTicketDepartureDate() + "-" + ticketBoughtFmDB
                .getTicketNumberTrainFahrkarteNummerZug().getNumberTrainNummerZug();
        int seatNumber = ticketBoughtFmDB.getTicketSeatNumber();
        String nameStationDeparture = ticketBoughtFmDB.getTicketRwStationDeparture().getNameRailwayStationBahnhof();
        String nameStationArrival = ticketBoughtFmDB.getTicketRwStationArrival().getNameRailwayStationBahnhof();
        SeatInTrain seatInTrainFmDB = seatInTrainRepository.findByVoyageNumberAndTrainSeatNumber(voyageNumber,seatNumber);
        String stationsSequence = seatInTrainFmDB.getSeatSequenceRwStations();
        String[] stationsSequenceArray = stationsSequence.split(";");
        ArrayList<String> newStationsSequenceAList = new ArrayList<>();
        // перебрать последовательность станций и удалить станцию отправления и последующие, но не включая станцию
        // прибытия: ZURICH;05:55;05:55;GENEVA;07:22;07:25;BASEL;10:10;10:15;BERN;12:30;12:30
        int indexNameStationDeparture = IntConstants.ERROR_INT.getDigits();
        int indexNameStationArrival = IntConstants.ERROR_INT.getDigits();
        for (int i = 0; i < stationsSequenceArray.length; i+=3) {
            if (stationsSequenceArray[i].equals(nameStationDeparture) &&
                    indexNameStationDeparture == IntConstants.ERROR_INT.getDigits()){
                indexNameStationDeparture = i;
            } else if (stationsSequenceArray[i].equals(nameStationArrival) &&
                    indexNameStationArrival == IntConstants.ERROR_INT.getDigits()){
                indexNameStationArrival = i;
                newStationsSequenceAList.add(stationsSequenceArray[i]);
                newStationsSequenceAList.add(stationsSequenceArray[i+1]);
                newStationsSequenceAList.add(stationsSequenceArray[i+2]);
            } else if (!stationsSequenceArray[i].equals(nameStationDeparture) &&
                    indexNameStationDeparture == IntConstants.ERROR_INT.getDigits()){
                newStationsSequenceAList.add(stationsSequenceArray[i]);
                newStationsSequenceAList.add(stationsSequenceArray[i+1]);
                newStationsSequenceAList.add(stationsSequenceArray[i+2]);
            } else if (!stationsSequenceArray[i].equals(nameStationDeparture) &&
                    indexNameStationDeparture != IntConstants.ERROR_INT.getDigits() &&
                    indexNameStationArrival != IntConstants.ERROR_INT.getDigits() &&
                    i > indexNameStationArrival ){
                newStationsSequenceAList.add(stationsSequenceArray[i]);
                newStationsSequenceAList.add(stationsSequenceArray[i+1]);
                newStationsSequenceAList.add(stationsSequenceArray[i+2]);
            }
        }
        // сформировать новый текст последовательности
        String newStationsSequence = "";
        for (String s : newStationsSequenceAList) {
            newStationsSequence = newStationsSequence + ";" + s;
        }
        newStationsSequence = newStationsSequence.replaceFirst(";","");
        // в базе у места в рейсе исправить последовательность
        // UPDATE `sbb`.`sbb_seat_in_train` SET `seat_sequence_stations` = 'BASEL;10:10;10:15;BERN;12:30;12:30' WHERE (`id` = '5');
        try {
            seatInTrainRepository.updateSeatSequenceStationsById(seatInTrainFmDB.getId(),newStationsSequence);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
