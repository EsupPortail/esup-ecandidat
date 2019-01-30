--
-- Modification type_decision_candidature
--
ALTER TABLE `type_decision_candidature`
	ADD COLUMN `action_type_dec_cand` VARCHAR(1) NULL DEFAULT NULL COMMENT 'action réalisé sur la decision' AFTER `user_valid_type_dec_cand`,
	ADD COLUMN `user_action_type_dec_cand` VARCHAR(50) NULL DEFAULT NULL COMMENT 'user action réalisé sur la decision' AFTER `action_type_dec_cand`;