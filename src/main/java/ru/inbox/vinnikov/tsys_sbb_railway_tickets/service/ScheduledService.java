package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TimetableRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final TimetableRepository timetableRepository;

    private final static String END_STATION_RU = "-=КОНЕЧНАЯ=-";

    @Transactional(readOnly = true)
    public List<ScheduleOnRwstationDto> getScheduleByStation(RailwayStationBahnhof stationFromDB) {

        if (stationFromDB == null) {
            return initializeSchedule();
        }

        long rwstationId = stationFromDB.getId();
        List<TimetableZeitplan> schedulesListFromDB = timetableRepository.findAllByCurrentRwstationId(rwstationId);

        if (schedulesListFromDB.isEmpty()) {
            return getNoScheduleByStation();
        }
        return getSchedule(schedulesListFromDB);
    }

    private List<ScheduleOnRwstationDto> getSchedule(List<TimetableZeitplan> schedulesListFromDB) {
        ArrayList<ScheduleOnRwstationDto> schedulesDtoList = new ArrayList<>();

        for (TimetableZeitplan timetable : schedulesListFromDB) {

            ScheduleOnRwstationDto schedule = new ScheduleOnRwstationDto();
            schedule.setTrainNumber(timetable.getTrainIdZugId().getNumberTrainNummerZug());

            if (isPreviouseEqualCurrentStation(timetable)) {
                setCurrentStation(timetable, schedule);
            } else if (isCurrentEqualsNextStation(timetable)) {
                setNextStation(timetable, schedule);
            } else {
                setCurrentStationTime(timetable, schedule);
            }
            schedulesDtoList.add(schedule);
        }
        return schedulesDtoList;
    }

    private void setCurrentStationTime(TimetableZeitplan timetable, ScheduleOnRwstationDto schedule) {
        schedule.setPreviousStationName(timetable.getPreviousRwstationIdBahnhofId().getNameRailwayStationBahnhof());
        schedule.setCurrentStationName(timetable.getCurrentRwstationIdBahnhofId().getNameRailwayStationBahnhof());
        schedule.setNextStationName(timetable.getNextRwstationIdBahnhofId().getNameRailwayStationBahnhof());

        schedule.setCurrentStationArrTime(timetable.getTrainArrivalTimeZugesAnkunftszeit());
        schedule.setCurrentStationDepTime(timetable.getTrainDepartureTimeZugesAbfahrtszeit());
    }

    private void setNextStation(TimetableZeitplan timetable, ScheduleOnRwstationDto schedule) {
        schedule.setPreviousStationName(timetable.getPreviousRwstationIdBahnhofId().getNameRailwayStationBahnhof());
        schedule.setCurrentStationArrTime(timetable.getTrainArrivalTimeZugesAnkunftszeit());
        schedule.setCurrentStationDepTime("----");
        schedule.setNextStationName(END_STATION_RU);
    }

    private void setCurrentStation(TimetableZeitplan timetable, ScheduleOnRwstationDto schedule) {
        schedule.setPreviousStationName("=--->");
        schedule.setCurrentStationArrTime("=--->");
        schedule.setNextStationName(timetable.getNextRwstationIdBahnhofId().getNameRailwayStationBahnhof());
        schedule.setCurrentStationDepTime(timetable.getTrainDepartureTimeZugesAbfahrtszeit());
    }

    private boolean isCurrentEqualsNextStation(TimetableZeitplan timetable) {
        var currentStation = timetable.getCurrentRwstationIdBahnhofId().getNameRailwayStationBahnhof();
        var nextStation = timetable.getNextRwstationIdBahnhofId().getNameRailwayStationBahnhof();
        return currentStation.equals(nextStation);
    }

    private boolean isPreviouseEqualCurrentStation(TimetableZeitplan timetable) {
        var previousStation = timetable.getPreviousRwstationIdBahnhofId().getNameRailwayStationBahnhof();
        var currentStation = timetable.getCurrentRwstationIdBahnhofId().getNameRailwayStationBahnhof();
        return previousStation.equals(currentStation);
    }

    private List<ScheduleOnRwstationDto> getNoScheduleByStation() {
        ScheduleOnRwstationDto schedule = new ScheduleOnRwstationDto();
        schedule.setTrainNumber("по");
        schedule.setPreviousStationName("данной");
        schedule.setCurrentStationArrTime("станции");
        schedule.setCurrentStationDepTime("нет");
        schedule.setNextStationName("поездов");
        schedule.setCurrentStationName("N/A");
        return List.of(schedule);
    }


    private List<ScheduleOnRwstationDto> initializeSchedule() {
        ScheduleOnRwstationDto schedule = new ScheduleOnRwstationDto();
        schedule.setTrainNumber("N/A");
        schedule.setPreviousStationName("N/A");
        schedule.setCurrentStationName("N/A");
        schedule.setNextStationName("N/A");
        schedule.setCurrentStationArrTime("N/A");
        schedule.setCurrentStationDepTime("N/A");
        return List.of(schedule);
    }
}
