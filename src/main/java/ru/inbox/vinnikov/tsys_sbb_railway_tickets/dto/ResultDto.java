package ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.PassengerFahrgast;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TicketFahrkarte;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class ResultDto {
    private ArrayList<String> resultsEnumList;
    private int resultsInt;
    private ArrayList<SequenceDto> sequenceDtoAList;
    private TicketFahrkarte ticketFahrkarte;
    private TicketToPrintDto ticketToPrintDto;
    private double ticketPrice;
    private String someText;
    private ArrayList<?> someList;
    private ArrayList<PassengerInOneTrainDto> passengerInOneTrainDtoList;
    private ArrayList<TicketToPrintDto> allUserTicketsPast;
    private ArrayList<TicketToPrintDto> allUserTicketsFutureAndToday;
}
