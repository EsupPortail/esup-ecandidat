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
package fr.univlorraine.ecandidat.entities.siscol.apogee;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the OPI_PJ database table. */
@SuppressWarnings("serial")
@Entity
@Table(name = "OPI_PJ")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "codOpiIndEpo", "nomFic"})
public class OpiPj implements Serializable {

	@EmbeddedId
	private OpiPjPk id;

	@Column(name = "COD_OPI_INT_EPO")
	private String codOpiIndEpo;

	@Column(name = "NOM_FIC")
	private String nomFic;

	@Column(name = "COD_ETU")
	private String codEtu;
}
