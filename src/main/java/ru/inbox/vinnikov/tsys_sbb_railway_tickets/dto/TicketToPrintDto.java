package ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto;

import lombok.*;

@Getter
@Setter
//@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TicketToPrintDto {
    private String namePassenger;
    private String surnamePassenger;
    private String passportNumber;
    private long userId;

    // номер билета - айди билета
    private long ticketNumberId;
    private String trainNumber;
    private double ticketPrice;
    private String departureDate;
    private String rwStationNameDeparture;
    private String ticketDepartureTime;
    private String rwStationNameArrival;
    private String ticketArrivalTime;
    private int seatNumber;

    @Override
    public String toString() {
        return "Билет. " +
                //"\n-------------------------- " +
                "\nНомер билета: " + ticketNumberId + ". " +
                "\nПассажир: " + namePassenger + ' ' + surnamePassenger + ". " +
                "\nНомер паспорта: " + passportNumber + ". " +
                "\nЦена: " + ticketPrice + " Евро. " +
                //"\n========================== " +
                "\n Дата отправления: " + departureDate + ". " +
                "\n Номер поезда: " + trainNumber + ". " +
                "\n Станция отправления: " + rwStationNameDeparture + ". " +
                "\n Время отправления: " + ticketDepartureTime + ". " +
                "\n Станция прибытия: " + rwStationNameArrival + ". " +
                "\n Время прибытия: " + ticketArrivalTime + ". " +
                "\n Номер места: " + seatNumber + ". " +
                "\n {userId=" + userId + '}';
    }
}
