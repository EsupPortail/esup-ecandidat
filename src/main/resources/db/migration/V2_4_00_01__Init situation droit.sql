ALTER TABLE `candidature`  DROP FOREIGN KEY `fk_candidature_sicol_cat_exo_ext`;
ALTER TABLE `candidature`  DROP INDEX `fk_candidature_sicol_cat_exo_ext`;

ALTER TABLE `siscol_cat_exo_ext` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_cat_exo_ext`;
ALTER TABLE `siscol_cat_exo_ext` DROP PRIMARY KEY;
ALTER TABLE `siscol_cat_exo_ext` ADD PRIMARY KEY (`cod_cat_exo_ext`, `typ_siscol`);

ALTER TABLE `candidature` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `id_cand`;
ALTER TABLE `candidature` ADD CONSTRAINT `fk_candidature_sicol_cat_exo_ext` FOREIGN KEY (`cod_cat_exo_ext`, `typ_siscol`) REFERENCES `siscol_cat_exo_ext` (`cod_cat_exo_ext`, `typ_siscol`);