package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Languages;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RoleRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.UserRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.ControllerService;

import java.security.Principal;

@Controller // если класс отмечен аннот-й @Controller и метод возвращает тип данных String - return "login"; , то
// это возвращает html файл login_ru.html из папки resources/templates
@RequestMapping("/sbb/v1")
public class AccountController {
    private int language = 0;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private ControllerService controllerService;
    private final String[] failureArray = {"0","Falscher Login oder Passwort.","Неправильные имя или пароль.","Wrong login or password."};

    @Autowired
    public AccountController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder,
                             ControllerService controllerService)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.controllerService = controllerService;
    }

    @GetMapping("/")
    public String getStartForm() {
        return "start";
    }

    @GetMapping("/login_de")
    public String getLoginDeForm() {
        language = Languages.DEUTSCH.getLanguageId();
//        System.out.println("----LANGUAGE_DEUTCH(1)------------------language:" + language);
        return "login_de";
    }

    @GetMapping("/login_ru")
    public String getLoginRuForm() {
        language = Languages.RUSSIAN.getLanguageId();
//        System.out.println("----LANGUAGE_RUSSIAN(2)------------------language:" + language);
        return "login_ru";
    }

    @GetMapping("/login_uk")
    public String getLoginUkForm() {
        language = Languages.ENGLISH.getLanguageId();
//        System.out.println("----LANGUAGE_ENGLISH(3)------------------language:" + language);
        return "login_uk";
    }

    @PostMapping("/login-failure")
    public String loginFailure(Model model)
    {
        String failure = failureArray[language];
        model.addAttribute("failure",failure);
        return "login_" + controllerService.getLanguageZone(language);
    }

    @GetMapping("/login-successful")
    public String loginSuccessful(Principal principal)
    {
        // "redirect:/sbb/v1/admin/account_god" or "redirect:/sbb/v1/admin/account" or "redirect:/sbb/v1/user/account"
        return controllerService.getRoleForRedirect(principal);
    }

    @GetMapping("/error") // access Denied Page
    public String loginErrorForm(Model model) {
        String warning = controllerService.getAccessDeniedText(language);
        model.addAttribute("access", warning);
        return "accessDeniedPage";
    }

    @GetMapping("/registration_de")
    public String getRegistrationDeForm(Model model) // тип данных Model используется, чтобы передать полученные
    // данные в хтмл-страничку. Чтобы переданные Моделом данные вывести в хтмл - применяется СпрингФреймворк
    // spring-boot-starter-thymeleaf
    {
//        System.out.println("---------registration------language:" + language);
//        model.addAttribute("title", new MyUser()); // в круглых скобках пара ключ, значение
//        String redirectRegistration = "redirect:/sbb/v1/";
//        redirectRegistration = redirectRegistration + "registration_" + controllerService.getLanguageZone(language);
        return "registration_de";
    }

    @GetMapping("/registration_ru")
    public String getRegistrationRuForm(Model model)
    {
//        System.out.println("---------registration------language:" + language);
//        model.addAttribute("title", new MyUser()); // в круглых скобках пара ключ, значение
//        String redirectRegistration = "redirect:/sbb/v1/";
//        redirectRegistration = redirectRegistration + "registration_" + controllerService.getLanguageZone(language);
        return "registration_ru";
    }

    @GetMapping("/registration_uk")
    public String getRegistrationUkForm(Model model)
    {
//        System.out.println("---------registration------language:" + language);
//        model.addAttribute("title", new MyUser()); // в круглых скобках пара ключ, значение
//        String redirectRegistration = "redirect:/sbb/v1/";
//        redirectRegistration = redirectRegistration + "registration_" + controllerService.getLanguageZone(language);
        return "registration_uk";
    }

    @PostMapping("/registration-handler") // регистрация пользователя, связь с хтмл registration_ru.html
    // form th:action="@{/registration-handler}" method="post"
    public String registrationProcessing(MyUser user, Model model) // User user соберётся логин+пароль из того что
    // введут в хтмл registration_ru.html
    {
        String[][] infoArr = controllerService.getRegistrationHandlerInfo(language);
//        String languageZone = controllerService.getLanguageZone(language); // "de", "ru", etc...
        //String redirectLogin = "redirect:/sbb/v1/login_" + languageZone;
        //String errorRegistration = "registration_" + languageZone;
        if (user.getLogin().length() < 6)
        {
            model.addAttribute("error", infoArr[language][1]
                    /*"ОШИБКА! Длина логина должна быть более 5ти символов!\n"*/);
            return "registration_" + infoArr[language][0] /*languageZone*/;
        }
        if (user.getLogin().length() > 5 && user.getPassword().length() < 9)
        {
            model.addAttribute("error", infoArr[language][2]
                    /*"ОШИБКА! Длина пароля должна быть более 8ми символов!\n"*/);
            return "registration_" + infoArr[language][0] /*languageZone*/;
        }
//        System.out.println("---------registration------language:" + language);
//        System.out.println("---------registration------user:" + user);
        MyUser fromDB = userRepository.findByLogin(user.getLogin()); // из введённых лог+пароль берём логин и смотрим
        // его в БД и если такой логин там уже есть (значит fromDB будет не НАЛ) - значит надо придумывать новый логин
//        System.out.println("--------------fromDB" + fromDB);
        if (fromDB != null)
        {
            model.addAttribute("error", infoArr[language][3] /*"Пользователь с таким именем уже существует"*/);
            return "registration_" + infoArr[language][0] /*languageZone*/;
        }

        user.setRole(roleRepository.findByRoleName("ROLE_USER")); // если логин новый, то добавляем роль
//        System.out.println("---------------user" + user);
        user.setPassword(encoder.encode(user.getPassword())); // кодируем пароль
        //System.out.println("!!!!!!!!!!!!кодируем пароль:" + user.getPassword() );
        userRepository.save(user); // и потом сохраняем польз-ля с закодированным паролем
        // перенаправляем на форму входа, после того как регистрация прошла удачно

        return "redirect:/sbb/v1/login_" + infoArr[language][0] /*languageZone*/; //"redirect:/sbb/v1/login_de";
    }

    @GetMapping("/user/account") // для расширенной работы с пользовательскими сессиями есть
    // пакет session api spring boot
    public String userAccount(Principal principal)
    {
        return "account";
    }
}