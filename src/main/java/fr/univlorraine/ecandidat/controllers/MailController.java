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
import fr.univlorraine.ecandidat.views.windows.ScolMailWindow;

/** Gestion de l'entité mail
 *
 * @author Kevin Hergalant */
@Component
public class MailController {

	private Logger logger = LoggerFactory.getLogger(MailController.class);

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
	public List<Mail> getMailsModels() {
		return mailRepository.findByTemIsModeleMail(true);
	}

	/** @return retourne un mail par son code */
	private Mail getMailByCod(final String code) {
		return mailRepository.findByCodMail(code);
	}

	/** @return liste des mails nouveau */
	public List<Mail> getMailsTypeDecScol() {
		return mailRepository.findByTemIsModeleMail(false);
	}

	/** @return liste des mails avec un type de decision */
	public List<Mail> getMailsTypeAvis() {
		return mailRepository.findByTypeAvisNotNullAndTesMail(true);
	}

	/** Ouvre une fenêtre d'édition d'un nouveau mail. */
	public void editNewMail() {
		Mail mail = new Mail(userController.getCurrentUserLogin());
		mail.setTypeAvis(tableRefController.getTypeAvisFavorable());
		mail.setI18nCorpsMail(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_CORPS)));
		mail.setI18nSujetMail(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_SUJET)));
		UI.getCurrent().addWindow(new ScolMailWindow(mail));
	}

	/** Ouvre une fenêtre d'édition de mail.
	 *
	 * @param mail
	 */
	public void editMail(final Mail mail) {
		Assert.notNull(mail, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(mail, null)) {
			return;
		}

		ScolMailWindow window = new ScolMailWindow(mail);
		window.addCloseListener(e -> lockController.releaseLock(mail));
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre un mail
	 *
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

	/** Supprime une mail
	 *
	 * @param mail
	 */
	public void deleteMail(final Mail mail) {
		Assert.notNull(mail, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (typeDecisionRepository.countByMail(mail) > 0) {
			Notification.show(applicationContext.getMessage("mail.error.delete", new Object[] {TypeDecision.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(mail, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("mail.window.confirmDelete", new Object[] {
				mail.getCodMail()}, UI.getCurrent().getLocale()), applicationContext.getMessage("mail.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
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

	/** Verifie l'unicité du code
	 *
	 * @param cod
	 * @param id
	 * @return true si le code est unique */
	public Boolean isCodMailUnique(final String cod, final Integer id) {
		Mail mail = getMailByCod(cod);
		if (mail == null) {
			return true;
		} else {
			if (mail.getIdMail().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/** @param candidat
	 * @param locale
	 * @return un bean mail de candidat */
	public CandidatMailBean getCandidatMailBean(final Candidat candidat, final String locale) {
		CandidatMailBean candidatMailBean = new CandidatMailBean();
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

	/** @param locale
	 * @return un bean mail pour la candidature-->commun a la plupart des mails */
	private CandidatureMailBean getCandidatureMailBean(final Candidature candidature, final String locale) {
		Formation formation = candidature.getFormation();
		Commission commission = candidature.getFormation().getCommission();

		/* Bean candidat */
		CandidatMailBean candidatMailBean = getCandidatMailBean(candidature.getCandidat(), locale);

		/* Bean de la formation */
		FormationMailBean formationMailBean = new FormationMailBean();
		formationMailBean.setCode(formation.getCodForm());
		formationMailBean.setLibelle(formation.getLibForm());
		formationMailBean.setCodEtpVetApo(formation.getCodEtpVetApoForm());
		formationMailBean.setCodVrsVetApo(formation.getCodVrsVetApoForm());
		formationMailBean.setLibApo(formation.getLibApoForm());
		formationMailBean.setMotCle(formation.getMotCleForm());
		if (formation.getDatPubliForm() != null) {
			formationMailBean.setDatPubli(formatterDate.format(formation.getDatPubliForm()));
		}
		if (formation.getDatRetourForm() != null) {
			formationMailBean.setDatRetour(formatterDate.format(formation.getDatRetourForm()));
		}
		if (formation.getDatJuryForm() != null) {
			formationMailBean.setDatJury(formatterDate.format(formation.getDatJuryForm()));
		}
		if (formation.getDatConfirmForm() != null) {
			formationMailBean.setDatConfirm(formatterDate.format(formation.getDatConfirmForm()));
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
		CommissionMailBean commissionMailBean = new CommissionMailBean();
		commissionMailBean.setLibelle(commission.getLibComm());
		commissionMailBean.setTel(commission.getTelComm());
		commissionMailBean.setMail(commission.getMailComm());
		commissionMailBean.setFax(commission.getFaxComm());
		commissionMailBean.setAdresse(adresseController.getLibelleAdresse(commission.getAdresse(), "<br>"));
		commissionMailBean.setCommentaireRetour(i18nController.getI18nTraduction(commission.getI18nCommentRetourComm(), locale));
		commissionMailBean.setSignataire(commission.getSignataireComm());

		DossierMailBean dossierMailBean = new DossierMailBean(MethodUtils.formatDate(candidature.getDatReceptDossierCand(), formatterDate));
		return new CandidatureMailBean(campagneController.getLibelleCampagne(cacheController.getCampagneEnService(), locale), candidatMailBean, formationMailBean, commissionMailBean, dossierMailBean);
	}

	/** Envoie un email
	 *
	 * @param mailTo
	 * @param title
	 * @param text
	 * @param bcc
	 * @param attachement
	 */
	private void sendMail(final String mailTo, final String title, String text, final String bcc, final PdfAttachement attachement) {
		try {
			MimeMessage message = javaMailService.createMimeMessage();
			message.setFrom(new InternetAddress(mailFromNoreply));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailTo));
			if (bcc != null) {
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
			}
			message.setSubject(title);
			text = text
					+ applicationContext.getMessage("mail.footer", null, Locale.getDefault());

			message.setHeader("X-Mailer", "Java");
			message.setSentDate(new Date());

			/* Ajout d'une piece jointe? */
			if (attachement != null) {
				// creates body part for the message
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(text, "text/html; charset=utf-8");

				Multipart multipart = new MimeMultipart();
				// creates body part for the attachment
				MimeBodyPart attachPart = new MimeBodyPart();
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

		} catch (AddressException e) {
			logger.error("Erreur lors de l'envoie du mail : " + e.getMessage());
		} catch (MessagingException e) {
			logger.error("Erreur lors de l'envoie du mail : " + e.getMessage());
		}
	}

	/** Envoie un mail grace a son code
	 *
	 * @param cod
	 * @param bean
	 */
	public void sendMailByCod(final String mailAdr, final String cod, final MailBean bean, final Candidature candidature, final String locale) {
		Mail mail = getMailByCod(cod);
		sendMail(mailAdr, mail, bean, candidature, locale, null);
	}

	/** Envoie un mail */
	public void sendMail(final String mailAdr, final Mail mail, final MailBean bean, final Candidature candidature, String locale, final PdfAttachement attachement) {
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
		String varMail = getVarMail(mail);
		String varCandidature = getVarMailCandidature(mail.getCodMail());

		/* Suppression des if */
		String contentWithoutIf = deleteIfFromMail(contentMail, bean, candidatureMailBean);
		while (contentWithoutIf != null) {
			contentMail = contentWithoutIf;
			contentWithoutIf = deleteIfFromMail(contentMail, bean, candidatureMailBean);
		}
		/* Fin supression if */

		sujetMail = parseVar(sujetMail, varMail, bean);
		sujetMail = parseVar(sujetMail, varCandidature, candidatureMailBean);
		contentMail = parseVar(contentMail, varMail, bean);
		contentMail = parseVar(contentMail, varCandidature, candidatureMailBean);

		String bcc = null;

		if (candidature != null) {
			CentreCandidature ctrCand = candidature.getFormation().getCommission().getCentreCandidature();
			if (ctrCand.getTemSendMailCtrCand() && ctrCand.getMailContactCtrCand() != null) {
				bcc = ctrCand.getMailContactCtrCand();
			}
		}

		sendMail(mailAdr, sujetMail, contentMail, bcc, attachement);
	}

	/** Parse les variables du mail
	 *
	 * @param contentMail
	 * @param var
	 * @param bean
	 * @return le contenu parsé */
	private String parseVar(String contentMail, final String var, final MailBean bean) {
		if (bean != null && var != null && !var.equals("")) {
			String[] tabSplit = var.split(";");

			for (String property : tabSplit) {
				String propRegEx = "\\$\\{" + property + "\\}";
				contentMail = contentMail.replaceAll(propRegEx, bean.getValueProperty(property));
			}
		}
		return contentMail;
	}

	/** Parse les if d'un mail
	 *
	 * @param contentMail
	 * @param beanSpecifique
	 * @param candidatureMailBean
	 * @return le contenu parsé */
	private String deleteIfFromMail(String contentMail, final MailBean beanSpecifique, final CandidatureMailBean candidatureMailBean) {
		/* Les balsies IF */
		String baliseDebutIf = "{if($";
		String baliseFinIf = ")}";

		/* On recherche la position de la balise if */
		int indexDebutIf = contentMail.indexOf(baliseDebutIf, 0);
		if (indexDebutIf != -1) {
			/* elle existe donc on recherche la premiere position de la balise de fin du if */
			int indexFinIf = contentMail.indexOf(baliseFinIf, indexDebutIf + baliseDebutIf.length());
			if (indexFinIf != -1) {
				/* Elle existe, on calcul la propriété */
				String property = contentMail.substring(indexDebutIf + baliseDebutIf.length(), indexFinIf);
				String endIf = "{endif($" + property + ")}";

				if (property != null && !property.equals("")) {
					String valueProperty = (beanSpecifique == null) ? null : beanSpecifique.getValueProperty(property);
					String valuePropertyCandidature = (candidatureMailBean == null) ? null : candidatureMailBean.getValueProperty(property);
					if (valueProperty != null && !valueProperty.equals("")) {
						contentMail = contentMail.replace(baliseDebutIf + property + baliseFinIf, "");
						contentMail = contentMail.replace(endIf, "");
						return contentMail;
					} else if (valuePropertyCandidature != null && !valuePropertyCandidature.equals("")) {
						contentMail = contentMail.replace(baliseDebutIf + property + baliseFinIf, "");
						contentMail = contentMail.replace(endIf, "");
						return contentMail;
					} else {
						int indexDebutEndIf = contentMail.indexOf(endIf, indexFinIf + baliseFinIf.length());
						if (indexDebutEndIf != -1) {
							String strToReplace = contentMail.substring(indexDebutIf, indexDebutEndIf + endIf.length());
							return contentMail.replace(strToReplace, "");
						}
					}

				}

			}
		}
		return null;
	}

	/** @param mail
	 * @return les variables de mail */
	public String getVarMail(final Mail mail) {
		String codMail = mail.getCodMail();
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
		} else if (mail.getTypeAvis() != null) {
			/* Mail de type de decisoion */
			TypeAvis type = mail.getTypeAvis();
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

	/** @param codMail
	 * @return les variables de mail génériques */
	public String getVarMailCandidature(final String codMail) {
		if (codMail != null && (codMail.equals(NomenclatureUtils.MAIL_CPT_MIN) ||
				codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE) ||
				codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL) ||
				codMail.equals(NomenclatureUtils.MAIL_CPT_MIN_DELETE) ||
				codMail.equals(NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI))) {
			return null;
		} else {
			return NomenclatureUtils.MAIL_GEN_VAR + ";" + NomenclatureUtils.MAIL_CANDIDAT_GEN_VAR + ";" + NomenclatureUtils.MAIL_FORMATION_GEN_VAR + ";" + NomenclatureUtils.MAIL_COMMISSION_GEN_VAR
					+ ";" + NomenclatureUtils.MAIL_DOSSIER_GEN_VAR;
		}
	}

	/** Envoi une erreur à l'admin fonctionnel, si pas de mail, alors on log une erreur
	 *
	 * @param title
	 * @param text
	 * @param loggers
	 */
	public void sendErrorToAdminFonctionnel(final String title, final String text, final Logger loggers) {
		if (mailToFonctionnel != null && !mailToFonctionnel.equals("") && MethodUtils.isValidEmailAddress(mailToFonctionnel)) {
			sendMail(mailToFonctionnel, title, text, null, null);
			logger.debug(text);
		} else {
			logger.error(text);
		}
	}
}
