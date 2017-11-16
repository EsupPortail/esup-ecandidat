--
-- Mise à jour de la structure de la table siscol_bac_oux_equ
--

ALTER TABLE `siscol_bac_oux_equ`
	CHANGE COLUMN `tem_en_sve_bac` `tem_en_sve_bac` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Témoin en service' AFTER `tem_nat_bac`,
	ADD COLUMN `tem_ctrl_ine_bac` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'Temoin de controle de l\'ine' AFTER `tem_en_sve_bac`,
	ADD COLUMN `ann_ctrl_ine_bac` VARCHAR(4) NULL COMMENT 'Annee de controle de l\'ine' AFTER `tem_ctrl_ine_bac`;

--
-- Mise à jour de la structure de la table siscol_etablissement
--
ALTER TABLE `siscol_etablissement`
	ADD COLUMN `cod_tpe_etb` VARCHAR(2) NOT NULL DEFAULT '00' COMMENT 'Code Type Etablissement' AFTER `cod_etb`;