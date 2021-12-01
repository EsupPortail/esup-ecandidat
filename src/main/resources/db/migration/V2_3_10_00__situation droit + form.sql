CREATE TABLE `siscol_cat_exo_ext` (
	`cod_cat_exo_ext` VARCHAR(50) NOT NULL COMMENT 'Code catégorie exonération/extracommunautaire',
	`lic_cat_exo_ext` VARCHAR(200) NOT NULL COMMENT 'Libelle Court catégorie exonération/extracommunautaire',
	`lib_cat_exo_ext` VARCHAR(500) NOT NULL COMMENT 'Libelle Long catégorie exonération/extracommunautaire',
	`tem_en_sve_cat_exo_ext` BIT(1) NOT NULL COMMENT 'Temoin en Service',
	PRIMARY KEY (`cod_cat_exo_ext`)
)
COMMENT='Rérérentiel SiScol : catégorie exo./extracommunautaire'
ENGINE=InnoDB;

ALTER TABLE `candidature` ADD COLUMN `cod_cat_exo_ext` VARCHAR(50) NULL DEFAULT NULL COMMENT 'code catégorie exonération/extracommunautaire' AFTER `dat_analyse_form`;
ALTER TABLE `candidature` ADD CONSTRAINT `fk_candidature_sicol_cat_exo_ext` FOREIGN KEY (`cod_cat_exo_ext`) REFERENCES `siscol_cat_exo_ext` (`cod_cat_exo_ext`);


CREATE TABLE `formulaire_candidature` (
	`id_cand` INT(10) NOT NULL COMMENT 'id du candidat',
	`id_formulaire_limesurvey` INT(11) NOT NULL COMMENT 'id du formulaire limesurvey',
	`reponses_formulaire_candidat` TEXT NULL DEFAULT NULL COMMENT 'les réponses du formulaire' COLLATE 'latin1_swedish_ci',
	`dat_reponse_formulaire_cand` DATETIME NOT NULL COMMENT 'date de réponse au formulaire',
	`dat_cre_formulaire_cand` DATETIME NOT NULL COMMENT 'date de création de la réponse',
	`dat_mod_formulaire_cand` DATETIME NOT NULL COMMENT 'date de modification de la réponse',
	PRIMARY KEY (`id_cand`, `id_formulaire_limesurvey`) USING BTREE,
	INDEX `fk_candidat_formulaire_candidature_id_cand` (`id_cand`) USING BTREE,
	CONSTRAINT `fk_candidat_formulaire_candidature_id_cand` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`)
)
COMMENT='table des réponses aux formulaires limesurvey pour une candidature'
ENGINE=InnoDB;