package ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Results {
    KAFKA_REFRESH_SCHEDULE("kafkaRefreshSchedule"),
    CONGRATULATION("ПОЗДРАВЛЯЮ!)"),
    SUCCESS_TIME_FORMAT_IS_CORRECT("УСПЕШНО! Время введено в правильном формате!"),
    SUCCESS_NEW_PASSENGER_SAVED("УСПЕШНО! Новый пассажир сохранён в базе!"),
    SUCCESS_NEW_SEATS_SAVED("УСПЕШНО! Новые места сохранены в базе!"),
    SUCCESS_NEW_TICKET_BOOKED("УСПЕШНО! Новый билет забронирован, у Вас есть 5 минут на оплату. Вам отправлен код, введите его для оплаты билета!"),
    SUCCESS_NEW_TICKET_BOUGHT("УСПЕШНО! Новый билет оплачен! Копия билета отправлена на электронную почту."),
    SUCCESS_SEQUENCE_FROM_TO("УСПЕШНО! Найден поезд, проходящий от станции A до станции B в заданный промежуток времени!"),
    SUCCESS_PASSENGER_ON_TRAIN_FONUD("УСПЕШНО! Найдены пассажиры!"),
    ERROR_SEQUENCE_FROM_TO_NOT_FOUND("Упс, Не найден ни один поезд, проходящий от станции A до станции B в заданный промежуток времени!"),
    ERROR_PASSENGER_ON_TRAIN_NOT_FONUD("Упс, у выбранного поезда и даты нет ни одного пассажира!"),
    ERROR_TRAIN_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели номер поезда!"),
    ERROR_TRAIN_ABSENT_IN_DB("ОШИБКА! Введённый номер поезда отсутствует в базе!"),
    ERROR_TIME_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели время!"),
    ERROR_TIME_OUT_OF_RANGE_24HOURS("ОШИБКА! Введённое время находится вне суток (должно быть в интервале 00:00 - 23:59)!"),
    ERROR_TIME_WRONG_FORMAT("ОШИБКА! Введённое время введено в неправильном формате!"),
    ERROR_TIME_LATE_BUY_TICKET("ОШИБКА! Невозможно оформить билет (поезд уже ушёл или до отправления осталось менее десяти минут)!"),
    ERROR_INPUTED_WRONG_CODE("ОШИБКА! Вы ввели неправильный код!"),
    ERROR_NAME_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели имя!"),
    ERROR_SAVE_NEW_PASSENGER_MYSTIQUE("ОШИБКА! Что-то пошло не так %() Новый пассажир НЕ сохранился в базе!"),
    ERROR_SAVE_NEW_TICKET_MYSTIQUE("ОШИБКА! Что-то пошло не так %() Новый билет НЕ сохранился в базе!"),
    ERROR_DB_MYSTIQUE("ОШИБКА! Что-то пошло не так %() с подключением к базе!"),
    ERROR_NEW_TICKET_WRONG_PASSENGER("ОШИБКА! Выбран неправильный пассажир!"),
    ERROR_NEW_TICKET_DOUBLED_PASSENGER("ОШИБКА! Один и тот же пассажир не может купить два билета на один и тот же поезд!"),
    ERROR_NEW_TICKET_NO_SEATS("ОШИБКА! На выбранном поезде и маршруте нет свободных мест!"),
    PROCESS_NEW_TICKET_SELECT_SEAT("Процесс покупки билета продолжается. Надо выбрать место в поезде."),
    ERROR_SURNAME_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели фамилию!"),
    ERROR_PASSPORT_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели номер паспорта!"),
    ERROR_PHONE_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели номер телефона!"),
    ERROR_EMAIL_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели адрес электронной почты!"),
    ERROR_BIRTHDAY_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели дату рождения (проверьте правильность вводимого формата)!"),
    ERROR_DATE_WRONG_FORMAT("ОШИБКА! Введённая дата введена в неправильном формате!"),
    ERROR_DATE_IS_BEFORE_NOW("ОШИБКА! Введённая дата находится в прошлом (ранее текущей даты)!"),
    ERROR_RWSTATION_NAME_FROM_MISSED_IN_DB("ОШИБКА! Введённое название станции отправления отсутствует в базе!"),
    ERROR_RWSTATION_NAME_TO_MISSED_IN_DB("ОШИБКА! Введённое название станции назначения отсутствует в базе!"),
    ERROR_RWSTATION_ARRIVAL_BEFORE_DEPARTURE("ОШИБКА! Введённ неправиьный порядок станций: название станции назначения должно быть после станции отправления!"),
    ERROR_LOGIN_LENGTH("ОШИБКА! Длина логина должна быть более 5ти символов!"),
    ERROR_PASSWORD_LENGTH("ОШИБКА! Длина пароля должна быть более 8ми символов!"),
    ERROR_ADMIN_ALREADY_EXISTS("ОШИБКА! Сотрудник с таким именем (логином) уже существует!"),
    ERROR_TIMETABLE_DB_IS_EMPTY("Упс, В базе расписаний нет ни одной записи!"),
    ERROR_SEQUENCE_DB_IS_EMPTY("Упс, В базе следований поездов нет ни одной записи!"),
    MAIL_SUBJECT_SEND_CODE("SBB: секретный код внутри"),
    MAIL_SUBJECT_NEW_TICKET("SBB: Ваш новый билет"),
    MAIL_TEXT_CODE_TO_BUY_TICKET("Для покупки билета введите этот код (4 цифры): ");

    private final String resultText;

    Results(String resultText)
    {
        this.resultText = resultText;
    }

}
