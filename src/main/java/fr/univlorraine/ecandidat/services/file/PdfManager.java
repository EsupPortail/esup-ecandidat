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
package fr.univlorraine.ecandidat.services.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Locale;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.CreateSignaturePdf;

@Component
public class PdfManager {

	@Resource
	private transient ApplicationContext applicationContext;

	@Value("${pdf.signature.pass:}")
	private transient String pdfSignaturePass;

	@Value("${pdf.signature.keystore.path:}")
	private transient String pdfSignatureKeystorePath;

	@Value("${pdf.crypt.pass:}")
	private transient String pdfCryptPass;

	@Value("${pdf.crypt.interdit:}")
	private transient String pdfCryptInterdit;

	/* variable pour le cryptage */
	private static final String CRYPT_AUTH_ASSEMBLE = "AssembleDocument";
	private static final String CRYPT_AUTH_EXTRACT = "ExtractContent";
	private static final String CRYPT_AUTH_EXTRACT_FOR_ACC = "ExtractForAccessibility";
	private static final String CRYPT_AUTH_FILL_IN = "FillInForm";
	private static final String CRYPT_AUTH_MODIFY = "Modify";
	private static final String CRYPT_AUTH_MODIFY_ANNOT = "ModifyAnnotations";
	private static final String CRYPT_AUTH_PRINT = "Print";
	private static final String CRYPT_AUTH_PRINT_DEGRAD = "PrintDegraded";

	/* Provider signature */
	private static BouncyCastleProvider provider = new BouncyCastleProvider();

	/** @return true si la signature est activée */
	private Boolean isSignPdfEnable() {
		if (pdfSignaturePass == null || pdfSignatureKeystorePath == null || pdfSignaturePass.equals("") || pdfSignatureKeystorePath.equals("")) {
			return false;
		}
		return true;
	}

	/** @return true si la signature est activée */
	private Boolean isCryptPdfEnable() {
		return StringUtils.isNotBlank(pdfCryptPass);
	}

	/**
	 * @return true si le cryptage possede une fonctionnalité interdite
	 */
	private Boolean cryptHasAuth(final String option) {
		if (pdfCryptInterdit == null) {
			return true;
		}
		return !ArrayUtils.contains(pdfCryptInterdit.split(","), option);
	}

	/**
	 * @param  inStream
	 * @param  locale
	 * @return                           le pdf crypté et signé
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 */
	public InputStream cryptAndSignPdf(final ByteArrayInOutStream inStream, final Locale locale) throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
		final ByteArrayInOutStream stream = cryptPdf(inStream);
		return signPdf(stream, locale);
	}

	/**
	 * Crypte un pdf
	 * @param  inStream
	 * @param  locale
	 * @return             un pdf crypté
	 * @throws IOException
	 */
	private ByteArrayInOutStream cryptPdf(final ByteArrayInOutStream inStream) throws IOException {
		/* Si on ne crypte pas on renvoit le stream original */
		if (!isCryptPdfEnable()) {
			return inStream;
		}

		final ByteArrayInOutStream outCrypted = new ByteArrayInOutStream();
		final PDDocument pdd = PDDocument.load(inStream.getInputStream());
		final AccessPermission ap = new AccessPermission();
		ap.setCanAssembleDocument(cryptHasAuth(CRYPT_AUTH_ASSEMBLE));
		ap.setCanExtractContent(cryptHasAuth(CRYPT_AUTH_EXTRACT));
		ap.setCanExtractForAccessibility(cryptHasAuth(CRYPT_AUTH_EXTRACT_FOR_ACC));
		ap.setCanFillInForm(cryptHasAuth(CRYPT_AUTH_FILL_IN));
		ap.setCanModify(cryptHasAuth(CRYPT_AUTH_MODIFY));
		ap.setCanModifyAnnotations(cryptHasAuth(CRYPT_AUTH_MODIFY_ANNOT));
		ap.setCanPrint(cryptHasAuth(CRYPT_AUTH_PRINT));
		ap.setCanPrintDegraded(cryptHasAuth(CRYPT_AUTH_PRINT_DEGRAD));

		final StandardProtectionPolicy stpp = new StandardProtectionPolicy(pdfCryptPass, null, ap);
		stpp.setEncryptionKeyLength(128);
		stpp.setPermissions(ap);
		pdd.protect(stpp);
		pdd.save(outCrypted);
		pdd.close();

		return outCrypted;
	}

	/**
	 * Signe un PDF
	 * Pour créer un keystore, lancer en ligne de commande :
	 * keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias keystoreAlias -validity 365 -v -keyalg RSA -keystore keystore.p12
	 * @param  inStream
	 * @param  locale
	 * @return                           le stream signé
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 */
	private InputStream signPdf(final ByteArrayInOutStream inStream, final Locale locale)
		throws KeyStoreException,
		NoSuchAlgorithmException,
		CertificateException,
		FileNotFoundException,
		IOException,
		UnrecoverableKeyException {
		/* Si on ne crypte pas ni ne signe on renvoit le stream original */
		if (!isSignPdfEnable()) {
			return inStream.getInputStream();
		}
		final KeyStore keystore = KeyStore.getInstance("PKCS12", provider);
		final char[] pin = pdfSignaturePass.toCharArray();
		keystore.load(new FileInputStream(pdfSignatureKeystorePath), pin);
		final CreateSignaturePdf signing = new CreateSignaturePdf(keystore, pin.clone());
		return signing.signPdf(inStream,
			applicationContext.getMessage("pdf.signature.nom", null, locale),
			applicationContext.getMessage("pdf.signature.lieu", null, locale),
			applicationContext.getMessage("pdf.signature.raison", null, locale),
			applicationContext.getMessage("pdf.signature.contact.info", null, locale));
	}

}
