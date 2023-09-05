CREATE TABLE `configuration` (
	`cod_config` VARCHAR(50) NOT NULL,
	`val_config` VARCHAR(1000) NOT NULL,
	PRIMARY KEY (`cod_config`)
)
COMMENT='table de configuration'
ENGINE=InnoDB;