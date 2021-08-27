SET FOREIGN_KEY_CHECKS=0;
UPDATE `siscol_specialite_bac` set `typ_siscol` = 'typSiscol';
UPDATE `siscol_option_bac` set `typ_siscol` = 'typSiscol';
UPDATE `siscol_bac_spe_bac` set `typ_siscol` = 'typSiscol';
UPDATE `siscol_bac_opt_bac` set `typ_siscol` = 'typSiscol';
SET FOREIGN_KEY_CHECKS=1;