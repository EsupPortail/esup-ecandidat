--
-- Mise à jour de la structure de la table type_decision
--

ALTER TABLE `type_decision`
	ALTER `lib_typ_dec` DROP DEFAULT;
	
ALTER TABLE `type_decision`
	CHANGE COLUMN `lib_typ_dec` `lib_typ_dec` VARCHAR(50) NOT NULL COMMENT 'libellé type de decision';