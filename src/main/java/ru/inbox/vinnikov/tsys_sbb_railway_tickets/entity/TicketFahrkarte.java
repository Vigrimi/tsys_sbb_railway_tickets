package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity // Сущность Билет
@Table(name = "sbb_ticket_fahrkarte")
@Getter
@Setter
//@ToString
public class TicketFahrkarte extends SuperclassForEntity { // Номер поезда * Пассажир
    // из суперкласса придёт айди и версия

    // Номер поезда в билете - у многих билетов может быть один и тот же поезд
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_train_number_fahrkarte_zug_nummer", nullable = false)
    // НЕ РЕАЛИЗОВАНО в одном билете только один номер поезда, но у одного поезда много билетов
    //@Column(name = "ticket_train_number_fahrkarte_zug_nummer", nullable = false)
    private /*String*/ TrainZug ticketNumberTrainFahrkarteNummerZug; // тут Train_Zug - это айди поезда в билете

    // Пассажир в билете - у многих билетов может быть один и тот же Пассажир
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_passenger_id_fahrkarte_fahrgast_id", nullable = false)
//     НЕ РЕАЛИЗОВАНО в одном билете может быть одни пассажир, но у одного пассажира может быть много билетов
//    @Column(name = "ticket_passenger_id_fahrkarte_fahrgast_id", nullable = false)
    private /*long*/ PassengerFahrgast ticketPassengerIdFahrkarteFahrgastId; // тут айди пассажира

    // цена билета
    @Column(name = "ticket_price", nullable = false)
    private double ticketPrice;

    // номер места в билете
    @Column(name = "ticket_seat_number", nullable = false)
    private int ticketSeatNumber;

    // дата отправления в формате ГГГГ-ММ-ДД
    @Column(name = "ticket_departure_date", nullable = false)
    private String ticketDepartureDate;

    // станция отправления - у многих билетов может быть одна и та же Станция
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_rwstation_departure", nullable = false)
    private RailwayStationBahnhof ticketRwStationDeparture;

    // время отправления
    @Column(name = "ticket_departure_time", nullable = false)
    @Pattern(regexp = "\\d{2}:\\d{2}") // hh:mm
    private String ticketDepartureTime;

    // станция назначения
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_rwstation_arrival", nullable = false)
    private RailwayStationBahnhof ticketRwStationArrival;

    // время прибытия
    @Column(name = "ticket_arrival_time", nullable = false)
    @Pattern(regexp = "\\d{2}:\\d{2}") // hh:mm
    private String ticketArrivalTime;

    // "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
    @Override
    public String toString() {
        return "Ticket (Fahrkarte){" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", ticket Number Train (Fahrkarte Nummer Zug)=" + ticketNumberTrainFahrkarteNummerZug +
                ", ticket Passenger Id (Fahrkarte Fahrgast Id)=" + ticketPassengerIdFahrkarteFahrgastId +
                ", ticket Price=" + ticketPrice +
                ", ticket Seat Number=" + ticketSeatNumber +
                ", ticket Departure Date=" + ticketDepartureDate +
                ", ticket RwStation Departure=" + ticketRwStationDeparture +
                ", ticket Departure Time='" + ticketDepartureTime + '\'' +
                ", ticket RwStation Arrival=" + ticketRwStationArrival +
                ", ticket Arrival Time='" + ticketArrivalTime + '\'' +
                '}';
    }
}
