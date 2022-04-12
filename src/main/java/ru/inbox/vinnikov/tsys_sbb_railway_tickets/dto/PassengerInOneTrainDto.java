package ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PassengerInOneTrainDto {
    // номер по порядку
    private int numberInOrder;

    // айди(номер) билета
    private long ticketId;

    // имя пассажира
    private String passengerName;

    // фамилия пассажира
    private String passengerSurname;

    // номер поезда
    private String trainNumber;

    // дата отправления
    private String departureDate;

    // номер места
    private int ticketSeatNumber;

    // айди пассажира
    private long passengerId;

}
