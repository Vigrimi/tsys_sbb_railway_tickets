package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;

@Entity // Сущность Роль для юзера для секьюрити
@Table(name = "sbb_roles")
@Getter
@Setter
@ToString
public class MyUserRole
{
    // айди роли
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sbb_role_id", nullable = false)
    private Integer roleId;

    // имя роли
    @Column(name = "sbb_role_name", nullable = false, unique = true)
    private String roleName;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
//    private ArrayList<MyUser> myUsers;
}