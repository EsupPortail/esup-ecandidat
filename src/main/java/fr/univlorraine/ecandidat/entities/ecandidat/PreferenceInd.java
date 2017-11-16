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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the preference_individu database table.
 * 
 */
@Entity
@Table(name="preference_ind")
@Data @EqualsAndHashCode(of="individu")
public class PreferenceInd implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="login_ind")
	private String loginInd;

	@Lob
	@Column(name="cand_col_visible_pref", nullable=true, columnDefinition="TEXT")
	private String candColVisiblePref;
	
	@Lob
	@Column(name="cand_col_order_pref", nullable=true, columnDefinition="TEXT")
	private String candColOrderPref;
	
	@Column(name="cand_col_sort_pref", nullable=true, length=100)
	@Size(max = 100) 
	private String candColSortPref;
	
	@Column(name="cand_col_sort_direction_pref", nullable=true, length=1)
	@Size(max = 1) 
	private String candColSortDirectionPref;
	
	@Column(name="cand_col_frozen_pref", nullable=true)
	private Integer candColFrozenPref;
	
	@Column(name="cand_id_comm_pref", nullable=true)
	private Integer candIdCommPref;
	
	@Lob
	@Column(name="export_col_pref", nullable=true, columnDefinition="TEXT")
	private String exportColPref;
	
	@Column(name="export_tem_footer_pref", nullable=true)
	private Boolean exportTemFooterPref;	
	
	@Column(name="id_ctr_cand_pref", nullable=true)
	private Integer idCtrCandPref;
	
	@Column(name="id_comm_pref", nullable=true)
	private Integer idCommPref;
	
	//bi-directional one-to-one association to Individu
	@OneToOne
	@JoinColumn(name="login_ind", updatable = false, insertable = false)
	private Individu individu;
	
	public PreferenceInd() {
		super();
	}
	
	public PreferenceInd(Individu individu) {
		super();
		this.loginInd = individu.getLoginInd();
		this.individu = individu;
	}	
}