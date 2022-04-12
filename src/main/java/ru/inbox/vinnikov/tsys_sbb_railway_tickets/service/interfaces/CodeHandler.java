package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces;

import java.util.Random;

public interface CodeHandler {
    public default int getFourDigitsCode(){
        Random random = new Random();
        // типа код для оплаты билета
        int code = 0;
        int module = 1000;
        for (int i = 0; i < 4; i++) {
            int codeElement = random.nextInt(10);
            if (codeElement == 0 && module == 1000)
                codeElement = 3;
            codeElement = codeElement * module;
            module /= 10;
            code += codeElement;
        }
        return code;
    }
}
