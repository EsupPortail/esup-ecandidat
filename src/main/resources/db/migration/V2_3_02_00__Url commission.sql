ALTER TABLE `commission` ADD COLUMN `url_comm` VARCHAR(255) NULL COMMENT 'url de la commission' AFTER `mail_comm`;
ALTER TABLE `commission` ADD COLUMN `mail_alert_comm` VARCHAR(80) NULL COMMENT 'mail pour les alertes de la commission' AFTER `id_fichier`;
UPDATE `commission` set `mail_alert_comm` = `mail_comm`;
ALTER TABLE `commission` CHANGE COLUMN `mail_alert_comm` `mail_alert_comm` VARCHAR(80) NOT NULL COMMENT 'mail pour les alertes de la commission' AFTER `id_fichier`;
ALTER TABLE `commission` CHANGE COLUMN `mail_comm` `mail_comm` VARCHAR(80) NULL COMMENT 'mail de contact de la commission' AFTER `lib_comm`;