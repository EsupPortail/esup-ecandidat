ALTER TABLE `adresse` CHANGE COLUMN `lib_com_etr_adr` `lib_com_etr_adr` VARCHAR(50) NULL DEFAULT NULL COMMENT 'libellé de la commune du candidat' AFTER `cod_com`;
ALTER TABLE `adresse` CHANGE COLUMN `adr1_adr` `adr1_adr` VARCHAR(50) NULL DEFAULT NULL COMMENT 'libellé 1 de l''adresse' AFTER `lib_com_etr_adr`;
ALTER TABLE `adresse` CHANGE COLUMN `adr2_adr` `adr2_adr` VARCHAR(50) NULL DEFAULT NULL COMMENT 'libellé 2 de l''adresse' AFTER `adr1_adr`;
ALTER TABLE `adresse` CHANGE COLUMN `adr3_adr` `adr3_adr` VARCHAR(50) NULL DEFAULT NULL COMMENT 'libellé 3 de l''adresse' AFTER `adr2_adr`;