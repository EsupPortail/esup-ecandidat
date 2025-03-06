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

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the fichier database table.
 */
@Entity
@Table(name = "fichier")
@Data
@EqualsAndHashCode(of = "idFichier")
@SuppressWarnings("serial")
public class Fichier implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_fichier", nullable = false)
	private Integer idFichier;

	@Column(name = "auteur_fichier", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	private String auteurFichier;

	@Column(name = "cod_fichier", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	private String codFichier;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_fichier", nullable = false)
	@NotNull
	private LocalDateTime datCreFichier;

	@Column(name = "file_fichier", nullable = false, length = 1000)
	@NotNull
	@Size(max = 1000)
	private String fileFichier;

	@Column(name = "nom_fichier", nullable = false, length = 100)
	@NotNull
	@Size(max = 100)
	private String nomFichier;

	@Column(name = "typ_fichier", nullable = false, length = 1)
	@NotNull
	@Size(max = 1)
	private String typFichier;

	@Column(name = "typ_stockage_fichier", nullable = false, length = 1)
	@NotNull
	@Size(max = 1)
	private String typStockageFichier;

	// association to PieceJustif
	@OneToMany(mappedBy = "fichier")
	private List<PieceJustif> pieceJustifs;

	// association to Commission
	@OneToMany(mappedBy = "fichier")
	private List<Commission> commissions;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "fichier")
	private List<PjCand> pjCands;

	@PrePersist
	private void onPrePersist() {
		this.datCreFichier = LocalDateTime.now();
	}

	public Fichier(final String codFichier, final String fileFichier, final String nomFichier,
			final String typFichier, final String typStockageFichier, final String auteurFichier) {
		super();
		this.codFichier = codFichier;
		this.fileFichier = fileFichier;
		this.nomFichier = nomFichier;
		this.typFichier = typFichier;
		this.typStockageFichier = typStockageFichier;
		this.auteurFichier = auteurFichier;
	}

	public Fichier() {
		super();
	}

}
