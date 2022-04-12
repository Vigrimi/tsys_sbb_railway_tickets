package ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum IntConstants {
    TIME_DIGITS_LENGTH(5),
    ALL_DAY_TIME_24HOURS(2400),
    VOYAGE_LENGTH(20),
    ERROR_INT(-1),
    FIRST_ELEMENT_IN_ARRAY(0),
    DEPARTURE_TIME_INDEX_IN_ARRAY(0),
    ARRIVAL_TIME_INDEX_IN_ARRAY(1),
    SUCCESS_INT(99);

    private final int digits;

    IntConstants(int digits)
    {
        this.digits = digits;
    }
}
