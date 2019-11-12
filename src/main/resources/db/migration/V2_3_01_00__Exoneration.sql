ALTER TABLE `candidature`
	ADD COLUMN `comp_exo_ext_cand` VARCHAR(200) NULL DEFAULT NULL COMMENT 'complément exonération/extracommunautaire' AFTER `dat_analyse_form`,
	ADD COLUMN `mnt_charge_cand` DECIMAL(10,2) NULL DEFAULT NULL COMMENT 'montant restant à charge' AFTER `comp_exo_ext_cand`;