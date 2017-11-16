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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Page de gestion des versions
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminCacheView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminCacheView extends VerticalLayout implements View{

	/** serialVersionUID **/
	private static final long serialVersionUID = -2621803930906431928L;

	public static final String NAME = "adminCacheView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	
	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.CHAMPS_TITLE,SimpleTablePresentation.CHAMPS_VALUE,SimpleTablePresentation.CHAMPS_ACTION};

	/*Composants*/
	private BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private TableFormating cacheTable = new TableFormating(null, container);
	
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
		Label titleNom = new Label(applicationContext.getMessage("adminCacheView.title", null, UI.getCurrent().getLocale()));
		titleNom.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleNom);	
		
		/*Label*/
		
		CustomPanel cp = new CustomPanel(applicationContext.getMessage("cache.panel.caption", null, UI.getCurrent().getLocale()), applicationContext.getMessage("cache.panel.label", null, UI.getCurrent().getLocale()), FontAwesome.WARNING);
		cp.setMargin(true);
		addComponent(cp);	
		
		/*Reload ALL*/
		OneClickButton button = new OneClickButton(applicationContext.getMessage("cache.action.btn.all", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
		button.addClickListener(e->{
			cacheController.askToReloadData(null);
			reloadContainer();
		});
		addComponent(button);	
		
		cacheTable.addGeneratedColumn(SimpleTablePresentation.CHAMPS_ACTION, new ColumnGenerator() {
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final SimpleTablePresentation bean = (SimpleTablePresentation)itemId;
				OneClickButton button = new OneClickButton(applicationContext.getMessage("cache.action.btn", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
				button.addClickListener(e->{
					cacheController.askToReloadData(bean.getCode());
					reloadContainer();
				});
				return button;
			}
		});
		cacheTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			cacheTable.setColumnHeader(fieldName, applicationContext.getMessage("cache." + fieldName, null, UI.getCurrent().getLocale()));
		}
		cacheTable.setSortContainerPropertyId(SimpleTablePresentation.CHAMPS_TITLE);
		cacheTable.setColumnCollapsingAllowed(false);
		cacheTable.setColumnReorderingAllowed(false);
		cacheTable.setSelectable(false);
		cacheTable.setImmediate(true);
		cacheTable.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});		
		cacheTable.setSizeFull();
		addComponent(cacheTable);
		setExpandRatio(cacheTable, 1);
	}
	
	private void reloadContainer(){
		container.removeAllItems();
		container.addAll(cacheController.getListPresentation());
		cacheTable.sort();
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {		
		reloadContainer();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
	}
}
