--
-- Modification table batch_histo
--
ALTER TABLE batch_histo
	ADD COLUMN desc_histo_batch TEXT NULL COMMENT 'description de l\'historique' AFTER state_batch_histo;
