ALTER TABLE `compte_minima`
	CHANGE COLUMN `mail_perso_cpt_min` `mail_perso_cpt_min` VARCHAR(100) NOT NULL COMMENT 'mail perso du compte à minima',
	CHANGE COLUMN `nom_cpt_min` `nom_cpt_min` VARCHAR(100) NOT NULL COMMENT 'nom du compte à minima',
	CHANGE COLUMN `prenom_cpt_min` `prenom_cpt_min` VARCHAR(100) NOT NULL COMMENT 'prénom du compte à minima';
	
ALTER TABLE `candidat`
	CHANGE COLUMN `nom_pat_candidat` `nom_pat_candidat` VARCHAR(100) NOT NULL COMMENT 'nom patronymique du candidat',
	CHANGE COLUMN `nom_usu_candidat` `nom_usu_candidat` VARCHAR(100) NULL DEFAULT NULL COMMENT 'nom usuel du candidat',
	CHANGE COLUMN `prenom_candidat` `prenom_candidat` VARCHAR(100) NOT NULL COMMENT 'prénom du candidat',
	CHANGE COLUMN `autre_pren_candidat` `autre_pren_candidat` VARCHAR(100) NULL DEFAULT NULL COMMENT 'autre prénom du candidat';
	
ALTER TABLE `candidature`
	ADD COLUMN `cod_rgi` VARCHAR(50) NULL DEFAULT NULL COMMENT 'code régime' AFTER `mnt_charge_cand`,
	ADD CONSTRAINT `fk_candidature_siscol_regime` FOREIGN KEY (`cod_rgi`) REFERENCES `siscol_regime` (`cod_rgi`);