/**
 * ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the droit_fonctionnalite database table.
 *
 */
@Entity
@Table(name = "droit_fonctionnalite")
@Data
@EqualsAndHashCode(of = "codFonc")
@ToString(of = {"codFonc", "libFonc", "licFonc"})
public class DroitFonctionnalite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "cod_fonc", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codFonc;

	@Column(name = "lib_fonc", nullable = false, length = 255)
	@Size(max = 255)
	@NotNull
	private String libFonc;

	@Column(name = "lic_fonc", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String licFonc;

	@Column(name = "tem_open_com_fonc", nullable = false)
	@NotNull
	private Boolean temOpenComFonc;

	@Column(name = "order_fonc", nullable = false)
	@NotNull
	private Integer orderFonc;

	@Column(name = "tem_action_cand_fonc", nullable = false)
	@NotNull
	private Boolean temActionCandFonc;

	@Transient
	private Boolean readOnly;

	// bi-directional many-to-one association to DroitProfilFonc
	@OneToMany(mappedBy = "droitFonctionnalite")
	private List<DroitProfilFonc> droitProfilFoncs;

	public DroitFonctionnalite() {
		super();
	}

	public DroitFonctionnalite(final String codFonc, final String libFonc, final String licFonc,
			final Boolean temOpenComFonc, final Integer orderFonc, final Boolean temActionCandFonc) {
		super();
		this.codFonc = codFonc;
		this.libFonc = libFonc;
		this.temOpenComFonc = temOpenComFonc;
		this.licFonc = licFonc;
		this.orderFonc = orderFonc;
		this.temActionCandFonc = temActionCandFonc;
	}

	public DroitFonctionnalite(final String codFonc, final String libFonc, final Boolean temOpenComFonc,
			final Integer orderFonc, final Boolean temActionCandFonc) {
		super();
		this.codFonc = codFonc;
		this.libFonc = libFonc;
		this.temOpenComFonc = temOpenComFonc;
		this.licFonc = libFonc;
		this.orderFonc = orderFonc;
		this.temActionCandFonc = temActionCandFonc;
	}

	public DroitFonctionnalite(final String codFonc, final String libFonc, final Integer orderFonc,
			final Boolean temActionCandFonc) {
		super();
		this.codFonc = codFonc;
		this.libFonc = libFonc;
		this.temOpenComFonc = false;
		this.licFonc = libFonc;
		this.orderFonc = orderFonc;
		this.temActionCandFonc = temActionCandFonc;
	}

	public DroitFonctionnalite(final DroitFonctionnalite df, final Boolean readOnly) {
		super();
		this.codFonc = df.getCodFonc();
		this.libFonc = df.getLibFonc();
		this.temOpenComFonc = df.getTemOpenComFonc();
		this.licFonc = df.getLicFonc();
		this.orderFonc = df.getOrderFonc();
		this.temActionCandFonc = df.getTemActionCandFonc();
		this.readOnly = readOnly;
	}
}