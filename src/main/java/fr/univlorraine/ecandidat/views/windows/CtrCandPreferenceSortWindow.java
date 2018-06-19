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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.PreferenceController;
import fr.univlorraine.ecandidat.utils.bean.presentation.SortOrderPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** Fenêtre de preference de sort
 *
 * @author Kevin Hergalant */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
@Configurable(preConstruction = true)
public class CtrCandPreferenceSortWindow extends Window {

	public static final String[] FIELDS_ORDER = {SortOrderPresentation.CHAMPS_ORDER, SortOrderPresentation.CHAMPS_PROPERTY_NAME, SortOrderPresentation.CHAMPS_DIRECTION,
			SortOrderPresentation.CHAMPS_EXCHANGE,
			SortOrderPresentation.CHAMPS_MONTE, SortOrderPresentation.CHAMPS_DESCEND, SortOrderPresentation.CHAMPS_DELETE};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PreferenceController preferenceController;

	private PreferenceSortListener preferenceSortListener;

	private final List<SortOrderPresentation> listeColonne;
	private BeanItemContainer<SortOrderPresentation> container = new BeanItemContainer<>(SortOrderPresentation.class);
	private Grid grid = new Grid(container);
	private RequiredComboBox<SortOrderPresentation> rcbColonne;

	/** Crée une fenêtre pour enregistrer ses préférences de tri
	 *
	 * @param listeColonneView
	 *            les colonnes
	 * @param listeSortOrder
	 *            l'ordre */
	public CtrCandPreferenceSortWindow(final List<Column> listeColonneView, final List<SortOrder> listeSortOrder) {
		super();
		this.listeColonne = convertColonneToSortOrderPresentation(listeColonneView);
		setCaption(applicationContext.getMessage("preference.window.view.sort", null, UI.getCurrent().getLocale()));

		/* Style */
		setModal(true);
		setWidth(900, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setWidth(100, Unit.PERCENTAGE);
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		setContent(contentLayout);

		HorizontalLayout hlAddItem = new HorizontalLayout();
		hlAddItem.setSpacing(true);
		rcbColonne = new RequiredComboBox(this.listeColonne, SortOrderPresentation.class);
		rcbColonne.setItemCaptionPropertyId(SortOrderPresentation.CHAMPS_PROPERTY_NAME);
		hlAddItem.addComponent(rcbColonne);

		OneClickButton btnPlus = new OneClickButton(FontAwesome.PLUS_CIRCLE);
		btnPlus.addClickListener(e -> {
			if (rcbColonne.getValue() != null) {
				SortOrderPresentation val = (SortOrderPresentation) rcbColonne.getValue();
				val.setOrder(container.getItemIds().size() + 1);
				container.addBean(val);
				updateComboBox();
			}
		});
		hlAddItem.addComponent(btnPlus);

		contentLayout.addComponent(hlAddItem);

		container.addAll(convertSortOrderToSortOrderPresentation(listeSortOrder));
		grid.setSelectionMode(SelectionMode.NONE);
		grid.setSizeFull();
		grid.setImmediate(true);
		grid.removeColumn(SortOrderPresentation.CHAMPS_PROPERTY_ID);
		grid.removeColumn(SortOrderPresentation.CHAMPS_EXCHANGE_ASC);
		grid.removeColumn(SortOrderPresentation.CHAMPS_EXCHANGE_DESC);
		grid.setColumnOrder((Object[]) FIELDS_ORDER);
		grid.getColumns().forEach(e -> {
			e.setSortable(false);
			e.setHidable(false);
		});
		grid.getColumn(SortOrderPresentation.CHAMPS_ORDER).setHeaderCaption(applicationContext.getMessage("preference.sort.col."
				+ SortOrderPresentation.CHAMPS_ORDER, null, UI.getCurrent().getLocale()));
		grid.getColumn(SortOrderPresentation.CHAMPS_PROPERTY_NAME).setHeaderCaption(applicationContext.getMessage("preference.sort.col."
				+ SortOrderPresentation.CHAMPS_PROPERTY_NAME, null, UI.getCurrent().getLocale()));
		grid.getColumn(SortOrderPresentation.CHAMPS_DIRECTION).setHeaderCaption(applicationContext.getMessage("preference.sort.col."
				+ SortOrderPresentation.CHAMPS_DIRECTION, null, UI.getCurrent().getLocale()));
		grid.getColumn(SortOrderPresentation.CHAMPS_EXCHANGE).setRenderer(new HtmlRenderer()).setWidth(53).setHeaderCaption("");
		grid.getColumn(SortOrderPresentation.CHAMPS_MONTE).setRenderer(new HtmlRenderer()).setWidth(50).setHeaderCaption("");
		grid.getColumn(SortOrderPresentation.CHAMPS_DESCEND).setRenderer(new HtmlRenderer()).setWidth(50).setHeaderCaption("");
		grid.getColumn(SortOrderPresentation.CHAMPS_DELETE).setRenderer(new HtmlRenderer()).setWidth(50).setHeaderCaption("");
		grid.addItemClickListener(e -> {
			if (e.getPropertyId().equals(SortOrderPresentation.CHAMPS_MONTE)) {
				changeRowPosition(e.getItem(), 1);
			} else if (e.getPropertyId().equals(SortOrderPresentation.CHAMPS_DESCEND)) {
				changeRowPosition(e.getItem(), -1);
			} else if (e.getPropertyId().equals(SortOrderPresentation.CHAMPS_DELETE)) {
				deleteRow(e.getItem());
			} else if (e.getPropertyId().equals(SortOrderPresentation.CHAMPS_EXCHANGE)) {
				changeRowDirection(e.getItem());
			}
		});

		/* Ajout d'un header */
		grid.getDefaultHeaderRow().join(SortOrderPresentation.CHAMPS_EXCHANGE, SortOrderPresentation.CHAMPS_MONTE, SortOrderPresentation.CHAMPS_DESCEND, SortOrderPresentation.CHAMPS_DELETE).setText(applicationContext.getMessage("preference.sort.action", null, getLocale()));

		contentLayout.addComponent(grid);

		// met a jour les pref
		updateComboBox();

		// trie la grid
		sortGrid();

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		contentLayout.addComponent(buttonsLayout);

		OneClickButton btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		OneClickButton btnValid = new OneClickButton(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.CHECK);
		btnValid.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValid.addClickListener(e -> {
			/* Valide la saisie */
			preferenceSortListener.validSortPref(convertSortOrderPresentation(container.getItemIds()));
			/* Ferme la fenêtre */
			close();
		});
		buttonsLayout.addComponent(btnValid);
		buttonsLayout.setComponentAlignment(btnValid, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/** Tri la grid */
	private void sortGrid() {
		List<SortOrder> listSortOrder = new ArrayList<>();
		listSortOrder.add(new SortOrder(SortOrderPresentation.CHAMPS_ORDER, SortDirection.ASCENDING));
		grid.setSortOrder(listSortOrder);
	}

	/** Modifie la combo de selection */
	private void updateComboBox() {
		List<SortOrderPresentation> liste = new ArrayList<>(listeColonne);
		container.getItemIds().forEach(e -> {
			if (liste.contains(e)) {
				liste.remove(e);
			}
		});
		rcbColonne.removeAllItems();
		rcbColonne.addItems(liste);
	}

	/** Modifie la direction d'un item
	 *
	 * @param item
	 */
	private void changeRowDirection(final Item item) {
		SortOrderPresentation bean = (SortOrderPresentation) ((BeanItem) item).getBean();
		if (bean.getDirection().equals(applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale()))) {
			bean.setDirection(applicationContext.getMessage("preference.col.sort.dir.desc", null, UI.getCurrent().getLocale()));
			bean.setExchangeAsc();
		} else {
			bean.setDirection(applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale()));
			bean.setExchangeDesc();
		}
		grid.refreshAllRows();
	}

	/** Modifie la position d'un item
	 *
	 * @param item
	 * @param change
	 */
	private void changeRowPosition(final Item item, final int change) {
		Integer orderBean = ((SortOrderPresentation) ((BeanItem) item).getBean()).getOrder();
		container.getItemIds().forEach(e -> {
			if (change < 0 && orderBean < container.getItemIds().size()) {
				if (e.getOrder().equals(orderBean)) {
					e.setOrder(e.getOrder() + 1);
				} else if (e.getOrder().equals(orderBean + 1)) {
					e.setOrder(e.getOrder() - 1);
				}
			} else if (change > 0 && orderBean > 1) {
				if (e.getOrder().equals(orderBean)) {
					e.setOrder(e.getOrder() - 1);
				} else if (e.getOrder().equals(orderBean - 1)) {
					e.setOrder(e.getOrder() + 1);
				}
			}
		});
		grid.refreshAllRows();
		sortGrid();
	}

	/** Supprime une ligne
	 *
	 * @param item
	 */
	private void deleteRow(final Item item) {
		SortOrderPresentation bean = (SortOrderPresentation) ((BeanItem) item).getBean();
		Integer orderBean = bean.getOrder();
		container.removeItem(bean);
		container.getItemIds().forEach(e -> {
			if (e.getOrder() > orderBean) {
				e.setOrder(e.getOrder() - 1);
			}
		});
		grid.refreshAllRows();
		sortGrid();
	}

	/** Converti une liste de trie en beans de presentation
	 * 
	 * @param listeSortOrder
	 * @return la liste convertie */
	private List<SortOrderPresentation> convertSortOrderToSortOrderPresentation(List<SortOrder> listeSortOrder) {
		if (listeSortOrder == null || preferenceController.isDefaultSortOrder(listeSortOrder)) {
			listeSortOrder = new ArrayList<>();
		}
		List<SortOrderPresentation> liste = new ArrayList<>();
		Integer i = 1;
		for (SortOrder e : listeSortOrder) {
			SortOrderPresentation pres = new SortOrderPresentation((String) e.getPropertyId());
			pres.setIcons(applicationContext.getMessage("preference.sort.col.monte.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.descend.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.delete.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.exchangeDesc.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.exchangeAsc.tooltip", null, UI.getCurrent().getLocale()));
			pres.setPropertyName(applicationContext.getMessage("candidature.table." + e.getPropertyId(), null, UI.getCurrent().getLocale()));
			if (e.getDirection().equals(SortDirection.ASCENDING)) {
				pres.setExchangeDesc();
				pres.setDirection(applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale()));
			} else {
				pres.setExchangeAsc();
				pres.setDirection(applicationContext.getMessage("preference.col.sort.dir.desc", null, UI.getCurrent().getLocale()));
			}
			pres.setOrder(i);
			liste.add(pres);
			i++;
		}
		return liste;
	}

	/** Converti une ligne de colonne en beans de presentation
	 * 
	 * @param listeColonneView
	 * @return la liste convertie */
	private List<SortOrderPresentation> convertColonneToSortOrderPresentation(final List<Column> listeColonneView) {
		List<SortOrderPresentation> liste = new ArrayList<>();
		listeColonneView.forEach(e -> {
			SortOrderPresentation pres = new SortOrderPresentation((String) e.getPropertyId());
			pres.setIcons(applicationContext.getMessage("preference.sort.col.monte.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.descend.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.delete.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.exchangeDesc.tooltip", null, UI.getCurrent().getLocale()), applicationContext.getMessage("preference.sort.col.exchangeAsc.tooltip", null, UI.getCurrent().getLocale()));
			pres.setPropertyName(e.getHeaderCaption());
			pres.setExchangeDesc();
			pres.setDirection(applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale()));
			liste.add(pres);
		});
		return liste;
	}

	/** Converti un bean de presentation en tri vaadin
	 * 
	 * @param listePres
	 * @return la liste convertie */
	private List<SortOrder> convertSortOrderPresentation(final List<SortOrderPresentation> listePres) {
		List<SortOrder> liste = new ArrayList<>();
		listePres.forEach(e -> {
			liste.add(new SortOrder(e.getPropertyId(), (e.getDirection().equals(applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale()))
					? SortDirection.ASCENDING
					: SortDirection.DESCENDING)));
		});
		return liste;
	}

	/** Défini le 'preferenceSortListener' utilisé
	 *
	 * @param preferenceSortListener
	 */
	public void addPreferenceSortListener(final PreferenceSortListener preferenceSortListener) {
		this.preferenceSortListener = preferenceSortListener;
	}

	/** Interface pour récupérer un click sur Oui. */
	public interface PreferenceSortListener extends Serializable {

		/** Valide */
		public void validSortPref(List<SortOrder> listeSortOrder);

	}
}
