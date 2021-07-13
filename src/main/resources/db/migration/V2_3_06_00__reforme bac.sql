CREATE TABLE `siscol_option_bac` (
	`cod_opt_bac` VARCHAR(50) NOT NULL COMMENT 'Code option bac',
	`lib_opt_bac` VARCHAR(500) NOT NULL COMMENT 'Libelle long option bac',
	`lic_opt_bac` VARCHAR(200) NOT NULL COMMENT 'Libelle court option bac',
	`daa_deb_val_opt_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de début de l''option du bac',
	`daa_fin_val_opt_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de fin de l''option du bac',
	`tem_en_sve_opt_bac` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Témoin en service',
	PRIMARY KEY (`cod_opt_bac`)
)
COMMENT='Rérérentiel SiScol : options baccalaureats'
ENGINE=InnoDB
;

CREATE TABLE `siscol_specialite_bac` (
	`cod_spe_bac` VARCHAR(50) NOT NULL COMMENT 'Code spécialité bac',
	`lib_spe_bac` VARCHAR(500) NOT NULL COMMENT 'Libelle long spécialité bac',
	`lic_spe_bac` VARCHAR(200) NOT NULL COMMENT 'Libelle court spécialité bac',
	`daa_deb_val_spe_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de début de la spécialité du bac',
	`daa_fin_val_spe_bac` VARCHAR(4) NULL DEFAULT NULL COMMENT 'Date de fin de la spécialité du bac',
	`tem_en_sve_spe_bac` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Témoin en service',
	PRIMARY KEY (`cod_spe_bac`)
)
COMMENT='Rérérentiel SiScol : specialités baccalaureats'
ENGINE=InnoDB
;

CREATE TABLE `siscol_bac_opt_bac` (
	`cod_bac` VARCHAR(50) NOT NULL COMMENT 'Code baccalaureat ou equivalence',
	`cod_opt_bac` VARCHAR(50) NOT NULL COMMENT 'Code option bac',
	PRIMARY KEY (`cod_bac`, `cod_opt_bac`),
	CONSTRAINT `FK_bac_opt_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`),
	CONSTRAINT `FK_bac_opt_bac_opt_bac_cod_opt_bac` FOREIGN KEY (`cod_opt_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`)
)
COMMENT='Rérérentiel SiScol : table de correspondance bac - options bac'
ENGINE=InnoDB
;

CREATE TABLE `siscol_bac_spe_bac` (
	`cod_bac` VARCHAR(50) NOT NULL COMMENT 'Code baccalaureat ou equivalence',
	`cod_spe_bac` VARCHAR(50) NOT NULL COMMENT 'Code spécialité bac',
	PRIMARY KEY (`cod_bac`, `cod_spe_bac`),
	CONSTRAINT `FK_bac_spe_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`),
	CONSTRAINT `FK_bac_spe_bac_spe_bac_cod_spe_bac` FOREIGN KEY (`cod_spe_bac`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`)
)
COMMENT='Rérérentiel SiScol : table de correspondance bac - specialités bac'
ENGINE=InnoDB
;

CREATE TABLE `candidat_bac_specialite` (
	`id_candidat` INT(10) NOT NULL COMMENT 'identifiant du candidat',
	`cod_spe_bac` VARCHAR(50) NOT NULL COMMENT 'code spécialité bac',
	PRIMARY KEY (`id_candidat`, `cod_spe_bac`) USING BTREE,
	INDEX `fk_candidat_bac_specialite_candidat_id_candidat` (`id_candidat`) USING BTREE,
	INDEX `fk_candidat_bac_specialite_siscol_specialite_bac_cod_spe_bac` (`cod_spe_bac`) USING BTREE,
	CONSTRAINT `fk_candidat_bac_specialite_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`),
	CONSTRAINT `fk_candidat_bac_specialite_siscol_specialite_bac_cod_spe_bac` FOREIGN KEY (`cod_spe_bac`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`)
)
COMMENT='table des spécialités bacs du candidat'
ENGINE=InnoDB
;

CREATE TABLE `candidat_bac_option` (
	`id_candidat` INT(10) NOT NULL COMMENT 'identifiant du candidat',
	`cod_opt_bac` VARCHAR(50) NOT NULL COMMENT 'code option bac',
	PRIMARY KEY (`id_candidat`, `cod_opt_bac`) USING BTREE,
	INDEX `fk_candidat_bac_option_candidat_id_candidat` (`id_candidat`) USING BTREE,
	INDEX `fk_candidat_bac_option_siscol_option_bac_cod_opt_bac` (`cod_opt_bac`) USING BTREE,
	CONSTRAINT `fk_candidat_bac_option_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`),
	CONSTRAINT `fk_candidat_bac_option_siscol_option_bac_cod_opt_bac` FOREIGN KEY (`cod_opt_bac`) REFERENCES `siscol_option_bac` (`cod_opt_bac`)
)
COMMENT='table des options bacs du candidat'
ENGINE=InnoDB
;