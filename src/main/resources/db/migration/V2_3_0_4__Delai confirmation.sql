--
-- Modification formation
--
ALTER TABLE `formation`
	ADD COLUMN `delai_confirm_form` INT(10) NULL DEFAULT NULL COMMENT 'delai de confirmation' AFTER `dat_confirm_list_comp_form`,
	ADD COLUMN `delai_confirm_list_comp_form` INT(10) NULL DEFAULT NULL COMMENT 'delai de confirmation lors de la gestion automatique des listes complémentaires' AFTER `delai_confirm_form`;
	
--
-- Modification centre candidature
--
ALTER TABLE `centre_candidature`
	ADD COLUMN `delai_confirm_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'delai de confirmation' AFTER `dat_confirm_list_comp_ctr_cand`,
	ADD COLUMN `delai_confirm_list_comp_ctr_cand` INT(10) NULL DEFAULT NULL COMMENT 'delai de confirmation lors de la gestion automatique des listes complémentaires' AFTER `delai_confirm_ctr_cand`;