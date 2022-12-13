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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the MENTION_NIV_BAC database table.
 */
@Entity
@Table(name = "MENTION_NIV_BAC")
@Data
@EqualsAndHashCode(of = "codMnb")
@SuppressWarnings("serial")
public class MentionNivBac implements Serializable {

	@Id
	@Column(name = "COD_MNB", unique = true, nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codMnb;

	@Column(name = "LIB_MNB", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libMnb;

	@Column(name = "LIC_MNB", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licMnb;

	@Column(name = "TEM_EN_SVE_MNB", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveMnb;
}
