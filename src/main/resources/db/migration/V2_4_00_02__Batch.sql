ALTER TABLE `batch_run` ADD COLUMN `cod_run` VARCHAR(5) NOT NULL DEFAULT 'BATCH' COMMENT 'code du run' FIRST;
ALTER TABLE `batch_run` CHANGE COLUMN `dat_last_check_run` `dat_last_check_run` DATETIME NOT NULL COMMENT 'date du dernier run' AFTER `cod_run`;
ALTER TABLE `batch_run` DROP PRIMARY KEY;
ALTER TABLE `batch_run` ADD PRIMARY KEY (`cod_run`);
ALTER TABLE `batch_run` CHANGE COLUMN `cod_run` `cod_run` VARCHAR(5) NOT NULL COMMENT 'code du run' FIRST;