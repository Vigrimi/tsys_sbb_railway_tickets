package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService // или какой-то ещё ...Провайдер
{
    // инъекция бина
    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // если у пользователя несколько ролей, то заранее тут достаём роли и склеиваем их через запятую
    // Set<Role> roles = fromDb.getRoles(); и roles вставляем ниже в
    // строку: .roles(fromDb.getRole().getName().split("_")[1])

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MyUser fromDb = userRepository.findByLogin(userName); // обращаемся к репозиторию
        // и достаём пользователя найденного по логину
        // MyUser - это мой созданный класс в папке entity, а ниже
        // return User.builder - Юзер - это класс из спринг-секьюрити, чтобы не было ошибки именно поэтому
        // и надо прописать MyUser or etc, а по хорошему наш класс в
        // папке entity изначально надо назвать как-нить типа MyUser
        if (fromDb == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + userName);
        }
//        System.out.println("***------CustomUserDetailsService--MyUser fromDb:"
//                + fromDb);
        // по логину юзер найден и передаём данные для процесса авторизации
//        System.out.println("***------CustomUserDetailsService--fromDb.getRole().getRoleName():"
//                + fromDb.getRole().getRoleName());

        return User.builder()
                .username(fromDb.getLogin())
                .password(fromDb.getPassword()) // спринг-секьюрити сам возьмёт пароль из базы и сравнит
                // с вводимым значением
                .roles(fromDb.getRole().getRoleName().split("_")[1]) // передаём роли - они String, у пользователя
      // может быть несколько ролей, и префикс "ROLE_" спринг-секьюрити подставит сам, и если мы передадим ROLE_USER,
                // то будет ошибка программы, надо передавать просто USER
                .build();
    }
}