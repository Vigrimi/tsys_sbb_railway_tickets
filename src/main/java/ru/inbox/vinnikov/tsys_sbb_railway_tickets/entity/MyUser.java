package ru.inbox.vinnikov.tsys_sbb_railway_tickets.entity;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.*;

@Entity //Сущность Юзер для секьюрити
@Table(name = "sbb_users")
@Getter
@Setter
public class MyUser extends SuperclassForEntity {
    // из суперкласса придёт айди и версия
    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;*/

    // логин юзера
    @NotNull(message = "Поле не может быть пустым")
    @Size(min = 5, message = "Значение от 5 символов")
    @Column(name = "login", nullable = false, unique = true)
    private String login; // name

    // пароль юзера
    @NotNull(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Значение от 8 символов")
    @Column(name = "password", nullable = false)
    private String password;

    // присоединить айди роли - у многих юзеров может быть одна и та же роль
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private MyUserRole role;

    @Override
    public String toString() {
        return "MyUser{" +  "user_id='" + getId() + '\'' + ", version='" + getVersion() + '\'' +
                ", login='" + login + '\'' +
                ", password='<masked" /*+ password*/ + ">'" +
                ", role=" + role +
                '}';
    }
}