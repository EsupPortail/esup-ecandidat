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
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.IconLabel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des parametres
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminParametreView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminParametreView extends VerticalLayout implements View, EntityPushListener<Parametre>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 596644187861660177L;

	public static final String NAME = "adminParametreView";

	public static final String[] FIELDS_ORDER = {Parametre_.codParam.getName(), Parametre_.libParam.getName(), Parametre_.valParam.getName(), Parametre_.typParam.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient EntityPusher<Parametre> parametreEntityPusher;

	/* Composants */
	private OneClickButton btnEditParam = new OneClickButton(FontAwesome.PENCIL);
	private BeanItemContainer<Parametre> container = new BeanItemContainer<Parametre>(Parametre.class);
	private TableFormating parametreTable = new TableFormating(null, container);
	

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
		Label titleParam = new Label(applicationContext.getMessage("parametre.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		btnEditParam.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditParam.setEnabled(false);
		btnEditParam.addClickListener(e -> {
			if (parametreTable.getValue() instanceof Parametre) {
				parametreController.editParametre((Parametre) parametreTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEditParam);
		buttonsLayout.setComponentAlignment(btnEditParam, Alignment.MIDDLE_LEFT);


		/* Table des parametres */
		parametreTable.setSizeFull();
		parametreTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			parametreTable.setColumnHeader(fieldName, applicationContext.getMessage("parametre.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		parametreTable.addGeneratedColumn(Parametre_.libParam.getName(), new ColumnGenerator() {
			private static final long serialVersionUID = -7215358944101718592L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Parametre parametre = (Parametre) itemId;
				String lib = parametre.getLibParam();
				if (lib.length()>100){
					lib = lib.substring(0, 100)+"....";
				}
				Label label = new Label(lib);
				label.setDescription(parametre.getLibParam());
				return label;
			}
		});
		parametreTable.addGeneratedColumn(Parametre_.valParam.getName(), new ColumnGenerator() {
			
			private static final long serialVersionUID = -7215358944101718592L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Parametre parametre = (Parametre) itemId;
				if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)){
					String val = parametre.getValParam();
					Boolean value = (val!=null && val.equals(ConstanteUtils.TYP_BOOLEAN_YES))?true:(val!=null && val.equals(ConstanteUtils.TYP_BOOLEAN_NO))?false:null;
					return new IconLabel(value,true);
				}else{
					return parametre.getValParam();
				}
				
			}
		});
		parametreTable.setSortContainerPropertyId(Parametre_.codParam.getName());
		parametreTable.setColumnCollapsingAllowed(true);
		parametreTable.setColumnReorderingAllowed(true);
		parametreTable.setSelectable(true);
		parametreTable.setImmediate(true);
		parametreTable.addItemSetChangeListener(e -> parametreTable.sanitizeSelection());
		parametreTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de parametre sont actifs seulement si un parametre est sélectionné. */
			boolean paramIsSelected = parametreTable.getValue() instanceof Parametre;
			btnEditParam.setEnabled(paramIsSelected);
		});
		parametreTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				parametreTable.select(e.getItemId());
				btnEditParam.click();
			}
		});
		addComponent(parametreTable);
		setExpandRatio(parametreTable, 1);
		
		/* Inscrit la vue aux mises à jour de langue */
		parametreEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		container.removeAllItems();
		container.addAll(parametreController.getParametres());
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {		
		/* Désinscrit la vue des mises à jour de langue */
		parametreEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Parametre entity) {
		if (parametreController.isDisplayParam(entity)){
			parametreTable.removeItem(entity);
			parametreTable.addItem(entity);
			parametreTable.sort();
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Parametre entity) {
		if (parametreController.isDisplayParam(entity)){
			parametreTable.removeItem(entity);
			parametreTable.addItem(entity);
			parametreTable.sort();
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Parametre entity) {
		
	}

}
