package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.SeatInTrain;
import java.util.ArrayList;

@Repository
public interface SeatInTrainRepository extends JpaRepository<SeatInTrain, Long> {
    // взять все места на рейсе этого поезда seatsInTrainByVoyage
    ArrayList<SeatInTrain> findAllByVoyageNumber(String voyageNumber);

    SeatInTrain findByVoyageNumberAndTrainSeatNumber(String voyageNumber,int trainSeatNumber);

    @Modifying
    @Query(value = "UPDATE sbb.sbb_seat_in_train SET seat_sequence_stations = :newSequence WHERE (id = :id)"
            , nativeQuery = true)
    void updateSeatSequenceStationsById(Long id,String newSequence);
}
