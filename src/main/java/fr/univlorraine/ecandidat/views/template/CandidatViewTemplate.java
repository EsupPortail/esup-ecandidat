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
package fr.univlorraine.ecandidat.views.template;

import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.UiController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Template de vue candidat
 * @author Kevin Hergalant
 */
public class CandidatViewTemplate extends VerticalLayout {

	/** serialVersionUID **/
	private static final long serialVersionUID = 8171585717750226717L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient CandidatController candidatController;

	/* Données candidat */
	protected CompteMinima cptMin;
	protected Candidat candidat;

	/* Composants */
	protected Label title = new Label();
	protected Label subtitle = new Label();

	/* Composants d'erreur */
	private final VerticalLayout globalLayout = new VerticalLayout();
	private final HorizontalLayout buttonLayout = new HorizontalLayout();
	private final HorizontalLayout titleLayout = new HorizontalLayout();

	private final Label errorLabel = new Label();
	private final Label lockLabel = new Label();
	protected Boolean isArchive = false;
	protected Boolean isLectureSeule = false;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		globalLayout.setSizeFull();
		globalLayout.setSpacing(true);

		errorLabel.addStyleName(ValoTheme.LABEL_FAILURE);
		errorLabel.setVisible(false);

		addComponent(errorLabel);
		addComponent(globalLayout);

		/* Titre */
		titleLayout.setWidth(100, Unit.PERCENTAGE);
		titleLayout.setSpacing(true);
		title.addStyleName(StyleConstants.VIEW_TITLE);
		titleLayout.addComponent(title);
		titleLayout.setExpandRatio(title, 1);

		globalLayout.addComponent(titleLayout);

		/* Sous titre */
		subtitle.setContentMode(ContentMode.HTML);
		globalLayout.addComponent(subtitle);
		subtitle.setVisible(false);

		/* Lock */
		lockLabel.addStyleName(ValoTheme.LABEL_FAILURE);
		lockLabel.setVisible(false);
		globalLayout.addComponent(lockLabel);

		buttonLayout.setWidth(100, Unit.PERCENTAGE);
		globalLayout.addComponent(buttonLayout);
	}

	/**
	 * Ajoute un sous titre
	 */
	public void setSubtitle(final String subtitleTxt) {
		if (subtitleTxt != null && !subtitleTxt.equals("")) {
			subtitle.setVisible(true);
			subtitle.setValue(subtitleTxt);
		}
	}

	/**
	 * Ajoute des boutons de navigation
	 * @param previousView
	 * @param nextView
	 */
	public void setNavigationButton(final String viewName) {
		final StringBuilder previousView = new StringBuilder();
		final StringBuilder nextView = new StringBuilder();

		final LinkedList<String> menusCandidat = ((MainUI) UI.getCurrent()).getListMenuCandidat();
		final int indexView = menusCandidat.indexOf(viewName);
		if (indexView != 0) {
			previousView.append(menusCandidat.get(indexView - 1));
		}
		if (indexView != menusCandidat.size() - 1) {
			nextView.append(menusCandidat.get(indexView + 1));
		}

		if (StringUtils.isNotBlank(previousView.toString())) {
			final OneClickButton btnPrevious = new OneClickButton(applicationContext.getMessage("btnPrevious", null, UI.getCurrent().getLocale()), FontAwesome.ARROW_CIRCLE_O_LEFT);
			btnPrevious.addClickListener(e -> uiController.navigateTo(previousView.toString()));
			titleLayout.addComponent(btnPrevious);
			titleLayout.setComponentAlignment(btnPrevious, Alignment.MIDDLE_LEFT);
		}

		if (StringUtils.isNotBlank(nextView.toString())) {
			final OneClickButton btnNext = new OneClickButton(applicationContext.getMessage("btnNext", null, UI.getCurrent().getLocale()), FontAwesome.ARROW_CIRCLE_O_RIGHT);
			btnNext.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
			btnNext.addClickListener(e -> uiController.navigateTo(nextView.toString()));
			titleLayout.addComponent(btnNext);
			titleLayout.setComponentAlignment(btnNext, Alignment.MIDDLE_RIGHT);
		}
	}

	/**
	 * Ajoute un bouton
	 * @param b
	 * @param alignment
	 */
	public void addGenericButton(final OneClickButton b, final Alignment alignment) {
		buttonLayout.addComponent(b);
		buttonLayout.setComponentAlignment(b, alignment);
	}

	/**
	 * Rend le layout de bouton visible ou invisible
	 * @param visible
	 */
	public void setButtonVisible(final Boolean visible) {
		buttonLayout.setVisible(visible);
	}

	/**
	 * Ajoute un composant
	 * @param c
	 */
	public void addGenericComponent(final Component c) {
		globalLayout.addComponent(c);
	}

	/**
	 * Met le composant en ratio 1
	 * @param c
	 */
	public void setGenericExpandRatio(final Component c) {
		globalLayout.setExpandRatio(c, 1);
	}

	/**
	 * Redimensionne le layout pour les ecrans sans tables
	 * @param isFull
	 */
	public void setGenericLayoutSizeFull(final Boolean isFull) {
		if (isFull) {
			globalLayout.setSizeFull();
		} else {
			globalLayout.setSizeUndefined();
			globalLayout.setWidth(100, Unit.PERCENTAGE);
		}
	}

	/**
	 * Met à jour la vue
	 * @param  titre
	 * @param  checkCandidat
	 * @param  lockControl
	 * @return               true si la vue doit etre mise a jour ou si un message d'erreur doit s'afficher
	 */
	public Boolean majView(final String titre, final Boolean checkCandidat, final String lockControl) {
		final Authentication auth = userController.getCurrentAuthentication();
		cptMin = candidatController.getCompteMinima();

		if (cptMin == null) {
			/* On regarde si on a le droit d'aller chercher dans l'archivage */
			if (userController.isGestionnaireCandidat(auth) || userController.isGestionnaireCandidatLS(auth)) {
				cptMin = candidatController.getCompteMinimaForAllCampagne();
				isArchive = true;
			}
		}
		final String error = candidatController.getErrorView(cptMin);
		if (error != null) {
			errorLabel.setValue(error);
			errorLabel.setVisible(true);
			globalLayout.setVisible(false);
			return false;
		} else {
			if (userController.isGestionnaireCandidat(auth) || userController.isGestionnaireCandidatLS(auth)) {
				MainUI.getCurrent().checkConcordanceCandidat(cptMin.getNumDossierOpiCptMin());
			}

			candidat = cptMin.getCandidat();
			if (checkCandidat) {
				final String errorCandidat = candidatController.getErrorCandidat(candidat);
				if (errorCandidat != null) {
					errorLabel.setValue(errorCandidat);
					errorLabel.setVisible(true);
					globalLayout.setVisible(false);
					return false;
				}
			}
			errorLabel.setVisible(false);
			globalLayout.setVisible(true);
			title.setValue(applicationContext.getMessage("candidat.title", new Object[] { candidatController.getLibelleTitle(cptMin) }, UI.getCurrent().getLocale()) + " - " + titre);
			if (isArchive) {
				title.setValue(title.getValue() + " " + applicationContext.getMessage("candidat.archive.complement", null, UI.getCurrent().getLocale()));
			}

			if (lockControl != null) {
				final String lockError = candidatController.getLockError(cptMin, lockControl);
				if (lockError != null) {
					buttonLayout.setVisible(false);
					if (!userController.isGestionnaireCandidatLS(auth)) {
						lockLabel.setValue(lockError);
						lockLabel.setVisible(true);
					}
				}
			}

			/* Verification que l'utilisateur a les droits d'ecriture */
			if (!userController.isCandidat(auth) && !userController.isGestionnaireCandidat(auth)) {
				isLectureSeule = true;
			}

			if (isLectureSeule || isArchive) {
				buttonLayout.setVisible(false);
			}
		}
		return true;
	}
}
