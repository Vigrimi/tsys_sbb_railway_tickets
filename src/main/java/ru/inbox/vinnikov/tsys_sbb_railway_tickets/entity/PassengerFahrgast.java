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
    private String namePassengerFahrgast;

    // фамилия пассажира
    @Column(name = "surname_passenger_familienname_fahrgast", nullable = false)
    private String surnamePassengerFamiliennameFahrgast;

    // день рождения пассажира
    @Column(name = "birthday_passenger_geburtstag_fahrgast", nullable = false)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") // yyyy-mm-dd
    private String birthdayPassengerGeburtstagFahrgast;

    // номер паспорта
    @Column(name = "passport_number_passenger", nullable = false)
    private String passportNumber;

    // мэйл
    @Column(name = "email_passenger", nullable = false)
    private String emailPassenger;

    // номер мобильного
    @Column(name = "mobile_phone_number_passenger", nullable = false)
    private String mobilePhoneNumberPassenger;

    // айди логина
    @Column(name = "user_id_in_passenger", nullable = false)
    private long userId;
// + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +

    @Override
    public String toString() {
        return "PassengerFahrgast{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", namePassengerFahrgast='" + namePassengerFahrgast + '\'' +
                ", surnamePassengerFamiliennameFahrgast='" + surnamePassengerFamiliennameFahrgast + '\'' +
                ", birthdayPassengerGeburtstagFahrgast='" + birthdayPassengerGeburtstagFahrgast + '\'' +
                ", passportNumber=" + passportNumber +
                ", emailPassenger='" + emailPassenger + '\'' +
                ", mobilePhoneNumberPassenger='" + mobilePhoneNumberPassenger + '\'' +
                ", userId=" + userId +
                '}';
    }
}
