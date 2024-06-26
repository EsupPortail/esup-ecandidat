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
import java.time.LocalTime;
import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the type_decision_candidature database table. */
@Entity
@Table(name = "type_decision_candidature")
@Data
@EqualsAndHashCode(of = "idTypeDecCand")
@ToString(exclude = { "candidature" })
@SuppressWarnings("serial")
public class TypeDecisionCandidature implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_type_dec_cand", nullable = false)
	private Integer idTypeDecCand;

	@Column(name = "comment_type_dec_cand", length = 1000)
	@Size(max = 1000)
	private String commentTypeDecCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_type_dec_cand", nullable = false)
	@NotNull
	private LocalDateTime datCreTypeDecCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_valid_type_dec_cand")
	private LocalDateTime datValidTypeDecCand;

	@Column(name = "list_comp_rang_typ_dec_cand")
	private Integer listCompRangTypDecCand;

	@Column(name = "list_comp_rang_reel_typ_dec_cand")
	private Integer listCompRangReelTypDecCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "preselect_date_type_dec_cand")
	private LocalDate preselectDateTypeDecCand;

	@Column(name = "preselect_heure_type_dec_cand", nullable = true)
	@Convert(converter = LocalTimePersistenceConverter.class)
	private LocalTime preselectHeureTypeDecCand;

	@Column(name = "preselect_lieu_type_dec_cand", length = 100)
	@Size(max = 100)
	private String preselectLieuTypeDecCand;

	@Column(name = "tem_valid_type_dec_cand", nullable = false)
	@NotNull
	private Boolean temValidTypeDecCand;

	@Column(name = "tem_appel_type_dec_cand", nullable = false)
	@NotNull
	private Boolean temAppelTypeDecCand;

	@Column(name = "user_cre_type_dec_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreTypeDecCand;

	@Column(name = "user_valid_type_dec_cand", length = 50)
	@Size(max = 50)
	private String userValidTypeDecCand;

	// bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name = "id_cand", nullable = false)
	@NotNull
	private Candidature candidature;

	// bi-directional many-to-one association to MotivationAvis
	@ManyToOne
	@JoinColumn(name = "id_motiv")
	private MotivationAvis motivationAvis;

	// bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name = "id_typ_dec", nullable = false)
	@NotNull
	private TypeDecision typeDecision;

	@PrePersist
	private void onPrePersist() {
		if (datCreTypeDecCand == null) {
			datCreTypeDecCand = LocalDateTime.now();
		}
	}

	public TypeDecisionCandidature(final Candidature candidature, final TypeDecision typeDecision) {
		super();
		this.candidature = candidature;
		this.typeDecision = typeDecision;
	}

	public TypeDecisionCandidature() {
		super();
	}

	/** @return une Decision clone */
	public TypeDecisionCandidature cloneTypeDecisionCandidature() {
		final TypeDecisionCandidature tdc = new TypeDecisionCandidature();
		tdc.setCommentTypeDecCand(commentTypeDecCand);
		tdc.setListCompRangTypDecCand(listCompRangTypDecCand);
		tdc.setPreselectDateTypeDecCand(preselectDateTypeDecCand);
		tdc.setPreselectHeureTypeDecCand(preselectHeureTypeDecCand);
		tdc.setPreselectLieuTypeDecCand(preselectLieuTypeDecCand);
		tdc.setMotivationAvis(motivationAvis);
		tdc.setTypeDecision(typeDecision);
		return tdc;
	}

	/** @return une Decision clone */
	public TypeDecisionCandidature cloneCompletTypeDecisionCandidature() {
		final TypeDecisionCandidature tdc = cloneTypeDecisionCandidature();
		tdc.setDatCreTypeDecCand(datCreTypeDecCand);
		tdc.setDatValidTypeDecCand(datValidTypeDecCand);
		tdc.setTemValidTypeDecCand(temValidTypeDecCand);
		tdc.setTemAppelTypeDecCand(temAppelTypeDecCand);
		tdc.setUserCreTypeDecCand(userCreTypeDecCand);
		tdc.setUserValidTypeDecCand(userValidTypeDecCand);
		return tdc;
	}

	private static Comparator<Integer> nullSafeIntegerComparator = Comparator.nullsFirst(Integer::compareTo);
	private static Comparator<LocalDateTime> nullSafeLocalDateTimeComparator = Comparator.nullsFirst(LocalDateTime::compareTo);

	public static int compareForListComp(final TypeDecisionCandidature td1, final TypeDecisionCandidature td2) {
		final int compareByRang = nullSafeIntegerComparator.compare(td1.getListCompRangTypDecCand(), td2.getListCompRangTypDecCand());
		if (compareByRang == 0) {
			return nullSafeLocalDateTimeComparator.compare(td1.getDatValidTypeDecCand(), td2.getDatValidTypeDecCand());
		}
		return compareByRang;
	}
}
