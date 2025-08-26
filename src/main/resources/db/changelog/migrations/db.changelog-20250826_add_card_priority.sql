--liquibase formatted sql
--changeset architect:20250826_add_card_priority
--comment: Add priority field to cards table

ALTER TABLE CARDS ADD COLUMN priority VARCHAR(10) DEFAULT 'MEDIUM';

--rollback ALTER TABLE CARDS DROP COLUMN priority;