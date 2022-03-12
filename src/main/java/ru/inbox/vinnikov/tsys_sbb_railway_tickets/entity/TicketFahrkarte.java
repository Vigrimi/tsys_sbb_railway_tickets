package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private /*String*/ TrainZug ticketNumberTrain_fahrkarteNummerZug; // тут Train_Zug - это айди поезда в билете

    // Пассажир в билете - у многих билетов может быть один и тот же Пассажир
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_passenger_id_fahrkarte_fahrgast_id", nullable = false)
//     НЕ РЕАЛИЗОВАНО в одном билете может быть одни пассажир, но у одного пассажира может быть много билетов
//    @Column(name = "ticket_passenger_id_fahrkarte_fahrgast_id", nullable = false)
    private /*long*/ PassengerFahrgast ticketPassengerId_fahrkarteFahrgastId; // тут айди пассажира

    @Override
    public String toString() {
        return "Ticket_Fahrkarte{" + "id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", ticketNumberTrain_fahrkarteNummerZug='" + ticketNumberTrain_fahrkarteNummerZug + '\'' +
                ", ticketPassengerId_fahrkarteFahrgastId=" + ticketPassengerId_fahrkarteFahrgastId +
                '}';
    }
}
