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
    private String numberTrain_nummerZug;

    // Количество пассажирских мест
    @Column(name = "passengers_capacity_passagierkapazitat", nullable = false)
    @Min(0)
    private int passengersCapacity_passagierkapazitat;

    // Станции следования у поезда
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "stations_sequence_stationen_sequenz",
//    joinColumns = @JoinColumn(name = "train_zug_id", referencedColumnName = "id"), // это айди поезда Train_Zug
//    inverseJoinColumns = @JoinColumn(name = "rwstation_bahnhof_id", referencedColumnName = "id")) // это айди станции RailwayStation_Bahnhof
//    @ToString.Exclude
//    private ArrayList<RailwayStation_Bahnhof> stationsSequence_stationenSequenz;

    @Override
    public String toString() {
        return "Train_Zug{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", numberTrain_nummerZug='" + numberTrain_nummerZug + '\'' +
                ", passengersCapacity_passagierkapazitat=" + passengersCapacity_passagierkapazitat +
                '}';
    }
}
