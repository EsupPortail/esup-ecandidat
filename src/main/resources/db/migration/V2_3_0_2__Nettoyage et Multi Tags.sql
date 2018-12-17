--
-- Nettoyage candidature
--
ALTER TABLE `candidature` DROP COLUMN `dat_opi_cand`;

--
-- Nettoyage post-it
--
ALTER TABLE `post_it` CHANGE COLUMN `user_cre_post_it` `user_cre_post_it` VARCHAR(255) NOT NULL COMMENT 'login de la personne ayant réalisé le post-it' AFTER `message_post_it`;

--
-- Commentaire siscol_typ_resultat
--
ALTER TABLE `siscol_typ_resultat` COMMENT='Rérérentiel SiScol : Types de résultats';

--
-- Création table tag_candidature
--
CREATE TABLE `tag_candidature` (
	`id_cand` INT(10) NOT NULL COMMENT 'identifiant de la candidature',
	`id_tag` INT(10) NOT NULL COMMENT 'identifiant du tag',
	PRIMARY KEY (`id_cand`, `id_tag`),
	INDEX `fk_tag_candidature_candidature_id_cand` (`id_cand`),
	INDEX `fk_tag_candidature_tag_id_tag` (`id_tag`),
	CONSTRAINT `fk_tag_candidature_candidature_id_cand` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`),
	CONSTRAINT `fk_tag_candidature_tag_id_tag` FOREIGN KEY (`id_tag`) REFERENCES `tag` (`id_tag`)
)
COMMENT='table des tags des candidatures' ENGINE=InnoDB;

--
-- Insertion données table tag_candidature
--
INSERT INTO `tag_candidature` (`id_cand`, `id_tag`) SELECT `id_cand`, `id_tag` from `candidature` where `id_tag` is not null;

--
-- Nettoyage candidature
--
ALTER TABLE `candidature` DROP FOREIGN KEY `fk_candidature_tag_id_tag`;
ALTER TABLE `candidature` DROP COLUMN `id_tag`;

--
-- Modofication préférences
--
UPDATE preference_ind set cand_col_visible_pref = REPLACE(cand_col_visible_pref,'tag;','tags;');
UPDATE preference_ind set cand_col_order_pref = REPLACE(cand_col_order_pref,'tag;','tags;');
UPDATE preference_ind set cand_col_sort_pref = REPLACE(cand_col_sort_pref,'tag:','tags:');