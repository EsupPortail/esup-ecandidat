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
package fr.univlorraine.ecandidat.views.windows;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif_;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * Fenêtre de choix d'option d'export
 *
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings({"serial"})
public class CtrCandDownloadPJWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient ParametreController parametreController;

	/** Crée une fenêtre de choix d'option d'export */

	public CtrCandDownloadPJWindow(final Commission commission, final List<Candidature> listeCand) {
		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		// layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.download.pj.window", null, UI.getCurrent().getLocale()));

		/* Label */
		layout.addComponent(new Label(applicationContext.getMessage("candidature.download.pj.window.label", new Object[] {listeCand.size()}, UI.getCurrent().getLocale())));

		/* Liste des PJ à afficher */
		List<PieceJustif> liste = new ArrayList<>();

		// On ajoute les PJ communes de la scole centrale-->déjà trié
		liste.addAll(pieceJustifController.getPieceJustifsByCtrCandEnService(null, true));

		// On ajoute les PJ communes du centre de candidature-->déjà trié
		liste.addAll(pieceJustifController.getPieceJustifsByCtrCandEnService(commission.getCentreCandidature().getIdCtrCand(), true));

		/* Combobox de choix de la PJ à exporter */
		RequiredComboBox<PieceJustif> cbPj = new RequiredComboBox<>(liste, PieceJustif.class, false);
		cbPj.setItemCaptionPropertyId(PieceJustif_.libPj.getName());
		cbPj.setSizeUndefined();
		layout.addComponent(cbPj);
		layout.setComponentAlignment(cbPj, Alignment.MIDDLE_CENTER);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		/* Annuler */
		OneClickButton btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		/* Exporter */
		Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()), FontAwesome.FILE_EXCEL_O);
		btnExport.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnExport.setEnabled(false);
		btnExport.setDisableOnClick(true);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				/* Téléchargement */
				OnDemandFile file = candidaturePieceController.downlaodMultiplePjZip(listeCand, (PieceJustif) cbPj.getValue());
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

		/* Action sur la listebox */
		cbPj.addValueChangeListener(e -> {
			if (cbPj.getValue() == null) {
				btnExport.setEnabled(false);
			} else {
				btnExport.setEnabled(true);
			}
		});

		/* Centre la fenêtre */
		center();
	}
}
