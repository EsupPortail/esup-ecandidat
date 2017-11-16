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

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.OdfListener;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfCtrCand;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfDiplome;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfFormation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Page de visu de l'offre de formation
 * @author Kevin Hergalant
 *
 */
@SpringView(name = OffreFormationView.NAME)
public class OffreFormationView extends VerticalLayout implements View,OdfListener{

	/** serialVersionUID **/
	private static final long serialVersionUID = -5102816633107689510L;

	public static final String NAME = "offreFormationView";
	
	public static final String[] FIELDS_ORDER = {ConstanteUtils.ODF_CAPTION,
		//ConstanteUtils.ODF_TYPE,
		ConstanteUtils.ODF_FORM_MOT_CLE,
		ConstanteUtils.ODF_FORM_DATE,
		ConstanteUtils.ODF_FORM_MODE_CAND};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient DateTimeFormatter formatterDate;
		
	private HierarchicalContainer container = new HierarchicalContainer();
	private TreeTable tree = new TreeTable(null,container);
	private HorizontalLayout hlFilter = new HorizontalLayout();
	private OneClickButton btnFilter = new OneClickButton(FontAwesome.SEARCH);
	private Label lockLabel = new Label();
	private Label noFormationLabel = new Label();

	protected CompteMinima cptMin;
	private Boolean isLocked = true;
	
	/**
	 * Initialise la vue
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		/* Style */
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		/* Titre */
		Label title = new Label(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		title.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(title);
		
		lockLabel.setValue(applicationContext.getMessage("lock.message.odf", null, UI.getCurrent().getLocale()));
		lockLabel.addStyleName(ValoTheme.LABEL_FAILURE);
		lockLabel.setVisible(false);
		addComponent(lockLabel);

		addComponent(new Label(applicationContext.getMessage("odf.label", null, UI.getCurrent().getLocale())));		
		
		container.addContainerProperty(ConstanteUtils.ODF_CAPTION, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_TYPE, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_DIP_ID, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_FORM_ID, Integer.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_FORM_TITLE, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_FORM_MOT_CLE, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_FORM_DATE, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_FORM_MODE_CAND, String.class, null);	
		container.addContainerProperty(ConstanteUtils.ODF_FORM_DIPLOME, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_FORM_CTR_CAND, String.class, null);
		container.addContainerProperty(ConstanteUtils.ODF_ICON, com.vaadin.server.Resource.class, null);

		tree.setSizeFull();
		tree.setItemCaptionPropertyId(ConstanteUtils.ODF_CAPTION);
		tree.setItemIconPropertyId(ConstanteUtils.ODF_ICON);
		tree.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			tree.setColumnHeader(fieldName, applicationContext.getMessage("odf.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		tree.setColumnWidth(ConstanteUtils.ODF_FORM_MOT_CLE, 150);
		tree.setColumnWidth(ConstanteUtils.ODF_FORM_DATE, 220);
		tree.setColumnWidth(ConstanteUtils.ODF_FORM_MODE_CAND, 220);
		tree.setImmediate(true);
		tree.addItemClickListener(e->{	
			Item item = e.getItem();
			String type = (String) item.getItemProperty(ConstanteUtils.ODF_TYPE).getValue();
			//permet d'ouvrir un element de l'arbre en cliquant dessus
			if (type!=null && !type.equals(ConstanteUtils.ODF_TYPE_FORM)){
				Object itemId = e.getItemId();
				if (itemId != null){
					Boolean isCollapse = tree.isCollapsed(itemId);
					if (isCollapse!=null){
						tree.setCollapsed(itemId, !isCollapse);
					}					
				}				
			}
			//verifications
			if (cptMin == null){
				return;
			}
			if (isLocked){
				Notification.show(applicationContext.getMessage("lock.message.odf", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
			if (type!=null && type.equals(ConstanteUtils.ODF_TYPE_FORM)){
				Integer idForm = (Integer) item.getItemProperty(ConstanteUtils.ODF_FORM_ID).getValue();
				candidatureController.candidatToFormation(idForm, this, false);
			}
		});
		
		/*tree.addGeneratedColumn(ConstanteUtils.ODF_TYPE, new ColumnGenerator() {
			private static final long serialVersionUID = -7536672274094786260L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Item item = source.getItem(itemId);
				String type = (String) item.getItemProperty(ConstanteUtils.ODF_TYPE).getValue();
				if (type != null){
					return applicationContext.getMessage("odf.type." + type, null, UI.getCurrent().getLocale());
				}
				return null;
			}
		});*/
		
		tree.setCellStyleGenerator(new Table.CellStyleGenerator() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				Item item = tree.getItem(itemId);
				Property<String> propertyType = item.getItemProperty(ConstanteUtils.ODF_TYPE);
				if (propertyType!=null){
					String value = propertyType.getValue();
					if (value!=null && value.equals(ConstanteUtils.ODF_TYPE_CTR)){
						return "level-1-Ctr";
					}else if (value!=null && value.equals(ConstanteUtils.ODF_TYPE_DIP)){
						return "level-2-Dip";
					}else if (value!=null && value.equals(ConstanteUtils.ODF_TYPE_FORM)){
						return "level-3-Form";
					}
				}
				return null;
			}
		});
		
		/*tree.setItemStyleGenerator(new ItemStyleGenerator() {

			private static final long serialVersionUID = 2940526202958994909L;

			@Override
			public String getStyle(Tree source, Object itemId) {
				Item item = tree.getItem(itemId);
				Property<String> propertyType = item.getItemProperty(ConstanteUtils.ODF_TYPE);
				if (propertyType!=null){
					String value = propertyType.getValue();
					if (value!=null && value.equals(ConstanteUtils.ODF_TYPE_CTR)){
						return "level-1-Ctr";
					}else if (value!=null && value.equals(ConstanteUtils.ODF_TYPE_DIP)){
						return "level-2-Dip";
					}else if (value!=null && value.equals(ConstanteUtils.ODF_TYPE_FORM)){
						return "level-3-Form";
					}
				}
				return null;
			}
		});*/
		
		
		
		//tfFilter.setCaption(applicationContext.getMessage("odf.filter", null, UI.getCurrent().getLocale()));
		TextField tfFilter = new TextField();
		tfFilter.setInputPrompt(applicationContext.getMessage("odf.filter", null, UI.getCurrent().getLocale()));
		tfFilter.setImmediate(true);
		/*tfFilter.addTextChangeListener(e->{
			container.removeAllContainerFilters();
			Filter filterTitle = new SimpleStringFilter(ConstanteUtils.ODF_FORM_TITLE, e.getText(), true, false);
			Filter filterMotCle = new SimpleStringFilter(ConstanteUtils.ODF_FORM_MOT_CLE, e.getText(), true, false);
			Filter filterDip = new SimpleStringFilter(ConstanteUtils.ODF_FORM_DIPLOME, e.getText(), true, false);
			Filter filterCtrCand = new SimpleStringFilter(ConstanteUtils.ODF_FORM_CTR_CAND, e.getText(), true, false);
			container.addContainerFilter(new Or(filterDip,filterMotCle,filterTitle,filterCtrCand));
			tree.refreshRowCache();
		});*/
		
		Label labelFilter = new Label(applicationContext.getMessage("odf.filter.nofilter", null, UI.getCurrent().getLocale()));
		btnFilter.setCaption(applicationContext.getMessage("odf.filter.btn", null, UI.getCurrent().getLocale()));
		btnFilter.addClickListener(e->{
			String valFilter = tfFilter.getValue();
			if (valFilter!=null && !valFilter.equals("")){
				labelFilter.setValue(applicationContext.getMessage("odf.filter.label", new Object[]{valFilter}, UI.getCurrent().getLocale()));
			}else{
				labelFilter.setValue(applicationContext.getMessage("odf.filter.nofilter", null, UI.getCurrent().getLocale()));
			}
			container.removeAllContainerFilters();
			Filter filterTitle = new SimpleStringFilter(ConstanteUtils.ODF_FORM_TITLE, valFilter, true, false);
			Filter filterMotCle = new SimpleStringFilter(ConstanteUtils.ODF_FORM_MOT_CLE, valFilter, true, false);
			Filter filterDip = new SimpleStringFilter(ConstanteUtils.ODF_FORM_DIPLOME, valFilter, true, false);
			Filter filterCtrCand = new SimpleStringFilter(ConstanteUtils.ODF_FORM_CTR_CAND, valFilter, true, false);
			container.addContainerFilter(new Or(filterDip,filterMotCle,filterTitle,filterCtrCand));
			tree.setPageLength(0);
			
		});
		tfFilter.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {
			private static final long serialVersionUID = 6231790311427334925L;

			@Override
			public void handleAction(Object sender, Object target) {
				btnFilter.click();
			}
		});
		
		hlFilter.setSpacing(true);
		hlFilter.addComponent(tfFilter);
		hlFilter.addComponent(btnFilter);
		hlFilter.addComponent(labelFilter);
		hlFilter.setComponentAlignment(tfFilter, Alignment.BOTTOM_LEFT);
		hlFilter.setComponentAlignment(btnFilter, Alignment.BOTTOM_LEFT);
		hlFilter.setComponentAlignment(labelFilter, Alignment.MIDDLE_LEFT);
		
		noFormationLabel.setValue(applicationContext.getMessage("odf.no.formation", null, UI.getCurrent().getLocale()));
		
		addComponent(noFormationLabel);
		addComponent(hlFilter);		
		addComponent(tree);
		setExpandRatio(tree, 1);
		updateOdfTree();
	}	
	
	/**
	 * Met à jour l'odf
	 */
	@SuppressWarnings("unchecked")
	private void updateOdfTree(){
		tree.removeAllItems();
		List<OdfCtrCand> liste = cacheController.getOdf();
		Boolean isUtiliseDemat = parametreController.getIsUtiliseDemat();
		
		if(liste.size()>0){
			tree.setCaption(applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale()));			
			tree.addStyleName(StyleConstants.CUSTOM_TREE);
			for (OdfCtrCand ctrCand : liste){
			//liste.forEach(ctrCand -> {
				Item ctrCandItem = container.addItem(ctrCand);				
				if (!checkReloadOdf(ctrCandItem)){
					return;
				}
				//ctrCandItem.getItemProperty(ConstanteUtils.ODF_CAPTION).setValue(applicationContext.getMessage("odf.ctrCand", new Object[]{ctrCand.getTitle()}, UI.getCurrent().getLocale()));
				ctrCandItem.getItemProperty(ConstanteUtils.ODF_CAPTION).setValue(ctrCand.getTitle());
				ctrCandItem.getItemProperty(ConstanteUtils.ODF_TYPE).setValue(ConstanteUtils.ODF_TYPE_CTR);
				ctrCandItem.getItemProperty(ConstanteUtils.ODF_ICON).setValue(FontAwesome.UNIVERSITY);
				
				/*String libModCand = getLibModCand(parametreController.getIsUtiliseDemat(), ctrCand.getModeCandidature());
				ctrCandItem.getItemProperty(ConstanteUtils.ODF_FORM_MODE_CAND).setValue(libModCand);*/
				
				
				/*if (){
					libModCand = applicationContext.getMessage("odf.mode.demat", null, UI.getCurrent().getLocale());
				}else{
					libModCand = applicationContext.getMessage("odf.mode.non.demat", null, UI.getCurrent().getLocale());
				}*/
				
				for (OdfDiplome dip : ctrCand.getListeDiplome()){
				//ctrCand.getListeDiplome().forEach(dip -> {
					
					Item dipItem = container.addItem(dip);
					if (!checkReloadOdf(dipItem)){
						return;
					}
					
					dipItem.getItemProperty(ConstanteUtils.ODF_CAPTION).setValue(dip.getTitle());
					dipItem.getItemProperty(ConstanteUtils.ODF_DIP_ID).setValue(dip.getId());
					//dipItem.getItemProperty(ConstanteUtils.ODF_CAPTION).setValue(applicationContext.getMessage("odf.diplome", new Object[]{dip.getTitle()}, UI.getCurrent().getLocale()));
					dipItem.getItemProperty(ConstanteUtils.ODF_TYPE).setValue(ConstanteUtils.ODF_TYPE_DIP);
					dipItem.getItemProperty(ConstanteUtils.ODF_ICON).setValue(FontAwesome.GRADUATION_CAP);
					container.setParent(dip, ctrCand);
					for (OdfFormation form : dip.getListeFormation()){
					//dip.getListeFormation().forEach(form -> {
						String libModCand = getLibModCand(isUtiliseDemat, form.getModeCandidature());
						Item formItem = container.addItem(form);
						if (!checkReloadOdf(formItem)){
							return;
						}
						formItem.getItemProperty(ConstanteUtils.ODF_CAPTION).setValue(form.getTitle());
						//formItem.getItemProperty(ConstanteUtils.ODF_CAPTION).setValue(applicationContext.getMessage("odf.formation", new Object[]{form.getTitle()}, UI.getCurrent().getLocale()));
						formItem.getItemProperty(ConstanteUtils.ODF_TYPE).setValue(ConstanteUtils.ODF_TYPE_FORM);
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_ID).setValue(form.getIdFormation());						
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_TITLE).setValue(form.getTitle());
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_MOT_CLE).setValue(form.getMotCle());
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_DATE).setValue(applicationContext.getMessage("odf.dates.candidature", new Object[]{formatterDate.format(form.getDateDebut()),formatterDate.format(form.getDateFin())}, UI.getCurrent().getLocale()));
						
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_DIPLOME).setValue(dip.getTitle());
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_CTR_CAND).setValue(ctrCand.getTitle());
						formItem.getItemProperty(ConstanteUtils.ODF_FORM_MODE_CAND).setValue(libModCand);
						
						
						container.setParent(form, dip);
						tree.setChildrenAllowed(form, false);						
					}
					//tree.setCollapsed(dip, false);
				}
				//tree.setCollapsed(ctrCand, false);
			}
			
			hlFilter.setVisible(true);
			noFormationLabel.setVisible(false);
		}else{
			hlFilter.setVisible(false);
			noFormationLabel.setVisible(true);
		}
	}
	
	/** Verifie que l'item a bien été chargé
	 * @param item
	 * @return true si ok
	 */
	private Boolean checkReloadOdf(Item item){		
		if (item == null){
			Notification.show(applicationContext.getMessage("odf.error.load", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			tree.removeAllItems();
			hlFilter.setVisible(false);
			noFormationLabel.setVisible(true);
			//si une erreur est détectée, on demande un rechargement du cache!
			cacheController.reloadOdf(false);
			return false;
		}
		return true;
	}

	/**
	 * @param isUtiliseDemat
	 * @param modeCandidature
	 * @return le libellé du mode de candidature
	 */
	private String getLibModCand(Boolean isUtiliseDemat, Boolean modeCandidature) {
		if (isUtiliseDemat && modeCandidature){
			return applicationContext.getMessage("odf.mode.demat", null, UI.getCurrent().getLocale());
		}else{
			return applicationContext.getMessage("odf.mode.non.demat", null, UI.getCurrent().getLocale());
		}
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {		
		cptMin = candidatController.getCompteMinima();
		if (cptMin==null){
			return;
		}
		String lockError = candidatController.getLockError(cptMin, ConstanteUtils.LOCK_ODF);
		if (lockError!=null){
			isLocked = true;
			lockLabel.setVisible(true);
			return;
		}		
		lockLabel.setVisible(false);
		isLocked = false;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
		if (cptMin==null){			
			return;
		}
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_ODF);
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.OdfListener#updateOdf()
	 */
	@Override
	public void updateOdf() {
		updateOdfTree();
	}

}
