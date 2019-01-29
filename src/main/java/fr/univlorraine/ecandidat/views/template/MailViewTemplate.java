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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Page de gestion des mails par la scolarité
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class MailViewTemplate extends VerticalLayout {

	public static final String NAME = "scolMailView";

	public static final String[] MAIL_FIELDS_ORDER = {Mail_.codMail.getName(), Mail_.libMail.getName(), Mail_.tesMail.getName(), Mail_.typeAvis.getName() + "." + TypeAvis_.libelleTypAvis.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MailController mailController;

	/* Composants */
	protected Label title = new Label();
	protected BeanItemContainer<Mail> container = new BeanItemContainer<>(Mail.class);
	protected TableFormating mailTable = new TableFormating(null, container);
	protected OneClickButton btnNew = new OneClickButton(FontAwesome.PLUS);
	protected OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	protected OneClickButton btnDelete = new OneClickButton(FontAwesome.TRASH_O);
	protected HorizontalLayout buttonsLayout = new HorizontalLayout();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Titre */
		title.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(title);

		/* Layout bouton */
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		/* Bouton new */
		btnNew.setCaption(applicationContext.getMessage("mail.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNew.setEnabled(true);
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		/* Edit */
		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (mailTable.getValue() instanceof Mail) {
				mailController.editMail((Mail) mailTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		/* Delete */
		btnDelete.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (mailTable.getValue() instanceof Mail) {
				mailController.deleteMail((Mail) mailTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table */
		container.addNestedContainerProperty(Mail_.typeAvis.getName() + "." + TypeAvis_.libelleTypAvis.getName());
		mailTable.setContainerDataSource(container);
		mailTable.addBooleanColumn(Mail_.tesMail.getName());
		mailTable.setSizeFull();
		mailTable.setVisibleColumns((Object[]) MAIL_FIELDS_ORDER);
		for (String fieldName : MAIL_FIELDS_ORDER) {
			mailTable.setColumnHeader(fieldName, applicationContext.getMessage("mail.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		mailTable.setSortContainerPropertyId(Mail_.codMail.getName());
		mailTable.setColumnCollapsingAllowed(true);
		mailTable.setColumnReorderingAllowed(true);
		mailTable.setSelectable(true);
		mailTable.setImmediate(true);
		mailTable.addItemSetChangeListener(e -> mailTable.sanitizeSelection());
		mailTable.addValueChangeListener(e -> {
			/* Les boutons d'édition de mail sont actifs seulement si un mail est sélectionnée. */
			boolean mailIsSelected = mailTable.getValue() instanceof Mail;
			btnEdit.setEnabled(mailIsSelected);
			btnDelete.setEnabled(mailIsSelected);
		});
		addComponent(mailTable);
		setExpandRatio(mailTable, 1);
		mailTable.sort();
	}
}
