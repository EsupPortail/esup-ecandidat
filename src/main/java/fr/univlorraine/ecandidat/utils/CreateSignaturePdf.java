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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import org.apache.pdfbox.examples.signature.CreateSignatureBase;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

public class CreateSignaturePdf extends CreateSignatureBase {

	/** Créé la signature
	 * @param keystore
	 * @param pin
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws CertificateException
	 */
	public CreateSignaturePdf(KeyStore keystore, char[] pin) throws KeyStoreException, UnrecoverableKeyException,
			NoSuchAlgorithmException, IOException, CertificateException {
		super(keystore, pin);
	}

	/** Signe un document PDF
	 * @param inStream
	 * @param name
	 * @param location
	 * @param reason
	 * @param contactInfo
	 * @return l'inputStream
	 * @throws IOException
	 */
	public InputStream signPdf(ByteArrayInOutStream inStream, String name, String location, String reason, String contactInfo) throws IOException {
		if (inStream == null) {
			throw new FileNotFoundException("Document for signing does not exist");
		}
		// sign
		PDDocument doc = PDDocument.load(inStream.getInputStream());		

		// create signature dictionary
		PDSignature signature = new PDSignature();
		signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
		signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
		signature.setName(name);
		signature.setLocation(location);
		signature.setContactInfo(contactInfo);
		signature.setReason(reason);

		// the signing date, needed for valid signature
		signature.setSignDate(Calendar.getInstance());

		// register signature dictionary and sign interface
		doc.addSignature(signature, this);

		// write incremental (only for signing purpose)
		ByteArrayInOutStream outStream = new ByteArrayInOutStream();
		doc.saveIncremental(outStream);
		doc.close();
		inStream.close();
		return outStream.getInputStream();
	}
}
