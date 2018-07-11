--
-- Modification table batch
--
ALTER TABLE batch
	ADD COLUMN frequence_batch INT(10) NOT NULL DEFAULT '0' COMMENT 'la frequence de passage du batch' AFTER fixe_year_batch,
	ADD COLUMN tem_frequence_batch BIT(1) NOT NULL DEFAULT b'0' COMMENT 'témoin si on utilise la frequence du batch plutot que l\'heure de passage' AFTER frequence_batch;