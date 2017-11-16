--
-- Mise à jour de la structure de la table compte_minima
--

ALTER TABLE `compte_minima`
	ADD COLUMN `typ_gen_cpt_min` VARCHAR(1) NOT NULL DEFAULT 'P' COMMENT 'type de hash utilisé' AFTER `pwd_cpt_min`;