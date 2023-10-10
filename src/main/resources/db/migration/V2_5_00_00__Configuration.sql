CREATE TABLE `configuration` (
	`cod_config` VARCHAR(50) NOT NULL,
	`val_config` VARCHAR(1000) NOT NULL,
	PRIMARY KEY (`cod_config`)
)
COMMENT='table de configuration'
ENGINE=InnoDB;

CREATE TABLE `inscription_ind` (
	`login_ins` VARCHAR(50) NOT NULL COMMENT 'login de l''inscription individu',
	`libelle_ins` VARCHAR(255) NOT NULL COMMENT 'displayName de l''inscription individu',
	`mail_ins` VARCHAR(255) NOT NULL COMMENT 'mail de l''inscription individu',
	PRIMARY KEY (`login_ins`)
)
COMMENT='table des inscriptions des individus'
ENGINE=InnoDB;
