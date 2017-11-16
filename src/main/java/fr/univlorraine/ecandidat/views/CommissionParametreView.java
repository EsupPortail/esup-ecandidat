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

import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.services.security.SecurityCommissionFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des parametres de la commission
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CommissionParametreView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_COMMISSION)
public class CommissionParametreView extends VerticalLayout implements View, EntityPushListener<Commission>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 6636733484208381133L;

	public static final String NAME = "commissionParametreView";

	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.CHAMPS_TITLE,SimpleTablePresentation.CHAMPS_VALUE};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient EntityPusher<Commission> commissionEntityPusher;
	
	/*Le droit sur la vue*/
	private SecurityCommissionFonc securityCommissionFonc;
	
	/* Composants */
	private BeanItemContainer<SimpleTablePresentation> containerReadOnly = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private BeanItemContainer<SimpleTablePresentation> containerGeneral = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private BeanItemContainer<SimpleTablePresentation> containerLettre = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private Label labelSignataire = new Label("", ContentMode.HTML);
	private Image imgSignataire = new Image();
	private OneClickButton btnAddImage  = new OneClickButton(FontAwesome.PLUS);
	private OneClickButton btnDeleteImage  = new OneClickButton(FontAwesome.MINUS);
	private OneClickButton btnTestAdm  = new OneClickButton(FontAwesome.ENVELOPE);
	private OneClickButton btnTestRef  = new OneClickButton(FontAwesome.ENVELOPE_O);

	/* Composants */

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		//setSizeFull();
		setWidth(100, Unit.PERCENTAGE);
		setMargin(true);
		setSpacing(true);
		
		/*Récupération de la commission en cours en cours*/
		securityCommissionFonc = userController.getCommissionFonctionnalite(NomenclatureUtils.FONCTIONNALITE_PARAM);			
		if (securityCommissionFonc.hasNoRight()){
			return;
		}
		
		/* Titre + bouton*/
		HorizontalLayout titleButtonLayout = new HorizontalLayout();
		titleButtonLayout.setWidth(100, Unit.PERCENTAGE);
		titleButtonLayout.setSpacing(true);
		addComponent(titleButtonLayout);
		
		/*Titre*/
		Label titleParam = new Label(applicationContext.getMessage("commission.parametre.title", new Object[]{securityCommissionFonc.getCommission().getLibComm()}, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		titleButtonLayout.addComponent(titleParam);
		
		/*Bouton*/
		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.addClickListener(e -> {
			commissionController.editCommission(securityCommissionFonc.getCommission(),false);
		});
		titleButtonLayout.addComponent(btnEdit);
		titleButtonLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_RIGHT);
		
		/*Descriptif*/		
		Label titleParamDesc = new Label(applicationContext.getMessage("commission.parametre.title.desc", null, UI.getCurrent().getLocale()));
		titleParamDesc.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponent(titleParamDesc);
		addComponent(getTable(containerReadOnly, 3));
		
		/*Parametres généraux*/		
		Label titleParamParam = new Label(applicationContext.getMessage("commission.parametre.title.param", null, UI.getCurrent().getLocale()));
		titleParamParam.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponent(titleParamParam);		
		addComponent(getTable(containerGeneral, 8));
		
		/*Lettres*/
		Label titleParamLettre = new Label(applicationContext.getMessage("commission.parametre.title.lettre", null, UI.getCurrent().getLocale()));
		titleParamLettre.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponent(titleParamLettre);
		addComponent(getTable(containerLettre, 2));
		
		/*Signataire*/
		HorizontalLayout hlSignataire = new HorizontalLayout();
		hlSignataire.setWidth(100, Unit.PERCENTAGE);
		hlSignataire.setSpacing(true);
		addComponent(hlSignataire);
		
		/*Label signataire*/
		VerticalLayout vlLabelSignataire = new VerticalLayout();
		vlLabelSignataire.setSpacing(true);
		Label labelTitleLabelSign = new Label(applicationContext.getMessage("commission.parametre.sign.label", null, UI.getCurrent().getLocale()));
		labelTitleLabelSign.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		labelTitleLabelSign.setSizeUndefined();
		vlLabelSignataire.addComponent(labelTitleLabelSign);
		vlLabelSignataire.setComponentAlignment(labelTitleLabelSign, Alignment.MIDDLE_CENTER);
		
		labelSignataire.setSizeUndefined();
		labelSignataire.setContentMode(ContentMode.HTML);
		labelSignataire.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
		vlLabelSignataire.addComponent(labelSignataire);
		vlLabelSignataire.setComponentAlignment(labelSignataire, Alignment.MIDDLE_CENTER);
		
		hlSignataire.addComponent(vlLabelSignataire);
		
		/*Image signature*/
		VerticalLayout vlImgSignataire = new VerticalLayout();
		vlImgSignataire.setSpacing(true);
		Label labelTitleImgSign = new Label(applicationContext.getMessage("commission.parametre.sign.img", null, UI.getCurrent().getLocale()));
		labelTitleImgSign.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		labelTitleImgSign.setSizeUndefined();
		vlImgSignataire.addComponent(labelTitleImgSign);
		vlImgSignataire.setComponentAlignment(labelTitleImgSign, Alignment.MIDDLE_CENTER);
		
		if (securityCommissionFonc.isWrite()){
			btnAddImage.setCaption(applicationContext.getMessage("file.btnAdd", null, UI.getCurrent().getLocale()));
			btnAddImage.addClickListener(e->commissionController.addFileToSignataire(securityCommissionFonc.getCommission()));
			btnAddImage.setVisible(false);
			vlImgSignataire.addComponent(btnAddImage);
			vlImgSignataire.setComponentAlignment(btnAddImage, Alignment.MIDDLE_CENTER);			
			
			btnDeleteImage.setCaption(applicationContext.getMessage("file.btnDel", null, UI.getCurrent().getLocale()));
			btnDeleteImage.addClickListener(e->commissionController.deleteFileToSignataire(securityCommissionFonc.getCommission()));
			btnDeleteImage.setVisible(false);
			vlImgSignataire.addComponent(btnDeleteImage);
			vlImgSignataire.setComponentAlignment(btnDeleteImage, Alignment.MIDDLE_CENTER);			
		}		
		
		vlImgSignataire.addComponent(imgSignataire);
		vlImgSignataire.setComponentAlignment(imgSignataire, Alignment.MIDDLE_CENTER);
		
		hlSignataire.addComponent(vlImgSignataire);
		
		/*Boutons de test de lettre*/
		VerticalLayout vlButtonLettre = new VerticalLayout();
		vlButtonLettre.setSpacing(true);
		hlSignataire.addComponent(vlButtonLettre);
		
		Label labelLettreTest = new Label(applicationContext.getMessage("commission.parametre.sign.test", null, UI.getCurrent().getLocale()));
		labelLettreTest.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		labelLettreTest.setSizeUndefined();
		vlButtonLettre.addComponent(labelLettreTest);
		vlButtonLettre.setComponentAlignment(labelLettreTest, Alignment.MIDDLE_CENTER);
		
		btnTestAdm.setCaption(applicationContext.getMessage("commission.parametre.test.lettre.adm", null, UI.getCurrent().getLocale()));
		new OnDemandFileDownloader(new OnDemandStreamFile() {

			@Override
			public OnDemandFile getOnDemandFile() {
				return commissionController.testLettreAdm(securityCommissionFonc.getCommission(), ConstanteUtils.TEMPLATE_LETTRE_ADM, applicationContext.getMessage("candidature.lettre.file.adm", new Object[]{"AXQDF1P8_Martinpat_Jean", "CODFORM"}, UI.getCurrent().getLocale()));
			}
		},btnTestAdm);
		btnTestRef.setCaption(applicationContext.getMessage("commission.parametre.test.lettre.ref", null, UI.getCurrent().getLocale()));
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				return commissionController.testLettreAdm(securityCommissionFonc.getCommission(), ConstanteUtils.TEMPLATE_LETTRE_REFUS, applicationContext.getMessage("candidature.lettre.file.ref", new Object[]{"AXQDF1P8_Martinpat_Jean", "CODFORM"}, UI.getCurrent().getLocale()));
			}
		},btnTestRef);		
		vlButtonLettre.addComponent(btnTestAdm);
		vlButtonLettre.addComponent(btnTestRef);
		
		miseAJourContainer(securityCommissionFonc.getCommission());
		//setExpandRatio(paramTable, 1);
		
		/*Gestion du readOnly*/
		if (securityCommissionFonc.isWrite()){
			btnEdit.setVisible(true);
		}else{
			btnEdit.setVisible(false);
		}
		
		/* Inscrit la vue aux mises à jour de centreCandidature */
		commissionEntityPusher.registerEntityPushListener(this);
	}
	
	/**
	 * @param container
	 * @param size
	 * @return une table pour formatter les données
	 */
	private TableFormating getTable(BeanItemContainer<SimpleTablePresentation> container, Integer size){
		TableFormating table = new TableFormating(null, container);
		table.addBooleanColumn(SimpleTablePresentation.CHAMPS_VALUE,false);
		table.setVisibleColumns((Object[]) FIELDS_ORDER);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);
		table.setPageLength(size);
		table.setWidth(100, Unit.PERCENTAGE);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 300);
		table.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		return table;
	}
	
	/** Met a jour le container
	 * @param ctrCand
	 */
	private void miseAJourContainer(Commission commission){
		containerReadOnly.removeAllItems();		
		containerGeneral.removeAllItems();
		containerLettre.removeAllItems();
		labelSignataire.setValue("");
		if (commission != null){
			containerReadOnly.addAll(commissionController.getListPresentation(commission,ConstanteUtils.COMM_TYP_AFF_READONLY));
			containerGeneral.addAll(commissionController.getListPresentation(commission,ConstanteUtils.COMM_TYP_AFF_GEN));
			containerLettre.addAll(commissionController.getListPresentation(commission,ConstanteUtils.COMM_TYP_AFF_LETTRE));
			labelSignataire.setValue(commission.getSignataireComm());
			Fichier file = commission.getFichier();
			if (file!=null){
				StreamResource imageResource =  new StreamResource(new StreamSource() {
					/**serialVersionUID**/
					private static final long serialVersionUID = -4583630655596056637L;

					@Override
					public InputStream getStream() {
						InputStream is = fileController.getInputStreamFromFichier(file);
						if (is != null){
							return is;
						}
						return null;
					}
				}, file.getNomFichier());
				imgSignataire.setSource(imageResource);
				btnAddImage.setVisible(false);
				btnDeleteImage.setVisible(true);
			}else{
				imgSignataire.setSource(null);
				btnAddImage.setVisible(true);
				btnDeleteImage.setVisible(false);
			}			
		}		
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
		/* Désinscrit la vue des mises à jour de centreCandidature */
		commissionEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Commission entity) {
		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Commission entity) {
		if (securityCommissionFonc.getCommission()!=null && securityCommissionFonc.getCommission().getIdComm().equals(entity.getIdComm())){
			securityCommissionFonc.setCommission(entity);
			miseAJourContainer(entity);
		}		
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Commission entity) {
		miseAJourContainer(null);
	}
}
