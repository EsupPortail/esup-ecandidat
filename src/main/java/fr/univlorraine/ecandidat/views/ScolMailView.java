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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des mails par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolMailView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolMailView extends VerticalLayout implements View, EntityPushListener<Mail>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 7549088461110559571L;

	public static final String NAME = "scolMailView";

	public static final String[] MAIL_FIELDS_ORDER = {Mail_.codMail.getName(),Mail_.libMail.getName(),Mail_.tesMail.getName(),Mail_.typeAvis.getName()+"."+TypeAvis_.libelleTypAvis.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient EntityPusher<Mail> mailEntityPusher;

	/* Composants */
	private TableFormating mailModelTable = new TableFormating();
	private TableFormating mailTypeDecTable = new TableFormating();
	
	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {		
		
		/* Style */
		setSizeFull();
		setSpacing(true);		
		
		/*Layout des mails*/
		VerticalLayout layoutMailModel = new VerticalLayout();
		layoutMailModel.setSizeFull();
		layoutMailModel.setSpacing(true);
		layoutMailModel.setMargin(true);
		
		/*Layout des typ decision*/
		VerticalLayout layoutMailTypeDec = new VerticalLayout();
		layoutMailTypeDec.setSizeFull();
		layoutMailTypeDec.setSpacing(true);
		layoutMailTypeDec.setMargin(true);
		
		/*Le layout a onglet*/
		TabSheet sheet = new TabSheet();
		sheet.setImmediate(true);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		addComponent(sheet);
		sheet.setSizeFull();
		
		sheet.addTab(layoutMailModel, applicationContext.getMessage("mail.model.title", null, UI.getCurrent().getLocale()),FontAwesome.ENVELOPE_O);
		sheet.addTab(layoutMailTypeDec, applicationContext.getMessage("mail.typdec.title", null, UI.getCurrent().getLocale()),FontAwesome.ENVELOPE);
		
		/*Populate le layoutMailModel*/
		populateMailModelLayout(layoutMailModel);
		
		/*Populate le layoutMailModel*/
		populateMailTypeDecLayout(layoutMailTypeDec);
		
		
		/* Inscrit la vue aux mises à jour de mail */
		mailEntityPusher.registerEntityPushListener(this);
	}
	
	private void populateMailTypeDecLayout(VerticalLayout layoutMailTypeDec) {
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layoutMailTypeDec.addComponent(buttonsLayout);
		
		OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("mail.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			mailController.editNewMail();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		OneClickButton btnEditMailTypeDec = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditMailTypeDec.setEnabled(false);
		btnEditMailTypeDec.addClickListener(e -> {
			if (mailTypeDecTable.getValue() instanceof Mail) {
				mailController.editMail((Mail) mailTypeDecTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEditMailTypeDec);
		buttonsLayout.setComponentAlignment(btnEditMailTypeDec, Alignment.MIDDLE_CENTER);
		
		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (mailTypeDecTable.getValue() instanceof Mail) {
				mailController.deleteMail((Mail) mailTypeDecTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des mails avec type de decision */
		BeanItemContainer<Mail> container = new BeanItemContainer<Mail>(Mail.class, mailController.getMailsTypeDecScol());
		container.addNestedContainerProperty(Mail_.typeAvis.getName()+"."+TypeAvis_.libelleTypAvis.getName());
		mailTypeDecTable.setContainerDataSource(container);		
		mailTypeDecTable.addBooleanColumn(Mail_.tesMail.getName());
		mailTypeDecTable.setSizeFull();
		mailTypeDecTable.setVisibleColumns((Object[]) MAIL_FIELDS_ORDER);
		for (String fieldName : MAIL_FIELDS_ORDER) {
			mailTypeDecTable.setColumnHeader(fieldName, applicationContext.getMessage("mail.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		mailTypeDecTable.setSortContainerPropertyId(Mail_.codMail.getName());
		mailTypeDecTable.setColumnCollapsingAllowed(true);
		mailTypeDecTable.setColumnReorderingAllowed(true);
		mailTypeDecTable.setSelectable(true);
		mailTypeDecTable.setImmediate(true);
		mailTypeDecTable.addItemSetChangeListener(e -> mailTypeDecTable.sanitizeSelection());
		mailTypeDecTable.addValueChangeListener(e -> {
			/* Les boutons d'édition de mail sont actifs seulement si un mail est sélectionnée. */
			boolean mailIsSelected = mailTypeDecTable.getValue() instanceof Mail;
			btnEditMailTypeDec.setEnabled(mailIsSelected);
			btnDelete.setEnabled(mailIsSelected);
		});
		mailTypeDecTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				mailTypeDecTable.select(e.getItemId());
				btnEditMailTypeDec.click();
			}
		});
		layoutMailTypeDec.addComponent(mailTypeDecTable);
		layoutMailTypeDec.setExpandRatio(mailTypeDecTable, 1);
	}

	/**Rempli le layout de mail modele
	 * @param layoutMailModel
	 */
	private void populateMailModelLayout(VerticalLayout layoutMailModel) {
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layoutMailModel.addComponent(buttonsLayout);


		OneClickButton btnEditMailModel = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditMailModel.setEnabled(false);
		btnEditMailModel.addClickListener(e -> {
			if (mailModelTable.getValue() instanceof Mail) {
				mailController.editMail((Mail) mailModelTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEditMailModel);
		buttonsLayout.setComponentAlignment(btnEditMailModel, Alignment.MIDDLE_LEFT);

		/* Table des mails */
		BeanItemContainer<Mail> container = new BeanItemContainer<Mail>(Mail.class, mailController.getMailsModels());
		container.addNestedContainerProperty(Mail_.typeAvis.getName()+"."+TypeAvis_.libelleTypAvis.getName());
		mailModelTable.setContainerDataSource(container);
		mailModelTable.addBooleanColumn(Mail_.tesMail.getName());
		mailModelTable.setSizeFull();
		mailModelTable.setVisibleColumns((Object[]) MAIL_FIELDS_ORDER);
		for (String fieldName : MAIL_FIELDS_ORDER) {
			mailModelTable.setColumnHeader(fieldName, applicationContext.getMessage("mail.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		mailModelTable.setSortContainerPropertyId(Mail_.codMail.getName());
		mailModelTable.setColumnCollapsingAllowed(true);
		mailModelTable.setColumnReorderingAllowed(true);
		mailModelTable.setSelectable(true);
		mailModelTable.setImmediate(true);
		mailModelTable.addItemSetChangeListener(e -> mailModelTable.sanitizeSelection());
		mailModelTable.addValueChangeListener(e -> {
			/* Les boutons d'édition de mail sont actifs seulement si un mail est sélectionnée. */
			boolean mailIsSelected = mailModelTable.getValue() instanceof Mail;
			btnEditMailModel.setEnabled(mailIsSelected);			
		});
		mailModelTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				mailModelTable.select(e.getItemId());
				btnEditMailModel.click();
			}
		});
		layoutMailModel.addComponent(mailModelTable);
		layoutMailModel.setExpandRatio(mailModelTable, 1);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de mail */
		mailEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Mail entity) {
		if (entity.getTemIsModeleMail()){
			mailModelTable.removeItem(entity);
			mailModelTable.addItem(entity);
			mailModelTable.sort();
		}else{
			mailTypeDecTable.removeItem(entity);
			mailTypeDecTable.addItem(entity);
			mailTypeDecTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Mail entity) {
		if (entity.getTemIsModeleMail()){
			mailModelTable.removeItem(entity);
			mailModelTable.addItem(entity);
			mailModelTable.sort();
		}else{
			mailTypeDecTable.removeItem(entity);
			mailTypeDecTable.addItem(entity);
			mailTypeDecTable.sort();
		}
		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Mail entity) {
		if (entity.getTemIsModeleMail()){
			mailModelTable.removeItem(entity);
		}else{
			mailTypeDecTable.removeItem(entity);
		}
	}
}
