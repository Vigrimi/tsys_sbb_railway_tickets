package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

@Entity // Сущность Пассажир
@Table(name = "sbb_passenger_fahrgast")
@Getter
@Setter
//@ToString
public class PassengerFahrgast extends SuperclassForEntity {
    // из суперкласса придёт айди и версия

    // имя пассажира
    @Column(name = "name_passenger_fahrgast", nullable = false)
    private String name_passenger_fahrgast;

    // фамилия пассажира
    @Column(name = "surname_passenger_familienname_fahrgast", nullable = false)
    private String surnamePassenger_familiennameFahrgast;

    // день рождения пассажира
    @Column(name = "birthday_passenger_geburtstag_fahrgast", nullable = false)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") // yyyy-mm-dd
    private String birthdayPassenger_geburtstagFahrgast;

    // айди логина, пока НЕ РЕАЛИЗОВАНО


    @Override
    public String toString() {
        return "Passenger_Fahrgast{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                "name_passenger_fahrgast='" + name_passenger_fahrgast + '\'' +
                ", surnamePassenger_familiennameFahrgast='" + surnamePassenger_familiennameFahrgast + '\'' +
                ", birthdayPassenger_geburtstagFahrgast='" + birthdayPassenger_geburtstagFahrgast + '\'' +
                '}';
    }
}
