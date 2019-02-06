--
-- Modification candidature
--
ALTER TABLE `candidature`
	ADD COLUMN `tem_relance_cand` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'témoin pour savoir si la candidature a été relancée' AFTER `user_accept_cand`;