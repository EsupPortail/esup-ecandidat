-- Ajout de la colonne typ_siscol pour les tables de nomenclature

ALTER TABLE `siscol_annee_uni`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_anu`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_anu`, `typ_siscol`);
	
ALTER TABLE `siscol_bac_oux_equ`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_bac`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_bac`, `typ_siscol`);
	
ALTER TABLE `siscol_centre_gestion`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_cge`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_cge`, `typ_siscol`);

ALTER TABLE `siscol_commune`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_com`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_com`, `typ_siscol`);

ALTER TABLE `siscol_com_bdi`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_bdi`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_com`, `cod_bdi`, `typ_siscol`);
	
ALTER TABLE `siscol_departement`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_dep`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_dep`, `typ_siscol`);
	
ALTER TABLE `siscol_dip_aut_cur`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL COMMENT 'Type de siscol' AFTER `cod_dac`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_dac`, `typ_siscol`);
	
ALTER TABLE `siscol_etablissement`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_etb`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_etb`, `typ_siscol`);

ALTER TABLE `siscol_mention`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_men`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_men`, `typ_siscol`);

ALTER TABLE `siscol_mention_niv_bac`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_mnb`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_mnb`, `typ_siscol`);
	
ALTER TABLE `siscol_pays`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_pay`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_pay`, `typ_siscol`);
	
ALTER TABLE `siscol_typ_diplome`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_tpd_etb`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_tpd_etb`, `typ_siscol`);
	
ALTER TABLE `siscol_typ_resultat`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_tre`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`cod_tre`, `typ_siscol`);
	
ALTER TABLE `siscol_utilisateur`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_uti`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`id_uti`, `typ_siscol`);

-- Ajout de la colonne typ_siscol pour les tables ecandidat

-- Table adresse 
ALTER TABLE `adresse`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_adr`;
	
ALTER TABLE `adresse`
	DROP INDEX `fk_adresse_siscol_pays_cod_pay`,
	DROP INDEX `fk_adresse_siscol_commune_cod_com`,
	DROP FOREIGN KEY `fk_adresse_siscol_commune_cod_com`,
	DROP FOREIGN KEY `fk_adresse_siscol_pays_cod_pay`;
	
ALTER TABLE `adresse` 
	ADD CONSTRAINT `fk_adresse_siscol_commune_cod_com` FOREIGN KEY (`cod_com`, `typ_siscol`) REFERENCES `ecandidattest`.`siscol_commune` (`cod_com`, `typ_siscol`),
	ADD CONSTRAINT `fk_adresse_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`, `typ_siscol`) REFERENCES `ecandidattest`.`siscol_pays` (`cod_pay`, `typ_siscol`);	

-- Table candidat
ALTER TABLE `candidat`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_candidat`;

ALTER TABLE `candidat`
	DROP INDEX `fk_candidat_siscol_pays_cod_pay_nat`,
	DROP INDEX `fk_candidat_siscol_pays_cod_pays_naiss`,
	DROP INDEX `fk_siscol_departement_cod_dep_cod_dep_naiss`,
	DROP FOREIGN KEY `fk_candidat_siscol_pays_cod_pay_nat`,
	DROP FOREIGN KEY `fk_candidat_siscol_pays_cod_pays_naiss`,
	DROP FOREIGN KEY `fk_siscol_departement_cod_dep_cod_dep_naiss`;
	
ALTER TABLE `candidat` 
	ADD CONSTRAINT `fk_candidat_siscol_pays_cod_pay_nat` FOREIGN KEY (`cod_pay_nat`, `typ_siscol`) REFERENCES `ecandidattest`.`siscol_pays` (`cod_pay`, `typ_siscol`),
	ADD CONSTRAINT `fk_candidat_siscol_pays_cod_pays_naiss` FOREIGN KEY (`cod_pay_naiss`, `typ_siscol`) REFERENCES `ecandidattest`.`siscol_pays` (`cod_pay`, `typ_siscol`),
	ADD CONSTRAINT `fk_siscol_departement_cod_dep_cod_dep_naiss` FOREIGN KEY (`cod_dep_naiss_candidat`, `typ_siscol`) REFERENCES `ecandidattest`.`siscol_departement` (`cod_dep`, `typ_siscol`);
	
-- Table candidat_bac_ou_equ --> A faire à partir d'ici!!!!!! 
	
ALTER TABLE `candidat_bac_ou_equ`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_candidat`,
	DROP PRIMARY KEY,
	ADD PRIMARY KEY (`id_candidat`, `typ_siscol`);
	
ALTER TABLE `candidat_cursus_interne`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_cursus_interne`;
	
ALTER TABLE `candidat_cursus_post_bac`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_cursus`;
	
ALTER TABLE `candidature`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_cand`;
	
ALTER TABLE `formation`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_form`;
	
ALTER TABLE `gestionnaire`
	ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_droit_profil_ind`;
	
-- Nettoyage des tables de nomenclature
ALTER TABLE `siscol_bac_oux_equ` DROP COLUMN `tem_nat_bac`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `cod_tpe_etb` `cod_tpe_etb` VARCHAR(20) NOT NULL DEFAULT '00' COMMENT 'Code Type Etablissement';
ALTER TABLE `siscol_commune`
	DROP COLUMN `cod_dep`,
	DROP INDEX `fk_siscol_departement_commune_cod_dep`,
	DROP FOREIGN KEY `fk_siscol_departement_commune_cod_dep`;
