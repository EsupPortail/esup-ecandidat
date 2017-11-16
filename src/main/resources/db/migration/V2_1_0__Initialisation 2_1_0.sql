--
-- Initialisation de la base de données
--

--
-- Structure de la table `adresse`
--

CREATE TABLE `adresse` (
  `id_adr` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de l''adresse',
  `cod_pay` varchar(3) NOT NULL COMMENT 'code du pays de l''adresse',
  `cod_bdi_adr` varchar(5) DEFAULT NULL COMMENT 'code postal de l''adresse (fr)',
  `cod_com` varchar(5) DEFAULT NULL COMMENT 'code commune de l''adresse (fr)',
  `lib_com_etr_adr` varchar(32) DEFAULT NULL COMMENT 'libellé de la commune du candidat',
  `adr1_adr` varchar(32) DEFAULT NULL COMMENT 'libellé 1 de l''adresse',
  `adr2_adr` varchar(32) DEFAULT NULL COMMENT 'libellé 2 de l''adresse',
  `adr3_adr` varchar(32) DEFAULT NULL COMMENT 'libellé 3 de l''adresse',
  `cedex_adr` varchar(50) DEFAULT NULL COMMENT 'cedex de l''adresse',
  PRIMARY KEY (`id_adr`),
  KEY `fk_adresse_siscol_pays_cod_pay` (`cod_pay`),
  KEY `fk_adresse_siscol_commune_cod_com` (`cod_com`)
) ENGINE=InnoDB COMMENT='table des adresses';

-- --------------------------------------------------------

--
-- Structure de la table `batch`
--

CREATE TABLE `batch` (
  `cod_batch` varchar(30) NOT NULL COMMENT 'code du batch',
  `lib_batch` varchar(50) NOT NULL COMMENT 'libellé du batch',
  `tes_batch` bit(1) NOT NULL COMMENT 'témoin en service',
  `tem_is_launch_imedia_batch` bit(1) NOT NULL COMMENT 'témoin indiquant si le job vient d''etre lancé manuellement',
  `tem_lundi_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le lundi',
  `tem_mardi_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le mardi',
  `tem_mercr_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le mercredi',
  `tem_jeudi_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le jeudi',
  `tem_vendredi_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le vendredi',
  `tem_samedi_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le samedi',
  `tem_diman_batch` bit(1) NOT NULL COMMENT 'témoin si le lancement doit se faire le dimanche',
  `fixe_hour_batch` time NOT NULL COMMENT 'heure spécifique du lancement du job',
  `fixe_day_batch` int(10) DEFAULT NULL COMMENT 'jour de lancement du batch',
  `fixe_month_batch` int(10) DEFAULT NULL COMMENT 'mois de lancement du batch',
  `fixe_year_batch` int(10) DEFAULT NULL COMMENT 'jour fixe de lancement du batch',
  `last_dat_execution_batch` datetime DEFAULT NULL COMMENT 'date de la deniere execution',
  PRIMARY KEY (`cod_batch`)
) ENGINE=InnoDB COMMENT='table des batchs';

-- --------------------------------------------------------

--
-- Structure de la table `batch_histo`
--

CREATE TABLE `batch_histo` (
  `id_batch_histo` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de l''historique',
  `cod_batch` varchar(30) NOT NULL COMMENT 'code du batch',
  `date_deb_batch_histo` datetime DEFAULT NULL COMMENT 'date de début du batch',
  `date_fin_batch_histo` datetime DEFAULT NULL COMMENT 'date de fin de lancement',
  `state_batch_histo` varchar(10) DEFAULT NULL COMMENT 'état du batch',
  PRIMARY KEY (`id_batch_histo`),
  KEY `fk_batch_histo_batch_cod_batch` (`cod_batch`)
) ENGINE=InnoDB COMMENT='table d''historique de lancement des jobs';

-- --------------------------------------------------------

--
-- Structure de la table `batch_run`
--

CREATE TABLE `batch_run` (
  `dat_last_check_run` datetime NOT NULL COMMENT 'valeur du dernier run de batch',
  PRIMARY KEY (`dat_last_check_run`)
) ENGINE=InnoDB COMMENT='table du dernier run';

-- --------------------------------------------------------

--
-- Structure de la table `campagne`
--

CREATE TABLE `campagne` (
  `id_camp` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id de la campagne',
  `cod_camp` varchar(20) NOT NULL COMMENT 'code de la campagne',
  `lib_camp` varchar(50) NOT NULL COMMENT 'libellé de la campagne',
  `dat_deb_camp` date NOT NULL COMMENT 'date début de la campagne',
  `dat_fin_camp` date NOT NULL COMMENT 'date fin de la campagne',
  `tes_camp` bit(1) NOT NULL COMMENT 'temoin en service de la campagne',
  `dat_activat_prev_camp` datetime DEFAULT NULL COMMENT 'date time d''activation prévisionnel',
  `dat_activat_effec_camp` datetime DEFAULT NULL COMMENT 'date time d''activation effectif',
  `dat_archiv_camp` datetime DEFAULT NULL COMMENT 'date d''archivage effectif',
  `dat_destruct_effec_camp` datetime DEFAULT NULL COMMENT 'date time de destruction des dossier effectif',
  `archiv_id_camp` int(10) DEFAULT NULL COMMENT 'code de la campagne lié --> archivage/activation',
  PRIMARY KEY (`id_camp`),
  UNIQUE KEY `cod_camp` (`cod_camp`),
  KEY `campagne_id_camp_id_camp` (`archiv_id_camp`)
) ENGINE=InnoDB COMMENT='table des campagne de candidature';

-- --------------------------------------------------------

--
-- Structure de la table `candidat`
--

CREATE TABLE `candidat` (
  `id_candidat` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du candidat',
  `cod_civ` varchar(4) NOT NULL COMMENT 'code de la civilite',
  `nom_pat_candidat` varchar(50) NOT NULL COMMENT 'nom patronymique du candidat',
  `nom_usu_candidat` varchar(50) DEFAULT NULL COMMENT 'nom usuel du candidat',
  `prenom_candidat` varchar(50) NOT NULL COMMENT 'prénom du candidat',
  `autre_pren_candidat` varchar(50) DEFAULT NULL COMMENT 'autre prénom du candidat',
  `dat_naiss_candidat` date NOT NULL COMMENT 'date naissance du candidat',
  `cod_pay_nat` varchar(3) NOT NULL COMMENT 'nationalité du candidat',
  `ine_candidat` varchar(10) DEFAULT NULL COMMENT 'INE du candidat',
  `cle_ine_candidat` varchar(1) DEFAULT NULL COMMENT 'INE du candidat',
  `cod_pay_naiss` varchar(3) NOT NULL COMMENT 'code Apogee pays de naissance du candidat',
  `cod_dep_naiss_candidat` varchar(3) DEFAULT NULL COMMENT 'code Apogee departement de naissance candidat',
  `lib_ville_naiss_candidat` varchar(50) NOT NULL COMMENT 'ville de naissance du candidat',
  `id_adr` int(10) DEFAULT NULL COMMENT 'identifiant de l''adresse',
  `tel_candidat` varchar(20) DEFAULT NULL COMMENT 'numéro de téléphone du candidat',
  `tel_port_candidat` varchar(20) DEFAULT NULL COMMENT 'numéro de téléphone portable du candidat',
  `cod_langue` varchar(5) NOT NULL COMMENT 'code langue',
  `id_cpt_min` int(10) NOT NULL COMMENT 'id du compte a minima',
  `tem_updatable_candidat` bit(1) NOT NULL COMMENT 'temoin indiquant que les données proviennent d''apogee',
  PRIMARY KEY (`id_candidat`),
  UNIQUE KEY `id_cpt_min` (`id_cpt_min`),
  KEY `fk_candidat_langue_id_langue` (`cod_langue`),
  KEY `fk_candidat_adresse_id_adr` (`id_adr`),
  KEY `fk_candidat_siscol_pays_cod_pay_nat` (`cod_pay_nat`),
  KEY `fk_candidat_siscol_pays_cod_pays_naiss` (`cod_pay_naiss`),
  KEY `fk_siscol_departement_cod_dep_cod_dep_naiss` (`cod_dep_naiss_candidat`),
  KEY `fk_compte_minima_candidat_id_cpt_min` (`id_cpt_min`),
  KEY `fk_civilite_candidat_cod_civ` (`cod_civ`)
) ENGINE=InnoDB COMMENT='table des candidats';

-- --------------------------------------------------------

--
-- Structure de la table `candidature`
--

CREATE TABLE `candidature` (
  `id_cand` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la candidature',
  `id_form` int(10) NOT NULL COMMENT 'identifiant de la formation de la candidature',
  `id_candidat` int(10) NOT NULL COMMENT 'identifiant du candidat de la candidature',
  `cod_typ_trait` varchar(2) NOT NULL COMMENT 'type de traitement de la canidature',
  `tem_valid_typ_trait_cand` bit(1) NOT NULL COMMENT 'témoin indiquant que le type de traitement a été validé pour la candidature',
  `cod_typ_statut` varchar(2) NOT NULL COMMENT 'code du statut du dossier de la candidature',
  `tem_proposition_cand` bit(1) NOT NULL COMMENT 'témoin indiquant que la candidature est une proposition',
  `dat_cre_cand` datetime NOT NULL COMMENT 'date de création',
  `user_cre_cand` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_cand` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_cand` varchar(30) NOT NULL COMMENT 'user de modification',
  `dat_annul_cand` datetime DEFAULT NULL COMMENT 'date d''annulation de la candidature',
  `user_annul_cand` varchar(20) DEFAULT NULL COMMENT 'login d''annulation de la candidature (si null, c''est le candidat qui a supprimé)',
  `tem_accept_cand` bit(1) DEFAULT NULL COMMENT 'temoin si la candidature est asseptee : null->pas encore de choix, 0->refusé, 1->acceptee',
  `dat_accept_cand` datetime DEFAULT NULL COMMENT 'date d''acceptation ou de refus de la candidature',
  `user_accept_cand` varchar(20) DEFAULT NULL COMMENT 'login d''acceptation ou de refus de la candidature',
  `dat_opi_cand` datetime DEFAULT NULL COMMENT 'date de l''opi pour cette candidature',
  `dat_mod_typ_statut_cand` datetime DEFAULT NULL COMMENT 'date de modif du statut du dossier',
  `dat_trans_dossier_cand` datetime DEFAULT NULL COMMENT 'date de transmission du dossier',
  `dat_recept_dossier_cand` date DEFAULT NULL COMMENT 'date de réception du dossier',
  `dat_complet_dossier_cand` date DEFAULT NULL COMMENT 'date changement statut dossier à complet',
  `dat_incomplet_dossier_cand` date DEFAULT NULL COMMENT 'date changement statut dossier à incomplet',
  PRIMARY KEY (`id_cand`),
  KEY `fk_candidature_formation_id_form` (`id_form`),
  KEY `fk_candidature_candidat_id_candidat` (`id_candidat`),
  KEY `fk_candidature_type_traitement_cod_typ_trait` (`cod_typ_trait`),
  KEY `fk_candidature_type_statut_cod_typ_statut` (`cod_typ_statut`)
) ENGINE=InnoDB COMMENT='table des candidatures';

-- --------------------------------------------------------

--
-- Structure de la table `candidat_bac_ou_equ`
--

CREATE TABLE `candidat_bac_ou_equ` (
  `id_candidat` int(10) NOT NULL COMMENT 'identifiant du candidat',
  `cod_pay` varchar(3) DEFAULT NULL COMMENT 'code du pays',
  `cod_dep` varchar(3) DEFAULT NULL COMMENT 'code du departement',
  `cod_com` varchar(5) DEFAULT NULL COMMENT 'code de la commune',
  `cod_etb` varchar(8) DEFAULT NULL COMMENT 'code de l''etablissement',
  `annee_obt_bac` int(4) DEFAULT NULL COMMENT 'année d''obtention du bac',
  `cod_bac` varchar(4) NOT NULL COMMENT 'code du bac',
  `cod_mnb` varchar(2) DEFAULT NULL COMMENT 'code de la mention',
  `tem_updatable_bac` bit(1) NOT NULL COMMENT 'temoin indiquant si le bac a été récupéré d''apogee',
  PRIMARY KEY (`id_candidat`),
  KEY `fk_bac_ou_equ_candidat_id_candidat` (`id_candidat`),
  KEY `fk_bac_ou_equ_siscol_pays_cod_pay` (`cod_pay`),
  KEY `fk_bac_ou_equ_siscol_bac_ou_equ_cod_bac` (`cod_bac`),
  KEY `fk_bac_ou_equ_siscol_etablissement_cod_etb` (`cod_etb`),
  KEY `fk_bac_ou_equ_siscol_mention_niv_bac_cod_mnb` (`cod_mnb`),
  KEY `fk_bac_ou_equ_siscol_departement_cod_dep` (`cod_dep`),
  KEY `fk_bac_ou_equ_siscol_commune_cod_com` (`cod_com`)
) ENGINE=InnoDB COMMENT='table des bacs ou equ du candidat';

-- --------------------------------------------------------

--
-- Structure de la table `candidat_cursus_interne`
--

CREATE TABLE `candidat_cursus_interne` (
  `id_cursus_interne` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du cursus interne',
  `id_candidat` int(10) NOT NULL COMMENT 'identifiant du candidat',
  `annee_univ_cursus_interne` int(4) DEFAULT NULL COMMENT 'annee universitaire du cursus interne',
  `cod_vet_cursus_interne` varchar(100) DEFAULT NULL COMMENT 'code de la vet du cursus interne',
  `lib_cursus_interne` varchar(255) DEFAULT NULL COMMENT 'libelle du cursus interne',
  `cod_men_cursus_interne` varchar(2) DEFAULT NULL COMMENT 'code de la mention du cursus interne',
  `cod_tre_cursus_interne` varchar(4) DEFAULT NULL COMMENT 'temoin d''obention du cursus interne',
  PRIMARY KEY (`id_cursus_interne`),
  KEY `fk_candidat_cursus_interne_candidat_id_candidat` (`id_candidat`),
  KEY `fk_siscol_mention_cursus_interne_cod_men` (`cod_men_cursus_interne`),
  KEY `fk_siscol_typ_resultat_candidat_cursus_interne_cod_tre` (`cod_tre_cursus_interne`)
) ENGINE=InnoDB COMMENT='table des cursus interne du candidat';

-- --------------------------------------------------------

--
-- Structure de la table `candidat_cursus_post_bac`
--

CREATE TABLE `candidat_cursus_post_bac` (
  `id_cursus` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du cursus',
  `id_candidat` int(10) NOT NULL COMMENT 'identifiant du candidat',
  `cod_pay` varchar(3) NOT NULL COMMENT 'code du pays',
  `cod_dep` varchar(3) DEFAULT NULL COMMENT 'code du departement',
  `cod_com` varchar(5) DEFAULT NULL COMMENT 'code de la commune',
  `cod_etb` varchar(8) DEFAULT NULL COMMENT 'code de l''etablissement',
  `annee_univ_cursus` int(4) NOT NULL COMMENT 'année universitaire',
  `cod_dac` varchar(7) NOT NULL COMMENT 'code de diplome',
  `cod_men` varchar(2) DEFAULT NULL COMMENT 'code de mention',
  `lib_cursus` varchar(255) NOT NULL COMMENT 'libellé du cursus',
  `obtenu_cursus` varchar(1) NOT NULL COMMENT 'si le cursus a été obtenu',
  PRIMARY KEY (`id_cursus`),
  KEY `fk_cursus_siscol_pays_cod_pay` (`cod_pay`),
  KEY `fk_cursus_siscol_commune_cod_com` (`cod_com`),
  KEY `fk_cursus_siscol_departement_cod_dep` (`cod_dep`),
  KEY `fk_cursus_siscol_etablissement_cod_etb` (`cod_etb`),
  KEY `fk_cursus_siscol_dip_aut_cur_cod_dac` (`cod_dac`),
  KEY `fk_cursus_siscol_mention_cod_men` (`cod_men`),
  KEY `fk_cursus_post_bac_candidat_id_candidat` (`id_candidat`)
) ENGINE=InnoDB COMMENT='table des cursus post bac (interne et externe)';

-- --------------------------------------------------------

--
-- Structure de la table `candidat_cursus_pro`
--

CREATE TABLE `candidat_cursus_pro` (
  `id_cursus_pro` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du curusus',
  `id_candidat` int(10) NOT NULL COMMENT 'identifiant du candidat',
  `annee_cursus_pro` int(4) NOT NULL COMMENT 'année du curusus',
  `duree_cursus_pro` varchar(20) NOT NULL COMMENT 'durée du curusus',
  `organisme_cursus_pro` varchar(50) NOT NULL COMMENT 'organisme ou employeur du curusus',
  `intitule_cursus_pro` varchar(50) NOT NULL COMMENT 'intitulé du curusus',
  `objectif_cursus_pro` varchar(500) DEFAULT NULL COMMENT 'descriptif du curusus',
  PRIMARY KEY (`id_cursus_pro`),
  KEY `fk_cursus_pro_candidat_id_candidat` (`id_candidat`)
) ENGINE=InnoDB COMMENT='table des cursus pro ou stage';

-- --------------------------------------------------------

--
-- Structure de la table `candidat_stage`
--

CREATE TABLE `candidat_stage` (
  `id_stage` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du stage',
  `id_candidat` int(10) NOT NULL COMMENT 'identifiant du candidat',
  `annee_stage` int(4) NOT NULL COMMENT 'année du stage',
  `duree_stage` varchar(20) NOT NULL COMMENT 'durée du stage',
  `organisme_stage` varchar(50) NOT NULL COMMENT 'organisme ou employeur du stage',
  `descriptif_stage` varchar(500) NOT NULL COMMENT 'descriptif du stage',
  `nb_h_sem_stage` int(3) DEFAULT NULL COMMENT 'nb d''heure / semaine du stage',
  PRIMARY KEY (`id_stage`),
  KEY `fk_candidat_stage_candidat_id_candidat` (`id_candidat`)
) ENGINE=InnoDB COMMENT='table des stages d''un candidat';

-- --------------------------------------------------------

--
-- Structure de la table `centre_candidature`
--

CREATE TABLE `centre_candidature` (
  `id_ctr_cand` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du centre de candidature',
  `cod_ctr_cand` varchar(20) NOT NULL COMMENT 'code du centre de candidature',
  `lib_ctr_cand` varchar(200) NOT NULL COMMENT 'libellé du centre de candidature',
  `id_typ_dec_fav` int(10) NOT NULL COMMENT 'L’avis favorable à envoyer, lors de la validation des types de traitements, aux formations en Accès direct',
  `nb_max_voeux_ctr_cand` int(10) NOT NULL COMMENT 'nombre maximum de voeux pour ce centre de candidature',
  `tem_demat_ctr_cand` bit(1) NOT NULL COMMENT 'si les pièces justificatives seront dématérialisées ou non pour toutes les formations du centre de candidature',
  `tem_list_comp_ctr_cand` bit(1) NOT NULL COMMENT 'activation ou désactivation de la gestion automatique des listes complémentaires',
  `id_typ_dec_fav_list_comp` int(10) DEFAULT NULL COMMENT 'avis favorable à envoyer pour la gestion auto des liste complémentaires',
  `dat_deb_depot_ctr_cand` date NOT NULL COMMENT 'date de début de dépot des voeux',
  `dat_fin_depot_ctr_cand` date NOT NULL COMMENT 'date de fin de dépot des voeux',
  `dat_retour_ctr_cand` date NOT NULL COMMENT 'date limite de retour de dossier',
  `dat_confirm_ctr_cand` date DEFAULT NULL COMMENT 'date limite de confirmation',
  `dat_publi_ctr_cand` date DEFAULT NULL COMMENT 'date de publication des résultats',
  `dat_jury_ctr_cand` date DEFAULT NULL COMMENT 'date de jury',
  `dat_analyse_ctr_cand` date DEFAULT NULL COMMENT 'date de pré-analyse du dossier par défaut',
  `info_comp_ctr_cand` varchar(500) DEFAULT NULL COMMENT 'informations complémentaires par défaut pour les formations',
  `tes_ctr_cand` bit(1) NOT NULL COMMENT 'témoin en service du centre de candidature',
  `dat_cre_ctr_cand` datetime NOT NULL COMMENT 'date de création',
  `user_cre_ctr_cand` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_ctr_cand` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_ctr_cand` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_ctr_cand`),
  UNIQUE KEY `cod_ctr_cand` (`cod_ctr_cand`),
  KEY `fk_centre_candidature_typ_decision_id_typ_dec_fav` (`id_typ_dec_fav`),
  KEY `fk_centre_candidature_typ_decision_id_typ_dec_fav_list_comp` (`id_typ_dec_fav_list_comp`)
) ENGINE=InnoDB COMMENT='table des centres de candidatures';

-- --------------------------------------------------------

--
-- Structure de la table `civilite`
--

CREATE TABLE `civilite` (
  `cod_civ` varchar(4) NOT NULL COMMENT 'code de la civilité',
  `lib_civ` varchar(255) NOT NULL COMMENT 'libellé de la civilité',
  `cod_apo` varchar(1) NOT NULL COMMENT 'code apogée correspondant',
  PRIMARY KEY (`cod_civ`)
) ENGINE=InnoDB COMMENT='table des civilités';

-- --------------------------------------------------------

--
-- Structure de la table `commission`
--

CREATE TABLE `commission` (
  `id_comm` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la commission',
  `id_ctr_cand` int(10) NOT NULL COMMENT 'identifiant du centre de candidature de la commissio',
  `cod_comm` varchar(30) NOT NULL COMMENT 'code de la commission',
  `lib_comm` varchar(200) NOT NULL COMMENT 'libellé de la commission',
  `mail_comm` varchar(80) NOT NULL COMMENT 'mail de contact de la commission',
  `tel_comm` varchar(20) DEFAULT NULL COMMENT 'telephone de contact de la commission',
  `fax_comm` varchar(20) DEFAULT NULL COMMENT 'fax de la commission',
  `id_adr` int(10) NOT NULL COMMENT 'identifiant de l''adresse',
  `comment_retour_comm` varchar(500) DEFAULT NULL COMMENT 'commentaire lors du retour de dossier pour la commission',
  `tes_comm` bit(1) NOT NULL COMMENT 'temoin en service de la commission',
  `dat_cre_comm` datetime NOT NULL COMMENT 'date de création',
  `user_cre_comm` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_comm` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_comm` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_comm`),
  UNIQUE KEY `cod_comm` (`cod_comm`),
  KEY `fk_commission_centre_candidature_id_ctr_cand` (`id_ctr_cand`),
  KEY `fk_commission_adresse_id_adr` (`id_adr`)
) ENGINE=InnoDB COMMENT='table des commissions';

-- --------------------------------------------------------

--
-- Structure de la table `commission_membre`
--

CREATE TABLE `commission_membre` (
  `id_droit_profil_ind` int(10) NOT NULL COMMENT 'identifiant du profil individu',
  `id_comm` int(10) NOT NULL COMMENT 'identifiant de la commission',
  `tem_is_president` bit(1) NOT NULL COMMENT 'temoin is president',
  PRIMARY KEY (`id_droit_profil_ind`),
  KEY `fk_commission_membre_commission_id_comm` (`id_comm`),
  KEY `fk_droit_profil_ind_commission_membre_id_droit_profil_ind` (`id_droit_profil_ind`)
) ENGINE=InnoDB COMMENT='table des membres de commissions';

-- --------------------------------------------------------

--
-- Structure de la table `compte_minima`
--

CREATE TABLE `compte_minima` (
  `id_cpt_min` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du compte à minima',
  `id_camp` int(10) NOT NULL COMMENT 'id de la campagne',
  `mail_perso_cpt_min` varchar(80) NOT NULL COMMENT 'mail perso du compte à minima',
  `nom_cpt_min` varchar(50) NOT NULL COMMENT 'nom du compte à minima',
  `prenom_cpt_min` varchar(50) NOT NULL COMMENT 'prénom du compte à minima',
  `num_dossier_opi_cpt_min` varchar(8) NOT NULL COMMENT 'identifiant du candidat : 6 caractères alphanumériques aléatoires en majuscule préfixés par deux lettres paramétrées au niveau de l’application',
  `login_cpt_min` varchar(30) DEFAULT NULL COMMENT 'login du candidat',
  `pwd_cpt_min` varchar(150) NOT NULL COMMENT 'mot de passe du compte à minima',
  `supann_etu_id_cpt_min` varchar(30) DEFAULT NULL COMMENT 'etuId provenant du ldap',
  `tem_valid_cpt_min` bit(1) NOT NULL COMMENT 'témoin de validité du compte à minima',
  `tem_valid_mail_cpt_min` bit(1) NOT NULL COMMENT 'témoin de validité du mail du compte à minima',
  `dat_fin_valid_cpt_min` datetime NOT NULL COMMENT 'date de fin de validite du compte a minima',
  `dat_cre_cpt_min` datetime NOT NULL COMMENT 'date de création',
  PRIMARY KEY (`id_cpt_min`),
  UNIQUE KEY `num_dossier_opi_cpt_min` (`num_dossier_opi_cpt_min`),
  KEY `fk_camp_cpt_min_id_camp` (`id_camp`)
) ENGINE=InnoDB COMMENT='table des comptes à minima';

-- --------------------------------------------------------

--
-- Structure de la table `droit_fonctionnalite`
--

CREATE TABLE `droit_fonctionnalite` (
  `cod_fonc` varchar(20) NOT NULL COMMENT 'code de la fonctionnalité',
  `lib_fonc` varchar(255) NOT NULL COMMENT 'libellé de la fonctionnalité',
  PRIMARY KEY (`cod_fonc`)
) ENGINE=InnoDB COMMENT='table des fonctionnalités';

-- --------------------------------------------------------

--
-- Structure de la table `droit_profil`
--

CREATE TABLE `droit_profil` (
  `id_profil` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du profil',
  `cod_profil` varchar(20) NOT NULL COMMENT 'code du profil',
  `lib_profil` varchar(255) NOT NULL COMMENT 'libellé du profil',
  `tem_admin_profil` bit(1) NOT NULL COMMENT 'témoin indiquant que le profil est administrateur tech ou scol centrale',
  `tem_ctr_cand_profil` bit(1) NOT NULL COMMENT 'témoin indiquant que le profil est scol central',
  `tem_updatable` bit(1) NOT NULL COMMENT 'temoin pour savoir si le droit est modifiable',
  `tes_profil` bit(1) NOT NULL COMMENT 'témoin en service du profil',
  `dat_cre_profil` datetime NOT NULL COMMENT 'date de création',
  `user_cre_profil` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_profil` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_profil` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_profil`),
  UNIQUE KEY `cod_profil` (`cod_profil`)
) ENGINE=InnoDB COMMENT='table des profils';

-- --------------------------------------------------------

--
-- Structure de la table `droit_profil_fonc`
--

CREATE TABLE `droit_profil_fonc` (
  `id_profil` int(10) NOT NULL COMMENT 'id du profil',
  `cod_fonc` varchar(20) NOT NULL COMMENT 'code de la fonctionnalité',
  `tem_read_only` bit(1) NOT NULL COMMENT 'temoin si la fonctionnalité est en read-only pour ce profil',
  PRIMARY KEY (`id_profil`,`cod_fonc`),
  KEY `fk_droit_profil_fonc_droit_fonctionnalite_cod_fonc` (`cod_fonc`),
  KEY `fk_droit_profil_fonc_droit_profil_id_profil` (`id_profil`)
) ENGINE=InnoDB COMMENT='table d''association droit fonctionnalité';

-- --------------------------------------------------------

--
-- Structure de la table `droit_profil_ind`
--

CREATE TABLE `droit_profil_ind` (
  `id_droit_profil_ind` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du user profil',
  `login_ind` varchar(50) NOT NULL COMMENT 'login de l''individu',
  `id_profil` int(10) NOT NULL COMMENT 'identifiant du profil',
  PRIMARY KEY (`id_droit_profil_ind`),
  KEY `fk_droit_user_profil_droit_profil_id_profil` (`id_profil`),
  KEY `fk_droit_profil_ind_individu_login_ind` (`login_ind`)
) ENGINE=InnoDB COMMENT='table des profils associé à des logins';

-- --------------------------------------------------------

--
-- Structure de la table `faq`
--

CREATE TABLE `faq` (
  `id_faq` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id element faq',
  `lib_faq` varchar(50) NOT NULL COMMENT 'libellé element faq',
  `id_i18n_question_faq` int(10) NOT NULL COMMENT 'id18n traduction question',
  `id_i18n_reponse_faq` int(10) NOT NULL COMMENT 'id18n traduction reponse',
  `order_faq` int(10) NOT NULL COMMENT 'ordre d''affichage de la question',
  PRIMARY KEY (`id_faq`),
  KEY `fk_faq_i18n_id_i18n_question_faq` (`id_i18n_question_faq`),
  KEY `fk_faq_i18n_id_i18n_reponse_faq` (`id_i18n_reponse_faq`)
) ENGINE=InnoDB COMMENT='table des elements FAQ';

-- --------------------------------------------------------

--
-- Structure de la table `fichier`
--

CREATE TABLE `fichier` (
  `id_fichier` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du fichier',
  `cod_fichier` varchar(50) NOT NULL COMMENT 'le code du fichier composé de son type de la pièce et de l''id de la pièce',
  `nom_fichier` varchar(100) NOT NULL COMMENT 'le fichier lui même',
  `file_fichier` varchar(500) NOT NULL COMMENT 'le libellé du fichier',
  `typ_fichier` char(1) NOT NULL COMMENT 'le type de fichier G = gestionnaire, C=candidat',
  `typ_stockage_fichier` char(1) NOT NULL COMMENT 'type de stockage de fichier-->soit fileSystem soit cmis',
  `auteur_fichier` varchar(50) NOT NULL COMMENT 'l''auteur du fichier',
  `dat_cre_fichier` datetime NOT NULL COMMENT 'date de création',
  PRIMARY KEY (`id_fichier`)
) ENGINE=InnoDB COMMENT='table des fichiers';

-- --------------------------------------------------------

--
-- Structure de la table `formation`
--

CREATE TABLE `formation` (
  `id_form` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la formation',
  `id_comm` int(10) NOT NULL COMMENT 'identifiant de la commission de la formation',
  `cod_typ_trait` varchar(2) NOT NULL COMMENT 'type de traitement de la formation',
  `cod_form` varchar(20) NOT NULL COMMENT 'code eCandidat de la formation',
  `lib_form` varchar(200) NOT NULL COMMENT 'libellé eCandidat de la formation',
  `cod_etp_vet_apo_form` varchar(20) DEFAULT NULL COMMENT 'code etape apogée de la formation',
  `cod_vrs_vet_apo_form` varchar(20) DEFAULT NULL COMMENT 'code version etape apogée de la formation',
  `lib_apo_form` varchar(120) DEFAULT NULL COMMENT 'libellé apogée de la formation',
  `id_typ_dec_fav` int(10) NOT NULL COMMENT 'avis favorable à envoyer, lors de la validation des types de traitements, aux formations en Accès direct',
  `tem_list_comp_form` bit(1) NOT NULL COMMENT 'activation ou désactivation de la gestion automatique des listes complémentaires',
  `id_typ_dec_fav_list_comp` int(10) DEFAULT NULL COMMENT 'avis favorable à envoyer pour la gestion auto des liste complémentaires',
  `dat_deb_depot_form` date NOT NULL COMMENT 'date de début de dépot des voeux',
  `dat_fin_depot_form` date NOT NULL COMMENT 'date de fin de dépot des voeux',
  `dat_retour_form` date NOT NULL COMMENT 'date limite de retour de dossier',
  `dat_confirm_form` date DEFAULT NULL COMMENT 'date limite de confirmation',
  `dat_publi_form` date DEFAULT NULL COMMENT 'date de publication des résultats',
  `dat_jury_form` date DEFAULT NULL COMMENT 'date de jury',
  `dat_analyse_form` date DEFAULT NULL COMMENT 'date de pré-analyse du dossier',
  `cod_cge` varchar(3) NOT NULL COMMENT 'code CGE rattaché',
  `mot_cle_form` varchar(500) DEFAULT NULL COMMENT 'mots clé pour la recherche dans l''offre de formation',
  `cod_tpd_etb` varchar(2) NOT NULL COMMENT 'type de diplome associé',
  `preselect_date_form` date DEFAULT NULL COMMENT 'date de l''épreuve de sélection par défaut',
  `preselect_heure_form` time DEFAULT NULL COMMENT 'heure de l''épreuve de sélection par défaut',
  `preselect_lieu_form` varchar(100) DEFAULT NULL COMMENT 'lieu de l''épreuve de sélection par défaut',
  `info_comp_form` varchar(500) DEFAULT NULL COMMENT 'informations complémentaires de la formation',
  `tes_form` bit(1) NOT NULL COMMENT 'témoin en service',
  `dat_cre_form` datetime NOT NULL COMMENT 'date de création',
  `user_cre_form` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_form` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_form` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_form`),
  UNIQUE KEY `cod_form` (`cod_form`),
  KEY `fk_formation_type_decision_id_typ_dec_fav_list_comp` (`id_typ_dec_fav_list_comp`),
  KEY `fk_formation_type_decision_id_typ_dec_fav_form` (`id_typ_dec_fav`),
  KEY `fk_formation_commission_id_comm` (`id_comm`),
  KEY `fk_formation_siscol_typ_diplome_cod_tpd_etb` (`cod_tpd_etb`),
  KEY `fk_formation_siscol_centre_gestion_cod_cge` (`cod_cge`),
  KEY `fk_formation_type_traitement_cod_typ_trait` (`cod_typ_trait`)
) ENGINE=InnoDB COMMENT='table des formations';

-- --------------------------------------------------------

--
-- Structure de la table `formulaire`
--

CREATE TABLE `formulaire` (
  `id_formulaire` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du formulaire',
  `id_formulaire_limesurvey` int(11) NOT NULL COMMENT 'identifiant limesurvey du formulaire',
  `cod_formulaire` varchar(20) NOT NULL COMMENT 'code du formulaire',
  `lib_formulaire` varchar(50) NOT NULL COMMENT 'libellé du formulaire',
  `id_i18n_lib_formulaire` int(10) NOT NULL COMMENT 'identifiant i18n du libellé du formulaire',
  `id_i18n_url_formulaire` int(10) NOT NULL COMMENT 'identifiant i18n de l''url du formulaire',
  `tes_formulaire` bit(1) NOT NULL COMMENT 'temoin en service du formulaire',
  `tem_conditionnel_formulaire` bit(1) NOT NULL COMMENT 'temoin de piece conditionnelle du formulaire',
  `tem_commun_formulaire` bit(1) NOT NULL COMMENT 'temoin de commun a toute les formation du formulaire',
  `id_ctr_cand` int(10) DEFAULT NULL COMMENT 'identifiant du centre de canidature',
  `dat_cre_formulaire` datetime NOT NULL COMMENT 'date de création',
  `user_cre_formulaire` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_formulaire` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_formulaire` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_formulaire`),
  UNIQUE KEY `cod_formulaire` (`cod_formulaire`),
  KEY `fk_i18n_formulaire_id18n_lib` (`id_i18n_lib_formulaire`),
  KEY `fk_i18n_formulaire_id18n_url` (`id_i18n_url_formulaire`),
  KEY `fk_centre_candidature_formulaire_id_ctr_cand` (`id_ctr_cand`)
) ENGINE=InnoDB COMMENT='table des formulaires';

-- --------------------------------------------------------

--
-- Structure de la table `formulaire_cand`
--

CREATE TABLE `formulaire_cand` (
  `id_formulaire` int(10) NOT NULL COMMENT 'identifiant du formulaire',
  `id_cand` int(10) NOT NULL COMMENT 'identifiant de la candidature',
  `cod_typ_statut_piece` varchar(2) NOT NULL COMMENT 'statut de la pièce',
  `dat_cre_formulaire_cand` datetime NOT NULL COMMENT 'date de création',
  `user_cre_formulaire_cand` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_reponse_formulaire_cand` datetime DEFAULT NULL COMMENT 'date de réponse du formulaire',
  `dat_mod_formulaire_cand` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_formulaire_cand` varchar(30) NOT NULL COMMENT 'user de modification',
  `reponses_formulaire_cand` text COMMENT 'les réponses du candidat',
  PRIMARY KEY (`id_formulaire`,`id_cand`),
  KEY `fk_formulaire_cand_typ_statut_piece_cod` (`cod_typ_statut_piece`),
  KEY `fk_formulaire_cand_candidature_id_cand` (`id_cand`),
  KEY `fk_formulaire_cand_formulaire_id_formulaire` (`id_formulaire`)
) ENGINE=InnoDB COMMENT='table des formulaires de la candidature';

-- --------------------------------------------------------

--
-- Structure de la table `formulaire_form`
--

CREATE TABLE `formulaire_form` (
  `id_formulaire` int(10) NOT NULL COMMENT 'identifiant du formulaire',
  `id_form` int(10) NOT NULL COMMENT 'identifiant de la formation',
  PRIMARY KEY (`id_formulaire`,`id_form`),
  KEY `fk_formulaire_formation_pj_id_pj` (`id_formulaire`),
  KEY `fk_formulaire_form_formation_id_form` (`id_form`)
) ENGINE=InnoDB COMMENT='table de jointure formulaires-formations';

-- --------------------------------------------------------

--
-- Structure de la table `gestionnaire`
--

CREATE TABLE `gestionnaire` (
  `id_droit_profil_ind` int(10) NOT NULL COMMENT 'identifiant du profil individu',
  `id_ctr_cand` int(10) NOT NULL COMMENT 'identifiant du centre de candidature',
  `login_apo_gest` varchar(20) DEFAULT NULL COMMENT 'login apogee eventuel du gestionnaire',
  `cod_cge` varchar(3) DEFAULT NULL COMMENT 'code cge du gestionnaire',
  `tem_all_comm_gest` bit(1) NOT NULL COMMENT 'témoin si le gestionnaire est gestionnaire de toutes les commissions du centre de candidature',
  PRIMARY KEY (`id_droit_profil_ind`),
  KEY `fk_centre_candidature_gestionnaire_id_ctr_cand` (`id_ctr_cand`),
  KEY `fk_gestionnaire_droit_profil_ind_id_droit_profil_ind` (`id_droit_profil_ind`),
  KEY `fk_gestionnaire_cge_cod_cge` (`cod_cge`)
) ENGINE=InnoDB COMMENT='table des gestionnaires de ctr candidature';

-- --------------------------------------------------------

--
-- Structure de la table `gestionnaire_commission`
--

CREATE TABLE `gestionnaire_commission` (
  `id_droit_profil_ind` int(10) NOT NULL COMMENT 'id du profil gestionnaire',
  `id_comm` int(10) NOT NULL COMMENT 'id de la commission',
  KEY `gestionnaire_gestionnaire_commission_id_droit_profil_ind` (`id_droit_profil_ind`),
  KEY `commission_gestionnaire_commission_id_comm` (`id_comm`)
) ENGINE=InnoDB COMMENT='table des gestionnaires de commission';

-- --------------------------------------------------------

--
-- Structure de la table `histo_num_dossier`
--

CREATE TABLE `histo_num_dossier` (
  `num_dossier` varchar(8) NOT NULL COMMENT 'identifiant du candidat : 6 caractères alphanumériques aléatoires en majuscule préfixés par deux lettres paramétrées au niveau de l’application',
  `cod_camp` varchar(20) NOT NULL COMMENT 'code de la campagne',
  PRIMARY KEY (`num_dossier`)
) ENGINE=InnoDB COMMENT='table d''historique des numéro de dossier';

-- --------------------------------------------------------

--
-- Structure de la table `i18n`
--

CREATE TABLE `i18n` (
  `id_i18n` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant i18n',
  `cod_typ_trad` varchar(20) NOT NULL COMMENT 'code du type de traduction',
  PRIMARY KEY (`id_i18n`),
  KEY `fk_type_traduction_cod_typ_trad` (`cod_typ_trad`)
) ENGINE=InnoDB COMMENT='tables des identifiants i18n';

-- --------------------------------------------------------

--
-- Structure de la table `i18n_traduction`
--

CREATE TABLE `i18n_traduction` (
  `id_i18n` int(10) NOT NULL COMMENT 'le code (identifiant) de la traduction',
  `cod_langue` varchar(5) NOT NULL COMMENT 'code de la langue',
  `val_trad` longtext NOT NULL COMMENT 'la valeur de la traduction',
  PRIMARY KEY (`id_i18n`,`cod_langue`),
  KEY `fk_traduction_langue_id_langue` (`cod_langue`),
  KEY `fk_traduction_i18n_id_i18n` (`id_i18n`)
) ENGINE=InnoDB COMMENT='tables des traductions';

-- --------------------------------------------------------

--
-- Structure de la table `individu`
--

CREATE TABLE `individu` (
  `login_ind` varchar(50) NOT NULL COMMENT 'login de l''individu',
  `libelle_ind` varchar(255) NOT NULL COMMENT 'displayName de l''individu',
  `mail_ind` varchar(255) DEFAULT NULL COMMENT 'mail de l''individu',
  PRIMARY KEY (`login_ind`)
) ENGINE=InnoDB COMMENT='table des individus';

-- --------------------------------------------------------

--
-- Structure de la table `langue`
--

CREATE TABLE `langue` (
  `cod_langue` varchar(5) NOT NULL COMMENT 'code de la langue (important, utile aux traductions fr, en, etc..)',
  `lib_langue` varchar(20) NOT NULL COMMENT 'libellé de la langue',
  `tem_defaut_langue` bit(1) NOT NULL COMMENT 'témoin de la langue par défaut de l''application',
  `tes_langue` bit(1) NOT NULL COMMENT 'temoin en service',
  PRIMARY KEY (`cod_langue`)
) ENGINE=InnoDB COMMENT='table des langues';

-- --------------------------------------------------------

--
-- Structure de la table `load_balancing_reload`
--

CREATE TABLE `load_balancing_reload` (
  `cod_data_lb_reload` varchar(20) NOT NULL COMMENT 'code de la donnée a recharger',
  `dat_cre_lb_reload` datetime NOT NULL COMMENT 'date de création du chargement',
  PRIMARY KEY (`cod_data_lb_reload`)
) ENGINE=InnoDB COMMENT='table des éléments à recharger';

-- --------------------------------------------------------

--
-- Structure de la table `load_balancing_reload_run`
--

CREATE TABLE `load_balancing_reload_run` (
  `dat_last_check_lb_reload_run` datetime NOT NULL COMMENT 'deniere verification par instance',
  `instance_id_lb_reload_run` varchar(20) NOT NULL COMMENT 'id de l''instance',
  PRIMARY KEY (`dat_last_check_lb_reload_run`,`instance_id_lb_reload_run`)
) ENGINE=InnoDB COMMENT='table de besoin de reload de liste en cache';

-- --------------------------------------------------------

--
-- Structure de la table `lock_candidat`
--

CREATE TABLE `lock_candidat` (
  `num_dossier_opi_cpt_min` varchar(8) NOT NULL COMMENT 'le numero de dossier du compte a minima locké',
  `ressource_lock` varchar(40) NOT NULL COMMENT 'la ressource du compte a minima lockée',
  `ui_id_lock` varchar(500) NOT NULL COMMENT 'l''id de l''ui ayant locké la ressource',
  `instance_id_lock` varchar(20) NOT NULL COMMENT 'l''instance de l''application',
  `dat_lock` datetime NOT NULL COMMENT 'date du lock',
  PRIMARY KEY (`num_dossier_opi_cpt_min`,`ressource_lock`)
) ENGINE=InnoDB COMMENT='table des locks eCandidat';

-- --------------------------------------------------------

--
-- Structure de la table `mail`
--

CREATE TABLE `mail` (
  `id_mail` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du mail',
  `cod_typ_avis` varchar(2) DEFAULT NULL COMMENT 'type de décision lié au mail',
  `cod_mail` varchar(30) NOT NULL COMMENT 'code du mail',
  `lib_mail` varchar(50) NOT NULL COMMENT 'libelle du mail',
  `tem_is_modele_mail` bit(1) NOT NULL COMMENT 'temoin si le mail est un modele de mail',
  `tes_mail` bit(1) NOT NULL COMMENT 'temoin en service',
  `id_i18n_sujet_mail` int(10) NOT NULL COMMENT 'identifiant i18n du sujet du mail',
  `id_i18n_corps_mail` int(10) NOT NULL COMMENT 'identifiant i18n du corps du mail',
  `dat_cre_mail` datetime NOT NULL COMMENT 'date de création',
  `user_cre_mail` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_mail` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_mail` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_mail`),
  UNIQUE KEY `cod_mail` (`cod_mail`),
  KEY `fk_mail_type_avis_cod_typ_avis` (`cod_typ_avis`),
  KEY `fk_mail_i18n_id_i18n_sujet_mail` (`id_i18n_sujet_mail`),
  KEY `fk_mail_i18n_id_i18n_corps_mail` (`id_i18n_corps_mail`)
) ENGINE=InnoDB COMMENT='table des mails';

-- --------------------------------------------------------

--
-- Structure de la table `motivation_avis`
--

CREATE TABLE `motivation_avis` (
  `id_motiv` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la motivation d''avis',
  `cod_motiv` varchar(20) NOT NULL COMMENT 'code de la motivation d''avis',
  `lib_motiv` varchar(50) NOT NULL COMMENT 'libellé de la motivation',
  `id_i18n_lib_motiv` int(10) NOT NULL COMMENT 'identifiant i18n de la motivation d''avis',
  `tes_motiv` bit(1) NOT NULL COMMENT 'témoin en service de la motivation d''avis',
  `dat_cre_motiv` datetime NOT NULL COMMENT 'date de création',
  `user_cre_motiv` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_motiv` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_motiv` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_motiv`),
  UNIQUE KEY `cod_motiv` (`cod_motiv`),
  KEY `fk_motivation_avis_i18n_id_i18n_lib_motiv` (`id_i18n_lib_motiv`)
) ENGINE=InnoDB COMMENT='table des motivations d''avis';

-- --------------------------------------------------------

--
-- Structure de la table `opi`
--

CREATE TABLE `opi` (
  `id_cand` int(10) NOT NULL COMMENT 'identifiant de la candidature',
  `dat_cre_opi` datetime NOT NULL COMMENT 'date de création',
  `dat_passage_opi` datetime DEFAULT NULL COMMENT 'date de création de l''opi',
  PRIMARY KEY (`id_cand`),
  KEY `fk_cand_opi_id_cand` (`id_cand`)
) ENGINE=InnoDB COMMENT='table des opi en attente';

-- --------------------------------------------------------

--
-- Structure de la table `parametre`
--

CREATE TABLE `parametre` (
  `cod_param` varchar(30) NOT NULL COMMENT 'code du paramètre',
  `lib_param` varchar(200) NOT NULL COMMENT 'libellé du paramètre',
  `val_param` varchar(100) NOT NULL COMMENT 'valeur du paramètre',
  `typ_param` varchar(20) NOT NULL COMMENT 'type du paramètre',
  PRIMARY KEY (`cod_param`)
) ENGINE=InnoDB COMMENT='table des paramètres';

-- --------------------------------------------------------

--
-- Structure de la table `piece_justif`
--

CREATE TABLE `piece_justif` (
  `id_pj` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la pièce justificative',
  `cod_pj` varchar(20) NOT NULL COMMENT 'code de la pièce justificative',
  `lib_pj` varchar(50) NOT NULL COMMENT 'libellé de la pj',
  `tes_pj` bit(1) NOT NULL COMMENT 'témoin en service de la pièce justificative',
  `id_i18n_lib_pj` int(10) NOT NULL COMMENT 'identifiant i18n du libellé de la pj',
  `id_fichier` int(10) DEFAULT NULL COMMENT 'fichier eventuel rattaché à la pièce',
  `tem_commun_pj` bit(1) NOT NULL COMMENT 'témoin commun à toutes les formations de la pièce justificative',
  `tem_conditionnel_pj` bit(1) NOT NULL COMMENT 'témoin de pièce conditionnel de la pièce justificative',
  `id_ctr_cand` int(10) DEFAULT NULL COMMENT 'identifiant du centre de candidature éventuel',
  `dat_cre_pj` datetime NOT NULL COMMENT 'date de création',
  `user_cre_pj` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_pj` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_pj` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_pj`),
  UNIQUE KEY `cod_pj` (`cod_pj`),
  KEY `fk_piece_justif_ctr_cand_id_ctr_cand` (`id_ctr_cand`),
  KEY `fk_piece_justif_i18n_id_i18n_lib_pj` (`id_i18n_lib_pj`),
  KEY `fk_fichier_piece_justif_id_fichier` (`id_fichier`)
) ENGINE=InnoDB COMMENT='table des pièces justificatives';

-- --------------------------------------------------------

--
-- Structure de la table `pj_cand`
--

CREATE TABLE `pj_cand` (
  `id_pj` int(10) NOT NULL COMMENT 'identifiant de la pj',
  `id_cand` int(10) NOT NULL COMMENT 'identifiant de la candidature',
  `id_fichier` int(10) DEFAULT NULL COMMENT 'fichier en demat''',
  `cod_typ_statut_piece` varchar(2) NOT NULL COMMENT 'statut de la pièce',
  `lib_file_pj_cand` varchar(500) DEFAULT NULL COMMENT 'libellé de la pj déposée par le candidat',
  `comment_pj_cand` varchar(500) DEFAULT NULL COMMENT 'commentaire éventuel du gestionnaire',
  `dat_cre_pj_cand` datetime NOT NULL COMMENT 'date de création',
  `user_cre_pj_cand` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_pj_cand` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_pj_cand` varchar(30) NOT NULL COMMENT 'user de modification',
  `dat_mod_statut_pj_cand` datetime DEFAULT NULL COMMENT 'date de modif du statut de la piece',
  `user_mod_statut_pj_cand` varchar(30) DEFAULT NULL COMMENT 'user de modif de statut de la piece',
  PRIMARY KEY (`id_pj`,`id_cand`),
  KEY `fk_pj_cand_type_statut_piece_cod` (`cod_typ_statut_piece`),
  KEY `fk_cand_pj_form_cand_pk` (`id_cand`),
  KEY `fk_pj_cand_pj_id_pj` (`id_pj`),
  KEY `fk_fichier_pj_cand_id_fichier` (`id_fichier`)
) ENGINE=InnoDB COMMENT='table des pj déposées par le candidat pour une formation';

-- --------------------------------------------------------

--
-- Structure de la table `pj_form`
--

CREATE TABLE `pj_form` (
  `id_pj` int(10) NOT NULL COMMENT 'identifiant de la pj',
  `id_form` int(10) NOT NULL COMMENT 'identifiant de la formation',
  PRIMARY KEY (`id_pj`,`id_form`),
  KEY `fk_pj_form_form_id_form` (`id_form`),
  KEY `fk_pj_form_pj_id_pj` (`id_pj`)
) ENGINE=InnoDB COMMENT='table associative liant des pièces et des formations';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_annee_uni`
--

CREATE TABLE `siscol_annee_uni` (
  `cod_anu` varchar(4) NOT NULL COMMENT 'Code Annee Universitaire',
  `eta_anu_iae` varchar(1) NOT NULL COMMENT 'Etat de l''Annee Universitaire pour l''Inscription Administrative',
  `lib_anu` varchar(40) NOT NULL COMMENT 'Libelle Long Annee Universitaire',
  `lic_anu` varchar(10) NOT NULL COMMENT 'Libelle Court Annee Universitaire',
  PRIMARY KEY (`cod_anu`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Table des années universitaires';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_bac_oux_equ`
--

CREATE TABLE `siscol_bac_oux_equ` (
  `cod_bac` varchar(4) NOT NULL COMMENT 'Code Baccalaureat ou Equivalence',
  `lib_bac` varchar(40) NOT NULL COMMENT 'Libelle Long Baccalaureat ou Equivalence',
  `lic_bac` varchar(10) NOT NULL COMMENT 'Libelle Court Baccalaureat ou Equivalence',
  `daa_deb_vld_bac` varchar(4) DEFAULT NULL COMMENT 'Date de début de validité du bac',
  `daa_fin_vld_bac` varchar(4) DEFAULT NULL COMMENT 'Date de fin de validité du bac',
  `tem_nat_bac` bit(1) NOT NULL COMMENT 'Temoin Nature de Bac',
  `tem_en_sve_bac` bit(1) DEFAULT NULL COMMENT 'témoin en service',
  PRIMARY KEY (`cod_bac`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Baccalaureats ou equivalences';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_centre_gestion`
--

CREATE TABLE `siscol_centre_gestion` (
  `cod_cge` varchar(3) NOT NULL COMMENT 'Code Centre de Gestion',
  `lib_cge` varchar(40) NOT NULL COMMENT 'Libelle Long Centre de Gestion',
  `lic_cge` varchar(10) NOT NULL COMMENT 'Libelle Court Centre de Gestion',
  `tem_en_sve_cge` bit(1) NOT NULL COMMENT 'Temoin Code en Service',
  PRIMARY KEY (`cod_cge`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Centres de gestion';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_commune`
--

CREATE TABLE `siscol_commune` (
  `cod_com` varchar(5) NOT NULL COMMENT 'Code INSEE Commune',
  `cod_dep` varchar(3) NOT NULL COMMENT 'Code Departement',
  `lib_com` varchar(32) NOT NULL COMMENT 'libelle Long Commune',
  `tem_en_sve_com` bit(1) NOT NULL COMMENT 'Temoin en Service',
  PRIMARY KEY (`cod_com`),
  KEY `fk_siscol_departement_commune_cod_dep` (`cod_dep`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Table des communes';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_com_bdi`
--

CREATE TABLE `siscol_com_bdi` (
  `cod_com` varchar(5) NOT NULL COMMENT 'code de la commune',
  `cod_bdi` varchar(5) NOT NULL COMMENT 'code postal',
  PRIMARY KEY (`cod_com`,`cod_bdi`)
) ENGINE=InnoDB COMMENT='Referentiel SiScol : association code commune, code postal';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_departement`
--

CREATE TABLE `siscol_departement` (
  `cod_dep` varchar(3) NOT NULL COMMENT 'Code Departement',
  `lib_dep` varchar(40) NOT NULL COMMENT 'Libelle Long Departement',
  `lic_dep` varchar(10) NOT NULL COMMENT 'Libelle Court Departement',
  `tem_en_sve_dep` bit(1) NOT NULL COMMENT 'Temoin Code en Service',
  PRIMARY KEY (`cod_dep`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Table des departements';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_dip_aut_cur`
--

CREATE TABLE `siscol_dip_aut_cur` (
  `cod_dac` varchar(7) NOT NULL COMMENT 'Code Diplome Autre Cursus',
  `lib_dac` varchar(60) NOT NULL COMMENT 'Libelle Long Diplome Autre Cursus',
  `lic_dac` varchar(10) NOT NULL COMMENT 'Libelle Court Diplome Autre Cursus',
  `tem_en_sve_dac` bit(1) NOT NULL COMMENT 'Temoin en Service',
  PRIMARY KEY (`cod_dac`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Diplomes de l"enseignement superieur';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_etablissement`
--

CREATE TABLE `siscol_etablissement` (
  `cod_etb` varchar(8) NOT NULL COMMENT 'Code National de l"Etablissement',
  `cod_dep` varchar(3) NOT NULL COMMENT 'Code Departement',
  `cod_com` varchar(5) DEFAULT NULL COMMENT 'code commune',
  `lib_etb` varchar(40) NOT NULL COMMENT 'Libelle Long Etablissement',
  `lic_etb` varchar(10) NOT NULL COMMENT 'Libelle Court Etablissement',
  `tem_en_sve_etb` bit(1) NOT NULL COMMENT 'Temoin Code en Service',
  `lib_web_etb` varchar(120) DEFAULT NULL COMMENT 'Libellé Web',
  PRIMARY KEY (`cod_etb`),
  KEY `fk_siscol_commune_siscol_etab_cod_com` (`cod_com`),
  KEY `fk_siscol_departement_etab_cod_dep` (`cod_dep`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Etablissements francais sec ou sup';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_mention`
--

CREATE TABLE `siscol_mention` (
  `cod_men` varchar(2) NOT NULL COMMENT 'Code mention',
  `lic_men` varchar(10) NOT NULL COMMENT 'Libelle court mention',
  `lib_men` varchar(50) NOT NULL COMMENT 'Libelle long mention',
  `tem_en_sve_men` bit(1) NOT NULL COMMENT 'Temoin en service mention',
  PRIMARY KEY (`cod_men`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Mention';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_mention_niv_bac`
--

CREATE TABLE `siscol_mention_niv_bac` (
  `cod_mnb` varchar(2) NOT NULL COMMENT 'Code Mention Niveau Bac',
  `lib_mnb` varchar(40) NOT NULL COMMENT 'Libelle Long Mention Niveau Bac',
  `lic_mnb` varchar(10) NOT NULL COMMENT 'Libelle Court Mention Niveau Bac',
  `tem_en_sve_mnb` bit(1) NOT NULL COMMENT 'Temoin Code en  Service',
  PRIMARY KEY (`cod_mnb`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Mentions accordees au baccalaureat';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_pays`
--

CREATE TABLE `siscol_pays` (
  `cod_pay` varchar(3) NOT NULL COMMENT 'Code Pays INSEE',
  `lib_pay` varchar(40) NOT NULL COMMENT 'Libelle Long Pays',
  `lic_pay` varchar(10) NOT NULL COMMENT 'Libelle Court Pays',
  `lib_nat` varchar(40) NOT NULL COMMENT 'Libelle Nationalite',
  `tem_en_sve_pay` bit(1) NOT NULL COMMENT 'Temoin en Service',
  PRIMARY KEY (`cod_pay`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Table des pays';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_typ_diplome`
--

CREATE TABLE `siscol_typ_diplome` (
  `cod_tpd_etb` varchar(2) NOT NULL COMMENT 'Code Type Diplome Etablissement',
  `lib_tpd` varchar(40) NOT NULL COMMENT 'Libelle Long Type Diplome SISE',
  `lic_tpd` varchar(10) NOT NULL COMMENT 'Libelle Court Type Diplome SISE',
  `tem_en_sve_tpd` bit(1) NOT NULL COMMENT 'Temoin en Service',
  PRIMARY KEY (`cod_tpd_etb`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Typologie des diplomes';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_typ_resultat`
--

CREATE TABLE `siscol_typ_resultat` (
  `cod_tre` varchar(4) NOT NULL COMMENT 'code type resultat',
  `lib_tre` varchar(50) NOT NULL COMMENT 'libellé type resultat',
  `lic_tre` varchar(20) NOT NULL COMMENT 'libellé court type resultat',
  `tem_en_sve_tre` bit(1) NOT NULL COMMENT 'temoin en service type résultat',
  PRIMARY KEY (`cod_tre`)
) ENGINE=InnoDB COMMENT='Rrrentiel SiScol : Types de rsultats';

-- --------------------------------------------------------

--
-- Structure de la table `siscol_utilisateur`
--

CREATE TABLE `siscol_utilisateur` (
  `id_uti` int(10) NOT NULL COMMENT 'id de l''utilisateur',
  `cod_uti` varchar(30) NOT NULL COMMENT 'Code Utilisateur D"APOGEE connu d"ORACLE',
  `cod_cge` varchar(3) DEFAULT NULL COMMENT '(COPIED)Code Centre de Gestion d"appartenance de l"utilisateur',
  `lib_cmt_uti` varchar(200) DEFAULT NULL COMMENT 'Libelle long associe a l"utilisateur',
  `tem_en_sve_uti` bit(1) NOT NULL COMMENT 'Temoin code en service de l"utilisateur',
  `adr_mail_uti` varchar(200) DEFAULT NULL COMMENT 'Adresse mail de l''utilisateur',
  PRIMARY KEY (`id_uti`),
  KEY `fk_siscol_centre_gestion_utilisateur_cod_cge` (`cod_cge`)
) ENGINE=InnoDB COMMENT='Rérérentiel SiScol : Table des utilisateurs';

-- --------------------------------------------------------

--
-- Structure de la table `type_avis`
--

CREATE TABLE `type_avis` (
  `cod_typ_avis` varchar(2) NOT NULL COMMENT 'code du type d''avis',
  `libelle_typ_avis` varchar(20) NOT NULL COMMENT 'libelle du type d''avis',
  PRIMARY KEY (`cod_typ_avis`)
) ENGINE=InnoDB COMMENT='table des types d''avis';

-- --------------------------------------------------------

--
-- Structure de la table `type_decision`
--

CREATE TABLE `type_decision` (
  `id_typ_dec` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant du type de decision',
  `cod_typ_avis` varchar(2) NOT NULL COMMENT 'type_d''avis correspondant au type de décision',
  `cod_typ_dec` varchar(20) NOT NULL COMMENT 'code du type de decision',
  `lib_typ_dec` varchar(50) DEFAULT NULL COMMENT 'libellé type de decision',
  `id_i18n_lib_typ_dec` int(10) NOT NULL COMMENT 'identifiant i18n du libellé du type de décision',
  `id_mail` int(10) NOT NULL COMMENT 'id mail du type de decision',
  `tem_deverse_opi_typ_dec` bit(1) NOT NULL COMMENT 'temoin de deversement d''opi pour ce type de decision',
  `tem_definitif_typ_dec` bit(1) NOT NULL COMMENT 'temoin pour indiquer si le type de decision est défnitif',
  `tes_typ_dec` bit(1) NOT NULL COMMENT 'temoin en service du type de decision',
  `tem_model_typ_dec` bit(1) NOT NULL COMMENT 'témoi si le type de decision est un modele',
  `dat_cre_typ_dec` datetime NOT NULL COMMENT 'date de création',
  `user_cre_typ_dec` varchar(30) NOT NULL COMMENT 'user de création',
  `dat_mod_typ_dec` datetime NOT NULL COMMENT 'date de modification',
  `user_mod_typ_dec` varchar(30) NOT NULL COMMENT 'user de modification',
  PRIMARY KEY (`id_typ_dec`),
  UNIQUE KEY `cod_typ_dec` (`cod_typ_dec`),
  KEY `fktype_decision_type_avis_cod_typ_avis` (`cod_typ_avis`),
  KEY `fk_type_decision_mail_id_mail` (`id_mail`),
  KEY `fk_type_decision_i18n_id_i18n_lib_typ_dec` (`id_i18n_lib_typ_dec`)
) ENGINE=InnoDB COMMENT='table des types de décision';

-- --------------------------------------------------------

--
-- Structure de la table `type_decision_candidature`
--

CREATE TABLE `type_decision_candidature` (
  `id_type_dec_cand` int(10) NOT NULL AUTO_INCREMENT COMMENT 'identifiant de la décision prise sur une candidature',
  `id_cand` int(10) NOT NULL COMMENT 'identifiant de la canidature',
  `id_typ_dec` int(10) NOT NULL COMMENT 'identifiant du type de decision',
  `id_motiv` int(10) DEFAULT NULL COMMENT 'identifiant de la motivation d''avis',
  `list_comp_rang_typ_dec_cand` int(10) DEFAULT NULL COMMENT 'rang de liste complémentaire',
  `comment_type_dec_cand` varchar(500) DEFAULT NULL COMMENT 'éventuel commentaire',
  `preselect_date_type_dec_cand` date DEFAULT NULL COMMENT 'Date de l''épreuve de sélection',
  `preselect_heure_type_dec_cand` time DEFAULT NULL COMMENT 'Heure de l''épreuve de sélection',
  `preselect_lieu_type_dec_cand` varchar(100) DEFAULT NULL COMMENT 'Lieu de l''épreuve de sélection',
  `tem_appel_type_dec_cand` bit(1) NOT NULL COMMENT 'témoin pour indiquer que l''avis est un appel',
  `dat_cre_type_dec_cand` datetime NOT NULL COMMENT 'date de création',
  `user_cre_type_dec_cand` varchar(30) NOT NULL COMMENT 'user de création',
  `tem_valid_type_dec_cand` bit(1) NOT NULL COMMENT 'témoin pour indiquer que l''avis a été validé',
  `dat_valid_type_dec_cand` datetime DEFAULT NULL COMMENT 'date de validation',
  `user_valid_type_dec_cand` varchar(30) DEFAULT NULL COMMENT 'user de validation',
  PRIMARY KEY (`id_type_dec_cand`),
  KEY `fk_typ_dec_cand_typ_dec_id_typ_dec` (`id_typ_dec`),
  KEY `fk_typ_dec_cand_candidature_id_cand` (`id_cand`),
  KEY `fk_typ_dec_cand_motivation_avis_id_motiv` (`id_motiv`)
) ENGINE=InnoDB COMMENT='table de l''historique des decisions d''une canidature';

-- --------------------------------------------------------

--
-- Structure de la table `type_statut`
--

CREATE TABLE `type_statut` (
  `cod_typ_statut` varchar(2) NOT NULL COMMENT 'code du type de statut',
  `lib_typ_statut` varchar(20) NOT NULL COMMENT 'libellé du type de statut',
  `id_i18n_lib_typ_statut` int(10) NOT NULL COMMENT 'idi18n de traduction de statut d''un dossier',
  `dat_mod_typ_statut` datetime DEFAULT NULL COMMENT 'date de modification',
  PRIMARY KEY (`cod_typ_statut`),
  KEY `fk_type_statut_i18n_id_i18n_lib_typ_statut` (`id_i18n_lib_typ_statut`)
) ENGINE=InnoDB COMMENT='table des type de statut du dossier de candidature';

-- --------------------------------------------------------

--
-- Structure de la table `type_statut_piece`
--

CREATE TABLE `type_statut_piece` (
  `cod_typ_statut_piece` varchar(2) NOT NULL COMMENT 'code du type de statut',
  `lib_typ_statut_piece` varchar(20) NOT NULL COMMENT 'libellé du type de statut',
  `id_i18n_lib_typ_statut_piece` int(10) NOT NULL COMMENT 'idi18n de traduction de statut d''une piece',
  `dat_mod_typ_statut_piece` datetime DEFAULT NULL COMMENT 'date de modification',
  PRIMARY KEY (`cod_typ_statut_piece`),
  KEY `fk_type_statut_piece_i18n_id_i18n_lib_typ_statut_piece` (`id_i18n_lib_typ_statut_piece`)
) ENGINE=InnoDB COMMENT='table de type de statut des pièces (pj et formulaire)';

-- --------------------------------------------------------

--
-- Structure de la table `type_traduction`
--

CREATE TABLE `type_traduction` (
  `cod_typ_trad` varchar(20) NOT NULL COMMENT 'code du type de la traduction',
  `lib_typ_trad` varchar(30) NOT NULL COMMENT 'libellé du type de la traduction',
  `length_typ_trad` int(10) NOT NULL COMMENT 'taille du champs du type de la traduction',
  PRIMARY KEY (`cod_typ_trad`)
) ENGINE=InnoDB COMMENT='table des types de traduction';

-- --------------------------------------------------------

--
-- Structure de la table `type_traitement`
--

CREATE TABLE `type_traitement` (
  `cod_typ_trait` varchar(2) NOT NULL COMMENT 'code du type de traitement',
  `lib_typ_trait` varchar(20) NOT NULL COMMENT 'libellé du type de traitement',
  `tem_final_typ_trait` bit(1) NOT NULL COMMENT 'témoin pour les traitements finals',
  `id_i18n_lib_typ_trait` int(10) NOT NULL COMMENT 'libellé internationalisé',
  `dat_mod_typ_trait` datetime DEFAULT NULL COMMENT 'date de modification',
  PRIMARY KEY (`cod_typ_trait`),
  KEY `fk_type_trait_i18n_id_i18n_lib_typ_trait` (`id_i18n_lib_typ_trait`)
) ENGINE=InnoDB COMMENT='table des types de traitement';

-- --------------------------------------------------------

--
-- Structure de la table `version`
--

CREATE TABLE `version` (
  `cod_version` varchar(20) NOT NULL COMMENT 'code de version',
  `val_version` varchar(10) NOT NULL COMMENT 'valeur de la version',
  `dat_version` datetime NOT NULL COMMENT 'date de la version',
  PRIMARY KEY (`cod_version`)
) ENGINE=InnoDB COMMENT='table des versions';

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `adresse`
--
ALTER TABLE `adresse`
  ADD CONSTRAINT `fk_adresse_siscol_commune_cod_com` FOREIGN KEY (`cod_com`) REFERENCES `siscol_commune` (`cod_com`),
  ADD CONSTRAINT `fk_adresse_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`) REFERENCES `siscol_pays` (`cod_pay`);

--
-- Contraintes pour la table `batch_histo`
--
ALTER TABLE `batch_histo`
  ADD CONSTRAINT `fk_batch_histo_batch_cod_batch` FOREIGN KEY (`cod_batch`) REFERENCES `batch` (`cod_batch`);

--
-- Contraintes pour la table `campagne`
--
ALTER TABLE `campagne`
  ADD CONSTRAINT `campagne_id_camp_id_camp` FOREIGN KEY (`archiv_id_camp`) REFERENCES `campagne` (`id_camp`);

--
-- Contraintes pour la table `candidat`
--
ALTER TABLE `candidat`
  ADD CONSTRAINT `fk_candidat_adresse_id_adr` FOREIGN KEY (`id_adr`) REFERENCES `adresse` (`id_adr`),
  ADD CONSTRAINT `fk_candidat_langue_id_langue` FOREIGN KEY (`cod_langue`) REFERENCES `langue` (`cod_langue`),
  ADD CONSTRAINT `fk_candidat_siscol_pays_cod_pays_naiss` FOREIGN KEY (`cod_pay_naiss`) REFERENCES `siscol_pays` (`cod_pay`),
  ADD CONSTRAINT `fk_candidat_siscol_pays_cod_pay_nat` FOREIGN KEY (`cod_pay_nat`) REFERENCES `siscol_pays` (`cod_pay`),
  ADD CONSTRAINT `fk_civilite_candidat_cod_civ` FOREIGN KEY (`cod_civ`) REFERENCES `civilite` (`cod_civ`),
  ADD CONSTRAINT `fk_compte_minima_candidat_id_cpt_min` FOREIGN KEY (`id_cpt_min`) REFERENCES `compte_minima` (`id_cpt_min`),
  ADD CONSTRAINT `fk_siscol_departement_cod_dep_cod_dep_naiss` FOREIGN KEY (`cod_dep_naiss_candidat`) REFERENCES `siscol_departement` (`cod_dep`);

--
-- Contraintes pour la table `candidature`
--
ALTER TABLE `candidature`
  ADD CONSTRAINT `fk_candidature_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`),
  ADD CONSTRAINT `fk_candidature_formation_id_form` FOREIGN KEY (`id_form`) REFERENCES `formation` (`id_form`),
  ADD CONSTRAINT `fk_candidature_type_statut_cod_typ_statut` FOREIGN KEY (`cod_typ_statut`) REFERENCES `type_statut` (`cod_typ_statut`),
  ADD CONSTRAINT `fk_candidature_type_traitement_cod_typ_trait` FOREIGN KEY (`cod_typ_trait`) REFERENCES `type_traitement` (`cod_typ_trait`);

--
-- Contraintes pour la table `candidat_bac_ou_equ`
--
ALTER TABLE `candidat_bac_ou_equ`
  ADD CONSTRAINT `fk_bac_ou_equ_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`),
  ADD CONSTRAINT `fk_bac_ou_equ_siscol_bac_ou_equ_cod_bac` FOREIGN KEY (`cod_bac`) REFERENCES `siscol_bac_oux_equ` (`cod_bac`),
  ADD CONSTRAINT `fk_bac_ou_equ_siscol_commune_cod_com` FOREIGN KEY (`cod_com`) REFERENCES `siscol_commune` (`cod_com`),
  ADD CONSTRAINT `fk_bac_ou_equ_siscol_departement_cod_dep` FOREIGN KEY (`cod_dep`) REFERENCES `siscol_departement` (`cod_dep`),
  ADD CONSTRAINT `fk_bac_ou_equ_siscol_etablissement_cod_etb` FOREIGN KEY (`cod_etb`) REFERENCES `siscol_etablissement` (`cod_etb`),
  ADD CONSTRAINT `fk_bac_ou_equ_siscol_mention_niv_bac_cod_mnb` FOREIGN KEY (`cod_mnb`) REFERENCES `siscol_mention_niv_bac` (`cod_mnb`),
  ADD CONSTRAINT `fk_bac_ou_equ_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`) REFERENCES `siscol_pays` (`cod_pay`);

--
-- Contraintes pour la table `candidat_cursus_interne`
--
ALTER TABLE `candidat_cursus_interne`
  ADD CONSTRAINT `fk_candidat_cursus_interne_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`),
  ADD CONSTRAINT `fk_siscol_mention_cursus_interne_cod_men` FOREIGN KEY (`cod_men_cursus_interne`) REFERENCES `siscol_mention` (`cod_men`),
  ADD CONSTRAINT `fk_siscol_typ_resultat_candidat_cursus_interne_cod_tre` FOREIGN KEY (`cod_tre_cursus_interne`) REFERENCES `siscol_typ_resultat` (`cod_tre`);

--
-- Contraintes pour la table `candidat_cursus_post_bac`
--
ALTER TABLE `candidat_cursus_post_bac`
  ADD CONSTRAINT `fk_cursus_post_bac_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`),
  ADD CONSTRAINT `fk_cursus_siscol_commune_cod_com` FOREIGN KEY (`cod_com`) REFERENCES `siscol_commune` (`cod_com`),
  ADD CONSTRAINT `fk_cursus_siscol_departement_cod_dep` FOREIGN KEY (`cod_dep`) REFERENCES `siscol_departement` (`cod_dep`),
  ADD CONSTRAINT `fk_cursus_siscol_dip_aut_cur_cod_dac` FOREIGN KEY (`cod_dac`) REFERENCES `siscol_dip_aut_cur` (`cod_dac`),
  ADD CONSTRAINT `fk_cursus_siscol_etablissement_cod_etb` FOREIGN KEY (`cod_etb`) REFERENCES `siscol_etablissement` (`cod_etb`),
  ADD CONSTRAINT `fk_cursus_siscol_mention_cod_men` FOREIGN KEY (`cod_men`) REFERENCES `siscol_mention` (`cod_men`),
  ADD CONSTRAINT `fk_cursus_siscol_pays_cod_pay` FOREIGN KEY (`cod_pay`) REFERENCES `siscol_pays` (`cod_pay`);

--
-- Contraintes pour la table `candidat_cursus_pro`
--
ALTER TABLE `candidat_cursus_pro`
  ADD CONSTRAINT `fk_cursus_pro_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`);

--
-- Contraintes pour la table `candidat_stage`
--
ALTER TABLE `candidat_stage`
  ADD CONSTRAINT `fk_candidat_stage_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`);

--
-- Contraintes pour la table `centre_candidature`
--
ALTER TABLE `centre_candidature`
  ADD CONSTRAINT `fk_centre_candidature_typ_decision_id_typ_dec_fav` FOREIGN KEY (`id_typ_dec_fav`) REFERENCES `type_decision` (`id_typ_dec`),
  ADD CONSTRAINT `fk_centre_candidature_typ_decision_id_typ_dec_fav_list_comp` FOREIGN KEY (`id_typ_dec_fav_list_comp`) REFERENCES `type_decision` (`id_typ_dec`);

--
-- Contraintes pour la table `commission`
--
ALTER TABLE `commission`
  ADD CONSTRAINT `fk_commission_adresse_id_adr` FOREIGN KEY (`id_adr`) REFERENCES `adresse` (`id_adr`),
  ADD CONSTRAINT `fk_commission_centre_candidature_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`);

--
-- Contraintes pour la table `commission_membre`
--
ALTER TABLE `commission_membre`
  ADD CONSTRAINT `fk_commission_membre_commission_id_comm` FOREIGN KEY (`id_comm`) REFERENCES `commission` (`id_comm`),
  ADD CONSTRAINT `fk_droit_profil_ind_commission_membre_id_droit_profil_ind` FOREIGN KEY (`id_droit_profil_ind`) REFERENCES `droit_profil_ind` (`id_droit_profil_ind`);

--
-- Contraintes pour la table `compte_minima`
--
ALTER TABLE `compte_minima`
  ADD CONSTRAINT `fk_camp_cpt_min_id_camp` FOREIGN KEY (`id_camp`) REFERENCES `campagne` (`id_camp`);

--
-- Contraintes pour la table `droit_profil_fonc`
--
ALTER TABLE `droit_profil_fonc`
  ADD CONSTRAINT `fk_droit_profil_fonc_droit_fonctionnalite_cod_fonc` FOREIGN KEY (`cod_fonc`) REFERENCES `droit_fonctionnalite` (`cod_fonc`),
  ADD CONSTRAINT `fk_droit_profil_fonc_droit_profil_id_profil` FOREIGN KEY (`id_profil`) REFERENCES `droit_profil` (`id_profil`);

--
-- Contraintes pour la table `droit_profil_ind`
--
ALTER TABLE `droit_profil_ind`
  ADD CONSTRAINT `fk_droit_profil_ind_individu_login_ind` FOREIGN KEY (`login_ind`) REFERENCES `individu` (`login_ind`),
  ADD CONSTRAINT `fk_droit_user_profil_droit_profil_id_profil` FOREIGN KEY (`id_profil`) REFERENCES `droit_profil` (`id_profil`);

--
-- Contraintes pour la table `faq`
--
ALTER TABLE `faq`
  ADD CONSTRAINT `fk_faq_i18n_id_i18n_question_faq` FOREIGN KEY (`id_i18n_question_faq`) REFERENCES `i18n` (`id_i18n`),
  ADD CONSTRAINT `fk_faq_i18n_id_i18n_reponse_faq` FOREIGN KEY (`id_i18n_reponse_faq`) REFERENCES `i18n` (`id_i18n`);

--
-- Contraintes pour la table `formation`
--
ALTER TABLE `formation`
  ADD CONSTRAINT `fk_formation_commission_id_comm` FOREIGN KEY (`id_comm`) REFERENCES `commission` (`id_comm`),
  ADD CONSTRAINT `fk_formation_siscol_centre_gestion_cod_cge` FOREIGN KEY (`cod_cge`) REFERENCES `siscol_centre_gestion` (`cod_cge`),
  ADD CONSTRAINT `fk_formation_siscol_typ_diplome_cod_tpd_etb` FOREIGN KEY (`cod_tpd_etb`) REFERENCES `siscol_typ_diplome` (`cod_tpd_etb`),
  ADD CONSTRAINT `fk_formation_type_decision_id_typ_dec_fav_form` FOREIGN KEY (`id_typ_dec_fav`) REFERENCES `type_decision` (`id_typ_dec`),
  ADD CONSTRAINT `fk_formation_type_decision_id_typ_dec_fav_list_comp` FOREIGN KEY (`id_typ_dec_fav_list_comp`) REFERENCES `type_decision` (`id_typ_dec`),
  ADD CONSTRAINT `fk_formation_type_traitement_cod_typ_trait` FOREIGN KEY (`cod_typ_trait`) REFERENCES `type_traitement` (`cod_typ_trait`);

--
-- Contraintes pour la table `formulaire`
--
ALTER TABLE `formulaire`
  ADD CONSTRAINT `fk_centre_candidature_formulaire_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`),
  ADD CONSTRAINT `fk_i18n_formulaire_id18n_lib` FOREIGN KEY (`id_i18n_lib_formulaire`) REFERENCES `i18n` (`id_i18n`),
  ADD CONSTRAINT `fk_i18n_formulaire_id18n_url` FOREIGN KEY (`id_i18n_url_formulaire`) REFERENCES `i18n` (`id_i18n`);

--
-- Contraintes pour la table `formulaire_cand`
--
ALTER TABLE `formulaire_cand`
  ADD CONSTRAINT `fk_formulaire_cand_candidature_id_cand` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`),
  ADD CONSTRAINT `fk_formulaire_cand_formulaire_id_formulaire` FOREIGN KEY (`id_formulaire`) REFERENCES `formulaire` (`id_formulaire`),
  ADD CONSTRAINT `fk_formulaire_cand_typ_statut_piece_cod` FOREIGN KEY (`cod_typ_statut_piece`) REFERENCES `type_statut_piece` (`cod_typ_statut_piece`);

--
-- Contraintes pour la table `formulaire_form`
--
ALTER TABLE `formulaire_form`
  ADD CONSTRAINT `fk_formulaire_formation_pj_id_pj` FOREIGN KEY (`id_formulaire`) REFERENCES `formulaire` (`id_formulaire`),
  ADD CONSTRAINT `fk_formulaire_form_formation_id_form` FOREIGN KEY (`id_form`) REFERENCES `formation` (`id_form`);

--
-- Contraintes pour la table `gestionnaire`
--
ALTER TABLE `gestionnaire`
  ADD CONSTRAINT `fk_centre_candidature_gestionnaire_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`),
  ADD CONSTRAINT `fk_gestionnaire_cge_cod_cge` FOREIGN KEY (`cod_cge`) REFERENCES `siscol_centre_gestion` (`cod_cge`),
  ADD CONSTRAINT `fk_gestionnaire_droit_profil_ind_id_droit_profil_ind` FOREIGN KEY (`id_droit_profil_ind`) REFERENCES `droit_profil_ind` (`id_droit_profil_ind`);

--
-- Contraintes pour la table `gestionnaire_commission`
--
ALTER TABLE `gestionnaire_commission`
  ADD CONSTRAINT `commission_gestionnaire_commission_id_comm` FOREIGN KEY (`id_comm`) REFERENCES `commission` (`id_comm`),
  ADD CONSTRAINT `gestionnaire_gestionnaire_commission_id_droit_profil_ind` FOREIGN KEY (`id_droit_profil_ind`) REFERENCES `gestionnaire` (`id_droit_profil_ind`);

--
-- Contraintes pour la table `i18n`
--
ALTER TABLE `i18n`
  ADD CONSTRAINT `fk_type_traduction_cod_typ_trad` FOREIGN KEY (`cod_typ_trad`) REFERENCES `type_traduction` (`cod_typ_trad`);

--
-- Contraintes pour la table `i18n_traduction`
--
ALTER TABLE `i18n_traduction`
  ADD CONSTRAINT `fk_traduction_i18n_id_i18n` FOREIGN KEY (`id_i18n`) REFERENCES `i18n` (`id_i18n`),
  ADD CONSTRAINT `fk_traduction_langue_id_langue` FOREIGN KEY (`cod_langue`) REFERENCES `langue` (`cod_langue`);

--
-- Contraintes pour la table `mail`
--
ALTER TABLE `mail`
  ADD CONSTRAINT `fk_mail_i18n_id_i18n_corps_mail` FOREIGN KEY (`id_i18n_corps_mail`) REFERENCES `i18n` (`id_i18n`),
  ADD CONSTRAINT `fk_mail_i18n_id_i18n_sujet_mail` FOREIGN KEY (`id_i18n_sujet_mail`) REFERENCES `i18n` (`id_i18n`),
  ADD CONSTRAINT `fk_mail_type_avis_cod_typ_avis` FOREIGN KEY (`cod_typ_avis`) REFERENCES `type_avis` (`cod_typ_avis`);

--
-- Contraintes pour la table `motivation_avis`
--
ALTER TABLE `motivation_avis`
  ADD CONSTRAINT `fk_motivation_avis_i18n_id_i18n_lib_motiv` FOREIGN KEY (`id_i18n_lib_motiv`) REFERENCES `i18n` (`id_i18n`);

--
-- Contraintes pour la table `opi`
--
ALTER TABLE `opi`
  ADD CONSTRAINT `fk_cand_opi_id_cand` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`);

--
-- Contraintes pour la table `piece_justif`
--
ALTER TABLE `piece_justif`
  ADD CONSTRAINT `fk_fichier_piece_justif_id_fichier` FOREIGN KEY (`id_fichier`) REFERENCES `fichier` (`id_fichier`),
  ADD CONSTRAINT `fk_piece_justif_ctr_cand_id_ctr_cand` FOREIGN KEY (`id_ctr_cand`) REFERENCES `centre_candidature` (`id_ctr_cand`),
  ADD CONSTRAINT `fk_piece_justif_i18n_id_i18n_lib_pj` FOREIGN KEY (`id_i18n_lib_pj`) REFERENCES `i18n` (`id_i18n`);

--
-- Contraintes pour la table `pj_cand`
--
ALTER TABLE `pj_cand`
  ADD CONSTRAINT `fk_cand_pj_form_cand_pk` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`),
  ADD CONSTRAINT `fk_fichier_pj_cand_id_fichier` FOREIGN KEY (`id_fichier`) REFERENCES `fichier` (`id_fichier`),
  ADD CONSTRAINT `fk_pj_cand_pj_id_pj` FOREIGN KEY (`id_pj`) REFERENCES `piece_justif` (`id_pj`),
  ADD CONSTRAINT `fk_pj_cand_type_statut_piece_cod` FOREIGN KEY (`cod_typ_statut_piece`) REFERENCES `type_statut_piece` (`cod_typ_statut_piece`);

--
-- Contraintes pour la table `pj_form`
--
ALTER TABLE `pj_form`
  ADD CONSTRAINT `fk_pj_form_form_id_form` FOREIGN KEY (`id_form`) REFERENCES `formation` (`id_form`),
  ADD CONSTRAINT `fk_pj_form_pj_id_pj` FOREIGN KEY (`id_pj`) REFERENCES `piece_justif` (`id_pj`);

--
-- Contraintes pour la table `siscol_commune`
--
ALTER TABLE `siscol_commune`
  ADD CONSTRAINT `fk_siscol_departement_commune_cod_dep` FOREIGN KEY (`cod_dep`) REFERENCES `siscol_departement` (`cod_dep`) ON UPDATE CASCADE;

--
-- Contraintes pour la table `siscol_etablissement`
--
ALTER TABLE `siscol_etablissement`
  ADD CONSTRAINT `fk_siscol_commune_siscol_etab_cod_com` FOREIGN KEY (`cod_com`) REFERENCES `siscol_commune` (`cod_com`),
  ADD CONSTRAINT `fk_siscol_departement_etab_cod_dep` FOREIGN KEY (`cod_dep`) REFERENCES `siscol_departement` (`cod_dep`) ON UPDATE CASCADE;

--
-- Contraintes pour la table `siscol_utilisateur`
--
ALTER TABLE `siscol_utilisateur`
  ADD CONSTRAINT `fk_siscol_centre_gestion_utilisateur_cod_cge` FOREIGN KEY (`cod_cge`) REFERENCES `siscol_centre_gestion` (`cod_cge`) ON UPDATE CASCADE;

--
-- Contraintes pour la table `type_decision`
--
ALTER TABLE `type_decision`
  ADD CONSTRAINT `fktype_decision_type_avis_cod_typ_avis` FOREIGN KEY (`cod_typ_avis`) REFERENCES `type_avis` (`cod_typ_avis`),
  ADD CONSTRAINT `fk_type_decision_i18n_id_i18n_lib_typ_dec` FOREIGN KEY (`id_i18n_lib_typ_dec`) REFERENCES `i18n` (`id_i18n`),
  ADD CONSTRAINT `fk_type_decision_mail_id_mail` FOREIGN KEY (`id_mail`) REFERENCES `mail` (`id_mail`);

--
-- Contraintes pour la table `type_decision_candidature`
--
ALTER TABLE `type_decision_candidature`
  ADD CONSTRAINT `fk_typ_dec_cand_candidature_id_cand` FOREIGN KEY (`id_cand`) REFERENCES `candidature` (`id_cand`),
  ADD CONSTRAINT `fk_typ_dec_cand_motivation_avis_id_motiv` FOREIGN KEY (`id_motiv`) REFERENCES `motivation_avis` (`id_motiv`),
  ADD CONSTRAINT `fk_typ_dec_cand_typ_dec_id_typ_dec` FOREIGN KEY (`id_typ_dec`) REFERENCES `type_decision` (`id_typ_dec`);

--
-- Contraintes pour la table `type_statut`
--
ALTER TABLE `type_statut`
  ADD CONSTRAINT `fk_type_statut_i18n_id_i18n_lib_typ_statut` FOREIGN KEY (`id_i18n_lib_typ_statut`) REFERENCES `i18n` (`id_i18n`);

--
-- Contraintes pour la table `type_statut_piece`
--
ALTER TABLE `type_statut_piece`
  ADD CONSTRAINT `fk_type_statut_piece_i18n_id_i18n_lib_typ_statut_piece` FOREIGN KEY (`id_i18n_lib_typ_statut_piece`) REFERENCES `i18n` (`id_i18n`);

--
-- Contraintes pour la table `type_traitement`
--
ALTER TABLE `type_traitement`
  ADD CONSTRAINT `fk_type_trait_i18n_id_i18n_lib_typ_trait` FOREIGN KEY (`id_i18n_lib_typ_trait`) REFERENCES `i18n` (`id_i18n`);