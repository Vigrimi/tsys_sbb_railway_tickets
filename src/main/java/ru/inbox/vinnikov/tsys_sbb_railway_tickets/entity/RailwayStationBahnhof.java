package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity // Сущность Станция
@Table(name = "sbb_railwaystation_bahnhof")
@Getter
@Setter
//@ToString
public class RailwayStationBahnhof extends SuperclassForEntity {
    // из суперкласса придёт айди и версия

    // название жд станции
    @Column(name = "name_rwstation_bahnhof", nullable = false, unique = true)
    private String nameRailwayStationBahnhof;

    // номера поездов с остановками на этой станции
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "stationsSequence_stationenSequenz")
//    @ToString.Exclude
//    private ArrayList<Train_Zug> trainsZugeList;

    @Override
    public String toString() {
        return "\nRailwayStation_Bahnhof{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", name_RailwayStation_Bahnhof='" + nameRailwayStationBahnhof + '\'' +
                '}';
    }
}
