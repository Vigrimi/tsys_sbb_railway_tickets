package ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleOnRwstationDto {
    private String trainNumber;
    private String previousStationName;
    private String currentStationName;
    private String nextStationName;
    private String currentStationArrTime;
    private String currentStationDepTime;
}
