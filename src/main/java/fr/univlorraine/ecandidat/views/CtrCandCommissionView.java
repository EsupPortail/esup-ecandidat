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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif_;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileLayout;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.ImageViewerWindow;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des commissions du centre de candidature
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CtrCandCommissionView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandCommissionView extends VerticalLayout implements View, EntityPushListener<Commission> {

	public static final String NAME = "ctrCandCommissionView";

	public String[] FIELDS_ORDER = {};
	public static final String[] FIELDS_ORDER_FILE = { Commission_.codComm.getName(),
		Commission_.libComm.getName(),
		Commission_.tesComm.getName(),
		Commission_.temEditLettreComm.getName(),
		Commission_.signataireComm.getName(),
		Commission_.fichier.getName() };
	public static final String[] FIELDS_ORDER_NO_FILE =
		{ Commission_.codComm.getName(), Commission_.libComm.getName(), Commission_.tesComm.getName(), Commission_.temEditLettreComm.getName(), Commission_.signataireComm.getName() };

	public static final String[] FIELDS_ORDER_MEMBRE = {
		CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName(),
		CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.libelleInd.getName(),
		CommissionMembre_.temIsPresident.getName(),
		CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.droitProfil.getName() + "." + DroitProfil_.libProfil.getName(),
		CommissionMembre_.commentaire.getName() };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient EntityPusher<Commission> commissionEntityPusher;

	/* Le droit sur la vue */
	private SecurityCtrCandFonc securityCtrCandFonc;

	/* Composants */
	private final TableFormating commissionTable = new TableFormating();
	private final TableFormating commissionMembreTable = new TableFormating();
	private final OneClickButton btnNewMembre = new OneClickButton(FontAwesome.PLUS);
	private final Label labelMembre = new Label();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Récupération du centre de canidature en cours */
		securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_COMMISSION);
		if (securityCtrCandFonc.hasNoRight()) {
			return;
		}

		/* Table des centres de candidatures */
		final VerticalLayout commissionLayout = new VerticalLayout();
		commissionLayout.setSizeFull();
		commissionLayout.setSpacing(true);

		/* Titre */
		final Label titleParam = new Label(applicationContext.getMessage("commission.title", new Object[] { securityCtrCandFonc.getCtrCand().getLibCtrCand() }, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		commissionLayout.addComponent(titleParam);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		commissionLayout.addComponent(buttonsLayout);

		final OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("commission.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			commissionController.editNewCommission(securityCtrCandFonc.getCtrCand());
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);
		if (!securityCtrCandFonc.getIsGestAllCommission()) {
			btnNew.setEnabled(false);
			btnNew.setDescription(applicationContext.getMessage("commission.create.disable", null, UI.getCurrent().getLocale()));
		}

		final OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (commissionTable.getValue() instanceof Commission) {
				commissionController.editCommission((Commission) commissionTable.getValue(), true);
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		final OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (commissionTable.getValue() instanceof Commission) {
				commissionController.deleteCommission((Commission) commissionTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		final Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()),
			FontAwesome.FILE_EXCEL_O);
		/* Export de la liste des formations */
		btnExport.setDescription(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()));
		btnExport.setDisableOnClick(true);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				@SuppressWarnings("unchecked")
				final List<Commission> listeCommission = (List<Commission>) commissionTable.getContainerDataSource().getItemIds();

				if (listeCommission.size() == 0) {
					btnExport.setEnabled(true);
					return null;
				}

				/* Téléchargement */
				final OnDemandFile file = commissionController.generateExport(listeCommission, securityCtrCandFonc);
				if (file != null) {
					btnExport.setEnabled(true);
					return file;
				}
				btnExport.setEnabled(true);
				return null;
			}
		}, btnExport);
		buttonsLayout.addComponent(btnExport);
		buttonsLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);

		/* Table des commissions */
		final BeanItemContainer<Commission> container = new BeanItemContainer<>(Commission.class,
			commissionController.getCommissionsByCtrCand(securityCtrCandFonc.getCtrCand(), securityCtrCandFonc.getIsGestAllCommission(), securityCtrCandFonc.getListeIdCommission()));
		commissionTable.setContainerDataSource(container);
		commissionTable.addBooleanColumn(Commission_.tesComm.getName());
		commissionTable.addBooleanColumn(Commission_.temEditLettreComm.getName());
		commissionTable.setSortContainerPropertyId(Commission_.codComm.getName());
		commissionTable.setColumnCollapsingAllowed(true);
		commissionTable.setColumnReorderingAllowed(true);
		commissionTable.setSelectable(true);
		commissionTable.setImmediate(true);
		commissionTable.addItemSetChangeListener(e -> commissionTable.sanitizeSelection());
		commissionTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de commission sont actifs seulement si une commission est sélectionnée. */
			final boolean commissionIsSelected = commissionTable.getValue() instanceof Commission;
			btnEdit.setEnabled(commissionIsSelected);
			btnDelete.setEnabled(commissionIsSelected);
			btnNewMembre.setEnabled(commissionIsSelected);
			if (commissionIsSelected) {
				majMembreTable((Commission) commissionTable.getValue());
			} else {
				majMembreTable(null);
			}

		});
		if (!fileController.getModeDematBackoffice().equals(ConstanteUtils.TYPE_FICHIER_STOCK_NONE)) {
			commissionTable.addGeneratedColumn(PieceJustif_.fichier.getName(), new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final Commission commission = (Commission) itemId;
					if (commission.getFichier() == null) {
						if (securityCtrCandFonc.isWrite()) {
							final OneClickButton btnAdd = new OneClickButton(FontAwesome.PLUS);
							btnAdd.addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
							btnAdd.setDescription(applicationContext.getMessage("file.btnAdd", null, UI.getCurrent().getLocale()));
							btnAdd.addClickListener(e -> commissionController.addFileToSignataire(commission));
							return btnAdd;
						}
						return null;
					} else {
						final OnDemandFileLayout fileLayout = new OnDemandFileLayout(commission.getFichier().getNomFichier());

						/* Delete */
						if (securityCtrCandFonc.isWrite()) {
							fileLayout.addBtnDelClickListener(e -> commissionController.deleteFileToSignataire(commission));
						}

						/* Show */
						fileLayout.addBtnViewerClickListener(e -> {
							final OnDemandFile file = new OnDemandFile(commission.getFichier().getNomFichier(), fileController.getInputStreamFromFichier(commission.getFichier()));
							final ImageViewerWindow iv = new ImageViewerWindow(file, applicationContext.getMessage("commission.signataire.warning", null, UI.getCurrent().getLocale()));
							UI.getCurrent().addWindow(iv);
						});

						/* Download */
						fileLayout.addBtnDownloadFileDownloader(new OnDemandStreamFile() {
							@Override
							public OnDemandFile getOnDemandFile() {
								return new OnDemandFile(commission.getFichier().getNomFichier(), fileController.getInputStreamFromFichier(commission.getFichier()));
							}
						});

						return fileLayout;
					}
				}
			});
			FIELDS_ORDER = FIELDS_ORDER_FILE;
		} else {
			FIELDS_ORDER = FIELDS_ORDER_NO_FILE;
		}
		commissionTable.setVisibleColumns((Object[]) FIELDS_ORDER);

		for (final String fieldName : FIELDS_ORDER) {
			commissionTable.setColumnHeader(fieldName, applicationContext.getMessage("commission.table." + fieldName, null, UI.getCurrent().getLocale()));
		}

		commissionLayout.addComponent(commissionTable);
		commissionLayout.setExpandRatio(commissionTable, 1);
		commissionTable.setSizeFull();
		addComponent(commissionLayout);
		setExpandRatio(commissionLayout, 3);

		/* Commission Membre */
		final VerticalLayout commissionMembreLayout = new VerticalLayout();
		commissionMembreLayout.setSizeFull();
		commissionMembreLayout.setSpacing(true);

		/* Titre */
		final HorizontalLayout layoutMembreLabel = new HorizontalLayout();
		layoutMembreLabel.setSpacing(true);
		final Label titleMembre = new Label(applicationContext.getMessage("commission.title.membre", null, UI.getCurrent().getLocale()));
		titleMembre.addStyleName(StyleConstants.VIEW_SUBTITLE);
		layoutMembreLabel.addComponent(titleMembre);
		layoutMembreLabel.setComponentAlignment(titleMembre, Alignment.BOTTOM_LEFT);

		labelMembre.setValue(applicationContext.getMessage("commission.membre.nocomm", null, UI.getCurrent().getLocale()));
		labelMembre.addStyleName(ValoTheme.LABEL_SMALL);
		layoutMembreLabel.addComponent(labelMembre);
		layoutMembreLabel.setComponentAlignment(labelMembre, Alignment.BOTTOM_LEFT);

		commissionMembreLayout.addComponent(layoutMembreLabel);

		/* Boutons */
		final HorizontalLayout buttonsLayoutMembre = new HorizontalLayout();
		buttonsLayoutMembre.setWidth(100, Unit.PERCENTAGE);
		buttonsLayoutMembre.setSpacing(true);
		commissionMembreLayout.addComponent(buttonsLayoutMembre);

		btnNewMembre.setCaption(applicationContext.getMessage("droitprofilind.btnNouveauMembre", null, UI.getCurrent().getLocale()));
		btnNewMembre.setEnabled(false);
		btnNewMembre.addClickListener(e -> {
			commissionController.addProfilToMembre((Commission) commissionTable.getValue());
		});
		buttonsLayoutMembre.addComponent(btnNewMembre);
		buttonsLayoutMembre.setComponentAlignment(btnNewMembre, Alignment.MIDDLE_LEFT);

		/* Edit profil */
		final OneClickButton btnEditMembre = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditMembre.setEnabled(false);
		btnEditMembre.addClickListener(e -> {
			if (commissionMembreTable.getValue() instanceof CommissionMembre) {
				commissionController.updateProfilToMembre((CommissionMembre) commissionMembreTable.getValue());
			}
		});
		buttonsLayoutMembre.addComponent(btnEditMembre);
		buttonsLayoutMembre.setComponentAlignment(btnEditMembre, Alignment.MIDDLE_CENTER);

		/* Delete profil */

		final OneClickButton btnDeleteMembre = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDeleteMembre.setEnabled(false);
		btnDeleteMembre.addClickListener(e -> {
			if (commissionMembreTable.getValue() instanceof CommissionMembre) {
				commissionController.deleteProfilToMembre((CommissionMembre) commissionMembreTable.getValue());
			}
		});
		buttonsLayoutMembre.addComponent(btnDeleteMembre);
		buttonsLayoutMembre.setComponentAlignment(btnDeleteMembre, Alignment.MIDDLE_RIGHT);

		/* Table des CommissionMembre */
		final BeanItemContainer<CommissionMembre> containerMembre = new BeanItemContainer<>(CommissionMembre.class);
		containerMembre.addNestedContainerProperty(CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName());
		containerMembre.addNestedContainerProperty(CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.libelleInd.getName());
		containerMembre.addNestedContainerProperty(CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.droitProfil.getName() + "." + DroitProfil_.libProfil.getName());
		commissionMembreTable.setContainerDataSource(containerMembre);
		commissionMembreTable.addBooleanColumn(CommissionMembre_.temIsPresident.getName());
		commissionMembreTable.setVisibleColumns((Object[]) FIELDS_ORDER_MEMBRE);
		for (final String fieldName : FIELDS_ORDER_MEMBRE) {
			commissionMembreTable.setColumnHeader(fieldName, applicationContext.getMessage("droit." + fieldName, null, UI.getCurrent().getLocale()));
		}
		commissionMembreTable.setSortContainerPropertyId(CommissionMembre_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName());
		commissionMembreTable.setColumnCollapsingAllowed(true);
		commissionMembreTable.setColumnReorderingAllowed(true);
		commissionMembreTable.setSelectable(true);
		commissionMembreTable.setImmediate(true);
		commissionMembreTable.addItemSetChangeListener(e -> commissionMembreTable.sanitizeSelection());
		commissionMembreTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de commission sont actifs seulement si une commission est sélectionnée. */
			final boolean membreIsSelected = commissionMembreTable.getValue() instanceof CommissionMembre;
			btnDeleteMembre.setEnabled(membreIsSelected);
			btnEditMembre.setEnabled(membreIsSelected);
		});

		commissionMembreLayout.addComponent(commissionMembreTable);
		commissionMembreLayout.setExpandRatio(commissionMembreTable, 1);
		commissionMembreTable.setSizeFull();
		addComponent(commissionMembreLayout);
		setExpandRatio(commissionMembreLayout, 2);

		/* Gestion du readOnly */
		if (securityCtrCandFonc.isWrite()) {
			commissionTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					commissionTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			commissionMembreTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					commissionMembreTable.select(e.getItemId());
					btnEditMembre.click();
				}
			});
			buttonsLayout.setVisible(true);
			buttonsLayoutMembre.setVisible(true);
		} else {
			buttonsLayout.setVisible(false);
			buttonsLayoutMembre.setVisible(false);
		}

		/* Inscrit la vue aux mises à jour de commission */
		commissionEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * Met à jour la table des CommissionMembre
	 * @param ctr
	 */
	private void majMembreTable(final Commission commission) {
		commissionMembreTable.removeAllItems();
		if (commission != null) {
			labelMembre.setValue(applicationContext.getMessage("commission.membre.comm", new Object[] { commission.getLibComm() }, UI.getCurrent().getLocale()));
			commissionMembreTable.addItems(commission.getCommissionMembres());
		} else {
			labelMembre.setValue(applicationContext.getMessage("commission.membre.nocomm", null, UI.getCurrent().getLocale()));
		}
		commissionMembreTable.sort();
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
		/* Désinscrit la vue des mises à jour de commission */
		commissionEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Commission entity) {
		if (securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			commissionTable.removeItem(entity);
			commissionTable.addItem(entity);
			commissionTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Commission entity) {
		Commission commSelected = null;
		if (commissionTable.getValue() instanceof Commission) {
			commSelected = (Commission) commissionTable.getValue();
		}
		if (securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			commissionTable.removeItem(entity);
			commissionTable.addItem(entity);
			commissionTable.sort();
		}

		if (commSelected != null && entity.getIdComm().equals(commSelected.getIdComm())) {
			commissionTable.select(entity);
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Commission entity) {
		if (securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			commissionTable.removeItem(entity);
		}
	}
}
