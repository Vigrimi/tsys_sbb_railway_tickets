package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.PassengerInOneTrainDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.TicketToPrintDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.*;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.IntConstants;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationsTrainSequenceRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.SeatInTrainRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TicketRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces.CodeHandler;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces.DateAndTimeHandler;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces.FileHandler;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class TicketService implements CodeHandler, DateAndTimeHandler,FileHandler {
    private final String ticketFileExtention = ".txt";
    private final String filePathForTicket = "D:\\DeuTelecomIdea\\tickets_files\\";
    private final TrainService trainService;
    private final RwStationService rwStationService;
    private final RwStationsTrainSequenceService sequenceService;
    private final TicketRepository ticketRepository;
    private final EMailService mailService;
    private final SeatInTrainRepository seatInTrainRepository;
    private final SeatInTrainService seatInTrainService;
    private final RwStationsTrainSequenceRepository trainSequenceRepository;
    private final TimetableService timetableService;
    private final PassengerService passengerService;
    private final UserService userService;

    @Autowired
    public TicketService(TrainService trainService,RwStationService rwStationService,RwStationsTrainSequenceService
            sequenceService,TicketRepository ticketRepository,EMailService mailService,SeatInTrainRepository
            seatInTrainRepository,SeatInTrainService seatInTrainService,RwStationsTrainSequenceRepository
            trainSequenceRepository,TimetableService timetableService,PassengerService passengerService,UserService
            userService) {
        this.trainService = trainService;
        this.rwStationService = rwStationService;
        this.sequenceService = sequenceService;
        this.ticketRepository = ticketRepository;
        this.mailService = mailService;
        this.seatInTrainRepository = seatInTrainRepository;
        this.seatInTrainService = seatInTrainService;
        this.trainSequenceRepository = trainSequenceRepository;
        this.timetableService = timetableService;
        this.passengerService = passengerService;
        this.userService = userService;
    }
    //-------------------------------------------------------------
    public ResultDto getNewTicket(String passIdString, String trainNumber, String rwstationDepartureInputed
            , String rwstationArrivalInputed, String departureDate, ArrayList<PassengerFahrgast> allPassengers){
        // TODO сделать проверки введённых данных, если Стринги пришли нал
        // результат есть ли места и всё ли ок
        // цена билета
        long passengerId = Long.parseLong(passIdString);
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumListFmTo = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumListFmTo);

        TicketFahrkarte newTicket = new TicketFahrkarte();
        TrainZug trainFmDB = trainService.getTrainFmDBByTrainNumber(trainNumber);
        //проверка если айди пассажира не принадлежит юзеру
        PassengerFahrgast passengerFmDB = null; //new PassengerFahrgast();
        for (PassengerFahrgast passenger : allPassengers) {
            if (passenger.getId() == passengerId){
                passengerFmDB = passenger;
                break;
            }
        }
        // проверить названия станций
        RailwayStationBahnhof stationDepFmDB = null;
        RailwayStationBahnhof stationArrFmDB = null;
        if (rwstationDepartureInputed != null || !rwstationDepartureInputed.isEmpty() || !rwstationDepartureInputed.isBlank()){
            // взять из базы станции отпр и прибытия
            rwstationDepartureInputed = rwstationDepartureInputed.trim();
            stationDepFmDB = rwStationService.getRwStationFmDBByName(rwstationDepartureInputed);
            newTicket.setTicketRwStationDeparture(stationDepFmDB);
        }
        if (rwstationArrivalInputed != null || !rwstationArrivalInputed.isEmpty() || !rwstationArrivalInputed.isBlank()){
            // взять из базы станции отпр и прибытия
            rwstationArrivalInputed = rwstationArrivalInputed.trim();
            stationArrFmDB = rwStationService.getRwStationFmDBByName(rwstationArrivalInputed);
            newTicket.setTicketRwStationArrival(stationArrFmDB);
        }

        assert stationDepFmDB != null;
        String rwstationDeparture = stationDepFmDB.getNameRailwayStationBahnhof();
//        System.out.println("--------94-----------getNewTicket  rwstationDeparture:" + rwstationDeparture);
        assert stationArrFmDB != null;
        String rwstationArrival = stationArrFmDB.getNameRailwayStationBahnhof();

        // взять последовательность станций у этого поезда
        RwStationsTrainSequence rwStationsTrainSequence = trainSequenceRepository
                .findBySequenceTrainNumber(trainFmDB.getNumberTrainNummerZug());
        String trainSequence = rwStationsTrainSequence.getSequenceRwStations();
        // получить время отправления и прибытия по соответствующим станциям
        String[] timeDepAndArr = sequenceService.getDepTimeByDepStationAndArrTimeByArrStationFmSequence
                (rwstationDeparture,rwstationArrival,trainSequence);
//        System.out.println("--------103-----------getNewTicket  String[] timeDepAndArr:" + Arrays.toString(timeDepAndArr));
        String timeDeparture = timeDepAndArr[IntConstants.DEPARTURE_TIME_INDEX_IN_ARRAY.getDigits()];
//        System.out.println("--------104-----------getNewTicket  timeDeparture:" + timeDeparture);
        String timeArrival = timeDepAndArr[IntConstants.ARRIVAL_TIME_INDEX_IN_ARRAY.getDigits()];
        // если станции из базы нал
        if (stationDepFmDB == null){
            resultsEnumListFmTo.add(Results.ERROR_RWSTATION_NAME_FROM_MISSED_IN_DB.getResultText());
            newTicket.setTicketDepartureTime(Results.ERROR_RWSTATION_NAME_FROM_MISSED_IN_DB.getResultText());
            resultDto.setTicketFahrkarte(newTicket);
        } else
        if (stationArrFmDB == null){
            resultsEnumListFmTo.add(Results.ERROR_RWSTATION_NAME_TO_MISSED_IN_DB.getResultText());
            newTicket.setTicketArrivalTime(Results.ERROR_RWSTATION_NAME_TO_MISSED_IN_DB.getResultText());
            resultDto.setTicketFahrkarte(newTicket);
        } else
        // проверить, что в этой последовательности правильный порядок - сначала станция отправления и потом прибытия
        if (!seatInTrainService.sequenceIsAvailableToBuy(trainSequence,rwstationDeparture,rwstationArrival)){
            //вывести ошибку
            resultsEnumListFmTo.add(Results.ERROR_RWSTATION_ARRIVAL_BEFORE_DEPARTURE.getResultText());
            newTicket.setTicketDepartureTime(Results.ERROR_RWSTATION_ARRIVAL_BEFORE_DEPARTURE.getResultText());
            resultDto.setTicketFahrkarte(newTicket);
        } else
        // если дата раньше текущей - вывести ошибку
        if (checkIfDateBeforeNow(departureDate)){
            //вывести ошибку
            resultsEnumListFmTo.add(Results.ERROR_DATE_IS_BEFORE_NOW.getResultText());
            newTicket.setTicketDepartureTime(Results.ERROR_DATE_IS_BEFORE_NOW.getResultText());
            resultDto.setTicketFahrkarte(newTicket);
        } else
        // если пассажир чужой, то passengerFmDB = null, записываем ошибку Иначе готовим билет
        if (passengerFmDB == null){
            resultsEnumListFmTo.add(Results.ERROR_NEW_TICKET_WRONG_PASSENGER.getResultText());
            newTicket.setTicketDepartureTime(Results.ERROR_NEW_TICKET_WRONG_PASSENGER.getResultText());
            resultDto.setTicketFahrkarte(newTicket);
        } else
        // проверить: есл дата отправления сегодня: время: не ушёл ли уже поезд, и если до отправки менее десяти минут
        if (checkIfDateIsToday(departureDate) &&
                !timetableService.timeAvailableToBuyTicket(trainSequence,rwstationDeparture)){
                resultsEnumListFmTo.add(Results.ERROR_TIME_LATE_BUY_TICKET.getResultText());
                newTicket.setTicketDepartureTime(Results.ERROR_TIME_LATE_BUY_TICKET.getResultText());
                resultDto.setTicketFahrkarte(newTicket);
        } else { // всё хорошо, готовим билет
            // проверить есть ли у этого пассажира уже билет на этот рейс
            // TODO настроить также проверку по станциям: могут быть два билета у пассажира на разные станции следования
            ResultDto resultDtoAllPassengers = passengerService.serviceFindAllPassengersInOneTrainHandler
                    (trainFmDB.getNumberTrainNummerZug(),departureDate);
            ArrayList<PassengerInOneTrainDto> passengerInOneTrainDtoArrayList =
                    resultDtoAllPassengers.getPassengerInOneTrainDtoList();
            boolean passengerWasFound = false;
            // перебрать полученных на поезде пассажиров и сравнить с оформляемым
            for (PassengerInOneTrainDto passenger : passengerInOneTrainDtoArrayList) {
                if (passenger.getPassengerId() == passengerFmDB.getId()){
                    passengerWasFound = true;
                    break;
                }
            }
            // если такой пассажир есть - ошибка
            if (passengerWasFound){
                resultsEnumListFmTo.add(Results.ERROR_NEW_TICKET_DOUBLED_PASSENGER.getResultText());
                newTicket.setTicketDepartureTime(Results.ERROR_NEW_TICKET_DOUBLED_PASSENGER.getResultText());
                resultDto.setTicketFahrkarte(newTicket);
            } else { // если такого пассажира нет - оформить билет
                resultDto.setTicketFahrkarte(newTicket);
                newTicket.setTicketDepartureDate(departureDate);
                newTicket.setTicketNumberTrainFahrkarteNummerZug(trainFmDB);
                newTicket.setTicketPassengerIdFahrkarteFahrgastId(passengerFmDB);
                newTicket.setTicketDepartureTime(timeDeparture);
                newTicket.setTicketArrivalTime(timeArrival);
//            System.out.println("--------151-----------getNewTicket  newTicket:" + newTicket);
                // надо идти в базу мест и взять все места на рейсе этого поезда
                String voyageNumber = departureDate + "-" + trainFmDB.getNumberTrainNummerZug();
                ArrayList<SeatInTrain> seatsInTrainByVoyage = seatInTrainRepository.findAllByVoyageNumber(voyageNumber);

                // получили все места на рейсе
                ArrayList<Integer> availableSeatsToBuy = seatInTrainService.getAvailableSeatsToBuyByVoyage(
                        seatsInTrainByVoyage,stationDepFmDB,stationArrFmDB);
                // если нет мест
                if (availableSeatsToBuy.isEmpty()){
                    resultsEnumListFmTo.add(Results.ERROR_NEW_TICKET_NO_SEATS.getResultText());
                    newTicket.setTicketDepartureTime(Results.ERROR_NEW_TICKET_NO_SEATS.getResultText());
                    newTicket.setTicketSeatNumber(IntConstants.ERROR_INT.getDigits());
                }
                resultDto.setSomeList(availableSeatsToBuy);

//            System.out.println("--------167-----------getNewTicket  newTicket:" + newTicket);
                // PROCESS_NEW_TICKET_SELECT_SEAT("Процесс покупки билета продолжается. Надо выбрать место в поезде.")
                resultsEnumListFmTo.add(Results.PROCESS_NEW_TICKET_SELECT_SEAT.getResultText());
                String someText = "Продолжение формирования билета. Станция отправления: " + newTicket
                        .getTicketRwStationDeparture().getNameRailwayStationBahnhof() + ". Время отправления: " +
                        newTicket.getTicketDepartureTime() + ". Станция прибытия: " + newTicket.getTicketRwStationArrival()
                        .getNameRailwayStationBahnhof() + ". Время прибытия: " + newTicket.getTicketArrivalTime() +
                        ". Номер поезда: " + newTicket.getTicketNumberTrainFahrkarteNummerZug().getNumberTrainNummerZug()
                        + ". Дата отправления: " + newTicket.getTicketDepartureDate() + ".";
                resultDto.setSomeText(someText);
            }
        }
        return resultDto;
    }

    public ResultDto getNewTicketWithSeatNumber(TicketFahrkarte newTicketBookedNotPayedNoSeat, String seatNumberString){
        if (seatNumberString == null) seatNumberString = "0";
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumListFmTo = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumListFmTo);
        // уже есть новый билет, но нет места и цены
        TicketFahrkarte newTicket = newTicketBookedNotPayedNoSeat;
        // новый билет, установить цену
        Random random = new Random();
        double price = (random.nextInt(10000) + 10) + 1;
        price /= 100;
        newTicket.setTicketPrice(price);
        // новый билет, установить место
        int seatNumber = Integer.parseInt(seatNumberString);
        newTicket.setTicketSeatNumber(seatNumber);

        // типа код для оплаты билета
        int code = getFourDigitsCode();
        System.out.println("-------getNewTicket--------------code:" + code);
        resultDto.setResultsInt(code);
        String textMail = Results.MAIL_TEXT_CODE_TO_BUY_TICKET.getResultText()  + code;
        try {
            LOGGER.info("--------try mailService.sendSimpleMail  началось -> " + LocalDateTime.now());
            mailService.sendSimpleMail(Results.MAIL_SUBJECT_SEND_CODE.getResultText(), textMail); // sendEMail(code + "");
            LOGGER.info("--------try mailService.sendSimpleMail  закончилось -> " + LocalDateTime.now());
//            LOGGER.info("--------try whatsAppService  началось -> " + LocalDateTime.now());
//            WhatsAppService.sendInWatsapWeb(Results.MAIL_SUBJECT_SEND_CODE.getResultText() + ". " + textMail);
//            LOGGER.info("--------try whatsAppService  закончилось -> " + LocalDateTime.now());
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error(e + "**" + LocalDateTime.now());
            e.printStackTrace();
        }

        resultDto.setTicketPrice(newTicket.getTicketPrice());
        resultsEnumListFmTo.add(Results.SUCCESS_NEW_TICKET_BOOKED.getResultText());
        resultDto.setTicketFahrkarte(newTicket);
//        System.out.println("------------getNewTicket--------newTicket:" + newTicket);
        return resultDto;
    }

    public ResultDto checkCodeAndBuyNewTicket(int codeInputed, int codeFmSystem,TicketFahrkarte newTicketBookedNotPayed){
        // TODO сделать проверки введённых данных
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumListBuy = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumListBuy);
        TicketFahrkarte ticketBoughtFmDB = new TicketFahrkarte();
        TicketToPrintDto ticketToPrintDto = new TicketToPrintDto();
        if (codeInputed != codeFmSystem){
            ticketBoughtFmDB.setTicketDepartureTime(Results.ERROR_INPUTED_WRONG_CODE.getResultText());
            resultsEnumListBuy.add(Results.ERROR_INPUTED_WRONG_CODE.getResultText());
            ticketToPrintDto.setTicketDepartureTime(ticketBoughtFmDB.getTicketDepartureTime());
            resultDto.setTicketToPrintDto(ticketToPrintDto);
        } else { // код принят, сохранить в БД новый билет
            long passengerId = newTicketBookedNotPayed.getTicketPassengerIdFahrkarteFahrgastId().getId();
            try {
                ticketRepository.save(newTicketBookedNotPayed);
            } catch (Exception se){
                resultsEnumListBuy.add(Results.ERROR_SAVE_NEW_TICKET_MYSTIQUE.getResultText());
                se.printStackTrace();
            }
            ArrayList<TicketFahrkarte> ticketBoughtFmDBAlist = ticketRepository.findAllByPassengerId(passengerId);
            // TODO при сохранении билета сразу взять его айди в базе
            ticketBoughtFmDB = ticketBoughtFmDBAlist.get(ticketBoughtFmDBAlist.size()-1);
            if (ticketBoughtFmDB.getId() != 0){
                resultsEnumListBuy.add(Results.SUCCESS_NEW_TICKET_BOUGHT.getResultText());
                // у места в конкретном рейсе убрать станции, чтобы было нельзя купить ещё билет на это же место на
                // такую же последовательность станций
                // TODO проверка результата удаления станций
                seatInTrainService.removeStationsFmSeatSequence(ticketBoughtFmDB);
            }
        }
        //сделать печатный вид билета и вернуть на экран и в мэйл и в ватсап
        if(!ticketBoughtFmDB.getTicketDepartureTime().contains(Results.ERROR_INPUTED_WRONG_CODE.getResultText())){
            ticketToPrintDto = getTicketToPrintDtoFromTicketBoughtFmDB(ticketBoughtFmDB);
            resultDto.setTicketToPrintDto(ticketToPrintDto);
            // сохранить билет в файл для последующей отправки в мэйл и берём название этого файла с его путём
            String fileNameAndPath = filePathForTicket + ticketToPrintDto.getTicketNumberId()
                    + ticketFileExtention;
            writeInOutputFile(ticketToPrintDto.toString(),fileNameAndPath);
            // отправить билет на мэйл и в ватсап
            try {
                LOGGER.info("--------try mailService.sendSimpleMail  началось -> " + LocalDateTime.now());
                // отправка мэйла с аттаченным файлом
                mailService.sendFileMail(Results.MAIL_SUBJECT_NEW_TICKET.getResultText()
                        , ticketToPrintDto.toString(), fileNameAndPath);
                LOGGER.info("--------try mailService.sendSimpleMail  закончилось -> " + LocalDateTime.now());
//                LOGGER.info("--------try whatsAppService  началось -> " + LocalDateTime.now());
//                WhatsAppService.sendInWatsapWeb(Results.MAIL_SUBJECT_NEW_TICKET.getResultText() + ". \r"
//                        + ticketToPrintDto.toString().replaceAll("\n","\r"));
//                LOGGER.info("--------try whatsAppService  закончилось -> " + LocalDateTime.now());
            } catch (MessagingException | UnsupportedEncodingException e) {
                LOGGER.error(e + "**" + LocalDateTime.now());
                e.printStackTrace();
            }
        }
        return resultDto;
    }

    public TicketToPrintDto getTicketToPrintDtoFromTicketBoughtFmDB(TicketFahrkarte ticketBoughtFmDB){
        TicketToPrintDto ticketToPrintDto = new TicketToPrintDto();
        ticketToPrintDto.setNamePassenger(ticketBoughtFmDB.getTicketPassengerIdFahrkarteFahrgastId().getNamePassengerFahrgast());
        ticketToPrintDto.setSurnamePassenger(ticketBoughtFmDB.getTicketPassengerIdFahrkarteFahrgastId().getSurnamePassengerFamiliennameFahrgast());
        ticketToPrintDto.setPassportNumber(ticketBoughtFmDB.getTicketPassengerIdFahrkarteFahrgastId().getPassportNumber());
        ticketToPrintDto.setUserId(ticketBoughtFmDB.getTicketPassengerIdFahrkarteFahrgastId().getUserId());
        ticketToPrintDto.setTicketNumberId(ticketBoughtFmDB.getId());
        ticketToPrintDto.setTrainNumber(ticketBoughtFmDB.getTicketNumberTrainFahrkarteNummerZug().getNumberTrainNummerZug());
        ticketToPrintDto.setTicketPrice(ticketBoughtFmDB.getTicketPrice());
        ticketToPrintDto.setRwStationNameDeparture(ticketBoughtFmDB.getTicketRwStationDeparture().getNameRailwayStationBahnhof());
        ticketToPrintDto.setTicketDepartureTime(ticketBoughtFmDB.getTicketDepartureTime());
        ticketToPrintDto.setRwStationNameArrival(ticketBoughtFmDB.getTicketRwStationArrival().getNameRailwayStationBahnhof());
        ticketToPrintDto.setTicketArrivalTime(ticketBoughtFmDB.getTicketArrivalTime());
        ticketToPrintDto.setSeatNumber(ticketBoughtFmDB.getTicketSeatNumber());
        ticketToPrintDto.setDepartureDate(ticketBoughtFmDB.getTicketDepartureDate());
        return ticketToPrintDto;
    }

    public ResultDto getAllUserTickets(Principal principal){
        ResultDto resultDto = new ResultDto();
        ArrayList<TicketToPrintDto> allUserTicketsPast = new ArrayList<>();
        resultDto.setAllUserTicketsPast(allUserTicketsPast);
        ArrayList<TicketToPrintDto> allUserTicketsFutureAndToday = new ArrayList<>();
        resultDto.setAllUserTicketsFutureAndToday(allUserTicketsFutureAndToday);

        // взять всех пассажиров по айди юзера
        ArrayList<PassengerFahrgast> allPassengersFromUser = passengerService.getAllPassengersFromUser(principal);
        ArrayList<Long> allPassengersIds = new ArrayList<>();
        for (PassengerFahrgast passenger : allPassengersFromUser) {
            allPassengersIds.add(passenger.getId());
        }
        // взять все билеты по всем айди пассажиров
        ArrayList<TicketFahrkarte> allPassengersTickets = new ArrayList<>();
        for (Long id : allPassengersIds) {
            allPassengersTickets.addAll(ticketRepository.findAllByPassengerId(id));
        }

        for (TicketFahrkarte ticket : allPassengersTickets) {
            TicketToPrintDto ticketDto = getTicketToPrintDtoFromTicketBoughtFmDB(ticket);
            if (checkIfDateBeforeNow(ticketDto.getDepartureDate())){
                allUserTicketsPast.add(ticketDto);
            } else {
                allUserTicketsFutureAndToday.add(ticketDto);
            }
        }
        return resultDto;
    }
}
