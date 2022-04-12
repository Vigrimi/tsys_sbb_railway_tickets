package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUserRole;

@Repository
public interface RoleRepository extends JpaRepository<MyUserRole, Integer> {
    MyUserRole findByRoleName(String roleName);
}