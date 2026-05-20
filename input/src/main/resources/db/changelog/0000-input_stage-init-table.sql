-- liquibase formatted sql
-- changeset sergey:0000_init_table

CREATE SCHEMA IF NOT EXISTS input_stage;

CREATE TABLE IF NOT EXISTS input_stage.platform (
    id                          BIGSERIAL                PRIMARY KEY,
    platform_name               VARCHAR(50)              NOT NULL UNIQUE                                -- название платформы
);

CREATE TABLE IF NOT EXISTS input_stage.marketing_data (
    id                          BIGSERIAL                PRIMARY KEY,
    user_id                     BIGINT                   NOT NULL,                                      -- id пользователя
    platform_id                 BIGINT                   NOT NULL,                                      -- id платформы для размещения рекламмы
    reporting_month             DATE                     NOT NULL,                                      -- дата статисики, важен только месяц
    costs                       NUMERIC(15, 2)           NOT NULL,                                      -- затраты на рекламу
    sales_volume                NUMERIC(15, 2)           NOT NULL,                                      -- объем продаж
    created_at                  TIMESTAMPTZ              NOT NULL DEFAULT CURRENT_TIMESTAMP,            -- когда добавлена запись

    CONSTRAINT fk_platform
    FOREIGN KEY (platform_id)
    REFERENCES input_stage.platform(id)
    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_marketing_user_id ON input_stage.marketing_data(user_id);