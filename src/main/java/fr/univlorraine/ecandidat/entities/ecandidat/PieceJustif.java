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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the piece_justif database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "piece_justif")
@Data
@EqualsAndHashCode(of = "idPj")
@ToString(of = {"idPj", "codPj", "libPj", "codApoPj", "tesPj", "orderPj"})
@SuppressWarnings("serial")
public class PieceJustif implements Serializable, Comparable<PieceJustif> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pj", nullable = false)
	private Integer idPj;

	@Column(name = "cod_pj", unique = true, nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codPj;

	@Column(name = "lib_pj", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	private String libPj;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_pj", nullable = false)
	@NotNull
	private LocalDateTime datCrePj;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_pj", nullable = false)
	@NotNull
	private LocalDateTime datModPj;

	@Column(name = "tem_commun_pj", nullable = false)
	@NotNull
	private Boolean temCommunPj;

	@Column(name = "tem_unicite_pj", nullable = false)
	@NotNull
	private Boolean temUnicitePj;

	@Column(name = "tem_conditionnel_pj", nullable = false)
	@NotNull
	private Boolean temConditionnelPj;

	@Column(name = "tes_pj", nullable = false)
	@NotNull
	private Boolean tesPj;

	@Column(name = "cod_apo_pj", nullable = true, length = 5)
	@Size(max = 5)
	private String codApoPj;

	@Column(name = "order_pj", nullable = true)
	private Integer orderPj;

	@Column(name = "user_cre_pj", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCrePj;

	@Column(name = "user_mod_pj", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModPj;

	// bi-directional many-to-many association to Formation
	@ManyToMany(mappedBy = "pieceJustifs")
	private List<Formation> formations;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand")
	private CentreCandidature centreCandidature;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "cod_typ_trait")
	private TypeTraitement typeTraitement;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_lib_pj")
	@NotNull
	private I18n i18nLibPj;

	// bi-directional many-to-one association to Fichier
	@OneToOne
	@JoinColumn(name = "id_fichier")
	private Fichier fichier;

	// bi-directional many-to-one association to PjCand
	@OneToMany(mappedBy = "pieceJustif")
	private List<PjCand> pjCands;

	@PrePersist
	private void onPrePersist() {
		this.datCrePj = LocalDateTime.now();
		this.datModPj = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModPj = LocalDateTime.now();
	}

	public PieceJustif() {
		super();
	}

	public PieceJustif(final String currentUserName) {
		super();
		this.userCrePj = currentUserName;
		this.userModPj = currentUserName;
		this.tesPj = false;
		this.temCommunPj = false;
		this.temConditionnelPj = false;
	}

	@Override
	public int compareTo(final PieceJustif pj) {
		Integer orderThis = this.orderPj;
		Integer orderOther = pj.getOrderPj();
		if (orderThis == null && orderOther == null) {
			return this.getLibPj().compareTo(pj.getLibPj());
		} else if (orderThis == null && orderOther != null) {
			return 1;
		} else if (orderThis != null && orderOther == null) {
			return -1;
		} else {
			return orderThis.compareTo(orderOther);
		}
	}

}
