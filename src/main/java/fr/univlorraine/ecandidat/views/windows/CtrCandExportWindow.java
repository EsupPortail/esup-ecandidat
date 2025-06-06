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
import java.util.concurrent.atomic.AtomicInteger;

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
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings({ "unchecked", "serial" })
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
		setWidth(950, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		// layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("export.window", null, UI.getCurrent().getLocale()));

		/* Options */
		final LinkedHashSet<ExportListCandidatureOption> allOptions = new LinkedHashSet<>();

		/* Infos du candidat */
		allOptions.add(new ExportListCandidatureOption("numDossierHide", applicationContext.getMessage("export.option.numDossier", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("numEtuHide", applicationContext.getMessage("export.option.numEtu", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("civiliteHide", applicationContext.getMessage("export.option.civilite", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("nomPatHide", applicationContext.getMessage("export.option.nomPat", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("nomUsuHide", applicationContext.getMessage("export.option.nomUsu", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("prenomHide", applicationContext.getMessage("export.option.prenom", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dtNaissHide", applicationContext.getMessage("export.option.dtnaiss", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("villeNaissHide", applicationContext.getMessage("export.option.villenaiss", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("nationaliteHide", applicationContext.getMessage("export.option.nationalite", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("langueHide", applicationContext.getMessage("export.option.langue", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("etuIdHide", applicationContext.getMessage("export.option.etuId", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("ineHide", applicationContext.getMessage("export.option.ine", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("cleIneHide", applicationContext.getMessage("export.option.cleIne", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("temFcHide", applicationContext.getMessage("export.option.temFc", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("telHide", applicationContext.getMessage("export.option.tel", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("telPortHide", applicationContext.getMessage("export.option.telPort", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("mailHide", applicationContext.getMessage("export.option.mail", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("bacHide", applicationContext.getMessage("export.option.bac", null, UI.getCurrent().getLocale())));

		/* Adresse */
		allOptions.add(new ExportListCandidatureOption("adresseHide", applicationContext.getMessage("export.option.adresse", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("adresseDiviseHide", applicationContext.getMessage("export.option.adresse.div", null, UI.getCurrent().getLocale())));

		/* Dernier établissement - diplome */
		allOptions.add(new ExportListCandidatureOption("etablissementHide", applicationContext.getMessage("export.option.etablissement", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("lastDipHide", applicationContext.getMessage("export.option.lastDip", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("lastLibDipHide", applicationContext.getMessage("export.option.lastLibDip", null, UI.getCurrent().getLocale())));

		/* Infos de la candidature */
		allOptions.add(new ExportListCandidatureOption("tagHide", applicationContext.getMessage("export.option.tag", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("codFormHide", applicationContext.getMessage("export.option.codForm", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("libFormHide", applicationContext.getMessage("export.option.libForm", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateCandHide", applicationContext.getMessage("export.option.dateCand", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateTransHide", applicationContext.getMessage("export.option.dateTrans", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("statutHide", applicationContext.getMessage("export.option.statut", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateModStatutHide", applicationContext.getMessage("export.option.dateModStatut", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateReceptHide", applicationContext.getMessage("export.option.dateRecept", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateCompletHide", applicationContext.getMessage("export.option.dateComplet", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateIncompletHide", applicationContext.getMessage("export.option.dateIncomplet", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("typeTraitHide", applicationContext.getMessage("export.option.typeTrait", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("typeTraitValidHide", applicationContext.getMessage("export.option.typeTraitValid", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateModPjHide", applicationContext.getMessage("export.option.dateModPj", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("commissionHide", applicationContext.getMessage("export.option.commission", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("avisCandHide", applicationContext.getMessage("export.option.avisCand", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("avisValidHide", applicationContext.getMessage("export.option.avisValid", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("dateValidHide", applicationContext.getMessage("export.option.dateValid", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("motifHide", applicationContext.getMessage("export.option.motif", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("rangHide", applicationContext.getMessage("export.option.rang", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("rangReelHide", applicationContext.getMessage("export.option.rangReel", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("preselectionHide", applicationContext.getMessage("export.option.preselection", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("commentaireHide", applicationContext.getMessage("export.option.commentaire", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("confirmHide", applicationContext.getMessage("export.option.confirm", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("datNewConfirmHide", applicationContext.getMessage("export.option.datNewConfirm", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("datNewRetourHide", applicationContext.getMessage("export.option.datNewRetour", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("regimeHide", applicationContext.getMessage("export.option.regime", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("catExoHide", applicationContext.getMessage("export.option.catExo", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("compExoHide", applicationContext.getMessage("export.option.compExo", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("mntChargeHide", applicationContext.getMessage("export.option.mntCharge", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("datPassageOpiHide", applicationContext.getMessage("export.option.datPassageOpi", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("codOpiHide", applicationContext.getMessage("export.option.codOpi", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("datAnnulHide", applicationContext.getMessage("export.option.datAnnul", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("userAnnulHide", applicationContext.getMessage("export.option.userAnnul", null, UI.getCurrent().getLocale())));
		allOptions.add(new ExportListCandidatureOption("questionReponseHide", applicationContext.getMessage("export.option.questionReponse", null, UI.getCurrent().getLocale())));
		if (parametreController.getIsExportBlocNote()) {
			allOptions.add(new ExportListCandidatureOption("postItHide", applicationContext.getMessage("export.option.postit", null, UI.getCurrent().getLocale())));
		}

		/* Répartition dans les colonnes */
		final LinkedHashSet<ExportListCandidatureOption> setOptionLeft = new LinkedHashSet<>();
		final LinkedHashSet<ExportListCandidatureOption> setOptionMiddle = new LinkedHashSet<>();
		final LinkedHashSet<ExportListCandidatureOption> setOptionRight = new LinkedHashSet<>();

		/* On calcul le nombre d'option par colonne */
		final int nbByColonne = allOptions.size() / 3;

		/* Pour la première colonne, on met le reste */
		final int nbLeftColonne = nbByColonne + (allOptions.size() % 3);
		/* Pour la deuxieme, on prend le nombre par colonne + la première colonne */
		final int nbMiddleColonne = nbByColonne + nbLeftColonne;

		/* On applique dans chaque colonne */
		final AtomicInteger cpt = new AtomicInteger(1);
		allOptions.forEach(e -> {
			if (cpt.get() <= nbLeftColonne) {
				setOptionLeft.add(e);
			} else if (cpt.get() <= nbMiddleColonne) {
				setOptionMiddle.add(e);
			} else {
				setOptionRight.add(e);
			}
			cpt.addAndGet(1);
		});

		final BeanItemContainer<ExportListCandidatureOption> containerLeft = new BeanItemContainer<>(ExportListCandidatureOption.class, setOptionLeft);
		final BeanItemContainer<ExportListCandidatureOption> containerMiddle = new BeanItemContainer<>(ExportListCandidatureOption.class, setOptionMiddle);
		final BeanItemContainer<ExportListCandidatureOption> containerRight = new BeanItemContainer<>(ExportListCandidatureOption.class, setOptionRight);

		final Label label = new Label(applicationContext.getMessage("export.caption", null, UI.getCurrent().getLocale()));
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		layout.addComponent(label);
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		final HorizontalLayout hlCoche = new HorizontalLayout();
		hlCoche.setWidth(100, Unit.PERCENTAGE);
		hlCoche.setSpacing(true);
		layout.addComponent(hlCoche);

		/* Preferences utilisateurs */
		final String[] colonneChecked = preferenceController.getPrefExportColonnes();

		final OptionGroup multiOptionGroupLeft = new OptionGroup(null, containerLeft);
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

		final OptionGroup multiOptionGroupMiddle = new OptionGroup(null, containerMiddle);
		multiOptionGroupMiddle.setMultiSelect(true);
		multiOptionGroupMiddle.setImmediate(true);
		multiOptionGroupMiddle.setItemCaptionPropertyId("caption");
		multiOptionGroupMiddle.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		hlCoche.addComponent(multiOptionGroupMiddle);
		if (colonneChecked == null) {
			multiOptionGroupMiddle.setValue(setOptionMiddle);
		} else {
			multiOptionGroupMiddle.setValue(getOptionChecked(colonneChecked, setOptionMiddle));
		}

		final OptionGroup multiOptionGroupRight = new OptionGroup(null, containerRight);
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
		final CheckBox rcbFooter = new CheckBox(applicationContext.getMessage("export.footer.check", null, UI.getCurrent().getLocale()));
		final Boolean temFooter = preferenceController.getPrefExportFooter();
		rcbFooter.setValue(temFooter);
		layout.addComponent(rcbFooter);
		layout.setComponentAlignment(rcbFooter, Alignment.MIDDLE_CENTER);

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		/* Annuler */
		final OneClickButton btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		/* Tout selectionner */
		final OneClickButton btnAllCheck = new OneClickButton(applicationContext.getMessage("export.btn.check", null, UI.getCurrent().getLocale()), FontAwesome.CHECK_SQUARE_O);
		btnAllCheck.addClickListener(e -> {
			multiOptionGroupLeft.setValue(setOptionLeft);
			multiOptionGroupMiddle.setValue(setOptionMiddle);
			multiOptionGroupRight.setValue(setOptionRight);
		});
		buttonsLayout.addComponent(btnAllCheck);
		buttonsLayout.setComponentAlignment(btnAllCheck, Alignment.MIDDLE_CENTER);

		/* Preferences */
		final OneClickButton btnPref = new OneClickButton(FontAwesome.COG);
		btnPref.setDescription(applicationContext.getMessage("preference.view.btn", null, UI.getCurrent().getLocale()));
		btnPref.addClickListener(e -> {
			final LinkedHashSet<ExportListCandidatureOption> setCoche = new LinkedHashSet<>();
			setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupLeft.getValue());
			setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupMiddle.getValue());
			setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupRight.getValue());

			final CtrCandPreferenceExportWindow window = new CtrCandPreferenceExportWindow(setCoche, rcbFooter.getValue());
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
					multiOptionGroupMiddle.setValue(setOptionMiddle);
					multiOptionGroupRight.setValue(setOptionRight);
					close();
				}

			});
			UI.getCurrent().addWindow(window);
		});
		buttonsLayout.addComponent(btnPref);
		buttonsLayout.setComponentAlignment(btnPref, Alignment.MIDDLE_CENTER);

		/* Tout deselectionner */
		final OneClickButton btnAllDecheck = new OneClickButton(applicationContext.getMessage("export.btn.uncheck", null, UI.getCurrent().getLocale()), FontAwesome.SQUARE_O);
		btnAllDecheck.addClickListener(e -> {
			multiOptionGroupLeft.setValue(null);
			multiOptionGroupMiddle.setValue(null);
			multiOptionGroupRight.setValue(null);
		});
		buttonsLayout.addComponent(btnAllDecheck);
		buttonsLayout.setComponentAlignment(btnAllDecheck, Alignment.MIDDLE_CENTER);

		/* Exporter */
		final Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()), FontAwesome.FILE_EXCEL_O);
		btnExport.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnExport.setDisableOnClick(true);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				final LinkedHashSet<ExportListCandidatureOption> setCoche = new LinkedHashSet<>();
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupLeft.getValue());
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupMiddle.getValue());
				setCoche.addAll((Set<ExportListCandidatureOption>) multiOptionGroupRight.getValue());

				if (setCoche.size() == 0) {
					btnExport.setEnabled(true);
					return null;
				}

				/* Téléchargement depuis la commission */
				if (commission != null) {
					final OnDemandFile file = ctrCandCandidatureController.generateExport(commission, listeCand, allOptions, setCoche, rcbFooter.getValue());
					if (file != null) {
						btnExport.setEnabled(true);
						return file;
					}
				} else if (centreCandidature != null) {
					final OnDemandFile file = ctrCandCandidatureController.generateExport(centreCandidature, allOptions, setCoche, rcbFooter.getValue());
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
		final LinkedHashSet<ExportListCandidatureOption> listeToRet = new LinkedHashSet<>();
		setOption.stream().forEach(e -> {
			if (Arrays.stream(colonneChecked).filter(f -> f.equals(e.getId())).findAny().isPresent()) {
				listeToRet.add(e);
			}
		});
		return listeToRet;
	}
}
