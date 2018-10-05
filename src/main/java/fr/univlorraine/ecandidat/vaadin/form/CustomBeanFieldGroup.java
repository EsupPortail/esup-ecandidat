/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.vaadin.form;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nLenghtValidator;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nValidator;
import fr.univlorraine.ecandidat.vaadin.form.siscol.CustomFieldGroupFieldFactoryAdr;

/** BeanFieldGroup customisé, qui permet d'afficher les erreurs après le clic bouton et pas avant
 *
 * @author Kevin Hergalant
 * @param <T>
 */
@Configurable(preConstruction = true)
public class CustomBeanFieldGroup<T> extends BeanFieldGroup<T> {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3612930739982458751L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	private Class<T> beanType;

	public CustomBeanFieldGroup(final Class<T> beanType, final String code) {
		super(beanType);
		this.beanType = beanType;
		if (code.equals(ConstanteUtils.TYP_FORM_ADR)) {
			this.setFieldFactory(new CustomFieldGroupFieldFactoryAdr());
		} else if (code.equals(ConstanteUtils.TYP_FORM_CANDIDAT)) {
			this.setFieldFactory(new CustomFieldGroupFieldFactoryCandidat());
		}
	}

	public CustomBeanFieldGroup(final Class<T> beanType) {
		super(beanType);
		this.beanType = beanType;
		this.setFieldFactory(new CustomFieldGroupFieldFactory());
	}

	/** Pre commit tout les champs et affiche les erreurs si besoin */
	public void preCommit() {
		for (Field<?> newField : this.getFields()) {
			IRequiredField field = (IRequiredField) newField;
			field.preCommit();
		}
	}

	/** Avant le comit on valide les champs-->cela affiche les erreurs
	 *
	 * @see com.vaadin.data.fieldgroup.FieldGroup#commit() */
	@Override
	public void commit() throws CommitException {
		for (Field<?> newField : this.getFields()) {
			IRequiredField field = (IRequiredField) newField;
			field.preCommit();
		}
		super.commit();
	}

	/** ajoute le champs ainsi que le validateur, le required, et initialise le field
	 *
	 * @param caption
	 * @param propertyId
	 * @return le field */
	public Field<?> buildAndBind(final String caption, final String propertyId) {
		Field<?> field = super.buildAndBind(caption, propertyId);
		if (MethodUtils.getIsNotNull(this.beanType, propertyId)) {
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			field.setRequired(true);
		}

		if (field instanceof AbstractTextField) {
			((AbstractTextField) field).setNullRepresentation("");
			((AbstractTextField) field).setNullSettingAllowed(true);
		}

		if (field instanceof DateField) {
			((DateField) field).setParseErrorMessage(applicationContext.getMessage("validation.parse.date", null, UI.getCurrent().getLocale()));
		}
		if (field instanceof RequiredIntegerField) {
			((RequiredIntegerField) field).setConversionError(applicationContext.getMessage("validation.parse.int", null, UI.getCurrent().getLocale()));
		}
		if (field instanceof I18nField) {
			if (cacheController.getLangueEnServiceWithoutDefault().size() != 0) {
				field.setRequiredError(applicationContext.getMessage("validation.i18n.obigatoire", null, UI.getCurrent().getLocale()));
			}
			field.addValidator(new I18nValidator(applicationContext.getMessage("validation.i18n.one.missing", null, UI.getCurrent().getLocale()), applicationContext
					.getMessage("validation.i18n.same.lang", null, UI.getCurrent().getLocale())));
			field.addValidator(new I18nLenghtValidator());
		}

		IRequiredField requiredField = (IRequiredField) field;
		requiredField.initField(true);
		return field;
	}

	/** Ajoute un required a la demande
	 *
	 * @param caption
	 * @param propertyId
	 * @param overrideRequired
	 * @return le field */
	public Field<?> buildAndBind(final String caption, final String propertyId, final Boolean overrideRequired) {
		Field<?> field = buildAndBind(caption, propertyId);
		if (overrideRequired) {
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			field.setRequired(true);
		}
		IRequiredField requiredField = (IRequiredField) field;
		requiredField.initField(true);
		return field;
	}

	/** construit un champs avec un type
	 *
	 * @param caption
	 * @param propertyId
	 * @param fieldType
	 * @return le champs
	 * @throws BindException
	 */
	@SuppressWarnings({"rawtypes", "hiding"})
	public <T extends Field> T buildAndBind(final String caption, final String propertyId,
			final Class<T> fieldType) throws BindException {
		T field = super.buildAndBind(caption, propertyId, fieldType);
		if (MethodUtils.getIsNotNull(this.beanType, propertyId)) {
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			field.setRequired(true);
		}
		if (field instanceof AbstractTextField) {
			((AbstractTextField) field).setNullRepresentation("");
			((AbstractTextField) field).setNullSettingAllowed(true);
		}
		IRequiredField requiredField = (IRequiredField) field;
		requiredField.initField(true);
		return field;
	}
}
