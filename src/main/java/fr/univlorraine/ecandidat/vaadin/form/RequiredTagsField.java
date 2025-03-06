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
package fr.univlorraine.ecandidat.vaadin.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.ui.renderers.HtmlRenderer;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag_;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.TagColorToHtmlSquareConverter;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import jakarta.annotation.Resource;

/**
 * Champs tags
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class RequiredTagsField extends GridFormatting<Tag> {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	public static final String[] FIELDS_ORDER = { Tag_.colorTag.getName(), Tag_.libTag.getName() };

	/**
	 * Constructeur, initialisation du champs
	 */
	public RequiredTagsField() {
		super(Tag.class);
		setWidth(100, Unit.PERCENTAGE);

		/* Grid des candidatures */
		initColumn(FIELDS_ORDER, "tag.table.", Tag_.idTag.getName());
		setSelectionMode(SelectionMode.MULTI);
		/* Ajout du flag de couleur */
		setColumnConverter(Tag_.colorTag.getName(), new TagColorToHtmlSquareConverter());
		setColumnRenderer(Tag_.colorTag.getName(), new HtmlRenderer());
		setColumnWidth(Tag_.colorTag.getName(), 90);
		removeFilterRow();

	}

	public void setTagsItems(final List<Tag> listeTag) {
		addItems(listeTag);
	}

	/**
	 * Modifie les tags selectionnés
	 * @param tags
	 */
	public void setTags(final List<Tag> tags) {
		tags.forEach(tag -> {
			select(tag);
		});
	}

	/**
	 * @return les tags selectionnés
	 */
	public List<Tag> getTags() {
		final List<Tag> listeSelected = new ArrayList<>();
		getSelectedRows().forEach(candItem -> {
			try {
				listeSelected.add(getItem(candItem));
			} catch (final Exception e) {
			}
		});
		return listeSelected;
	}

}
