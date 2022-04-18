package ru.inbox.vinnikov.tsys_sbb_railway_tickets.utils;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.exception.NameOfStationNotSavedException;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.exception.NoTrainInDbException;

// это пример как нужно обрабатывать исключения из контроллеров
// исключения надо так обрабатывать, если ты хочешь ввывести сообщение пользователю
// а не придумывать какую то свою систему названия ошибок
@ControllerAdvice
public class RwStationControllerAdvicer {

    @ExceptionHandler(NameOfStationNotSavedException.class)
    public ModelAndView  rwStationException(NameOfStationNotSavedException ex) {
        ModelAndView model = new ModelAndView();
        // чтобы  отображалось то сообщение которое ты хочешь, нужно в
        // NameOfStationNotSavedException сделать контсруктор который принимает нужное сообщение
        // либо тут вместо ex.getMessage() написать то сообщение которо ты хочешь вывести пользователю
        model.addObject("errMsg", ex.getMessage());
        // TODO посмотреть как в html закинуть сообщение об этой ошибке
        model.setViewName("here name of view");
        return model;
    }

    @ExceptionHandler(NoTrainInDbException.class)
    public ModelAndView noTrainException(NameOfStationNotSavedException ex) {
        ModelAndView model = new ModelAndView();
        // чтобы  отображалось то сообщение которое ты хочешь, нужно в
        // NameOfStationNotSavedException сделать контсруктор который принимает нужное сообщение
        // либо тут вместо ex.getMessage() написать то сообщение которо ты хочешь вывести пользователю
        model.addObject("errMsg", "В базу не внесено ни одной станции!");
        // TODO посмотреть как в html закинуть сообщение об этой ошибке
        model.setViewName("here name of view");
        return model;
    }

    //аналогично нужно обработать оснтальные исключения, которые вылетают из RwStationController
}
