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
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidatPK_;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidat_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.LockCandidatListener;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Page de gestion des locks candidat
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminLockCandidatView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminLockCandidatView extends VerticalLayout implements View, LockCandidatListener{

	/** serialVersionUID **/
	private static final long serialVersionUID = 6118429225941087757L;

	public static final String NAME = "adminLockCandidatView";

	public static final String[] FIELDS_ORDER = {LockCandidat_.datLock.getName(), LockCandidat_.id.getName()+"."+LockCandidatPK_.numDossierOpiCptMin.getName(), LockCandidat_.id.getName()+"."+LockCandidatPK_.ressourceLock.getName(), LockCandidat_.instanceIdLock.getName(), LockCandidat_.uiIdLock.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockCandidatController lockCandidatController;

	/* Composants */
	private OneClickButton btnDelete = new OneClickButton(FontAwesome.TRASH_O);
	private OneClickButton btnDeleteAll = new OneClickButton(FontAwesome.TRASH);
	private BeanItemContainer<LockCandidat> container = new BeanItemContainer<LockCandidat>(LockCandidat.class);
	private TableFormating table = new TableFormating(null, container);

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
		Label titleParam = new Label(applicationContext.getMessage("lock.candidat.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		Label subTitleParam = new Label(applicationContext.getMessage("lock.candidat.subtitle", null, UI.getCurrent().getLocale()), ContentMode.HTML);
		subTitleParam.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponent(subTitleParam);		
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		LockCandidatListener listener = this;
		btnDelete.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (table.getValue() instanceof LockCandidat) {
				lockCandidatController.deleteLock((LockCandidat) table.getValue(), listener);
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_LEFT);
		
		btnDeleteAll.setCaption(applicationContext.getMessage("lock.candidat.all.btn", null, UI.getCurrent().getLocale()));
		btnDeleteAll.setEnabled(false);
		btnDeleteAll.addClickListener(e -> {
			lockCandidatController.deleteAllLock(container.getItemIds(), listener);
		});
		buttonsLayout.addComponent(btnDeleteAll);
		buttonsLayout.setComponentAlignment(btnDeleteAll, Alignment.MIDDLE_RIGHT);

		/* Table des locks */
		container.addNestedContainerProperty(LockCandidat_.id.getName()+"."+LockCandidatPK_.numDossierOpiCptMin.getName());
		container.addNestedContainerProperty(LockCandidat_.id.getName()+"."+LockCandidatPK_.ressourceLock.getName());
		table.setSizeFull();
		table.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			table.setColumnHeader(fieldName, applicationContext.getMessage("lock.candidat.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		table.setSortContainerPropertyId(LockCandidat_.datLock.getName());
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		table.setSelectable(true);
		table.setImmediate(true);
		table.addItemSetChangeListener(e -> table.sanitizeSelection());
		table.addValueChangeListener(e -> {
			if (table.getValue()==null){
				btnDelete.setEnabled(false);
			}else{
				btnDelete.setEnabled(true);
			}
			
		});
		table.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				table.select(e.getItemId());
				btnDelete.click();
			}
		});
		addComponent(table);
		setExpandRatio(table, 1);
	}
	
	/**
	 * Rafraishci la liste des locks
	 */
	private void refreshListLock(){
		container.removeAllItems();
		container.addAll(lockCandidatController.getListLockMore24Heure());
		refreshStateBtnAll();
	}
	
	/**
	 * Rafraichi le bouton deleteAll
	 */
	private void refreshStateBtnAll(){
		if (container.getItemIds().size()>0){
			btnDeleteAll.setEnabled(true);
		}else{
			btnDeleteAll.setEnabled(false);
		}
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		refreshListLock();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
	}


	@Override
	public void lockCandidatDeleted(LockCandidat lock) {
		table.removeItem(lock);
		refreshStateBtnAll();
	}

	@Override
	public void lockCandidatAllDeleted() {
		refreshListLock();
	}
}
