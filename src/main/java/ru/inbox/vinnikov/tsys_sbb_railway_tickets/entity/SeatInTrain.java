package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity // Сущность Место в поезде (надо для того, чтобы понимать где могут подсаживаться пассажиры)
@Table(name = "sbb_seat_in_train")
@Getter
@Setter
public class SeatInTrain extends SuperclassForEntity {
    // из суперкласса придёт айди и версия

    // номер рейса, формируется конкатенацией даты и номера поезда: 2022-03-20-115С
    @Column(name = "voyage_number", nullable = false) // , unique = true
    private String voyageNumber;

    // последовательности поезда - у многих мест может быть одна и та же Последовательность станций следования поезда
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_train_sequence", nullable = false)
    private RwStationsTrainSequence seatTrainSequence;

    // номер места
    @Column(name = "train_seat_number", nullable = false) // , unique = true
    private int trainSeatNumber;

    // доступная последовательность станций
    @Column(name = "seat_sequence_stations", nullable = false) // , unique = true
    private String seatSequenceRwStations;

}
