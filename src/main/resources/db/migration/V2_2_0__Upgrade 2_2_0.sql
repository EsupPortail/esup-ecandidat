--
-- Mise à jour version 2.2.0
--

--
-- Structure de la table `alert_sva`
--
CREATE TABLE alert_sva 
  ( 
     id_sva      INT(11) NOT NULL auto_increment comment 
     'identifiant alerte sva', 
     nb_jour_sva INT(11) NOT NULL UNIQUE comment 
     'nombre de jour sur laquelle l''alerte SVA aura effet', 
     color_sva   VARCHAR(20) NOT NULL comment 'couleur de l''alerte (code hexa)' 
     , 
     tes_sva     BIT(1) NOT NULL comment 'témoin en service', 
     PRIMARY KEY (id_sva) 
  ) 
comment='table des couleurs d''alertes SVA' 
engine=innodb;

--
-- Structure de la table `post_it`
--
CREATE TABLE post_it 
  ( 
     id_post_it      INT(10) NOT NULL auto_increment comment 
     'identifiant du post-it', 
     user_post_it    VARCHAR(255) NOT NULL comment 
     'utilisateur ayant réalisé le post-it', 
     id_cand         INT(10) NOT NULL comment 'identifiant de la candidature', 
     message_post_it VARCHAR(255) NOT NULL comment 'message du post-it', 
     dat_cre_post_it DATETIME NOT NULL comment 'date de création du post-it', 
     PRIMARY KEY (id_post_it) 
  ) 
comment='table des post-it' 
engine=innodb; 

 
--
-- Structure de la table `message`
--
CREATE TABLE message 
  ( 
     cod_msg         VARCHAR(30) NOT NULL comment 'code du message', 
     lib_msg         VARCHAR(50) NOT NULL comment 'Libellé du message', 
     id_i18n_val_msg INT(10) NOT NULL comment 'identifiant i18n du message', 
     tes_msg         BIT(1) NOT NULL comment 'témoin en service', 
     dat_mod_msg     DATETIME NOT NULL comment 'date de modification du message' 
, 
     PRIMARY KEY (cod_msg) 
  ) 
comment='table des messages' 
engine=innodb; 

--
-- Structure de la table `preference_individu`
--
CREATE TABLE preference_individu 
  ( 
     login_ind VARCHAR(50) NOT NULL comment 'login individu', 
     cod_pref  CHAR(1) NOT NULL comment 'code de la preference', 
     val_pref  VARCHAR(550) NOT NULL comment 'valeur de la préference', 
     PRIMARY KEY (login_ind, cod_pref) 
  ) 
comment='table des preferences individu' 
engine=innodb; 

--
-- Contraintes pour la table `preference_individu`
--
ALTER TABLE preference_individu 
  ADD INDEX fk_individu_preference_individu_login_ind (login_ind), 
  ADD CONSTRAINT fk_individu_preference_individu_login_ind FOREIGN KEY ( 
  login_ind) REFERENCES individu (login_ind); 

--
-- Contraintes pour la table `post_it`
--
ALTER TABLE post_it 
  ADD INDEX fk_candidature_post_it_id_candidature (id_cand), 
  ADD CONSTRAINT fk_candidature_post_it_id_candidature FOREIGN KEY (id_cand) 
  REFERENCES candidature (id_cand);

--
-- Contraintes pour la table `message`
--
ALTER TABLE message 
  ADD INDEX fk_message_i18n_id_i18n (id_i18n_val_msg), 
  ADD CONSTRAINT fk_message_i18n_id_i18n FOREIGN KEY (id_i18n_val_msg) 
  REFERENCES i18n (id_i18n); 

--
-- Mise à jour structure de la table `fichier`
--
ALTER TABLE `fichier` 
  modify `file_fichier` VARCHAR(1000) NOT NULL comment 'le libellé du fichier'; 

--
-- Mise à jour structure de la table `centre_candidature`
--
ALTER TABLE `centre_candidature` 
  ADD COLUMN `mail_contact_ctr_cand` VARCHAR(80) NULL comment 
  'mail de contact du centre de candidature'; 

ALTER TABLE `centre_candidature` 
  ADD COLUMN `tem_send_mail_ctr_cand` BIT(1) NOT NULL DEFAULT 0 comment 
'témoin pour indiquer que les gestionnaires recevront une copie des mails (BCC) envoyés aux candidats'
; 

--
-- Mise à jour structure et données de la table `droit_profil`
--
ALTER TABLE `droit_profil` 
  ADD COLUMN `typ_profil` VARCHAR(1) NOT NULL DEFAULT 'G' comment 
  'type de profil : admin (A), gestionnaire (G) ou commission (C)' after 
  `tem_ctr_cand_profil`; 

UPDATE `droit_profil` 
SET    `typ_profil` = 'A' 
WHERE  `tem_admin_profil` = 1; 

UPDATE `droit_profil` 
SET    `typ_profil` = 'G' 
WHERE  `tem_ctr_cand_profil` = 1; 

UPDATE `droit_profil` 
SET    `typ_profil` = 'C' 
WHERE  `tem_admin_profil` = 0 
       AND `tem_ctr_cand_profil` = 0; 

ALTER TABLE `droit_profil` 
  DROP COLUMN `tem_admin_profil`; 

ALTER TABLE `droit_profil` 
  DROP COLUMN `tem_ctr_cand_profil`; 

--
-- Mise à jour structure de la table `droit_fonctionnalite`
--
ALTER TABLE `droit_fonctionnalite` 
  ADD COLUMN `tem_open_com_fonc` BIT(1) NOT NULL DEFAULT 0 comment 
  'témoin indiquant que la fonctionnalité est ouverte aux commissions'; 

ALTER TABLE `droit_fonctionnalite` 
  ADD COLUMN `lic_fonc` VARCHAR(50) NOT NULL DEFAULT '' comment 
  'libellé court de la fonctionnalité' after `lib_fonc`; 

ALTER TABLE `droit_fonctionnalite` 
  ADD COLUMN `order_fonc` INT NOT NULL DEFAULT 0 comment 
  'ordre d’affichage de la fonctionnalité'; 

ALTER TABLE `droit_fonctionnalite` 
  ADD COLUMN `tem_action_cand_fonc` BIT(1) NOT NULL DEFAULT b'0' comment 
'témoin indiquant que la fonctionnalité est ouverte aux actions sur la candidature';

--
-- Mise à jour structure et données de la table `candidat` 
--
UPDATE `candidat` 
SET    `nom_pat_candidat` = Substring(`nom_pat_candidat`, 1, 30); 

ALTER TABLE `candidat` 
  CHANGE COLUMN `nom_pat_candidat` `nom_pat_candidat` VARCHAR(30) NOT NULL 
  comment 'nom patronymique du candidat'; 

UPDATE `candidat` 
SET    `nom_usu_candidat` = Substring(`nom_usu_candidat`, 1, 30); 

ALTER TABLE `candidat` 
  CHANGE COLUMN `nom_usu_candidat` `nom_usu_candidat` VARCHAR(30) NULL DEFAULT 
  NULL comment 'nom usuel du candidat'; 

UPDATE `candidat` 
SET    `prenom_candidat` = Substring(`prenom_candidat`, 1, 20); 

ALTER TABLE `candidat` 
  CHANGE COLUMN `prenom_candidat` `prenom_candidat` VARCHAR(20) NOT NULL comment 
  'prénom du candidat'; 

UPDATE `candidat` 
SET    `autre_pren_candidat` = Substring(`autre_pren_candidat`, 1, 20); 

ALTER TABLE `candidat` 
  CHANGE COLUMN `autre_pren_candidat` `autre_pren_candidat` VARCHAR(20) NULL 
  DEFAULT NULL comment 'autre prénom du candidat'; 

UPDATE `candidat` 
SET    `lib_ville_naiss_candidat` = Substring(`lib_ville_naiss_candidat`, 1, 30);

ALTER TABLE `candidat` 
  CHANGE COLUMN `lib_ville_naiss_candidat` `lib_ville_naiss_candidat` VARCHAR(30 
  ) NOT NULL comment 'ville de naissance du candidat'; 

--
-- Mise à jour structure et données de la table `compte_minima` 
--
UPDATE `compte_minima` 
SET    `nom_cpt_min` = Substring(`nom_cpt_min`, 1, 30); 

ALTER TABLE `compte_minima` 
  CHANGE COLUMN `nom_cpt_min` `nom_cpt_min` VARCHAR(30) NOT NULL comment 
  'nom du compte à minima'; 

UPDATE `compte_minima` 
SET    `prenom_cpt_min` = Substring(`prenom_cpt_min`, 1, 20); 

ALTER TABLE `compte_minima` 
  CHANGE COLUMN `prenom_cpt_min` `prenom_cpt_min` VARCHAR(20) NOT NULL comment 
  'prénom du compte à minima';

ALTER TABLE `compte_minima` 
  ADD COLUMN `tem_fc_cpt_min` BIT(1) NOT NULL comment 'témoin FC' after 
  `tem_valid_mail_cpt_min`; 

--
-- Mise à jour structure de la table `opi` 
--
ALTER TABLE `opi` 
  ADD COLUMN `cod_opi` VARCHAR(10) NULL comment 'code de l\'opi'; 

--
-- Mise à jour structure de la table `formation` 
--
ALTER TABLE `formation` 
  ADD COLUMN `tem_demat_form` BIT(1) NOT NULL comment 
'si les pièces justificatives seront dématérialisées ou non pour la formation' 
  after `tem_list_comp_form`; 

update `formation` 
set `tem_demat_form` = (select centre_candidature.tem_demat_ctr_cand from centre_candidature, commission where formation.id_comm = commission.id_comm and commission.id_ctr_cand = centre_candidature.id_ctr_cand);

--
-- Mise à jour structure de la table `piece_justif` 
--
ALTER TABLE `piece_justif` 
  ADD COLUMN `tem_unicite_pj` BIT(1) NOT NULL comment 
  'témoin pièce unique pour les pieces communes' after `tem_commun_pj`; 

ALTER TABLE `piece_justif` 
  ADD COLUMN `cod_apo_pj` VARCHAR(50) NULL comment 'code apogée de la pièce' 
  after `id_ctr_cand`; 

ALTER TABLE `piece_justif` 
  ADD COLUMN `order_pj` INT(10) NULL comment 'ordre d\'affichage de la pièce' 
  after `tem_conditionnel_pj`; 

--
-- Mise à jour structure de la table `type_decision` 
--
ALTER TABLE `type_decision` 
  CHANGE COLUMN `tes_typ_dec` `tes_typ_dec` BIT(1) NOT NULL comment 
  'temoin en service du type de decision' after `id_mail`; 

ALTER TABLE type_decision 
  ADD COLUMN `tem_aff_comment_typ_dec` BIT(1) NOT NULL DEFAULT b'1' comment 
  'témoin si le commentaire s\'affiche pour le candidat' after 
  `tem_model_typ_dec`;

--
-- Mise à jour structure de la table `commission` 
--
ALTER TABLE `commission` 
  ADD COLUMN `tem_alert_prop_comm` BIT(1) NOT NULL DEFAULT b'1' comment 
  'témoin indiquant que la commission reçoit les alertes de proposition' after 
  `tes_comm`; 

ALTER TABLE `commission` 
  ADD COLUMN `tem_alert_annul_comm` BIT(1) NOT NULL DEFAULT b'1' comment 
  'témoin indiquant que la commission reçoit les alertes d\'annulation' after 
  `tem_alert_prop_comm`; 

ALTER TABLE `commission` 
  ADD COLUMN `tem_alert_trans_comm` BIT(1) NOT NULL DEFAULT b'1' comment 
'témoin indiquant que la commission reçoit les alertes de transmission de dossier' 
  after `tem_alert_annul_comm`; 

ALTER TABLE `commission` 
  ADD COLUMN `id_fichier` INT(10) NULL comment 'fichier de la signature' after 
  `tes_comm`; 

ALTER TABLE `commission` 
  ADD COLUMN `signataire_comm` VARCHAR(255) NULL comment 'signataire' after 
  `tes_comm`; 

ALTER TABLE `commission` 
  ADD COLUMN `tem_edit_lettre_comm` BIT(1) NOT NULL DEFAULT b'0' comment 
'témoin indiquant que la commission autorise l\'edition des lettres d\'admission et de refus'
  after `id_fichier`; 

ALTER TABLE commission 
  ADD INDEX fk_fichier_commission_id_fichier (id_fichier), 
  ADD CONSTRAINT fk_fichier_commission_id_fichier FOREIGN KEY (id_fichier) 
  REFERENCES fichier (id_fichier); 

ALTER TABLE `commission` 
  ADD COLUMN `tem_mail_lettre_comm` BIT(1) NOT NULL DEFAULT b'0' comment 
'témoin indiquant que la commission autorise l\'envoi par mail des lettres d\'admission et de refus'
  after `tem_edit_lettre_comm`;