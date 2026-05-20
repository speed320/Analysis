-- liquibase formatted sql
-- changeset sergey:0001_init_table

CREATE SCHEMA IF NOT EXISTS auth_stage;

CREATE TABLE IF NOT EXISTS auth_stage.users (
    id                  BIGSERIAL           PRIMARY KEY,
    username            VARCHAR(50)         NOT NULL UNIQUE,
    password            VARCHAR(255)        NOT NULL,
    email               VARCHAR(100)        NOT NULL UNIQUE,
    created_at          TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP
);