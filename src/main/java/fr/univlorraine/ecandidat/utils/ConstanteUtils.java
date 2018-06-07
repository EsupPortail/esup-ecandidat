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

/** Classe de constantes
 *
 * @author Kevin Hergalant */
public class ConstanteUtils {

	/* Parametres Servlet */
	public static final String SERVLET_ALL_MATCH = "/*";
	public static final String SERVLET_NO_MATCH = "/nomatchingpossible";
	public static final String SERVLET_PARAMETER_HEARTBEAT_INTERVAL = "60";
	public static final String STARTUP_INIT_FLYWAY = "startuptInitFlyway";
	public static final String STARTUP_INIT_FLYWAY_OK = "O";

	/* Parametre Session */
	public static final String SESSION_MAX_INACTIVE_INTERVAL = "2700";

	/* Nombre de caractère minimum pour la recherche de personnels */
	public static final Integer NB_MIN_CAR_PERS = 2;

	/* Nombre de caractère minimum pour la recherche de candidats */
	public static final Integer NB_MIN_CAR_CAND = 2;

	/* Nombre de caractère minimum pour la recherche de formations APO */
	public static final Integer NB_MIN_CAR_FORM = 3;

	/* Nombre maximum de compte a minima en recherche */
	public static final Integer NB_MAX_RECH_CPT_MIN = 200;

	/* Nombre maximum de personnel en recherche */
	public static final Integer NB_MAX_RECH_PERS = 200;

	/* Nombre maximum de formation en recherche */
	public static final Integer NB_MAX_RECH_FORM = 50;

	/* Taille max du param de la taille max d'une PJ */
	public static final Integer SIZE_MAX_PARAM_MAX_FILE_PJ = 10;

	/* Nombre de candidature maximum pour l'edition en masse */
	public static final Integer SIZE_MAX_EDITION_MASSE = 200;

	/* Boolean si on ajoute les PJ au dossier telechargé */
	public static final Boolean ADD_LETTRE_TO_MAIL = true;

	/* Folder pour le stockage des pièces communes */
	public static final String PJ_FOLDER_COMMUNES = "commun";

	/* Constantes des objets de session */
	public static final String SESSION_PROPERTY_ID = "propertyId";
	public static final String SESSION_PROPERTY_TITLE = "propertyTitle";
	public static final String SESSION_PROPERTY_INFO = "propertyInfo";
	public static final String SESSION_PROPERTY_ICON = "propertyIcon";

	/* Les autorisations des vues */
	public static final String PREFIXE_ROLE = "ROLE_";
	public static final String PRE_AUTH_ADMIN = "hasAnyRole('" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH + "')";
	public static final String PRE_AUTH_SCOL_CENTRALE = "hasAnyRole('" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH + "','"
			+ PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE + "')";
	public static final String PRE_AUTH_CTR_CAND = "hasAnyRole('" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH + "','"
			+ PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE + "')";
	public static final String PRE_AUTH_COMMISSION = "hasAnyRole('" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH + "','"
			+ PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_COMMISSION + "')";
	public static final String PRE_AUTH_CANDIDAT = "hasAnyRole('" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH + "','"
			+ PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT + "','" + PREFIXE_ROLE
			+ NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT_LS + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_CANDIDAT + "')";
	public static final String PRE_AUTH_CANDIDAT_ADMIN = "hasAnyRole('" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH + "','"
			+ PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE + "','" + PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT + "')";

	/* Droit profil */
	public static final String ROLE_ANONYMOUS = PREFIXE_ROLE + "ANONYMOUS";
	public static final String ROLE_ADMIN_TECH = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH;
	public static final String ROLE_ADMIN = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_ADMIN;
	public static final String ROLE_CANDIDAT = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_CANDIDAT;
	public static final String ROLE_SCOL_CENTRALE = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE;
	public static final String ROLE_CENTRE_CANDIDATURE = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE;
	public static final String ROLE_COMMISSION = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_COMMISSION;
	public static final String ROLE_GESTION_CANDIDAT = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT;
	public static final String ROLE_GESTION_CANDIDAT_LS = PREFIXE_ROLE + NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT_LS;

	/* Les property d'icones */
	public static final String PROPERTY_FLAG = "flag";

	/* Les menus de l'UI */
	public static final String UI_MENU_ADMIN = "UI_MENU_PARAM";
	public static final String UI_MENU_SCOL = "UI_MENU_SCOL";
	public static final String UI_MENU_CTR = "UI_MENU_CTR";
	public static final String UI_MENU_GEST_CAND = "UI_MENU_GEST_CAND";
	public static final String UI_MENU_CAND = "UI_MENU_CAND";
	public static final String UI_MENU_COMM = "UI_MENU_COMM";

	/* Constantes de batch */
	public static String BATCH_RUNNING = "RUNNING";
	public static String BATCH_FINISH = "FINISH";
	public static String BATCH_ERROR = "ERROR";
	public static String BATCH_INTERRUPT = "INTERRUPT";

	/* Type fichier */
	public static String TYPE_FICHIER_CANDIDAT = "C";
	public static String TYPE_FICHIER_GESTIONNAIRE = "G";
	public static String TYPE_FICHIER_STOCK_CMIS = "C";
	public static String TYPE_FICHIER_STOCK_FILE_SYSTEM = "F";
	public static String TYPE_FICHIER_STOCK_NONE = "N";
	public static String TYPE_FICHIER_SIGN_COMM = "S";
	public static String TYPE_FICHIER_PJ_GEST = "G";
	public static String TYPE_FICHIER_PJ_CAND = "C";

	/* Type MIME Acceptes */
	public static String TYPE_MIME_FILE_PDF = "application/pdf";
	public static String TYPE_MIME_FILE_JPG = "image/jpeg";
	public static String TYPE_MIME_FILE_PNG = "image/png";

	/* Extensions Acceptees */
	public static String[] EXTENSION_PDF = {"pdf"};
	public static String[] EXTENSION_PDF_IMG = {"pdf", "jpg", "jpeg", "png"};
	public static String[] EXTENSION_IMG = {"jpg", "jpeg", "png"};
	public static String[] EXTENSION_JPG = {"jpg", "jpeg"};
	public static String[] EXTENSION_PNG = {"png"};

	/* Type de boolean apogee */
	public static final String TYP_BOOLEAN_YES = "O";
	public static final String TYP_BOOLEAN_NO = "N";

	/* Code pays france Apogee */
	public static final String PAYS_CODE_FRANCE = "100";

	/* Code Type pays ou dpt Apogee */
	public static final String COD_TYP_PAY_DPT_PAYS = "P";
	public static final String COD_TYP_PAY_DPT_DEPARTEMENT = "D";

	/* Constante jour,mois,annee */
	public static Integer TYPE_JOUR = 1;
	public static Integer TYPE_MOIS = 2;
	public static Integer TYPE_ANNEE = 3;

	/* Constante type de formulaire */
	public static final String TYP_FORM_ADR = "TYP_FORM_ADR";
	public static final String TYP_FORM_CANDIDAT = "TYP_FORM_CANDIDAT";

	/* Constantes generation */
	public static final String GEN_PWD = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
	public static final String GEN_NUM_DOSS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final Integer GEN_SIZE = 8;
	public static final String GEN_PWD_TYPE_PBKDF2 = "P";
	public static final String GEN_PWD_TYPE_BCRYPT = "B";

	/* Constante ODF */
	public static final String ODF_CAPTION = "caption";
	public static final String ODF_ICON = "icon";
	public static final String ODF_FORM_ID = "idForm";
	public static final String ODF_DIP_ID = "idDip";
	public static final String ODF_FORM_TITLE = "title";
	public static final String ODF_FORM_DIPLOME = "diplome";
	public static final String ODF_FORM_CTR_CAND = "ctrCand";
	public static final String ODF_FORM_MOT_CLE = "motCle";
	public static final String ODF_FORM_DATE = "dates";
	public static final String ODF_FORM_MODE_CAND = "modeCand";

	public static final String ODF_TYPE = "type";
	public static final String ODF_TYPE_CTR = "type_ctr";
	public static final String ODF_TYPE_DIP = "type_dip";
	public static final String ODF_TYPE_FORM = "type_form";

	/* Constantes WS validation compte */
	public static final String REST_VALID_SUCCESS = "success";
	public static final String REST_VALID_CPT_NULL = "cptNull";
	public static final String REST_VALID_ALREADY_VALID = "alreadyValid";
	public static final String REST_VALID_ERROR = "error";

	/* Les modes de fonctionnement Si Scol */
	public static final String SI_SCOL_NOT_APOGEE = "SI_SCOL_NOT_APOGEE";
	public static final String SI_SCOL_APOGEE = "SI_SCOL_APOGEE";

	/* Libelle generic */;
	public static final String GENERIC_LIBELLE = "genericLibelle";
	public static final String GENERIC_LIBELLE_ALTERNATIF = "genericLibelleAlternatif";

	/* Lock pour le candidat */
	public static final String LOCK_INFOS_PERSO = "LOCK_INFOS_PERSO";
	public static final String LOCK_ADRESSE = "LOCK_ADRESSE";
	public static final String LOCK_BAC = "LOCK_BAC";
	public static final String LOCK_CURSUS_EXTERNE = "LOCK_CURSUS_EXTERNE";
	public static final String LOCK_FORMATION_PRO = "LOCK_FORMATION_PRO";
	public static final String LOCK_STAGE = "LOCK_STAGE";
	public static final String LOCK_ODF = "LOCK_ODF";
	public static final String LOCK_CAND = "LOCK_CAND";

	/* Candidature */
	public static final String CANDIDATURE_LIB_FORM = "libForm";
	public static final String CANDIDATURE_DAT_RETOUR_FORM = "datRetourForm";
	public static final String CANDIDATURE_LIB_STATUT = "libStatut";
	public static final String CANDIDATURE_LIB_TYPE_TRAITEMENT = "libTypTraitement";
	public static final String CANDIDATURE_LIB_LAST_DECISION = "libLastDecision";
	public static final String CANDIDATURE_COMMENTAIRE = "commentaire";
	public static final String CANDIDATURE_OPI = "codOpi";

	/* Cursus externe */
	public static final String CURSUS_EXTERNE_OBTENU = "O";
	public static final String CURSUS_EXTERNE_NON_OBTENU = "N";
	public static final String CURSUS_EXTERNE_EN_COURS = "E";

	/* Load Balancing rechargement listes */
	public static final String CACHE_PARAM = "param";
	public static final String CACHE_LANGUE = "langue";
	public static final String CACHE_LANGUE_DEFAULT = "langue_def";
	public static final String CACHE_FAQ = "faq";
	public static final String CACHE_MSG = "msg";
	public static final String CACHE_ODF = "odf";
	public static final String CACHE_CAMP = "campagne";
	public static final String CACHE_TABLE_REF_PAYS = "tr_pays";
	public static final String CACHE_TABLE_REF_PAYS_FRANCE = "tr_pays_fr";
	public static final String CACHE_TABLE_REF_DPT = "tr_dpt";
	public static final String CACHE_TABLE_REF_TYPDIP = "tr_typ_dip";
	public static final String CACHE_TABLE_REF_CGE = "tr_cge";
	public static final String CACHE_TABLE_REF_BAC = "tr_bac";
	public static final String CACHE_TABLE_REF_DIP = "tr_dip";
	public static final String CACHE_TABLE_REF_MENTION = "tr_mention";
	public static final String CACHE_TABLE_REF_MENTBAC = "tr_mention_bac";
	public static final String CACHE_TABLE_REF_TYPRES = "tr_typ_res";
	public static final String CACHE_TABLE_REF_TYPTRAIT = "tr_typ_trait";
	public static final String CACHE_TABLE_REF_TYPSTATUT = "tr_typ_statut";
	public static final String CACHE_TABLE_REF_TYPSTATUT_PJ = "tr_typ_statut_pj";
	public static final String CACHE_TABLE_REF_ANNEE_UNI = "tr_annee_uni";

	/* Cache non rechargé */
	public static final String CACHE_TABLE_REF_CIVILITE = "tr_civilite";
	public static final String CACHE_TABLE_REF_TYPAVIS = "tr_typ_avis";

	/* Cache gestionnaire */
	public static final String CACHE_TABLE_REF_FONCTIONNALITE = "tr_fonctionnalite";
	public static final String CACHE_SVA = "sva";
	public static final String CACHE_TAG = "tag";

	/* Type de gestion de la candidature-->Gestionnaire ou commission ou candidat */
	public static final String TYP_GESTION_CANDIDATURE_CTR_CAND = "C";
	public static final String TYP_GESTION_CANDIDATURE_COMMISSION = "O";
	public static final String TYP_GESTION_CANDIDATURE_CANDIDAT = "I";

	/* Les types de vues */
	public static final int TYP_VIEW_SCOL = 1;
	public static final int TYP_VIEW_CTR_CAND = 2;
	public static final int TYP_VIEW_COMM = 3;

	/* Mode d'id oublié ou code d'activation */
	public static final String FORGOT_MODE_ID_OUBLIE = "I";
	public static final String FORGOT_MODE_CODE_ACTIVATION = "C";

	/* Options de candidature gestionnaire */
	public static final String OPTION_CLASSIQUE = "0";
	public static final String OPTION_PROP = "1";

	/* Preference de direction de trie */
	public static final String PREFERENCE_SORT_DIRECTION_ASCENDING = "A";
	public static final String PREFERENCE_SORT_DIRECTION_DESCENDING = "D";

	/* Type d'affichage pour la commission */
	public static final String COMM_TYP_AFF_READONLY = "R";
	public static final String COMM_TYP_AFF_GEN = "G";
	public static final String COMM_TYP_AFF_LETTRE = "L";

	/* Les templates */
	public static final String TEMPLATE_DOSSIER = "dossier_export_template";
	public static final String TEMPLATE_LETTRE_ADM = "lettre_adm_export_template";
	public static final String TEMPLATE_LETTRE_REFUS = "lettre_refus_export_template";
	public static final String TEMPLATE_EXTENSION = ".docx";

	/* Les type de lettres-->telechargement ou mail */
	public static final String TYP_LETTRE_DOWNLOAD = "D";
	public static final String TYP_LETTRE_MAIL = "M";

	/* Options de candidature gestionnaire */
	public static final String OPTION_IMG_AFF_ORIGINAL = "0";
	public static final String OPTION_IMG_AFF_OPTIMISE = "1";

	/* Variable a intégrer dans les formulaires */
	public static String VAR_REGEX_FORM_NUM_DOSSIER = "\\$\\{numdossier\\}";

	/* Constantes de mois */
	public static String[] NOM_MOIS_SHORT = {"jan", "fev", "mar", "avr", "mai", "juin", "juil", "aou", "sep", "oct", "nov", "dec"};

	public static String[] NOM_MOIS_LONG = {"Janvier", "Fevrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Aout", "Septembre", "Octobre", "Novembre", "Decembre"};

	/* Constantes de jour */
	public static String[] NOM_JOURS = {"lun", "mar", "mer", "jeu", "ven", "sam", "dim"};

	/* Pour l'upload */
	public static long UPLOAD_MO1 = 1048576;
	public static int UPLOAD_INTERVAL = 500000;

	// accepte les chiffres
	public static String regExNoTel = "^[0-9\\/\\+\\(\\)\\-\\.\\s]+$";
	// même regex que le validator vaadin
	public static String regExMail = "^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";

	// chaine de validation INE UL
	public static String chaineValidationNNE[] = new String[] {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	/* Security constants */
	public static String SECURITY_CONNECT_PATH = "/connect";
	public static String SECURITY_LOGOUT_PATH = "/logout";
	public static String SECURITY_SWITCH_PATH = "/login/impersonate";
	public static String SECURITY_SWITCH_BACK_PATH = "/logout/impersonate";

	/* Charset Rest ApoWS */
	public static String WS_APOGEE_PROP_FILE = "/configUrlServices.properties";
	public static String WS_APOGEE_DEFAULT_CHARSET = "UTF-8";
	public static String WS_APOGEE_PJ_SERVICE = "pj.urlService";
	public static String WS_APOGEE_PJ_INFO = "info";
	public static String WS_APOGEE_PJ_FILE = "fichier";
	public static String WS_APOGEE_PJ_TEM_VALID_CODE = "V";

	/* WS INES */
	public static String WS_INES_CHECK_URI_SERVICE = "checkInes.urlService";
	public static String WS_INES_CHECK_SERVICE = "/v1/check-ine";
	public static String WS_INES_PARAM_INE = "ine";
	public static String WS_INES_PARAM_KEY = "cle";
	public static String WS_INES_PARAM_TYPE = "type";
	public static String WS_INES_PARAM_TYPE_INES = "INES";

	/* WS PJ OPI */
	public static Boolean WS_PJ_OPI_ACTIVE = true;

	/* Constantes pour le print du dossier */
	public static Boolean DOSSIER_ADD_HEADER_IMG = true;
	public static Float DOSSIER_MARGIN = 15f;
	public static Float DOSSIER_FONT_SIZE = 12f;

	/* Constante de flag pour les formations */
	public static String FLAG_GREEEN = "green";
	public static String FLAG_RED = "red";
	public static String FLAG_YELLOW = "yellow";
	public static String FLAG_BLUE = "blue";
}
