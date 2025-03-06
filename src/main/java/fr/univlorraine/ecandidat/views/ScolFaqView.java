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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.FaqController;
import fr.univlorraine.ecandidat.entities.ecandidat.Faq;
import fr.univlorraine.ecandidat.entities.ecandidat.Faq_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion de la faq par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolFaqView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolFaqView extends VerticalLayout implements View, EntityPushListener<Faq>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 8432471097989849796L;

	public static final String NAME = "scolFaqView";

	public static final String[] FIELDS_ORDER = {Faq_.orderFaq.getName(),Faq_.libFaq.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FaqController faqController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient EntityPusher<Faq> faqEntityPusher;

	/* Composants */
	private TableFormating faqTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("faq.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("faq.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			faqController.editNewFaq();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (faqTable.getValue() instanceof Faq) {
				faqController.editFaq((Faq) faqTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (faqTable.getValue() instanceof Faq) {
				faqController.deleteFaq((Faq) faqTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des faqs */
		BeanItemContainer<Faq> container = new BeanItemContainer<Faq>(Faq.class, cacheController.getFaq());
		faqTable.setContainerDataSource(container);		
		faqTable.setSizeFull();
		faqTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			faqTable.setColumnHeader(fieldName, applicationContext.getMessage("faq.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		faqTable.setSortContainerPropertyId(Faq_.orderFaq.getName());
		faqTable.setColumnCollapsingAllowed(true);
		faqTable.setColumnReorderingAllowed(true);
		faqTable.setSelectable(true);
		faqTable.setImmediate(true);
		faqTable.addItemSetChangeListener(e -> faqTable.sanitizeSelection());
		faqTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de faq sont actifs seulement si une faq est sélectionnée. */
			boolean faqIsSelected = faqTable.getValue() instanceof Faq;
			btnEdit.setEnabled(faqIsSelected);
			btnDelete.setEnabled(faqIsSelected);
		});
		faqTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				faqTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(faqTable);
		setExpandRatio(faqTable, 1);
		
		/* Inscrit la vue aux mises à jour de faq */
		faqEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de faq */
		faqEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Faq entity) {
		faqTable.removeItem(entity);
		faqTable.addItem(entity);
		faqTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Faq entity) {
		faqTable.removeItem(entity);
		faqTable.addItem(entity);
		faqTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Faq entity) {
		faqTable.removeItem(entity);
	}
}
