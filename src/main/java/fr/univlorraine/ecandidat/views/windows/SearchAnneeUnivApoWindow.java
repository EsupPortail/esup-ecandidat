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

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUniPK_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de recherche d'annee univ apogee
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class SearchAnneeUnivApoWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	public static final String[] FIELDS_ORDER = { SiScolAnneeUni_.id.getName() + "." + SiScolAnneeUniPK_.codAnu.getName(), SiScolAnneeUni_.libAnu.getName(), SiScolAnneeUni_.etaAnuIae.getName() };

	/* Composants */
	private final TableFormating tableResult;
	private final OneClickButton btnValider;
	private final OneClickButton btnAnnuler;

	/* Listener */
	private AnneeUniListener anneeUniListener;

	/**
	 * Crée une fenêtre de recherche de anneeUni
	 */
	public SearchAnneeUnivApoWindow() {
		/* Style */
		setWidth(740, Unit.PIXELS);
		setHeight(480, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setHeight(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("window.search.anneeUni.title", null, UI.getCurrent().getLocale()));

		/* Table de Resultat de recherche */
		final List<SiScolAnneeUni> listeAnneeUni = cacheController.getListeAnneeUni();
		if (listeAnneeUni.size() == 0) {
			layout.addComponent(new Label(applicationContext.getMessage("window.search.anneeUni.noannee", null, UI.getCurrent().getLocale())));
		}

		final BeanItemContainer<SiScolAnneeUni> container = new BeanItemContainer<>(SiScolAnneeUni.class, listeAnneeUni);
		container.addNestedContainerProperty(SiScolAnneeUni_.id.getName() + "." + SiScolAnneeUniPK_.codAnu.getName());

		tableResult = new TableFormating(null, container);

		final String[] columnHeadersHarp = new String[FIELDS_ORDER.length];
		for (int fieldIndex = 0; fieldIndex < FIELDS_ORDER.length; fieldIndex++) {
			columnHeadersHarp[fieldIndex] = applicationContext.getMessage("window.search.anneeUni." + FIELDS_ORDER[fieldIndex], null, UI.getCurrent().getLocale());
		}
		tableResult.setVisibleColumns((Object[]) FIELDS_ORDER);
		tableResult.setSortContainerPropertyId(SiScolAnneeUni_.id.getName() + "." + SiScolAnneeUniPK_.codAnu.getName());
		tableResult.setSortAscending(false);
		tableResult.setColumnHeaders(columnHeadersHarp);
		tableResult.setColumnCollapsingAllowed(true);
		tableResult.setColumnReorderingAllowed(true);
		tableResult.setSelectable(true);
		tableResult.setImmediate(true);
		tableResult.setSizeFull();
		tableResult.addItemSetChangeListener(e -> tableResult.sanitizeSelection());

		layout.addComponent(tableResult);
		layout.setExpandRatio(tableResult, 1.0f);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnValider = new OneClickButton(applicationContext.getMessage("btnAdd", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValider.setEnabled(false);
		btnValider.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValider.addClickListener(e -> {
			performAction();
		});
		buttonsLayout.addComponent(btnValider);
		buttonsLayout.setComponentAlignment(btnValider, Alignment.MIDDLE_RIGHT);

		tableResult.addValueChangeListener(e -> {
			/* Le bouton d'enregistrement est actif seulement si un anneeUni est sélectionné. */
			final boolean anneeUniIsSelected = tableResult.getValue() instanceof SiScolAnneeUni;
			btnValider.setEnabled(anneeUniIsSelected);
		});

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Vérifie els donnée et si c'est ok, fait l'action (renvoie le AnneeUni)
	 */
	private void performAction() {
		if (anneeUniListener != null) {
			if (tableResult.getValue() == null) {
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
				return;
			} else {
				final SiScolAnneeUni anneeUni = (SiScolAnneeUni) tableResult.getValue();
				anneeUniListener.btnOkClick(anneeUni.getId().getCodAnu());
				close();
			}
		}
	}

	/**
	 * Défini le 'AnneeUniListener' utilisé
	 * @param anneeUniListener
	 */
	public void addAnneeUniListener(final AnneeUniListener anneeUniListener) {
		this.anneeUniListener = anneeUniListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface AnneeUniListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param anneeUniv l'AnneeUni a renvoyer
		 */
		void btnOkClick(String anneeUniv);

	}

}
