package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TicketFahrkarte;
import java.util.ArrayList;

public interface TicketRepository extends JpaRepository<TicketFahrkarte, Long> {
    @Query(value =
            "SELECT * FROM sbb.sbb_ticket_fahrkarte WHERE ticket_train_number_fahrkarte_zug_nummer = :id"
            , nativeQuery = true)
    ArrayList<TicketFahrkarte> findAllByTrainId(Long id);

}
