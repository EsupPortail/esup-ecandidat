CREATE TABLE `siscol_option_bac` (
	`cod_opt_bac` VARCHAR(50) NOT NULL COMMENT 'Code option bac',
	`lib_opt_bac` VARCHAR(500) NOT NULL COMMENT 'Libelle long option bac',
	`lic_opt_bac` VARCHAR(200) NOT NULL COMMENT 'Libelle court option bac',
	`daa_deb_val_opt_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de debut de l''option du bac',
	`daa_fin_val_opt_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de fin de l''option du bac',
	`tem_en_sve_opt_bac` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Temoin en service',
	PRIMARY KEY (`cod_opt_bac`)
)
COMMENT='Referentiel SiScol : options baccalaureats'
ENGINE=InnoDB
;

CREATE TABLE `siscol_specialite_bac` (
	`cod_spe_bac` VARCHAR(50) NOT NULL COMMENT 'Code specialite bac',
	`lib_spe_bac` VARCHAR(500) NOT NULL COMMENT 'Libelle long specialite bac',
	`lic_spe_bac` VARCHAR(200) NOT NULL COMMENT 'Libelle court specialite bac',
	`daa_deb_val_spe_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de debut de la specialite du bac',
	`daa_fin_val_spe_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de fin de la specialite du bac',
	`tem_en_sve_spe_bac` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Temoin en service',
	PRIMARY KEY (`cod_spe_bac`)
)
COMMENT='Referentiel SiScol : specialites baccalaureats'
ENGINE=InnoDB
;

CREATE TABLE `siscol_bac_opt_bac` (
	`cod_bac` VARCHAR(50) NOT NULL COMMENT 'Code baccalaureat ou equivalence',
	`cod_opt_bac` VARCHAR(50) NOT NULL COMMENT 'Code option bac',
	PRIMARY KEY (`cod_bac`, `cod_opt_bac`),
	CONSTRAINT `FK_bac_opt_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`),
	CONSTRAINT `FK_bac_opt_bac_opt_bac_cod_opt_bac` FOREIGN KEY (`cod_opt_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`)
)
COMMENT='Referentiel SiScol : table bac - options bac'
ENGINE=InnoDB
;

CREATE TABLE `siscol_bac_spe_bac` (
	`cod_bac` VARCHAR(50) NOT NULL COMMENT 'Code baccalaureat ou equivalence',
	`cod_spe_bac` VARCHAR(50) NOT NULL COMMENT 'Code specialite bac',
	PRIMARY KEY (`cod_bac`, `cod_spe_bac`),
	CONSTRAINT `FK_bac_spe_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`),
	CONSTRAINT `FK_bac_spe_bac_spe_bac_cod_spe_bac` FOREIGN KEY (`cod_spe_bac`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`)
)
COMMENT='Referentiel SiScol : table bac - specialites bac'
ENGINE=InnoDB
;

ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_spe1_bac_ter` VARCHAR(50) NULL DEFAULT NULL COMMENT 'specialite 1 de terminale' AFTER `cod_mnb`;
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_spe2_bac_ter` VARCHAR(50) NULL DEFAULT NULL COMMENT 'specialite 2 de terminale' AFTER `cod_spe1_bac_ter`;
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_spe_bac_pre` VARCHAR(50) NULL DEFAULT NULL COMMENT 'specialite de première abandonnee' AFTER `cod_spe2_bac_ter`;
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_opt1_bac` VARCHAR(50) NULL DEFAULT NULL COMMENT 'option 1 de terminale' AFTER `cod_spe_bac_pre`;
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_opt2_bac` VARCHAR(50) NULL DEFAULT NULL COMMENT 'option 2 de terminale' AFTER `cod_opt1_bac`;
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_opt3_bac` VARCHAR(50) NULL DEFAULT NULL COMMENT 'option 3 de terminale' AFTER `cod_opt2_bac`;
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `cod_opt4_bac` VARCHAR(50) NULL DEFAULT NULL COMMENT 'option 4 de terminale' AFTER `cod_opt3_bac`;

ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe1_bac_ter` FOREIGN KEY (`cod_spe1_bac_ter`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe2_bac_ter` FOREIGN KEY (`cod_spe2_bac_ter`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe_bac_pre` FOREIGN KEY (`cod_spe_bac_pre`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt1_bac` FOREIGN KEY (`cod_opt1_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt2_bac` FOREIGN KEY (`cod_opt2_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt3_bac` FOREIGN KEY (`cod_opt3_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt4_bac` FOREIGN KEY (`cod_opt4_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`);
