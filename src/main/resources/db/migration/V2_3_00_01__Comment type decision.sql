--
-- Modification table type_decision_candidature
--
ALTER TABLE `type_decision_candidature`	CHANGE COLUMN `comment_type_dec_cand` `comment_type_dec_cand` VARCHAR(1000) NULL DEFAULT NULL COMMENT 'éventuel commentaire'