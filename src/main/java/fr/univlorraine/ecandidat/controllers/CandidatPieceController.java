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

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidatPK;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.repositories.PjCandidatRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatAdminListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import jakarta.annotation.Resource;

/**
 * Gestion des pieces du candidat
 * @author Kevin Hergalant
 */
@Component
public class CandidatPieceController {
	private final Logger logger = LoggerFactory.getLogger(CandidatPieceController.class);
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PjCandidatRepository pjCandidatRepository;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient ParametreController parametreController;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Resource
	private transient DateTimeFormatter formatterDateTimeApoWsPj;

	/**
	 * @param cptMin
	 * @param listener
	 */
	public void adminSynchronizePJCandidat(final CompteMinima cptMin, final CandidatAdminListener listener) {
		if (!siScolService.hasSyncEtudiantPJ()) {
			return;
		}
		final Candidat candidat = cptMin.getCandidat();
		if (candidat == null) {
			Notification.show(applicationContext.getMessage("pj.sync.apo.cand.absent", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		final String codEtu = candidat.getCompteMinima().getSupannEtuIdCptMin();
		if (codEtu == null) {
			Notification.show(applicationContext.getMessage("pj.sync.apo.codEtu.absent", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		final ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("pj.sync.apo.window", null, UI.getCurrent().getLocale()));
		win.addBtnOuiListener(e -> {
			try {
				synchronizePJCandidat(candidat);
				if (listener != null) {
					listener.cptMinModified(candidat.getCompteMinima());
				}
			} catch (final Exception e1) {
				Notification.show(applicationContext.getMessage("pj.sync.apo.nok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
			Notification.show(applicationContext.getMessage("pj.sync.apo.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(win);
	}

	/**
	 * Supprime toutes les pièces d'un candidat
	 * @param candidat
	 */
	public void deletePJCandidat(final Candidat candidat) {
		if (!siScolService.hasSyncEtudiantPJ()) {
			return;
		}
		// on supprime toutes les pièces de notre candidat
		pjCandidatRepository.deleteAll(candidat.getPjCandidats());
	}

	/**
	 * Synchronise les pièces d'un candidat : delete + insert
	 * @param  candidat
	 * @throws SiScolException
	 */
	@Transactional(rollbackFor = SiScolException.class)
	public void synchronizePJCandidat(final Candidat candidat) throws SiScolException {
		if (!siScolService.hasSyncEtudiantPJ()) {
			return;
		}
		// on supprime toutes les pièces de notre candidat
		pjCandidatRepository.deleteAll(candidat.getPjCandidats());
		// on insere toutes les pieces d'apogée
		recordPjFromApo(candidat);
	}

	/**
	 * Insere les nouvelles pièces
	 * @param  candidat
	 * @throws SiScolException
	 */
	@Transactional(rollbackFor = SiScolException.class)
	private void recordPjFromApo(final Candidat candidat) throws SiScolException {
		if (candidat == null || candidat.getCompteMinima().getSupannEtuIdCptMin() == null || !siScolService.hasSyncEtudiantPJ()) {
			return;
		}
		// on collecte les code Apogee de pieces et on constitue une liste de codeApogee distinct
		final List<String> listeCodeApo = pieceJustifController.getAllPieceJustifs()
			.stream()
			.filter(piece -> piece.getCodApoPj() != null && !piece.getCodApoPj().equals(""))
			.map(PieceJustif::getCodApoPj)
			.distinct()
			.collect(Collectors.toList());

		final List<PjCandidat> liste = new ArrayList<>();
		// on ajoute ses nouvelles pieces
		for (final String codeTpj : listeCodeApo) {
			final WSPjInfo info = siScolService.getPjInfoFromApogee(null, candidat.getCompteMinima().getSupannEtuIdCptMin(), codeTpj);
			if (info != null) {
				final PjCandidatPK pk = new PjCandidatPK(candidat.getIdCandidat(), info.getCodAnu(), info.getCodTpj());
				final PjCandidat pjCandidat = new PjCandidat();
				pjCandidat.setId(pk);
				pjCandidat.setNomFicPjCandidat(info.getNomFic());
				pjCandidat.setCandidat(candidat);
				if (info.getDatExp() != null) {
					pjCandidat.setDatExpPjCandidat(LocalDateTime.parse(info.getDatExp(), formatterDateTimeApoWsPj));
				}
				if (MethodUtils.validateBean(pjCandidat, logger)) {
					liste.add(pjCandidatRepository.save(pjCandidat));
				} else {
					throw new SiScolException("Erreur de validation");
				}
			}
		}
		candidat.setPjCandidats(liste);
	}

	/**
	 * @param  codeApo
	 * @param  candidat
	 * @return          la PJCandidat trouvée
	 */
	public PjCandidat getPjCandidat(final String codeApo, final Candidat candidat) {
		if (codeApo == null || !siScolService.hasSyncEtudiantPJ()) {
			return null;
		}
		final Optional<PjCandidat> pjCandidatOpt = candidat.getPjCandidats()
			.stream()
			.filter(e -> e.getId().getCodTpjPjCandidat().equals(codeApo)
				&& (e.getDatExpPjCandidat() == null || e.getDatExpPjCandidat().isAfter(LocalDateTime.now()) || e.getDatExpPjCandidat().isEqual(LocalDateTime.now())))
			.findAny();
		if (pjCandidatOpt.isPresent()) {
			return pjCandidatOpt.get();
		}
		return null;
	}

	/**
	 * @param  pjCandidatFromApogee
	 * @return                      recupere l'inputstream d'apogée
	 * @throws SiScolException
	 */
	public InputStream getInputStreamFromFichier(final PjCandidat pjCandidatFromApogee) throws SiScolException {
		if (!siScolService.hasSyncEtudiantPJ()) {
			return null;
		}
		return siScolService.getPjFichierFromApogee(pjCandidatFromApogee.getId().getCodAnuPjCandidat(),
			pjCandidatFromApogee.getCandidat().getCompteMinima().getSupannEtuIdCptMin(),
			pjCandidatFromApogee.getId().getCodTpjPjCandidat());
	}
}
