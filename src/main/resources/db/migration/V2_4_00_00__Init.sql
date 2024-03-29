-- Suppression des clé étrangeres
ALTER TABLE `adresse` DROP FOREIGN KEY `fk_adresse_siscol_commune_cod_com`;
ALTER TABLE `adresse` DROP FOREIGN KEY `fk_adresse_siscol_pays_cod_pay`;
ALTER TABLE `adresse` DROP INDEX `fk_adresse_siscol_pays_cod_pay`;
ALTER TABLE `adresse` DROP INDEX `fk_adresse_siscol_commune_cod_com`;

ALTER TABLE `candidat` DROP FOREIGN KEY `fk_candidat_siscol_pays_cod_pay_nat`;
ALTER TABLE `candidat` DROP FOREIGN KEY `fk_candidat_siscol_pays_cod_pays_naiss`;
ALTER TABLE `candidat` DROP FOREIGN KEY `fk_siscol_departement_cod_dep_cod_dep_naiss`;
ALTER TABLE `candidat` DROP INDEX `fk_candidat_siscol_pays_cod_pay_nat`;
ALTER TABLE `candidat` DROP INDEX `fk_candidat_siscol_pays_cod_pays_naiss`;
ALTER TABLE `candidat` DROP INDEX `fk_siscol_departement_cod_dep_cod_dep_naiss`;

ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_bac_ou_equ_cod_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_commune_cod_com`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_departement_cod_dep`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_etablissement_cod_etb`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_mention_niv_bac_cod_mnb`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_pays_cod_pay`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_specialite_bac_cod_spe1_bac_ter`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_specialite_bac_cod_spe2_bac_ter`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_specialite_bac_cod_spe_bac_pre`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_option_bac_cod_opt1_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_option_bac_cod_opt2_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_option_bac_cod_opt3_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP FOREIGN KEY `fk_bac_ou_equ_siscol_option_bac_cod_opt4_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_bac_ou_equ_cod_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_commune_cod_com`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_departement_cod_dep`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_etablissement_cod_etb`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_mention_niv_bac_cod_mnb`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_pays_cod_pay`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_specialite_bac_cod_spe1_bac_ter`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_specialite_bac_cod_spe2_bac_ter`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_specialite_bac_cod_spe_bac_pre`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_option_bac_cod_opt1_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_option_bac_cod_opt2_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_option_bac_cod_opt3_bac`;
ALTER TABLE `candidat_bac_ou_equ`  DROP INDEX `fk_bac_ou_equ_siscol_option_bac_cod_opt4_bac`;

ALTER TABLE `candidat_cursus_interne` DROP FOREIGN KEY `fk_siscol_mention_cursus_interne_cod_men`;
ALTER TABLE `candidat_cursus_interne` DROP FOREIGN KEY `fk_siscol_typ_resultat_candidat_cursus_interne_cod_tre`;
ALTER TABLE `candidat_cursus_interne` DROP INDEX `fk_siscol_mention_cursus_interne_cod_men`;
ALTER TABLE `candidat_cursus_interne` DROP INDEX `fk_siscol_typ_resultat_candidat_cursus_interne_cod_tre`;

ALTER TABLE `candidat_cursus_post_bac`  DROP FOREIGN KEY `fk_cursus_siscol_commune_cod_com`;
ALTER TABLE `candidat_cursus_post_bac`  DROP FOREIGN KEY `fk_cursus_siscol_departement_cod_dep`;
ALTER TABLE `candidat_cursus_post_bac`  DROP FOREIGN KEY `fk_cursus_siscol_dip_aut_cur_cod_dac`;
ALTER TABLE `candidat_cursus_post_bac`  DROP FOREIGN KEY `fk_cursus_siscol_etablissement_cod_etb`;
ALTER TABLE `candidat_cursus_post_bac`  DROP FOREIGN KEY `fk_cursus_siscol_mention_cod_men`;
ALTER TABLE `candidat_cursus_post_bac`  DROP FOREIGN KEY `fk_cursus_siscol_pays_cod_pay`;
ALTER TABLE `candidat_cursus_post_bac`  DROP INDEX `fk_cursus_siscol_commune_cod_com`;
ALTER TABLE `candidat_cursus_post_bac`  DROP INDEX `fk_cursus_siscol_departement_cod_dep`;
ALTER TABLE `candidat_cursus_post_bac`  DROP INDEX `fk_cursus_siscol_dip_aut_cur_cod_dac`;
ALTER TABLE `candidat_cursus_post_bac`  DROP INDEX `fk_cursus_siscol_etablissement_cod_etb`;
ALTER TABLE `candidat_cursus_post_bac`  DROP INDEX `fk_cursus_siscol_mention_cod_men`;
ALTER TABLE `candidat_cursus_post_bac`  DROP INDEX `fk_cursus_siscol_pays_cod_pay`;

ALTER TABLE `siscol_bac_opt_bac` DROP FOREIGN KEY `FK_bac_opt_bac_bac_cod_bac`;
ALTER TABLE `siscol_bac_opt_bac`  DROP FOREIGN KEY `FK_bac_opt_bac_opt_bac_cod_opt_bac`;
ALTER TABLE `siscol_bac_opt_bac`  DROP INDEX `FK_bac_opt_bac_opt_bac_cod_opt_bac`;

ALTER TABLE `siscol_bac_spe_bac` DROP FOREIGN KEY `FK_bac_spe_bac_bac_cod_bac`;
ALTER TABLE `siscol_bac_spe_bac`  DROP FOREIGN KEY `FK_bac_spe_bac_spe_bac_cod_spe_bac`;
ALTER TABLE `siscol_bac_spe_bac`  DROP INDEX `FK_bac_spe_bac_spe_bac_cod_spe_bac`;

ALTER TABLE `formation` DROP FOREIGN KEY `fk_formation_siscol_centre_gestion_cod_cge`;
ALTER TABLE `formation` DROP FOREIGN KEY `fk_formation_siscol_typ_diplome_cod_tpd_etb`;
ALTER TABLE `formation` DROP INDEX `fk_formation_siscol_centre_gestion_cod_cge`;
ALTER TABLE `formation` DROP INDEX `fk_formation_siscol_typ_diplome_cod_tpd_etb`;

ALTER TABLE `gestionnaire` DROP FOREIGN KEY `fk_gestionnaire_cge_cod_cge`;
ALTER TABLE `gestionnaire` DROP INDEX `fk_gestionnaire_cge_cod_cge`;

ALTER TABLE `siscol_commune` DROP FOREIGN KEY `fk_siscol_departement_commune_cod_dep`;
ALTER TABLE `siscol_commune` DROP INDEX `fk_siscol_departement_commune_cod_dep`;

ALTER TABLE `siscol_etablissement` DROP FOREIGN KEY `fk_siscol_commune_siscol_etab_cod_com`;
ALTER TABLE `siscol_etablissement` DROP FOREIGN KEY `fk_siscol_departement_etab_cod_dep`;
ALTER TABLE `siscol_etablissement` DROP INDEX `fk_siscol_commune_siscol_etab_cod_com`;
ALTER TABLE `siscol_etablissement` DROP INDEX `fk_siscol_departement_etab_cod_dep`;

ALTER TABLE `siscol_utilisateur` DROP FOREIGN KEY `fk_siscol_centre_gestion_utilisateur_cod_cge`;
ALTER TABLE `siscol_utilisateur` DROP INDEX `fk_siscol_centre_gestion_utilisateur_cod_cge`;

-- Ajout de la colonne typ_siscol pour les tables de nomenclature
ALTER TABLE `siscol_annee_uni` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_anu`;
ALTER TABLE `siscol_annee_uni` DROP PRIMARY KEY;
ALTER TABLE `siscol_annee_uni` ADD PRIMARY KEY (`cod_anu`, `typ_siscol`);
ALTER TABLE `siscol_annee_uni` CHANGE COLUMN `cod_anu` `cod_anu` VARCHAR(50) NOT NULL COMMENT 'Code Annee Universitaire' FIRST;
ALTER TABLE `siscol_annee_uni` CHANGE COLUMN `lib_anu` `lib_anu` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Annee Universitaire' AFTER `eta_anu_iae`;
ALTER TABLE `siscol_annee_uni` CHANGE COLUMN `lic_anu` `lic_anu` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Annee Universitaire' AFTER `lib_anu`;

ALTER TABLE `siscol_bac_oux_equ` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_bac`;
ALTER TABLE `siscol_bac_oux_equ` DROP PRIMARY KEY;
ALTER TABLE `siscol_bac_oux_equ` ADD PRIMARY KEY (`cod_bac`, `typ_siscol`);
ALTER TABLE `siscol_bac_oux_equ` CHANGE COLUMN `cod_bac` `cod_bac` VARCHAR(50) NOT NULL COMMENT 'Code Baccalaureat ou Equivalence' FIRST;
ALTER TABLE `siscol_bac_oux_equ` CHANGE COLUMN `lib_bac` `lib_bac` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Baccalaureat ou Equivalence' AFTER `typ_siscol`;
ALTER TABLE `siscol_bac_oux_equ` CHANGE COLUMN `lic_bac` `lic_bac` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Baccalaureat ou Equivalence' AFTER `lib_bac`;
	
ALTER TABLE `siscol_centre_gestion` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_cge`;
ALTER TABLE `siscol_centre_gestion` DROP PRIMARY KEY;
ALTER TABLE `siscol_centre_gestion` ADD PRIMARY KEY (`cod_cge`, `typ_siscol`);
ALTER TABLE `siscol_centre_gestion` CHANGE COLUMN `cod_cge` `cod_cge` VARCHAR(50) NOT NULL COMMENT 'Code Centre de Gestion' FIRST;
ALTER TABLE `siscol_centre_gestion` CHANGE COLUMN `lib_cge` `lib_cge` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Centre de Gestion' AFTER `cod_cge`;
ALTER TABLE `siscol_centre_gestion` CHANGE COLUMN `lic_cge` `lic_cge` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Centre de Gestion' AFTER `lib_cge`;

ALTER TABLE `siscol_commune` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_com`;
ALTER TABLE `siscol_commune` DROP PRIMARY KEY;
ALTER TABLE `siscol_commune` ADD PRIMARY KEY (`cod_com`, `typ_siscol`);
ALTER TABLE `siscol_commune` CHANGE COLUMN `cod_com` `cod_com` VARCHAR(50) NOT NULL COMMENT 'Code INSEE Commune' FIRST;
ALTER TABLE `siscol_commune` CHANGE COLUMN `lib_com` `lib_com` VARCHAR(500) NOT NULL COMMENT 'libelle Long Commune' AFTER `typ_siscol`;
ALTER TABLE `siscol_commune` CHANGE COLUMN `cod_dep` `cod_dep` VARCHAR(50) NOT NULL COMMENT 'Code Departement' AFTER `cod_com`;

ALTER TABLE `siscol_com_bdi` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_bdi`;
ALTER TABLE `siscol_com_bdi` DROP PRIMARY KEY;
ALTER TABLE `siscol_com_bdi` ADD PRIMARY KEY (`cod_com`, `cod_bdi`, `typ_siscol`);
ALTER TABLE `siscol_com_bdi` CHANGE COLUMN `cod_com` `cod_com` VARCHAR(50) NOT NULL COMMENT 'code de la commune' FIRST;
ALTER TABLE `siscol_com_bdi` CHANGE COLUMN `cod_bdi` `cod_bdi` VARCHAR(50) NOT NULL COMMENT 'code postal' AFTER `cod_com`;
	
ALTER TABLE `siscol_departement` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_dep`;
ALTER TABLE `siscol_departement` DROP PRIMARY KEY;
ALTER TABLE `siscol_departement` ADD PRIMARY KEY (`cod_dep`, `typ_siscol`);
ALTER TABLE `siscol_departement` CHANGE COLUMN `cod_dep` `cod_dep` VARCHAR(50) NOT NULL COMMENT 'Code Departement' FIRST;
ALTER TABLE `siscol_departement` CHANGE COLUMN `lib_dep` `lib_dep` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Departement' AFTER `cod_dep`;
ALTER TABLE `siscol_departement` CHANGE COLUMN `lic_dep` `lic_dep` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Departement' AFTER `lib_dep`;
	
ALTER TABLE `siscol_dip_aut_cur` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_dac`;
ALTER TABLE `siscol_dip_aut_cur` DROP PRIMARY KEY;
ALTER TABLE `siscol_dip_aut_cur` ADD PRIMARY KEY (`cod_dac`, `typ_siscol`);
ALTER TABLE `siscol_dip_aut_cur` CHANGE COLUMN `cod_dac` `cod_dac` VARCHAR(50) NOT NULL COMMENT 'Code Diplome Autre Cursus' FIRST;
ALTER TABLE `siscol_dip_aut_cur` CHANGE COLUMN `lib_dac` `lib_dac` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Diplome Autre Cursus' AFTER `cod_dac`;
ALTER TABLE `siscol_dip_aut_cur` CHANGE COLUMN `lic_dac` `lic_dac` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Diplome Autre Cursus' AFTER `lib_dac`;
	
ALTER TABLE `siscol_etablissement` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_etb`;
ALTER TABLE `siscol_etablissement` DROP PRIMARY KEY;
ALTER TABLE `siscol_etablissement` ADD PRIMARY KEY (`cod_etb`, `typ_siscol`);
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `cod_etb` `cod_etb` VARCHAR(50) NOT NULL COMMENT 'Code National de l"Etablissement' FIRST;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `cod_dep` `cod_dep` VARCHAR(50) NOT NULL COMMENT 'Code Departement' AFTER `cod_tpe_etb`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `cod_com` `cod_com` VARCHAR(50) NULL COMMENT 'code commune' AFTER `cod_dep`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `lib_etb` `lib_etb` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Etablissement' AFTER `cod_com`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `lic_etb` `lic_etb` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Etablissement' AFTER `lib_etb`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `lib_web_etb` `lib_web_etb` VARCHAR(500) NULL COMMENT 'Libellé Web' AFTER `tem_en_sve_etb`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `cod_tpe_etb` `cod_tpe_etb` VARCHAR(50) NOT NULL DEFAULT '00' COMMENT 'Code Type Etablissement' AFTER `cod_etb`;

ALTER TABLE `siscol_mention` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_men`;
ALTER TABLE `siscol_mention` DROP PRIMARY KEY;
ALTER TABLE `siscol_mention` ADD PRIMARY KEY (`cod_men`, `typ_siscol`);
ALTER TABLE `siscol_mention` CHANGE COLUMN `cod_men` `cod_men` VARCHAR(50) NOT NULL COMMENT 'Code mention' FIRST;
ALTER TABLE `siscol_mention` CHANGE COLUMN `lic_men` `lic_men` VARCHAR(200) NOT NULL COMMENT 'Libelle court mention' AFTER `cod_men`;
ALTER TABLE `siscol_mention` CHANGE COLUMN `lib_men` `lib_men` VARCHAR(500) NOT NULL COMMENT 'Libelle long mention' AFTER `lic_men`;

ALTER TABLE `siscol_mention_niv_bac`ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_mnb`;
ALTER TABLE `siscol_mention_niv_bac`DROP PRIMARY KEY;
ALTER TABLE `siscol_mention_niv_bac`ADD PRIMARY KEY (`cod_mnb`, `typ_siscol`);
ALTER TABLE `siscol_mention_niv_bac`CHANGE COLUMN `cod_mnb` `cod_mnb` VARCHAR(50) NOT NULL COMMENT 'Code Mention Niveau Bac' FIRST;
ALTER TABLE `siscol_mention_niv_bac`CHANGE COLUMN `lib_mnb` `lib_mnb` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Mention Niveau Bac' AFTER `cod_mnb`;
ALTER TABLE `siscol_mention_niv_bac`CHANGE COLUMN `lic_mnb` `lic_mnb` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Mention Niveau Bac' AFTER `lib_mnb`;

ALTER TABLE `siscol_pays` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_pay`;
ALTER TABLE `siscol_pays` DROP PRIMARY KEY;
ALTER TABLE `siscol_pays` ADD PRIMARY KEY (`cod_pay`, `typ_siscol`);
ALTER TABLE `siscol_pays` CHANGE COLUMN `cod_pay` `cod_pay` VARCHAR(50) NOT NULL COMMENT 'Code Pays INSEE' FIRST;
ALTER TABLE `siscol_pays` CHANGE COLUMN `lib_pay` `lib_pay` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Pays' AFTER `cod_pay`;
ALTER TABLE `siscol_pays` CHANGE COLUMN `lic_pay` `lic_pay` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Pays' AFTER `lib_pay`;
ALTER TABLE `siscol_pays` CHANGE COLUMN `lib_nat` `lib_nat` VARCHAR(500) NOT NULL COMMENT 'Libelle Nationalite' AFTER `lic_pay`;

ALTER TABLE `siscol_typ_diplome` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_tpd_etb`;
ALTER TABLE `siscol_typ_diplome` DROP PRIMARY KEY;
ALTER TABLE `siscol_typ_diplome` ADD PRIMARY KEY (`cod_tpd_etb`, `typ_siscol`);
ALTER TABLE `siscol_typ_diplome` CHANGE COLUMN `cod_tpd_etb` `cod_tpd_etb` VARCHAR(50) NOT NULL COMMENT 'Code Type Diplome Etablissement' FIRST;
ALTER TABLE `siscol_typ_diplome` CHANGE COLUMN `lib_tpd` `lib_tpd` VARCHAR(500) NOT NULL COMMENT 'Libelle Long Type Diplome SISE' AFTER `cod_tpd_etb`;
ALTER TABLE `siscol_typ_diplome` CHANGE COLUMN `lic_tpd` `lic_tpd` VARCHAR(200) NOT NULL COMMENT 'Libelle Court Type Diplome SISE' AFTER `lib_tpd`;

ALTER TABLE `siscol_typ_resultat` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_tre`;
ALTER TABLE `siscol_typ_resultat` DROP PRIMARY KEY;
ALTER TABLE `siscol_typ_resultat` ADD PRIMARY KEY (`cod_tre`, `typ_siscol`);
ALTER TABLE `siscol_typ_resultat` CHANGE COLUMN `cod_tre` `cod_tre` VARCHAR(50) NOT NULL COMMENT 'code type resultat' FIRST;
ALTER TABLE `siscol_typ_resultat` CHANGE COLUMN `lib_tre` `lib_tre` VARCHAR(500) NOT NULL COMMENT 'libellé type resultat' AFTER `cod_tre`;
ALTER TABLE `siscol_typ_resultat` CHANGE COLUMN `lic_tre` `lic_tre` VARCHAR(200) NOT NULL COMMENT 'libellé court type resultat' AFTER `lib_tre`;
	
ALTER TABLE `siscol_utilisateur` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_uti`;
ALTER TABLE `siscol_utilisateur` DROP PRIMARY KEY;
ALTER TABLE `siscol_utilisateur` ADD PRIMARY KEY (`id_uti`, `typ_siscol`);

-- Ajout foreign key pour les tables siscol
ALTER TABLE `siscol_etablissement` ADD CONSTRAINT `fk_siscol_commune_siscol_etab_cod_com` FOREIGN KEY (`cod_com`, `typ_siscol`) REFERENCES `siscol_commune` (`cod_com`, `typ_siscol`);
ALTER TABLE `siscol_etablissement` ADD CONSTRAINT `fk_siscol_departement_etab_cod_dep` FOREIGN KEY (`cod_dep`, `typ_siscol`) REFERENCES `siscol_departement` (`cod_dep`, `typ_siscol`);
ALTER TABLE `siscol_utilisateur` ADD CONSTRAINT `fk_siscol_centre_gestion_utilisateur_cod_cge` FOREIGN KEY (`cod_cge`, `typ_siscol`) REFERENCES `siscol_centre_gestion` (`cod_cge`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_specialite_bac
ALTER TABLE `siscol_specialite_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_spe_bac`;
ALTER TABLE `siscol_specialite_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_specialite_bac` ADD PRIMARY KEY (`cod_spe_bac`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_option_bac
ALTER TABLE `siscol_option_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_opt_bac`;
ALTER TABLE `siscol_option_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_option_bac` ADD PRIMARY KEY (`cod_opt_bac`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_bac_spe_bac
ALTER TABLE `siscol_bac_spe_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_spe_bac`;
ALTER TABLE `siscol_bac_spe_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_bac_spe_bac` ADD PRIMARY KEY (`cod_bac`, `cod_spe_bac`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_bac_opt_bac
ALTER TABLE `siscol_bac_opt_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_opt_bac`;
ALTER TABLE `siscol_bac_opt_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_bac_opt_bac` ADD PRIMARY KEY (`cod_bac`, `cod_opt_bac`, `typ_siscol`);

-- Ajout de la colonne typ_siscol pour les tables ecandidat + foreign key

-- Table adresse 
ALTER TABLE `adresse` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_adr`;
ALTER TABLE `adresse` CHANGE COLUMN `cod_pay` `cod_pay` VARCHAR(50) NOT NULL COMMENT 'code du pays de l''adresse' AFTER `id_adr`;
ALTER TABLE `adresse` CHANGE COLUMN `cod_com` `cod_com` VARCHAR(50) NULL COMMENT 'code commune de l''adresse (fr)' AFTER `cod_bdi_adr`;
ALTER TABLE `adresse` ADD CONSTRAINT `fk_adresse_siscol_commune_cod_com` FOREIGN KEY (`cod_com`, `typ_siscol`) REFERENCES `siscol_commune` (`cod_com`, `typ_siscol`);
ALTER TABLE `adresse` ADD CONSTRAINT `fk_adresse_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`, `typ_siscol`) REFERENCES `siscol_pays` (`cod_pay`, `typ_siscol`);	

-- Table candidat
ALTER TABLE `candidat` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_candidat`;
ALTER TABLE `candidat` CHANGE COLUMN `cod_pay_nat` `cod_pay_nat` VARCHAR(50) NOT NULL COMMENT 'nationalité du candidat' AFTER `dat_naiss_candidat`;
ALTER TABLE `candidat` CHANGE COLUMN `cod_pay_naiss` `cod_pay_naiss` VARCHAR(50) NOT NULL COMMENT 'code Apogee pays de naissance du candidat' AFTER `cle_ine_candidat`;
ALTER TABLE `candidat` CHANGE COLUMN `cod_dep_naiss_candidat` `cod_dep_naiss_candidat` VARCHAR(50) NULL COMMENT 'code Apogee departement de naissance candidat' AFTER `cod_pay_naiss`;
ALTER TABLE `candidat` ADD CONSTRAINT `fk_candidat_siscol_pays_cod_pay_nat` FOREIGN KEY (`cod_pay_nat`, `typ_siscol`) REFERENCES `siscol_pays` (`cod_pay`, `typ_siscol`);
ALTER TABLE `candidat` ADD CONSTRAINT `fk_candidat_siscol_pays_cod_pays_naiss` FOREIGN KEY (`cod_pay_naiss`, `typ_siscol`) REFERENCES `siscol_pays` (`cod_pay`, `typ_siscol`);
ALTER TABLE `candidat` ADD CONSTRAINT `fk_siscol_departement_cod_dep_cod_dep_naiss` FOREIGN KEY (`cod_dep_naiss_candidat`, `typ_siscol`) REFERENCES `siscol_departement` (`cod_dep`, `typ_siscol`);
	
-- Table candidat_bac_ou_equ
ALTER TABLE `candidat_bac_ou_equ` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_candidat`;
ALTER TABLE `candidat_bac_ou_equ`  CHANGE COLUMN `cod_pay` `cod_pay` VARCHAR(50) NULL COMMENT 'code du pays' AFTER `id_candidat`;
ALTER TABLE `candidat_bac_ou_equ`  CHANGE COLUMN `cod_dep` `cod_dep` VARCHAR(50) NULL COMMENT 'code du departement' AFTER `cod_pay`;
ALTER TABLE `candidat_bac_ou_equ`  CHANGE COLUMN `cod_com` `cod_com` VARCHAR(50) NULL COMMENT 'code de la commune' AFTER `cod_dep`;
ALTER TABLE `candidat_bac_ou_equ`  CHANGE COLUMN `cod_etb` `cod_etb` VARCHAR(50) NULL COMMENT 'code de l''etablissement' AFTER `cod_com`;
ALTER TABLE `candidat_bac_ou_equ`  CHANGE COLUMN `cod_bac` `cod_bac` VARCHAR(50) NOT NULL COMMENT 'code du bac' AFTER `annee_obt_bac`;
ALTER TABLE `candidat_bac_ou_equ`  CHANGE COLUMN `cod_mnb` `cod_mnb` VARCHAR(50) NULL COMMENT 'code de la mention' AFTER `cod_bac`;
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_bac_ou_equ_cod_bac` FOREIGN KEY (`cod_bac`, `typ_siscol`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_commune_cod_com` FOREIGN KEY (`cod_com`, `typ_siscol`) REFERENCES `siscol_commune` (`cod_com`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_departement_cod_dep` FOREIGN KEY (`cod_dep`, `typ_siscol`) REFERENCES `siscol_departement` (`cod_dep`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_etablissement_cod_etb` FOREIGN KEY (`cod_etb`, `typ_siscol`) REFERENCES `siscol_etablissement` (`cod_etb`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_mention_niv_bac_cod_mnb` FOREIGN KEY (`cod_mnb`, `typ_siscol`) REFERENCES `siscol_mention_niv_bac` (`cod_mnb`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`, `typ_siscol`) REFERENCES `siscol_pays` (`cod_pay`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe1_bac_ter` FOREIGN KEY (`cod_spe1_bac_ter`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe2_bac_ter` FOREIGN KEY (`cod_spe2_bac_ter`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe_bac_pre` FOREIGN KEY (`cod_spe_bac_pre`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt1_bac` FOREIGN KEY (`cod_opt1_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt2_bac` FOREIGN KEY (`cod_opt2_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt3_bac` FOREIGN KEY (`cod_opt3_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt4_bac` FOREIGN KEY (`cod_opt4_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);

-- Ajout des clé étrangeres de siscol_bac_spe_bac
ALTER TABLE `siscol_bac_spe_bac` ADD CONSTRAINT `FK_bac_spe_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`, `typ_siscol`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`, `typ_siscol`);
ALTER TABLE `siscol_bac_spe_bac` ADD CONSTRAINT `FK_bac_spe_bac_spe_bac_cod_spe_bac` FOREIGN KEY (`cod_spe_bac`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);

-- Ajout des clé étrangeres de siscol_bac_opt
ALTER TABLE `siscol_bac_opt_bac` ADD CONSTRAINT `FK_bac_opt_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`, `typ_siscol`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`, `typ_siscol`);
ALTER TABLE `siscol_bac_opt_bac` ADD CONSTRAINT `FK_bac_opt_bac_opt_bac_cod_opt_bac` FOREIGN KEY (`cod_opt_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);

-- Table candidat_cursus_interne
ALTER TABLE `candidat_cursus_interne` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_candidat`;
ALTER TABLE `candidat_cursus_interne` CHANGE COLUMN `cod_men_cursus_interne` `cod_men_cursus_interne` VARCHAR(50) NULL COMMENT 'code de la mention du cursus interne' AFTER `lib_cursus_interne`;
ALTER TABLE `candidat_cursus_interne` CHANGE COLUMN `cod_tre_cursus_interne` `cod_tre_cursus_interne` VARCHAR(50) NULL COMMENT 'temoin d''obention du cursus interne' AFTER `cod_men_cursus_interne`;
ALTER TABLE `candidat_cursus_interne` ADD CONSTRAINT `fk_siscol_mention_cursus_interne_cod_men` FOREIGN KEY (`cod_men_cursus_interne`, `typ_siscol`) REFERENCES `siscol_mention` (`cod_men`, `typ_siscol`);
ALTER TABLE `candidat_cursus_interne` ADD CONSTRAINT `fk_siscol_typ_resultat_candidat_cursus_interne_cod_tre` FOREIGN KEY (`cod_tre_cursus_interne`, `typ_siscol`) REFERENCES `siscol_typ_resultat` (`cod_tre`, `typ_siscol`);
	
-- Table candidat_cursus_post_bac
ALTER TABLE `candidat_cursus_post_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_candidat`;
ALTER TABLE `candidat_cursus_post_bac`  CHANGE COLUMN `cod_pay` `cod_pay` VARCHAR(50) NOT NULL COMMENT 'code du pays' AFTER `id_candidat`;
ALTER TABLE `candidat_cursus_post_bac`  CHANGE COLUMN `cod_dep` `cod_dep` VARCHAR(50) NULL COMMENT 'code du departement' AFTER `cod_pay`;
ALTER TABLE `candidat_cursus_post_bac`  CHANGE COLUMN `cod_com` `cod_com` VARCHAR(50) NULL COMMENT 'code de la commune' AFTER `cod_dep`;
ALTER TABLE `candidat_cursus_post_bac`  CHANGE COLUMN `cod_etb` `cod_etb` VARCHAR(50) NULL COMMENT 'code de l''etablissement' AFTER `cod_com`;
ALTER TABLE `candidat_cursus_post_bac`  CHANGE COLUMN `cod_dac` `cod_dac` VARCHAR(50) NOT NULL COMMENT 'code de diplome' AFTER `annee_univ_cursus`;
ALTER TABLE `candidat_cursus_post_bac`  CHANGE COLUMN `cod_men` `cod_men` VARCHAR(50) NULL COMMENT 'code de mention' AFTER `cod_dac`;
ALTER TABLE `candidat_cursus_post_bac`  ADD CONSTRAINT `fk_cursus_siscol_commune_cod_com` FOREIGN KEY (`cod_com`, `typ_siscol`) REFERENCES `siscol_commune` (`cod_com`, `typ_siscol`);
ALTER TABLE `candidat_cursus_post_bac`  ADD CONSTRAINT `fk_cursus_siscol_departement_cod_dep` FOREIGN KEY (`cod_dep`, `typ_siscol`) REFERENCES `siscol_departement` (`cod_dep`, `typ_siscol`);
ALTER TABLE `candidat_cursus_post_bac`  ADD CONSTRAINT `fk_cursus_siscol_dip_aut_cur_cod_dac` FOREIGN KEY (`cod_dac`, `typ_siscol`) REFERENCES `siscol_dip_aut_cur` (`cod_dac`, `typ_siscol`);
ALTER TABLE `candidat_cursus_post_bac`  ADD CONSTRAINT `fk_cursus_siscol_etablissement_cod_etb` FOREIGN KEY (`cod_etb`, `typ_siscol`) REFERENCES `siscol_etablissement` (`cod_etb`, `typ_siscol`);
ALTER TABLE `candidat_cursus_post_bac`  ADD CONSTRAINT `fk_cursus_siscol_mention_cod_men` FOREIGN KEY (`cod_men`, `typ_siscol`) REFERENCES `siscol_mention` (`cod_men`, `typ_siscol`);
ALTER TABLE `candidat_cursus_post_bac`  ADD CONSTRAINT `fk_cursus_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`, `typ_siscol`) REFERENCES `siscol_pays` (`cod_pay`, `typ_siscol`);
	
-- Table formation
ALTER TABLE `formation` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_form`;
ALTER TABLE `formation` CHANGE COLUMN `cod_tpd_etb` `cod_tpd_etb` VARCHAR(50) NOT NULL COMMENT 'type de diplome associé' AFTER `mot_cle_form`;
ALTER TABLE `formation` ADD COLUMN `cod_pegase_form` VARCHAR(50) NULL COMMENT 'code pegase de la formation' AFTER `lib_dip_apo_form`;
ALTER TABLE `formation` ADD COLUMN `lib_pegase_form` VARCHAR(500) NULL COMMENT 'libellé pegase de la formation' AFTER `cod_pegase_form`;
ALTER TABLE `formation` CHANGE COLUMN `cod_form` `cod_form` VARCHAR(50) NOT NULL COMMENT 'code eCandidat de la formation' AFTER `cod_typ_trait`;
ALTER TABLE `formation` CHANGE COLUMN `lib_form` `lib_form` VARCHAR(500) NOT NULL COMMENT 'libellé eCandidat de la formation' AFTER `cod_form`;
ALTER TABLE `formation` CHANGE COLUMN `cod_cge` `cod_cge` VARCHAR(50) NOT NULL COMMENT 'code CGE rattaché' AFTER `dat_analyse_form`;
ALTER TABLE `formation` ADD CONSTRAINT `fk_formation_siscol_centre_gestion_cod_cge` FOREIGN KEY (`cod_cge`, `typ_siscol`) REFERENCES `siscol_centre_gestion` (`cod_cge`, `typ_siscol`);
ALTER TABLE `formation` ADD CONSTRAINT `fk_formation_siscol_typ_diplome_cod_tpd_etb` FOREIGN KEY (`cod_tpd_etb`, `typ_siscol`) REFERENCES `siscol_typ_diplome` (`cod_tpd_etb`, `typ_siscol`);
	
-- Table gestionnaire
ALTER TABLE `gestionnaire` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `login_apo_gest`;
ALTER TABLE `gestionnaire` ADD CONSTRAINT `fk_gestionnaire_cge_cod_cge` FOREIGN KEY (`cod_cge`, `typ_siscol`) REFERENCES `siscol_centre_gestion` (`cod_cge`, `typ_siscol`);

-- Nettoyage des tables de nomenclature
ALTER TABLE `siscol_bac_oux_equ` DROP COLUMN `tem_nat_bac`;
ALTER TABLE `siscol_etablissement` CHANGE COLUMN `cod_tpe_etb` `cod_tpe_etb` VARCHAR(20) NOT NULL DEFAULT '00' COMMENT 'Code Type Etablissement';
ALTER TABLE `siscol_commune` DROP COLUMN `cod_dep`;
	
-- Ajout civilité Pegase
ALTER TABLE `civilite` CHANGE COLUMN `cod_apo` `cod_siscol` VARCHAR(3) NOT NULL COMMENT 'code siscol correspondant' AFTER `lib_civ`;
ALTER TABLE `civilite` ADD COLUMN `cod_sexe` VARCHAR(1) NOT NULL COMMENT 'code sexe' AFTER `cod_siscol`;

-- Ajout typSiscol à la campagne
ALTER TABLE `campagne` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_camp`;

-- Modif version
ALTER TABLE `version` CHANGE COLUMN `val_version` `val_version` VARCHAR(100) NOT NULL COMMENT 'valeur de la version' AFTER `cod_version`;

-- Modif formation
ALTER TABLE `formation`	CHANGE COLUMN `cod_cge` `cod_cge` VARCHAR(50) NULL COMMENT 'code CGE rattaché' AFTER `dat_analyse_form`;