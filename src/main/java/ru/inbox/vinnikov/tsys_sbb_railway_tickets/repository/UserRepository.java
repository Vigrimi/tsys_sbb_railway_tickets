package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUser;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> { //CrudRepository<MyUser, Long> {
    MyUser findByLogin(String login);
}
