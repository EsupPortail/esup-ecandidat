--
-- Creation table preference_ind
--
CREATE TABLE `preference_ind` (
	`login_ind` VARCHAR(50) NOT NULL COMMENT 'login individu',
	`cand_col_visible_pref` TEXT NULL DEFAULT NULL COMMENT 'préférences des colonnes visibles dans l\'ecran des candidatures',
	`cand_col_order_pref` TEXT NULL DEFAULT NULL COMMENT 'préférences de l\'ordre des colonnes dans l\'ecran des candidatures',
	`cand_col_sort_pref` VARCHAR(100) NULL DEFAULT NULL COMMENT 'préférences de la colonne de trie dans l\'ecran des candidatures',
	`cand_col_sort_direction_pref` VARCHAR(1) NULL DEFAULT NULL COMMENT 'préférences de la direction de trie dans l\'ecran des candidatures',
	`cand_col_frozen_pref` INT(2) NULL DEFAULT NULL COMMENT 'préférences de la colonne frozen dans l\'ecran des candidatures',
	`cand_id_comm_pref` INT(10) NULL DEFAULT NULL COMMENT 'préférences de la dernière commission visitée dans l\'ecran de candidatures',
	`export_col_pref` TEXT NULL DEFAULT NULL COMMENT 'préférences des colonnes d\'export',
	`export_tem_footer_pref` BIT(1) NULL DEFAULT NULL COMMENT 'préférences du footer d\'export',	
	`id_ctr_cand_pref` INT(10) NULL DEFAULT NULL COMMENT 'préférences du dernier centre de candidature visité',
	`id_comm_pref` INT(10) NULL DEFAULT NULL COMMENT 'préférences de la dernière commission visitée',
	PRIMARY KEY (`login_ind`),
	INDEX `fk_individu_preference_ind_login_ind` (`login_ind`),
	CONSTRAINT `fk_individu_preference_ind_login_ind` FOREIGN KEY (`login_ind`) REFERENCES `individu` (`login_ind`)
)
COMMENT='table des preferences individu'
ENGINE=InnoDB;

--
-- Alimentation de la table preference_ind
--
INSERT INTO `preference_ind` (`login_ind`) SELECT DISTINCT `login_ind` FROM `preference_individu`;
UPDATE `preference_ind` SET `cand_col_visible_pref` = (SELECT `val_pref` FROM `preference_individu` WHERE `preference_ind`.`login_ind` = `preference_individu`.`login_ind` AND `cod_pref`='V');
UPDATE `preference_ind` SET `cand_col_order_pref` = (SELECT `val_pref` FROM `preference_individu` WHERE `preference_ind`.`login_ind` = `preference_individu`.`login_ind` AND `cod_pref`='O');
UPDATE `preference_ind` SET `cand_col_sort_pref` = (SELECT `val_pref` FROM `preference_individu` WHERE `preference_ind`.`login_ind` = `preference_individu`.`login_ind` AND `cod_pref`='C');
UPDATE `preference_ind` SET `cand_col_sort_direction_pref` = (SELECT `val_pref` FROM `preference_individu` WHERE `preference_ind`.`login_ind` = `preference_individu`.`login_ind` AND `cod_pref`='D');
UPDATE `preference_ind` SET `cand_col_frozen_pref` = (SELECT `val_pref` FROM `preference_individu` WHERE `preference_ind`.`login_ind` = `preference_individu`.`login_ind` AND `cod_pref`='F');
UPDATE `preference_ind` SET `export_col_pref` = (SELECT `val_pref` FROM `preference_individu` WHERE `preference_ind`.`login_ind` = `preference_individu`.`login_ind` AND `cod_pref`='E');

--
-- Alimentation de la table preference_individu
--
DROP TABLE `preference_individu`;