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
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Languages;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RoleRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.UserRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.ControllerService;

import java.security.Principal;

@Controller
@RequestMapping("/sbb/v1")
public class AccountController {
    private int language = 0;

    private final ControllerService controllerService;

    @Autowired
    public AccountController(ControllerService controllerService)
    {
        this.controllerService = controllerService;
    }
    //-----------------------------------------------------------

    @GetMapping("/")
    public String getStartForm() {
        return "startstyle";
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
        model.addAttribute("failure",controllerService.getFailure(language));
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
    public String getRegistrationDeForm()
    {
        return "registration_de";
    }

    @GetMapping("/registration_ru")
    public String getRegistrationRuForm()
    {
        return "registration_ru";
    }

    @GetMapping("/registration_uk")
    public String getRegistrationUkForm()
    {
        return "registration_uk";
    }

    @PostMapping("/registration-handler")
    public String registrationProcessing(MyUser user, Model model)
    {
        ResultDto resultDto = controllerService.getRegistrationProcessing(user,language);
        model.addAttribute("error",resultDto.getResultsEnumList());
        return resultDto.getSomeText(); //"redirect:/sbb/v1/login_" + infoArr[language][0] /*languageZone*/; //"redirect:/sbb/v1/login_de";
    }

}