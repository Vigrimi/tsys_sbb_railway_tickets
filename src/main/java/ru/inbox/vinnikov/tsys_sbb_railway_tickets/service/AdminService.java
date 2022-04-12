package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.dto.ResultDto;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.enums.Results;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RoleRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.UserRepository;

import java.security.Principal;
import java.util.ArrayList;

@Service
public class AdminService {
    @Value("${admin.levelgod}")
    private String adminLevelGod;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public AdminService(UserRepository userRepository,RoleRepository roleRepository,PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }
    //--------------------------------------------------------
    public String whereToGoIfAdminGoesIntoAdminLevelGod(Principal principal,String whereToGo){
//        String whereToGo = "account_admin_god";
        // если админ не уровня бог, то редирект на error
        if (!principal.getName().equals(adminLevelGod)){
            whereToGo = "redirect:/sbb/v1/error";
        }
        return whereToGo;
    }

    public ResultDto getRegistrationNewAdminProcessing(MyUser user, Principal principal){
        ResultDto resultDto = new ResultDto();
        ArrayList<String> resultsEnumList = new ArrayList<>();
        resultDto.setResultsEnumList(resultsEnumList);

        // если админ HE уровня бог, то редирект на error
        if (!principal.getName().equals(adminLevelGod)){
            resultDto.setSomeText("redirect:/sbb/v1/error");
        } else { // иначе если админ уровня бог, то работаем
            if (user.getLogin().length() < 6)
            {
//                System.out.println("----256-----registration------user.getLogin():" + user.getLogin());
//                        "error","ОШИБКА! Длина логина должна быть более 5ти символов!\n");
                resultsEnumList.add(Results.ERROR_LOGIN_LENGTH.getResultText());
                resultDto.setSomeText("registration_new_admin");
            } else
            if (user.getLogin().length() > 5 && user.getPassword().length() < 9)
            {
//                        "error","ОШИБКА! Длина пароля должна быть более 8ми символов!\n");
                resultsEnumList.add(Results.ERROR_PASSWORD_LENGTH.getResultText());
                resultDto.setSomeText("registration_new_admin");
            } else {
                MyUser fromDB = userRepository.findByLogin(user.getLogin());
                if (fromDB != null)
                {
//                    "error", "ОШИБКА! Сотрудник с таким именем (логином) уже существует!\n");
                    resultsEnumList.add(Results.ERROR_ADMIN_ALREADY_EXISTS.getResultText());
                    resultDto.setSomeText("registration_new_admin");
                } else {
                    user.setRole(roleRepository.findByRoleName("ROLE_ADMIN")); // если логин новый, то добавляем роль
                    user.setPassword(encoder.encode(user.getPassword())); // кодируем пароль
                    userRepository.save(user); // и потом сохраняем польз-ля с закодированным паролем
//                    System.out.println("---286-----------user:" + user);
                    resultsEnumList.add(Results.CONGRATULATION.getResultText());
                    resultDto.setSomeText("registration_new_admin_succesful");
                }
            }
        }
        return resultDto;
    }

}
