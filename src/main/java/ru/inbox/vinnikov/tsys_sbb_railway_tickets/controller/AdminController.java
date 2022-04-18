package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.AdminService;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.PassengerService;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.RwStationService;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.TrainService;

import java.security.Principal;
import java.util.Arrays;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Controller
@RequestMapping("/sbb/v1/admin")
public class AdminController {
    private String allStationsNames;
    private String allTrainsNumbers;
    private int inputedRouteQtyStations;
    private final String[] resultArrAddNewRwSt = {"0","ОШИБКА! Название станции не указано!","ОШИБКА! Название такой станции уже есть в базе!","ПОЗДРАВЛЯЕМ! Новое название станции успешно сохранено в базе!","ОШИБКА! Что-то пошло не так %() название станции НЕ сохранилось в базе!"};
    private final String[] resultArrAddNewTrainSchedule = {"ПОЗДРАВЛЯЕМ! Новый поезд удачно сохранён в базу! Расписание и маршрут следования нового поезда также удачно сохранены в базу!","ОШИБКА! Номер поезда не указан!","ОШИБКА! Номер такого поезда уже есть в базе!","ПОЗДРАВЛЯЕМ! Новый поезд успешно сохранён в базе!","ОШИБКА! Что-то пошло не так >%() Поезд НЕ сохранился в базе!" /*// 4*/,"ОШИБКА! Вы не ввели станцию отправления!" /*// errorNoDepartSt = 5;*/,"ОШИБКА! Введённая следующая после отправления станция не найдена в базе!" /*// errorNoNextFmDepartSt = 6;*/,"ОШИБКА! Введённое время отправления со станции отправления введено в неправильном формате!" /*// errorTimeDepartSt = 7;*/,"ОШИБКА! Введённое время прибытия на промежуточную станцию введено в неправильном формате!" /*// errorArrTimeMidSt = 8;*/,"ОШИБКА! Введённое время отправления с промежуточной станции введено в неправильном формате!" /*// errorDepTimeMidSt = 9;*/,"ОШИБКА! Введённое время прибытия на промежуточную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!" /*// errorMidStArrTimeOutOf24h = 10;*/,"ОШИБКА! Введённое время отправления с промежуточной станции находится вне суток (должно быть в интервале 00:00 - 23:59)!" /*// errorMidStDepTimeOutOf24h = 11;*/,"ОШИБКА! Введённое время отправления с промежуточной станции раньше, чем время прибытия!" /*// errorWrongArrDepTimeMidSt = 12;*/,"ОШИБКА! Введённое время отправления со станции отправления находится вне суток (должно быть в интервале 00:00 - 23:59)!" /*// errorDepStTimeDepOutOf24h = 13;*/,"ОШИБКА! Введённая станция отправления не найдена в базе!" /*// errorNoDepartStDB = 14;*/,"ОШИБКА! Вы не ввели время отправления со станции отправления!" /*// errorNoDepartStTime = 15;*/,"ОШИБКА! Что-то пошло не так с введённым временем отправления по станции отправления!" /*// errorMystiqueTimeDep = 16;*/,"ОШИБКА! Вы не ввели время прибытия у промежуточной станции!" /*// errorNoMidStDepTime = 17;*/,"ОШИБКА! Вы не ввели время отправления у промежуточной станции!" /*// errorNoMidStArrTime = 18;*/,"ОШИБКА! Вы не ввели конечную станцию!" /*// errorNoEndSt = 19;*/,"ОШИБКА! Введённая последовательность станций неправильная: две станции назначения подряд не могут быть одинаковыми!" /*// errorTwoEqualSt = 20;*/,"ОШИБКА! Вы не ввели время прибытия на конечную станцию!" /*// errorNoEndStTime = 21;*/,"ОШИБКА! Введённое время прибытия на конечную станцию введено в неправильном формате!" /*// errorTimeEndSt = 22;*/,"ОШИБКА! Вы не ввели следующую после отправления станцию!" /*// errorNoDepNextFmDepartSt = 23;*/,"ОШИБКА! Вы не ввели промежуточную станцию!" /*// errorNoMidNextFmDepartSt = 24;*/,"ОШИБКА! Что-то пошло не так %() Поезд и его маршрут НЕ сохранились в базе!" /*// errorMystique = 25;*/,"ОШИБКА! Что-то пошло не так с введённым временем прибытия и отправления по промежуточной станции!" /*// errorMystiqueTime = 26;*/,"ОШИБКА! Что-то пошло не так с введённым временем прибытия на конечную станцию!" /*// errorMystiqueTimeArr = 27;*/,"ОШИБКА! Введённое время прибытия на конечную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!" /*// errorArrStTimeDepOutOf24h = 28;*/};
    private final TrainRepository trainRepository;
    private final TrainService trainService;
    private final RwStationService rwStationService;
    private final PassengerService passengerService;
    private final AdminService adminService;

    @Autowired
    public AdminController(TrainRepository trainRepository,TrainService trainService,RwStationService rwStationService
            ,PassengerService passengerService,AdminService adminService)
    {
        this.trainRepository = trainRepository;
        this.trainService = trainService;
        this.rwStationService = rwStationService;
        this.passengerService = passengerService;
        this.adminService = adminService;
    }

    //------------ admin employee ---------------------------------------
    @GetMapping("/account")
    public String adminAccount()
    {
        //"account_admin"; or "redirect:/sbb/v1/admin/account_admin_god"
        return "account_admin";
    }

    // Добавление новых станций


    // Добавление новых поездов: надо ввести НОМЕР ПОЕЗДА и ВМЕСТИМОСТЬ
    @GetMapping("/account/add_new_train")
    public String addNewTrain()
    {
        allStationsNames = rwStationService.getAllStationsNamesStr();
        allTrainsNumbers = trainService.getAllTrainsNumbersStr();
        return "account_adminAdd_new_train";
    }
    @PostMapping("/account/add_new_train_handler") // ввести количество станций в маршруте поезда
    public String addNewTrainHandler(int routeQtyStations)
    {
        inputedRouteQtyStations = routeQtyStations;
        return "redirect:/sbb/v1/admin/account/add_new_train_handler_schedule";
    }
    @GetMapping("/account/add_new_train_handler_schedule") // заполнение поезда и маршрута
    public String addNewTrainHandlerSchedule(Model model)
    {
        model.addAttribute("allstations", allStationsNames);
        model.addAttribute("trainsnames", allTrainsNumbers);
        model.addAttribute("qtystations", inputedRouteQtyStations);
        return "account_adminAdd_new_train_handler_" + inputedRouteQtyStations;
    }
    @PostMapping("/account/add_new_train_handler_schedule_handler") // проверка поезда и маршрута, внесение в БД
    public String addNewTrainHandlerScheduleHandler(
            String trainNumber, int passengersCapacity,
            String beginstation,String beginstation_time,
            String middle1station,String middle1station_timearr,String middle1station_timedep,
            String middle2station,String middle2station_timearr,String middle2station_timedep,
            String middle3station,String middle3station_timearr,String middle3station_timedep,
            String middle4station,String middle4station_timearr,String middle4station_timedep,
            String middle5station,String middle5station_timearr,String middle5station_timedep,
            String middle6station,String middle6station_timearr,String middle6station_timedep,
            String middle7station,String middle7station_timearr,String middle7station_timedep,
            String middle8station,String middle8station_timearr,String middle8station_timedep,
            String endstation,String endstation_time,
            Model model)
    {
        String[] inputedData = {beginstation,beginstation_time,middle1station,
                middle1station_timearr,middle1station_timedep,middle2station,middle2station_timearr,
                middle2station_timedep,middle3station,middle3station_timearr,middle3station_timedep,
                middle4station,middle4station_timearr,middle4station_timedep,middle5station,
                middle5station_timearr,middle5station_timedep,middle6station,middle6station_timearr,
                middle6station_timedep,middle7station,middle7station_timearr,middle7station_timedep,
                middle8station,middle8station_timearr,middle8station_timedep,
                endstation,endstation_time};
        LOGGER.info("--------------------String[] inputedData:" + Arrays.toString(inputedData));

        int result = trainService.serviceAddNewTrainHandler
                (trainNumber, passengersCapacity, inputedData,inputedRouteQtyStations);
        model.addAttribute("result", resultArrAddNewTrainSchedule[result]);
        //return "account_adminAdd_new_train_handler_" + inputedRouteQtyStations;
        return "account_adminAdd_new_train_schedule_result";
    }

    // Просмотр всех поездов
    @GetMapping("/account/find_all_trains")
    public String findAllTrains(Model model)
    {
        model.addAttribute("trains",trainRepository.findAll());
        return "account_adminFind_all_trains";
    }

    //------------ admin_god --------------------------------------------
    @GetMapping("/account_admin_god")
    public String adminGodAccount(Principal principal)
    {
        return adminService.whereToGoIfAdminGoesIntoAdminLevelGod(principal,"account_admin_god"); //whereToGo;
    }

    @GetMapping("/registration_new_admin")
    public String getRegistrationNewAdminForm(Principal principal)
    {
        return adminService.whereToGoIfAdminGoesIntoAdminLevelGod(principal,"registration_new_admin");
    }

    @PostMapping("/registration_handler_newadmin")
    public String registrationNewAdminProcessing(MyUser user, Model model,Principal principal)
    {
        ResultDto resultDto = adminService.getRegistrationNewAdminProcessing(user,principal);
        model.addAttribute("error", resultDto.getResultsEnumList());
        return resultDto.getSomeText(); // whereToGo;
    }
}
