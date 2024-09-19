CREATE TABLE `siscol_commune_naiss` (
	`cod_com_naiss` VARCHAR(50) NOT NULL COMMENT 'Code INSEE Commune',
	`typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol',
	`lib_com_naiss` VARCHAR(500) NOT NULL COMMENT 'libelle Long Commune',
	`cod_dep` VARCHAR(50) NOT NULL COMMENT 'Code Departement',
	`tem_en_sve_com_naiss` BIT(1) NOT NULL COMMENT 'Temoin en Service',
	PRIMARY KEY (`cod_com_naiss`, `typ_siscol`),
	CONSTRAINT `fk_siscol_commune_naiss_cod_dep` FOREIGN KEY (`cod_dep`, `typ_siscol`) REFERENCES `siscol_departement` (`cod_dep`, `typ_siscol`)
)
COMMENT='Rérérentiel SiScol : Table des communes de naissance'
ENGINE=InnoDB;

ALTER TABLE `candidat` ADD COLUMN `cod_com_naiss_candidat` VARCHAR(50) NULL DEFAULT NULL COMMENT 'code commune ville de naissance du candidat' AFTER `cod_dep_naiss_candidat`;
ALTER TABLE `candidat` ADD CONSTRAINT `fk_candidat_siscol_com_naiss_cod_com` FOREIGN KEY (`cod_com_naiss_candidat`, `typ_siscol`) REFERENCES `siscol_commune_naiss` (`cod_com_naiss`, `typ_siscol`);
ALTER TABLE `candidat` CHANGE COLUMN `lib_ville_naiss_candidat` `lib_ville_naiss_candidat` VARCHAR(30) NULL COMMENT 'ville de naissance du candidat';