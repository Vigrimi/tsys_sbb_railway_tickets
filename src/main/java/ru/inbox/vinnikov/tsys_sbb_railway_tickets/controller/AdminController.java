package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.PassengerInOneTrainDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RoleRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.UserRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.ControllerService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Controller
@RequestMapping("/sbb/v1/admin")
public class AdminController {
    @Value("${admin.levelgod}")
    private String adminLevelGod;
    private String allStationsNames;
    private String allTrainsNumbers;
    private int inputedRouteQtyStations;
    private final String[] resultArrAddNewRwSt = {"0"
            ,"ОШИБКА! Название станции не указано!"
            ,"ОШИБКА! Название такой станции уже есть в базе!"
            ,"ПОЗДРАВЛЯЕМ! Новое название станции успешно сохранено в базе!"
            ,"ОШИБКА! Что-то пошло не так %() название станции НЕ сохранилось в базе!"};
    private final String[] resultArrAddNewTrainSchedule = {
            "ПОЗДРАВЛЯЕМ! Новый поезд удачно сохранён в базу! Расписание и маршрут следования нового поезда также удачно сохранены в базу!"
            ,"ОШИБКА! Номер поезда не указан!"
            ,"ОШИБКА! Номер такого поезда уже есть в базе!"
            ,"ПОЗДРАВЛЯЕМ! Новый поезд успешно сохранён в базе!"
            ,"ОШИБКА! Что-то пошло не так >%() Поезд НЕ сохранился в базе!" // 4
            ,"ОШИБКА! Вы не ввели станцию отправления!" // errorNoDepartSt = 5;
            ,"ОШИБКА! Введённая следующая после отправления станция не найдена в базе!" // errorNoNextFmDepartSt = 6;
            ,"ОШИБКА! Введённое время отправления со станции отправления введено в неправильном формате!" // errorTimeDepartSt = 7;
            ,"ОШИБКА! Введённое время прибытия на промежуточную станцию введено в неправильном формате!" // errorArrTimeMidSt = 8;
            ,"ОШИБКА! Введённое время отправления с промежуточной станции введено в неправильном формате!" // errorDepTimeMidSt = 9;
            ,"ОШИБКА! Введённое время прибытия на промежуточную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!" // errorMidStArrTimeOutOf24h = 10;
            ,"ОШИБКА! Введённое время отправления с промежуточной станции находится вне суток (должно быть в интервале 00:00 - 23:59)!" // errorMidStDepTimeOutOf24h = 11;
            ,"ОШИБКА! Введённое время отправления с промежуточной станции раньше, чем время прибытия!" // errorWrongArrDepTimeMidSt = 12;
            ,"ОШИБКА! Введённое время отправления со станции отправления находится вне суток (должно быть в интервале 00:00 - 23:59)!" // errorDepStTimeDepOutOf24h = 13;
            ,"ОШИБКА! Введённая станция отправления не найдена в базе!" // errorNoDepartStDB = 14;
            ,"ОШИБКА! Вы не ввели время отправления со станции отправления!" // errorNoDepartStTime = 15;
            ,"ОШИБКА! Что-то пошло не так с введённым временем отправления по станции отправления!" // errorMystiqueTimeDep = 16;
            ,"ОШИБКА! Вы не ввели время прибытия у промежуточной станции!" // errorNoMidStDepTime = 17;
            ,"ОШИБКА! Вы не ввели время отправления у промежуточной станции!" // errorNoMidStArrTime = 18;
            ,"ОШИБКА! Вы не ввели конечную станцию!" // errorNoEndSt = 19;
            ,"ОШИБКА! Введённая последовательность станций неправильная: две станции назначения подряд не могут быть одинаковыми!" // errorTwoEqualSt = 20;
            ,"ОШИБКА! Вы не ввели время прибытия на конечную станцию!" // errorNoEndStTime = 21;
            ,"ОШИБКА! Введённое время прибытия на конечную станцию введено в неправильном формате!" // errorTimeEndSt = 22;
            ,"ОШИБКА! Вы не ввели следующую после отправления станцию!" // errorNoDepNextFmDepartSt = 23;
            ,"ОШИБКА! Вы не ввели промежуточную станцию!" // errorNoMidNextFmDepartSt = 24;
            ,"ОШИБКА! Что-то пошло не так %() Поезд и его маршрут НЕ сохранились в базе!" // errorMystique = 25;
            ,"ОШИБКА! Что-то пошло не так с введённым временем прибытия и отправления по промежуточной станции!" // errorMystiqueTime = 26;
            ,"ОШИБКА! Что-то пошло не так с введённым временем прибытия на конечную станцию!" // errorMystiqueTimeArr = 27;
            ,"ОШИБКА! Введённое время прибытия на конечную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!" // errorArrStTimeDepOutOf24h = 28;
    };

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private TrainRepository trainRepository;
    private PasswordEncoder encoder;
    private ControllerService controllerService;

    @Autowired
    public AdminController(UserRepository userRepository, RoleRepository roleRepository, TrainRepository trainRepository
            , PasswordEncoder encoder, ControllerService controllerService)
    {
        this.userRepository = userRepository;
        this.trainRepository = trainRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.controllerService = controllerService;
    }

    //------------ admin employee ---------------------------------------
    @GetMapping("/account")
    public String adminAccount(Principal principal)
    {
        //"account_admin"; or "redirect:/sbb/v1/admin/account_admin_god"
        return "account_admin";
    }

    // Просмотр всех пассажиров, зарегистрированных на поезд
    @GetMapping("/account/find_all_passengers_in_one_train")
    public String findAllPassengersInOneTrain(Model model /*Principal principal*/)
    {
        return "account_adminFind_all_passengers_in_one_train";
    }
    @PostMapping("/account/find_all_passengers_in_one_train_handler") // Просмотр всех пассажиров, зарегистрированных на поезд
    public String findAllPassengersInOneTrainHandler(String trainNumber, Model model /*Principal principal*/)
    {
//        System.out.println("------контроллер------------trainZug:" + trainNumber);
        //--trainZug:пустой    если пустой запрос
        if (trainNumber.isEmpty() || trainNumber.isBlank()){
            model.addAttribute("result","ВНИМАНИЕ! Вы не ввели номер поезда. Выполнение запроса " +
                    "невозможно!");
        } else {
            model.addAttribute("trainnumber", trainNumber);

            ArrayList<PassengerInOneTrainDto> passengerInOneTrainDtoArrayList =
                    controllerService.serviceFindAllPassengersInOneTrainHandler(trainNumber);
            if (!passengerInOneTrainDtoArrayList.isEmpty() &&
                    passengerInOneTrainDtoArrayList.get(0).getNumberInOrder() == -1){
                model.addAttribute("result","ВНИМАНИЕ! Был введён номер несуществующего поезда. " +
                        "Выполнение запроса невозможно!");
            } else
            if (passengerInOneTrainDtoArrayList.isEmpty()){
                model.addAttribute("result","ВНИМАНИЕ! На данный поезд не зарегистрированно ни " +
                        "одного пассажира!");
            } else {
                model.addAttribute("passengers",passengerInOneTrainDtoArrayList);
            }
        }
        return "account_adminFind_all_passengers_in_one_train_handler";
    }

    // Добавление новых станций
    @GetMapping("/account/add_new_rwstation")
    public String addNewRwStation(/*Principal principal*/ Model model)
    {
        allStationsNames = controllerService.getAllStationsNamesStr();
        model.addAttribute("allstations", allStationsNames);
        return "account_adminAdd_new_rwstation";
    }
    @PostMapping("/account/add_new_rwstation_handler")
    public String addNewRwStationHandler(String rwstationName, Model model)
    {
        int result = controllerService.serviceAddNewRwstationNameHandler(rwstationName);
        if (result == 3) allStationsNames = controllerService.getAllStationsNamesStr();
        model.addAttribute("result", resultArrAddNewRwSt[result]);
        model.addAttribute("allstations", allStationsNames);
        return "account_adminAdd_new_rwstation";
    }

    // Добавление новых поездов: надо ввести НОМЕР ПОЕЗДА и ВМЕСТИМОСТЬ
    @GetMapping("/account/add_new_train")
    public String addNewTrain(Model model)
    {
        allStationsNames = controllerService.getAllStationsNamesStr();
        allTrainsNumbers = controllerService.getAllTrainsNumbersStr();
        return "account_adminAdd_new_train";
    }
    @PostMapping("/account/add_new_train_handler") // ввести количество станций в маршруте поезда
    public String addNewTrainHandler(int routeQtyStations, Model model)
    {
        System.out.println("----add_new_train_handler-------routeQtyStations:" + routeQtyStations);
        inputedRouteQtyStations = routeQtyStations;
        return "redirect:/sbb/v1/admin/account/add_new_train_handler_schedule";
    }
    @GetMapping("/account/add_new_train_handler_schedule") // заполнение поезда и маршрута
    public String addNewTrainHandlerSchedule(Model model)
    {
        model.addAttribute("allstations", allStationsNames);
        model.addAttribute("trainsnames", allTrainsNumbers);
        model.addAttribute("qtystations", inputedRouteQtyStations);
        System.out.println("--------5---allStationsNames:" + allStationsNames);
        System.out.println("--------6---model:" + model.toString());
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
        System.out.println("--------------------String[] inputedData:" + Arrays.toString(inputedData));

        int result = controllerService.serviceAddNewTrainHandler
                (trainNumber, passengersCapacity, inputedData,inputedRouteQtyStations);
        model.addAttribute("result", resultArrAddNewTrainSchedule[result]);
        //return "account_adminAdd_new_train_handler_" + inputedRouteQtyStations;
        return "account_adminAdd_new_train_schedule_result";
    }

    // Просмотр всех поездов
    @GetMapping("/account/find_all_trains")
    public String findAllTrains(Model model)
    {
        LOGGER.info("------------------------trainRepository.findAll() started -> " + LocalDateTime.now());
        model.addAttribute("trains",trainRepository.findAll());
        LOGGER.info("------------------------trainRepository.findAll() finished -> " + LocalDateTime.now());
        return "account_adminFind_all_trains";
    }

    //------------ admin_god --------------------------------------------
    @GetMapping("/account_admin_god") // для расширенной работы с пользовательскими сессиями есть
    // пакет session api spring boot
    public String adminGodAccount(Principal principal)
    {
        String whereToGo = "account_admin_god";
        // если админ не уровня бог, то редирект на error
        if (!principal.getName().equals(adminLevelGod)){
            whereToGo = "redirect:/sbb/v1/error";
        }
        return whereToGo;
    }

    @GetMapping("/registration_new_admin")
    public String getRegistrationNewAdminForm(Model model,Principal principal) // тип данных Model используется, чтобы передать полученные
    // данные в хтмл-страничку. Чтобы переданные Моделом данные вывести в хтмл - применяется СпрингФреймворк
    // spring-boot-starter-thymeleaf
    {
//        System.out.println("---------registration------language:" + language);
//        model.addAttribute("title", new MyUser()); // в круглых скобках пара ключ, значение
//        String redirectRegistration = "redirect:/sbb/v1/";
//        redirectRegistration = redirectRegistration + "registration_" + controllerService.getLanguageZone(language);
        String whereToGo = "registration_new_admin";
        // если админ не уровня бог, то редирект на error
        if (!principal.getName().equals(adminLevelGod)){
            whereToGo = "redirect:/sbb/v1/error";
        }
        return whereToGo;
    }

    @PostMapping("/registration_handler_newadmin")
    public String registrationNewAdminProcessing(MyUser user, Model model,Principal principal)
    {
        String whereToGo = "registration_new_admin_succesful";
        // если админ не уровня бог, то редирект на error
        if (!principal.getName().equals(adminLevelGod)){
            whereToGo = "redirect:/sbb/v1/error";
        } else {
            if (user.getLogin().length() < 6)
            {
//                System.out.println("----256-----registration------user.getLogin():" + user.getLogin());
                model.addAttribute("error",
                        "ОШИБКА! Длина логина должна быть более 5ти символов!\n");
                whereToGo = "registration_new_admin";
            } else
            if (user.getLogin().length() > 5 && user.getPassword().length() < 9)
            {
//                System.out.println("----263-----registration------user.getLogin():" + user.getLogin());
//                System.out.println("----264-----registration------user.getPassword():" + user.getPassword());
                model.addAttribute("error",
                        "ОШИБКА! Длина пароля должна быть более 8ми символов!\n");
                whereToGo = "registration_new_admin";
            } else {
                //        System.out.println("---------registration------language:" + language);
//        System.out.println("---------registration------user:" + user);
                MyUser fromDB = userRepository.findByLogin(user.getLogin()); // из введённых лог+пароль берём логин и смотрим
                // его в БД и если такой логин там уже есть (значит fromDB будет не НАЛ) - значит надо придумывать новый логин
//        System.out.println("---273-----------fromDB:" + fromDB);
                if (fromDB != null)
                {
                    model.addAttribute("error", "ОШИБКА! Сотрудник с таким именем (логином) уже существует!\n");
                    whereToGo = "registration_new_admin";
                }

                user.setRole(roleRepository.findByRoleName("ROLE_ADMIN")); // если логин новый, то добавляем роль
//        System.out.println("---------------user" + user);
                user.setPassword(encoder.encode(user.getPassword())); // кодируем пароль
                //System.out.println("!!!!!!!!!!!!кодируем пароль:" + user.getPassword() );
                userRepository.save(user); // и потом сохраняем польз-ля с закодированным паролем
                // перенаправляем на форму входа, после того как регистрация прошла удачно
                System.out.println("---286-----------user:" + user);
            }
        }
        return whereToGo;
    }
}
