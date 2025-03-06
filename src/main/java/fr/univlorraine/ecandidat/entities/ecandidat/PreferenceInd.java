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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** The persistent class for the preference_individu database table. */
@Entity
@Table(name = "preference_ind")
@Data
@EqualsAndHashCode(of = "individu")
@SuppressWarnings("serial")
public class PreferenceInd implements Serializable {

	@Id
	@Column(name = "login_ind")
	private String loginInd;

	@Lob
	@Column(name = "cand_col_visible_pref", nullable = true, columnDefinition = "TEXT")
	private String candColVisiblePref;

	@Lob
	@Column(name = "cand_col_order_pref", nullable = true, columnDefinition = "TEXT")
	private String candColOrderPref;

	@Lob
	@Column(name = "cand_col_sort_pref", nullable = true, columnDefinition = "TEXT")
	private String candColSortPref;

	@Column(name = "cand_col_frozen_pref", nullable = true)
	private Integer candColFrozenPref;

	@Column(name = "cand_id_comm_pref", nullable = true)
	private Integer candIdCommPref;

	@Lob
	@Column(name = "export_col_pref", nullable = true, columnDefinition = "TEXT")
	private String exportColPref;

	@Column(name = "export_tem_footer_pref", nullable = true)
	private Boolean exportTemFooterPref;

	@Column(name = "id_ctr_cand_pref", nullable = true)
	private Integer idCtrCandPref;

	@Column(name = "id_comm_pref", nullable = true)
	private Integer idCommPref;

	// bi-directional one-to-one association to Individu
	@OneToOne
	@JoinColumn(name = "login_ind", updatable = false, insertable = false)
	private Individu individu;

	public PreferenceInd() {
		super();
	}

	public PreferenceInd(final Individu individu) {
		super();
		this.loginInd = individu.getLoginInd();
		this.individu = individu;
	}
}
