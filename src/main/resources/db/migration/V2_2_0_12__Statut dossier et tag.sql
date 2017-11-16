--
-- Modification table type_statut
--
ALTER TABLE `type_statut`
	ADD COLUMN `tem_comm_visible` BIT(1) NOT NULL DEFAULT b'1' COMMENT 'témoin de visu pour les membres de commission (si le témoin est à false, les membres de commission ne verront pas les candidatures liées à ce statut)' AFTER `id_i18n_lib_typ_statut`;

--
-- Creation table tag
--
CREATE TABLE `tag` (
	`id_tag` INT(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant tag',
	`color_tag` VARCHAR(20) NOT NULL COMMENT 'couleur du tag',
	`lib_tag` VARCHAR(255) NOT NULL COMMENT 'libellé du tag',
	`tes_tag` BIT(1) NOT NULL COMMENT 'témoin en service',
	PRIMARY KEY (`id_tag`)
)
COMMENT='table des tags'
ENGINE=InnoDB;

--
-- Modification table candidature
--
ALTER TABLE `candidature`
	ADD COLUMN `id_tag` INT(10) NULL COMMENT 'identifiant du tag' AFTER `cod_typ_statut`;
	
ALTER TABLE `candidature` 
	ADD INDEX `fk_candidature_tag_id_tag` (`id_tag`), 
	ADD CONSTRAINT `fk_candidature_tag_id_tag` 
	FOREIGN KEY (`id_tag`) REFERENCES tag (`id_tag`);