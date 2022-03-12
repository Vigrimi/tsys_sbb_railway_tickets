package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.TimetableZeitplan;

public interface TimetableRepository extends JpaRepository<TimetableZeitplan, Long> {
}
