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
package fr.univlorraine.ecandidat.vaadin.components;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;

/** Layout de connexion pour un anonymous
 * 
 * @author Kevin Hergalant */
@Configurable(preConstruction = true)
public class ConnexionLayout extends VerticalLayout {

	/** serialVersionUID */
	private static final long serialVersionUID = -3178844375518329434L;

	@Resource
	private transient ApplicationContext applicationContext;

	/** Listeners */
	private CasListener casListener;
	private StudentListener studentListener;
	private ForgotPasswordListener forgotPasswordListener;
	private ForgotCodeActivationListener forgotCodeActivationListener;
	private CreateCompteListener createCompteListener;

	/* Composants */
	private OneClickButton logBtn = new OneClickButton(FontAwesome.SIGN_OUT);
	private OneClickButton logBtnEc = new OneClickButton(FontAwesome.SIGN_OUT);
	private OneClickButton passBtn = new OneClickButton(FontAwesome.KEY);
	private OneClickButton codeActBtn = new OneClickButton(FontAwesome.LOCK);
	private OneClickButton createBtn = new OneClickButton(FontAwesome.MAGIC);

	public void addCasListener(final CasListener casListener) {
		this.casListener = casListener;
		logBtn.setVisible(true);
	}

	public void addStudentListener(final StudentListener studentListener) {
		this.studentListener = studentListener;
		logBtnEc.setVisible(true);
	}

	public void addForgotCodeActivationListener(final ForgotCodeActivationListener forgotCodeActivationListener) {
		this.forgotCodeActivationListener = forgotCodeActivationListener;
		codeActBtn.setVisible(true);
	}

	public void addForgotPasswordListener(final ForgotPasswordListener forgotPasswordListener) {
		this.forgotPasswordListener = forgotPasswordListener;
		passBtn.setVisible(true);
	}

	public void addCreateCompteListener(final CreateCompteListener createCompteListener) {
		this.createCompteListener = createCompteListener;
		createBtn.setVisible(true);
	}

	public ConnexionLayout() {
		init();
	}

	public void updateLibelle() {
		String libBtnConnect = applicationContext.getMessage("btnConnect.candidat", null, UI.getCurrent().getLocale());
		String libConnectMdp = applicationContext.getMessage("accueilView.connect.mdp", null, UI.getCurrent().getLocale());
		String libConnectUser = applicationContext.getMessage("accueilView.connect.user", null, UI.getCurrent().getLocale());
		panelStudent.setCaption(applicationContext.getMessage("accueilView.title.etu", new Object[] {
				applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()));
		panelNotStudent.setCaption(applicationContext.getMessage("accueilView.title.nonetu", new Object[] {
				applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()));
		labelConnect.setValue(applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale()));
		logBtn.setCaption(libBtnConnect);
		createBtn.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		passBtn.setCaption(applicationContext.getMessage("compteMinima.id.oublie.title", null, UI.getCurrent().getLocale()));
		codeActBtn.setCaption(applicationContext.getMessage("compteMinima.code.oublie.title", null, UI.getCurrent().getLocale()));
		logBtnEc.setCaption(libBtnConnect);
		password.setCaption(libConnectMdp);
		password.setInputPrompt(libConnectMdp);
		labelEc.setValue(applicationContext.getMessage("accueilView.connect.ec", null, UI.getCurrent().getLocale()));
		user.setCaption(libConnectUser);
		user.setInputPrompt(libConnectUser);
	}

	private Panel panelStudent = new Panel();
	private Panel panelNotStudent = new Panel();
	private Label labelConnect = new Label();
	private PasswordField password = new PasswordField();
	private TextField user = new TextField();
	private Label labelEc = new Label("", ContentMode.HTML);

	public void init() {
		setSpacing(true);

		VerticalLayout vlStudent = new VerticalLayout();
		vlStudent.setSpacing(true);
		vlStudent.setMargin(true);
		VerticalLayout vlNotStudent = new VerticalLayout();
		vlNotStudent.setSpacing(true);
		vlNotStudent.setMargin(true);
		panelStudent.setContent(vlStudent);
		panelStudent.addStyleName(StyleConstants.PANEL_COLORED);
		panelNotStudent.setContent(vlNotStudent);
		panelNotStudent.addStyleName(StyleConstants.PANEL_COLORED);
		this.addComponent(panelStudent);
		this.addComponent(panelNotStudent);

		HorizontalLayout hlConnect = new HorizontalLayout();
		hlConnect.setSpacing(true);
		hlConnect.addComponent(labelConnect);
		hlConnect.setComponentAlignment(labelConnect, Alignment.MIDDLE_LEFT);

		/* Connexion CAS */
		logBtn.setVisible(false);
		hlConnect.addComponent(logBtn);
		hlConnect.setComponentAlignment(logBtn, Alignment.MIDDLE_CENTER);
		logBtn.addClickListener(e -> {
			if (casListener != null) {
				casListener.connectCAS();
			}
		});

		vlStudent.addComponent(hlConnect);

		/* Connexion eCandidat */
		vlNotStudent.addComponent(labelEc);
		user.setWidth(200, Unit.PIXELS);
		user.setRequired(true);
		user.setValue("");
		// user.setValue("235A1TA2");

		// Create the password input field
		password.setWidth(200, Unit.PIXELS);
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");

		// password.setValue("123456");

		vlNotStudent.addComponent(user);
		vlNotStudent.addComponent(password);

		logBtnEc.setVisible(false);
		logBtnEc.addClickListener(e -> {
			if (studentListener != null) {
				studentListener.connectStudent(user.getValue(), password.getValue());
			}
		});
		vlNotStudent.addComponent(logBtnEc);

		passBtn.setVisible(false);
		passBtn.addStyleName(ValoTheme.BUTTON_LINK);
		passBtn.addStyleName(ValoTheme.BUTTON_SMALL);
		vlNotStudent.addComponent(passBtn);
		passBtn.addClickListener(e -> {
			if (forgotPasswordListener != null) {
				forgotPasswordListener.forgot();
			}
		});

		codeActBtn.setVisible(false);
		codeActBtn.addStyleName(ValoTheme.BUTTON_LINK);
		codeActBtn.addStyleName(ValoTheme.BUTTON_SMALL);
		vlNotStudent.addComponent(codeActBtn);
		codeActBtn.addClickListener(e -> {
			if (forgotCodeActivationListener != null) {
				forgotCodeActivationListener.forgot();
			}
		});

		createBtn.setVisible(false);
		createBtn.addStyleName(ValoTheme.BUTTON_LINK);
		createBtn.addStyleName(ValoTheme.BUTTON_SMALL);
		vlNotStudent.addComponent(createBtn);
		createBtn.addClickListener(e -> {
			if (createCompteListener != null) {
				createCompteListener.createCompte();
			}
		});
	}

	/** @param login
	 */
	public void setLogin(final String login) {
		if (login != null && !login.equals("")) {
			user.setValue(login);
			password.setValue("123");
		} else {
			user.setValue("");
			password.setValue("");
		}
	}

	/** AJoute ou enleve le shortcut
	 * 
	 * @param hasShortcut
	 */
	public void setClickShortcut(final boolean hasShortcut) {
		if (hasShortcut) {
			logBtnEc.setClickShortcut(KeyCode.ENTER);
		} else {
			logBtnEc.removeClickShortcut();
		}
	}

	/** Interface pour les listeners du bouton cas. */
	public interface CasListener extends Serializable {

		/** Appelé lorsque cas est cliqué. */
		public void connectCAS();

	}

	/** Interface pour les listeners du bouton de connexion. */
	public interface StudentListener extends Serializable {

		/** Appelé lorsque le bouton de connexion est cliqué. */
		public void connectStudent(String user, String pwd);

	}

	/** Interface pour les listeners du bouton d'oublie. */
	public interface ForgotCodeActivationListener extends Serializable {

		/** Appelé lorsque cas est cliqué. */
		public void forgot();

	}

	/** Interface pour les listeners du bouton d'oublie. */
	public interface ForgotPasswordListener extends Serializable {

		/** Appelé lorsque cas est cliqué. */
		public void forgot();

	}

	/** Interface pour les listeners du bouton de creation. */
	public interface CreateCompteListener extends Serializable {

		/** Appelé lorsque cas est cliqué. */
		public void createCompte();

	}
}
