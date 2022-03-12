package ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Languages {
    DEUTSCH(1),
    RUSSIAN(2),
    ENGLISH(3);

    private final int languageId;

    Languages(int languageId)
    {
        this.languageId = languageId;
    }

}
