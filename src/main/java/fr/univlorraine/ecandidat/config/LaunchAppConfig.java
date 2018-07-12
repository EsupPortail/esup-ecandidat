/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Configuration du lancement de l'appli
 *
 * @author Kevin Hergalant */
@Component
public class LaunchAppConfig implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(LaunchAppConfig.class);

	@Resource
	private transient NomenclatureController nomenclatureController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient BatchController batchController;

	@Value("${limesurvey.path:}")
	private transient String urlLS;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		preprocessLimesurvey();
		preprocessCleanLock();
		preprocessCleanBatch();
		preprocessNomenclature();
		preprocessTemplate();
		preprocessCache();
	}

	/** Affiche les données de config de LimeSurvey */
	private void preprocessLimesurvey() {
		if (urlLS != null) {
			logger.info("Configuration Limesurvey : " + urlLS);
		}
	}

	/** Met les données en cache */
	private void preprocessCache() {
		logger.info("Mise à jour du cache de données");
		loadBalancingController.reloadAllData();
	}

	/** Au démarrage de l'appli, on supprime tout les locks */
	private void preprocessCleanLock() {
		logger.info("Nettoyage des locks");
		lockCandidatController.cleanAllLockCandidatForInstance();
	}

	/** Charge les nomenclatures si pas a jour */
	public void preprocessNomenclature() {
		if (!loadBalancingController.isLoadBalancingCandidatMode() && nomenclatureController.isNomenclatureToReload()) {
			logger.info("Mise à jour nomenclature");
			nomenclatureController.cleanNomenclature();
			nomenclatureController.majNomenclature();
		} else {
			logger.info("Nomenclature a jour");
		}
	}

	/** Charge les nomenclatures si pas a jour */
	public void preprocessCleanBatch() {
		if (!loadBalancingController.isLoadBalancingCandidatMode()) {
			logger.info("Nettoyage des batchs");
			batchController.nettoyageBatch(0);
		}
	}

	/** Charge les templates */
	public void preprocessTemplate() {
		try {
			logger.info("Generation du report");
			// InputStream in = getClass().getResourceAsStream("/template/"+ConstanteUtils.TEMPLATE_DOSSIER+ConstanteUtils.TEMPLATE_EXTENSION);
			InputStream in = MethodUtils.getXDocReportTemplate(ConstanteUtils.TEMPLATE_DOSSIER, null, null);
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
			IContext context = report.createContext();
			report.convert(context, options, out);
			out.close();
			in.close();
		} catch (IOException | XDocReportException e) {
			logger.error("Erreur a la generation du report", e);
		}
	}
}
