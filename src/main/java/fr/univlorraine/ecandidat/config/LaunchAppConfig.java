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
package fr.univlorraine.ecandidat.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
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
import fr.univlorraine.apowsutils.WSUtils;
import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Configuration du lancement de l'appli
 * @author Kevin Hergalant
 */
@Component
public class LaunchAppConfig implements ApplicationListener<ContextRefreshedEvent> {

	private final Logger logger = LoggerFactory.getLogger(LaunchAppConfig.class);

	@Resource
	private transient NomenclatureController nomenclatureController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient ConfigController configController;

	@Value("${external.ressource:}")
	private transient String externalRessource;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

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
		preprocessVersions();
		preprocessAnnotations();
		preprocessConfigUrlServicesLocation();
	}

	/** Affiche les données de config de LimeSurvey */
	private void preprocessLimesurvey() {
		if (StringUtils.isNotBlank(urlLS)) {
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

	/**
	 * Chargement des versions
	 */
	private void preprocessVersions() {
		/* Chargement des versions */
		nomenclatureController.loadMapVersion();
		/* Affichage des versions */
		nomenclatureController.printVersions();
	}

	/** Charge les templates */
	public void preprocessTemplate() {
		try {
			logger.info("Generation du report");
			// InputStream in = getClass().getResourceAsStream("/template/"+ConstanteUtils.TEMPLATE_DOSSIER+ConstanteUtils.TEMPLATE_EXTENSION);
			final InputStream in = MethodUtils.getInputStream(configController.getXDocReportTemplate(ConstanteUtils.TEMPLATE_DOSSIER, null, null));
			final IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);
			final IContext context = report.createContext();
			report.convert(context, options, out);
			out.close();
			in.close();
		} catch (IOException | XDocReportException e) {
			logger.error("Erreur a la generation du report", e);
		}
	}

	/** Modifie la valeur de certaines annotations */
	public void preprocessAnnotations() {
		try {
			final int size = siScolService.getSizeFieldAdresse();
			if (size != ConstanteUtils.SIZE_FIELD_ADRESSE_DEFAULT) {
				logger.info("Modification des annotations adresse, size = " + size);
				changeAnnotationAdresse(Adresse.FIELD_ADR1, size);
				changeAnnotationAdresse(Adresse.FIELD_ADR2, size);
				changeAnnotationAdresse(Adresse.FIELD_ADR3, size);
				changeAnnotationAdresse(Adresse.FIELD_LIB_COM_ETR, size);
			}
		} catch (final Exception e) {
			logger.warn("Erreur a la modification des annotations", e);
		}
	}

	/**
	 * Modifie la taille des champs d'adresse
	 * @param  fieldName
	 * @throws Exception
	 */
	private void changeAnnotationAdresse(final String fieldName, final int size) throws Exception {
		try {
			final Field field = Adresse.class.getDeclaredField(fieldName);

			final Column fieldAnnotationColumn = field.getAnnotation(Column.class);
			MethodUtils.changeAnnotationValue(fieldAnnotationColumn, "length", size);

			final Size fieldAnnotationSize = field.getAnnotation(Size.class);
			MethodUtils.changeAnnotationValue(fieldAnnotationSize, "max", size);
		} catch (final Exception e) {
			throw e;
		}
	}

	/**
	 * Charge éventuellement un fichier de config externe pour les fichiers SiScol --> util dans WSutil
	 */
	private void preprocessConfigUrlServicesLocation() {
		try {
			if (StringUtils.isNotBlank(externalRessource)) {
				final String path = externalRessource + ConstanteUtils.EXTERNAL_RESSOURCE_SISCOL_FOLDER + File.separator;
				final File fileExternal = new File(path);
				if (fileExternal.exists() && fileExternal.isDirectory()) {
					System.setProperty(WSUtils.PROPERTY_FILE_PATH, path);
				}
			}
		} catch (final Exception e) {

		}
	}
}
