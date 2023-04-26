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
package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * FieldGroupFactory utilisé dans l'application pour l'adresse siscol
 * Permet d'utiliser le bon composant pour le bon type
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CustomFieldGroupFieldFactoryAdr extends DefaultFieldGroupFieldFactory {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	/* Encodage par défaut */
	@Value("${charset.default:}")
	private String defaultCharset;

	/**
	 * @see com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory#createField(java.lang.Class, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Field> T createField(final Class<?> dataType, final Class<T> fieldType) {
		/* Le type du champs est un entier */
		if (fieldType == RequiredIntegerField.class) {
			return fieldType.cast(new RequiredIntegerField());
		}
		/* La valeur est siScolPays */
		else if (dataType == SiScolPays.class) {
			return fieldType.cast(new ComboBoxPays(cacheController.getListePays().stream().filter(e -> e.getTemEnSvePay()).collect(Collectors.toList()),
				applicationContext.getMessage("adresse.siScolPays.suggest", null, UI.getCurrent().getLocale())));
		}
		/* La valeur est SiScolCommune */
		else if (dataType == SiScolCommune.class) {
			return fieldType.cast(new ComboBoxCommune(applicationContext.getMessage("adresse.commune.suggest", null, UI.getCurrent().getLocale())));
		}

		/* Sinon, le champs est un simple TextField */
		else {
			return fieldType.cast(new RequiredTextField(defaultCharset));
		}
	}

}
