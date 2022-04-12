package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.PassengerInOneTrainDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.PassengerFahrgast;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TicketFahrkarte;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.PassengerRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TicketRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.interfaces.DateAndTimeHandler;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class PassengerService implements DateAndTimeHandler {
    private final TrainRepository trainRepository;
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final PassengerRepository passengerRepository;

    @Autowired
    public PassengerService(TrainRepository trainRepository,TicketRepository ticketRepository,UserService userService
            ,PassengerRepository passengerRepository) {
        this.trainRepository = trainRepository;
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.passengerRepository = passengerRepository;
    }
    //---------------------------------------------------------------
    public ResultDto serviceFindAllPassengersInOneTrainHandler(String trainNumber,String departureDateString){
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);
        ArrayList<PassengerInOneTrainDto> passengerInOneTrainDtoArrayList = new ArrayList<>();
        resultDto.setPassengerInOneTrainDtoList(passengerInOneTrainDtoArrayList);
        LocalDate departureDate = null;
        TrainZug trainZug = null;
        departureDateString = departureDateString.trim();

        if (trainNumber.isEmpty() || trainNumber.isBlank()){
            //ERROR_TRAIN_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели номер поезда!")
            resultsEnumList.add(Results.ERROR_TRAIN_WAS_NOT_INPUTED.getResultText());
        } else if (departureDateString.isEmpty() || departureDateString.isBlank()){
            // ERROR_TIME_WAS_NOT_INPUTED("ОШИБКА! Вы не ввели время!")
            resultsEnumList.add(Results.ERROR_TIME_WAS_NOT_INPUTED.getResultText());
        } else {
            // получить дату из стринга
            departureDate = getDateFromString(departureDateString);
            // если дата нал, то ошибка формата даты
            if (departureDate == null){
                // ERROR_DATE_WRONG_FORMAT("ОШИБКА! Введённая дата введена в неправильном формате!")
                resultsEnumList.add(Results.ERROR_DATE_WRONG_FORMAT.getResultText());
            }
            // по номеру поезда из базы взять поезд - нужен будет его айди
            trainZug = trainRepository.findByNumberTrain(trainNumber);
        System.out.println("---65---service-------------------trainZug:" + trainZug);
            //--trainZug:null не существующий в БД поезд
            // пришёл несуществующий номер поезда - результат будет нал
            if (trainZug == null){
                // ERROR_TRAIN_ABSENT_IN_DB("ОШИБКА! Введённый номер поезда отсутствует в базе!")
                resultsEnumList.add(Results.ERROR_TRAIN_ABSENT_IN_DB.getResultText());
//                PassengerInOneTrainDto pdto = new PassengerInOneTrainDto();
//                pdto.setNumberInOrder(-1);
//                passengerInOneTrainDtoArrayList.add(pdto);
            }
            // если поезд и дата не нал, то выполняем запрос
            if (departureDate != null && trainZug != null){
                // в билетах, взять Лист всех билетов где есть айди нужного поезда и нужная дата отправления
                ArrayList<TicketFahrkarte> ticketFahrkarteArrayList = ticketRepository
                        .findAllByTrainIdAndDepartureDate(trainZug.getId(),departureDateString);
//            System.out.println("----------service---------------ticketFahrkarteArrayList:" + ticketFahrkarteArrayList);
                // если список пустой
                if (ticketFahrkarteArrayList.isEmpty()){
                    // ERROR_PASSENGER_ON_TRAIN_NOT_FONUD("Упс, у выбранного поезда и даты нет ни одного пассажира!")
                    resultsEnumList.add(Results.ERROR_PASSENGER_ON_TRAIN_NOT_FONUD.getResultText());
                } else {
                    // сделать Лист пассажировДТО
                    int i = 1;
                    for (TicketFahrkarte ticketFahrkarte : ticketFahrkarteArrayList) {
                        PassengerInOneTrainDto pdto = new PassengerInOneTrainDto();
                        pdto.setNumberInOrder(i);
                        pdto.setTicketId(ticketFahrkarte.getId());
                        pdto.setPassengerName(ticketFahrkarte.getTicketPassengerIdFahrkarteFahrgastId().getNamePassengerFahrgast());
                        pdto.setPassengerSurname(ticketFahrkarte.getTicketPassengerIdFahrkarteFahrgastId()
                                .getSurnamePassengerFamiliennameFahrgast());
                        pdto.setTrainNumber(ticketFahrkarte.getTicketNumberTrainFahrkarteNummerZug().getNumberTrainNummerZug());
                        pdto.setDepartureDate(ticketFahrkarte.getTicketDepartureDate());
                        pdto.setTicketSeatNumber(ticketFahrkarte.getTicketSeatNumber());
                        pdto.setPassengerId(ticketFahrkarte.getTicketPassengerIdFahrkarteFahrgastId().getId());
                        i++;
                        passengerInOneTrainDtoArrayList.add(pdto);
                    }
                    // SUCCESS_PASSENGER_ON_TRAIN_FONUD("УСПЕШНО! Найдены пассажиры!"),
                    resultsEnumList.add(Results.SUCCESS_PASSENGER_ON_TRAIN_FONUD.getResultText());
                }
            }
        }
//        System.out.println("--------service----------passengerInOneTrainDtoArrayList:" + passengerInOneTrainDtoArrayList);
        return resultDto;
    }

    public ArrayList<PassengerFahrgast> getAllPassengersFromUser(Principal principal){
        // по логину юзера достать его из базы и взять айди юзера
        long userId = userService.getUserIdByPrincipalName(principal);
        //long[] userIdArr = new long[]{userId};
        System.out.println("-----------getAllPassengersFromUser----userId:" + userId);
        // по айди юзера взять всех пассажиров из базы с этим айди юзера
        ArrayList<PassengerFahrgast> allPassengersFromUser = passengerRepository.findAllByUserId(userId);
        System.out.println("-----------getAllPassengersFromUser----allPassengersFromUser:" + allPassengersFromUser);
        // если у юзера нет ни одного пассажира
        if (allPassengersFromUser.isEmpty()){
            PassengerFahrgast passenger = new PassengerFahrgast();
            passenger.setId(0);
            passenger.setNamePassengerFahrgast("у Вас не сохранено");
            passenger.setSurnamePassengerFamiliennameFahrgast("ни одного пассажира. ");
            passenger.setBirthdayPassengerGeburtstagFahrgast("Сохраните нового пассажира.");
            passenger.setPassportNumber("0");
            passenger.setUserId(userId);
            allPassengersFromUser.add(passenger);
        }
        return allPassengersFromUser;
    }

    public ResultDto addNewPassengerInDB(String namePassenger, String surnamePassenger, String birthdayPassenger
            , String passportNumber, String emailPassenger, String mobilePhoneNumber, long currentUserId){
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);

        // проверка данных введённых юзером
        if (namePassenger == null || namePassenger.isBlank() || namePassenger.isEmpty()){
            resultsEnumList.add(Results.ERROR_NAME_WAS_NOT_INPUTED.getResultText());
        }
        if (surnamePassenger == null || surnamePassenger.isBlank() || surnamePassenger.isEmpty()){
            resultsEnumList.add(Results.ERROR_SURNAME_WAS_NOT_INPUTED.getResultText());
        }
        // Введите день рождения нового пассажира в формате гггг-мм-дд (yyyy-mm-dd)
        if (birthdayPassenger == null || birthdayPassenger.isBlank() || birthdayPassenger.isEmpty()){
            resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
        } else if (birthdayPassenger.length() != 10){
            resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
        } else if (!birthdayPassenger.contains("-")){
            resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
        } else if (checkIfDateAfterNow(birthdayPassenger)){
            resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
        } else {
            String[] bdayArr = birthdayPassenger.split("-");
            if (bdayArr[0].length() != 4){ // yyyy
                resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
            } else if (bdayArr[1].length() != 2){ // mm
                resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
            } else if (bdayArr[2].length() != 2){ // mm
                resultsEnumList.add(Results.ERROR_BIRTHDAY_WAS_NOT_INPUTED.getResultText());
            }
        }
        if (passportNumber == null || passportNumber.isBlank() || passportNumber.isEmpty()){
            resultsEnumList.add(Results.ERROR_PASSPORT_WAS_NOT_INPUTED.getResultText());
        }
        if (emailPassenger == null || emailPassenger.isBlank() || emailPassenger.isEmpty()){
            resultsEnumList.add(Results.ERROR_EMAIL_WAS_NOT_INPUTED.getResultText());
        } else if (emailPassenger.length() <= 5){
            resultsEnumList.add(Results.ERROR_EMAIL_WAS_NOT_INPUTED.getResultText());
        } else if (!emailPassenger.contains("@")){
            resultsEnumList.add(Results.ERROR_EMAIL_WAS_NOT_INPUTED.getResultText());
        } else {
            String[] emailArr = emailPassenger.split("@");
            if (!emailArr[1].contains(".")){
                resultsEnumList.add(Results.ERROR_EMAIL_WAS_NOT_INPUTED.getResultText());
            }
        }
        if (mobilePhoneNumber == null || mobilePhoneNumber.isBlank() || mobilePhoneNumber.isEmpty()){
            resultsEnumList.add(Results.ERROR_PHONE_WAS_NOT_INPUTED.getResultText());
        } else if (mobilePhoneNumber.length() < 5){
            resultsEnumList.add(Results.ERROR_PHONE_WAS_NOT_INPUTED.getResultText());
        } else if (!mobilePhoneNumber.startsWith("+")){
            resultsEnumList.add(Results.ERROR_PHONE_WAS_NOT_INPUTED.getResultText());
        }

        // если ошибок не было, сохранить в базу нового пассажира
        if (resultsEnumList.isEmpty()){
            PassengerFahrgast newPassenger = new PassengerFahrgast();
            newPassenger.setNamePassengerFahrgast(namePassenger);
            newPassenger.setSurnamePassengerFamiliennameFahrgast(surnamePassenger);
            newPassenger.setBirthdayPassengerGeburtstagFahrgast(birthdayPassenger);
            newPassenger.setPassportNumber(passportNumber);
            newPassenger.setEmailPassenger(emailPassenger);
            newPassenger.setMobilePhoneNumberPassenger(mobilePhoneNumber);
            newPassenger.setUserId(currentUserId);
            try {
                passengerRepository.save(newPassenger);
            } catch (Exception se){
                resultsEnumList.add(Results.ERROR_SAVE_NEW_PASSENGER_MYSTIQUE.getResultText());
                se.printStackTrace();
            }
        }
        if (resultsEnumList.isEmpty()){
            resultsEnumList.add(Results.SUCCESS_NEW_PASSENGER_SAVED.getResultText());
        }
        return resultDto;
    }
}
