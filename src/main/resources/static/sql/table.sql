create table member(
    id bigint auto_increment primary key,
    name varchar(255) not null unique,
    password varchar(255) not null
);