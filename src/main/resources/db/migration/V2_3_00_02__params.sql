--
-- Modification table parametre
--
ALTER TABLE `parametre`	ADD COLUMN `regex_param` VARCHAR(100) NULL DEFAULT NULL COMMENT 'regex pour les listes de valeur' AFTER `typ_param`;