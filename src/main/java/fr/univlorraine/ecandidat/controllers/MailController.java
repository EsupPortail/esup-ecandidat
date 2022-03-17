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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.repositories.MailRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionRepository;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.PdfAttachement;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatureMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.CommissionMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.DossierMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.FormationMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.MailBean;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.MailWindow;

/**
 * Gestion de l'entité mail
 * @author Kevin Hergalant
 */
@Component
public class MailController {

	private final Logger logger = LoggerFactory.getLogger(MailController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient MailRepository mailRepository;
	@Resource
	private transient TypeDecisionRepository typeDecisionRepository;
	@Resource
	private JavaMailSender javaMailService;
	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Variable d'envirronement */
	@Value("${mail.from.noreply:}")
	private String mailFromNoreply;

	@Value("${mail.to.fonctionnel:}")
	private transient String mailToFonctionnel;

	/** @return liste des mails modele */
	public List<Mail> getMailsByCtrCand(final Boolean temModel, final CentreCandidature ctrCand) {
		return mailRepository.findByTemIsModeleMailAndCentreCandidature(temModel, ctrCand);
	}

	/** @return retourne un mail par son code */
	private Mail getMailByCod(final String code) {
		return mailRepository.findByCodMail(code);
	}

	/** @return liste des mails avec un type de decision */
	public List<Mail> getMailsTypeAvisEnServiceByCtrCand(final CentreCandidature ctrCand) {
		// Mail de la scol centrale
		final List<Mail> liste = mailRepository.findByTypeAvisNotNullAndTesMailAndCentreCandidatureIdCtrCand(true, null);
		// Mail pour les ctrCand
		if (ctrCand != null) {
			liste.addAll(mailRepository.findByTypeAvisNotNullAndTesMailAndCentreCandidatureIdCtrCand(true, ctrCand.getIdCtrCand()));

		}
		liste.sort((h1, h2) -> h1.getCodMail().compareTo(h2.getCodMail()));
		return liste;
	}

	/** Ouvre une fenêtre d'édition d'un nouveau mail. */
	public void editNewMail(final CentreCandidature ctrCand) {
		final Mail mail = new Mail(userController.getCurrentUserLogin());
		mail.setTesMail(true);
		mail.setCentreCandidature(ctrCand);
		mail.setTypeAvis(tableRefController.getTypeAvisFavorable());
		mail.setI18nCorpsMail(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_CORPS)));
		mail.setI18nSujetMail(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_SUJET)));
		UI.getCurrent().addWindow(new MailWindow(mail));
	}

	/**
	 * Ouvre une fenêtre d'édition de mail.
	 * @param mail
	 */
	public void editMail(final Mail mail) {
		Assert.notNull(mail, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(mail, null)) {
			return;
		}

		final MailWindow window = new MailWindow(mail);
		window.addCloseListener(e -> lockController.releaseLock(mail));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un mail
	 * @param mail
	 */
	public void saveMail(Mail mail) {
		Assert.notNull(mail, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (mail.getIdMail() != null && !lockController.getLockOrNotify(mail, null)) {
			return;
		}
		mail.setUserModMail(userController.getCurrentUserLogin());
		mail.setI18nSujetMail(i18nController.saveI18n(mail.getI18nSujetMail()));
		mail.setI18nCorpsMail(i18nController.saveI18n(mail.getI18nCorpsMail()));
		mail = mailRepository.saveAndFlush(mail);

		lockController.releaseLock(mail);
	}

	/**
	 * Supprime une mail
	 * @param mail
	 */
	public void deleteMail(final Mail mail) {
		Assert.notNull(mail, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (typeDecisionRepository.countByMail(mail) > 0) {
			Notification.show(applicationContext.getMessage("mail.error.delete", new Object[] { TypeDecision.class.getSimpleName() }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(mail, null)) {
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("mail.window.confirmDelete", new Object[] { mail.getCodMail() }, UI.getCurrent().getLocale()),
			applicationContext.getMessage("mail.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(mail, null)) {
				mailRepository.delete(mail);
				/* Suppression du lock */
				lockController.releaseLock(mail);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(mail);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Verifie l'unicité du code
	 * @param  cod
	 * @param  id
	 * @return     true si le code est unique
	 */
	public Boolean isCodMailUnique(final String cod, final Integer id) {
		final Mail mail = getMailByCod(cod);
		if (mail == null) {
			return true;
		} else {
			if (mail.getIdMail().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param  candidat
	 * @param  locale
	 * @return          un bean mail de candidat
	 */
	public CandidatMailBean getCandidatMailBean(final Candidat candidat, final String locale) {
		final CandidatMailBean candidatMailBean = new CandidatMailBean();
		if (candidat.getCivilite() != null && candidat.getCivilite().getCodCiv().equals(NomenclatureUtils.CIVILITE_F)) {
			candidatMailBean.setCivilite(applicationContext.getMessage("candidature.mail.civilite.madame", null, new Locale(locale)));
		} else {
			candidatMailBean.setCivilite(applicationContext.getMessage("candidature.mail.civilite.monsieur", null, new Locale(locale)));
		}
		candidatMailBean.setNumDossierOpi(candidat.getCompteMinima().getNumDossierOpiCptMin());
		candidatMailBean.setNomPat(candidat.getNomPatCandidat());
		candidatMailBean.setNomUsu(candidat.getNomUsuCandidat());
		candidatMailBean.setPrenom(candidat.getPrenomCandidat());
		candidatMailBean.setAutrePrenom(candidat.getAutrePrenCandidat());
		candidatMailBean.setIne(candidat.getIneCandidat());
		candidatMailBean.setCleIne(candidat.getCleIneCandidat());
		candidatMailBean.setDatNaiss(formatterDate.format(candidat.getDatNaissCandidat()));
		candidatMailBean.setLibVilleNaiss(candidat.getLibVilleNaissCandidat());
		candidatMailBean.setLibLangue(candidat.getLangue().getLibLangue());
		candidatMailBean.setTel(candidat.getTelCandidat());
		candidatMailBean.setTelPort(candidat.getTelPortCandidat());
		return candidatMailBean;
	}

	/**
	 * @param  locale
	 * @return        un bean mail pour la candidature-->commun a la plupart des mails
	 */
	private CandidatureMailBean getCandidatureMailBean(final Candidature candidature, final String locale) {
		final Formation formation = candidature.getFormation();
		final Commission commission = candidature.getFormation().getCommission();

		/* Bean candidat */
		final CandidatMailBean candidatMailBean = getCandidatMailBean(candidature.getCandidat(), locale);

		/* Bean de la formation */
		final FormationMailBean formationMailBean = new FormationMailBean();
		formationMailBean.setCode(formation.getCodForm());
		formationMailBean.setLibelle(formation.getLibForm());
		formationMailBean.setCodEtpVetApo(formation.getCodEtpVetApoForm());
		formationMailBean.setCodVrsVetApo(formation.getCodVrsVetApoForm());
		formationMailBean.setLibApo(formation.getLibApoForm());
		formationMailBean.setMotCle(formation.getMotCleForm());
		if (formation.getDatPubliForm() != null) {
			formationMailBean.setDatPubli(formatterDate.format(formation.getDatPubliForm()));
		}
		final LocalDate dateRetour = candidatureController.getDateRetourCandidat(candidature);
		if (dateRetour != null) {
			formationMailBean.setDatRetour(formatterDate.format(dateRetour));
		}
		if (formation.getDatJuryForm() != null) {
			formationMailBean.setDatJury(formatterDate.format(formation.getDatJuryForm()));
		}
		final LocalDate dateConfirm = candidatureController.getDateConfirmCandidat(candidature);
		if (dateConfirm != null) {
			formationMailBean.setDatConfirm(formatterDate.format(dateConfirm));
		}
		if (formation.getDatDebDepotForm() != null) {
			formationMailBean.setDatDebDepot(formatterDate.format(formation.getDatDebDepotForm()));
		}
		if (formation.getDatFinDepotForm() != null) {
			formationMailBean.setDatFinDepot(formatterDate.format(formation.getDatFinDepotForm()));
		}
		if (formation.getDatAnalyseForm() != null) {
			formationMailBean.setDatPreAnalyse(formatterDate.format(formation.getDatAnalyseForm()));
		}

		/* Bean de la commission */
		final CommissionMailBean commissionMailBean = new CommissionMailBean();
		commissionMailBean.setLibelle(commission.getLibComm());
		commissionMailBean.setTel(commission.getTelComm());
		commissionMailBean.setUrl(commission.getUrlComm());
		commissionMailBean.setMail(commission.getMailComm());
		commissionMailBean.setFax(commission.getFaxComm());
		commissionMailBean.setAdresse(adresseController.getLibelleAdresse(commission.getAdresse(), "<br>"));
		commissionMailBean.setCommentaireRetour(i18nController.getI18nTraduction(commission.getI18nCommentRetourComm(), locale));
		commissionMailBean.setSignataire(commission.getSignataireComm());

		final DossierMailBean dossierMailBean = new DossierMailBean(MethodUtils.formatDate(candidature.getDatReceptDossierCand(), formatterDate), candidature.getMntChargeCand(), candidature.getCompExoExtCand());
		return new CandidatureMailBean(campagneController.getLibelleCampagne(cacheController.getCampagneEnService(), locale), candidatMailBean, formationMailBean, commissionMailBean, dossierMailBean);
	}

	/**
	 * Envoie un email
	 * @param mailTo
	 * @param title
	 * @param text
	 * @param bcc
	 * @param attachement
	 * @param locale
	 */
	private void sendMail(final String[] mailTo, final String title, String text, final String[] bcc, final PdfAttachement attachement, final String locale) {
		try {
			final MimeMessage message = javaMailService.createMimeMessage();
			message.setFrom(new InternetAddress(mailFromNoreply));
			message.setRecipients(Message.RecipientType.TO, stringToInternetAddressArray(mailTo));
			if (bcc != null && bcc.length != 0) {
				message.addRecipients(Message.RecipientType.BCC, stringToInternetAddressArray(bcc));
			}
			message.setSubject(title, "utf-8");
			text = text + applicationContext.getMessage("mail.footer", null, locale == null ? new Locale("fr") : new Locale(locale));

			message.setHeader("X-Mailer", "Java");
			message.setSentDate(new Date());

			/* Ajout d'une piece jointe? */
			if (attachement != null) {
				// creates body part for the message
				final MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(text, "text/html; charset=utf-8");

				final Multipart multipart = new MimeMultipart();
				// creates body part for the attachment
				final MimeBodyPart attachPart = new MimeBodyPart();
				attachPart.setDataHandler(new DataHandler(attachement));
				attachPart.setFileName(attachement.getFileName());

				// adds parts to the multipart
				multipart.addBodyPart(messageBodyPart);
				multipart.addBodyPart(attachPart);

				// sets the multipart as message's content
				message.setContent(multipart);
			} else {
				message.setContent(text, "text/html; charset=utf-8");
			}

			javaMailService.send(message);

		} catch (final AddressException e) {
			logger.error("Erreur lors de l'envoie du mail : " + e.getMessage());
		} catch (final MessagingException e) {
			logger.error("Erreur lors de l'envoie du mail : " + e.getMessage());
		}
	}

	/** Envoie un mail */
	public void sendMail(final String[] mailAdr, final Mail mail, final MailBean bean, final Candidature candidature, String locale, final PdfAttachement attachement) {
		if (mail == null || !mail.getTesMail()) {
			return;
		}
		if (locale == null) {
			locale = cacheController.getLangueDefault().getCodLangue();
		}
		CandidatureMailBean candidatureMailBean = null;
		if (candidature != null) {
			candidatureMailBean = getCandidatureMailBean(candidature, locale);
		}
		String contentMail = i18nController.getI18nTraduction(mail.getI18nCorpsMail(), locale);
		String sujetMail = i18nController.getI18nTraduction(mail.getI18nSujetMail(), locale);
		final String varMail = getVarMail(mail);
		final String varCandidature = getVarMailCandidature(mail.getCodMail());

		/* Suppression des if */
		String contentWithoutIf = deleteIfFromMail(contentMail, bean, candidatureMailBean);
		while (contentWithoutIf != null) {
			contentMail = contentWithoutIf;
			contentWithoutIf = deleteIfFromMail(contentMail, bean, candidatureMailBean);
		}
		/* Fin supression if */

		sujetMail = parseVar(sujetMail, varMail, bean, varCandidature, candidatureMailBean);
		contentMail = parseVar(contentMail, varMail, bean, varCandidature, candidatureMailBean);

		String[] bcc = new String[] {};

		if (candidature != null) {
			bcc = candidature.getFormation().getCommission().getCentreCandidature().getMailBcc();
		}
		sendMail(mailAdr, sujetMail, contentMail, bcc, attachement, locale);
	}

	/**
	 * Envoi un mail
	 * @param mailAdr
	 * @param mail
	 * @param bean
	 * @param candidature
	 * @param locale
	 * @param attachement
	 */
	public void sendMail(final String mailAdr, final Mail mail, final MailBean bean, final Candidature candidature, final String locale, final PdfAttachement attachement) {
		sendMail(new String[] { mailAdr }, mail, bean, candidature, locale, attachement);
	}

	/**
	 * Envoie un mail grace a son code
	 * @param cod
	 * @param bean
	 */
	public void sendMailByCod(final String mailAdr, final String cod, final MailBean bean, final Candidature candidature, final String locale) {
		sendMailByCod(new String[] { mailAdr }, cod, bean, candidature, locale);
	}

	/**
	 * Envoie un mail grace a son code
	 * @param cod
	 * @param bean
	 */
	public void sendMailByCod(final String[] mailAdr, final String cod, final MailBean bean, final Candidature candidature, final String locale) {
		final Mail mail = getMailByCod(cod);
		sendMail(mailAdr, mail, bean, candidature, locale, null);
	}

	/**
	 * @param  a
	 * @return                  un array de InternetAddress
	 * @throws AddressException
	 */
	private InternetAddress[] stringToInternetAddressArray(final String[] a) throws AddressException {
		final InternetAddress[] ia = new InternetAddress[a.length];
		for (int i = 0; i < a.length; i++) {
			ia[i] = new InternetAddress(a[i]);
		}
		return ia;
	}

	/**
	 * Parse les variables du mail
	 * @param  contentMail
	 * @param  var
	 * @param  bean
	 * @param  candidatureMailBean
	 * @param  varCandidature
	 * @return                     le contenu parsé
	 */
	private String parseVar(String contentMail, final String var, final MailBean bean, final String varCandidature, final CandidatureMailBean candidatureMailBean) {
		/* Bean spécifique */
		if (bean != null && var != null && !var.equals("")) {
			final String[] tabSplit = var.split(";");

			for (final String property : tabSplit) {
				final String propRegEx = "\\$\\{" + property + "\\}";
				contentMail = contentMail.replaceAll(propRegEx, bean.getValueProperty(property));
			}
		}
		/* Bean candidature */
		if (candidatureMailBean != null && varCandidature != null && !varCandidature.equals("")) {
			final String[] tabSplit = varCandidature.split(";");

			for (final String property : tabSplit) {
				final String propRegEx = "\\$\\{" + property + "\\}";
				contentMail = contentMail.replaceAll(propRegEx, candidatureMailBean.getValueProperty(property));
			}
		}
		return contentMail;
	}

	/**
	 * Parse les if d'un mail
	 * @param  contentMail
	 * @param  beanSpecifique
	 * @param  candidatureMailBean
	 * @return                     le contenu parsé
	 */
	private String deleteIfFromMail(String contentMail, final MailBean beanSpecifique, final CandidatureMailBean candidatureMailBean) {
		/* Les balsies IF */
		final String baliseDebutIf = "{if($";
		final String baliseFinIf = ")}";

		/* On recherche la position de la balise if */
		final int indexDebutIf = contentMail.indexOf(baliseDebutIf, 0);
		if (indexDebutIf != -1) {
			/* elle existe donc on recherche la premiere position de la balise de fin du if */
			final int indexFinIf = contentMail.indexOf(baliseFinIf, indexDebutIf + baliseDebutIf.length());
			if (indexFinIf != -1) {
				/* Elle existe, on calcul la propriété */
				final String property = contentMail.substring(indexDebutIf + baliseDebutIf.length(), indexFinIf);
				final String endIf = "{endif($" + property + ")}";

				if (property != null && !property.equals("")) {
					final String valueProperty = (beanSpecifique == null) ? null : beanSpecifique.getValueProperty(property);
					final String valuePropertyCandidature = (candidatureMailBean == null) ? null : candidatureMailBean.getValueProperty(property);
					if (valueProperty != null && !valueProperty.equals("")) {
						contentMail = contentMail.replace(baliseDebutIf + property + baliseFinIf, "");
						contentMail = contentMail.replace(endIf, "");
						return contentMail;
					} else if (valuePropertyCandidature != null && !valuePropertyCandidature.equals("")) {
						contentMail = contentMail.replace(baliseDebutIf + property + baliseFinIf, "");
						contentMail = contentMail.replace(endIf, "");
						return contentMail;
					} else {
						final int indexDebutEndIf = contentMail.indexOf(endIf, indexFinIf + baliseFinIf.length());
						if (indexDebutEndIf != -1) {
							final String strToReplace = contentMail.substring(indexDebutIf, indexDebutEndIf + endIf.length());
							return contentMail.replace(strToReplace, "");
						}
					}

				}

			}
		}
		return null;
	}

	/**
	 * @param  mail
	 * @return      les variables de mail
	 */
	public String getVarMail(final Mail mail) {
		final String codMail = mail.getCodMail();
		/* Mail de compte a minima */
		if (codMail != null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN)) {
			return NomenclatureUtils.MAIL_GEN_VAR + ";" + NomenclatureUtils.MAIL_CPT_MIN_VAR;
		} else if (codMail != null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE)) {
			/* Mail de d'identifiants oubliés */
			return NomenclatureUtils.MAIL_GEN_VAR + ";" + NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE_VAR;
		} else if (codMail != null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL)) {
			/* Mail de modification de mail */
			return NomenclatureUtils.MAIL_GEN_VAR + ";" + NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL_VAR;
		} else if (codMail != null && codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_DELETE)) {
			/* Mail suppression de compte */
			return NomenclatureUtils.MAIL_GEN_VAR + ";" + NomenclatureUtils.MAIL_CPT_MIN_DELETE_VAR;
		} else if (codMail != null && codMail.equals(NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI)) {
			/* Mail de modification d'OPI */
			return NomenclatureUtils.MAIL_CANDIDAT_GEN_VAR + ";" + NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI_VAR;
		} else if (codMail != null && codMail.equals(NomenclatureUtils.MAIL_CANDIDATURE_RELANCE_FORMULAIRE)) {
			/* Mail de modification d'OPI */
			return NomenclatureUtils.MAIL_CANDIDATURE_RELANCE_FORMULAIRE_VAR;
		} else if (mail.getTypeAvis() != null) {
			/* Mail de type de decisoion */
			final TypeAvis type = mail.getTypeAvis();
			String var = NomenclatureUtils.MAIL_DEC_VAR;
			if (type.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF)) {
				var = var + ";" + NomenclatureUtils.MAIL_DEC_VAR_DEFAVORABLE;
			} else if (type.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)) {
				var = var + ";" + NomenclatureUtils.MAIL_DEC_VAR_PRESELECTION;
			} else if (type.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP)) {
				var = var + ";" + NomenclatureUtils.MAIL_DEC_VAR_LISTE_COMP;
			}
			return var;
		}
		return null;
	}

	/**
	 * @param  codMail
	 * @return         les variables de mail génériques
	 */
	public String getVarMailCandidature(final String codMail) {
		if (codMail != null && (codMail.equals(NomenclatureUtils.MAIL_CPT_MIN) || codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE)
			|| codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL)
			|| codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_DELETE)
			|| codMail.equals(NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI))) {
			return null;
		} else {
			return NomenclatureUtils.MAIL_GEN_VAR + ";"
				+ NomenclatureUtils.MAIL_CANDIDAT_GEN_VAR
				+ ";"
				+ NomenclatureUtils.MAIL_FORMATION_GEN_VAR
				+ ";"
				+ NomenclatureUtils.MAIL_COMMISSION_GEN_VAR
				+ ";"
				+ NomenclatureUtils.MAIL_DOSSIER_GEN_VAR;
		}
	}

	/**
	 * Envoi une erreur à l'admin fonctionnel, si pas de mail, alors on log une erreur
	 * @param title
	 * @param text
	 * @param loggers
	 */
	public void sendErrorToAdminFonctionnel(final String title, final String text, final Logger loggers) {
		if (mailToFonctionnel != null && !mailToFonctionnel.equals("") && MethodUtils.isValidEmailAddress(mailToFonctionnel)) {
			sendMail(new String[] { mailToFonctionnel }, title, text, null, null, cacheController.getLangueDefault().getCodLangue());
			logger.debug(text);
		} else {
			logger.error(text);
		}
	}
}
