package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.exception.NoNameStationException;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.exception.NoTrainInDbException;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.exception.StationExistInDBException;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
@RequiredArgsConstructor
public class RwStationService {
    private final RwStationRepository rwStationRepository;
    private final ScheduledService scheduledService;


    @Transactional(readOnly = true)
    public String getAllStationsNamesStr() {
        checkAnyStationInDb();
        return rwStationRepository.findAll()
                                  .stream()
                                  .map(RailwayStationBahnhof::getNameRailwayStationBahnhof)
                                  .collect(Collectors.joining("; ", "Список актуальных станций: ", ""));
    }

    @Transactional()
    public void serviceAddNewRwstationNameHandler(String rwstationName) {
        validationStationName(rwstationName);

        rwstationName = rwstationName.toUpperCase();
        checkStationInDBByName(rwstationName);

        RailwayStationBahnhof newStation = new RailwayStationBahnhof();
        newStation.setNameRailwayStationBahnhof(rwstationName);

        rwStationRepository.save(newStation);
    }

    @Transactional(readOnly = true)
    public List<ScheduleOnRwstationDto> getScheduleOnRwstationHandler(String rwstationName) {
        RailwayStationBahnhof stationFromDB = rwStationRepository.findByRwStationName(rwstationName);
        LOGGER.info(LocalDateTime.now() + "\n --------getUserScheduleOnRwstationHandler---stationFromDB->" + stationFromDB);
        return scheduledService.getScheduleByStation(stationFromDB);
    }

    @Transactional(readOnly = true)
    public RailwayStationBahnhof getRwStationFmDBByName(String rwstationName) {
        validationStationName(rwstationName);
        return rwStationRepository.findByRwStationName(rwstationName);
    }

    private void checkAnyStationInDb() {
        if (rwStationRepository.findAll().isEmpty()) {
            throw new NoTrainInDbException();
        }
    }

    private void validationStationName(String rwstationName) {
        if (rwstationName == null || rwstationName.isBlank() || rwstationName.isEmpty()) { // "result", "ОШИБКА! Название станции не указано!"
            throw new NoNameStationException();
        }
    }

    private void checkStationInDBByName(String rwstationName) {
        RailwayStationBahnhof stationFromDB = rwStationRepository.findByRwStationName(rwstationName);
        LOGGER.info(LocalDateTime.now() + "\n ---------trainFromDB->" + stationFromDB);
        if (stationFromDB != null) {
            // "error", "ОШИБКА! Название такой станции уже есть в базе!"
            throw new StationExistInDBException();
        }
    }
}
