--
-- Modification table batch_histo
--
ALTER TABLE batch_histo
	ADD COLUMN detail_batch_histo TEXT NULL COMMENT 'détail de l\'historique' AFTER state_batch_histo;