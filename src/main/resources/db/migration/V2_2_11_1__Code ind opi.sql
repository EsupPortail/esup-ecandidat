--
-- Modification table pj_opi
--
ALTER TABLE pj_opi ADD COLUMN cod_ind_opi VARCHAR(20) NULL DEFAULT NULL COMMENT 'Code individu opi' AFTER cod_opi;
