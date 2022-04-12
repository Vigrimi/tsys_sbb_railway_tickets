package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity // сущность Последовательность станций следования поезда
@Table(name = "sbb_rwstations_train_sequence")
@Getter
@Setter
public class RwStationsTrainSequence extends SuperclassForEntity {
    // из суперкласса придёт айди и версия

    // Номер поезда, именно номер, не айди
    @Column(name = "number_train_nummer_zug", nullable = false, unique = true)
    private String sequenceTrainNumber;

    // Последовательность станций следования поезда
    @Column(name = "sequence_rwstations", nullable = false)
    private String sequenceRwStations;

    @Override
    public String toString() {
        return "\nRwStationsTrainSequence{"  + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", sequenceTrainNumber='" + sequenceTrainNumber + '\'' +
                ", sequenceRwStations='" + sequenceRwStations + '\'' +
                '}';
    }
}
