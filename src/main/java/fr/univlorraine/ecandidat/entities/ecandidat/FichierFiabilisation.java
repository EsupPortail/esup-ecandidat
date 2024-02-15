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

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the fichier_fiabilisation database table.
 */
@Entity
@Table(name = "fichier_fiabilisation")
@Data
@EqualsAndHashCode(of = "idFichierFiab")
@SuppressWarnings("serial")
public class FichierFiabilisation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_fichier_fiab", nullable = false)
	private Integer idFichierFiab;

	@NotNull
	@Column(name = "id_fichier", nullable = false)
	private Integer idFichier;

	@Column(name = "id_pj")
	private Integer idPj;

	@Column(name = "id_cand")
	private Integer idCand;

	@Column(name = "id_comm")
	private Integer idComm;

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

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_fichier_fiab", nullable = false)
	@NotNull
	private LocalDateTime datCreFichierFiab;

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

	@PrePersist
	private void onPrePersist() {
		this.datCreFichierFiab = LocalDateTime.now();
	}

	public FichierFiabilisation(final Fichier fichier) {
		super();
		this.idFichier = fichier.getIdFichier();
		this.auteurFichier = fichier.getAuteurFichier();
		this.codFichier = fichier.getCodFichier();
		this.datCreFichier = fichier.getDatCreFichier();
		this.fileFichier = fichier.getFileFichier();
		this.nomFichier = fichier.getNomFichier();
		this.typFichier = fichier.getTypFichier();
		this.typStockageFichier = fichier.getTypStockageFichier();
	}

	public FichierFiabilisation() {
		super();
	}

}
