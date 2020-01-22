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

import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
public class TestWsController {
	private final Logger logger = LoggerFactory.getLogger(TestWsController.class);

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	public void testWs() {
		final ResourceBundle bundle = ResourceBundle.getBundle("test-ws");

		final String codEtu = bundle.getString("ind.codEtu");
		try {

			/* Données individu */
			final WSIndividu ind = siScolService.getIndividu(codEtu, null, null);
			checkString(bundle, String.valueOf(ind.getCodEtu()), "ind.codEtu");
			checkString(bundle, String.valueOf(ind.getCodInd()), "ind.codInd");
			checkString(bundle, ind.getCodNneInd(), "ind.codNneInd");
			checkString(bundle, ind.getCodCleNneInd(), "ind.codCleNneInd");
			checkString(bundle, ind.getCodPayNai(), "ind.codPayNai");
			checkString(bundle, ind.getCodDepNai(), "ind.codDepNai");
			checkString(bundle, ind.getCodPayNat(), "ind.codPayNat");
			checkString(bundle, ind.getLibNomPatInd(), "ind.libNomPatInd");
			checkString(bundle, ind.getLibNomUsuInd(), "ind.libNomUsuInd");
			checkString(bundle, ind.getLibPr1Ind(), "ind.libPr1Ind");
			checkString(bundle, ind.getLibPr2Ind(), "ind.libPr2Ind");
			checkString(bundle, ind.getLibVilNaiEtu(), "ind.libVilNaiEtu");

			/* Données bac */

			/* Adresse :
			 * WSAdresse(codBdi=54710, codCom=54197, codPay=100, libAd1=3 RUE COROT, libAd2=null, libAd3=null, libAde=null, numTel=03.83.53.36.88,
			 * numTelPort=06.42.01.24.68)
			 *
			 * Bac :
			 * WSBac(codBac=ES, codDep=054, codEtb=0542208G, codMnb=null, daaObtBacIba=2016, temInsAdm=null)
			 *
			 * Cursus interne :
			 * [WSCursusInterne(codVet=1KSNLG/300, libVet=L1-Sciences du langage (NANCY) (FI) - Session 1, codAnu=2016, codMen=null, codTre=DEF, notVet=DEF,
			 * barNotVet=null), WSCursusInterne(codVet=1KSNLG/300, libVet=L1-Sciences du langage (NANCY) (FI) - Session 2, codAnu=2016, codMen=null, codTre=DEF, notVet=DEF,
			 * barNotVet=null), WSCursusInterne(codVet=1WGN20/800, libVet=L1-Psychologie (NANCY) (FI) - Session 1, codAnu=2018, codMen=null, codTre=AJ, notVet=8.793,
			 * barNotVet=20), WSCursusInterne(codVet=CKLNA3/800, libVet=Cles Anglais 2 session 2 (FI), codAnu=2018, codMen=null, codTre=null, notVet=null, barNotVet=null),
			 * WSCursusInterne(codVet=CKLNA1/800, libVet=CLES 1 ANGLAIS - Session unique, codAnu=2018, codMen=null, codTre=ADM, notVet=null, barNotVet=null)] */
		} catch (final SiScolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkString(final ResourceBundle bundle, final String str, final String codBundle) {
		final String value = bundle.getString(codBundle);
		final String log = "codeBundle = " + codBundle + ", valBundle = " + value + ", valeur = " + str;

		if (value.equals("null") && str == null) {
			logger.info("Ok - " + log);
			return;
		}

		if (Objects.equals(str, value)) {
			logger.info("Ok - " + log);
			return;
		}

		throw new RuntimeException("Erreur - " + log);

	}
}
