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
package fr.univlorraine.ecandidat.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.ConnexionLayout;
import fr.univlorraine.ecandidat.views.windows.CandidatIdOublieWindow;

/**
 * Page de gestion du compte a minima du candidat
 * @author Kevin Hergalant
 */
@SpringView(name = CandidatCompteMinimaView.NAME)
public class CandidatCompteMinimaView extends VerticalLayout implements View {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1892026915407604201L;

	public static final String NAME = "candidatCompteMinimaView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient I18nController i18nController;

	private final Label restResult = new Label();
	private final Label labelTitle = new Label();
	private final Label labelAccueil = new Label();
	private String restResultParam;
	private final ConnexionLayout connexionLayout = new ConnexionLayout();

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		/* Titre */
		final HorizontalLayout hlLangue = new HorizontalLayout();
		hlLangue.setWidth(100, Unit.PERCENTAGE);
		hlLangue.setSpacing(true);

		/* Le titre */
		labelTitle.addStyleName(StyleConstants.VIEW_TITLE);
		hlLangue.addComponent(labelTitle);
		hlLangue.setExpandRatio(labelTitle, 1);
		hlLangue.setComponentAlignment(labelTitle, Alignment.MIDDLE_LEFT);

		if (cacheController.getLangueEnServiceWithoutDefault().size() > 0) {
			final Langue langueDef = cacheController.getLangueDefault();
			final Image flagDef = new Image(null, new ThemeResource("images/flags/" + langueDef.getCodLangue() + ".png"));
			flagDef.addClickListener(e -> updateLangue(langueDef));
			flagDef.addStyleName(StyleConstants.CLICKABLE);
			hlLangue.addComponent(flagDef);
			hlLangue.setComponentAlignment(flagDef, Alignment.MIDDLE_CENTER);
			cacheController.getLangueEnServiceWithoutDefault().forEach(langue -> {
				final Image flag = new Image(null, new ThemeResource("images/flags/" + langue.getCodLangue() + ".png"));
				flag.addClickListener(e -> updateLangue(langue));
				flag.addStyleName(StyleConstants.CLICKABLE);
				hlLangue.addComponent(flag);
				hlLangue.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);

			});
		}

		addComponent(hlLangue);

		/* Panel scrollable de contenu */
		final Panel panelContent = new Panel();
		panelContent.setSizeFull();
		panelContent.addStyleName(ValoTheme.PANEL_BORDERLESS);
		addComponent(panelContent);
		setExpandRatio(panelContent, 1);

		final VerticalLayout vlContent = new VerticalLayout();
		vlContent.setSpacing(true);
		panelContent.setContent(vlContent);

		restResult.setContentMode(ContentMode.HTML);
		restResult.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		restResult.addStyleName(ValoTheme.LABEL_COLORED);
		restResult.setValue("");
		vlContent.addComponent(restResult);

		/* Texte */
		labelAccueil.setValue("");
		labelAccueil.setContentMode(ContentMode.HTML);
		vlContent.addComponent(labelAccueil);

		connexionLayout.addStyleName(StyleConstants.ACCUEIL_COMPTE_PANEL);
		connexionLayout.addCasListener(() -> userController.connectCAS());
		connexionLayout.addStudentListener((user, pwd) -> userController.connectCandidatInterne(user, pwd));
		connexionLayout.addForgotPasswordListener(() -> {
			UI.getCurrent().addWindow(new CandidatIdOublieWindow(ConstanteUtils.FORGOT_MODE_ID_OUBLIE));
		});
		connexionLayout.addForgotCodeActivationListener(() -> {
			UI.getCurrent().addWindow(new CandidatIdOublieWindow(ConstanteUtils.FORGOT_MODE_CODE_ACTIVATION));
		});
		vlContent.addComponent(connexionLayout);
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		userController.validSecurityUserCptMin();
		restResultParam = event.getParameters();
		if (restResultParam == null || restResultParam.equals("") || restResultParam.equals(ConstanteUtils.REST_VALID_ERROR)) {
			restResultParam = ConstanteUtils.REST_VALID_ERROR;
		}

		if (userController.isCandidat() && (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS))) {
			connexionLayout.setVisible(false);
			((MainUI) UI.getCurrent()).constructMainMenu();
		} else if (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS)) {
			connexionLayout.setVisible(true);
		} else {
			connexionLayout.setVisible(false);
		}
		updateLangue(cacheController.getLangueDefault());

		/* Modif mot de passe éventuellement */
		try {
			final MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(Page.getCurrent().getLocation()).build().getQueryParams();
			final List<String> paramInitPwd = parameters.get(ConstanteUtils.CPT_MIN_INIT_PWD_PARAM);
			if (paramInitPwd.size() != 0) {
				candidatController.reinitPwd(paramInitPwd.get(0));
			}
		} catch (final Exception ex) {

		}
	}

	/** Internationalisation-->calcul du texte a afficher */
	private void updateLangue(final Langue langue) {
		i18nController.changeLangue(langue);
		labelTitle.setValue(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		try {
			restResult.setValue(applicationContext.getMessage("compteMinima.valid." + restResultParam, null, UI.getCurrent().getLocale()));
		} catch (final Exception e) {
			restResult.setValue("");
		}
		String txtAccueil = "";
		final Authentication auth = userController.getCurrentAuthentication();
		if (userController.isCandidat(auth) && (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS))) {
			txtAccueil += applicationContext.getMessage("accueilView.connected", new Object[] { userController.getCurrentUserLogin(auth) }, UI.getCurrent().getLocale());
			txtAccueil += applicationContext.getMessage("accueilView.cand.connected", null, UI.getCurrent().getLocale());
		} else if (restResultParam.equals(ConstanteUtils.REST_VALID_ALREADY_VALID) || restResultParam.equals(ConstanteUtils.REST_VALID_SUCCESS)) {
			txtAccueil += applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale());
		}
		if (!txtAccueil.equals("")) {
			labelAccueil.setValue(txtAccueil);
			labelAccueil.setVisible(true);
		} else {
			labelAccueil.setVisible(false);
		}
		connexionLayout.updateLibelle();
	}
}
