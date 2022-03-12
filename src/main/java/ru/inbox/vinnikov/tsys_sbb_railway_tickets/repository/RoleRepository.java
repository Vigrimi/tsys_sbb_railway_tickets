package ru.inbox.vinnikov.tsys_sbb_railway_tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity.MyUserRole;

public interface RoleRepository extends JpaRepository<MyUserRole, Integer> {
    MyUserRole findByRoleName(String roleName);
}