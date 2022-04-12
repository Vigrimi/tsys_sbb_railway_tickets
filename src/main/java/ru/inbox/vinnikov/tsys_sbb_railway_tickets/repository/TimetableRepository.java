package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableZeitplan, Long> {
    @Query(value = "SELECT * FROM sbb.sbb_timetable_zeitplan WHERE current_rwstation_id_bahnhof_id = :rwstationId"
            , nativeQuery = true)
    ArrayList<TimetableZeitplan> findAllByCurrentRwstationId(long rwstationId);

//    @Query(value = "SELECT * FROM sbb.sbb_timetable_zeitplan", nativeQuery = true)
//    ArrayList<TimetableZeitplan> findAll();
}
