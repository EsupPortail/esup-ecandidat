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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * The persistent class for the droit_profil database table.
 * 
 */
@Entity @EntityListeners(EntityPushEntityListener.class)
@Table(name="droit_profil")
@Data @EqualsAndHashCode(of="idProfil")
@ToString(of={"idProfil", "codProfil", "libProfil", "tesProfil"})
public class DroitProfil implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_profil", nullable=false)
	private Integer idProfil;

	@Column(name="cod_profil", unique=true, nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codProfil;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_profil", nullable=false)
	@NotNull
	private LocalDateTime datCreProfil;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_profil", nullable=false)
	@NotNull
	private LocalDateTime datModProfil;

	@Column(name="lib_profil", nullable=false, length=255)
	@Size(max = 255) 
	@NotNull
	private String libProfil;

	@Column(name="user_cre_profil", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String userCreProfil;

	@Column(name="user_mod_profil", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String userModProfil;
	
	@Column(name="typ_profil", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String typProfil;
	
	@Column(name="tem_updatable", nullable=false)
	@NotNull
	private Boolean temUpdatable;	
	
	@Column(name="tes_profil", nullable=false)
	@NotNull
	private Boolean tesProfil;

	//bi-directional many-to-one association to DroitProfilFonc
	@OneToMany(mappedBy="droitProfil", orphanRemoval=true,cascade = CascadeType.ALL)	
	private List<DroitProfilFonc> droitProfilFoncs;

	//bi-directional many-to-one association to DroitProfilInd
	@OneToMany(mappedBy="droitProfil")
	private List<DroitProfilInd> droitProfilInds;

	@PrePersist
	private void onPrePersist() {
		this.datCreProfil = LocalDateTime.now();
		this.datModProfil = LocalDateTime.now();
	}
	@PreUpdate
	private void onPreUpdate() {
		this.datModProfil = LocalDateTime.now();
	}
	public DroitProfil() {
		super();
		this.temUpdatable = true;
		this.tesProfil = true;
	}
	
	public DroitProfil(String user) {
		this();
		this.userCreProfil = user;
		this.userModProfil = user;
	}
	
	public DroitProfil(String codProfil, String libProfil,
			String userCreProfil, String userModProfil, String typProfil, 
			Boolean temUpdatable, Boolean tesProfil) {
		super();
		this.codProfil = codProfil;
		this.libProfil = libProfil;
		this.userCreProfil = userCreProfil;
		this.userModProfil = userModProfil;
		this.typProfil = typProfil;
		this.temUpdatable = temUpdatable;
		this.tesProfil = tesProfil;
	}
	
	/**
	 * @return true si le droit est un droit admin
	 */
	public Boolean isDroitProfilAdmin(){
		return this.typProfil.equals(NomenclatureUtils.TYP_DROIT_PROFIL_ADM);
	}
	
	/**
	 * @return true si le droit est un droit Gestionnaire CtrCand
	 */
	public Boolean isDroitProfilGestionnaireCtrCand(){
		return this.typProfil.equals(NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE);
	}
	
	/**
	 * @return true si le droit est un droit commission
	 */
	public Boolean isDroitProfilCommission(){
		return this.typProfil.equals(NomenclatureUtils.TYP_DROIT_PROFIL_COMMISSION);
	}
	
	/**
	 * @return true si le droit est un droit Gestionnaire candidat
	 */
	public Boolean isDroitProfilGestionnaireCandidat(){
		return this.typProfil.equals(NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT);
	}
	
	/**
	 * @return true si le droit est un droit Gestionnaire candidat Lecture seule
	 */
	public Boolean isDroitProfilGestionnaireCandidatLS(){
		return this.typProfil.equals(NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT_LS);
	}
	
	/** Ajoute une fonctionnalité à la liste
	 * @param droitProfilFonc
	 */
	public void addFonctionnalite(DroitProfilFonc droitProfilFonc) {
		if (new ArrayList<DroitProfilFonc>(this.droitProfilFoncs).stream().filter(s -> s.getId().equals(droitProfilFonc.getId())).findFirst()!=null){
			droitProfilFoncs.add(droitProfilFonc);
		}
	}
	
}