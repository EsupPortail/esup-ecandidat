-- Alertes pour les commissions
ALTER TABLE `commission` CHANGE COLUMN `mail_alert_comm` `mail_alert_comm` VARCHAR(80) NULL COMMENT 'mail pour les alertes de la commission' AFTER `id_fichier`;
ALTER TABLE `commission` ADD COLUMN `typ_alert_comm` VARCHAR(1) NOT NULL DEFAULT 'M' COMMENT 'type d''alertes des commission' AFTER `id_fichier`;

-- Envoie de mail BCC pour les centres de candidatures
ALTER TABLE `centre_candidature` ADD COLUMN `typ_send_mail_ctr_cand` VARCHAR(1) NOT NULL DEFAULT 'N' COMMENT 'type d''envoie des copies des mails (BCC) des mails envoyés aux candidats' AFTER `tem_send_mail_ctr_cand`;
UPDATE `centre_candidature` set `typ_send_mail_ctr_cand` = 'M' WHERE `tem_send_mail_ctr_cand` = b'1';
ALTER TABLE `centre_candidature` DROP COLUMN `tem_send_mail_ctr_cand`;

-- Commentaire du gestionnaire
ALTER TABLE `gestionnaire` ADD COLUMN `commentaire` VARCHAR(500) NULL DEFAULT NULL COMMENT 'commentaire sur le gestionnaire' AFTER `tem_all_comm_gest`;

-- Types de formation
CREATE TABLE `type_formation` (
	`id_typ_form` INT(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du type de formation',
	`cod_typ_form` VARCHAR(20) NOT NULL COMMENT 'code du type de formation',
	`lib_typ_form` VARCHAR(50) NOT NULL COMMENT 'libellé du type de formation',
	`tes_typ_form` BIT(1) NOT NULL COMMENT 'temoin en service du type de formation',
	`dat_cre_typ_form` DATETIME NOT NULL COMMENT 'date de création',
	`user_cre_typ_form` VARCHAR(50) NOT NULL COMMENT 'user de création',
	`dat_mod_typ_form` DATETIME NOT NULL COMMENT 'date de modification',
	`user_mod_typ_form` VARCHAR(50) NOT NULL COMMENT 'user de modification',
	PRIMARY KEY (`id_typ_form`),
	UNIQUE INDEX `cod_typ_form` (`cod_typ_form`)
)
COMMENT='table des types de formation'
ENGINE=InnoDB;

ALTER TABLE `formation`	ADD COLUMN `id_typ_form` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du type de formation' AFTER `cod_tpd_etb`;
ALTER TABLE `formation`	ADD CONSTRAINT `fk_formation_type_formation_id_typ_form` FOREIGN KEY (`id_typ_form`) REFERENCES `type_formation` (`id_typ_form`);
ALTER TABLE `formation` CHANGE COLUMN `cod_tpd_etb` `cod_tpd_etb` VARCHAR(50) NULL COMMENT 'type de diplome associé' AFTER `mot_cle_form`;

-- Questions
CREATE TABLE `question` (
	`id_question` INT(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la question',
	`typ_question` VARCHAR(1) NOT NULL COMMENT 'type de la question',
	`cod_question` VARCHAR(20) NOT NULL COMMENT 'code de la question',
	`lib_question` VARCHAR(50) NOT NULL COMMENT 'libellé de la question',
	`id_i18n_lib_question` INT(10) NOT NULL COMMENT 'identifiant i18n du libellé de la question',
	`tes_question` BIT(1) NOT NULL COMMENT 'temoin en service de la question',
	`tem_conditionnel_question` BIT(1) NOT NULL COMMENT 'temoin conditionnelle de la question',
	`tem_commun_question` BIT(1) NOT NULL COMMENT 'temoin commun a toute les formation de la question',
	`id_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du centre de candidature',
	`dat_cre_question` DATETIME NOT NULL COMMENT 'date de création',
	`user_cre_question` VARCHAR(50) NOT NULL COMMENT 'user de création',
	`dat_mod_question` DATETIME NOT NULL COMMENT 'date de modification',
	`user_mod_question` VARCHAR(50) NOT NULL COMMENT 'user de modification',
	PRIMARY KEY (`id_question`),
	UNIQUE INDEX `cod_question` (`cod_question`),
	INDEX `fk_i18n_question_id18n_lib` (`id_i18n_lib_question`),
	INDEX `fk_centre_candidature_question_id_ctr_cand` (`id_ctr_cand`),
	CONSTRAINT `fk_centre_candidature_question_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`),
	CONSTRAINT `fk_i18n_question_id18n_lib` FOREIGN KEY (`id_i18n_lib_question`) REFERENCES `i18n` (`id_i18n`)
)
COMMENT='table des questions'
ENGINE=InnoDB;

CREATE TABLE `question_form` (
	`id_question` INT(10) NOT NULL COMMENT 'identifiant de la question',
	`id_form` INT(10) NOT NULL COMMENT 'identifiant de la formation',
	PRIMARY KEY (`id_question`, `id_form`),
	INDEX `fk_question_form_question_id_question` (`id_question`),
	INDEX `fk_question_form_formation_id_form` (`id_form`),
	CONSTRAINT `fk_question_form_formation_id_form` FOREIGN KEY (`id_form`) REFERENCES `formation` (`id_form`),
	CONSTRAINT `fk_question_form_question_id_question` FOREIGN KEY (`id_question`) REFERENCES `question` (`id_question`)
)
COMMENT='table de jointures questions-formations'
ENGINE=InnoDB;

CREATE TABLE `question_cand` (
	`id_cand` INT(10) NOT NULL COMMENT 'id de la candidature',
	`id_question` INT(10) NOT NULL COMMENT 'id de la question',
	`reponse_question_cand` VARCHAR(1000) NULL COMMENT 'les réponses à la question',
	`dat_cre_question_cand` DATETIME NOT NULL COMMENT 'date de création de la réponse',
	`dat_mod_question_cand` DATETIME NOT NULL COMMENT 'date de modification de la réponse',
	PRIMARY KEY (`id_cand`, `id_question`),
	INDEX `fk_candidature_question_question_id_question` (`id_question`),
	INDEX `fk_candidature_question_cand_id_cand` (`id_cand`),
	CONSTRAINT `fk_candidature_question_cand_id_cand` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`),
	CONSTRAINT `fk_candidature_question_question_id_question` FOREIGN KEY (`id_question`) REFERENCES `question` (`id_question`)
)
COMMENT='table des réponses aux questions'
ENGINE=InnoDB;