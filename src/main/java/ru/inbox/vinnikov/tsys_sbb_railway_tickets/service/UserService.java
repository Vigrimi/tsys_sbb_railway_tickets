package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.RwStationRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.TimetableRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository.UserRepository;

import java.security.Principal;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
//---------------------------------------------------------------------------------------
    public long getUserIdByPrincipalName(Principal principal){
        MyUser myUser = userRepository.findByLogin(principal.getName());
        return myUser.getId();
    }

}
