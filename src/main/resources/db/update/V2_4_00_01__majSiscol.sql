SET FOREIGN_KEY_CHECKS=0;
UPDATE `candidature` set `typ_siscol` = 'typSiscol';
UPDATE `siscol_cat_exo_ext` set `typ_siscol` = 'typSiscol';
SET FOREIGN_KEY_CHECKS=1;