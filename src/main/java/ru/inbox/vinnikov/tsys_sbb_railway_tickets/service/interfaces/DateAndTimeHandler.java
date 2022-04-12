package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public interface DateAndTimeHandler {
    public default LocalDate getDateFromString(String dateString){
        // проверка входящего формата yyyy-MM-dd
        String[] dateArray = dateString.split("-");
        LocalDate dateFromString = null;
        if (dateString != null){
            dateString = dateString.trim();
            if (dateArray.length == 3){ // yyyy;MM;dd
                if (dateArray[0].length() == 4){ // yyyy
                    if (dateArray[1].length() == 2){ // MM
                        if (dateArray[2].length() == 2){ // dd
                            // проверка, что всё цифры - убрать цифры и должно остаться "--"
                            String checkIfDigits = dateString.replaceAll("[0-9]", "");
                            if (checkIfDigits.equals("--")){
                                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                //convert String to LocalDate
                                dateFromString = LocalDate.parse(dateString, dateFormatter);
                            }
                        }
                    }
                }
            }
        }

        return dateFromString;
    }

    public default boolean checkIfDateBeforeNow(String dateToCheckString){
        // String dateToCheckString передаются в формате ГГГГ-ММ-ДД
        LocalDate dateNow = LocalDate.now();
        //convert String to LocalDate
        LocalDate dateToCheck = getDateFromString(dateToCheckString);
        boolean before = false;
        if (dateToCheck != null && dateToCheck.isBefore(dateNow))
            before = true;

        return before;
    }

    public default boolean checkIfDateAfterNow(String dateToCheckString){
        // String dateToCheckString передаются в формате ГГГГ-ММ-ДД
        LocalDate dateNow = LocalDate.now();
        //convert String to LocalDate
        LocalDate dateToCheck = getDateFromString(dateToCheckString);
        boolean after = false;
        if (dateToCheck != null && dateToCheck.isAfter(dateNow))
            after = true;

        return after;
    }

    public default boolean checkIfDateIsToday(String dateToCheckString){
        // String dateToCheckString передаются в формате ГГГГ-ММ-ДД
        LocalDate dateNow = LocalDate.now();
        //convert String to LocalDate
        LocalDate dateToCheck = getDateFromString(dateToCheckString);
        boolean today = false;
        if (dateToCheck != null && dateToCheck.isEqual(dateNow))
            today = true;

        return today;
    }

    public default LocalTime getLocalTimeFromString(String timeDepFmSequence){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime timeFromString = LocalTime.parse(timeDepFmSequence,timeFormatter);
        return timeFromString;
    }
}
