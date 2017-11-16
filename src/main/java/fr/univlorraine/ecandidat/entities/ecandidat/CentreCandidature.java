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
import java.time.LocalDate;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;


/**
 * The persistent class for the centre_candidature database table.
 * 
 */
@Entity
@Table(name="centre_candidature") @EntityListeners(EntityPushEntityListener.class)
@Data @EqualsAndHashCode(of="idCtrCand")
@ToString(of={"idCtrCand", "codCtrCand", "libCtrCand", "tesCtrCand"})
public class CentreCandidature implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2282282314657251911L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_ctr_cand", nullable=false)
	private Integer idCtrCand;

	@Column(name="cod_ctr_cand", unique=true, nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codCtrCand;
	
	@Column(name="mail_contact_ctr_cand", nullable=true, length=80)
	@Size(max = 80) 
	private String mailContactCtrCand;
	
	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_analyse_ctr_cand")
	private LocalDate datAnalyseCtrCand;
	
	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_confirm_ctr_cand")
	private LocalDate datConfirmCtrCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_ctr_cand", nullable=false)
	@NotNull
	private LocalDateTime datCreCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_deb_depot_ctr_cand", nullable=false)
	@NotNull
	private LocalDate datDebDepotCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_fin_depot_ctr_cand", nullable=false)
	@NotNull
	private LocalDate datFinDepotCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_jury_ctr_cand")
	private LocalDate datJuryCtrCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_ctr_cand", nullable=false)
	@NotNull
	private LocalDateTime datModCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_publi_ctr_cand")
	private LocalDate datPubliCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name="dat_retour_ctr_cand", nullable=false)
	@NotNull
	private LocalDate datRetourCtrCand;

	@Column(name="tem_demat_ctr_cand", nullable=false)
	@NotNull
	private Boolean temDematCtrCand;

	@Column(name="lib_ctr_cand", nullable=false, length=200)
	@Size(max = 200) 
	@NotNull
	private String libCtrCand;

	@Column(name="tem_list_comp_ctr_cand", nullable=false)
	@NotNull
	private Boolean temListCompCtrCand;
	
	@Column(name="tem_send_mail_ctr_cand", nullable=false)
	@NotNull
	private Boolean temSendMailCtrCand;
	
	@Column(name="nb_max_voeux_ctr_cand", nullable=false)
	@NotNull
	private Integer nbMaxVoeuxCtrCand;
	
	@Column(name="info_comp_ctr_cand", length=500)
	@Size(max = 500) 
	private String infoCompCtrCand;

	@Column(name="tes_ctr_cand", nullable=false)
	@NotNull
	private Boolean tesCtrCand;

	@Column(name="user_cre_ctr_cand", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String userCreCtrCand;

	@Column(name="user_mod_ctr_cand", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String userModCtrCand;

	//bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name="id_typ_dec_fav_list_comp", nullable=true)
	private TypeDecision typeDecisionFavListComp;

	//bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name="id_typ_dec_fav", nullable=false)
	@NotNull
	private TypeDecision typeDecisionFav;

	//bi-directional many-to-one association to Commission
	@OneToMany(mappedBy="centreCandidature")
	private List<Commission> commissions;

	//bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy="centreCandidature")
	private List<PieceJustif> pieceJustifs;
	
	//bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy="centreCandidature")
	private List<Formulaire> formulaires;
	
	//bi-directional many-to-one association to Gestionnaire
	@OneToMany(mappedBy="centreCandidature",cascade=CascadeType.ALL)
	private List<Gestionnaire> gestionnaires;

	@PrePersist
	private void onPrePersist() {
		this.datCreCtrCand = LocalDateTime.now();
		this.datModCtrCand = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datModCtrCand = LocalDateTime.now();
	}
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codCtrCand+"/"+this.libCtrCand;
	}

	public CentreCandidature() {
		super();
	}
	
	public CentreCandidature(String user, TypeDecision typeDecDefault, Integer nbVoeuxDefaut, Boolean temDemat) {
		super();
		this.userCreCtrCand = user;
		this.userModCtrCand = user;
		this.typeDecisionFavListComp = null;
		this.typeDecisionFav = typeDecDefault;
		this.nbMaxVoeuxCtrCand = nbVoeuxDefaut;
		this.tesCtrCand = false;
		this.temListCompCtrCand = false;
		this.temDematCtrCand = temDemat;
		this.datDebDepotCtrCand = LocalDate.now();
		this.datFinDepotCtrCand = LocalDate.now();
		this.datRetourCtrCand = LocalDate.now();
		
	}

}