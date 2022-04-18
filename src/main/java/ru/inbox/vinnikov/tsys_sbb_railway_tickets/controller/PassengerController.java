package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.PassengerService;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.TrainService;

@Controller
@AllArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;
    private final TrainService trainService;

    @GetMapping("/account/find_all_passengers_in_one_train")
    public String findAllPassengersInOneTrain(Model model)
    {
        //список актуальных номеров поездов
        String allTrainsNumbers = trainService.getAllTrainsNumbersStr();
        model.addAttribute("trainsnames", allTrainsNumbers);
        return "account_adminFind_all_passengers_in_one_train";
    } // Просмотр всех пассажиров, зарегистрированных на поезд
    @PostMapping("/account/find_all_passengers_in_one_train_handler")
    public String findAllPassengersInOneTrainHandler(String trainNumber,String departureDate,Model model){
        ResultDto resultDto = passengerService.serviceFindAllPassengersInOneTrainHandler(trainNumber,departureDate);
        model.addAttribute("trainnumber", trainNumber);
        model.addAttribute("result",resultDto.getResultsEnumList());
        model.addAttribute("passengers",resultDto.getPassengerInOneTrainDtoList());
        return "account_adminFind_all_passengers_in_one_train_handler";
    }
}
