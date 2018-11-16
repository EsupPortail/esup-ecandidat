--
-- Modification table formation
--
ALTER TABLE formation ADD COLUMN capacite_form INT(10) NULL DEFAULT NULL COMMENT 'capacite accueil de la formation' AFTER info_comp_form;