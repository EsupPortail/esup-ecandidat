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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.MailViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des mails par la scolarité
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolMailTypeDecView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolMailTypeDecView extends MailViewTemplate implements View, EntityPushListener<Mail> {

	public static final String NAME = "scolMailTypDecView";

	public static final String[] MAIL_FIELDS_ORDER = {Mail_.codMail.getName(), Mail_.libMail.getName(), Mail_.tesMail.getName(), Mail_.typeAvis.getName() + "." + TypeAvis_.libelleTypAvis.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient EntityPusher<Mail> mailEntityPusher;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		/* Init à partir du template */
		super.init();

		/* Titre */
		title.setValue(applicationContext.getMessage("mail.typdec.title", null, UI.getCurrent().getLocale()));

		container.addAll(mailController.getMailsByCtrCand(false, null));

		btnNew.addClickListener(e -> {
			mailController.editNewMail(null);
		});

		mailTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				mailTable.select(e.getItemId());
				btnEdit.click();
			}
		});

		btnNew.setVisible(true);
		btnDelete.setVisible(true);

		/* Inscrit la vue aux mises à jour de mail */
		mailEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
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
	public void entityPersisted(final Mail entity) {
		if (entity.getCentreCandidature() == null && !entity.getTemIsModeleMail()) {
			mailTable.removeItem(entity);
			mailTable.addItem(entity);
			mailTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Mail entity) {
		if (entity.getCentreCandidature() == null && !entity.getTemIsModeleMail()) {
			mailTable.removeItem(entity);
			mailTable.addItem(entity);
			mailTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Mail entity) {
		if (entity.getCentreCandidature() == null && !entity.getTemIsModeleMail()) {
			mailTable.removeItem(entity);
		}
	}
}
