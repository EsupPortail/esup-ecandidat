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
package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de confirmation de candidature par un gestionnaire
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandOdfCandidatureWindow extends Window {


	/** serialVersionUID **/
	private static final long serialVersionUID = 6987137911454340251L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private OptionGroup optionGroupAction = new OptionGroup();
	

	/*Listener*/
	private OdfCandidatureListener odfCandidatureListener;
	/**
	 * Crée une fenêtre de choix pour le gestionnaire : proposition ou candidature simple
	 * @param message
	 */
	public CtrCandOdfCandidatureWindow(String message) {
		/* Style */
		setWidth(630, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.gest.window", null, UI.getCurrent().getLocale()));

		/* Texte */
		layout.addComponent(new Label(message));
		layout.addComponent(new Label(applicationContext.getMessage("candidature.gest.window.choice", null, UI.getCurrent().getLocale())));
		
		/*Le container d'options*/
		BeanItemContainer<SimpleTablePresentation> optContainer = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
		SimpleTablePresentation optionClassique = new SimpleTablePresentation(ConstanteUtils.OPTION_CLASSIQUE,applicationContext.getMessage("candidature.gest.window.choice.classique", null, UI.getCurrent().getLocale()),null);
		SimpleTablePresentation optionProposition = new SimpleTablePresentation(ConstanteUtils.OPTION_PROP,applicationContext.getMessage("candidature.gest.window.choice.proposition", null, UI.getCurrent().getLocale()),null);
		optContainer.addItem(optionClassique);
		optContainer.addItem(optionProposition);
		
		optionGroupAction.setContainerDataSource(optContainer);
		optionGroupAction.addStyleName(StyleConstants.OPTION_GROUP_HORIZONTAL);
		optionGroupAction.setItemCaptionPropertyId(SimpleTablePresentation.CHAMPS_TITLE);
		optionGroupAction.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		optionGroupAction.setValue(optionClassique);
		
		layout.addComponent(optionGroupAction);
		layout.setComponentAlignment(optionGroupAction, Alignment.MIDDLE_CENTER);
		

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		OneClickButton btnNon = new OneClickButton(applicationContext.getMessage("confirmWindow.btnNon", null, UI.getCurrent().getLocale()),FontAwesome.TIMES);
		btnNon.addClickListener(e -> close());
		buttonsLayout.addComponent(btnNon);
		buttonsLayout.setComponentAlignment(btnNon, Alignment.MIDDLE_LEFT);

		OneClickButton btnOui = new OneClickButton(applicationContext.getMessage("confirmWindow.btnOui", null, UI.getCurrent().getLocale()),FontAwesome.CHECK);
		btnOui.setIcon(FontAwesome.CHECK);
		btnOui.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnOui.addClickListener(e -> {
			SimpleTablePresentation option = (SimpleTablePresentation)optionGroupAction.getValue();
			odfCandidatureListener.btnOkClick(option.getCode());
			close();
		});
		buttonsLayout.addComponent(btnOui);
		buttonsLayout.setComponentAlignment(btnOui, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Défini le 'OdfCandidatureListener' utilisé
	 * @param odfCandidatureListener
	 */
	public void addOdfCandidatureListener(OdfCandidatureListener odfCandidatureListener) {
		this.odfCandidatureListener = odfCandidatureListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui
	 */
	public interface OdfCandidatureListener extends Serializable {
		
		/** Appelé lorsque Oui est cliqué.
		/**
		 * @param type : proposition ou candidature classique
		 */
		public void btnOkClick(String type);

	}
}
