-- сущность Роль для секьюрити
--DROP TABLE IF EXISTS sbb_roles;
CREATE TABLE IF NOT EXISTS sbb_roles (
    sbb_role_id INTEGER NOT NULL AUTO_INCREMENT,
    sbb_role_name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT sbb_roles_PRIMARY_KEY PRIMARY KEY (sbb_role_id)
);
-- сделать: если таблица sbb_roles уже существует, то не вставлять роли
--insert into sbb_roles(sbb_role_name) values ('ROLE_ADMIN');
--insert into sbb_roles(sbb_role_name) values ('ROLE_USER');

-- сущность Юзер для секьюрити
CREATE TABLE IF NOT EXISTS sbb_users (
    id SERIAL PRIMARY KEY,
    version INTEGER,
    login VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT sbb_roles_FOREIGN_KEY FOREIGN KEY (role_id) REFERENCES sbb_roles (sbb_role_id)
);
-- сделать: если таблица sbb_users уже существует, то не вставлять админа
--insert into sbb_users(version,login,password,role_id) values ('0','adminuser16','$2a$10$sSnf4QkRKbqRkqxKTnf7A.bzJ4moWb0h7kstcST4tx6T9mp4vDIgG','1');

-- сущность Поезд
CREATE TABLE IF NOT EXISTS sbb_train_zug (
    id INTEGER NOT NULL AUTO_INCREMENT,
    version INTEGER,
    number_train_nummer_zug VARCHAR(255) NOT NULL UNIQUE,
    passengers_capacity_passagierkapazitat INTEGER NOT NULL,
    CONSTRAINT sbb_train_zug_PRIMARY_KEY PRIMARY KEY (id)
);
--insert into sbb_train_zug(version,number_train_nummer_zug,passengers_capacity_passagierkapazitat)
--values ('0','104','52');

-- сущность Станция жд
CREATE TABLE IF NOT EXISTS sbb_railwaystation_bahnhof (
    id INTEGER NOT NULL AUTO_INCREMENT,
    version INTEGER,
    name_rwstation_bahnhof VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT sbb_railwaystation_bahnhof_PRIMARY_KEY PRIMARY KEY (id)
);

-- таблица для мэни-ту-мэни: станции следования у поезда
CREATE TABLE IF NOT EXISTS stations_sequence_stationen_sequenz (
    train_zug_id INTEGER NOT NULL,
    rwstation_bahnhof_id INTEGER NOT NULL
--    , CONSTRAINT train_zug_FOREIGN_KEY FOREIGN KEY (train_zug_id) REFERENCES sbb_train_zug (id),
--    CONSTRAINT rwstation_bahnhof_FOREIGN_KEY FOREIGN KEY (rwstation_bahnhof_id) REFERENCES sbb_railwayStation_bahnhof (id)
);

-- сущность Пассажир
CREATE TABLE IF NOT EXISTS sbb_passenger_fahrgast (
    id INTEGER NOT NULL AUTO_INCREMENT,
    version INTEGER,
    name_passenger_fahrgast VARCHAR(255) NOT NULL,
    surname_passenger_familienname_fahrgast VARCHAR(255) NOT NULL,
    birthday_passenger_geburtstag_fahrgast VARCHAR(255) NOT NULL,
    CONSTRAINT sbb_passenger_fahrgast_PRIMARY_KEY PRIMARY KEY (id)
);

-- сущность Билет жд
CREATE TABLE IF NOT EXISTS sbb_ticket_fahrkarte (
    id SERIAL PRIMARY KEY,
    version INTEGER,
    --ticket_train_number_fahrkarte_zug_nummer VARCHAR(255) NOT NULL,
    ticket_train_number_fahrkarte_zug_nummer INTEGER NOT NULL,
    ticket_passenger_id_fahrkarte_fahrgast_id INTEGER NOT NULL,
    CONSTRAINT sbb_train_zug_FOREIGN_KEY
        FOREIGN KEY (ticket_train_number_fahrkarte_zug_nummer)
        REFERENCES sbb_train_zug (id),
    CONSTRAINT sbb_passenger_fahrgast_FOREIGN_KEY
        FOREIGN KEY (ticket_passenger_id_fahrkarte_fahrgast_id)
        REFERENCES sbb_passenger_fahrgast (id)
);

-- Сущность Расписание
CREATE TABLE IF NOT EXISTS sbb_timetable_zeitplan (
    id SERIAL PRIMARY KEY,
    version INTEGER,
    train_id_zug_id INTEGER NOT NULL,
    previous_rwstation_id_bahnhof_id INTEGER NOT NULL,
    current_rwstation_id_bahnhof_id INTEGER NOT NULL,
    next_rwstation_id_bahnhof_id INTEGER NOT NULL,
    train_arrival_time_zuges_ankunftszeit VARCHAR(5) NOT NULL,
    train_departure_time_zuges_abfahrtszeit VARCHAR(5) NOT NULL,
    CONSTRAINT sbb_timetable_train_zug_FOREIGN_KEY
            FOREIGN KEY (train_id_zug_id)
            REFERENCES sbb_train_zug (id),
    CONSTRAINT sbb_prev_railwaystation_bahnhof_FOREIGN_KEY
            FOREIGN KEY (previous_rwstation_id_bahnhof_id)
            REFERENCES sbb_railwaystation_bahnhof (id),
    CONSTRAINT sbb_cur_railwaystation_bahnhof_FOREIGN_KEY
            FOREIGN KEY (current_rwstation_id_bahnhof_id)
            REFERENCES sbb_railwaystation_bahnhof (id),
    CONSTRAINT sbb_next_railwaystation_bahnhof_FOREIGN_KEY
            FOREIGN KEY (next_rwstation_id_bahnhof_id)
            REFERENCES sbb_railwaystation_bahnhof (id)
);
