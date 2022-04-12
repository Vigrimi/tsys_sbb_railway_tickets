package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity // сущность Поезд
@Table(name = "sbb_train_zug")
@Getter
@Setter
//@ToString //(exclude = "stationsSequence_stationenSequenz")
public class TrainZug extends SuperclassForEntity {
    // из суперкласса придёт айди и версия

    // Номер поезда
    @Column(name = "number_train_nummer_zug", nullable = false, unique = true)
    private String numberTrainNummerZug;

    // Количество пассажирских мест
    @Column(name = "passengers_capacity_passagierkapazitat", nullable = false)
    @Min(0)
    private int passengersCapacityPassagierkapazitat;

    @Override
    public String toString() {
        return "\nTrain_Zug{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", numberTrain_nummerZug='" + numberTrainNummerZug + '\'' +
                ", passengersCapacity_passagierkapazitat=" + passengersCapacityPassagierkapazitat +
                '}';
    }
}
