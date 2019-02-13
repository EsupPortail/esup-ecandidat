ALTER TABLE `piece_justif`
	ADD COLUMN `cod_typ_trait` VARCHAR(2) NULL DEFAULT NULL COMMENT 'code du type de traitement' AFTER `cod_apo_pj`,
	ADD CONSTRAINT `fk_piece_justif_type_traitement_cod_typ_trait` FOREIGN KEY (`cod_typ_trait`) REFERENCES `type_traitement` (`cod_typ_trait`);