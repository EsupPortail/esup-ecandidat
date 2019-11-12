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

import com.vaadin.data.sort.SortOrder;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.PreferenceController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.template.CtrCandPreferenceWindowTemplate;

/**
 * Fenêtre de preference de vue candidature
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
public class CtrCandPreferenceViewWindow extends CtrCandPreferenceWindowTemplate {

	/*** serialVersionUID */
	private static final long serialVersionUID = 6528147988356073643L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PreferenceController preferenceController;

	private PreferenceViewListener preferenceViewListener;
	private final TextField tfFrozen = new TextField();
	private final Label labelSort = new Label();
	private List<SortOrder> sortOrder;
	private final List<Column> listeColonneView;

	/**
	 * Constructeur
	 * @param listeColonneView
	 * @param frozenCountView
	 * @param maxColumnView
	 * @param sortOrder
	 */
	public CtrCandPreferenceViewWindow(final List<Column> listeColonneView, final Integer frozenCountView, final Integer maxColumnView, final List<SortOrder> sortOrder) {
		super();
		this.sortOrder = sortOrder;
		this.listeColonneView = listeColonneView;
		setCaption(applicationContext.getMessage("preference.window.view", null, UI.getCurrent().getLocale()));
		setInfoMessage(applicationContext.getMessage("preference.info.view", null, UI.getCurrent().getLocale()));

		/* Les colonnes */
		String valeurColonneToReturn = "";
		String txtColonnesVisible = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstVisibleFind = false;
		String txtColonnesInvisible = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstInvisibleFind = false;
		String valeurColonneOrderToReturn = "";
		String txtColonnesOrder = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstOrderFind = false;
		/* Traitement des colonnes */

		for (final Column col : listeColonneView) {
			if (col.isHidden()) {
				if (firstInvisibleFind) {
					txtColonnesInvisible = txtColonnesInvisible + " - ";
				} else {
					txtColonnesInvisible = "";
				}
				txtColonnesInvisible = txtColonnesInvisible + col.getHeaderCaption();
				firstInvisibleFind = true;
			} else {
				if (firstVisibleFind) {
					txtColonnesVisible = txtColonnesVisible + " - ";
				} else {
					txtColonnesVisible = "";
				}
				txtColonnesVisible = txtColonnesVisible + col.getHeaderCaption();
				firstVisibleFind = true;
				valeurColonneToReturn = valeurColonneToReturn + col.getPropertyId() + ";";
			}
			if (firstOrderFind) {
				txtColonnesOrder = txtColonnesOrder + " - ";
			} else {
				txtColonnesOrder = "";
			}
			txtColonnesOrder = txtColonnesOrder + col.getHeaderCaption();
			firstOrderFind = true;
			valeurColonneOrderToReturn = valeurColonneOrderToReturn + col.getPropertyId() + ";";
		}
		final String valeurColonneVisibleToReturnFinal = valeurColonneToReturn;
		final String valeurColonneOrderToReturnFinal = valeurColonneOrderToReturn;

		final VerticalLayout vlSort = new VerticalLayout();
		vlSort.setSpacing(true);
		vlSort.setWidth(100, Unit.PERCENTAGE);
		labelSort.setContentMode(ContentMode.HTML);
		vlSort.addComponent(labelSort);
		updateSort();
		final OneClickButton editSortBtn = new OneClickButton(applicationContext.getMessage("preference.sort.btn", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		editSortBtn.setStyleName(ValoTheme.BUTTON_TINY);
		editSortBtn.addClickListener(e -> {
			final CtrCandPreferenceSortWindow sortWindow = new CtrCandPreferenceSortWindow(listeColonneView, this.sortOrder);
			sortWindow.addPreferenceSortListener(listeSortOrder -> {
				if (listeSortOrder == null || listeSortOrder.size() == 0) {
					this.sortOrder = preferenceController.getDefaultSortOrder();
				} else {
					this.sortOrder = listeSortOrder;
				}
				updateSort();
			});
			UI.getCurrent().addWindow(sortWindow);
		});
		vlSort.addComponent(editSortBtn);

		/* Ajout des composants de colonnes */
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.visible", new Object[] { txtColonnesVisible }, UI.getCurrent().getLocale()), ContentMode.HTML));
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.invisible", new Object[] { txtColonnesInvisible }, UI.getCurrent().getLocale()), ContentMode.HTML));
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.order", new Object[] { txtColonnesOrder }, UI.getCurrent().getLocale()), ContentMode.HTML));
		addComponentSpecifique(vlSort);

		/* Frozen */
		final HorizontalLayout hlFrozen = new HorizontalLayout();
		hlFrozen.setSpacing(true);
		final Label labelFrozen = new Label(applicationContext.getMessage("preference.col.frozen", null, UI.getCurrent().getLocale()), ContentMode.HTML);
		hlFrozen.addComponent(labelFrozen);
		hlFrozen.setComponentAlignment(labelFrozen, Alignment.BOTTOM_LEFT);
		tfFrozen.setMaxLength(2);
		tfFrozen.setColumns(3);
		tfFrozen.setValue(frozenCountView.toString());
		tfFrozen.addStyleName(ValoTheme.TEXTFIELD_TINY);
		hlFrozen.addComponent(tfFrozen);
		addComponentSpecifique(hlFrozen);

		/* Bouton de reinitialisation */
		addReinitClickListener(e -> {
			final Integer frozen = getFrozen(maxColumnView);
			if (frozen != null) {
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("preference.confirm.init", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(c -> {
					if (preferenceViewListener != null) {
						preferenceViewListener.initPref();
					}
					close();
				});
				UI.getCurrent().addWindow(confirmWindow);
			}
		});

		/* Bouton d'enregistrement de session */
		addRecordSessionClickListener(e -> {
			final Integer frozen = getFrozen(maxColumnView);
			if (frozen != null) {
				if (preferenceViewListener != null) {
					preferenceViewListener.saveInSession(valeurColonneVisibleToReturnFinal, valeurColonneOrderToReturnFinal, frozen, this.sortOrder);
				}
				close();
			}
		});

		/* Bouton d'enregistrement en base */
		addRecordDbClickListener(e -> {
			final Integer frozen = getFrozen(maxColumnView);
			if (frozen != null) {
				if (preferenceViewListener != null) {
					preferenceViewListener.saveInDb(valeurColonneVisibleToReturnFinal, valeurColonneOrderToReturnFinal, frozen, this.sortOrder);
				}
				close();
			}
		});

		/* Centre la fenêtre */
		center();
	}

	/** Modifie le label de sort */
	private void updateSort() {
		String txtSort = applicationContext.getMessage("default.label", null, UI.getCurrent().getLocale());
		String sortColonne = null;
		Boolean firstSortFind = false;

		// Le tri
		if (!preferenceController.isDefaultSortOrder(sortOrder)) {
			for (final SortOrder sort : sortOrder) {
				if (sortColonne == null) {
					sortColonne = "";
				}
				sortColonne = sortColonne + sort.getPropertyId()
					+ ":"
					+ (sort.getDirection().equals(SortDirection.ASCENDING) ? ConstanteUtils.PREFERENCE_SORT_DIRECTION_ASCENDING
						: ConstanteUtils.PREFERENCE_SORT_DIRECTION_DESCENDING)
					+ ";";
				if (firstSortFind) {
					txtSort = txtSort + " - ";
				} else {
					txtSort = "";
				}
				txtSort = txtSort + listeColonneView.stream().filter(col -> col.getPropertyId().equals(sort.getPropertyId())).findFirst().get().getHeaderCaption() + " (";
				txtSort = txtSort + (sort.getDirection().equals(SortDirection.ASCENDING) ? applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale())
					: applicationContext.getMessage("preference.col.sort.dir.desc", null, UI.getCurrent().getLocale())) + ")";
				firstSortFind = true;

			}
		}
		labelSort.setValue(applicationContext.getMessage("preference.col.sort", new Object[] { txtSort }, UI.getCurrent().getLocale()));
	}

	/**
	 * @param  maxColumn
	 * @return           le nombre de colonne gelees
	 */
	private Integer getFrozen(final Integer maxColumn) {
		if (tfFrozen.getValue() == null || tfFrozen.getValue().equals("")) {
			Notification.show(applicationContext.getMessage("preference.col.frozen.error", new Object[] { 0, maxColumn }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}
		try {
			final Integer frozen = Integer.valueOf(tfFrozen.getValue());
			if (frozen < 0 || frozen > maxColumn) {
				Notification.show(applicationContext.getMessage("preference.col.frozen.error", new Object[] { 0, maxColumn }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			return frozen;
		} catch (final Exception ex) {
			Notification.show(applicationContext.getMessage("preference.col.frozen.error", new Object[] { 0, maxColumn }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}
	}

	/**
	 * Défini le 'preferenceViewListener' utilisé
	 * @param preferenceViewListener
	 */
	public void addPreferenceViewListener(final PreferenceViewListener preferenceViewListener) {
		this.preferenceViewListener = preferenceViewListener;
	}

	/** Interface pour récupérer un click sur Oui. */
	public interface PreferenceViewListener extends Serializable {
		/** Initialise les preferences */
		void initPref();

		/**
		 * @param valeurColonneVisible
		 * @param valeurColonneOrder
		 * @param frozenCols
		 */
		void saveInSession(String valeurColonneVisible, String valeurColonneOrder, Integer frozenCols, List<SortOrder> sortOrder);

		/**
		 * @param valeurColonneVisible
		 * @param valeurColonneOrder
		 * @param frozenCols
		 */
		void saveInDb(String valeurColonneVisible, String valeurColonneOrder, Integer frozenCols, List<SortOrder> sortOrder);

	}
}
