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
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
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

import org.apache.axis.utils.XMLChar;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.services.siscol.SiScolRestUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;

/**
 * Class de methode utilitaires
 *
 * @author Kevin Hergalant
 */
public class MethodUtils {

	/**
	 * Renvoi pour une classe donnée si le champs est nullable ou non
	 *
	 * @param classObject
	 * @param property
	 * @return true si l'objet n'est pas null
	 */
	public static Boolean getIsNotNull(final Class<?> classObject, final String property) {
		try {
			NotNull notNull = classObject.getDeclaredField(property).getAnnotation(NotNull.class);
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
	 *
	 * @param temoin
	 * @return le boolean associe
	 */
	public static Boolean getBooleanFromTemoin(final String temoin) {
		if (temoin == null || temoin.equals(ConstanteUtils.TYP_BOOLEAN_NO)) {
			return false;
		}
		return true;
	}

	/**
	 * Renvoi temoin en string (O ou N) pour un boolean
	 *
	 * @param bool
	 * @return le String associe
	 */
	public static String getTemoinFromBoolean(final Boolean bool) {
		if (!bool) {
			return ConstanteUtils.TYP_BOOLEAN_NO;
		}
		return ConstanteUtils.TYP_BOOLEAN_YES;
	}

	/**
	 * Ajoute du texte à la suite et place une virgule entre
	 *
	 * @param text
	 * @param more
	 * @return le txt complété
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
	 *
	 * @param time
	 * @return le label de minute ou d'heure complété
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
	 * @param millis
	 * @return un label de millisecondes
	 */
	public static Integer getStringMillisecondeToInt(final String millis) {
		if (millis != null) {
			try {
				return Integer.valueOf(millis);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	/**
	 * @param millis
	 * @return un label de millisecondes
	 */
	public static String getIntMillisecondeToString(final Integer millis) {
		if (millis == null || millis.equals(0)) {
			return "00 sec";
		} else if (millis < 60000) {
			return String.format("%02d sec", TimeUnit.MILLISECONDS.toSeconds(millis));
		} else if (millis % 60000 == 0) {
			return String.format("%02d min", TimeUnit.MILLISECONDS.toMinutes(millis));
		} else {
			return String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(millis),
					TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		}
	}

	/**
	 * Nettoie un nom de fichier pour le stockage fs
	 *
	 * @param fileName
	 * @return le nom de fichier pour le stockage fs
	 */
	public static String cleanFileName(final String fileName) {
		if (fileName == null || fileName.equals("")) {
			return "_";
		}
		return removeAccents(fileName).replaceAll("[^A-Za-z0-9\\.\\-\\_]", "");
	}

	/**
	 * Remplace les accents
	 *
	 * @param text
	 * @return le text sans accents
	 */
	public static String removeAccents(final String text) {
		return text == null ? "" : Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	/**
	 * Valide un bean
	 *
	 * @param bean
	 * @throws CustomException
	 */
	public static <T> Boolean validateBean(final T bean, final Logger logger) {
		logger.debug(" ***VALIDATION*** : " + bean);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(bean);
		if (constraintViolations != null && constraintViolations.size() > 0) {
			for (ConstraintViolation<?> violation : constraintViolations) {
				logger.debug(" *** " + violation.getPropertyPath().toString() + " : " + violation.getMessage());
			}
			return false;
		}
		return true;
	}

	/**
	 * Formate un texte
	 *
	 * @param txt
	 * @return un txt formaté
	 */
	public static String formatToExport(final String txt) {
		if (txt == null) {
			return "";
		}
		return txt;
	}

	/**
	 * Formate un texte et supprime les balise HTML
	 *
	 * @param txt
	 * @return un txt formaté
	 */
	public static String formatToExportHtml(final String txt) {
		return formatToExport(txt).replaceAll("\\<.*?>", "");
	}

	/**
	 * Verifie que le fichier est un pdf
	 *
	 * @param fileName
	 * @return true si le fichier est un pdf
	 */
	public static Boolean isPdfFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_PDF).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * Verifie que le fichier est une image
	 *
	 * @param fileName
	 * @return true si le fichier est une image
	 */
	public static Boolean isImgFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_IMG).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * Verifie que le fichier est un jpg
	 *
	 * @param fileName
	 * @return true si le fichier est un jpg
	 */
	public static Boolean isJpgFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_JPG).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * Verifie que le fichier est un png
	 *
	 * @param fileName
	 * @return true si le fichier est un png
	 */
	public static Boolean isPngFileName(final String fileName) {
		return Arrays.asList(ConstanteUtils.EXTENSION_PNG).contains(getExtension(fileName.toLowerCase()));
	}

	/**
	 * renvoie l'extension
	 *
	 * @param fileName
	 * @return l'extension du fichier
	 */
	public static String getExtension(final String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i >= 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	/**
	 * @param liste
	 * @param code
	 * @return le libellé de presentation
	 */
	public static String getLibByPresentationCode(final List<SimpleTablePresentation> liste, final String code) {
		Optional<SimpleTablePresentation> opt = liste.stream().filter(e -> e.getCode().equals(code)).findFirst();
		if (opt.isPresent() && opt.get().getValue() != null) {
			return opt.get().getValue().toString();
		}
		return "";
	}

	/**
	 * Verifie qu'une date est inclue dans un intervalle
	 *
	 * @param dateToCompare
	 * @return true si la date est incluse dans un interval
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
	 *
	 * @param txt
	 * @return l'entier converti
	 */
	public static Integer convertStringToIntger(final String txt) {
		if (txt == null) {
			return null;
		}
		try {
			return Integer.valueOf(txt);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converti une date en LocalDate
	 *
	 * @param date
	 * @return la localDate convertie
	 */
	public static LocalDate convertDateToLocalDate(final Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Converti une LocalDate en date
	 *
	 * @param date
	 * @return la date convertie
	 */
	public static Date convertLocalDateToDate(final LocalDate date) {
		if (date == null) {
			return null;
		}
		Instant instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	/**
	 * Converti une LocalDateTime en LocalDate
	 *
	 * @param localDateTime
	 * @return la date convertie
	 */
	public static LocalDate convertLocalDateTimeToDate(final LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	/**
	 * Replace la derniere occurence
	 *
	 * @param string
	 * @param from
	 * @param to
	 * @return le string nettoye
	 */
	public static String replaceLast(final String string, final String from, final String to) {
		int lastIndex = string.lastIndexOf(from);
		if (lastIndex < 0) {
			return string;
		}
		String tail = string.substring(lastIndex).replaceFirst(from, to);
		return string.substring(0, lastIndex) + tail;
	}

	/**
	 * @param fileName
	 * @param isOnlyImg
	 * @return true si l'extension est jpg ou pdf
	 */
	public static Boolean checkExtension(final String fileName, final Boolean isOnlyImg) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
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
	 * @param nomFichier
	 * @return le type MIME d'un fichier
	 */
	public static String getMimeType(final String nomFichier) {
		if (isPdfFileName(nomFichier)) {
			return ConstanteUtils.TYPE_MIME_FILE_PDF;
		} else if (isJpgFileName(nomFichier)) {
			return ConstanteUtils.TYPE_MIME_FILE_JPG;
		} else if (isPngFileName(nomFichier)) {
			return ConstanteUtils.TYPE_MIME_FILE_PNG;
		} else if (Arrays.asList(new String[] {"docx"}).contains(getExtension(nomFichier.toLowerCase()))) {
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		}
		return null;
	}

	/**
	 * @param path
	 * @return la path agrémenté d'un / a la fin
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
	 * @param appPath
	 * @param add
	 * @return l'url formatée pour switch user
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
	 * @param date
	 * @param formatterDate
	 * @return la date formatee
	 */
	public static String formatDate(final LocalDateTime date, final DateTimeFormatter formatterDate) {
		if (date == null) {
			return "";
		} else {
			return date.format(formatterDate);
		}
	}

	/**
	 * @param date
	 * @param formatterDate
	 * @return la date formatee
	 */
	public static String formatDate(final LocalDate date, final DateTimeFormatter formatterDate) {
		if (date == null) {
			return "";
		} else {
			return date.format(formatterDate);
		}
	}

	/** @return la version des WS */
	public static String getClassVersion(final Class<?> theClass) {
		try {

			// Find the path of the compiled class
			String classPath = theClass.getResource(theClass.getSimpleName() + ".class").toString();

			// Find the path of the lib which includes the class
			String libPath = classPath.substring(0, classPath.lastIndexOf("!"));

			if (libPath != null) {
				Integer lastIndex = libPath.lastIndexOf("/");
				if (lastIndex != -1) {
					libPath = libPath.substring(lastIndex + 1, libPath.length());
					libPath = libPath.replaceAll(".jar", "");
					return libPath;
				}
			}
			/*
			 * if (libPath!=null){ Integer lastIndex = libPath.lastIndexOf("/"); if
			 * (lastIndex!=-1){ libPath = libPath.substring(0,lastIndex); lastIndex =
			 * libPath.lastIndexOf("/"); if (lastIndex!=-1){ libPath =
			 * libPath.substring(lastIndex+1,libPath.length()); } return libPath; } }
			 */
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * Vérifie si une throwable appartient à une classe
	 *
	 * @param cause
	 * @param causeSearch
	 * @return true si la cause existe
	 */
	public static Boolean checkCause(final Throwable cause, final String causeSearch) {
		try {
			if (cause.getClass().getName().contains(causeSearch)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * @param cause
	 * @param causeSearch
	 * @param lineNumber
	 * @return vérifie si la premiere cause de la stack appartient a une classe
	 */
	public static Boolean checkCauseByStackTrace(final Throwable cause, final String causeSearch, final Integer lineNumber) {
		try {
			if (cause.getStackTrace()[lineNumber].getClassName().contains(causeSearch)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Vérifie si une throwable possède un message ciblé
	 *
	 * @param cause
	 * @param messageSearch
	 * @return true si le message existe
	 */
	public static Boolean checkCauseByMessage(final Throwable cause, final String messageSearch) {
		try {
			if (cause.getMessage().contains(messageSearch)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Pour vérifier le cas des "NullPointerException" sans stack
	 *
	 * @param cause
	 * @return true si il n'y a pas de stackTrace
	 */
	public static Boolean checkCauseEmpty(final Throwable cause) {
		try {
			if (cause.getStackTrace() == null || cause.getStackTrace().length == 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * @param e
	 *            l'exception
	 * @param clazz
	 *            la class à trouver
	 * @param messageToFind
	 *            le message à trouver
	 * @return true si l'exception correspond et que le message a été trouvé
	 */
	public static Boolean checkExceptionAndMessage(final Exception e, final Class<?> clazz, final String messageToFind) {
		try {
			if (e != null && clazz.isInstance(e) && e.getMessage() != null && e.getMessage().contains(messageToFind)) {
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	/**
	 * @param value
	 * @return le string modifié en upperCase et sans espace à la fin
	 */
	public static String cleanForApogee(String value) {
		if (value == null) {
			return null;
		}
		value = value.replaceAll("\\s+$", "");
		return value.toUpperCase();
	}

	/**
	 * @param value
	 * @return supprime les accents
	 */
	public static String cleanForApogeeWS(final String value) {
		if (value == null) {
			return null;
		}
		return Normalizer.normalize(cleanForApogee(value), Normalizer.Form.NFD).replaceAll("[̀-ͯ]", "");
	}

	/**
	 * @param typGestionCandidature
	 * @return true si le mode de gestion de candidature est centre de candidature
	 */
	public static boolean isGestionCandidatureCtrCand(final String typGestionCandidature) {
		if (typGestionCandidature == null) {
			return false;
		}
		return typGestionCandidature.equals(ConstanteUtils.TYP_GESTION_CANDIDATURE_CTR_CAND);
	}

	/**
	 * @param typGestionCandidature
	 * @return true si le mode de gestion de candidature est candidat
	 */
	public static boolean isGestionCandidatureCandidat(final String typGestionCandidature) {
		if (typGestionCandidature == null) {
			return false;
		}
		return typGestionCandidature.equals(ConstanteUtils.TYP_GESTION_CANDIDATURE_CANDIDAT);
	}

	/**
	 * @param typGestionCandidature
	 * @return true si le mode de gestion de candidature est commission
	 */
	public static boolean isGestionCandidatureCommission(final String typGestionCandidature) {
		if (typGestionCandidature == null) {
			return false;
		}
		return typGestionCandidature.equals(ConstanteUtils.TYP_GESTION_CANDIDATURE_COMMISSION);
	}

	/**
	 * @param fileNameDefault
	 * @param codeLangue
	 * @param codLangueDefault
	 * @return le template XDocReport
	 */
	public static InputStream getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault) {
		return getXDocReportTemplate(fileNameDefault, codeLangue, codLangueDefault, null);
	}

	/**
	 * @param fileNameDefault
	 * @param codeLangue
	 * @param codLangueDefault
	 * @param subPath
	 * @param suffixe
	 * @return le template XDocReport
	 */
	public static InputStream getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault, final String subPath, final String suffixe) {

		/* On cherche le fichier du suffixe "séparé par _ " */
		InputStream in = getXDocReportTemplate(fileNameDefault + "_" + suffixe, codeLangue, codLangueDefault, subPath);

		/* Si il n'existe pas on renvoit le fichier par défaut */
		if (in == null) {
			in = getXDocReportTemplate(fileNameDefault, codeLangue, codLangueDefault, subPath);
		}
		return in;
	}

	/**
	 * @param fileNameDefault
	 * @param codeLangue
	 * @param codLangueDefault
	 * @param subPath
	 * @return le template XDocReport
	 */
	public static InputStream getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault, final String subPath) {
		String resourcePath = "/" + ConstanteUtils.TEMPLATE_PATH + "/";
		if (subPath != null) {
			resourcePath = resourcePath + subPath + "/";
		}
		String extension = ConstanteUtils.TEMPLATE_EXTENSION;
		InputStream in = null;
		if (codeLangue != null && !codeLangue.equals(codLangueDefault)) {
			in = MethodUtils.class.getResourceAsStream(resourcePath + fileNameDefault + "_" + codeLangue + extension);
		}

		if (in == null) {
			in = MethodUtils.class.getResourceAsStream(resourcePath + fileNameDefault + extension);
			if (in == null) {
				return null;
			}
		}
		return in;
	}

	/**
	 * @param email
	 * @return true si l'adrese est correcte
	 */
	public static boolean isValidEmailAddress(final String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	/**
	 * @param id
	 * @param listeId
	 * @return true si l'id est trouvé dans la liste
	 */
	public static boolean isIdInListId(final Integer id, final List<Integer> listeId) {
		if (listeId == null) {
			return false;
		}
		return listeId.stream().filter(i -> i.equals(id)).findAny().isPresent();
	}

	/**
	 * Fonction Modulo calculant le modulo-->Rouen
	 *
	 * @param a
	 * @param b
	 * @return le modulo
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
	 *
	 * @param bea23
	 * @return true si l'ine est ok
	 */
	public static boolean checkBEA23(final String bea23) {

		boolean isBea23 = true;
		String localBea = bea23.toUpperCase();

		if ((localBea.length() < 11) || // Ine a la bonne longueur ?
				(!localBea.matches("^[0-9]{10}[A-Z]{1}$")) // INE RECTORAT est
															// écrit
															// correctement ?
		) {
			isBea23 = false;
		} else {
			// Décomposition du BEA

			String beaSansCle = localBea.substring(0, 10);
			char lettreCle = localBea.charAt(10);
			/*
			 * String academie = localBea.substring(0, 2); String anneeScol =
			 * localBea.substring(2, 2); String numSeq = localBea.substring(4, 6);
			 */
			// Calcul de la somme des 10 caractères
			Long extractBea = Long.parseLong(localBea.substring(0, 10));
			Integer moduloBea = modulo(extractBea, 23);

			// Génération de l'alphabet de 23 caractères sans les lettres I, O,
			// Q
			String alphabet23 = "ABCDEFGHJKLMNPRSTUVWXYZ";

			// Calcul de la clé réelle attendu
			char cleCalcule = alphabet23.charAt(moduloBea);

			// Opération de vérification du numéro INE sans clé
			// Vérification de la forme sur les 10 premiers caractères
			// <> succession du même chiffre sur 10
			if (beaSansCle.matches("^[0]{10}$") || beaSansCle.matches("^[1]{10}$") || beaSansCle.matches("^[2]{10}$") || beaSansCle.matches("^[3]{10}$") || beaSansCle.matches("^[4]{10}$")
					|| beaSansCle.matches("^[5]{10}$") || beaSansCle.matches("^[6]{10}$") || beaSansCle.matches("^[7]{10}$") || beaSansCle.matches("^[8]{10}$") || beaSansCle.matches("^[9]{10}$")) {
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
	 *
	 * @param nne36
	 * @return si l'ine est ok
	 */
	public static boolean checkNNE36(final String nne36) {

		boolean isNNE36 = true;

		if (nne36.length() < 11) {
			isNNE36 = false;
		} else {
			String localNNE36 = nne36.toUpperCase();
			String extractNNE = localNNE36.substring(0, 10);
			char cleNNE36 = localNNE36.charAt(10);
			char cleCalc;

			// Opération de vérification du numéro INE sans clé
			// Vérification de la forme sur les 10 premiers caractères
			// <> succession du même chiffre sur 10
			// ou numéro non écrit correctement

			if (extractNNE.matches("^[0]{10}$") || extractNNE.matches("^[1]{10}$") || extractNNE.matches("^[2]{10}$") || extractNNE.matches("^[3]{10}$") || extractNNE.matches("^[4]{10}$")
					|| extractNNE.matches("^[5]{10}$") || extractNNE.matches("^[6]{10}$") || extractNNE.matches("^[7]{10}$") || extractNNE.matches("^[8]{10}$") || extractNNE.matches("^[9]{10}$")
					|| extractNNE.matches("^[A-Z]{10}$") || !localNNE36.matches("^[0-9A-Z]{10}[0-9]|[A-Z]{1}$")) {
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
			char dernierChar = extractNNE.charAt(extractNNE.length() - 1);
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
	 * @param ineAndKey
	 * @return l'INE à partir de l'INE et sa clé
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
	 * @param ineAndKey
	 * @return la clé INE à partir de l'INE et sa clé
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
	 * @param ineAndKey
	 * @return true si l'INE est un INES
	 */
	public static Boolean isINES(final String ineAndKey) {
		if (ineAndKey == null || ineAndKey.isEmpty() || ineAndKey.length() != 11) {
			return false;
		}
		Pattern patternINES = Pattern.compile("[0-9]{9}[a-zA-Z]{2}");
		Matcher matcher = patternINES.matcher(ineAndKey);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	/**
	 * Methode utilitaire pour nettoyer les string (erreur au téléchargement du
	 * dossier)
	 *
	 * @param xmlstring
	 * @return le string nettoyé
	 */
	public static String stripNonValidXMLCharacters(String xmlstring) {
		if (xmlstring == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < xmlstring.length(); i++) {
			char c = xmlstring.charAt(i);
			if (XMLChar.isValid(c)) {
				sb.append(c);
			}
		}
		xmlstring = sb.toString();

		// peut être pas utile mais je le laisse qd meme..
		if (xmlstring.contains("\0")) {
			xmlstring = xmlstring.replaceAll("\0", "");
		}
		return xmlstring;
	}

	/**
	 * Ferme une ressource closeable
	 *
	 * @param ressource
	 *            la ressource a fermer
	 */
	public static void closeRessource(Closeable ressource) {
		try {
			if (ressource != null) {
				ressource.close();
				ressource = null;
			}
		} catch (Exception e) {
		}
	}

	/** @return la locale par défaut */
	public static Locale getLocale() {
		try {
			Locale locale = UI.getCurrent().getLocale();
			if (locale != null) {
				return locale;
			}
		} catch (Exception e) {
		}
		return new Locale("fr");
	}

	/**
	 * @param temporal
	 * @param formatterDate
	 * @param formatterDateTime
	 * @return un localDate formaté
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
	 * @param codCouleur
	 * @param description
	 * @return un carré Html coloré
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
	 * @param object
	 * @param prop
	 * @return le type d'un champ d'un objet
	 */
	public static Class<?> getClassProperty(Class<?> object, final String prop) {
		try {
			if (object == null || prop == null) {
				return null;
			}
			if (prop.contains(".")) {
				StringTokenizer st = new StringTokenizer(prop, ".");
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
	 * @param number
	 * @return une valeur en long pour les totaux (prend en compte les null)
	 */
	public static Long getLongValue(final Long number) {
		if (number == null) {
			return new Long(0);
		}
		return number;
	}

	/**
	 * @param html
	 * @return remplace les cochoneries de word pour en laisser que l'html standard
	 */
	public static String cleanHtmlValue(final String html) {
		if (html == null) {
			return null;
		}
		return Jsoup.clean(html, Whitelist.relaxed());
	}

	/**
	 * @param service
	 * @return l'url de service Apogée
	 */
	public static String getUrlWSApogee(final String service) {
		String filename = ConstanteUtils.WS_APOGEE_PROP_FILE;
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = SiScolRestUtils.class.getResourceAsStream(filename);
			if (input == null) {
				return null;
			}
			prop.load(input);
			String path = prop.getProperty(service);
			if (path != null && !path.endsWith("/")) {
				path = path + "/";
			}
			return path;
		} catch (IOException ex) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					return null;
				}
			}
		}
	}

	/**
	 * @param masterPath
	 * @param codIndOpi
	 * @return le chemin vers le fichier de stockage des OPIPJ
	 */
	public static String getFolderOpiPjPath(final String masterPath, final String codIndOpi) {
		return masterPath + "/" + codIndOpi + ConstanteUtils.OPI_PJ_SUFFIXE_FOLDER;
	}

	/**
	 * @param codApoPj
	 * @param codIndOpi
	 * @return le nom de fichier OPIPJ sans le nom de l'extension extension
	 */
	public static String getFileOpiPj(final String codApoPj, final String codIndOpi) {
		return ConstanteUtils.OPI_PJ_PREFIXE_FILE + codApoPj + ConstanteUtils.OPI_PJ_SEPARATOR_FILE + codIndOpi + ".";
	}
}
