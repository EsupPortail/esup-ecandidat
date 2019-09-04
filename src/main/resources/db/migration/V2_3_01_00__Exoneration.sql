CREATE TABLE `siscol_cat_exo_ext` (
	`cod_cat_exo_ext` VARCHAR(2) NOT NULL COMMENT 'Code catégorie exonération/extracommunautaire',
	`lic_cat_exo_ext` VARCHAR(15) NOT NULL COMMENT 'Libelle Court catégorie exonération/extracommunautaire',
	`lib_cat_exo_ext` VARCHAR(65) NOT NULL COMMENT 'Libelle Long catégorie exonération/extracommunautaire',
	`cod_sis_cat_exo_ext` VARCHAR(2) NOT NULL COMMENT 'CodeSis catégorie exonération/extracommunautaire',
	`tem_en_sve_cat_exo_ext` BIT(1) NOT NULL COMMENT 'Temoin en Service',
	PRIMARY KEY (`cod_cat_exo_ext`)
)
COMMENT='Rérérentiel SiScol : catégorie exo./extracommunautaire'
ENGINE=InnoDB;

ALTER TABLE `candidature`
	ADD COLUMN `cod_cat_exo_ext` VARCHAR(2) NULL DEFAULT NULL COMMENT 'code catégorie exonération/extracommunautaire' AFTER `dat_analyse_form`,
	ADD COLUMN `cmt_cat_exo_ext_cand` VARCHAR(15) NULL DEFAULT NULL COMMENT 'commentaire exonération/extracommunautaire' AFTER `cod_cat_exo_ext`,
	ADD COLUMN `mnt_charge_cand` DECIMAL(10,2) NULL DEFAULT NULL COMMENT 'montant restant à charge' AFTER `cmt_cat_exo_ext_cand`;
	
ALTER TABLE `candidature`
	ADD CONSTRAINT `fk_candidature_sicol_cat_exo_ext` FOREIGN KEY (`cod_cat_exo_ext`) REFERENCES `siscol_cat_exo_ext` (`cod_cat_exo_ext`);