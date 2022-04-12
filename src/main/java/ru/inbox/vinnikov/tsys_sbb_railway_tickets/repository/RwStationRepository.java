package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.RailwayStationBahnhof;
import java.util.ArrayList;

@Repository
public interface RwStationRepository extends JpaRepository<RailwayStationBahnhof, Long> {
    ArrayList<RailwayStationBahnhof> findAll();

    @Query(value = "SELECT * FROM sbb.sbb_railwaystation_bahnhof " +
            "WHERE name_rwstation_bahnhof = :rwStationName", nativeQuery = true)
    RailwayStationBahnhof findByRwStationName(String rwStationName);
}
