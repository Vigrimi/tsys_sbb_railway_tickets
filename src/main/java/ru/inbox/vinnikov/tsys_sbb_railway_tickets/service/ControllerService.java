package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.PassengerInOneTrainDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.*;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Languages;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static ru.inbox.vinnikov.tsys_sbb_railway_tickets.TsysSbbRailwayTicketsApplication.LOGGER;

@Service
public class ControllerService {
    private final String[] failureArray = {"0","Falscher Login oder Passwort.","Неправильные имя или пароль.","Wrong login or password."};
    @Value("${admin.levelgod}")
    private String adminLevelGod;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public ControllerService(UserRepository userRepository,RoleRepository roleRepository,PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }
//----------------------------------------------------------------------------------------
    //TODO поменять POST запросы на GET где идёт простой поиск из БД
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

    public String getRoleForRedirect(Principal principal){
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

    public String getFailure(int language){
        return failureArray[language];
    }

    public ResultDto getRegistrationProcessing(MyUser user,int language){
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);

        String[][] infoArr = getRegistrationHandlerInfo(language);
//        String languageZone = controllerService.getLanguageZone(language); // "de", "ru", etc...
        //String redirectLogin = "redirect:/sbb/v1/login_" + languageZone;
        //String errorRegistration = "registration_" + languageZone;
        if (user.getLogin().length() < 6)
        {
            /*"ОШИБКА! Длина логина должна быть более 5ти символов!\n"*/
            resultsEnumList.add(infoArr[language][1]);
            resultDto.setSomeText("registration_" + infoArr[language][0]);
        } else
        if (user.getLogin().length() > 5 && user.getPassword().length() < 9)
        {
            /*"ОШИБКА! Длина пароля должна быть более 8ми символов!\n"*/
            resultsEnumList.add(infoArr[language][2]);
            resultDto.setSomeText("registration_" + infoArr[language][0]);
        } else {
            MyUser fromDB = userRepository.findByLogin(user.getLogin());
            if (fromDB != null)
            {
                /*"Пользователь с таким именем уже существует"*/
                resultsEnumList.add(infoArr[language][3]);
                resultDto.setSomeText("registration_" + infoArr[language][0]);
            } else {
                user.setRole(roleRepository.findByRoleName("ROLE_USER")); // если логин новый, то добавляем роль
//        System.out.println("---------------user" + user);
                user.setPassword(encoder.encode(user.getPassword())); // кодируем пароль
                userRepository.save(user); // и потом сохраняем польз-ля с закодированным паролем
                // перенаправляем на форму входа, после того как регистрация прошла удачно
                resultsEnumList.add(Results.CONGRATULATION.getResultText());
                resultDto.setSomeText("redirect:/sbb/v1/login_" + infoArr[language][0]);
            }
        }
//        System.out.println("---------registration------language:" + language);
//        System.out.println("---------registration------user:" + user);
        return resultDto;
    }
}
