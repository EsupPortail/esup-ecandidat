--
-- Modification table candidat
--
ALTER TABLE `candidat`
	CHANGE COLUMN `cle_ine_candidat` `cle_ine_candidat` VARCHAR(2) NULL DEFAULT NULL COMMENT 'INE du candidat' AFTER `ine_candidat`;