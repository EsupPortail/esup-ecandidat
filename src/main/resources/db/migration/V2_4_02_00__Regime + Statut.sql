CREATE TABLE `siscol_regime` (
	`cod_rgi` VARCHAR(50) NOT NULL COMMENT 'code regime',
	`typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol',
	`lib_rgi` VARCHAR(500) NOT NULL COMMENT 'libellé regime',
	`lic_rgi` VARCHAR(200) NOT NULL COMMENT 'libellé court regime',
	`tem_en_sve_rgi` BIT(1) NOT NULL COMMENT 'temoin en service regime',
	PRIMARY KEY (`cod_rgi`, `typ_siscol`)
)
COMMENT='Rérérentiel SiScol : Types de régime'
ENGINE=InnoDB;

CREATE TABLE `siscol_statut` (
	`cod_stu` VARCHAR(50) NOT NULL COMMENT 'code statut',
	`typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol',
	`lib_stu` VARCHAR(500) NOT NULL COMMENT 'libellé statut',
	`lic_stu` VARCHAR(200) NOT NULL COMMENT 'libellé court statut',
	`tem_en_sve_stu` BIT(1) NOT NULL COMMENT 'temoin en service statut',
	PRIMARY KEY (`cod_stu`, `typ_siscol`)
)
COMMENT='Rérérentiel SiScol : Types de régime'
ENGINE=InnoDB;

ALTER TABLE `candidat` ADD COLUMN `cod_rgi` VARCHAR(50) NULL DEFAULT NULL COMMENT 'code regime' AFTER `cod_langue`;
ALTER TABLE `candidat` ADD CONSTRAINT `fk_candidat_siscol_regime_cod_rgi` FOREIGN KEY (`cod_rgi`, `typ_siscol`) REFERENCES `siscol_regime` (`cod_rgi`, `typ_siscol`);

ALTER TABLE `candidat` ADD COLUMN `cod_stu` VARCHAR(50) NULL DEFAULT NULL COMMENT 'code statut' AFTER `cod_rgi`;
ALTER TABLE `candidat` ADD CONSTRAINT `fk_candidat_siscol_statut_cod_stu` FOREIGN KEY (`cod_stu`, `typ_siscol`) REFERENCES `siscol_statut` (`cod_stu`, `typ_siscol`);