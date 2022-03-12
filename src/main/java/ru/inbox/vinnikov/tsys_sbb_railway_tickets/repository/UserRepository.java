package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;

public interface UserRepository extends JpaRepository<MyUser, Long> { //CrudRepository<MyUser, Long> {
    MyUser findByLogin(String login);
}