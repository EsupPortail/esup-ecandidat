--
-- Modification table candidat_cursus_interne
--
ALTER TABLE candidat_cursus_interne
	ADD COLUMN not_vet_cursus_interne VARCHAR(50) NULL DEFAULT NULL COMMENT 'note de la vet du cursus interne' AFTER cod_tre_cursus_interne;