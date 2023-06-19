ALTER TABLE `compte_minima` ADD COLUMN `init_pwd_key_cpt_min` VARCHAR(150) NULL DEFAULT NULL COMMENT 'clé d''initialisation du mot de passe' AFTER `typ_gen_cpt_min`;
ALTER TABLE `compte_minima` ADD COLUMN `dat_fin_init_pwd_cpt_min` DATETIME NULL DEFAULT NULL COMMENT 'date de fin d''initialisation du mot de passe' AFTER `init_pwd_key_cpt_min`;

ALTER TABLE `compte_minima`	ADD UNIQUE INDEX `init_pwd_key_cpt_min` (`init_pwd_key_cpt_min`);