-- liquibase formatted sql
-- changeset sergey:0001_init_table

CREATE SCHEMA IF NOT EXISTS output_stage;

CREATE TABLE IF NOT EXISTS output_stage.analysis_results (
    request_id          UUID                PRIMARY KEY,
    user_id             BIGINT              NOT NULL,
    status              VARCHAR(20)         NOT NULL,
    request_type        VARCHAR(20)         NOT NULL,
    result_data         JSONB,
    created_at          TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at        TIMESTAMPTZ
);