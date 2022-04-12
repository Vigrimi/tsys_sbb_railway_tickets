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
public class CustomUserDetailsService implements UserDetailsService
{
    // инъекция бина
    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MyUser fromDb = userRepository.findByLogin(userName); // обращаемся к репозиторию

        if (fromDb == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + userName);
        }

        return User.builder()
                .username(fromDb.getLogin())
                .password(fromDb.getPassword()) // спринг-секьюрити сам возьмёт пароль из базы и сравнит с вводимым значением
                .roles(fromDb.getRole().getRoleName().split("_")[1]) // передаём роли - они String, у пользователя
      // может быть несколько ролей, и префикс "ROLE_" спринг-секьюрити подставит сам, и если мы передадим ROLE_USER,
                // то будет ошибка программы, надо передавать просто USER
                .build();
    }
}