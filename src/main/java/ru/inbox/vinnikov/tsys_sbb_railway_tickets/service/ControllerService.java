package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.PassengerInOneTrainDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TicketFahrkarte;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Languages;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TicketRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TimetableRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TrainRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class ControllerService {

    @Value("${admin.levelgod}")
    private String adminLevelGod;
    private TrainRepository trainRepository;
    private TicketRepository ticketRepository;
    private RwStationRepository rwStationRepository;
    private TimetableRepository timetableRepository;

    @Autowired
    public ControllerService(TrainRepository trainRepository,TicketRepository ticketRepository,RwStationRepository
            rwStationRepository,TimetableRepository timetableRepository) {
        this.trainRepository = trainRepository;
        this.ticketRepository = ticketRepository;
        this.rwStationRepository = rwStationRepository;
        this.timetableRepository = timetableRepository;
    }

    public String[][] getRegistrationHandlerInfo(int language){
        //int qtyInfoSlots = 3;
        String languageZone = getLanguageZone(language);
        String[][] infoArr = {
                {"1","2","3","4"}, // запасной
                {languageZone, "Fehler! Die Login-Länge muss mehr als 5 Zeichen betragen!",
                        "Fehler! Die Passwort-Länge muss mehr als 8 Zeichen betragen!",
                        "Ein Benutzer mit diesem Login-Namen existiert bereits!"}, // language = 1 = de
                {languageZone, "ОШИБКА! Длина логина должна быть более 5ти символов!",
                        "ОШИБКА! Длина пароля должна быть более 8ми символов!",
                        "Пользователь с таким именем уже существует!"}, // language = 2 = ru
                {languageZone, "Mistake! The length of the login must be more than 5 characters!",
                        "Mistake! The length of the password must be more than 8 characters!",
                        "A user with the same login already exists!"} // language = 3 = uk
        }; //[кол-во языков + 1][qtyInfoSlots]
        return infoArr;
    }

    public String getAccessDeniedText(int language){
        String warning = "Access Denied";
        int de = 1, ru = 2, uk = 3;
        if (language == de){
            warning = "Ihnen wird der Zugriff auf die angeforderte Seite verweigert! Klicken Sie in Ihrem Browser auf die Schaltfläche \"Zurück\".";
        } else if (language == ru){
            warning = "Вам запрещён доступ на запрашиваемую страницу! Нажмите кнопку \"Назад\" в Вашем браузере.";
        } else if (language == uk){
            warning = "You are denied access to the requested page! Click the \"Back\" button in your browser.";
        }
        return warning;
    }

    public String getLanguageZone(int language){
        String languageZone = "";
        if(language == Languages.DEUTSCH.getLanguageId())
        {
            languageZone = "de";
        } else if(language == Languages.RUSSIAN.getLanguageId())
        {
            languageZone = "ru";
        } else if(language == Languages.ENGLISH.getLanguageId())
        {
            languageZone = "uk";
        }
        return languageZone;
    }

    public String getRoleForRedirect(Principal principal)
    {
        String principalString = principal.toString();
        String[] buffer = principalString.split("ROLE_");
        String secondPartPrincipalString = buffer[1];
        String[] buffer1 = secondPartPrincipalString.split("]");
        String currentRole = buffer1[0];
        String whereToGo = "";
        LOGGER.info("---------principal---currentRole:" + currentRole + ", " + LocalDateTime.now());
        if(currentRole.equalsIgnoreCase("USER"))
            whereToGo = "redirect:/sbb/v1/user/account";
        else if(currentRole.equalsIgnoreCase("ADMIN")){
            LOGGER.info("---------principal---principal.getName():" + principal.getName() + ", " + LocalDateTime.now());
            if (principal.getName().equalsIgnoreCase(adminLevelGod))
                whereToGo = "redirect:/sbb/v1/admin/account_admin_god";
            else whereToGo = "redirect:/sbb/v1/admin/account";
        }
        return whereToGo;
    }

    public int serviceAddNewTrainHandler(String trainNumber, int passengersCapacity,
                                         String[] inputedData, int inputedRouteQtyStations){
        TrainZug newTrainFromDB = new TrainZug();
        long trainId = 0L;
        int result = -2;
        int successTimetableSavedInDB = 0;
        int errorNoTrainNumber = 1;
        int errorTrainNumberAlreadyExistInDB = 2;
        int successTrainNumberSaved = 3;
        int errorMystique = 4;

        // внести в базу новый номер поезда
        if (trainNumber == null || trainNumber.isBlank() || trainNumber.isEmpty())
        {
            result = errorNoTrainNumber; // "error", "ОШИБКА! Номер поезда не указан!"
        } else {
            TrainZug newTrain = new TrainZug();
            newTrain.setNumberTrain_nummerZug(trainNumber);
            newTrain.setPassengersCapacity_passagierkapazitat(passengersCapacity);
            LOGGER.info(LocalDateTime.now() + "\n ------serviceAddNewTrainHandler--------newTrain->" + newTrain);
            try {
                TrainZug trainFromDB = trainRepository.findByNumberTrain(trainNumber);
                LOGGER.info(LocalDateTime.now() + "\n ---serviceAddNewTrainHandler-------trainFromDB->" + trainFromDB);
                if (trainFromDB != null){
                    result = errorTrainNumberAlreadyExistInDB; // "error", "ОШИБКА! Номер такого поезда уже есть в базе!"
                } else {
                    trainRepository.save(newTrain);
                    result = successTrainNumberSaved; // "success", "ПОЗДРАВЛЯЕМ! Новый поезд успешно сохранён в базе!"
                    // надо получить айди нового записанного поезда
                    newTrainFromDB = trainRepository.findByNumberTrain(trainNumber);
                    trainId = newTrainFromDB.getId();
                    // если айди вновь внесённого поезда из БД = 0, то новый поезд не внесён в БД
                    if (trainId == 0){
                        result = errorMystique; // "error", "ОШИБКА! Что-то пошло не так %() Поезд НЕ сохранился в базе!"
                    }
                }
            } catch (Exception se){
                result = errorMystique; // "error", "ОШИБКА! Что-то пошло не так %() Поезд НЕ сохранился в базе!"
            }
        }
        // если новый номер поезда удачно внесён, то вносим расписание поезда
        System.out.println("--------successTrainNumberSaved = 3-----serviceAddNewTrainHandler result:" + result);
        if (result == successTrainNumberSaved){ // тут result = 3
            // если с расписанием всё ок, то станет result = 0 или будет равен коду ошибки
            result = serviceAddNewScheduleHandler(inputedData,newTrainFromDB,inputedRouteQtyStations);
        }

        // если расписание удачно сохранилось, вернётся код успеха = 0, если расписание не сохранилось - удалить
        // из БД поезд и вернуть код ошибки
        if (result != successTimetableSavedInDB){
            trainRepository.delete(newTrainFromDB);
        }
        System.out.println("-------serviceAddNewTrainHandler-----success=0----- result:" + result);
        return result;
    }

    public int serviceAddNewScheduleHandler(String[] inputedData,TrainZug newTrainFromDB,int inputedRouteQtyStations){
        System.out.println("------------serviceAddNewScheduleHandler begins----------");
        // коды ошибок для атрибута модели
        int resultSchedule = -1;
        int indexStations = 0;
        int fullSuccess = 0;
        int errorNoDepartSt = 5;
        int errorNoNextFmDepartSt = 6;
        int errorTimeDepartSt = 7;
        int errorArrTimeMidSt = 8;
        int errorDepTimeMidSt = 9;
        int errorMidStArrTimeOutOf24h = 10;
        int errorMidStDepTimeOutOf24h = 11;
        int errorWrongArrDepTimeMidSt = 12;
        int errorDepStTimeDepOutOf24h = 13;
        int errorNoDepartStDB = 14;
        int errorNoDepartStTime = 15;
        int errorMystiqueTimeDep = 16;
        int errorNoMidStDepTime = 17;
        int errorNoMidStArrTime = 18;
        int errorNoEndSt = 19;
        int errorTwoEqualSt = 20;
        int errorNoEndStTime = 21;
        int errorTimeEndSt = 22;
        int errorNoDepNextFmDepartSt = 23;
        int errorNoMidNextFmDepartSt = 24;
        int errorMystique = 25;
        int errorMystiqueTime = 26;
        int errorMystiqueTimeArr = 27;
        int errorArrStTimeDepOutOf24h = 28;

        ArrayList<TimetableZeitplan> timetablesList = new ArrayList<>();
        ArrayList<RailwayStationBahnhof> rwStationsList = new ArrayList<>();

        // перебираем введённые сотрудником данные по станциям следования поездом
        for (int i = 0; i < inputedData.length; i+=3) {
            TimetableZeitplan timetableZeitplan = new TimetableZeitplan();
            // !!!!!!!!!!!!!!!!!!!!!!!! станция отправления
            if (i == 0){ // станция отправления
                System.out.println("--------------станция отправления----i:" + i);

                if (inputedData[i] == null || inputedData[i].isBlank() || inputedData[i].isEmpty()){
                    // "error", "ОШИБКА! Вы не ввели станцию отправления!"
                    resultSchedule = errorNoDepartSt;
                    break;
                }
                timetableZeitplan.setTrainIdZugId(newTrainFromDB);
                String inputedDepartureStation = inputedData[i].toUpperCase();
                RailwayStationBahnhof rwDepartureStationFmDB =
                        rwStationRepository.findByRwStationName(inputedDepartureStation);
                // если не нашлось, станция из БД будет нал
                if (rwDepartureStationFmDB == null){
                    // "error", "ОШИБКА! Введённая станция отправления не найдена в базе!"
                    resultSchedule = errorNoDepartStDB;
                    break;
                }
                rwStationsList.add(rwDepartureStationFmDB);
                // так как это станция отправления, то текущая и предварит станции в расписании одинаковые
                timetableZeitplan.setPreviousRwstationIdBahnhofId(rwDepartureStationFmDB);
                timetableZeitplan.setCurrentRwstationIdBahnhofId(rwDepartureStationFmDB);
                // надо взять следующую станцию
                String inputedNextStationFmDeparture = "";
                if ( (inputedRouteQtyStations - rwStationsList.size() ) > 1 ){
                    inputedNextStationFmDeparture = inputedData[i+2];
                    if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                            inputedNextStationFmDeparture.isBlank()){
                        // "error", "ОШИБКА! Вы не ввели следующую после отправления станцию!"
                        resultSchedule = errorNoDepNextFmDepartSt;
                        break;
                    }
                } else if ( (inputedRouteQtyStations - rwStationsList.size() ) == 1 ){
                    inputedNextStationFmDeparture = inputedData[(inputedData.length-2)];
                    if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                            inputedNextStationFmDeparture.isBlank()){
                        // "error", "ОШИБКА! Вы не ввели конечную станцию!"
                        resultSchedule = errorNoEndSt;
                        break;
                    }
                }

                inputedNextStationFmDeparture = inputedNextStationFmDeparture.toUpperCase();
                RailwayStationBahnhof rwNextStationFmDepartureFmDB =
                        rwStationRepository.findByRwStationName(inputedNextStationFmDeparture);
                // если не нашлось, станция из БД будет нал
                if (rwNextStationFmDepartureFmDB == null){
                    // "error", "ОШИБКА! Введённая следующая после отправления станция не найдена в базе!"
                    resultSchedule = errorNoNextFmDepartSt;
                    break;
                }
                // следующая станция
                rwStationsList.add(rwNextStationFmDepartureFmDB);
                timetableZeitplan.setNextRwstationIdBahnhofId(rwNextStationFmDepartureFmDB);
                indexStations++;

                // обработка времени
                String time = inputedData[i+1];
                // проверка формата введённого времени
                if (time == null || time.isBlank() || time.isEmpty()){
                    // "error", "ОШИБКА! Вы не ввели время отправления у станции отправления!"
                    resultSchedule = errorNoDepartStTime;
                    break;
                }
                if (time.length() != 5 || !time.contains(":")){
                    // "error", "ОШИБКА! Введённое время отправления со станции отправления введено в неправильном формате!"
                    resultSchedule = errorTimeDepartSt;
                    break;
                }
                // проверить, что время в пределах 00:00-23:59
                String timeForInt = time.replaceAll(":","");
                int timeDep = -1;
                try {
                    timeDep = Integer.parseInt(timeForInt);
                } catch (NumberFormatException nfe){
                    // "error", "ОШИБКА! Введённое время отправления со станции отправления введено в неправильном формате!"
                    resultSchedule = errorTimeDepartSt;
                    nfe.printStackTrace();
                    break;
                }
                if (timeDep == -1){
                    // "error", "ОШИБКА! Что-то пошло не так с введённым временем отправления по станции отправления!"
                    resultSchedule = errorMystiqueTimeDep;
                    break;
                }
                if (timeDep >= 2400){
                    // "error", "ОШИБКА! Введённое время отправления со станции отправления находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                    resultSchedule = errorDepStTimeDepOutOf24h;
                    break;
                }
                // установить время прибытия и отправления - они равны
                timetableZeitplan.setTrainArrivalTime_ZugesAnkunftszeit(time);
                timetableZeitplan.setTrainDepartureTime_ZugesAbfahrtszeit(time);
                // все данные в расписание по станции отправления введены
                timetablesList.add(timetableZeitplan);
                System.out.println("------------serviceAddNewScheduleHandler timetablesList:" + timetablesList);
            }
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! промежуточные станции
            else if (i < (inputedData.length-2)){ // промежуточные станции
                if (i == 3) i = 2; // первая станция после станции отправления - не было времени прибытия
                System.out.println("--------------промежуточные станции----i:" + i);
                if (inputedData[i] == null && inputedData[i+1] == null && inputedData[i+2] == null){
                    System.out.println("--------------промежуточные станции----всё нал, пропускаем");
                } else {
                    timetableZeitplan.setTrainIdZugId(newTrainFromDB);
                    // так как это станция промежуточная, то текущая станция это взять из Листа c этим индексом,
                    // а предыдущая станция в расписании - надо взять из Листа с (индекс минус 1)
                    if (indexStations == 0) indexStations = 1;

                    timetableZeitplan.setPreviousRwstationIdBahnhofId(rwStationsList.get(indexStations-1));
                    timetableZeitplan.setCurrentRwstationIdBahnhofId(rwStationsList.get(indexStations));
                    //          indexStations++;
                    // надо взять следующую станцию
                    String inputedNextStationFmDeparture = "";
                    if ( (inputedRouteQtyStations - rwStationsList.size() ) > 1 ){
                        inputedNextStationFmDeparture = inputedData[i+3];
                        if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                                inputedNextStationFmDeparture.isBlank()){
                            // "error", "ОШИБКА! Вы не ввели промежуточную станцию!"
                            resultSchedule = errorNoMidNextFmDepartSt;
                            break;
                        }
                    } else if ( (inputedRouteQtyStations - rwStationsList.size() ) == 1 ){
                        inputedNextStationFmDeparture = inputedData[(inputedData.length-2)];
                        if (inputedNextStationFmDeparture == null || inputedNextStationFmDeparture.isEmpty() ||
                                inputedNextStationFmDeparture.isBlank()){
                                // "error", "ОШИБКА! Вы не ввели конечную станцию!"
                                resultSchedule = errorNoEndSt;
                            break;
                        }
                    }

                    inputedNextStationFmDeparture = inputedNextStationFmDeparture.toUpperCase();
                    RailwayStationBahnhof rwNextStationFmDepartureFmDB =
                            rwStationRepository.findByRwStationName(inputedNextStationFmDeparture);
                    // если не нашлось, станция из БД будет нал
                    if (rwNextStationFmDepartureFmDB == null){
                        // "error", "ОШИБКА! Введённая промежуточная станция не найдена в базе!"
                        resultSchedule = errorNoNextFmDepartSt;
                        break;
                    }
                    // следующая станция
                    rwStationsList.add(rwNextStationFmDepartureFmDB);
                    timetableZeitplan.setNextRwstationIdBahnhofId(rwNextStationFmDepartureFmDB);
                    indexStations++;

                    // время прибытия и отправления
                    String timeArr = inputedData[i+1];
                    String timeDep = inputedData[i+2];
                    if (timeArr == null || timeArr.isBlank() || timeArr.isEmpty()){
                        // "error", "ОШИБКА! Вы не ввели время прибытия у промежуточной станции!"
                        resultSchedule = errorNoMidStDepTime;
                        break;
                    }
                    if (timeDep == null || timeDep.isBlank() || timeDep.isEmpty()){
                        // "error", "ОШИБКА! Вы не ввели время отправления у промежуточной станции!"
                        resultSchedule = errorNoMidStArrTime;
                        break;
                    }
                    // проверка формата введённого времени
                    if (timeArr.length() != 5 || !timeArr.contains(":")){
                        // "error", "ОШИБКА! Введённое время прибытия на промежуточную станцию введено в неправильном формате!"
                        resultSchedule = errorArrTimeMidSt;
                        break;
                    }
                    if (timeDep.length() != 5 || !timeDep.contains(":")){
                        // "error", "ОШИБКА! Введённое время отправления с промежуточной станции введено в неправильном формате!"
                        resultSchedule = errorDepTimeMidSt;
                        break;
                    }

                    String timeArrTime = timeArr.replaceAll(":","");
                    String timeDepTime = timeDep.replaceAll(":","");
                    System.out.println("-------------timeArrTime--timeDepTime-:" + timeArrTime + "**" + timeDepTime);
                    int timeArrTime1 = -1;
                    int timeDepTime1 = -2;
                    try {
                        timeArrTime1 = Integer.parseInt(timeArrTime);
                    } catch (NumberFormatException nfe){
                        // "error", "ОШИБКА! Введённое время прибытия на промежуточную станцию введено в неправильном формате!"
                        resultSchedule = errorArrTimeMidSt;
                        nfe.printStackTrace();
                        break;
                    }
                    try {
                        timeDepTime1 = Integer.parseInt(timeDepTime);
                    } catch (NumberFormatException nfe){
                        // "error", "ОШИБКА! Введённое время отправления с промежуточной станции введено в неправильном формате!"
                        resultSchedule = errorDepTimeMidSt;
                        nfe.printStackTrace();
                        break;
                    }
                    if (timeArrTime1 >= 2400){
                        // "error", "ОШИБКА! Введённое время прибытия на промежуточную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                        resultSchedule = errorMidStArrTimeOutOf24h;
                        break;
                    }
                    if (timeDepTime1 >= 2400){
                        // "error", "ОШИБКА! Введённое время отправления с промежуточной станции находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                        resultSchedule = errorMidStDepTimeOutOf24h;
                        break;
                    }
                    if (timeArrTime1 != -1 && timeDepTime1 != -2){
                        if (timeArrTime1 > timeDepTime1){
                // проверка по часу 23:55-00:15 - это не ошибка, что время прибытия раньше, чем время отправления
                            if (timeArrTime1 >= 2200 && timeDepTime1 <= 200){
                                LOGGER.info("----------время приб и отпр норм, через полночь, timeArrTime1:" +
                                        timeArrTime1 + ", timeDepTime1:" + timeDepTime1);
                            } else {
                                // "error", "ОШИБКА! Введённое время отправления с промежуточной станции раньше, чем
                                // время прибытия!"
                                resultSchedule = errorWrongArrDepTimeMidSt;
                                break;
                            }
                        }
                    } else {
                        // "error", "ОШИБКА! Что-то пошло не так с введённым временем прибытия и отправления по промежуточной станции!"
                        resultSchedule = errorMystiqueTime;
                        break;
                    }

                    // установить время прибытия и отправления по промежуточной станции
                    timetableZeitplan.setTrainArrivalTime_ZugesAnkunftszeit(timeArr);
                    timetableZeitplan.setTrainDepartureTime_ZugesAbfahrtszeit(timeDep);
                    // все данные в расписание по станции отправления введены
                    timetablesList.add(timetableZeitplan);
                    System.out.println("------------serviceAddNewScheduleHandler timetablesList:" + timetablesList);
                }
            }
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! конечная станция
            else if (i == (inputedData.length-2)) { // конечная станция
                System.out.println("--------------конечная станция----i:" + i);
                if (inputedData[i] == null || inputedData[i].isEmpty() || inputedData[i].isBlank()){
                    // "error", "ОШИБКА! Вы не ввели конечную станцию!"
                    resultSchedule = errorNoEndSt;
                    break;
                }
                timetableZeitplan.setTrainIdZugId(newTrainFromDB);

                // так как это станция конечная, то текущая и следующая станции в расписании одинаковые
                timetableZeitplan.setCurrentRwstationIdBahnhofId(rwStationsList.get(indexStations));
                timetableZeitplan.setNextRwstationIdBahnhofId(rwStationsList.get(indexStations));
                // надо взять предыдущую станцию из Листа
                timetableZeitplan.setPreviousRwstationIdBahnhofId(rwStationsList.get(indexStations-1));
                // обработка времени
                String time = inputedData[i+1];
                // проверка формата введённого времени
                if (time == null || time.isBlank() || time.isEmpty()){
                    // "error", "ОШИБКА! Вы не ввели время прибытия на конечную станцию!"
                    resultSchedule = errorNoEndStTime;
                    break;
                }
                if (time.length() != 5 || !time.contains(":")){
                    // "error", "ОШИБКА! Введённое время прибытия на конечную станцию введено в неправильном формате!"
                    resultSchedule = errorTimeEndSt;
                    break;
                }
                // проверить, что время в пределах 00:00-23:59
                String timeForInt = time.replaceAll(":","");
                int timeDep = -1;
                try {
                    timeDep = Integer.parseInt(timeForInt);
                } catch (NumberFormatException nfe){
                    // "error", "ОШИБКА! Введённое время прибытия на конечную станцию введено в неправильном формате!"
                    resultSchedule = errorTimeEndSt;
                    nfe.printStackTrace();
                    break;
                }
                if (timeDep == -1){
                    // "error", "ОШИБКА! Что-то пошло не так с введённым временем прибытия на конечную станцию!"
                    resultSchedule = errorMystiqueTimeArr;
                    break;
                }
                if (timeDep >= 2400){
                    // "error", "ОШИБКА! Введённое время прибытия на конечную станцию находится вне суток (должно быть в интервале 00:00 - 23:59)!"
                    resultSchedule = errorArrStTimeDepOutOf24h;
                    break;
                }
                // установить время прибытия и отправления - они равны
                timetableZeitplan.setTrainArrivalTime_ZugesAnkunftszeit(time);
                timetableZeitplan.setTrainDepartureTime_ZugesAbfahrtszeit(time);
                // все данные в расписание по станции отправления введены
                timetablesList.add(timetableZeitplan);
                System.out.println("------------serviceAddNewScheduleHandler timetablesList:" + timetablesList);
            }
        }
        System.out.println("--TOTAL-----serviceAddNewScheduleHandler-----timetablesList:" + timetablesList);
        System.out.println("--TOTAL-----serviceAddNewScheduleHandler-----rwStationList:" + rwStationsList);

        // проверка, подряд две станции не могут быть одинаковыми
        for (int i = 1; i < rwStationsList.size(); i++) {
            if (rwStationsList.get(i).getName_RailwayStation_Bahnhof()
                    .equalsIgnoreCase(rwStationsList.get(i-1).getName_RailwayStation_Bahnhof())){
                // "error", "ОШИБКА! Введённая последовательность станций неправильная: две станции назначения подряд не могут быть одинаковыми!"
                resultSchedule = errorTwoEqualSt;
                break;
            }
        }

        // сохранить расписания в БД, если нет ошибок
        // всё ОК: --TOTAL-----serviceAddNewScheduleHandler-----resultSchedule:-1
        System.out.println("--сохранить расписания в БД, если нет ошибок(-1)-----serviceAddNewScheduleHandler-----resultSchedule:" + resultSchedule);
        if (resultSchedule == -1){
            try {
                for (TimetableZeitplan timetable : timetablesList) {
                    timetableRepository.save(timetable);
                }
                // "success", "ПОЗДРАВЛЯЮ! Новый поезд удачно сохранён в базу! Расписание и маршрут следования нового
                // поезда также удачно сохранены в базу!"
                resultSchedule = fullSuccess;
            } catch (Exception se){
                // "error", "ОШИБКА! Что-то пошло не так %() Поезд и его маршрут НЕ сохранились в базе!"
                resultSchedule = errorMystique;
                se.printStackTrace();
            }
        }

        System.out.println("--TOTAL-----serviceAddNewScheduleHandler-----resultSchedule:" + resultSchedule);
        return resultSchedule;
    }

    public int serviceAddNewRwstationNameHandler(String rwstationName){
        if (rwstationName == null || rwstationName.isBlank() || rwstationName.isEmpty())
        {
            return 1; // "result", "ОШИБКА! Название станции не указано!"
        } else {
            rwstationName = rwstationName.toUpperCase();
            RailwayStationBahnhof newStation = new RailwayStationBahnhof();
            newStation.setName_RailwayStation_Bahnhof(rwstationName);
            LOGGER.info(LocalDateTime.now() + "\n ---------newStation->" + newStation);
            try {
                RailwayStationBahnhof stationFromDB = rwStationRepository.findByRwStationName(rwstationName);
                LOGGER.info(LocalDateTime.now() + "\n ---------trainFromDB->" + stationFromDB);
                if (stationFromDB != null){
                    return 2; // "error", "ОШИБКА! Название такой станции уже есть в базе!"
                } else {
                    // TODO: проверять бы точно записался?
                    rwStationRepository.save(newStation);
                    return 3; // "success", "ПОЗДРАВЛЯЕМ! Новое название станции успешно сохранено в базе!"
                }
            } catch (Exception se){
                return 4; // "error", "ОШИБКА! Что-то пошло не так %() название станции НЕ сохранилось в базе!"
            }
        }
    }

    public ArrayList<PassengerInOneTrainDto> serviceFindAllPassengersInOneTrainHandler(String trainNumber){
        // по номеру поезда из базы взять поезд - нужен будет его айди
        TrainZug trainZug = trainRepository.findByNumberTrain(trainNumber);
//        System.out.println("------service-------------------trainZug:" + trainZug);
        int i = 1;
        ArrayList<PassengerInOneTrainDto> passengerInOneTrainDtoArrayList = new ArrayList<>();
        //--trainZug:null не существующий поезд
        // пришёл несуществующий номер поезда - результат будет нал
        if (trainZug == null){
            PassengerInOneTrainDto pdto = new PassengerInOneTrainDto();
            pdto.setNumberInOrder(-1);
            passengerInOneTrainDtoArrayList.add(pdto);
        } else {
            // в билетах, взять Лист всех билетов где есть айди нужного поезда
            ArrayList<TicketFahrkarte> ticketFahrkarteArrayList = ticketRepository.findAllByTrainId(trainZug.getId());
//            System.out.println("----------service---------------ticketFahrkarteArrayList:" + ticketFahrkarteArrayList);

            // сделать Лист пассажировДТО
            for (TicketFahrkarte ticketFahrkarte : ticketFahrkarteArrayList) {
                PassengerInOneTrainDto pdto = new PassengerInOneTrainDto();
                pdto.setNumberInOrder(i);
                pdto.setTicketId(ticketFahrkarte.getId());
                pdto.setPassengerName(ticketFahrkarte.getTicketPassengerId_fahrkarteFahrgastId().getName_passenger_fahrgast());
                pdto.setPassengerSurname(ticketFahrkarte.getTicketPassengerId_fahrkarteFahrgastId()
                        .getSurnamePassenger_familiennameFahrgast());
                pdto.setPassengerId(ticketFahrkarte.getTicketPassengerId_fahrkarteFahrgastId().getId());
                i++;
                passengerInOneTrainDtoArrayList.add(pdto);
            }
        }
//        System.out.println("--------service----------passengerInOneTrainDtoArrayList:" + passengerInOneTrainDtoArrayList);
        return passengerInOneTrainDtoArrayList;
    }

    public String getAllStationsNamesStr() {
        ArrayList<RailwayStationBahnhof> allStationsList = rwStationRepository.findAll();
        String allStationsNames = "Список актуальных станций: ";
        if (allStationsList.isEmpty()){
            allStationsNames = "В базу не внесено ни одной станции!";
        } else {
            for (RailwayStationBahnhof rwStation : allStationsList) {
                allStationsNames = allStationsNames + rwStation.getName_RailwayStation_Bahnhof() + "; ";
            }
        }
        return allStationsNames.trim();
    }

    public String getAllTrainsNumbersStr() {
        ArrayList<TrainZug> allTrainsList = trainRepository.findAll();
        String allTrainsNames = "Список номеров поездов уже имеющихся в базе: ";
        if (allTrainsList.isEmpty()){
            allTrainsNames = "В базу не внесено ни одного поезда!";
        } else {
            for (TrainZug trainZug : allTrainsList) {
                allTrainsNames = allTrainsNames + trainZug.getNumberTrain_nummerZug() + "; ";
            }
        }
        return allTrainsNames.trim();
    }
}
