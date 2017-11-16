--
-- Modification table formation
--
ALTER TABLE `formation`	ADD COLUMN `id_i18n_info_comp_form` INT(10) NULL DEFAULT NULL COMMENT 'i18n informations complémentaires de la formation' AFTER `preselect_lieu_form`;
ALTER TABLE formation ADD INDEX fk_formation_i18n_id_i18n_info_comp_form (id_i18n_info_comp_form), ADD CONSTRAINT fk_formation_i18n_id_i18n_info_comp_form FOREIGN KEY (id_i18n_info_comp_form) REFERENCES i18n (id_i18n);

--
-- Modification table commission
--
ALTER TABLE `commission` ADD COLUMN `id_i18n_comment_retour_comm` INT(10) NULL DEFAULT NULL COMMENT 'i18n commentaire lors du retour de dossier pour la commission' AFTER `id_adr`;
ALTER TABLE commission ADD INDEX fk_commission_i18n_id_i18n_comment_retour_comm (id_i18n_comment_retour_comm), ADD CONSTRAINT fk_commission_i18n_id_i18n_comment_retour_comm FOREIGN KEY (id_i18n_comment_retour_comm) REFERENCES i18n (id_i18n);
