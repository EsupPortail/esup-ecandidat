--
-- Mise à jour de la structure de la table campagne
--

ALTER TABLE `campagne`
	ADD COLUMN `id_i18n_libelle_campagne` INT(10) NULL COMMENT 'libellé campagne internationalisé' AFTER `lib_camp`;
	
ALTER TABLE campagne 
	ADD INDEX fk_campagne_i18n_id_i18n_id_i18n_libelle_campagne (id_i18n_libelle_campagne), 
	ADD CONSTRAINT fk_campagne_i18n_id_i18n_id_i18n_libelle_campagne FOREIGN KEY (id_i18n_libelle_campagne) 
	REFERENCES i18n (id_i18n);