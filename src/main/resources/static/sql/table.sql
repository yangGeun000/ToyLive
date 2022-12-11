create table member(
    id int auto_increment primary key,
    name varchar(255) not null unique,
    password varchar(255) not null
);