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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.ThemeResource;

import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Classe de converter de la grid
 * @author Kevin
 *
 */
public class GridConverter {
	
	/**
	 * LocalDateTimeToString Converter
	 * @author Kevin
	 *
	 */
	@SuppressWarnings("serial")
	public static class LocalDateTimeToStringConverter implements Converter<String, LocalDateTime> {

		private DateTimeFormatter formatterDateTime;
		private DateTimeFormatter formatterDate;
		
		public LocalDateTimeToStringConverter(DateTimeFormatter formatterDateTime, DateTimeFormatter formatterDate) {
			super();
			this.formatterDateTime = formatterDateTime;
			this.formatterDate = formatterDate;
		}

		@Override
		public LocalDateTime convertToModel(String value, Class<? extends LocalDateTime> targetType, Locale locale)
				throws ConversionException {	
			if (value==null){
				return null;
			}
			return LocalDateTime.parse(value, formatterDateTime);
		}

		@Override
		public String convertToPresentation(LocalDateTime value, Class<? extends String> targetType, Locale locale)
				throws ConversionException {
			if (value==null){
				return null;
			}
			if (value.getHour()==0 && value.getMinute()==0 && value.getSecond()==0){
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
	 *
	 */
	public static class LocalDateToStringConverter implements Converter<String, LocalDate> {
		
		/**serialVersionUID**/
		private static final long serialVersionUID = -1011457292428354615L;
		
		private DateTimeFormatter formatterDate;

		public LocalDateToStringConverter(DateTimeFormatter formatterDate) {
			super();
			this.formatterDate = formatterDate;
		}

		@Override
		public LocalDate convertToModel(String value, Class<? extends LocalDate> targetType, Locale locale)
				throws ConversionException {	
			if (value==null){
				return null;
			}
			return LocalDate.parse(value, formatterDate);
		}

		@Override
		public String convertToPresentation(LocalDate value, Class<? extends String> targetType, Locale locale)
				throws ConversionException {
			if (value==null){
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
	 *
	 */
	public static class TagToHtmlSquareConverter implements Converter<String, Tag> {

		/**serialVersionUID**/
		private static final long serialVersionUID = 1832861969164079117L;

		@Override
		public Tag convertToModel(String value, Class<? extends Tag> targetType, Locale locale)
				throws ConversionException {	
			return null;
		}

		@Override
		public String convertToPresentation(Tag value, Class<? extends String> targetType, Locale locale)
				throws ConversionException {
			if (value==null || !value.getTesTag()){
				return null;
			}
			return MethodUtils.getHtmlColoredSquare(value.getColorTag(), value.getLibTag(), 20, null);
		}

		@Override
		public Class<Tag> getModelType() {
			return Tag.class;
		}

		@Override
		public Class<String> getPresentationType() {
			return String.class;
		}
	}
	
	public static class StringColorToHtmlSquareConverter implements Converter<String, String> {

		/**serialVersionUID**/
		private static final long serialVersionUID = -7745956017076373371L;

		@Override
		public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
				throws ConversionException {	
			return null;
		}

		@Override
		public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
				throws ConversionException {
			if (value==null){
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
	
	public static class StringToThemeRessourceConverter implements Converter<ThemeResource, String> {
		
		/**serialVersionUID**/
		private static final long serialVersionUID = 6433704687374106657L;
		
		@Override
		public String convertToModel(ThemeResource value, Class<? extends String> targetType, Locale locale)
				throws ConversionException {
			if (value == null){
				return null;
			}
			return value.getResourceId();
		}

		@Override
		public ThemeResource convertToPresentation(String value, Class<? extends ThemeResource> targetType,
				Locale locale) throws ConversionException {
			if (value == null){
				return null;
			}
			return new ThemeResource("images/icon/Flag-"+value+"-icon.png");
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
}
