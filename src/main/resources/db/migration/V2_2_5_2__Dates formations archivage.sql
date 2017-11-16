--
-- Modification table candidature
--
ALTER TABLE `candidature`
	ADD COLUMN `dat_deb_depot_form` DATE NULL DEFAULT NULL COMMENT 'date de début de dépot des voeux (renseignée après archivage)' AFTER `dat_incomplet_dossier_cand`,
	ADD COLUMN `dat_fin_depot_form` DATE NULL DEFAULT NULL COMMENT 'date de fin de dépot des voeux (renseignée après archivage)' AFTER `dat_deb_depot_form`,
	ADD COLUMN `dat_retour_form` DATE NULL DEFAULT NULL COMMENT 'date limite de retour de dossier (renseignée après archivage)' AFTER `dat_fin_depot_form`,
	ADD COLUMN `dat_confirm_form` DATE NULL DEFAULT NULL COMMENT 'date limite de confirmation (renseignée après archivage)' AFTER `dat_retour_form`,
	ADD COLUMN `dat_publi_form` DATE NULL DEFAULT NULL COMMENT 'date de publication des résultats (renseignée après archivage)' AFTER `dat_confirm_form`,
	ADD COLUMN `dat_jury_form` DATE NULL DEFAULT NULL COMMENT 'date de jury (renseignée après archivage)' AFTER `dat_publi_form`,
	ADD COLUMN `dat_analyse_form` DATE NULL DEFAULT NULL COMMENT 'date de pré-analyse du dossier (renseignée après archivage)' AFTER `dat_jury_form`;