--
-- Creation d'une table pour la fiabilisation des fichiers
--

--table fichier_fiabilisation
CREATE TABLE `fichier_fiabilisation` (
	`id_fichier_fiab` INT(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de fiabilisation du fichier',
	`id_fichier` INT(10) NOT NULL COMMENT 'identifiant du fichier',
	`id_pj` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du fichier',
	`id_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant de la candidature',
	`id_comm` INT(10) NULL DEFAULT NULL COMMENT 'identifiant de la commission',
	`cod_fichier` VARCHAR(50) NOT NULL COMMENT 'le code du fichier composé de son type de la pièce et de l\'id de la pièce',
	`nom_fichier` VARCHAR(100) NOT NULL COMMENT 'le fichier lui même',
	`file_fichier` VARCHAR(1000) NOT NULL COMMENT 'le libellé du fichier',
	`typ_fichier` CHAR(1) NOT NULL COMMENT 'le type de fichier G = gestionnaire, C=candidat',
	`typ_stockage_fichier` CHAR(1) NOT NULL COMMENT 'type de stockage de fichier-->soit fileSystem soit cmis',
	`auteur_fichier` VARCHAR(50) NOT NULL COMMENT 'l\'auteur du fichier',
	`dat_cre_fichier` DATETIME NOT NULL COMMENT 'date de création',
	`dat_cre_fichier_fiab` DATETIME NOT NULL COMMENT 'date de création de la fiabilisation',
	PRIMARY KEY (`id_fichier_fiab`)
)
COMMENT='table de fiabilisation des fichiers'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;