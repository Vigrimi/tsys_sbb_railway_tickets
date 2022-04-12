package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RwStationsTrainSequence;
import java.util.ArrayList;

@Repository
public interface RwStationsTrainSequenceRepository extends JpaRepository<RwStationsTrainSequence, Long> {
    ArrayList<RwStationsTrainSequence> findAll();
    RwStationsTrainSequence findBySequenceTrainNumber(String trainNumber);
}
