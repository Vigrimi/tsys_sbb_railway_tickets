package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ScheduleOnRwstationDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.PassengerFahrgast;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RwStationsTrainSequence;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TicketFahrkarte;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationsTrainSequenceRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Controller
@RequestMapping("/sbb/v1/user")
public class UserController {
    private String allStationsNames;
    private ArrayList<RwStationsTrainSequence> fullTrainsSequencesAList;
    private ArrayList<PassengerFahrgast> allPassengers;
    private long currentUserId;
    private int codeFmSystem;
    private TicketFahrkarte newTicketBookedNotPayedWithSeat;
    private TicketFahrkarte newTicketBookedNotPayedNoSeat;
    private final RwStationService rwStationService;
    private final RwStationsTrainSequenceRepository sequenceRepository;
    private final RwStationsTrainSequenceService rwStationsTrainSequenceService;
    private final PassengerService passengerService;
    private final TicketService ticketService;
    private final TrainRepository trainRepository;

    @Autowired
    public UserController(RwStationService rwStationService,RwStationsTrainSequenceRepository sequenceRepository
            ,RwStationsTrainSequenceService rwStationsTrainSequenceService,PassengerService passengerService
            ,TicketService ticketService,TrainRepository trainRepository){
        this.rwStationService = rwStationService;
        this.sequenceRepository = sequenceRepository;
        this.rwStationsTrainSequenceService = rwStationsTrainSequenceService;
        this.passengerService = passengerService;
        this.ticketService = ticketService;
        this.trainRepository = trainRepository;
    }
    //---------------------------------------------------------------
    @GetMapping("/account")
    public String userAccount(Principal principal)
    {
        return "account";
    }

    // "/buy_rw_ticket" Покупка ж.-д. билета
    @GetMapping("/buy_rw_ticket")
    public String userBuyRwTicket(Principal principal, Model model)
    {
        // отобразить всех пассажиров у юзера и взять его айди для возможных следующих операций
        allPassengers = passengerService.getAllPassengersFromUser(principal);
        currentUserId = allPassengers.get(IntConstants.FIRST_ELEMENT_IN_ARRAY.getDigits()).getUserId();
        System.out.println("-------GetMapping /buy_rw_ticket ---currentUserId:" + currentUserId);
        model.addAttribute("passengers", allPassengers);
        model.addAttribute("trains",trainRepository.findAll());
        return "buy_rw_ticket";
    }
    // ввести пассажира, номер поезда и тп
    @PostMapping("/buy_rw_ticket_handler")
    public String userBuyRwTicketHandler(String passIdString,String trainNumber,String rwstationDeparture
            ,String rwstationArrival,String departureDate,Model model){
        System.out.println("========= passIdString:" + passIdString);
        ResultDto resultDtoNewTicket = ticketService.getNewTicket(passIdString,trainNumber,rwstationDeparture
                ,rwstationArrival,departureDate,allPassengers);
        // результат есть ли места на рейсе и выбор места
        newTicketBookedNotPayedNoSeat = resultDtoNewTicket.getTicketFahrkarte();
        model.addAttribute("text",resultDtoNewTicket.getSomeText());
        model.addAttribute("result",resultDtoNewTicket.getResultsEnumList());
        model.addAttribute("seats",resultDtoNewTicket.getSomeList());
        return "buy_rw_ticket_take_seat_in_train"; //"redirect:/sbb/v1/user/buy_rw_ticket_take_seat_in_train";
    }
    // по найденному поезду, дате и пр - вывести актуальные места в поезде для выбора покупателю
    @PostMapping("/buy_rw_ticket_take_seat_in_train")
    public String userBuyRwTicketTakeSeatInTrain(String seat,Model model){
        System.out.println("-----------------выбрал место seat:" + seat);
        ResultDto resultDtoNewTicket = ticketService.getNewTicketWithSeatNumber(newTicketBookedNotPayedNoSeat,seat);
        // цена билета
        model.addAttribute("result",resultDtoNewTicket.getResultsEnumList());
        model.addAttribute("price",resultDtoNewTicket.getTicketPrice());
        codeFmSystem = resultDtoNewTicket.getResultsInt();
        newTicketBookedNotPayedWithSeat = resultDtoNewTicket.getTicketFahrkarte();
        return "buy_rw_ticket_handler";
    }
    @PostMapping("/buy_rw_ticket_code")
    public String userBuyRwTicketCode(int codeInputed,Model model){
        ResultDto resultDtoTicketCode = ticketService.checkCodeAndBuyNewTicket
                (codeInputed,codeFmSystem,newTicketBookedNotPayedWithSeat);
        model.addAttribute("resultBuy",resultDtoTicketCode.getResultsEnumList());
        model.addAttribute("yourTicket",resultDtoTicketCode.getTicketToPrintDto().toString());
        return "buy_rw_ticket_handler";
    }
    @PostMapping("/buy_rw_ticket/add_new_passenger")
    public String userAddNewPassenger(Model model){
        return "userAdd_new_passenger";
    }
    @PostMapping("/buy_rw_ticket/add_new_passenger_handler")
    public String userAddNewPassengerHandler(String namePassenger,String surnamePassenger,String birthdayPassenger
            ,String passportNumber,String emailPassenger,String mobilePhoneNumber,Model model){
        // TODO удалить неактуального пассажира, кому отправлять билет пассажиру или юзеру или обоим
        // проверка: такой пассажир уже сохранён у этого юзера (фио, ДР, паспорт)
        // обработка добавления нового пассажира
        ResultDto resultDtoAddNewPassenger = passengerService.addNewPassengerInDB(namePassenger,surnamePassenger
                ,birthdayPassenger,passportNumber,emailPassenger,mobilePhoneNumber,currentUserId);
        model.addAttribute("result", resultDtoAddNewPassenger.getResultsEnumList());
        return "userAdd_new_passenger";
    }

    // "/find_fm_to_rwstation"  Поиск от станции до станции
    @GetMapping("/find_fm_to_rwstation")
    public String userFindFmToRwstation(Model model)
    {
        allStationsNames = rwStationService.getAllStationsNamesStr();
        model.addAttribute("allstations", allStationsNames);
        fullTrainsSequencesAList = sequenceRepository.findAll();
        return "find_fm_to_rwstation";
    }
    @PostMapping("/find_fm_to_rwstation_handler")
    public String userFindFmToRwstationHandler(String rwstationNameFrom,String rwstationNameTo,String timeFrom
            ,String timeTo,Model model){
//        System.out.println("------------inputed data:" + rwstationNameFrom + rwstationNameTo + timeFrom + timeTo);
        String text = "Для клиентов компании: поиск поезда, проходящего от станции " + rwstationNameFrom.toUpperCase()
                + " до станции " + rwstationNameTo.toUpperCase() + " в заданный промежуток времени (" + timeFrom +
                "-" + timeTo + ")." ;
        ResultDto resultDtoFmTo = rwStationsTrainSequenceService
                .findFmToRwstationHandler(rwstationNameFrom,rwstationNameTo,timeFrom,timeTo,fullTrainsSequencesAList);
        model.addAttribute("text", text);
        model.addAttribute("result", resultDtoFmTo.getResultsEnumList());
        model.addAttribute("sequences", resultDtoFmTo.getSequenceDtoAList());
        return "find_fm_to_rwstation_handler";
    }

    // "/schedule_on_rwstation" Расписание по станции
    @GetMapping("/schedule_on_rwstation")
    public String userScheduleOnRwstation(Model model){
        allStationsNames = rwStationService.getAllStationsNamesStr();
        model.addAttribute("allstations", allStationsNames);
        return "schedule_on_rwstation";
    }
    @PostMapping("/schedule_on_rwstation_handler")
    public String userScheduleOnRwstationHandler(String rwstationName,Model model){
//        model.addAttribute("result", result);
        String textForScoreboard = "Информация по станции: " + rwstationName.toUpperCase();
//        System.out.println("**************-----tablo------BAD ZURZACH textForScoreboard:" + textForScoreboard);
        model.addAttribute("rwstationName", textForScoreboard);
        ArrayList<ScheduleOnRwstationDto> scheduleDto = rwStationService.getScheduleOnRwstationHandler(rwstationName);
//        System.out.println("----------PostMapping schedule_on_rwstation_handler--------");
        model.addAttribute("schedules", scheduleDto);
        return "schedule_on_rwstation_handler";
    }

    // все билеты юзера
    @GetMapping("/all_my_rw_tickets")
    public String userWatchAllTickets(Principal principal,Model model){
        ResultDto resultDto = ticketService.getAllUserTickets(principal);
        model.addAttribute("allticketsPast", resultDto.getAllUserTicketsPast());
        model.addAttribute("allticketsFuture", resultDto.getAllUserTicketsFutureAndToday());
        return "userFind_all_my_tickets";
    }
}
