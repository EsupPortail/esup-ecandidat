--
-- Modification table candidature
--
ALTER TABLE candidature
	ADD COLUMN dat_new_confirm_cand DATE NULL DEFAULT NULL COMMENT 'nouvelle date de confirmation de candidature' AFTER dat_incomplet_dossier_cand;

ALTER TABLE candidature
	ADD COLUMN dat_new_retour_cand DATE NULL DEFAULT NULL COMMENT 'nouvelle date de retour du dossier' AFTER dat_incomplet_dossier_cand;

--
-- Modification table formation
--
ALTER TABLE formation
	ADD COLUMN dat_confirm_list_comp_form DATE NULL DEFAULT NULL COMMENT 'date limite de confirmation lors de la gestion automatique des listes complémentaires' AFTER dat_confirm_form;

--
-- Modification table centre_candidature
--	
ALTER TABLE centre_candidature
	ADD COLUMN dat_confirm_list_comp_ctr_cand DATE NULL DEFAULT NULL COMMENT 'date limite de confirmation lors de la gestion automatique des listes complémentaires' AFTER dat_confirm_ctr_cand;

	
--
-- Modification table formation
--
ALTER TABLE formation
	ADD COLUMN capacite_form INT(10) NULL DEFAULT NULL COMMENT 'capacite accueil de la formation' AFTER info_comp_form,
	ADD COLUMN cod_dip_apo_form VARCHAR(20) NULL DEFAULT NULL COMMENT 'code diplome apogée de la formation' AFTER lib_apo_form,
	ADD COLUMN cod_vrs_vdi_apo_form VARCHAR(20) NULL DEFAULT NULL COMMENT 'code version diplome apogée de la formation' AFTER cod_dip_apo_form,
	ADD COLUMN lib_dip_apo_form VARCHAR(120) NULL DEFAULT NULL COMMENT 'libellé diplome apogée de la formation' AFTER cod_vrs_vdi_apo_form;

--
-- Modification table preference_ind
--
ALTER TABLE preference_ind CHANGE COLUMN cand_col_sort_pref cand_col_sort_pref TEXT NULL DEFAULT NULL COMMENT 'ecran des candidatures : préférences de la colonne de trie';
UPDATE preference_ind SET cand_col_sort_pref = CASE WHEN (cand_col_sort_pref IS NULL OR cand_col_sort_direction_pref IS NULL) THEN NULL ELSE CONCAT(CONCAT(cand_col_sort_pref,':'), cand_col_sort_direction_pref) END;
ALTER TABLE preference_ind DROP COLUMN cand_col_sort_direction_pref;

--
-- Modification table candidat_cursus_interne
--

ALTER TABLE candidat_cursus_interne CHANGE COLUMN not_vet_cursus_interne not_vet_cursus_interne VARCHAR(20) NULL DEFAULT NULL COMMENT 'note de la vet du cursus interne';

--
-- Modification table post_it
--
ALTER TABLE post_it ADD COLUMN user_cre_post_it VARCHAR(255) NULL COMMENT 'login de la personne ayant réalisé le post-it' AFTER user_post_it;
UPDATE post_it p SET p.user_cre_post_it = (SELECT login_ind FROM individu i WHERE p.user_post_it = i.libelle_ind AND (SELECT count(1) FROM individu ii WHERE i.libelle_ind = ii.libelle_ind)=1 limit 1);
UPDATE post_it p SET p.user_cre_post_it = user_post_it WHERE user_cre_post_it IS NULL;
ALTER TABLE post_it CHANGE COLUMN user_cre_post_it user_cre_post_it VARCHAR(255) NOT NULL COMMENT 'login de la personne ayant réalisé le post-it';
ALTER TABLE post_it DROP COLUMN user_post_it;

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

--
-- Modification motivation_avis
--
ALTER TABLE `motivation_avis`
	ADD COLUMN `id_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du centre de candidature éventuel' AFTER `id_i18n_lib_motiv`,
	ADD CONSTRAINT `fk_motivation_avis_centre_candidature_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`);

--
-- Modification type_decision
--
ALTER TABLE `type_decision`
	ADD COLUMN `id_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du centre de candidature éventuel' AFTER `id_mail`,
	ADD CONSTRAINT `fk_type_decision_centre_candidature_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`);
	
--
-- Modification mail
--
ALTER TABLE `mail`
	ADD COLUMN `id_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du centre de candidature éventuel' AFTER `lib_mail`,
	ADD CONSTRAINT `fk_mail_centre_candidature_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`);
	
--
-- Modification tag
--
ALTER TABLE `tag`
	ADD COLUMN `id_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du centre de candidature éventuel' AFTER `lib_tag`,
	ADD CONSTRAINT `fk_tag_centre_candidature_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`);

--
-- Modification alert_sva
--
ALTER TABLE `alert_sva`
	ADD COLUMN `id_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'identifiant du centre de candidature éventuel' AFTER `color_sva`,
	ADD CONSTRAINT `fk_alert_sva_centre_candidature_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`),
	DROP INDEX `nb_jour_sva`;

--
-- Modification centre_candidature
--
ALTER TABLE `centre_candidature`
	ADD COLUMN `sva_dat_ctr_cand` VARCHAR(3) NULL DEFAULT NULL COMMENT 'paramétrage SVA de la date spécifique centre de candidature' AFTER `tem_send_mail_ctr_cand`,
	ADD COLUMN `sva_definitif_ctr_cand` BIT(1) NULL DEFAULT NULL COMMENT 'paramétrage SVA avis definitif spécifique centre de candidature' AFTER `sva_dat_ctr_cand`,
	ADD COLUMN `tem_param` BIT(1) NOT NULL DEFAULT b'1' COMMENT 'témoin pour indiquer que les gestionnaires auront le droit de modifier leur parametrage' AFTER `sva_definitif_ctr_cand`,
	CHANGE COLUMN `mail_contact_ctr_cand` `mail_contact_ctr_cand` VARCHAR(80) NULL DEFAULT NULL COMMENT 'mail de contact du centre de candidature' AFTER `info_comp_ctr_cand`,
	CHANGE COLUMN `tem_send_mail_ctr_cand` `tem_send_mail_ctr_cand` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'témoin pour indiquer que les gestionnaires recevront une copie des mails (BCC) envoyés aux candidats' AFTER `mail_contact_ctr_cand`;
	
--
-- Nettoyage type_decision
--
ALTER TABLE `type_decision`
	DROP FOREIGN KEY `fktype_decision_type_avis_cod_typ_avis`,
	DROP INDEX `fktype_decision_type_avis_cod_typ_avis`,
	ADD INDEX `fk_type_decision_type_avis_cod_typ_avis` (`cod_typ_avis`),
	ADD CONSTRAINT `fk_type_decision_type_avis_cod_typ_avis` FOREIGN KEY (`cod_typ_avis`) REFERENCES `type_avis` (`cod_typ_avis`);

--
-- Modification parametre
--	
ALTER TABLE `parametre`
	ADD COLUMN `tem_scol` BIT(1) NOT NULL DEFAULT 0 COMMENT 'temoin paramètre accessible scol centrale' AFTER `typ_param`,
	ADD COLUMN `tem_affiche` BIT(1) NOT NULL DEFAULT b'1' COMMENT 'temoin si le paramètre est affiché dans la liste éditable' AFTER `tem_scol`,
	CHANGE COLUMN `lib_param` `lib_param` VARCHAR(500) NOT NULL COMMENT 'libellé du paramètre' AFTER `cod_param`;