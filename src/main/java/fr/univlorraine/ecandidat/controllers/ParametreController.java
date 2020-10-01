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
package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.repositories.ParametreRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.DateSVAListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.GestionnaireCandidatListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.MaintenanceListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ParametrePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ParametreWindow;
import fr.univlorraine.ecandidat.views.windows.ScolAlertSvaParametreWindow;
import fr.univlorraine.ecandidat.views.windows.ScolGestCandidatWindow;

/**
 * Gestion de l'entité parametres
 * @author Kevin Hergalant
 */
@Component
public class ParametreController {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreRepository parametreRepository;

	@Value("${enableAdminPJ:}")
	private transient Boolean enableAdminPJ;

	@Value("${downloadMultipleMode:}")
	private transient String downloadMultipleMode;

	@Value("${downloadMultipleAddPj:}")
	private transient Boolean downloadMultipleAddPj;

	/** @return la liste des parametres */
	public Map<String, Parametre> getMapParametreToCache() {
		final Map<String, Parametre> mapParametre = new HashMap<>();
		parametreRepository.findAll().forEach(e -> mapParametre.put(e.getCodParam(), e));
		return mapParametre;
	}

	/**
	 * @param  showScol
	 * @return          liste des parametres
	 */
	public List<Parametre> getParametres(final Boolean showScol) {
		return parametreRepository.findAll().stream().filter(e -> {
			if (!e.getTemAffiche()) {
				return false;
			}
			if (!showScol && e.getTemScol()) {
				return false;
			}
			return true;
		}).collect(Collectors.toList());
	}

	/** @return liste des parametres pour la scol */
	public List<Parametre> getScolParametres() {
		return parametreRepository.findAll().stream().filter(e -> e.getTemAffiche() && e.getTemScol()).collect(Collectors.toList());
	}

	/**
	 * Ouvre une fenêtre d'édition de parametre.
	 * @param parametre
	 */
	public void editParametre(final Parametre parametre, final Boolean isAdmin) {
		Assert.notNull(parametre, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}
		final ParametreWindow window = new ParametreWindow(parametre, isAdmin);
		window.addCloseListener(e -> lockController.releaseLock(parametre));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un parametre
	 * @param parametre
	 * @param parametrePres
	 */
	public void saveParametre(final Parametre parametre, final ParametrePresentation parametrePres) {
		Assert.notNull(parametre, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		Assert.notNull(parametrePres, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}

		/* Map le param de presentation dans un parametre */
		if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)) {
			parametre.setValParam(parametrePres.getValParamBoolean());
		} else if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_INTEGER)) {
			parametre.setValParam(String.valueOf(parametrePres.getValParamInteger()));
		} else if (parametre.getTypParam().startsWith(NomenclatureUtils.TYP_PARAM_STRING)) {
			parametre.setValParam(parametrePres.getValParamString());
		}

		/* Témoin de scol */
		parametre.setTemScol(parametrePres.getTemScol());

		parametreRepository.saveAndFlush(parametre);
		cacheController.reloadMapParametre(true);
		/* Si on vient de changer le param CC il faut recharger le menu des CtrCand */
		if (parametre.getCodParam().equals(NomenclatureUtils.COD_PARAM_SCOL_IS_PARAM_CC_DECISION)) {
			MainUI.getCurrent().buildMenuCtrCand();
		}
		lockController.releaseLock(parametre);
	}

	/**
	 * Retourne la taille maximale d'un string par rapport à son type : String(2)
	 * renvoi 2
	 * @param  type
	 * @return      la taille maximale
	 */
	public Integer getMaxLengthForString(final String type) {
		if (type != null && type.startsWith(NomenclatureUtils.TYP_PARAM_STRING)) {
			final Pattern patt = Pattern.compile("(\\d+)");
			final Matcher match = patt.matcher(type);
			while (match.find()) {
				return Integer.valueOf(match.group());
			}
			return 0;
		}
		return 0;
	}

	/**
	 * Renvoie un parametre
	 * @param  codParam
	 * @return          le parametre
	 */
	private Parametre getParametre(final String codParam) {
		return cacheController.getMapParametre().get(codParam);
	}

	/**
	 * Met en maintenance ou en service l'application-->batch
	 * @param enMaintenance
	 */
	public void changeMaintenanceParam(final Boolean enMaintenance) {
		final Parametre parametre = cacheController.getMapParametre().get(NomenclatureUtils.COD_PARAM_TECH_IS_MAINTENANCE);
		if (parametre != null) {
			parametre.setValParam(MethodUtils.getTemoinFromBoolean(enMaintenance));
			parametreRepository.saveAndFlush(parametre);
			cacheController.reloadMapParametre(true);
		}
	}

	/**
	 * Met en maintenance ou en service l'application-->Bouton
	 * @param enMaintenance
	 * @param listener
	 */
	public void changeMaintenanceStatut(final Boolean enMaintenance, final MaintenanceListener listener) {
		final Parametre parametre = getParametre(NomenclatureUtils.COD_PARAM_TECH_IS_MAINTENANCE);
		final Boolean oldMaintenanceStatut = MethodUtils.getBooleanFromTemoin(parametre.getValParam());
		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}

		if (enMaintenance.equals(oldMaintenanceStatut)) {
			String message = "admin.maintenance.nocorrect.";
			if (enMaintenance) {
				message += "shutdown";
			} else {
				message += "wakeup";
			}
			listener.changeModeMaintenance();
			Notification.show(applicationContext.getMessage(message, null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			lockController.releaseLock(parametre);
		} else {
			String message = "admin.maintenance.confirm.";
			if (enMaintenance) {
				message += "shutdown";
			} else {
				message += "wakeup";
			}
			final ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage(message, null, UI.getCurrent().getLocale()));
			win.addBtnOuiListener(e -> {
				changeMaintenanceParam(enMaintenance);
				listener.changeModeMaintenance();
			});
			win.addCloseListener(e -> lockController.releaseLock(parametre));
			UI.getCurrent().addWindow(win);
		}
	}

	/**
	 * Modifie le parametre de date SVA
	 * @param listener
	 * @param parametreDatValue
	 */
	public void changeSVAParametre(final DateSVAListener listener, final String parametreDatValue, final Boolean parametreDefValue) {
		final Parametre parametreDat = getParametre(NomenclatureUtils.COD_PARAM_SVA_ALERT_DAT);
		final Parametre parametreDefinitif = getParametre(NomenclatureUtils.COD_PARAM_SVA_ALERT_DEFINITIF);

		/* Verrou */
		if (!lockController.getLockOrNotify(parametreDat, null) && !lockController.getLockOrNotify(parametreDefinitif, null)) {
			return;
		}

		if ((parametreDat != null && parametreDatValue != null && !parametreDat.getValParam().equals(parametreDatValue))
			|| (parametreDefinitif != null && parametreDefValue != null && !getAlertSvaDefinitif().equals(parametreDefValue))) {
			listener.changeModeParametreSVA();
			Notification.show(applicationContext.getMessage("alertSva.param.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			lockController.releaseLock(parametreDat);
			lockController.releaseLock(parametreDefinitif);
			return;
		}

		final ScolAlertSvaParametreWindow win = new ScolAlertSvaParametreWindow(parametreDat, parametreDefinitif);
		win.addChangeAlertSVAWindowListener(e -> {
			parametreDat.setValParam(e.getValeurParamDate());
			parametreRepository.saveAndFlush(parametreDat);
			parametreDefinitif.setValParam(e.getValeurParamDefinitif());
			parametreRepository.saveAndFlush(parametreDefinitif);
			cacheController.reloadMapParametre(true);
			listener.changeModeParametreSVA();
		});
		win.addCloseListener(e -> {
			lockController.releaseLock(parametreDat);
			lockController.releaseLock(parametreDefinitif);
		});
		UI.getCurrent().addWindow(win);
	}

	/**
	 * @return la liste des parametres d'affichage pour les gestionnaires de
	 *         candidats
	 */
	public List<SimpleTablePresentation> getParametresGestionCandidat() {
		final Parametre paramComm = getParametre(NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_COMM);
		final Parametre paramCtr = getParametre(NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_CTR_CAND);

		final List<SimpleTablePresentation> liste = new ArrayList<>();
		liste.add(new SimpleTablePresentation(1,
			NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_COMM,
			applicationContext.getMessage("parametrage.codParam.gestionCandidatComm", null, UI.getCurrent().getLocale()),
			paramComm.getValParam()));
		liste.add(new SimpleTablePresentation(2,
			NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_CTR_CAND,
			applicationContext.getMessage("parametrage.codParam.gestionCandidatCtrCand", null, UI.getCurrent().getLocale()),
			paramCtr.getValParam()));
		return liste;
	}

	/**
	 * @param  code
	 * @return      le libellé d'affichage pour les gestionnaires de candidats
	 */
	public String getLibelleParametresGestionCandidat(final String code) {
		if (code == null) {
			return "";
		} else if (code.equals(NomenclatureUtils.GEST_CANDIDATURE_NONE)) {
			return applicationContext.getMessage("droitprofilind.gestCandidat.none", null, UI.getCurrent().getLocale());
		} else if (code.equals(NomenclatureUtils.GEST_CANDIDATURE_READ)) {
			return applicationContext.getMessage("droitprofilind.gestCandidat.read", null, UI.getCurrent().getLocale());
		} else if (code.equals(NomenclatureUtils.GEST_CANDIDATURE_WRITE)) {
			return applicationContext.getMessage("droitprofilind.gestCandidat.write", null, UI.getCurrent().getLocale());
		}
		return "";
	}

	/**
	 * Modifie le parametre de date SVA
	 * @param listener
	 * @param parametreValue
	 */
	public void changeParametreGestionCandidat(final GestionnaireCandidatListener listener, final String codeParam, final String parametreValue, final String title) {
		final Parametre parametre = getParametre(codeParam);

		/* Verrou */
		if (!lockController.getLockOrNotify(parametre, null)) {
			return;
		}

		if (parametre != null && parametreValue != null && !parametre.getValParam().equals(parametreValue)) {
			listener.changeModeGestionnaireCandidat();
			Notification.show(applicationContext.getMessage("droitprofilind.gestCandidat.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			lockController.releaseLock(parametre);
			return;
		}

		final ScolGestCandidatWindow win = new ScolGestCandidatWindow(parametre, title);
		win.addChangeGestCandidatWindowListener(e -> {
			parametreRepository.saveAndFlush(parametre);
			cacheController.reloadMapParametre(true);
			listener.changeModeGestionnaireCandidat();
		});
		win.addCloseListener(e -> lockController.releaseLock(parametre));
		UI.getCurrent().addWindow(win);
	}

	/** @return la liste des possibilités pour les gestionnaire de candidat */
	public List<SimpleBeanPresentation> getListeGestionnaireCandidat() {
		final List<SimpleBeanPresentation> liste = new ArrayList<>();
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.GEST_CANDIDATURE_NONE, getLibelleParametresGestionCandidat(NomenclatureUtils.GEST_CANDIDATURE_NONE)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.GEST_CANDIDATURE_READ, getLibelleParametresGestionCandidat(NomenclatureUtils.GEST_CANDIDATURE_READ)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.GEST_CANDIDATURE_WRITE, getLibelleParametresGestionCandidat(NomenclatureUtils.GEST_CANDIDATURE_WRITE)));
		return liste;
	}

	/**
	 * @param  regex
	 * @return       la liste des valeurs d'un parametre
	 */
	public List<SimpleBeanPresentation> getListeRegex(final String regex) {
		final List<SimpleBeanPresentation> liste = new ArrayList<>();
		/* On split la regex */
		final String[] tableau = regex.split(";");
		/* Le premier élément est le prefixe de message */
		final String prefixeMessage = tableau[0];
		/* Les autres sont les codes de valeur */
		for (int i = 1; i < tableau.length; i++) {
			liste.add(new SimpleBeanPresentation(tableau[i], applicationContext.getMessage(prefixeMessage + "." + tableau[i], null, UI.getCurrent().getLocale())));
		}
		return liste;
	}

	/**
	 * Renvoie une valeur entiere
	 * @param  codParam
	 * @return          la valeur integer
	 */
	private Integer getIntegerValue(final String codParam) {
		final Parametre param = getParametre(codParam);
		if (param == null) {
			return 0;
		} else {
			return Integer.valueOf(param.getValParam());
		}
	}

	/**
	 * Renvoie une valeur string
	 * @param  codParam
	 * @return          la valeur string
	 */
	private String getStringValue(final String codParam) {
		final Parametre param = getParametre(codParam);
		if (param == null) {
			return "";
		} else {
			return param.getValParam();
		}
	}

	/**
	 * Renvoie une valeur boolean
	 * @param  codParam
	 * @return          la valeur boolean
	 */
	private Boolean getBooleanValue(final String codParam) {
		final Parametre param = getParametre(codParam);
		if (param == null) {
			return MethodUtils.getBooleanFromTemoin(ConstanteUtils.TYP_BOOLEAN_NO);
		} else {
			return MethodUtils.getBooleanFromTemoin(param.getValParam());
		}
	}

	/** @return le nombre de voeux max par defaut */
	public Integer getNbVoeuxMax() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_CANDIDATURE_NB_VOEUX_MAX);
	}

	/** @return le nombre de voeux max par defaut */
	public Boolean getNbVoeuxMaxIsEtab() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_CANDIDATURE_NB_VOEUX_MAX_IS_ETAB);
	}

	/** @return le nombre de jour apres quoi les dossier archivés sont detruits */
	public Integer getNbJourArchivage() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_SCOL_NB_JOUR_ARCHIVAGE);
	}

	/** @return le nombre de jour apres quoi les comptes a minima sont detruits */
	public Integer getNbJourKeepCptMin() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_CANDIDAT_NB_JOUR_KEEP_CPT_MIN);
	}

	/** @return le prefixe des dossiers */
	public String getPrefixeNumDossCpt() {
		return getStringValue(NomenclatureUtils.COD_PARAM_CANDIDAT_PREFIXE_NUM_DOSS);
	}

	/** @return le prefixe des no OPI */
	public String getPrefixeOPI() {
		return getStringValue(NomenclatureUtils.COD_PARAM_OPI_PREFIXE);
	}

	/** @return le mode de download multiple */
	public Boolean getIsDownloadMultipleModePdf() {
		final String dowloadMultiple = getStringValue(NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_MODE);
		if (dowloadMultiple != null && dowloadMultiple.equals(ConstanteUtils.PARAM_MODE_DOWNLOAD_MULTIPLE_PDF)) {
			return true;
		}
		return false;
	}

	/** @return true si l'etablissement utilise les OPI */
	public Boolean getIsUtiliseOpi() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE);
	}

	/** @return true si l'etablissement utilise les OPI PJ */
	public Boolean getIsUtiliseOpiPJ() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE_PJ);
	}

	/** @return true si l'etablissement a l'ine obligatoire pour les francais */
	public Boolean getIsIneObligatoireFr() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_INE_OBLI_FR);
	}

	/**
	 * @return true si l'etablissement le code siscol est obligatoire pour les
	 *         formations
	 */
	public Boolean getIsFormCodSiScolOblig() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_SCOL_IS_COD_SISCOL_OBLI);
	}

	/**
	 * @return true si le candidat ne pourra télécharger ses lettre qu'après sa
	 *         réponse
	 */
	public Boolean getIsDownloadLettreAfterAccept() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_DOWNLOAD_IS_LETTRE_ADM_APRES_CONFIRM);
	}

	/** @return true si l'etablissement utilise la demat' */
	public Boolean getIsUtiliseDemat() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_TECH_IS_UTILISE_DEMAT);
	}

	/** @return la taille max d'un fichier en Mo */
	public Integer getFileMaxSize() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_TECH_FILE_MAX_SIZE);
	}

	/** @return true si l'application est en maintenance */
	public Boolean getIsMaintenance() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_TECH_IS_MAINTENANCE);
	}

	/** @return true si l'application accepte les appel */
	public Boolean getIsAppel() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_SCOL_IS_APPEL);
	}

	/** @return true si l'application lance les OPI immédiatement */
	public Boolean getIsOpiImmediat() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_OPI_IS_IMMEDIAT);
	}

	/** @return le nombre de jour apres quoi l'histo de batch est effacé */
	public Integer getNbJourKeepHistoBatch() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_TECH_NB_JOUR_KEEP_HISTO_BATCH);
	}

	/** @return le nombre de jours avant la date limite de confirmation où les candidatures avec avis favorables seront relancés */
	public Integer getNbJourRelanceFavo() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_SCOL_NB_JOUR_RELANCE_FAVO);
	}

	/** @return la date sur laquelle l'alerte SVA aura effet */
	public String getAlertSvaDat() {
		return getStringValue(NomenclatureUtils.COD_PARAM_SVA_ALERT_DAT);
	}

	/** @return si l'alerte SVA a effet sur les avis definitif */
	public Boolean getAlertSvaDefinitif() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_SVA_ALERT_DEFINITIF);
	}

	/** @return le code sans bac */
	public String getSiscolCodeSansBac() {
		return getStringValue(NomenclatureUtils.COD_PARAM_SCOL_SISCOL_COD_SANS_BAC);
	}

	/** @return si on remonte l'adresse fixe dans l'OPI */
	public Boolean getIsUtiliseOpiAdr() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE_ADR);
	}

	/** @return true si le cursus interne est remonté d'apogée */
	public Boolean getIsGetCursusInterne() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_GET_CURSUS_INTERNE);
	}

	/** @return true si l'ajout des PJ SiScol dans le dossier se fait */
	public Boolean getIsAddSiScolPJDossier() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_DOWNLOAD_IS_ADD_SISCOL_PJ);
	}

	/** @return true si on remonte les PJ SiScol */
	public Boolean getIsGetSiScolPJ() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_GET_SISCOL_PJ);
	}

	/** @return true si l'activation de l'ajout des PJ en mode multiple est activé, false sinon */
	public Boolean getIsDownloadMultipleAddPj() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_IS_ADD_PJ);
	}

	/** @return le mode de gestionnaire de candidat pour la commission */
	public String getModeGestionnaireCandidatCommission() {
		return getStringValue(NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_COMM);
	}

	/** @return le mode de gestionnaire de candidat pour le centre de candidature */
	public String getModeGestionnaireCandidatCtrCand() {
		return getStringValue(NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_CTR_CAND);
	}

	/** @return le mode d'affichage du rang pour le candidat */
	public String getModeAffichageRangCandidat() {
		return getStringValue(NomenclatureUtils.COD_PARAM_LC_MODE_AFFICHAGE_RANG);
	}

	/** @return si l'application calcul le rang reel LC */
	public Boolean isCalculRangReelLc() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_LC_IS_CALCUL_RANG_REEL);
	}

	/**
	 * @return si l'application bloque la saisie d'avis en masse après saisie d'un
	 *         premier avis
	 */
	public Boolean getIsUtiliseBlocageAvisMasse() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_GEST_IS_UTILISE_BLOCAGE_MASSE);
	}

	/** @return si l'application bloque utilise la synchro par INE */
	public Boolean getIsUtiliseSyncIne() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_UTILISE_SYNCHRO_INE);
	}

	/** @return si l'application bloque le paramétrage CC (mails, type decision, motivation) */
	public Boolean getIsParamCC() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_SCOL_IS_PARAM_CC_DECISION);
	}

	/** @return si un changement de type de traitement entraine le passage du statut de dossier à "En attente" */
	public Boolean getIsStatutAttWhenChangeTT() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_SCOL_IS_STATUT_ATT_WHEN_CHANGE_TT);
	}

	/** @return si l'application permet l'export du bloc note */
	public Boolean getIsExportBlocNote() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_GEST_IS_EXPORT_BLOC_NOTE);
	}

	/** @return si après chaque action, si des candidatures sont sélectionnées un message d'alerte très visible sera affiché */
	public Boolean getIsWarningCandSelect() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_GEST_IS_WARNING_CAND_SELECT);
	}

	/** @return l'affichage du bouton d'amin des PJ : par defaut false */
	public Boolean getIsEnableAdminPJ() {
		if (enableAdminPJ == null || !enableAdminPJ) {
			return false;
		}
		return true;
	}

	/** @return le nombre de dossiers maximum téléchargeables simultanément */
	public Integer getNbDownloaMultipliedMax() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_NB_MAX);
	}

	/** @return true si le service de fichier est en maitenance */
	public Boolean getIsDematMaintenance() {
		return getBooleanValue(NomenclatureUtils.COD_PARAM_TECH_IS_DEMAT_MAINTENANCE);
	}

	/** @return le nombre d'OPI maximum à être traités par le batch */
	public Integer getNbOpiBatch() {
		return getIntegerValue(NomenclatureUtils.COD_PARAM_OPI_NB_BATCH_MAX);
	}

}
