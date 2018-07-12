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
