ALTER TABLE `opi`
	ADD COLUMN `tes_opi` BIT NOT NULL DEFAULT b'1' COMMENT 'témoin si l''opi doit être joué' AFTER `cod_opi`;
	
UPDATE `opi` set `tes_opi` = b'0' WHERE dat_passage_opi IS NOT NULL;