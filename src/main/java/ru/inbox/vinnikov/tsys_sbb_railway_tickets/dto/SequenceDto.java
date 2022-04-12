package ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SequenceDto {
    private String trainNumber;
    private String rwstationName;
    private String arrivalTime;
    private String departureTime;
}
