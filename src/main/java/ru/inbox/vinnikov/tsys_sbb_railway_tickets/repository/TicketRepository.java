package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TicketFahrkarte;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketFahrkarte, Long> {
    @Query(value =
            "SELECT * FROM sbb.sbb_ticket_fahrkarte WHERE ticket_train_number_fahrkarte_zug_nummer = :id"
            , nativeQuery = true)
    ArrayList<TicketFahrkarte> findAllByTrainId(Long id);

    @Query(value =
            "SELECT * FROM sbb.sbb_ticket_fahrkarte WHERE ticket_passenger_id_fahrkarte_fahrgast_id = :id"
            , nativeQuery = true)
    ArrayList<TicketFahrkarte> findAllByPassengerId(Long id);

    @Query(value =
            "SELECT * FROM sbb.sbb_ticket_fahrkarte WHERE ticket_train_number_fahrkarte_zug_nummer = :trainId " +
                    "AND ticket_departure_date = :departureDate"
            , nativeQuery = true)
    ArrayList<TicketFahrkarte> findAllByTrainIdAndDepartureDate(Long trainId,String departureDate);

    @Query(value =
            "SELECT * FROM sbb.sbb_ticket_fahrkarte WHERE ticket_passenger_id_fahrkarte_fahrgast_id = :ids"
            , nativeQuery = true)
    ArrayList<TicketFahrkarte> findAllByPassengerIds(ArrayList<Long> ids);
}
