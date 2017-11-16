--
-- Modification table campagne
--
ALTER TABLE `campagne`
	ADD COLUMN `dat_fin_candidat_camp` DATE NULL DEFAULT NULL COMMENT 'date de fin d\'ouverture aux candidats' AFTER `dat_fin_camp`;