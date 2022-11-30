--liquibase formatted sql

--changeset hailt:1
CREATE SEQUENCE hibernate_sequence START 1;

--changeset hailt:2
CREATE TABLE car_park (
    id bigint PRIMARY KEY,
    car_park_no varchar UNIQUE NOT NULL,
    address varchar NULL,
    coordinate geometry(Geometry, 4326) NOT NULL
);

--changeset hailt:3
CREATE TABLE car_park_availability (
    id bigint PRIMARY KEY,
    car_park_id bigint NOT NULL,
    total_lots int NULL,
    lots_available int NULL,
    last_updated_on timestamp NOT NULL,

    CONSTRAINT car_park_id_unique UNIQUE (car_park_id),
    CONSTRAINT fk_car_park FOREIGN KEY(car_park_id) REFERENCES car_park(id)
);
