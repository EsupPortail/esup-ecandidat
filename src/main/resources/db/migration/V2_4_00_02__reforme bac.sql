-- Création d'une procédure stockée (obligatoire pour supprimer les index car la version full pégase n'a pas tous les index
DELIMITER $$

DROP PROCEDURE IF EXISTS `DropConstraintIfExist` $$
CREATE PROCEDURE `DropConstraintIfExist`
(
    given_table    VARCHAR(64),
    given_constraint    VARCHAR(64)
)
BEGIN

    DECLARE IndexIsThere INTEGER;
    DECLARE FKIsThere INTEGER;
    
    SELECT COUNT(1) INTO FKIsThere
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE 
	 TABLE_SCHEMA = (SELECT DATABASE())
    AND 	 TABLE_NAME   = given_table
    AND   CONSTRAINT_NAME   = given_constraint;
    
    IF FKIsThere != 0 THEN
        SET @sqlstmtFK = CONCAT('ALTER TABLE ', given_table, ' DROP FOREIGN KEY ', given_constraint);
        PREPARE stFK FROM @sqlstmtFK;
        EXECUTE stFK;
        DEALLOCATE PREPARE stFK;
    END IF;

    SELECT COUNT(1) INTO IndexIsThere
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE 
	 TABLE_SCHEMA = (SELECT DATABASE())
    AND 	 TABLE_NAME   = given_table
    AND   INDEX_NAME   = given_constraint;

    IF IndexIsThere != 0 THEN
		  SET @sqlstmtIndex = CONCAT('ALTER TABLE ', given_table, ' DROP INDEX ', given_constraint);
        PREPARE stIndex FROM @sqlstmtIndex;
        EXECUTE stIndex;
        DEALLOCATE PREPARE stIndex;  
    END IF;

END $$

DELIMITER ;

-- Suppression des clé étrangeres de candidat_bac_ou_equ
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_specialite_bac_cod_spe1_bac_ter');
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_specialite_bac_cod_spe2_bac_ter');
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_specialite_bac_cod_spe_bac_pre');
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_option_bac_cod_opt1_bac');
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_option_bac_cod_opt2_bac');
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_option_bac_cod_opt3_bac');
call DropConstraintIfExist('candidat_bac_ou_equ','fk_bac_ou_equ_siscol_option_bac_cod_opt4_bac');

-- Suppression des clé étrangeres de siscol_bac_opt
call DropConstraintIfExist('siscol_bac_opt_bac','FK_bac_opt_bac_bac_cod_bac');
call DropConstraintIfExist('siscol_bac_opt_bac','FK_bac_opt_bac_opt_bac_cod_opt_bac');

-- Suppression des clé étrangeres de siscol_bac_spe_bac
call DropConstraintIfExist('siscol_bac_spe_bac','FK_bac_spe_bac_bac_cod_bac');
call DropConstraintIfExist('siscol_bac_spe_bac','FK_bac_spe_bac_spe_bac_cod_spe_bac');

-- Suppression procédure stockée
DROP PROCEDURE `DropConstraintIfExist`;

-- Ajout du typ_siscol pour les siscol_specialite_bac
ALTER TABLE `siscol_specialite_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_spe_bac`;
ALTER TABLE `siscol_specialite_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_specialite_bac` ADD PRIMARY KEY (`cod_spe_bac`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_option_bac
ALTER TABLE `siscol_option_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_opt_bac`;
ALTER TABLE `siscol_option_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_option_bac` ADD PRIMARY KEY (`cod_opt_bac`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_bac_spe_bac
ALTER TABLE `siscol_bac_spe_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_spe_bac`;
ALTER TABLE `siscol_bac_spe_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_bac_spe_bac` ADD PRIMARY KEY (`cod_bac`, `cod_spe_bac`, `typ_siscol`);

-- Ajout du typ_siscol pour les siscol_bac_opt_bac
ALTER TABLE `siscol_bac_opt_bac` ADD COLUMN `typ_siscol` VARCHAR(1) NOT NULL DEFAULT 'D' COMMENT 'Type de siscol' AFTER `cod_opt_bac`;
ALTER TABLE `siscol_bac_opt_bac` DROP PRIMARY KEY;
ALTER TABLE `siscol_bac_opt_bac` ADD PRIMARY KEY (`cod_bac`, `cod_opt_bac`, `typ_siscol`);


-- Ajout des clé étrangeres de candidat_bac_ou_equ
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe1_bac_ter` FOREIGN KEY (`cod_spe1_bac_ter`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe2_bac_ter` FOREIGN KEY (`cod_spe2_bac_ter`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_specialite_bac_cod_spe_bac_pre` FOREIGN KEY (`cod_spe_bac_pre`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt1_bac` FOREIGN KEY (`cod_opt1_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt2_bac` FOREIGN KEY (`cod_opt2_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt3_bac` FOREIGN KEY (`cod_opt3_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);
ALTER TABLE `candidat_bac_ou_equ` ADD CONSTRAINT `fk_bac_ou_equ_siscol_option_bac_cod_opt4_bac` FOREIGN KEY (`cod_opt4_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);

-- Ajout des clé étrangeres de siscol_bac_spe_bac
ALTER TABLE `siscol_bac_spe_bac` ADD CONSTRAINT `FK_bac_spe_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`, `typ_siscol`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`, `typ_siscol`);
ALTER TABLE `siscol_bac_spe_bac` ADD CONSTRAINT `FK_bac_spe_bac_spe_bac_cod_spe_bac` FOREIGN KEY (`cod_spe_bac`, `typ_siscol`) REFERENCES `siscol_specialite_bac` (`cod_spe_bac`, `typ_siscol`);

-- Ajout des clé étrangeres de siscol_bac_opt
ALTER TABLE `siscol_bac_opt_bac` ADD CONSTRAINT `FK_bac_opt_bac_bac_cod_bac` FOREIGN KEY (`cod_bac`, `typ_siscol`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`, `typ_siscol`);
ALTER TABLE `siscol_bac_opt_bac` ADD CONSTRAINT `FK_bac_opt_bac_opt_bac_cod_opt_bac` FOREIGN KEY (`cod_opt_bac`, `typ_siscol`) REFERENCES `siscol_option_bac` (`cod_opt_bac`, `typ_siscol`);