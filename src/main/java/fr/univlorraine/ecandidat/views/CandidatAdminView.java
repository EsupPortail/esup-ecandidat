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
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatPieceController;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidatPK_;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidat_;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatAdminListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileLayout;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.ImageViewerWindow;

/**
 * Page d'administration d'un candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatAdminView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT_ADMIN)
public class CandidatAdminView extends VerticalLayout implements View, CandidatAdminListener {

	public static final String NAME = "candidatAdminView";

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };
	public static final String[] FIELDS_ORDER_PJ = { PjCandidat_.id.getName() + "." + PjCandidatPK_.codAnuPjCandidat.getName(),
		PjCandidat_.id.getName() + "." + PjCandidatPK_.codTpjPjCandidat.getName(),
		PjCandidat_.nomFicPjCandidat.getName(),
		PjCandidat_.datExpPjCandidat.getName(),
		"file" };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatPieceController candidatPieceController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FileController fileController;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/* Composants d'affichage des données */
	private final BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating table = new TableFormating(null, container);

	private final BeanItemContainer<PjCandidat> containerPj = new BeanItemContainer<>(PjCandidat.class);
	private final TableFormating tablePj = new TableFormating(null, containerPj);

	/* Composants d'erreur */
	private final VerticalLayout globalLayout = new VerticalLayout();
	private final Label errorLabel = new Label();
	private final Label lockLabel = new Label();

	/* Titre et actions */
	private final HorizontalLayout controlLayout = new HorizontalLayout();
	private final HorizontalLayout buttonsLayout = new HorizontalLayout();
	private final HorizontalLayout buttonsLayoutPj = new HorizontalLayout();
	private final OneClickButton btnDeleteCnil = new OneClickButton(FontAwesome.ERASER);
	private final Label title = new Label();
	private final Label titlePJ = new Label();

	private CompteMinima cptMin;
	private Boolean isSiScolApo = false;
	private Boolean isSiScolApoPJ = false;

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		isSiScolApo = siScolService.hasSyncEtudiant();
		isSiScolApoPJ = siScolService.hasSyncEtudiantPJ();
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		globalLayout.setSizeFull();
		globalLayout.setSpacing(true);
		addComponent(globalLayout);
		addComponent(errorLabel);

		/* Titre */
		title.addStyleName(StyleConstants.VIEW_TITLE);
		globalLayout.addComponent(title);

		/* Lock */
		lockLabel.addStyleName(ValoTheme.LABEL_FAILURE);
		lockLabel.setVisible(false);
		globalLayout.addComponent(lockLabel);

		/* Layout pour le compte */
		final VerticalLayout cptMinLayout = new VerticalLayout();
		cptMinLayout.setSizeFull();
		cptMinLayout.setSpacing(true);

		/* Layout de controle */
		controlLayout.setWidth(100, Unit.PERCENTAGE);
		controlLayout.setSpacing(true);
		cptMinLayout.addComponent(controlLayout);

		/* Boutons candidat */
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		controlLayout.addComponent(buttonsLayout);
		controlLayout.setExpandRatio(buttonsLayout, 1);

		final OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.addClickListener(e -> {
			candidatController.editAdminCptMin(cptMin, this);
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);

		if (isSiScolApo) {
			final Button btnSyncApogee = new Button(applicationContext.getMessage("btnSyncApo", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnSyncApogee.setDisableOnClick(true);
			btnSyncApogee.addClickListener(e -> {
				candidatController.synchronizeCandidat(cptMin, this);
				btnSyncApogee.setEnabled(true);
			});
			buttonsLayout.addComponent(btnSyncApogee);
			buttonsLayout.setComponentAlignment(btnSyncApogee, Alignment.MIDDLE_CENTER);
		}

		final OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.addClickListener(e -> {
			candidatController.deleteCandidat(cptMin, this);
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		btnDeleteCnil.setCaption(applicationContext.getMessage("candidat.delete.cnil", null, UI.getCurrent().getLocale()));
		btnDeleteCnil.addClickListener(e -> {
			candidatController.deleteCandidatCnil(cptMin, this);
		});
		controlLayout.addComponent(btnDeleteCnil);
		controlLayout.setComponentAlignment(btnDeleteCnil, Alignment.MIDDLE_RIGHT);

		/* La table */
		table.addBooleanColumn(SimpleTablePresentation.CHAMPS_VALUE, false);
		table.setVisibleColumns((Object[]) FIELDS_ORDER);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		table.setCellStyleGenerator((components, itemId, columnId) -> {
			if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});

		cptMinLayout.addComponent(table);
		cptMinLayout.setExpandRatio(table, 1);
		table.setSizeFull();
		globalLayout.addComponent(cptMinLayout);
		globalLayout.setExpandRatio(cptMinLayout, 3);

		/* Les pièces */
		if (isSiScolApoPJ) {
			final VerticalLayout pjLayout = new VerticalLayout();
			pjLayout.setSizeFull();
			pjLayout.setSpacing(true);

			/* Titre */
			titlePJ.addStyleName(StyleConstants.VIEW_TITLE);
			globalLayout.addComponent(titlePJ);

			/* Boutons candidat */
			buttonsLayoutPj.setWidth(100, Unit.PERCENTAGE);
			buttonsLayoutPj.setSpacing(true);
			pjLayout.addComponent(buttonsLayoutPj);

			final Button btnSyncPjApogee = new Button(applicationContext.getMessage("candidat.admin.pj.btnSyncPjApo", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnSyncPjApogee.setDisableOnClick(true);
			btnSyncPjApogee.addClickListener(e -> {
				candidatPieceController.adminSynchronizePJCandidat(cptMin, this);
				btnSyncPjApogee.setEnabled(true);
			});
			buttonsLayoutPj.addComponent(btnSyncPjApogee);
			buttonsLayoutPj.setComponentAlignment(btnSyncPjApogee, Alignment.MIDDLE_CENTER);

			containerPj.addNestedContainerProperty(PjCandidat_.id.getName() + "." + PjCandidatPK_.codAnuPjCandidat.getName());
			containerPj.addNestedContainerProperty(PjCandidat_.id.getName() + "." + PjCandidatPK_.codTpjPjCandidat.getName());
			for (final String fieldName : FIELDS_ORDER_PJ) {
				tablePj.setColumnHeader(fieldName, applicationContext.getMessage("candidat.admin.pj.table." + fieldName, null, UI.getCurrent().getLocale()));
			}
			tablePj.addGeneratedColumn("file", new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PjCandidat pjCandidat = (PjCandidat) itemId;
					if (pjCandidat != null && pjCandidat.getNomFicPjCandidat() != null && !pjCandidat.getNomFicPjCandidat().equals("")) {
						final String nomFichier = pjCandidat.getNomFicPjCandidat();
						final OnDemandFileLayout fileLayout = new OnDemandFileLayout(pjCandidat.getNomFicPjCandidat());
						/* Viewer si JPG */
						if (MethodUtils.isImgFileName(nomFichier)) {
							fileLayout.addBtnViewerClickListener(e -> {
								final InputStream is = fileController.getInputStreamFromPjCandidat(pjCandidat);
								if (is != null) {
									final ImageViewerWindow iv = new ImageViewerWindow(new OnDemandFile(nomFichier, is), null);
									UI.getCurrent().addWindow(iv);
								}
							});
							/* Opener si PDF */
						} else if (MethodUtils.isPdfFileName(nomFichier)) {
							fileLayout.addBtnViewerPdfBrowserOpener(new OnDemandStreamFile() {

								@Override
								public OnDemandFile getOnDemandFile() {
									final InputStream is = fileController.getInputStreamFromPjCandidat(pjCandidat);
									return new OnDemandFile(nomFichier, is);
								}
							});
						}

						/* Bouton download */
						fileLayout.addBtnDownloadFileDownloader(new OnDemandStreamFile() {
							@Override
							public OnDemandFile getOnDemandFile() {
								final InputStream is = fileController.getInputStreamFromPjCandidat(pjCandidat);
								if (is != null) {
									return new OnDemandFile(nomFichier, is);
								}
								return null;
							}
						});
						return fileLayout;
					}
					return null;
				}

			});
			tablePj.setVisibleColumns((Object[]) FIELDS_ORDER_PJ);
			tablePj.setColumnCollapsingAllowed(true);
			tablePj.setColumnReorderingAllowed(true);
			tablePj.setSelectable(false);
			tablePj.setImmediate(true);
			globalLayout.addComponent(tablePj);
			globalLayout.setExpandRatio(tablePj, 1);
			pjLayout.addComponent(tablePj);
			pjLayout.setExpandRatio(tablePj, 1);
			tablePj.setSizeFull();

			globalLayout.addComponent(pjLayout);
			globalLayout.setExpandRatio(pjLayout, 2);
		}
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		cptMin = candidatController.getCompteMinima();
		Boolean isArchive = false;
		if (cptMin == null) {
			cptMin = candidatController.getCompteMinimaForAllCampagne();
			isArchive = true;
		}
		final String error = candidatController.getErrorViewForAdmin(cptMin);
		if (error != null) {
			errorLabel.setValue(error);
			errorLabel.setVisible(true);
			globalLayout.setVisible(false);
		} else {
			errorLabel.setVisible(false);
			globalLayout.setVisible(true);

			/* Le candidat */
			title.setValue(applicationContext.getMessage("candidat.admin.title", new Object[] { candidatController.getLibelleTitle(cptMin) }, UI.getCurrent().getLocale()));
			if (isArchive) {
				title.setValue(title.getValue() + " " + applicationContext.getMessage("candidat.archive.complement", null, UI.getCurrent().getLocale()));
			}

			final List<SimpleTablePresentation> liste = candidatController.getInfoForAdmin(cptMin);
			container.removeAllItems();
			container.addAll(liste);

			if (isSiScolApoPJ) {
				/* PJ */
				titlePJ.setValue(applicationContext.getMessage("candidat.admin.pj.title", new Object[] { candidatController.getLibelleTitle(cptMin) }, UI.getCurrent().getLocale()));
				containerPj.removeAllItems();
				if (cptMin.getCandidat() != null) {
					containerPj.addAll(cptMin.getCandidat().getPjCandidats());
				}
			}

			final String lockError = candidatController.getLockErrorFull(cptMin);
			if (lockError != null) {
				lockLabel.setValue(lockError);
				lockLabel.setVisible(true);
				controlLayout.setVisible(false);
				buttonsLayoutPj.setVisible(false);
			} else if (isArchive) {
				buttonsLayoutPj.setVisible(false);
				if (userController.isAdmin()) {
					buttonsLayout.setVisible(false);
				} else {
					controlLayout.setVisible(false);
				}
			} else {
				if (!userController.isAdmin()) {
					btnDeleteCnil.setVisible(false);
				}
			}
		}
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		candidatController.unlockCandidatFull(cptMin);
		super.detach();

	}

	/** @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatAdminListener#cptMinModified(fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima) */
	@Override
	public void cptMinModified(final CompteMinima cptMin) {
		final List<SimpleTablePresentation> liste = candidatController.getInfoForAdmin(cptMin);
		container.removeAllItems();
		container.addAll(liste);
		if (isSiScolApoPJ && cptMin.getCandidat() != null) {
			containerPj.removeAllItems();
			containerPj.addAll(cptMin.getCandidat().getPjCandidats());
		}
	}

}
