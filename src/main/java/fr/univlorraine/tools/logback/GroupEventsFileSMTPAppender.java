/**
 *
 *  ESUP-Portail MONDOSSIERWEB - Copyright (c) 2016 ESUP-Portail consortium
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
 *
 */
package fr.univlorraine.tools.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.util.ContentTypeUtil;
import jakarta.activation.DataHandler;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Appender qui regroupe les événements à mailer, dans une limite de temps et de nombre d'événements.
 * @author Adrien Colson
 */
public class GroupEventsFileSMTPAppender extends GroupEventsSMTPAppender {

	/** Tableau d'adresses vide. */
	public static final InternetAddress[] EMPTY_IA_ARRAY = new InternetAddress[0];

	/** Sujet de mail non défini. */
	public static final String UNDEFINED_SUBJECT = "Undefined subject";

	/** Layout du fichier joint. */
	private Layout<ILoggingEvent> fileLayout;

	/** Nom du fichier joint. */
	private String filename = "messages.csv";

	/** Mimetype du fichier joint. */
	private String fileMimeType = "text/csv";

	/** Charset du fichier joint. */
	private String fileCharset = "UTF-8";

	/** Header du fichier joint. */
	private String fileHeader;

	/**
	 * @return the fileLayout
	 */
	public Layout<ILoggingEvent> getFileLayout() {
		if (fileLayout == null) {
			final PatternLayout defaultLayout = new PatternLayout();
			defaultLayout.setContext(context);
			defaultLayout.setPattern("%nopex%date;%level;%logger;%message%n");
			defaultLayout.start();
			fileLayout = defaultLayout;
		}
		return fileLayout;
	}

	/**
	 * @param fileLayoutSet the fileLayout to set
	 */
	public void setFileLayout(final Layout<ILoggingEvent> fileLayoutSet) {
		fileLayout = fileLayoutSet;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filenameSet the filename to set
	 */
	public void setFilename(final String filenameSet) {
		filename = filenameSet;
	}

	/**
	 * @return the fileMimeType
	 */
	public String getFileMimeType() {
		return fileMimeType;
	}

	/**
	 * @param fileMimeTypeSet the fileMimeType to set
	 */
	public void setFileMimeType(final String fileMimeTypeSet) {
		fileMimeType = fileMimeTypeSet;
	}

	/**
	 * @return the fileCharset
	 */
	public String getFileCharset() {
		return fileCharset;
	}

	/**
	 * @param fileCharsetSet the fileCharset to set
	 */
	public void setFileCharset(final String fileCharsetSet) {
		fileCharset = fileCharsetSet;
	}

	/**
	 * @return the fileHeader
	 */
	public String getFileHeader() {
		return fileHeader;
	}

	/**
	 * @param fileHeaderSet the fileHeader to set
	 */
	public void setFileHeader(final String fileHeaderSet) {
		fileHeader = fileHeaderSet;
	}

	/**
	 * @see ch.qos.logback.core.net.SMTPAppenderBase#sendBuffer(ch.qos.logback.core.helpers.CyclicBuffer, Object)
	 */
	@Override
	protected void sendBuffer(final CyclicBuffer<ILoggingEvent> cb, final ILoggingEvent lastEventObjectFired) {
		try {
			final List<InternetAddress> destinationAddresses = parseAddress2(lastEventObjectFired);
			if (destinationAddresses.isEmpty()) {
				addInfo("Empty destination address. Aborting email transmission");
				return;
			}

			final StringBuffer sbuf = new StringBuffer();

			final String header = layout.getFileHeader();
			if (header instanceof String) {
				sbuf.append(header);
			}
			final String presentationHeader = layout.getPresentationHeader();
			if (presentationHeader instanceof String) {
				sbuf.append(presentationHeader);
			}
			final byte[] fileBytes = fillBufferAndFile(cb, sbuf);
			final String presentationFooter = layout.getPresentationFooter();
			if (presentationFooter instanceof String) {
				sbuf.append(presentationFooter);
			}
			final String footer = layout.getFileFooter();
			if (footer instanceof String) {
				sbuf.append(footer);
			}

			final MimeMessage mimeMsg = new MimeMessage(session);

			if (getFrom() instanceof String) {
				mimeMsg.setFrom(getAddress2(getFrom()));
			} else {
				mimeMsg.setFrom();
			}

			mimeMsg.setSubject(getMailSubject(lastEventObjectFired), getCharsetEncoding());

			final InternetAddress[] toAddressArray = destinationAddresses.toArray(EMPTY_IA_ARRAY);
			mimeMsg.setRecipients(Message.RecipientType.TO, toAddressArray);

			final String contentType = layout.getContentType();

			final MimeBodyPart part = new MimeBodyPart();
			if (ContentTypeUtil.isTextual(contentType)) {
				part.setText(sbuf.toString(), getCharsetEncoding(), ContentTypeUtil.getSubType(contentType));
			} else {
				part.setContent(sbuf.toString(), layout.getContentType());
			}

			final MimeBodyPart csvPart = new MimeBodyPart();
			final ByteArrayDataSource bads = new ByteArrayDataSource(fileBytes, getFileMimeType());
			csvPart.setDataHandler(new DataHandler(bads));
			csvPart.setFileName(getFilename());

			final Multipart mp = new MimeMultipart();
			mp.addBodyPart(part);
			mp.addBodyPart(csvPart);
			mimeMsg.setContent(mp);

			mimeMsg.setSentDate(new Date());
			addInfo("About to send out SMTP message \"" + mimeMsg.getSubject() + "\" to " + Arrays.toString(toAddressArray));
			Transport.send(mimeMsg);
		} catch (final MessagingException me) {
			addError("Error occurred while sending e-mail notification.", me);
		}
	}

	/**
	 * Génère le sujet du mail.
	 * @param lastEventObjectFired dernier événement levé
	 * @return le sujet du mail
	 */
	private String getMailSubject(final ILoggingEvent lastEventObjectFired) {
		if (subjectLayout instanceof Layout) {
			return subjectLayout.doLayout(lastEventObjectFired).split("\\r?\\n")[0];
		}
		return UNDEFINED_SUBJECT;
	}

	/**
	 * Génère le fichier à joindre.
	 * @param cb le CyclicBuffer à parcourir
	 * @param sbuf le buffer à remplir
	 * @return les données du fichier généré
	 */
	protected byte[] fillBufferAndFile(final CyclicBuffer<ILoggingEvent> cb, final StringBuffer sbuf) {
		final StringBuffer sb = new StringBuffer();
		if (getFileHeader() instanceof String) {
			sb.append(getFileHeader());
			sb.append(System.lineSeparator());
		}
		final int len = cb.length();
		for (int i = 0; i < len; i++) {
			final ILoggingEvent event = cb.get();
			sbuf.append(layout.doLayout(event));
			sb.append(getFileLayout().doLayout(event));
		}
		return String.valueOf(sb).getBytes(Charset.forName(getFileCharset()));
	}

	/**
	 * @param addressStr chaine à convertir en adresse
	 * @return adresse convertie en InternetAddress
	 */
	private InternetAddress getAddress2(final String addressStr) {
		try {
			return new InternetAddress(addressStr);
		} catch (final AddressException e) {
			addError("Could not parse address [" + addressStr + "].", e);
			return null;
		}
	}

	/**
	 * @param event liste d'adresses de destination
	 * @return event
	 */
	private List<InternetAddress> parseAddress2(final ILoggingEvent event) {
		final int len = getToList().size();

		final List<InternetAddress> iaList = new ArrayList<InternetAddress>();

		for (int i = 0; i < len; i++) {
			try {
				final PatternLayoutBase<ILoggingEvent> emailPL = getToList().get(i);
				final String emailAdrr = emailPL.doLayout(event);
				if (emailAdrr == null || emailAdrr.length() == 0) {
					continue;
				}
				final InternetAddress[] tmp = InternetAddress.parse(emailAdrr, true);
				iaList.addAll(Arrays.asList(tmp));
			} catch (final AddressException e) {
				addError("Could not parse email address for [" + getToList().get(i) + "] for event [" + event + "]", e);
				return iaList;
			}
		}

		return iaList;
	}

}
