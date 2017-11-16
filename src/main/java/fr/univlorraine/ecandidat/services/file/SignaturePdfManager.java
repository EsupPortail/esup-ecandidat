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

import javax.annotation.Resource;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.CreateSignaturePdf;

@Component
public class SignaturePdfManager {
	
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Value("${pdf.signature.pass:}")
	private transient String pdfSignaturePass;
	
	@Value("${pdf.signature.keystore.path:}")
	private transient String pdfSignatureKeystorePath;
	
	private static BouncyCastleProvider provider = new BouncyCastleProvider();
	
	/**
	 * @return true si la signature est activée
	 */
	private Boolean isSignPdfEnable(){
		if (pdfSignaturePass == null || pdfSignatureKeystorePath == null || pdfSignaturePass.equals("") || pdfSignatureKeystorePath.equals("")){
			return false;
		}
		return true;
	}
	
	/** Signe un PDF
	 * Pour créer un keystore, lancer en ligne de commande :
	 * keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias keystoreAlias -validity 365 -v -keyalg RSA -keystore keystore.p12
	 * @param inStream
	 * @return le stream signé
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 */
	public InputStream signPdf(ByteArrayInOutStream inStream) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException{
		if (!isSignPdfEnable()){
			return inStream.getInputStream();
		}
		KeyStore keystore = KeyStore.getInstance("PKCS12", provider);
        char[] pin = pdfSignaturePass.toCharArray();
        keystore.load(new FileInputStream(pdfSignatureKeystorePath), pin);
		CreateSignaturePdf signing = new CreateSignaturePdf(keystore, pin.clone());		
        return signing.signPdf(inStream,applicationContext.getMessage("pdf.signature.nom", null, UI.getCurrent().getLocale()),
        		applicationContext.getMessage("pdf.signature.lieu", null, UI.getCurrent().getLocale()),
        		applicationContext.getMessage("pdf.signature.raison", null, UI.getCurrent().getLocale()),
        		applicationContext.getMessage("pdf.signature.contact.info", null, UI.getCurrent().getLocale()));
	}

}
