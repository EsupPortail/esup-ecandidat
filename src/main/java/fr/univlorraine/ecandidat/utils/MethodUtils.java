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
package fr.univlorraine.ecandidat.utils;

import java.io.Closeable;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;

/**
 * Class de methode utilitaires
 * @author Kevin Hergalant
 */
public class MethodUtils {

	public static final Pattern PATTERN_INTEGER = Pattern.compile("^\\d+$");

	/**
	 * @param  integerString
	 * @return               true si la chaine passée en paramètre est un integer
	 */
	public static Boolean isInteger(final String integerString) {
		return integerString != null && PATTERN_INTEGER.matcher(integerString).matches();
	}

	/**
	 * Renvoi pour une classe donnée si le champs est nullable ou non
	 * @param  classObject
	 * @param  property
	 * @return             true si l'objet n'est pas null
	 */
	public static Boolean getIsNotNull(final Class<?> classObject, final String property) {
		try {
			final NotNull notNull = classObject.getDeclaredField(property).getAnnotation(NotNull.class);
			if (notNull == null) {
				return false;
			}
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
		}
	}

	/**
	 * Renvoi un boolean pour un temoin en string (O ou N)
	 * @param  temoin
	 * @return        le boolean associe
	 */
	public static Boolean getBooleanFromTemoin(final String temoin) {
		if (temoin == null || temoin.equals(ConstanteUtils.TYP_BOOLEAN_NO)) {
			return false;
		}
		return true;
	}

	/**
	 * Renvoi temoin en string (O ou N) pour un boolean
	 * @param  bool
	 * @return      le String associe
	 */
	public static String getTemoinFromBoolean(final Boolean bool) {
		if (!bool) {
			return ConstanteUtils.TYP_BOOLEAN_NO;
		}
		return ConstanteUtils.TYP_BOOLEAN_YES;
	}

	/**
	 * Ajoute du texte à la suite et place une virgule entre
	 * @param  text
	 * @param  more
	 * @return      le txt complété
	 */
	public static String constructStringEnum(final String text, final String more) {
		if (text == null || text.equals("")) {
			return more;
		} else {
			return text + ", " + more;
		}
	}

	/**
	 * Ajoute un 0 devant le label de temps pour 0, 1, 2, etc..
	 * @param  time
	 * @return      le label de minute ou d'heure complété
	 */
	public static String getLabelMinuteHeure(final Integer time) {
		if (time == null) {
			return "";
		} else {
			String temps = String.valueOf(time);
			if (temps.length() == 1) {
				temps = "0" + temps;
			}
			return temps;
		}
	}

	/**
	 * @param  millis
	 * @return        un label de millisecondes
	 */
	public static Integer getStringMillisecondeToInt(final String millis) {
		if (millis != null) {
			try {
				return Integer.valueOf(millis);
			} catch (final Exception e) {
			}
		}
		return 0;
	}

	/**
	 * @param  millis
	 * @return        un label de millisecondes
	 */
	public static String getIntMillisecondeToString(final Integer millis) {
		if (millis == null || millis.equals(0)) {
			return "00 sec";
		} else if (millis < 60000) {
			return String.format("%02d sec", TimeUnit.MILLISECONDS.toSeconds(millis));
		} else if (millis % 60000 == 0) {
			return String.format("%02d min", TimeUnit.MILLISECONDS.toMinutes(millis));
		} else {
			return String.format("%02d min, %02d sec",
				TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		}
	}

	/**
	 * Nettoie un nom de fichier pour le stockage fs
	 * @param  fileName
	 * @return          le nom de fichier pour le stockage fs
	 */
	public static String cleanFileName(final String fileName) {
		if (fileName == null || fileName.equals("")) {
			return "_";
		}
		return removeAccents(fileName).replaceAll("[^A-Za-z0-9\\.\\-\\_]", "");
	}

	/**
	 * Remplace les accents
	 * @param  text
	 * @return      le text sans accents
	 */
	public static String removeAccents(final String text) {
		return text == null ? "" : Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	/**
	 * Valide un bean
	 * @param  bean
	 * @throws CustomException
	 */
	public static <T> Boolean validateBean(final T bean, final Logger logger, final Boolean logOnlyError) {
		if (!logOnlyError) {
			logger.debug(" ***VALIDATION*** : " + bean);
		}
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		final Set<ConstraintViolation<T>> constraintViolations = validator.validate(bean);
		if (constraintViolations != null && constraintViolations.size() > 0) {
			for (final ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(" *** " + violation.getPropertyPath().toString() + " : " + violation.getMessage());
			}
			return false;
		}
		return true;
	}

	/**
	 * Changes the annotation value for the given key of the given annotation to newValue and returns
	 * the previous value.
	 */
	@SuppressWarnings("unchecked")
	public static Object changeAnnotationValue(final Annotation annotation, final String key, final Object newValue) {
		final Object handler = Proxy.getInvocationHandler(annotation);
		Field f;
		try {
			f = handler.getClass().getDeclaredField("memberValues");
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(e);
		}
		f.setAccessible(true);
		Map<String, Object> memberValues;
		try {
			memberValues = (Map<String, Object>) f.get(handler);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		final Object oldValue = memberValues.get(key);
		if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
			throw new IllegalArgumentException();
		}
		memberValues.put(key, newValue);
		return oldValue;
	}

	/**
	 * Valide un bean
	 * @param  bean
	 * @throws CustomException
	 */
	public static <T> Boolean validateBean(final T bean, final Logger logger) {
		return validateBean(bean, logger, false);
	}

	/**
	 * Formate un texte
	 * @param  txt
	 * @return     un txt formaté
	 */
	public static String formatToExport(final String txt) {
		if (txt == null) {
			return "";
		}
		return txt;
	}

	/**
	 * Formate un Integer
	 * @param  nb
	 * @return    un txt formaté
	 */
	public static String formatIntToExport(final Integer nb) {
		if (nb == null) {
			return "";
		}
		return String.valueOf(nb);
	}

	/**
	 * Formate un Boolean
	 * @param  val
	 * @return     un txt formaté
	 */
	public static String formatBoolToExport(final Boolean val) {
		if (val == null) {
			return "";
		}
		return val ? ConstanteUtils.TYP_BOOLEAN_YES : ConstanteUtils.TYP_BOOLEAN_NO;
	}

	/**
	 * Formate un texte et supprime les balise HTML
	 * @param  txt
	 * @return     un txt formaté
	 */
	public static String formatToExportHtml(final String txt) {
		return formatToExport(txt).replaceAll("\\<.*?>", "");
	}

	/**
	 * Verifie que le fichier est un pdf
	 * @param  fileName
	 * @return          true si le fichier est un pdf
	 */
	public static Boolean isPdfFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_PDF).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * Verifie que le fichier est une image
	 * @param  fileName
	 * @return          true si le fichier est une image
	 */
	public static Boolean isImgFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_IMG).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * Verifie que le fichier est un jpg
	 * @param  fileName
	 * @return          true si le fichier est un jpg
	 */
	public static Boolean isJpgFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_JPG).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * Verifie que le fichier est un png
	 * @param  fileName
	 * @return          true si le fichier est un png
	 */
	public static Boolean isPngFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_PNG).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * renvoie l'extension
	 * @param  fileName
	 * @return          l'extension du fichier
	 */
	public static String getExtension(final String fileName) {
		String extension = "";

		final int i = fileName.lastIndexOf('.');
		if (i >= 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	/**
	 * @param  liste
	 * @param  code
	 * @return       le libellé de presentation
	 */
	public static String getLibByPresentationCode(final List<SimpleTablePresentation> liste, final String code) {
		final Optional<SimpleTablePresentation> opt = liste.stream().filter(e -> e.getCode().equals(code)).findFirst();
		if (opt.isPresent() && opt.get().getValue() != null) {
			return opt.get().getValue().toString();
		}
		return "";
	}

	/**
	 * Verifie qu'une date est inclue dans un intervalle
	 * @param  dateToCompare
	 * @return               true si la date est incluse dans un interval
	 */
	public static Boolean isDateIncludeInInterval(final LocalDate dateToCompare, final LocalDate dateDebut, final LocalDate dateFin) {
		if (dateToCompare == null) {
			/* Si la date est null, c'est ok! */
			return true;
		} else if ((dateToCompare.equals(dateDebut) || dateToCompare.isAfter(dateDebut)) && (dateToCompare.equals(dateFin) || dateToCompare.isBefore(dateFin))) {
			return true;
		}
		return false;
	}

	/**
	 * Converti un String en entier
	 * @param  txt
	 * @return     l'entier converti
	 */
	public static Integer convertStringToIntger(final String txt) {
		if (txt == null) {
			return null;
		}
		try {
			return Integer.valueOf(txt);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Converti une date en LocalDate
	 * @param  date
	 * @return      la localDate convertie
	 */
	public static LocalDate convertDateToLocalDate(final Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Converti une LocalDate en date
	 * @param  date
	 * @return      la date convertie
	 */
	public static Date convertLocalDateToDate(final LocalDate date) {
		if (date == null) {
			return null;
		}
		final Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	/**
	 * Converti une LocalDateTime en LocalDate
	 * @param  localDateTime
	 * @return               la date convertie
	 */
	public static LocalDate convertLocalDateTimeToDate(final LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	/**
	 * Replace la derniere occurence
	 * @param  string
	 * @param  from
	 * @param  to
	 * @return        le string nettoye
	 */
	public static String replaceLast(final String string, final String from, final String to) {
		final int lastIndex = string.lastIndexOf(from);
		if (lastIndex < 0) {
			return string;
		}
		final String tail = string.substring(lastIndex).replaceFirst(from, to);
		return string.substring(0, lastIndex) + tail;
	}

	/**
	 * @param  fileName
	 * @param  isOnlyImg
	 * @return           true si l'extension est jpg ou pdf
	 */
	public static Boolean checkExtension(final String fileName, final Boolean isOnlyImg) {
		String extension = "";
		final int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		} else {
			return false;
		}

		if (extension.equals("")) {
			return false;
		}
		extension = extension.toLowerCase();
		if (!isOnlyImg && Arrays.asList(ConstanteUtils.EXTENSION_PDF_IMG).contains(extension)) {
			return true;
		} else if (isOnlyImg && Arrays.asList(ConstanteUtils.EXTENSION_IMG).contains(extension)) {
			return true;
		}
		return false;
	}

	/**
	 * @param  nomFichier
	 * @return            le type MIME d'un fichier
	 */
	public static String getMimeType(final String nomFichier) {
		if (isPdfFileName(nomFichier)) {
			return ConstanteUtils.TYPE_MIME_FILE_PDF;
		} else if (isJpgFileName(nomFichier)) {
			return ConstanteUtils.TYPE_MIME_FILE_JPG;
		} else if (isPngFileName(nomFichier)) {
			return ConstanteUtils.TYPE_MIME_FILE_PNG;
		} else if (Arrays.asList(new String[] { "docx" }).contains(getExtension(nomFichier.toLowerCase()))) {
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		}
		return null;
	}

	/**
	 * @param  path
	 * @return      la path agrémenté d'un / a la fin
	 */
	public static String formatUrlApplication(String path) {
		if (path != null && !path.equals("")) {
			if (!path.substring(path.length() - 1).equals("/")) {
				path += "/";
			}
		}
		return path;
	}

	/**
	 * @param  appPath
	 * @param  add
	 * @return         l'url formatée pour switch user
	 */
	public static String formatSecurityPath(String appPath, final String add) {
		if (appPath != null && !appPath.equals("")) {
			if (appPath.substring(appPath.length() - 1).equals("/")) {
				appPath = appPath.substring(0, appPath.length() - 1);
			}
		}
		return appPath + add;
	}

	/**
	 * @param  date
	 * @param  formatterDate
	 * @return               la date formatee
	 */
	public static String formatDate(final LocalDateTime date, final DateTimeFormatter formatterDate) {
		if (date == null) {
			return "";
		} else {
			return date.format(formatterDate);
		}
	}

	/**
	 * @param  date
	 * @param  formatterDate
	 * @return               la date formatee
	 */
	public static String formatDate(final LocalDate date, final DateTimeFormatter formatterDate) {
		if (date == null) {
			return "";
		} else {
			return date.format(formatterDate);
		}
	}

	/**
	 * @param  date
	 * @param  formatterDate
	 * @return               la date formatee
	 */
	public static String formatTime(final LocalTime time, final DateTimeFormatter formatterTime) {
		if (time == null) {
			return "";
		} else {
			return time.format(formatterTime);
		}
	}

	/** @return la version des WS */
	public static String getClassVersion(final Class<?> theClass) {
		try {

			// Find the path of the compiled class
			final String classPath = theClass.getResource(theClass.getSimpleName() + ".class").toString();

			// Find the path of the lib which includes the class
			String libPath = classPath.substring(0, classPath.lastIndexOf("!"));

			if (libPath != null) {
				final Integer lastIndex = libPath.lastIndexOf("/");
				if (lastIndex != -1) {
					libPath = libPath.substring(lastIndex + 1, libPath.length());
					libPath = libPath.replaceAll(".jar", "");
					return libPath;
				}
			}
		} catch (final Exception e) {
		}
		return "";
	}

	/**
	 * Vérifie si une throwable appartient à une classe
	 * @param  cause
	 * @param  causeSearch
	 * @return             true si la cause existe
	 */
	public static Boolean checkCause(final Throwable cause, final String causeSearch) {
		try {
			if (cause.getClass().getName().contains(causeSearch)) {
				return true;
			}
		} catch (final Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * @param  cause
	 * @param  causeSearch
	 * @param  lineNumber
	 * @return             vérifie si la premiere cause de la stack appartient a une classe
	 */
	public static Boolean checkCauseByStackTrace(final Throwable cause, final String causeSearch, final Integer lineNumber) {
		try {
			if (cause.getStackTrace()[lineNumber].getClassName().contains(causeSearch)) {
				return true;
			}
		} catch (final Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Vérifie si une throwable possède un message ciblé
	 * @param  cause
	 * @param  messageSearch
	 * @return               true si le message existe
	 */
	public static Boolean checkCauseByMessage(final Throwable cause, final String messageSearch) {
		try {
			if (cause.getMessage().contains(messageSearch)) {
				return true;
			}
		} catch (final Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Pour vérifier le cas des "NullPointerException" sans stack
	 * @param  cause
	 * @return       true si il n'y a pas de stackTrace
	 */
	public static Boolean checkCauseEmpty(final Throwable cause) {
		try {
			if (cause.getStackTrace() == null || cause.getStackTrace().length == 0) {
				return true;
			}
		} catch (final Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * @param  e
	 *                          l'exception
	 * @param  clazz
	 *                          la class à trouver
	 * @param  messageToFind
	 *                          le message à trouver
	 * @return               true si l'exception correspond et que le message a été trouvé
	 */
	public static Boolean checkExceptionAndMessage(final Exception e, final Class<?> clazz, final String messageToFind) {
		try {
			if (e != null && clazz.isInstance(e) && e.getMessage() != null && e.getMessage().contains(messageToFind)) {
				return true;
			}
		} catch (final Exception ex) {
		}
		return false;
	}

	/**
	 * @param  value
	 * @return       le string modifié en upperCase et sans espace à la fin
	 */
	public static String cleanForSiScol(String value) {
		if (value == null) {
			return null;
		}
		value = value.replaceAll("\\s+$", "");
		return value.toUpperCase();
	}

	/**
	 * @param  value
	 * @return       supprime les accents
	 */
	public static String cleanForSiScolWS(final String value) {
		if (value == null) {
			return null;
		}
		return Normalizer.normalize(cleanForSiScol(value), Normalizer.Form.NFD).replaceAll("[̀-ͯ]", "");
	}

	/**
	 * @param  typGestionCandidature
	 * @return                       true si le mode de gestion de candidature est centre de candidature
	 */
	public static boolean isGestionCandidatureCtrCand(final String typGestionCandidature) {
		if (typGestionCandidature == null) {
			return false;
		}
		return typGestionCandidature.equals(ConstanteUtils.TYP_GESTION_CANDIDATURE_CTR_CAND);
	}

	/**
	 * @param  typGestionCandidature
	 * @return                       true si le mode de gestion de candidature est candidat
	 */
	public static boolean isGestionCandidatureCandidat(final String typGestionCandidature) {
		if (typGestionCandidature == null) {
			return false;
		}
		return typGestionCandidature.equals(ConstanteUtils.TYP_GESTION_CANDIDATURE_CANDIDAT);
	}

	/**
	 * @param  typGestionCandidature
	 * @return                       true si le mode de gestion de candidature est commission
	 */
	public static boolean isGestionCandidatureCommission(final String typGestionCandidature) {
		if (typGestionCandidature == null) {
			return false;
		}
		return typGestionCandidature.equals(ConstanteUtils.TYP_GESTION_CANDIDATURE_COMMISSION);
	}

	/**
	 * @param  fileNameDefault
	 * @param  codeLangue
	 * @param  codLangueDefault
	 * @return                  le template XDocReport
	 */
	public static InputStream getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault) {
		return getXDocReportTemplate(fileNameDefault, codeLangue, codLangueDefault, null);
	}

	/**
	 * @param  fileNameDefault
	 * @param  codeLangue
	 * @param  codLangueDefault
	 * @param  subPath
	 * @param  suffixe
	 * @return                  le template XDocReport
	 */
	public static InputStream getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault, final String subPath, final String suffixe) {

		/* On cherche le fichier du suffixe "séparé par _ " */
		InputStream in = getXDocReportTemplate(fileNameDefault + "_" + suffixe, codeLangue, codLangueDefault, subPath);

		/* Si il n'existe pas on renvoit le fichier par défaut */
		if (in == null) {
			in = getXDocReportTemplate(fileNameDefault, codeLangue, codLangueDefault);
		}
		return in;
	}

	/**
	 * @param  fileNameDefault
	 * @param  codeLangue
	 * @param  codLangueDefault
	 * @param  subPath
	 * @return                  le template XDocReport
	 */
	public static InputStream getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault, final String subPath) {
		String resourcePath = "/" + ConstanteUtils.TEMPLATE_PATH + "/";
		if (subPath != null) {
			resourcePath = resourcePath + subPath + "/";
		}
		final String extension = ConstanteUtils.TEMPLATE_EXTENSION;
		InputStream in = null;
		/* On essaye de trouver le template lié à la langue */
		if (codeLangue != null && !codeLangue.equals(codLangueDefault)) {
			in = MethodUtils.class.getResourceAsStream(resourcePath + fileNameDefault + "_" + codeLangue + extension);
		}

		/* Template langue non trouvé, on utilise le template par défaut */
		if (in == null) {
			in = MethodUtils.class.getResourceAsStream(resourcePath + fileNameDefault + extension);
			if (in == null) {
				return null;
			}
		}
		return in;
	}

	/**
	 * @param  email
	 * @return       true si l'adrese est correcte
	 */
	public static boolean isValidEmailAddress(final String email) {
		boolean result = true;
		try {
			final InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (final AddressException ex) {
			result = false;
		}
		return result;
	}

	/**
	 * @param  id
	 * @param  listeId
	 * @return         true si l'id est trouvé dans la liste
	 */
	public static boolean isIdInListId(final Integer id, final List<Integer> listeId) {
		if (listeId == null) {
			return false;
		}
		return listeId.stream().filter(i -> i.equals(id)).findAny().isPresent();
	}

	/**
	 * Fonction Modulo calculant le modulo-->Rouen
	 * @param  a
	 * @param  b
	 * @return   le modulo
	 */
	private static Integer modulo(final Long a, final int b) {

		Long quotient;
		int mod;
		quotient = a / b;
		mod = (int) (a - (quotient * b));
		return (mod);
	}

	/**
	 * Méthode vérifiant la validité d'un numéro INE basé en code 23-->Rouen
	 * (principalement issu d'un rectorat)
	 * @param  bea23
	 * @return       true si l'ine est ok
	 */
	public static boolean checkBEA23(final String bea23) {

		boolean isBea23 = true;
		final String localBea = bea23.toUpperCase();

		if ((localBea.length() < 11) || // Ine a la bonne longueur ?
			(!localBea.matches("^[0-9]{10}[A-Z]{1}$")) // INE RECTORAT est
		// écrit
		// correctement ?
		) {
			isBea23 = false;
		} else {
			// Décomposition du BEA

			final String beaSansCle = localBea.substring(0, 10);
			final char lettreCle = localBea.charAt(10);
			/* String academie = localBea.substring(0, 2); String anneeScol =
			 * localBea.substring(2, 2); String numSeq = localBea.substring(4, 6); */
			// Calcul de la somme des 10 caractères
			final Long extractBea = Long.parseLong(localBea.substring(0, 10));
			final Integer moduloBea = modulo(extractBea, 23);

			// Génération de l'alphabet de 23 caractères sans les lettres I, O,
			// Q
			final String alphabet23 = "ABCDEFGHJKLMNPRSTUVWXYZ";

			// Calcul de la clé réelle attendu
			final char cleCalcule = alphabet23.charAt(moduloBea);

			// Opération de vérification du numéro INE sans clé
			// Vérification de la forme sur les 10 premiers caractères
			// <> succession du même chiffre sur 10
			if (beaSansCle.matches("^[0]{10}$") || beaSansCle.matches("^[1]{10}$")
				|| beaSansCle.matches("^[2]{10}$")
				|| beaSansCle.matches("^[3]{10}$")
				|| beaSansCle.matches("^[4]{10}$")
				|| beaSansCle.matches("^[5]{10}$")
				|| beaSansCle.matches("^[6]{10}$")
				|| beaSansCle.matches("^[7]{10}$")
				|| beaSansCle.matches("^[8]{10}$")
				|| beaSansCle.matches("^[9]{10}$")) {
				isBea23 = false;
			}
			// contrÃ´le supplémentaire sur la clé, elle ne doit pas être égale
			// Ã 'I','O','Q'
			// sinon clé invalide
			if ((lettreCle == 'I') || (lettreCle == 'O') || (lettreCle == 'Q')) {
				isBea23 = false;
			}
			// si clé passé en paramètre <> cleCalcule alors ineBea23 pas bon
			if (lettreCle != cleCalcule) {
				isBea23 = false;
			}
		}
		return (isBea23);
	}

	/**
	 * méthode vérifiant la validité d'un numéro INE saisie en base 36 (ine
	 * universitaire)-->Rouen
	 * @param  nne36
	 * @return       si l'ine est ok
	 */
	public static boolean checkNNE36(final String nne36) {

		boolean isNNE36 = true;

		if (nne36.length() < 11) {
			isNNE36 = false;
		} else {
			final String localNNE36 = nne36.toUpperCase();
			final String extractNNE = localNNE36.substring(0, 10);
			final char cleNNE36 = localNNE36.charAt(10);
			char cleCalc;

			// Opération de vérification du numéro INE sans clé
			// Vérification de la forme sur les 10 premiers caractères
			// <> succession du même chiffre sur 10
			// ou numéro non écrit correctement

			if (extractNNE.matches("^[0]{10}$") || extractNNE.matches("^[1]{10}$")
				|| extractNNE.matches("^[2]{10}$")
				|| extractNNE.matches("^[3]{10}$")
				|| extractNNE.matches("^[4]{10}$")
				|| extractNNE.matches("^[5]{10}$")
				|| extractNNE.matches("^[6]{10}$")
				|| extractNNE.matches("^[7]{10}$")
				|| extractNNE.matches("^[8]{10}$")
				|| extractNNE.matches("^[9]{10}$")
				|| extractNNE.matches("^[A-Z]{10}$")
				|| !localNNE36.matches("^[0-9A-Z]{10}[0-9]|[A-Z]{1}$")) {
				isNNE36 = false;
			}

			// Calcul de la clé
			int sum = 0;
			int valeurInt = 0;
			//
			for (int i = 0; i < 9; i++) {
				valeurInt = Integer.parseInt(String.valueOf(localNNE36.charAt(i)), 36);
				sum += (valeurInt * 6);
			}
			final char dernierChar = extractNNE.charAt(extractNNE.length() - 1);
			sum += Integer.parseInt(String.valueOf(dernierChar), 36);
			cleCalc = Integer.toString(sum).charAt(Integer.toString(sum).length() - 1);
			//
			if (cleCalc != cleNNE36) {
				isNNE36 = false;
			}
		} // fin du else
		return (isNNE36);
	}

	/**
	 * @param  ineAndKey
	 * @return           l'INE à partir de l'INE et sa clé
	 */
	public static String getIne(final String ineAndKey) {
		if (ineAndKey == null || ineAndKey.isEmpty() || ineAndKey.length() != 11) {
			return null;
		}
		if (isINES(ineAndKey)) {
			return ineAndKey.substring(0, 9);
		} else {
			return ineAndKey.substring(0, 10);
		}
	}

	/**
	 * @param  ineAndKey
	 * @return           la clé INE à partir de l'INE et sa clé
	 */
	public static String getCleIne(final String ineAndKey) {
		if (ineAndKey == null || ineAndKey.isEmpty() || ineAndKey.length() != 11) {
			return null;
		}
		if (isINES(ineAndKey)) {
			return ineAndKey.substring(9, 11);
		} else {
			return ineAndKey.substring(10, 11);
		}
	}

	/**
	 * @param  ineAndKey
	 * @return           true si l'INE est un INES
	 */
	public static Boolean isINES(final String ineAndKey) {
		if (ineAndKey == null || ineAndKey.isEmpty() || ineAndKey.length() != 11) {
			return false;
		}
		final Pattern patternINES = Pattern.compile("[0-9]{9}[a-zA-Z]{2}");
		final Matcher matcher = patternINES.matcher(ineAndKey);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * Methode utilitaire pour nettoyer les string (erreur au téléchargement du
	 * dossier)
	 * @param  xmlstring
	 * @return           le string nettoyé
	 */
	public static String encodeForDatabase(final String xmlstring, final String defaultEncoding) {
		if (xmlstring == null) {
			return null;
		}
		/* Si l'encodage est à vide --> on renvoit en UTF8 */
		if (StringUtils.isBlank(defaultEncoding)) {
			return new String(xmlstring.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8).trim();
		} else {
			try {
				return new String(xmlstring.getBytes(defaultEncoding), defaultEncoding).trim();
			} catch (final UnsupportedEncodingException ex) {
				return new String(xmlstring.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8).trim();
			}
		}
	}

	/**
	 * Ferme une ressource closeable
	 * @param ressource
	 *                     la ressource a fermer
	 */
	public static void closeRessource(Closeable ressource) {
		try {
			if (ressource != null) {
				ressource.close();
				ressource = null;
			}
		} catch (final Exception e) {
		}
	}

	/** @return la locale par défaut */
	public static Locale getLocale() {
		try {
			final Locale locale = UI.getCurrent().getLocale();
			if (locale != null) {
				return locale;
			}
		} catch (final Exception e) {
		}
		return new Locale("fr");
	}

	/**
	 * @param  temporal
	 * @param  formatterDate
	 * @param  formatterDateTime
	 * @return                   un localDate formaté
	 */
	public static String formatLocalDate(final Temporal temporal, final DateTimeFormatter formatterDate, final DateTimeFormatter formatterDateTime) {
		if (temporal == null) {
			return "";
		} else if (temporal instanceof LocalDate) {
			return formatterDate.format(temporal);
		} else if (temporal instanceof LocalDateTime) {
			return formatterDateTime.format(temporal);
		}
		return "";
	}

	/**
	 * @param  codCouleur
	 * @param  description
	 * @return             un carré Html coloré
	 */
	public static String getHtmlColoredSquare(String codCouleur, String description, final Integer size, String extraCss) {
		if (codCouleur == null) {
			codCouleur = "#FFFFFF";
		}
		if (extraCss == null) {
			extraCss = "";
		}
		if (description == null) {
			description = "";
		} else {
			description = "title='" + description + "'";
		}
		return "<div " + description + " style='" + extraCss + "display:inline-block;border:1px solid;width:" + size + "px;height:" + size + "px;background:" + codCouleur + ";'></div>";
	}

	/**
	 * @param  object
	 * @param  prop
	 * @return        le type d'un champ d'un objet
	 */
	public static Class<?> getClassProperty(Class<?> object, final String prop) {
		try {
			if (object == null || prop == null) {
				return null;
			}
			if (prop.contains(".")) {
				final StringTokenizer st = new StringTokenizer(prop, ".");
				while (st.hasMoreTokens()) {
					object = object.getDeclaredField(st.nextToken()).getType();
				}
				return object;
			} else {
				return object.getDeclaredField(prop).getType();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

	/**
	 * @param  number
	 * @return        une valeur en long pour les totaux (prend en compte les null)
	 */
	public static Long getLongValue(final Long number) {
		if (number == null) {
			return Long.valueOf(0);
		}
		return number;
	}

	/**
	 * @param  html
	 * @return      remplace les cochoneries de word pour en laisser que l'html standard
	 */
	public static String cleanHtmlValue(final String html, final String defaultEncoding) {
		if (html == null) {
			return null;
		}

		final Safelist whitelist = Safelist.relaxed();
		whitelist.addTags("font", "hr");
		whitelist.addAttributes("font", "color", "face", "size", "style");
		whitelist.addAttributes("table", "border", "cellspacing", "cellpadding", "style");
		whitelist.addAttributes("td", "width", "valign", "style");
		whitelist.addAttributes("p", "style");
		whitelist.addAttributes("span", "style");
		whitelist.addAttributes("div", "style");

		/* Utilisation du parser sinon il transforme tout en &amp; etc.. */
		return Parser.unescapeEntities(Jsoup.clean(encodeForDatabase(html, defaultEncoding), whitelist), true);
	}

	/**
	 * @param  service
	 * @return         l'url de service Apogée
	 */
	public static String getUrlWSApogee(final String service) {
		try {
			String path = ResourceBundle.getBundle(ConstanteUtils.WS_APOGEE_PROP_FILE).getString(service + ConstanteUtils.WS_APOGEE_SERVICE_SUFFIXE);
			if (path != null && !path.endsWith("/")) {
				path = path + "/";
			}
			if (path == null) {
				return "";
			}
			return path;
		} catch (final Exception ex) {
			return "";
		}
	}

	/**
	 * @param  service
	 * @return         la clé de header
	 */
	public static KeyValue getHeaderWSApogee(final String service) {
		try {
			final ResourceBundle bundle = ResourceBundle.getBundle(ConstanteUtils.WS_APOGEE_PROP_FILE);
			final Set<String> keys = bundle.keySet();
			if (keys == null) {
				return new KeyValue();
			}

			final Optional<String> keyHeaderOpt = keys.stream().filter(e -> e.startsWith(ConstanteUtils.WS_APOGEE_HEADER_PREFIXE + service + ".")).findFirst();
			if (keyHeaderOpt.isPresent()) {
				final String keyHeader = keyHeaderOpt.get();
				final String valueHeader = bundle.getString(keyHeader);
				return new KeyValue(keyHeader.replaceAll(ConstanteUtils.WS_APOGEE_HEADER_PREFIXE + service + ".", ""), valueHeader);
			}
		} catch (final Exception ex) {
		}
		return new KeyValue();
	}

	/**
	 * @param  masterPath
	 * @param  codIndOpi
	 * @return            le chemin vers le fichier de stockage des OPIPJ
	 */
	public static String getFolderOpiPjPath(final String masterPath, final String codIndOpi) {
		return masterPath + "/" + codIndOpi + ConstanteUtils.OPI_PJ_SUFFIXE_FOLDER;
	}

	/**
	 * @param  codApoPj
	 * @param  codIndOpi
	 * @return           le nom de fichier OPIPJ sans le nom de l'extension extension
	 */
	public static String getFileOpiPj(final String codApoPj, final String codIndOpi) {
		return ConstanteUtils.OPI_PJ_PREFIXE_FILE + codApoPj + ConstanteUtils.OPI_PJ_SEPARATOR_FILE + codIndOpi + ".";
	}

	/**
	 * @param  value
	 * @return
	 */
	public static Boolean isStringAsBigDecimal(final String value) {
		if (value == null || value.trim().isEmpty()) {
			return true;
		}
		return Pattern.compile(ConstanteUtils.PATTERN_BIG_DECIMAL).matcher(value).matches();
	}

	/**
	 * @param  value
	 * @return                un big decimal
	 * @throws ParseException
	 */
	public static BigDecimal parseStringAsBigDecimal(final String value) {
		if (value == null) {
			return null;
		}
		return new BigDecimal(value.replaceAll(",", "."));
	}

	/**
	 * @param  value
	 * @return                un big decimal
	 * @throws ParseException
	 */
	public static String parseBigDecimalAsString(final BigDecimal value) {
		if (value == null) {
			return null;
		}
		// Create a DecimalFormat that fits your requirements
		final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		final DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setGroupingUsed(false);
		decimalFormat.setDecimalFormatSymbols(symbols);
		decimalFormat.setParseBigDecimal(true);

		// format the BigDecimal
		return decimalFormat.format(value);
	}

	/**
	 * @param  str
	 * @param  size
	 * @return      le string truncate
	 */
	public static String subStr(String str, final int size) {
		if (str == null) {
			return null;
		}
		if (str.length() > size) {
			str = str.substring(0, size) + "...";
		}
		return str;
	}
}
