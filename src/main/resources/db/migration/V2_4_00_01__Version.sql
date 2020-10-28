ALTER TABLE `version`
	CHANGE COLUMN `val_version` `val_version` VARCHAR(100) NOT NULL COMMENT 'valeur de la version' AFTER `cod_version`;