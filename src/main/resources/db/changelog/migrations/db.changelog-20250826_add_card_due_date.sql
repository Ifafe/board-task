--liquibase formatted sql
--changeset architect:20250826_add_card_due_date
--comment: Add due_date field to cards table

ALTER TABLE CARDS ADD COLUMN due_date TIMESTAMP NULL;

--rollback ALTER TABLE CARDS DROP COLUMN due_date;