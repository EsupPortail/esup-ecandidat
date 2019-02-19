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
package fr.univlorraine.ecandidat.controllers;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import net.sf.jett.event.SheetEvent;
import net.sf.jett.event.SheetListener;
import net.sf.jett.transform.ExcelTransformer;

/**
 * Gestion de l'entité faq
 *
 * @author Kevin Hergalant
 */
@Component
public class ExportController {

	private Logger logger = LoggerFactory.getLogger(ExportController.class);

	/* Injections */
	@Value("${enableExportAutoSizeColumn:true}")
	private Boolean enableExportAutoSizeColumn;

	@Resource
	private transient ApplicationContext applicationContext;

	/**
	 * @param beans
	 *            les beans à passer au template
	 * @param template
	 *            le template
	 * @param libFile
	 *            le libellé du fichier
	 * @return le fichier généré
	 */
	public OnDemandFile generateXlsxExport(final Map<String, Object> beans, final String template, final String libFile) {

		ByteArrayInOutStream bos = null;
		InputStream fileIn = null;
		Workbook workbook = null;
		try {
			/* Récupération du template */
			fileIn = new BufferedInputStream(new ClassPathResource("template/exports-xlsx/" + template + ".xlsx").getInputStream());
			/* Génération du fichier excel */
			ExcelTransformer transformer = new ExcelTransformer();
			transformer.setSilent(true);
			transformer.setLenient(true);
			transformer.setDebug(false);

			/*
			 * Si enableAutoSizeColumn est à true, on active le resizing de colonnes
			 * Corrige un bug dans certains etablissements
			 */
			if (enableExportAutoSizeColumn) {
				transformer.addSheetListener(new SheetListener() {
					/** @see net.sf.jett.event.SheetListener#beforeSheetProcessed(net.sf.jett.event.SheetEvent) */
					@Override
					public boolean beforeSheetProcessed(final SheetEvent sheetEvent) {
						return true;
					}

					/** @see net.sf.jett.event.SheetListener#sheetProcessed(net.sf.jett.event.SheetEvent) */
					@Override
					public void sheetProcessed(final SheetEvent sheetEvent) {
						/* Ajuste la largeur des colonnes */
						final Sheet sheet = sheetEvent.getSheet();
						for (int i = 1; i < sheet.getRow(0).getLastCellNum(); i++) {
							sheet.autoSizeColumn(i);
						}
					}
				});
			}

			workbook = transformer.transform(fileIn, beans);
			bos = new ByteArrayInOutStream();
			workbook.write(bos);

			return new OnDemandFile(libFile, bos.getInputStream());
		} catch (Exception e) {
			Notification.show(applicationContext.getMessage("export.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			logger.error("erreur a la création du report", e);
			return null;
		} finally {
			MethodUtils.closeRessource(bos);
			MethodUtils.closeRessource(fileIn);
			MethodUtils.closeRessource(workbook);
		}
	}
}
