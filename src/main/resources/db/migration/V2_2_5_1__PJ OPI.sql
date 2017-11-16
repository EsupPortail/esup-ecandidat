--
-- Creation table tag
--
CREATE TABLE `pj_opi` (
	`cod_apo_pj` VARCHAR(5) NOT NULL COMMENT 'Code Apogée de la pièce',
	`cod_opi` VARCHAR(10) NOT NULL COMMENT 'Code de l\'opi',
	`id_candidat` INT(10) NOT NULL COMMENT 'Identifiant du candidat',
	`id_fichier` INT(10) NOT NULL COMMENT 'Identifiant du fichier (non clé étrangère car le fichier peut disparaitre)',
	`dat_deversement` DATETIME NULL DEFAULT NULL COMMENT 'Date de déversement de la pièce',
	PRIMARY KEY (`cod_apo_pj`, `cod_opi`),
	INDEX `fk_candidat_opi_pj_id_candidat` (`id_candidat`),
	CONSTRAINT `fk_candidat_opi_pj_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`)
)
COMMENT='table des PJ OPI à déverser dans Apogée'
ENGINE=InnoDB
;
