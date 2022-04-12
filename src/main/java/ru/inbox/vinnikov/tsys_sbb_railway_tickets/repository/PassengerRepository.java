package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.PassengerFahrgast;
import java.util.ArrayList;

@Repository
public interface PassengerRepository extends JpaRepository<PassengerFahrgast, Long> {
    ArrayList<PassengerFahrgast> findAllByUserId(long id);
}
