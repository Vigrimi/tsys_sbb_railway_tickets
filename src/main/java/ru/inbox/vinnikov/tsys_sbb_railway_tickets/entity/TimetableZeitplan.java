package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity // Сущность Расписание
@Table(name = "sbb_timetable_zeitplan")
@Getter
@Setter
//@ToString
public class TimetableZeitplan extends SuperclassForEntity {
    // из суперкласса придёт айди и версия

    // айди номера поезда
    // Номер поезда в расписании - у многих расписаний может быть один и тот же поезд
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id_zug_id", nullable = false)
//    @Column(name = "train_id_zug_id", nullable = false)
    private TrainZug trainIdZugId;

    // айди предыдущей станции, чтобы понять маршрут и направление следования
    // Номер станции в расписании - у многих расписаний может быть одна и та же станция
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_rwstation_id_bahnhof_id", nullable = false)
//  @Column(name = "previous_rwstation_id_bahnhof_id", nullable = false)
    private RailwayStationBahnhof previousRwstationIdBahnhofId;

    // айди текущей станции, чтобы понять маршрут и направление следования
    // Номер станции в расписании - у многих расписаний может быть одна и та же станция
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_rwstation_id_bahnhof_id", nullable = false)
//  @Column(name = "current_rwstation_id_bahnhof_id", nullable = false)
    private RailwayStationBahnhof currentRwstationIdBahnhofId;

    // айди следующей станции, чтобы понять маршрут и направление следования
    // Номер станции в расписании - у многих расписаний может быть одна и та же станция
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_rwstation_id_bahnhof_id", nullable = false)
//  @Column(name = "next_rwstation_id_bahnhof_id", nullable = false)
    private RailwayStationBahnhof nextRwstationIdBahnhofId;

    // время прибытия поезда на текущую станцию: чч:мм в формате 24ч
    @Column(name = "train_arrival_time_zuges_ankunftszeit", nullable = false)
    @Pattern(regexp = "\\d{2}:\\d{2}") // hh:mm
    private String trainArrivalTimeZugesAnkunftszeit;

    // время отправления поезда с текущей станции: чч:мм в формате 24ч
    @Column(name = "train_departure_time_zuges_abfahrtszeit", nullable = false)
    @Pattern(regexp = "\\d{2}:\\d{2}") // hh:mm
    private String trainDepartureTimeZugesAbfahrtszeit;

    @Override
    public String toString() {
        return "\nTimetableZeitplan{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", trainIdZugId=" + trainIdZugId +
                ", previousRwstationIdBahnhofId=" + previousRwstationIdBahnhofId +
                ", currentRwstationIdBahnhofId=" + currentRwstationIdBahnhofId +
                ", nextRwstationIdBahnhofId=" + nextRwstationIdBahnhofId +
                ", trainArrivalTime_ZugesAnkunftszeit=" + trainArrivalTimeZugesAnkunftszeit +
                ", trainDepartureTime_ZugesAbfahrtszeit='" + trainDepartureTimeZugesAbfahrtszeit + '\'' +
                '}';
    }
}
