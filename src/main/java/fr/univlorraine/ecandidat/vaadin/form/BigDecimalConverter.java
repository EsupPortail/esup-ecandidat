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

import java.math.BigDecimal;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import fr.univlorraine.ecandidat.utils.MethodUtils;

@SuppressWarnings("serial")
public class BigDecimalConverter implements Converter<String, BigDecimal> {

	@Override
	public BigDecimal convertToModel(final String value, final Class<? extends BigDecimal> targetType, final Locale locale) throws ConversionException {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		if (!MethodUtils.isStringAsBigDecimal(value)) {
			throw new ConversionException("");
		}
		return MethodUtils.parseStringAsBigDecimal(value);
	}

	@Override
	public String convertToPresentation(final BigDecimal value, final Class<? extends String> targetType, final Locale locale) throws ConversionException {
		return MethodUtils.parseBigDecimalAsString(value);
	}

	@Override
	public Class<BigDecimal> getModelType() {
		return BigDecimal.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
