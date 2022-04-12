package ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyUserDto {
    private long id;
    private int version;
    private String numberTrainNummerZug;
    private int passengersCapacityPassagierkapazitat;
}
