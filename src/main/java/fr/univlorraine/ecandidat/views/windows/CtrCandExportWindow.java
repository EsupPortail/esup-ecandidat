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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.PreferenceController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureOption;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.windows.CtrCandPreferenceExportWindow.PreferenceExportListener;

/**
 * Fenêtre de choix d'option d'export
 *
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings({"unchecked", "serial"})
public class CtrCandExportWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	@Resource
	private transient PreferenceController preferenceController;
	@Resource
	private transient ParametreController parametreController;

	private Commission commission;
	private List<Candidature> listeCand;
	private CentreCandidature centreCandidature;

	public CtrCandExportWindow(final Commission commission, final List<Candidature> listeCand) {
		this();
		this.commission = commission;
		this.listeCand = listeCand;
	}

	/** Crée une fenêtre de choix d'option d'export */

	public CtrCandExportWindow() {
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
		setCaption(applicationContext.getMessage("export.window", null, UI.getCurrent().getLocale()));

		/* Options */
		LinkedHashSet<ExportListCandidatureOption> setOptionLeft = new LinkedHashSet<>();
		/* Infos du candidat */
		setOptionLeft.add(new ExportListCandidatureOption("numDossierHide", applicationContext.getMessage("export.option.numDossier", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("civiliteHide", applicationContext.getMessage("export.option.civilite", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("nomHide", applicationContext.getMessage("export.option.nom", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("prenomHide", applicationContext.getMessage("export.option.prenom", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("dtNaissHide", applicationContext.getMessage("export.option.dtnaiss", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("nationaliteHide", applicationContext.getMessage("export.option.nationalite", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("langueHide", applicationContext.getMessage("export.option.langue", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("etuIdHide", applicationContext.getMessage("export.option.etuId", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("ineHide", applicationContext.getMessage("export.option.ine", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("cleIneHide", applicationContext.getMessage("export.option.cleIne", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("temFcHide", applicationContext.getMessage("export.option.temFc", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("telHide", applicationContext.getMessage("export.option.tel", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("telPortHide", applicationContext.getMessage("export.option.telPort", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("mailHide", applicationContext.getMessage("export.option.mail", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("adresseHide", applicationContext.getMessage("export.option.adresse", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("adresseDiviseHide", applicationContext.getMessage("export.option.adresse.div", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("etablissementHide", applicationContext.getMessage("export.option.etablissement", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("lastDipHide", applicationContext.getMessage("export.option.lastDip", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("lastLibDipHide", applicationContext.getMessage("export.option.lastLibDip", null, UI.getCurrent().getLocale())));
		/* Infos de la candidature */
		setOptionLeft.add(new ExportListCandidatureOption("tagHide", applicationContext.getMessage("export.option.tag", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("codFormHide", applicationContext.getMessage("export.option.codForm", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("libFormHide", applicationContext.getMessage("export.option.libForm", null, UI.getCurrent().getLocale())));
		setOptionLeft.add(new ExportListCandidatureOption("dateCandHide", applicationContext.getMessage("export.option.dateCand", null, UI.getCurrent().getLocale())));

		LinkedHashSet<ExportListCandidatureOption> setOptionRight = new LinkedHashSet<>();
		setOptionLeft.add(new ExportListCandidatureOption("dateTransHide", applicationContext.getMessage("export.option.dateTrans", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("statutHide", applicationContext.getMessage("export.option.statut", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateModStatutHide", applicationContext.getMessage("export.option.dateModStatut", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateReceptHide", applicationContext.getMessage("export.option.dateRecept", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateCompletHide", applicationContext.getMessage("export.option.dateComplet", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateIncompletHide", applicationContext.getMessage("export.option.dateIncomplet", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("typeTraitHide", applicationContext.getMessage("export.option.typeTrait", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("typeTraitValidHide", applicationContext.getMessage("export.option.typeTraitValid", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateModPjHide", applicationContext.getMessage("export.option.dateModPj", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("commissionHide", applicationContext.getMessage("export.option.commission", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("avisCandHide", applicationContext.getMessage("export.option.avisCand", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("avisValidHide", applicationContext.getMessage("export.option.avisValid", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("dateValidHide", applicationContext.getMessage("export.option.dateValid", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("motifHide", applicationContext.getMessage("export.option.motif", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("rangHide", applicationContext.getMessage("export.option.rang", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("rangReelHide", applicationContext.getMessage("export.option.rangReel", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("preselectionHide", applicationContext.getMessage("export.option.preselection", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("preselectionDiviseHide", applicationContext.getMessage("export.option.preselection.div", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("commentaireHide", applicationContext.getMessage("export.option.commentaire", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("confirmHide", applicationContext.getMessage("export.option.confirm", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("datNewConfirmHide", applicationContext.getMessage("export.option.datNewConfirm", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("datNewRetourHide", applicationContext.getMessage("export.option.datNewRetour", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("datAnnulHide", applicationContext.getMessage("export.option.datAnnul", null, UI.getCurrent().getLocale())));
		setOptionRight.add(new ExportListCandidatureOption("userAnnulHide", applicationContext.getMessage("export.option.userAnnul", null, UI.getCurrent().getLocale())));
		if (parametreController.getIsExportBlocNote()) {
			setOptionRight.add(new ExportListCandidatureOption("postItHide", applicationContext.getMessage("export.option.postit", null, UI.getCurrent().getLocale())));
		}

		LinkedHashSet<ExportListCandidatureOption> allOptions = new LinkedHashSet<>();
		allOptions.addAll(setOptionLeft);
		allOptions.addAll(setOptionRight);

		BeanItemContainer<ExportListCandidatureOption> containerLeft = new BeanItemContainer<>(ExportListCandidatureOption.class, setOptionLeft);
		BeanItemContainer<ExportListCandidatureOption> containerRight = new BeanItemContainer<>(ExportListCandidatureOption.class, setOptionRight);

		Label label = new Label(applicationContext.getMessage("export.caption", null, UI.getCurrent().getLocale()));
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		HorizontalLayout hlCoche = new HorizontalLayout();
		hlCoche.setWidth(100, Unit.PERCENTAGE);
		hlCoche.setSpacing(true);
		layout.addComponent(hlCoche);

		/* Preferences utilisateurs */
		String[] colonneChecked = preferenceController.getPrefExportColonnes();

		OptionGroup multiOptionGroupLeft = new OptionGroup(null, containerLeft);
		multiOptionGroupLeft.setMultiSelect(true);
		multiOptionGroupLeft.setImmediate(true);
		multiOptionGroupLeft.setItemCaptionPropertyId("caption");
		multiOptionGroupLeft.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		hlCoche.addComponent(multiOptionGroupLeft);
		if (colonneChecked == null) {
			multiOptionGroupLeft.setValue(setOptionLeft);
		} else {
			multiOptionGroupLeft.setValue(getOptionChecked(colonneChecked, setOptionLeft));
		}

		OptionGroup multiOptionGroupRight = new OptionGroup(null, containerRight);
		multiOptionGroupRight.setMultiSelect(true);
		multiOptionGroupRight.setImmediate(true);
		multiOptionGroupRight.setItemCaptionPropertyId("caption");
		multiOptionGroupRight.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		hlCoche.addComponent(multiOptionGroupRight);
		if (colonneChecked == null) {
			multiOptionGroupRight.setValue(setOptionRight);
		} else {
			multiOptionGroupRight.setValue(getOptionChecked(colonneChecked, setOptionRight));
		}

		/* Ajoute le temoin de footer */
		CheckBox rcbFooter = new CheckBox(applicationContext.getMessage("export.footer.check", null, UI.getCurrent().getLocale()));
		Boolean temFooter = preferenceController.getPrefExportFooter();
		rcbFooter.setValue(temFooter);
		layout.addComponent(rcbFooter);
		layout.setComponentAlignment(rcbFooter, Alignment.MIDDLE_CENTER);

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

		/* Tout selectionner */
		OneClickButton btnAllCheck = new OneClickButton(applicationContext.getMessage("export.btn.check", null, UI.getCurrent().getLocale()), FontAwesome.CHECK_SQUARE_O);
		btnAllCheck.addClickListener(e -> {
			multiOptionGroupLeft.setValue(setOptionLeft);
			multiOptionGroupRight.setValue(setOptionRight);
		});
		buttonsLayout.addComponent(btnAllCheck);
		buttonsLayout.setComponentAlignment(btnAllCheck, Alignment.MIDDLE_CENTER);

		/* Preferences */
		OneClickButton btnPref = new OneClickButton(FontAwesome.COG);
		btnPref.setDescription(applicationContext.getMessage("preference.view.btn", null, UI.getCurrent().getLocale()));
		btnPref.addClickListener(e -> {
			LinkedHashSet<ExportListCandidatureOption> setCoche = new LinkedHashSet<>();
			setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupLeft.getValue());
			setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupRight.getValue());

			CtrCandPreferenceExportWindow window = new CtrCandPreferenceExportWindow(setCoche, rcbFooter.getValue());
			window.addPreferenceExportListener(new PreferenceExportListener() {

				@Override
				public void saveInSession(final String valeurColonneCoche, final Boolean tempFooter) {
					preferenceController.savePrefExportInSession(valeurColonneCoche, tempFooter, true);
				}

				@Override
				public void saveInDb(final String valeurColonneCoche, final Boolean tempFooter) {
					preferenceController.savePrefExportInDb(valeurColonneCoche, tempFooter);
				}

				@Override
				public void initPref() {
					preferenceController.initPrefExport();
					multiOptionGroupLeft.setValue(setOptionLeft);
					multiOptionGroupRight.setValue(setOptionRight);
					close();
				}

			});
			UI.getCurrent().addWindow(window);
		});
		buttonsLayout.addComponent(btnPref);
		buttonsLayout.setComponentAlignment(btnPref, Alignment.MIDDLE_CENTER);

		/* Tout deselectionner */
		OneClickButton btnAllDecheck = new OneClickButton(applicationContext.getMessage("export.btn.uncheck", null, UI.getCurrent().getLocale()), FontAwesome.SQUARE_O);
		btnAllDecheck.addClickListener(e -> {
			multiOptionGroupLeft.setValue(null);
			multiOptionGroupRight.setValue(null);
		});
		buttonsLayout.addComponent(btnAllDecheck);
		buttonsLayout.setComponentAlignment(btnAllDecheck, Alignment.MIDDLE_CENTER);

		/* Exporter */
		Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()), FontAwesome.FILE_EXCEL_O);
		btnExport.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnExport.setDisableOnClick(true);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				LinkedHashSet<ExportListCandidatureOption> setCoche = new LinkedHashSet<>();
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupLeft.getValue());
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupRight.getValue());

				if (setCoche.size() == 0) {
					btnExport.setEnabled(true);
					return null;
				}

				/* Téléchargement depuis la commission */
				if (commission != null) {
					OnDemandFile file = ctrCandCandidatureController.generateExport(commission, listeCand, allOptions, setCoche, rcbFooter.getValue());
					if (file != null) {
						btnExport.setEnabled(true);
						return file;
					}
				} else if (centreCandidature != null) {
					OnDemandFile file = ctrCandCandidatureController.generateExport(centreCandidature, allOptions, setCoche, rcbFooter.getValue());
					if (file != null) {
						btnExport.setEnabled(true);
						return file;
					}
				}
				btnExport.setEnabled(true);
				return null;
			}
		}, btnExport);

		buttonsLayout.addComponent(btnExport);
		buttonsLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	private LinkedHashSet<ExportListCandidatureOption> getOptionChecked(final String[] colonneChecked, final LinkedHashSet<ExportListCandidatureOption> setOption) {
		LinkedHashSet<ExportListCandidatureOption> listeToRet = new LinkedHashSet<>();
		setOption.stream().forEach(e -> {
			if (Arrays.stream(colonneChecked).filter(f -> f.equals(e.getId())).findAny().isPresent()) {
				listeToRet.add(e);
			}
		});
		return listeToRet;
	}
}
