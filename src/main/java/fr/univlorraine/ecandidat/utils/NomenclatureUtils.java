/**
 *  ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fr.univlorraine.ecandidat.utils;

/**
 * Classe de constantes de nomenclature
 * @author Kevin Hergalant
 */
public class NomenclatureUtils {

	/* Nomenclature */
	public static final String USER_NOMENCLATURE = "Nomenclature";

	/* Version */
	public static final String VERSION_NOMENCLATURE_COD = "VERSION_NOMENCLATURE";
	public static final String VERSION_NOMENCLATURE_VAL = "2.5.0.0";
	public static final String VERSION_NO_VERSION_VAL = "-";
	public static final String VERSION_APPLICATION_COD = "VERSION_APPLICATION";
	public static final String VERSION_DB = "VERSION_DB";
	public static final String VERSION_SI_SCOL_COD = "VERSION_SI_SCOL";
	public static final String VERSION_DEMAT = "VERSION_DEMAT";
	public static final String VERSION_WS = "VERSION_WS";
	public static final String VERSION_WS_PJ = "VERSION_WS_PJ";
	public static final String VERSION_LS = "VERSION_LS";
	public static final String VERSION_INES = "VERSION_INES";

	/* Type d'avis */
	public static final String TYP_AVIS_FAV = "FA";
	public static final String TYP_AVIS_DEF = "DE";
	public static final String TYP_AVIS_LISTE_COMP = "LC";
	public static final String TYP_AVIS_LISTE_ATTENTE = "LA";
	public static final String TYP_AVIS_PRESELECTION = "PR";
	public static final String TYP_AVIS_ATTENTE = "AT";

	/* Type de statut de pièce */
	public static final String TYP_STATUT_PIECE_TRANSMIS = "TR";
	public static final String TYP_STATUT_PIECE_VALIDE = "VA";
	public static final String TYP_STATUT_PIECE_REFUSE = "RE";
	public static final String TYP_STATUT_PIECE_ATTENTE = "AT";
	public static final String TYP_STATUT_PIECE_NON_CONCERNE = "NC";

	/* Type de traitement */
	public static final String TYP_TRAIT_ALL = "ALL";
	public static final String TYP_TRAIT_AD = "AD";
	public static final String TYP_TRAIT_AC = "AC";
	public static final String TYP_TRAIT_AT = "AT";

	/* Civilite */
	public static final String CIVILITE_M = "M.";
	public static final String CIVILITE_F = "Mme";
	public static final String CIVILITE_SISCOL_M = "1|M";
	public static final String CIVILITE_SISCOL_F = "2|F";
	public static final String CIVILITE_SEXE_M = "M";
	public static final String CIVILITE_SEXE_F = "F";

	/* Langues */
	public static final String LANGUE_FR = "fr";
	public static final String LANGUE_EN = "en";
	public static final String LANGUE_ES = "es";
	public static final String LANGUE_DE = "de";

	/* Types de traduction */
	public static final String TYP_TRAD_FORM_LIB = "FORM_LIB";
	public static final String TYP_TRAD_FORM_URL = "FORM_URL";
	public static final String TYP_TRAD_MAIL_SUJET = "MAIL_SUJET";
	public static final String TYP_TRAD_MAIL_CORPS = "MAIL_CORPS";
	public static final String TYP_TRAD_MOTIV_LIB = "MOTIV_LIB";
	public static final String TYP_TRAD_PJ_LIB = "PJ_LIB";
	public static final String TYP_TRAD_QUESTION_LIB = "QUESTION_LIB";
	/* public static final String TYP_TRAD_PJ_FILE = "PJ_FILE";
	 * public static final String TYP_TRAD_PJ_LIB_FILE = "PJ_LIB_FILE"; */
	public static final String TYP_TRAD_TYP_DEC_LIB = "TYP_DEC_LIB";
	public static final String TYP_TRAD_TYP_TRAIT_LIB = "TYP_TRAIT_LIB";
	public static final String TYP_TRAD_TYP_STATUT = "TYP_STATUT";
	public static final String TYP_TRAD_TYP_STATUT_PIECE = "TYP_STATUT_PIECE";
	public static final String TYP_TRAD_FAQ_QUESTION = "FAQ_QUESTION";
	public static final String TYP_TRAD_FAQ_REPONSE = "FAQ_REPONSE";
	public static final String TYP_TRAD_MSG_VAL = "MSG_VAL";
	public static final String TYP_TRAD_CAMP_LIB = "CAMP_LIB";
	public static final String TYP_TRAD_FORM_INFO_COMP = "FORM_INFO_COMP";
	public static final String TYP_TRAD_COMM_COMMENT_RETOUR = "COMM_COMMENT_RETOUR";

	/* Droit profil */
	public static final String DROIT_PROFIL_ADMIN_TECH = "adminTechnique";
	public static final String DROIT_PROFIL_ADMIN = "admin";
	public static final String DROIT_PROFIL_CANDIDAT = "candidat";
	public static final String DROIT_PROFIL_SCOL_CENTRALE = "scolCentrale";
	public static final String DROIT_PROFIL_CENTRE_CANDIDATURE = "centreCandidature";
	public static final String DROIT_PROFIL_COMMISSION = "commission";
	public static final String DROIT_PROFIL_GESTION_CANDIDAT = "gestionCandidat";
	public static final String DROIT_PROFIL_GESTION_CANDIDAT_LS = "gestionCandidatLS";

	/* Type droit */
	public static final String TYP_DROIT_PROFIL_ADM = "A";
	public static final String TYP_DROIT_PROFIL_GESTIONNAIRE = "G";
	public static final String TYP_DROIT_PROFIL_COMMISSION = "C";
	public static final String TYP_DROIT_PROFIL_GEST_CANDIDAT = "I";
	public static final String TYP_DROIT_PROFIL_GEST_CANDIDAT_LS = "L";

	/* Fonctionnalite */
	public static final String FONCTIONNALITE_PARAM = "PARAMETRAGE";
	public static final String FONCTIONNALITE_STATS = "STATISTIQUES";
	public static final String FONCTIONNALITE_GEST_FORMATION = "GEST_FORMATION";
	public static final String FONCTIONNALITE_GEST_COMMISSION = "GEST_COMMISSION";
	public static final String FONCTIONNALITE_GEST_PJ = "GEST_PJ";
	public static final String FONCTIONNALITE_GEST_FORMULAIRE = "GEST_FORMULAIRE";
	public static final String FONCTIONNALITE_GEST_QUESTION = "GEST_QUESTION";
	public static final String FONCTIONNALITE_GEST_CANDIDATURE = "GEST_CANDIDATURE";
	public static final String FONCTIONNALITE_EDIT_STATUT_DOSSIER = "EDIT_STATUT_DOSSIER";
	public static final String FONCTIONNALITE_VISU_HISTO_AVIS = "VISU_HISTO_AVIS";
	public static final String FONCTIONNALITE_EDIT_TYPTRAIT = "EDIT_TYPTRAIT";
	public static final String FONCTIONNALITE_VALID_TYPTRAIT = "VALID_TYPTRAIT";
	public static final String FONCTIONNALITE_EDIT_AVIS = "EDIT_AVIS";
	public static final String FONCTIONNALITE_VALID_AVIS = "VALID_AVIS";
	public static final String FONCTIONNALITE_GEST_POST_IT = "GEST_POST_IT";
	public static final String FONCTIONNALITE_GEST_FENETRE_CAND = "GEST_FEN_CAND";
	public static final String FONCTIONNALITE_GEST_NUM_OPI = "GEST_NUM_OPI";
	public static final String FONCTIONNALITE_GEST_TAG = "GEST_TAG";
	public static final String FONCTIONNALITE_GEST_DAT_CONFIRM = "GEST_DAT_CONFIRM";
	public static final String FONCTIONNALITE_GEST_DAT_RETOUR = "GEST_DAT_RETOUR";
	public static final String FONCTIONNALITE_GEST_MONTANT = "GEST_MONTANT";

	/* Fonctionnalité étendue scol centrale */
	public static final String FONCTIONNALITE_GEST_PARAM_CC = "GEST_PARAM_CC";

	/* Action fonctionnalite sans droit */
	public static final String FONCTIONNALITE_OPEN_CANDIDAT = "OPEN_CANDIDAT";

	/* Mail */
	public static final String MAIL_GEN_VAR = "libelleCampagne";
	public static final String MAIL_CANDIDAT_GEN_VAR =
		"candidat.civilite;candidat.numDossierOpi;candidat.nomPat;candidat.nomUsu;candidat.prenom;candidat.autrePrenom;candidat.ine;candidat.cleIne;candidat.datNaiss;candidat.libVilleNaiss;candidat.libLangue;candidat.tel;candidat.telPort";
	public static final String MAIL_FORMATION_GEN_VAR =
		"formation.code;formation.libelle;formation.codEtpVetApo;formation.codVrsVetApo;formation.libApo;formation.motCle;formation.datDebDepot;formation.datFinDepot;formation.datPreAnalyse;formation.datRetour;formation.datJury;formation.datPubli;formation.datConfirm";
	public static final String MAIL_COMMISSION_GEN_VAR = "commission.libelle;commission.mail;commission.adresse;commission.tel;commission.url;commission.fax;commission.commentaireRetour;commission.signataire";
	public static final String MAIL_DOSSIER_GEN_VAR = "dossier.dateReception;dossier.montantFraisIns;dossier.complementExo";

	public static final String MAIL_DEC_VAR = "commentaire;complementAppel";
	public static final String MAIL_DEC_VAR_DEFAVORABLE = "motif";
	public static final String MAIL_DEC_VAR_PRESELECTION = "complementPreselect";
	public static final String MAIL_DEC_VAR_LISTE_COMP = "rang";
	public static final String MAIL_DEC_FAVORABLE = "AVIS_FAVORABLE";
	public static final String MAIL_DEC_DEFAVORABLE = "AVIS_DEFAVORABLE";
	public static final String MAIL_DEC_LISTE_ATT = "AVIS_LISTE_ATT";
	public static final String MAIL_DEC_LISTE_COMP = "AVIS_LISTE_COMP";
	public static final String MAIL_DEC_PRESELECTION = "AVIS_PRESELECTION";

	public static final String MAIL_STATUT_PREFIX = "STATUT_";
	public static final String MAIL_STATUT_AT = "STATUT_AT";
	public static final String MAIL_STATUT_RE = "STATUT_RE";
	public static final String MAIL_STATUT_IN = "STATUT_IN";
	public static final String MAIL_STATUT_CO = "STATUT_CO";

	public static final String MAIL_CPT_MIN = "CPT_MIN_CREATE";
	public static final String MAIL_CPT_MIN_VAR = "prenom;nom;numDossierOpi;lienValidation;lienValidationHtml;jourDestructionCptMin";

	public static final String MAIL_CPT_MIN_MOD_MAIL = "CPT_MIN_MOD_MAIL";
	public static final String MAIL_CPT_MIN_MOD_MAIL_VAR = "prenom;nom;numDossierOpi;lienValidation;lienValidationHtml";

	public static final String MAIL_CPT_MIN_MDP_OUBLIE = "CPT_MIN_MDP_OUBLIE";
	public static final String MAIL_CPT_MIN_MDP_OUBLIE_VAR = "prenom;nom;numDossierOpi;lienReinitialisation;lienReinitialisationHtml;datFinReinitCptMin";

	public static final String MAIL_CPT_MIN_LIEN_VALID_OUBLIE = "CPT_MIN_LIEN_VALID_OUBLIE";
	public static final String MAIL_CPT_MIN_LIEN_VALID_OUBLIE_VAR = "prenom;nom;numDossierOpi;lienValidation;lienValidationHtml;jourDestructionCptMin";

	public static final String MAIL_CPT_MIN_DELETE = "CPT_MIN_DELETE";
	public static final String MAIL_CPT_MIN_DELETE_VAR = "prenom;nom;numDossierOpi;";

	/* Mail de modification d'OPI */
	public static final String MAIL_CANDIDATURE_MODIF_COD_OPI_VAR = "newCodeOpi;formationsOpi";
	public static final String MAIL_CANDIDATURE_MODIF_COD_OPI = "CANDIDATURE_MODIF_COD_OPI";

	/* Mail de candidature */
	public static final String MAIL_CANDIDATURE = "CANDIDATURE";

	/* Mail d'annulation de candidature */
	public static final String MAIL_CANDIDATURE_ANNULATION = "CANDIDATURE_ANNULATION";

	/* Mail de proposition pour la commission */
	public static final String MAIL_COMMISSION_ALERT_PROPOSITION = "COMMISSION_ALERT_PROPOSITION";

	/* Mail d'annulation de candidature pour la commission */
	public static final String MAIL_COMMISSION_ALERT_ANNULATION = "COMMISSION_ALERT_ANNULATION";

	/* Mail de transmission de dossier pour la commission */
	public static final String MAIL_COMMISSION_ALERT_TRANSMISSION = "COMMISSION_ALERT_TRANSMISSION";

	/* Mail de desistement de candidature pour la commission */
	public static final String MAIL_COMMISSION_ALERT_DESISTEMENT = "COMMISSION_ALERT_DESISTEMENT";

	/* Mail de passage en liste principale pour la commission */
	public static final String MAIL_COMMISSION_ALERT_LISTE_PRINC = "COMMISSION_ALERT_LISTE_PRINC";

	/* Mail de confirmation de candidature */
	public static final String MAIL_CANDIDATURE_CONFIRM = "CANDIDATURE_CONFIRM";

	/* Mail de desistement de candidature */
	public static final String MAIL_CANDIDATURE_DESIST = "CANDIDATURE_DESIST";

	/* Mail de desistement automatique de candidature */
	public static final String MAIL_CANDIDATURE_DESIST_AUTO = "CANDIDATURE_DESIST_AUTO";

	/* Mail type de traitement AD */
	public static final String MAIL_TYPE_TRAIT_AD = "TYPE_TRAIT_AD";

	/* Mail type de traitement AC */
	public static final String MAIL_TYPE_TRAIT_AC = "TYPE_TRAIT_AC";

	/* Mail type de traitement AC */
	public static final String MAIL_TYPE_TRAIT_ATT = "TYPE_TRAIT_ATT";

	/* Mail de relance de formulaire de candidature */
	public static final String MAIL_CANDIDATURE_RELANCE_FORMULAIRE = "CANDIDATURE_RELANCE_FORMULAIRE";
	public static final String MAIL_CANDIDATURE_RELANCE_FORMULAIRE_VAR = "formulaires";

	/* Mail de relance de candidature sur avis favo */
	public static final String MAIL_CANDIDATURE_RELANCE_FAVO = "CANDIDATURE_RELANCE_FAVO";

	/* Type de decision */
	public static final String TYP_DEC_FAVORABLE = "AVIS_FAVORABLE";
	public static final String TYP_DEC_DEFAVORABLE = "AVIS_DEFAVORABLE";
	public static final String TYP_DEC_LISTE_ATT = "AVIS_LISTE_ATT";
	public static final String TYP_DEC_LISTE_COMP = "AVIS_LISTE_COMP";
	public static final String TYP_DEC_PRESELECTION = "AVIS_PRESELECTION";

	/* Type de statut de dossier */
	public static final String TYPE_STATUT_ATT = "AT";
	public static final String TYPE_STATUT_REC = "RE";
	public static final String TYPE_STATUT_INC = "IN";
	public static final String TYPE_STATUT_COM = "CO";

	/* Messages */
	public static final String COD_MSG_ACCUEIL = "MSG_ACCUEIL";
	public static final String COD_MSG_MAINTENANCE = "MSG_MAINTENANCE";

	/* Parametres */

	/* Paramètres OPI */
	public static final String COD_PARAM_OPI_PREFIXE = "OPI_PREFIXE";
	public static final String COD_PARAM_OPI_IS_UTILISE = "OPI_IS_UTILISE";
	public static final String COD_PARAM_OPI_IS_UTILISE_ADR = "OPI_IS_UTILISE_ADR";
	public static final String COD_PARAM_OPI_NB_BATCH_MAX = "OPI_NB_BATCH_MAX";
	public static final String COD_PARAM_OPI_IS_UTILISE_PJ = "OPI_IS_UTILISE_PJ";
	public static final String COD_PARAM_OPI_IS_IMMEDIAT = "OPI_IS_IMMEDIAT";

	/* Paramètres SVA */
	public static final String COD_PARAM_SVA_ALERT_DAT = "SVA_ALERT_DAT";
	public static final String COD_PARAM_SVA_ALERT_DEFINITIF = "SVA_ALERT_DEFINITIF";

	/* Paramètres Compte candidat */
	public static final String COD_PARAM_CANDIDAT_PREFIXE_NUM_DOSS = "CANDIDAT_PREFIXE_NUM_DOSS";
	public static final String COD_PARAM_CANDIDAT_NB_JOUR_KEEP_CPT_MIN = "CANDIDAT_NB_JOUR_KEEP_CPT_MIN";
	public static final String COD_PARAM_CANDIDAT_IS_INE_OBLI_FR = "CANDIDAT_IS_INE_OBLI_FR";
	public static final String COD_PARAM_CANDIDAT_IS_GET_CURSUS_INTERNE = "CANDIDAT_IS_GET_CURSUS_INTERNE";
	public static final String COD_PARAM_CANDIDAT_IS_UTILISE_SYNCHRO_INE = "CANDIDAT_IS_UTILISE_SYNCHRO_INE";
	public static final String COD_PARAM_CANDIDAT_IS_GET_SISCOL_PJ = "CANDIDAT_IS_GET_SISCOL_PJ";
	public static final String COD_PARAM_CANDIDAT_IS_MDP_CONNECT_CAS = "CANDIDAT_IS_MDP_CONNECT_CAS";
	public static final String COD_PARAM_CANDIDAT_NB_HEURE_LIEN_MDP_VALID = "CANDIDAT_NB_HEURE_LIEN_MDP_VALID";

	/* Paramètres Candidature */
	public static final String COD_PARAM_CANDIDATURE_NB_VOEUX_MAX = "CANDIDATURE_NB_VOEUX_MAX";
	public static final String COD_PARAM_CANDIDATURE_NB_VOEUX_MAX_IS_ETAB = "CANDIDATURE_NB_VOEUX_MAX_IS_ETAB";
	public static final String COD_PARAM_CANDIDATURE_IS_BLOC_TRANS_FORM = "CANDIDATURE_IS_BLOC_TRANS_FORM";

	/* Paramètres techniques */
	public static final String COD_PARAM_TECH_FILE_MAX_SIZE = "TECH_FILE_MAX_SIZE";
	public static final String COD_PARAM_TECH_IS_MAINTENANCE = "TECH_IS_MAINTENANCE";
	public static final String COD_PARAM_TECH_IS_UTILISE_DEMAT = "TECH_IS_UTILISE_DEMAT";
	public static final String COD_PARAM_TECH_NB_JOUR_KEEP_HISTO_BATCH = "TECH_NB_JOUR_KEEP_HISTO_BATCH";
	public static final String COD_PARAM_TECH_IS_DEMAT_MAINTENANCE = "TECH_IS_DEMAT_MAINTENANCE";
	public static final String COD_PARAM_TECH_IS_BLOC_LETTRE = "TECH_IS_BLOC_LETTRE";
	public static final String COD_PARAM_TECH_IS_INSCRIPTION_USER = "TECH_IS_INSCRIPTION_USER";

	/* Paramètres gestionnaire */
	public static final String COD_PARAM_GEST_IS_UTILISE_BLOCAGE_MASSE = "GEST_IS_UTILISE_BLOCAGE_MASSE";
	public static final String COD_PARAM_GEST_IS_EXPORT_BLOC_NOTE = "GEST_IS_EXPORT_BLOC_NOTE";
	public static final String COD_PARAM_GEST_IS_WARNING_CAND_SELECT = "GEST_IS_WARNING_CAND_SELECT";
	public static final String COD_PARAM_GEST_IS_UTILISE_REG_STU = "GEST_IS_UTILISE_REG_STU";

	/* Paramètres scol */
	public static final String COD_PARAM_SCOL_NB_JOUR_ARCHIVAGE = "SCOL_NB_JOUR_ARCHIVAGE";
	public static final String COD_PARAM_SCOL_IS_APPEL = "SCOL_IS_APPEL";
	public static final String COD_PARAM_SCOL_GESTION_CANDIDAT_COMM = "SCOL_GESTION_CANDIDAT_COMM";
	public static final String COD_PARAM_SCOL_GESTION_CANDIDAT_CTR_CAND = "SCOL_GESTION_CANDIDAT_CTR_CAND";
	public static final String COD_PARAM_SCOL_SISCOL_COD_SANS_BAC = "SCOL_SISCOL_COD_SANS_BAC";
	public static final String COD_PARAM_SCOL_IS_COD_SISCOL_OBLI = "SCOL_IS_COD_SISCOL_OBLI";
	public static final String COD_PARAM_SCOL_IS_PARAM_CC_DECISION = "SCOL_IS_PARAM_CC_DECISION";
	public static final String COD_PARAM_SCOL_NB_JOUR_RELANCE_FAVO = "SCOL_NB_JOUR_RELANCE_FAVO";
	public static final String COD_PARAM_SCOL_IS_STATUT_ATT_WHEN_CHANGE_TT = "SCOL_IS_STATUT_ATT_WHEN_CHANGE_TT";
	public static final String COD_PARAM_SCOL_MODE_TYPE_FORMATION = "SCOL_MODE_TYPE_FORMATION";

	/* Paramètres téléchargement multiple */
	public static final String COD_PARAM_DOWNLOAD_MULTIPLE_NB_MAX = "DOWNLOAD_MULTIPLE_NB_MAX";
	public static final String COD_PARAM_DOWNLOAD_MULTIPLE_IS_ADD_PJ = "DOWNLOAD_MULTIPLE_IS_ADD_PJ";
	public static final String COD_PARAM_DOWNLOAD_MULTIPLE_MODE = "DOWNLOAD_MULTIPLE_MODE";
	public static final String COD_PARAM_DOWNLOAD_IS_ADD_SISCOL_PJ = "DOWNLOAD_IS_ADD_SISCOL_PJ";
	public static final String COD_PARAM_DOWNLOAD_IS_LETTRE_ADM_APRES_CONFIRM = "DOWNLOAD_IS_LETTRE_ADM_APRES_CONFIRM";

	/* Paramètres liste complémentaire */
	public static final String COD_PARAM_LC_IS_CALCUL_RANG_REEL = "LC_IS_CALCUL_RANG_REEL";
	public static final String COD_PARAM_LC_MODE_AFFICHAGE_RANG = "LC_MODE_AFFICHAGE_RANG";

	/* Types de paramètres */
	public static final String TYP_PARAM_BOOLEAN = "Boolean";
	public static final String TYP_PARAM_INTEGER = "Integer";
	public static final String TYP_PARAM_STRING = "String";

	/* Liste de valeur parametre d'affcihage de rang */
	public static final String PARAM_MODE_AFFICHAGE_RANG_REGEX = "parametre.modeAffichageRang;N;S;R";

	/* Liste de valeur parametre pour le mode de téléchargemetn multiple */
	public static final String PARAM_MODE_DOWNLOAD_MULTIPLE_REGEX = "parametre.modeDownloadMultiple;P;Z";

	/* Liste de valeur parametre pour le type de formation */
	public static final String PARAM_MODE_TYPE_FORMATION_REGEX = "parametre.modeTypeFormation;NO;DIP;NOM";

	/* Batch */
	public static final String BATCH_SI_SCOL = "BATCH_SYNCHRO_SISCOL";
	public static final String BATCH_APP_EN_MAINT = "BATCH_APP_EN_MAINT";
	public static final String BATCH_APP_EN_SERVICE = "BATCH_APP_EN_SERVICE";
	public static final String BATCH_NETTOYAGE_CPT = "BATCH_NETTOYAGE_CPT";
	public static final String BATCH_NETTOYAGE = "BATCH_NETTOYAGE";
	public static final String BATCH_DESTRUCT_HISTO = "BATCH_DESTRUCT_HISTO";
	public static final String BATCH_ARCHIVAGE = "BATCH_ARCHIVAGE";
	public static final String BATCH_SYNCHRO_LIMESURVEY = "BATCH_SYNCHRO_LIMESURVEY";
	public static final String BATCH_DESTRUCT_DOSSIER = "BATCH_DESTRUCT_DOSSIER";
	public static final String BATCH_ASYNC_OPI = "BATCH_ASYNC_OPI";
	public static final String BATCH_ASYNC_OPI_PJ = "BATCH_ASYNC_OPI_PJ";
	public static final String BATCH_DESIST_AUTO = "BATCH_DESIST_AUTO";
	public static final String BATCH_RELANCE_FAVO = "BATCH_RELANCE_FAVO";
	public static final String BATCH_CALCUL_RANG_LC = "BATCH_CALCUL_RANG_LC";
	public static final String BATCH_MAJ_GESTIONNAIRE = "BATCH_MAJ_GESTIONNAIRE";
	public static final String BATCH_DEMO = "BATCH_DEMO";

	/* Alertes SVA */
	public static final String CAND_DAT_NO_DAT = "NO";
	public static final String CAND_DAT_CRE = "CRE";
	public static final String CAND_DAT_ANNUL = "ANN";
	public static final String CAND_DAT_ACCEPT = "ACC";
	public static final String CAND_DAT_TRANS = "TRANS";
	public static final String CAND_DAT_RECEPT = "REC";
	public static final String CAND_DAT_COMPLET = "COM";
	public static final String CAND_DAT_INCOMPLET = "INC";

	/* Type de gestion de candidature */
	public static final String GEST_CANDIDATURE_NONE = "N";
	public static final String GEST_CANDIDATURE_READ = "R";
	public static final String GEST_CANDIDATURE_WRITE = "W";

	/* Pour les mises a jour */
	public static final String VERSION_NOMENCLATURE_MAJ_2_2_0_6 = "2.2.0.6";
	public static final String VERSION_NOMENCLATURE_MAJ_2_2_0_13 = "2.2.0.13";
	public static final String VERSION_NOMENCLATURE_MAJ_2_2_0_14 = "2.2.0.14";
	public static final String VERSION_NOMENCLATURE_MAJ_2_2_3_0 = "2.2.3.0";
	public static final String VERSION_NOMENCLATURE_MAJ_2_2_5_1 = "2.2.5.1";
	public static final String VERSION_NOMENCLATURE_MAJ_2_2_9_1 = "2.2.9.1";
	public static final String VERSION_NOMENCLATURE_MAJ_2_3_0_0 = "2.3.0.0";
	public static final String VERSION_NOMENCLATURE_MAJ_2_3_0_5 = "2.3.0.5";
	public static final String VERSION_NOMENCLATURE_MAJ_2_3_0_6 = "2.3.0.6";
	public static final String VERSION_NOMENCLATURE_MAJ_2_4_0_0 = "2.4.0.0";
	public static final String VERSION_NOMENCLATURE_MAJ_2_4_0_3 = "2.4.0.3";
	public static final String VERSION_NOMENCLATURE_MAJ_2_4_0_5 = "2.4.0.5";
	public static final String VERSION_NOMENCLATURE_MAJ_2_4_2_0 = "2.4.2.0";
}
