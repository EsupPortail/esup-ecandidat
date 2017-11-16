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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.InfoPersoListener;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des infos perso du candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CandidatInfoPersoView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatInfoPersoView extends CandidatViewTemplate implements View, InfoPersoListener{	

	/** serialVersionUID **/
	private static final long serialVersionUID = 5842232696061936906L;

	public static final String NAME = "candidatInfoPersoView";

	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.CHAMPS_TITLE,SimpleTablePresentation.CHAMPS_VALUE};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	
	/* Composants */
	private BeanItemContainer<SimpleTablePresentation> infoPersoContainer = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private TableFormating infoPersoTable = new TableFormating(null, infoPersoContainer);
	
	private OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	private Label noInfoLabel = new Label();
	private Label labelMail = new Label();
	private OneClickButton changeContactBtn = new OneClickButton(FontAwesome.ENVELOPE_O);

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {		
		super.init();
		setNavigationButton(null,CandidatAdresseView.NAME);
		/*Adresse de contact*/
		HorizontalLayout contactLayout = new HorizontalLayout();
		contactLayout.setSpacing(true);
		
		contactLayout.addComponent(labelMail);
		contactLayout.setComponentAlignment(labelMail, Alignment.MIDDLE_LEFT);
		Authentication auth = userController.getCurrentAuthentication();
		if (userController.isCandidat(auth) || userController.isGestionnaireCandidat(auth)){
			changeContactBtn.setCaption(applicationContext.getMessage("infoperso.mail.btn", null, UI.getCurrent().getLocale()));
			changeContactBtn.addStyleName(ValoTheme.BUTTON_LINK);
			changeContactBtn.addStyleName(ValoTheme.BUTTON_SMALL);
			changeContactBtn.addClickListener(e->{
				candidatController.editMail(cptMin);
			});
			contactLayout.addComponent(changeContactBtn);
			contactLayout.setComponentAlignment(changeContactBtn, Alignment.MIDDLE_LEFT);
		}
		
		addGenericComponent(contactLayout);
		
		/*Edition des donneés*/	
		btnEdit.setCaption(applicationContext.getMessage("infoperso.edit.btn", null, UI.getCurrent().getLocale()));
		btnEdit.addClickListener(e -> {
			candidatController.editCandidat(cptMin, this);
		});
		addGenericButton(btnEdit,Alignment.MIDDLE_LEFT);		
		
		noInfoLabel.setValue(applicationContext.getMessage("infoperso.noinfo", null, UI.getCurrent().getLocale()));
		addGenericComponent(noInfoLabel);
		
		/*Table de présentation*/
		infoPersoTable.setSizeFull();
		infoPersoTable.addGeneratedColumn(SimpleTablePresentation.CHAMPS_VALUE, new Table.ColumnGenerator() {
            /**serialVersionUID**/
			private static final long serialVersionUID = -3483685206189347289L;

			@Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
				SimpleTablePresentation stp = (SimpleTablePresentation)itemId;
				if (stp.getCode().equals(Candidat_.langue.getName())){
					Langue langue = (Langue) stp.getValue();
					HorizontalLayout langueLayout = new HorizontalLayout();
					langueLayout.setSpacing(true);
					Image img = new Image(null, new ThemeResource("images/flags/"+langue.getCodLangue()+".png"));
					Label label = new Label(langue.getLibLangue());
					langueLayout.addComponent(img);
					langueLayout.addComponent(label);
					langueLayout.setComponentAlignment(img, Alignment.MIDDLE_LEFT);
					langueLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
					return langueLayout;
				}else{
					return stp.getValue();
				}									
            }            
        });
		infoPersoTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		infoPersoTable.setColumnCollapsingAllowed(false);
		infoPersoTable.setColumnReorderingAllowed(false);
		infoPersoTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		infoPersoTable.setSelectable(false);
		infoPersoTable.setImmediate(true);		
		infoPersoTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		infoPersoTable.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addGenericComponent(infoPersoTable);
		setGenericExpandRatio(infoPersoTable);
	}

	/**
	 * Met a jour les composants
	 */
	private void majComponentsInfoPerso(Candidat candidat){
		labelMail.setValue(applicationContext.getMessage("infoperso.mail", new Object[]{cptMin.getMailPersoCptMin()}, UI.getCurrent().getLocale()));
		if (candidat == null){
			infoPersoContainer.removeAllItems();
			infoPersoTable.setVisible(false);
			noInfoLabel.setVisible(true);
			setGenericLayoutSizeFull(false);
		}else{
			infoPersoContainer.removeAllItems();
			List<SimpleTablePresentation> liste = candidatController.getInformationsPerso(candidat);
			infoPersoContainer.addAll(liste);
			infoPersoTable.setPageLength(liste.size());
			infoPersoTable.setVisible(true);
			noInfoLabel.setVisible(false);
			setGenericLayoutSizeFull(true);
		}		
		if (candidatController.isCandidatAndHaveCandidature(candidat)){
			btnEdit.setEnabled(false);
		}
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("infoperso.title", null, UI.getCurrent().getLocale()), false,  ConstanteUtils.LOCK_INFOS_PERSO)){
			majComponentsInfoPerso(candidat);
		}
		if (isLectureSeule || isArchive){
			changeContactBtn.setVisible(false);
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_INFOS_PERSO);
		super.detach();
		
	}


	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.InfoPersoListener#infoPersoModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidat, java.lang.Boolean)
	 */
	@Override
	public void infoPersoModified(Candidat candidat, Boolean langueChanged) {
		/*Changement de langue*/
		if (langueChanged){
			title.setValue(applicationContext.getMessage("infoperso.title", new Object[]{cptMin.getNumDossierOpiCptMin()}, UI.getCurrent().getLocale()));
			noInfoLabel.setValue(applicationContext.getMessage("infoperso.noinfo", null, UI.getCurrent().getLocale()));
			labelMail.setValue(applicationContext.getMessage("infoperso.mail", new Object[]{cptMin.getMailPersoCptMin()}, UI.getCurrent().getLocale()));
			changeContactBtn.setCaption(applicationContext.getMessage("infoperso.mail.btn", null, UI.getCurrent().getLocale()));
			btnEdit.setCaption(applicationContext.getMessage("infoperso.edit.btn", null, UI.getCurrent().getLocale()));
		}
		cptMin.setCandidat(candidat);
		majComponentsInfoPerso(candidat);
	}

}
