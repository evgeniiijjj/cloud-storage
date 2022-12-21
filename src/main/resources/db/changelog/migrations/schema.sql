
create table files_data
(
    id bigint generated always as identity primary key,
    file_name varchar(255) not null unique,
    content_type varchar(255) not null,
    date varchar(50) not null,
    size bigint,
    deleted boolean
);

create table authorization_data
(
    id bigint generated always as identity primary key,
    login varchar(50) not null,
    password varchar(255) not null
);

insert into authorization_data (login, password)
values ('evg@mail.ru', '$2a$10$g4VDBe46xuZuM8s6Qxz8wuOvPi0IFl9.C9SrPuBUAkUdIP9c3gBn2'),
       ('tim@mail.ru', '$2a$10$In1DpIF0G9hxr7B1WcjRS.9zziH9xQBddnWYu6K2se/lWRy2JhuvK'),
       ('var@mail.ru', '$2a$10$kgnhc5wia/2rF3ddopncDedOxHleddxDCMK8NR0aYmTUrev1j.Owe');

create table jwt_blacklist
(
    jwt varchar(255) primary key,
    date varchar(50) not null
)