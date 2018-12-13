--
-- Modification table formation
--
ALTER TABLE formation
	ADD COLUMN capacite_form INT(10) NULL DEFAULT NULL COMMENT 'capacite accueil de la formation' AFTER info_comp_form,
	ADD COLUMN cod_dip_apo_form VARCHAR(20) NULL DEFAULT NULL COMMENT 'code diplome apogée de la formation' AFTER lib_apo_form,
	ADD COLUMN cod_vrs_vdi_apo_form VARCHAR(20) NULL DEFAULT NULL COMMENT 'code version diplome apogée de la formation' AFTER cod_dip_apo_form,
	ADD COLUMN lib_dip_apo_form VARCHAR(120) NULL DEFAULT NULL COMMENT 'libellé diplome apogée de la formation' AFTER cod_vrs_vdi_apo_form;