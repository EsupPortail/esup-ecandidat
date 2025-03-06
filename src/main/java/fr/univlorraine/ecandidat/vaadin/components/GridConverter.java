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
package fr.univlorraine.ecandidat.vaadin.components;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.vaadin.server.ThemeResource;
import com.vaadin.v7.data.util.converter.Converter;

import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Classe de converter de la grid
 * @author Kevin
 */
public class GridConverter {

	/**
	 * LocalDateTimeToString Converter
	 * @author Kevin
	 */
	@SuppressWarnings("serial")
	public static class LocalDateTimeToStringConverter implements Converter<String, LocalDateTime> {

		private final DateTimeFormatter formatterDateTime;
		private final DateTimeFormatter formatterDate;

		public LocalDateTimeToStringConverter(final DateTimeFormatter formatterDateTime, final DateTimeFormatter formatterDate) {
			super();
			this.formatterDateTime = formatterDateTime;
			this.formatterDate = formatterDate;
		}

		@Override
		public LocalDateTime convertToModel(final String value, final Class<? extends LocalDateTime> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			return LocalDateTime.parse(value, formatterDateTime);
		}

		@Override
		public String convertToPresentation(final LocalDateTime value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			if (value.getHour() == 0 && value.getMinute() == 0 && value.getSecond() == 0) {
				return formatterDate.format(value);
			}
			return formatterDateTime.format(value);
		}

		@Override
		public Class<LocalDateTime> getModelType() {
			return LocalDateTime.class;
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
	}

	/**
	 * LocalDate Converter
	 * @author Kevin
	 */
	@SuppressWarnings("serial")
	public static class LocalDateToStringConverter implements Converter<String, LocalDate> {

		private final DateTimeFormatter formatterDate;

		public LocalDateToStringConverter(final DateTimeFormatter formatterDate) {
			super();
			this.formatterDate = formatterDate;
		}

		@Override
		public LocalDate convertToModel(final String value, final Class<? extends LocalDate> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			return LocalDate.parse(value, formatterDate);
		}

		@Override
		public String convertToPresentation(final LocalDate value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			return formatterDate.format(value);
		}

		@Override
		public Class<LocalDate> getModelType() {
			return LocalDate.class;
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
	}

	/**
	 * @author Kevin
	 */
	@SuppressWarnings("serial")
	public static class TagColorToHtmlSquareConverter implements Converter<String, String> {

		@Override
		public String convertToModel(final String value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			return null;
		}

		@Override
		public String convertToPresentation(final String value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			return MethodUtils.getHtmlColoredSquare(value, null, 20, null);
		}

		@Override
		public Class<String> getModelType() {
			return String.class;
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
	}

	/**
	 * @author Kevin
	 */
	@SuppressWarnings("serial")
	public static class TagsToHtmlSquareConverter implements Converter<String, List<Tag>> {

		@Override
		public List<Tag> convertToModel(final String value, final Class<? extends List<Tag>> targetType, final Locale locale)
			throws ConversionException {
			return null;
		}

		@Override
		public String convertToPresentation(final List<Tag> value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			if (value == null || value.size() == 0) {
				return null;
			}
			final StringBuilder sb = new StringBuilder();
			value.stream()
				.filter(e -> e.getTesTag())
				.map(e -> MethodUtils.getHtmlColoredSquare(e.getColorTag(), e.getLibTag(), 20, null) + "&nbsp;")
				.forEach(sb::append);
			return sb.toString();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class<List<Tag>> getModelType() {
			return ((Class) List.class);
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
	}

	@SuppressWarnings("serial")
	public static class StringColorToHtmlSquareConverter implements Converter<String, String> {

		@Override
		public String convertToModel(final String value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			return null;
		}

		@Override
		public String convertToPresentation(final String value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			return MethodUtils.getHtmlColoredSquare(value, "", 20, "margin-left:14px;");
		}

		@Override
		public Class<String> getModelType() {
			return String.class;
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
	}

	@SuppressWarnings("serial")
	public static class StringToThemeRessourceConverter implements Converter<ThemeResource, String> {

		@Override
		public String convertToModel(final ThemeResource value, final Class<? extends String> targetType, final Locale locale)
			throws ConversionException {
			if (value == null) {
				return null;
			}
			return value.getResourceId();
		}

		@Override
		public ThemeResource convertToPresentation(final String value,
			final Class<? extends ThemeResource> targetType,
			final Locale locale) throws ConversionException {
			if (value == null) {
				return null;
			}
			return new ThemeResource("images/icon/Flag-" + value + "-icon.png");
		}

		@Override
		public Class<String> getModelType() {
			return String.class;
		}

		@Override
		public Class<ThemeResource> getPresentationType() {
			return ThemeResource.class;
		}
	}

	@SuppressWarnings("serial")
	public static class BigDecimalMonetaireToStringConverter implements Converter<String, BigDecimal> {

		@Override
		public BigDecimal convertToModel(final String value, final Class<? extends BigDecimal> targetType, final Locale locale) throws ConversionException {
			if (value == null) {
				return null;
			}
			return new BigDecimal(value);
		}

		@Override
		public String convertToPresentation(final BigDecimal value, final Class<? extends String> targetType, final Locale locale) throws ConversionException {
			if (value == null) {
				return null;
			}
			return String.valueOf(value) + "&euro;";
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
}
