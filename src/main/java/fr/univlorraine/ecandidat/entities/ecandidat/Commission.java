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
package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the commission database table.
 */
@Entity
@Table(name = "commission")
@Data
@EqualsAndHashCode(of = "idComm")
@EntityListeners(EntityPushEntityListener.class)
@ToString(of = { "idComm", "codComm", "libComm", "tesComm" })
@SuppressWarnings("serial")
public class Commission implements Serializable {

	public static final String TYP_ALERT_NONE = "N";
	public static final String TYP_ALERT_MAIL = "M";
	public static final String TYP_ALERT_LIST_MEMBRE = "L";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_comm", nullable = false)
	private Integer idComm;

	@Column(name = "cod_comm", unique = true, nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String codComm;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_comment_retour_comm", nullable = false)
	private I18n i18nCommentRetourComm;

	@Column(name = "comment_retour_comm", length = 500)
	@Size(max = 500)
	private String commentRetourComm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_comm", nullable = false)
	@NotNull
	private LocalDateTime datCreComm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_comm", nullable = false)
	@NotNull
	private LocalDateTime datModComm;

	@Column(name = "fax_comm", length = 20)
	@Size(max = 20)
	private String faxComm;

	@Column(name = "lib_comm", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String libComm;

	@Column(name = "mail_comm", nullable = true, length = 80)
	@Size(max = 80)
	private String mailComm;

	/* Type d'alerte de la commission */
	/* N : pas d'alerte, M : utilisation du mail d'alerte, M : utilisation des mails de la liste des membres */
	@Column(name = "typ_alert_comm", length = 1, nullable = false)
	@Size(max = 1)
	@NotNull
	private String typAlertComm;

	@Column(name = "mail_alert_comm", length = 80)
	@Size(max = 80)
	private String mailAlertComm;

	@Column(name = "url_comm", nullable = true, length = 255)
	@Size(max = 255)
	private String urlComm;

	@Column(name = "signataire_comm", length = 255)
	@Size(max = 255)
	private String signataireComm;

	@Column(name = "tel_comm", length = 20)
	@Size(max = 20)
	private String telComm;

	@Column(name = "tes_comm", nullable = false)
	@NotNull
	private Boolean tesComm;

	@Column(name = "tem_edit_lettre_comm", nullable = false)
	@NotNull
	private Boolean temEditLettreComm;

	@Column(name = "tem_mail_lettre_comm", nullable = false)
	@NotNull
	private Boolean temMailLettreComm;

	@Column(name = "tem_alert_prop_comm", nullable = false)
	@NotNull
	private Boolean temAlertPropComm;

	@Column(name = "tem_alert_annul_comm", nullable = false)
	@NotNull
	private Boolean temAlertAnnulComm;

	@Column(name = "tem_alert_trans_comm", nullable = false)
	@NotNull
	private Boolean temAlertTransComm;

	@Column(name = "tem_alert_desist_comm", nullable = false)
	@NotNull
	private Boolean temAlertDesistComm;

	@Column(name = "tem_alert_list_princ_comm", nullable = false)
	@NotNull
	private Boolean temAlertListePrincComm;

	@Column(name = "user_cre_comm", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreComm;

	@Column(name = "user_mod_comm", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModComm;

	// bi-directional many-to-one association to Adresse
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_adr", nullable = false)
	@NotNull
	private Adresse adresse;

	@OneToOne
	@JoinColumn(name = "id_fichier")
	private Fichier fichier;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand", nullable = false)
	@NotNull
	private CentreCandidature centreCandidature;

	// bi-directional many-to-one association to CommissionMembre
	@OneToMany(mappedBy = "commission", cascade = CascadeType.ALL)
	private List<CommissionMembre> commissionMembres;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "commission")
	private List<Formation> formations;

	// bi-directional many-to-many association to Gestionnaire
	@ManyToMany(mappedBy = "commissions", cascade = CascadeType.MERGE)
	private List<Gestionnaire> gestionnaires;

	@Transient
	private String adresseStr;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return codComm + "/" + libComm;
	}

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelleAlternatif() {
		return libComm + " (" + codComm + ")";
	}

	/**
	 * @return les mails d'alerte de la commission
	 */
	public String[] getMailAlert() {
		if (TYP_ALERT_MAIL.equals(typAlertComm) && mailAlertComm != null) {
			return new String[] { mailAlertComm };
		} else if (TYP_ALERT_LIST_MEMBRE.equals(typAlertComm) && commissionMembres != null) {
			return commissionMembres.stream().map(e -> e.getDroitProfilInd().getIndividu().getMailInd()).toArray(String[]::new);
		}
		return new String[] {};
	}

	@PrePersist
	private void onPrePersist() {
		datCreComm = LocalDateTime.now();
		datModComm = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		datModComm = LocalDateTime.now();
	}

	public Commission(final CentreCandidature ctrCand, final String user, final String typSiscol) {
		super();
		centreCandidature = ctrCand;
		userCreComm = user;
		userModComm = user;
		tesComm = true;
		typAlertComm = TYP_ALERT_MAIL;
		temAlertPropComm = true;
		temAlertAnnulComm = true;
		temAlertTransComm = true;
		temAlertDesistComm = true;
		temAlertListePrincComm = true;
		adresse = new Adresse(typSiscol);
	}

	public Commission() {
		super();
	}
}
