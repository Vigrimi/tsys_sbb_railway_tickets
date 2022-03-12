package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TrainZug;
import java.util.ArrayList;

public interface TrainRepository extends JpaRepository<TrainZug, Long> {
    ArrayList<TrainZug> findAll();

    @Query(value = "SELECT * FROM sbb.sbb_train_zug WHERE number_train_nummer_zug = :numberTrain", nativeQuery = true)
    TrainZug findByNumberTrain(String numberTrain);

//    @Query(value = "SELECT c FROM Course c WHERE c.title LIKE %:param%") // %:param% - % значит что может начинаться и
//    List<CourseSpringExample> getCourseLikeTitle(@Param("param") String str);
}
