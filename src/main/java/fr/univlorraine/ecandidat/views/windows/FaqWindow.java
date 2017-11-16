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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre d'édition de faq
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class FaqWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 7792672257993881113L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient I18nController i18nController;
	
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre FAQ
	 */
	public FaqWindow() {
		/* Style */
		setModal(true);
		setWidth(800,Unit.PIXELS);
		setHeight(70,Unit.PERCENTAGE);
		setResizable(true);
		setClosable(true);

		/* Panel */
		Panel panel = new Panel();
		panel.setSizeFull();
		
		/* Titre */
		VerticalLayout layoutItem = new VerticalLayout();
		layoutItem.setMargin(true);
		layoutItem.setWidth(100, Unit.PERCENTAGE);
		layoutItem.setSpacing(true);
		panel.setContent(layoutItem);
		
		setCaption(applicationContext.getMessage("faqWindow.title", null, UI.getCurrent().getLocale()));
		cacheController.getFaq().forEach(faq->{
			Label labelReponse = new Label(i18nController.getI18nTraduction(faq.getI18nReponse(),UI.getCurrent().getLocale()),ContentMode.HTML);
			labelReponse.setWidth(100, Unit.PERCENTAGE);
			labelReponse.setVisible(false);
			OneClickButton btnQuestion = new OneClickButton(i18nController.getI18nTraduction(faq.getI18nQuestion(),UI.getCurrent().getLocale()), FontAwesome.CERTIFICATE);
			btnQuestion.addStyleName(StyleConstants.BUTON_ALIGN_LEFT);
			btnQuestion.setWidth(100, Unit.PERCENTAGE);
			btnQuestion.addClickListener(e->{
				if (labelReponse.isVisible()){
					labelReponse.setVisible(false);
					center();
				}else{
					labelReponse.setVisible(true);
					center();
				}
			});
			btnQuestion.addStyleName(ValoTheme.BUTTON_LINK);
			layoutItem.addComponent(btnQuestion);
			layoutItem.setComponentAlignment(btnQuestion, Alignment.MIDDLE_LEFT);
			layoutItem.addComponent(labelReponse);
			layoutItem.setComponentAlignment(labelReponse, Alignment.MIDDLE_LEFT);
		});
		
		/* Layout */		
		/*try {
			String codLangue = "";
			Locale locale = UI.getCurrent().getLocale();
			if (locale!=null && !locale.getLanguage().equals(tableRefController.getLangueDefault().getCodLangue())){
				codLangue = "_"+locale.getLanguage();
			}
			VerticalLayout layout = new VerticalLayout();
			layout.setWidth(100, Unit.PERCENTAGE);
			layout.setSizeFull();
			layout.setMargin(true);
			layout.setSpacing(true);
			setContent(layout);
			CustomLayout cLayout = new CustomLayout(getClass().getResourceAsStream("/i18n/pages/Assistance"+codLangue+".html"));
			layout.addComponent(cLayout);
		} catch (Exception e) {
			
		}*/
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.addComponent(panel);
		layout.setExpandRatio(panel, 1);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_CENTER);
		
		setContent(layout);
		/* Centre la fenêtre */
		center();
	}

}
