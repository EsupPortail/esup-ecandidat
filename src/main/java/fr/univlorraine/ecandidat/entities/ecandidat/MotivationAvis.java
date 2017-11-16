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
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;


/**
 * The persistent class for the motivation_avis database table.
 * 
 */
@Entity
@Table(name="motivation_avis") @EntityListeners(EntityPushEntityListener.class)
@Data @EqualsAndHashCode(of="idMotiv")
@ToString(of={"idMotiv", "codMotiv", "libMotiv", "tesMotiv"})
public class MotivationAvis implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_motiv", nullable=false)
	private Integer idMotiv;
	
	@Column(name="cod_motiv", unique=true, nullable=false, length=20)
	@Size(max = 20) 
	@NotNull
	private String codMotiv;
	
	@Column(name="lib_motiv", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String libMotiv;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_cre_motiv", nullable=false)
	@NotNull
	private LocalDateTime datCreMotiv;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name="dat_mod_motiv", nullable=false)
	@NotNull
	private LocalDateTime datModMotiv;

	@Column(name="tes_motiv", nullable=false)
	@NotNull
	private Boolean tesMotiv;

	@Column(name="user_cre_motiv", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String userCreMotiv;

	@Column(name="user_mod_motiv", nullable=false, length=50)
	@Size(max = 50) 
	@NotNull
	private String userModMotiv;

	//bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name="id_i18n_lib_motiv", nullable=false)
	@NotNull
	private I18n i18nLibMotiv;

	//bi-directional many-to-one association to TypeDecisionCandidature
	@OneToMany(mappedBy="motivationAvis")
	private List<TypeDecisionCandidature> typeDecisionCandidatures;
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codMotiv+"/"+this.libMotiv;
	}
	
	@PrePersist
	private void onPrePersist() {
		this.datCreMotiv = LocalDateTime.now();
		this.datModMotiv = LocalDateTime.now();
	}
	
	@PreUpdate
	private void onPreUpdate() {
		this.datModMotiv = LocalDateTime.now();
	}
	
	public MotivationAvis() {
		super();
	}

	public MotivationAvis(String user) {
		super();
		this.userCreMotiv = user;
		this.userModMotiv = user;
		this.tesMotiv = false;
	}

}