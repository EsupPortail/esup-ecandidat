--
-- Mise à jour des tailles de login de log 
--

--table candidature
ALTER TABLE `candidature`
	CHANGE COLUMN `user_cre_cand` `user_cre_cand` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_cand` `user_mod_cand` VARCHAR(50) NOT NULL COMMENT 'user de modification',
	CHANGE COLUMN `user_annul_cand` `user_annul_cand` VARCHAR(50) NULL DEFAULT NULL COMMENT 'login d\'annulation de la candidature (si null, c\'est le candidat qui a supprimé)',
	CHANGE COLUMN `user_accept_cand` `user_accept_cand` VARCHAR(50) NULL DEFAULT NULL COMMENT 'login d\'acceptation ou de refus de la candidature';
	
--table centre_candidature
ALTER TABLE `centre_candidature`
	CHANGE COLUMN `user_cre_ctr_cand` `user_cre_ctr_cand` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_ctr_cand` `user_mod_ctr_cand` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table commission
ALTER TABLE `commission`
	CHANGE COLUMN `user_cre_comm` `user_cre_comm` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_comm` `user_mod_comm` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table droit_profil
ALTER TABLE `droit_profil`
	CHANGE COLUMN `user_cre_profil` `user_cre_profil` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_profil` `user_mod_profil` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table formation
ALTER TABLE `formation`
	CHANGE COLUMN `user_cre_form` `user_cre_form` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_form` `user_mod_form` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table formulaire
ALTER TABLE `formulaire`
	CHANGE COLUMN `user_cre_formulaire` `user_cre_formulaire` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_formulaire` `user_mod_formulaire` VARCHAR(50) NOT NULL COMMENT 'user de modification';

--table formulaire_cand
ALTER TABLE `formulaire_cand`
	CHANGE COLUMN `user_cre_formulaire_cand` `user_cre_formulaire_cand` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_formulaire_cand` `user_mod_formulaire_cand` VARCHAR(50) NOT NULL COMMENT 'user de modification';

--table mail
ALTER TABLE `mail`
	CHANGE COLUMN `user_cre_mail` `user_cre_mail` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_mail` `user_mod_mail` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table motivation_avis
ALTER TABLE `motivation_avis`
	CHANGE COLUMN `user_cre_motiv` `user_cre_motiv` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_motiv` `user_mod_motiv` VARCHAR(50) NOT NULL COMMENT 'user de modification';

--table piece_justif
ALTER TABLE `piece_justif`
	CHANGE COLUMN `user_cre_pj` `user_cre_pj` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_pj` `user_mod_pj` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table pj_cand
ALTER TABLE `pj_cand`
	CHANGE COLUMN `user_cre_pj_cand` `user_cre_pj_cand` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_pj_cand` `user_mod_pj_cand` VARCHAR(50) NOT NULL COMMENT 'user de modification',
	CHANGE COLUMN `user_mod_statut_pj_cand` `user_mod_statut_pj_cand` VARCHAR(50) NULL DEFAULT NULL COMMENT 'user de modif de statut de la piece';
	
--table type_decision
ALTER TABLE `type_decision`
	CHANGE COLUMN `user_cre_typ_dec` `user_cre_typ_dec` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_mod_typ_dec` `user_mod_typ_dec` VARCHAR(50) NOT NULL COMMENT 'user de modification';
	
--table type_decision_candidature
ALTER TABLE `type_decision_candidature`
	CHANGE COLUMN `user_cre_type_dec_cand` `user_cre_type_dec_cand` VARCHAR(50) NOT NULL COMMENT 'user de création',
	CHANGE COLUMN `user_valid_type_dec_cand` `user_valid_type_dec_cand` VARCHAR(50) NULL DEFAULT NULL COMMENT 'user de validation';