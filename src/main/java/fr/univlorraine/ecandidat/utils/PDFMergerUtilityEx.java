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

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/**
 *
 * @author vriviere
 */
public class PDFMergerUtilityEx extends PDFMergerUtility {

    private static void removeAllDigitalSignatures(PDDocument doc) {
        COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
        catalogDict.removeItem(COSName.PERMS);
        catalogDict.removeItem(COSName.getPDFName("DSS"));
    }

    private static void removeAllSignatureFields(PDDocument doc) {
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm(null);
        if (acroForm == null)
            return;

        COSArray arrayFields = acroForm.getCOSObject().getCOSArray(COSName.FIELDS);
        if (arrayFields == null)
            return;

        for (var iter = arrayFields.iterator(); iter.hasNext(); ) {
            COSBase baseRefField = iter.next();
            if (!(baseRefField instanceof COSObject))
                continue;
            COSObject objRefField = (COSObject)baseRefField;

            COSBase baseField = objRefField.getObject();
            if (!(baseField instanceof COSDictionary))
                continue;
            COSDictionary objField = (COSDictionary)baseField;

            COSName fieldType = objField.getCOSName(COSName.FT);
            if (fieldType != null && fieldType.equals(COSName.SIG))
                iter.remove();
        }
    }

    private static void removeAllVisibleSignatures(PDDocument doc) {
        for (PDPage page : doc.getPages()) {
            COSBase baseAnnotations = page.getCOSObject().getDictionaryObject(COSName.ANNOTS);
            if (!(baseAnnotations instanceof COSArray))
                continue;
            COSArray arrayAnnotations = (COSArray)baseAnnotations;

            for (var iter = arrayAnnotations.iterator(); iter.hasNext(); ) {
                COSBase baseRefAnnotation = iter.next();
                if (!(baseRefAnnotation instanceof COSObject))
                    continue;

                COSObject objRefAnnotation = (COSObject)baseRefAnnotation;
                COSBase baseAnnotation = objRefAnnotation.getObject();
                if (!(baseAnnotation instanceof COSDictionary))
                    continue;

                COSDictionary dictAnnotation = (COSDictionary)baseAnnotation;
                COSName fieldType = dictAnnotation.getCOSName(COSName.FT);
                if (fieldType != null && fieldType.equals(COSName.SIG))
                    iter.remove();
            }
        }
    }

    private static void removeSigFlags(PDDocument doc) {
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm(null);
        if (acroForm == null)
            return;

        acroForm.getCOSObject().removeItem(COSName.SIG_FLAGS);
    }

    public static void removeAllSignatures(PDDocument doc) {
        doc.setAllSecurityToBeRemoved(true);
        removeAllDigitalSignatures(doc);
        removeAllSignatureFields(doc);
        removeAllVisibleSignatures(doc);
        removeSigFlags(doc);
    }

    @Override
    public void appendDocument(PDDocument destination, PDDocument source) throws IOException {
        removeAllSignatures(source);
        super.appendDocument(destination, source);
    }
}
