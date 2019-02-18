--
-- Modification table parametre
--
ALTER TABLE `batch`	CHANGE COLUMN `lib_batch` `lib_batch` VARCHAR(100) NOT NULL COMMENT 'libellé du batch' AFTER `cod_batch`;