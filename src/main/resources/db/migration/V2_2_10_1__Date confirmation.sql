--
-- Modification table candidature
--
ALTER TABLE candidature
	ADD COLUMN dat_new_confirm_cand DATE NULL DEFAULT NULL COMMENT 'nouvelle date de confirmation de candidature' AFTER dat_incomplet_dossier_cand;
	
ALTER TABLE formation
	ADD COLUMN dat_confirm_list_comp_form DATE NULL DEFAULT NULL COMMENT 'date limite de confirmation lors de la gestion automatique des listes complémentaires' AFTER dat_confirm_form;