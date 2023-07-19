ALTER TABLE `compte_minima`	ADD COLUMN `tem_reset_pwd_cpt_min` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'témoin pour savoir si le mot de passe doit être modifié à la connexion' AFTER `tem_valid_mail_cpt_min`;
ALTER TABLE `compte_minima` ADD COLUMN `init_pwd_key_cpt_min` VARCHAR(150) NULL DEFAULT NULL COMMENT 'clé d''initialisation du mot de passe' AFTER `typ_gen_cpt_min`;
ALTER TABLE `compte_minima` ADD COLUMN `dat_fin_init_pwd_cpt_min` DATETIME NULL DEFAULT NULL COMMENT 'date de fin d''initialisation du mot de passe' AFTER `init_pwd_key_cpt_min`;
ALTER TABLE `compte_minima` ADD COLUMN `valid_key_cpt_min` VARCHAR(150) NULL DEFAULT NULL COMMENT 'clé de validation du compte' AFTER `typ_gen_cpt_min`;
UPDATE `compte_minima` set `valid_key_cpt_min` = `num_dossier_opi_cpt_min`;
ALTER TABLE `compte_minima` CHANGE COLUMN `valid_key_cpt_min` `valid_key_cpt_min` VARCHAR(150) NOT NULL COMMENT 'clé de validation du compte' AFTER `typ_gen_cpt_min`;

ALTER TABLE `compte_minima`	ADD UNIQUE INDEX `UK_init_pwd_key_cpt_min` (`init_pwd_key_cpt_min`);
ALTER TABLE `compte_minima`	ADD UNIQUE INDEX `UK_valid_key_cpt_min` (`valid_key_cpt_min`);

ALTER TABLE `individu` ADD COLUMN `tes_ind` BIT(1) NULL DEFAULT b'1' COMMENT 'témoin en service des individus' AFTER `mail_ind`;