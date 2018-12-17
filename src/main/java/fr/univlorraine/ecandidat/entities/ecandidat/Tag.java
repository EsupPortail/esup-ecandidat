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
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** The persistent class for the Tag database table. */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "tag")
@Data
@EqualsAndHashCode(of = "idTag")
@SuppressWarnings("serial")
public class Tag implements Serializable, Comparable<Tag> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tag", nullable = false)
	private Integer idTag;

	@Column(name = "lib_tag", nullable = false, length = 255)
	@Size(max = 255)
	@NotNull
	private String libTag;

	@Column(name = "color_tag", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String colorTag;

	@Column(name = "tes_tag", nullable = false)
	@NotNull
	private Boolean tesTag;

	/** @return le libellé à afficher dans la listBox */
	public String getGenericLibelle() {
		return this.libTag;
	}

	/** @see java.lang.Comparable#compareTo(java.lang.Object) */
	@Override
	public int compareTo(final Tag o) {
		if (o == null) {
			return 1;
		} else {
			return o.getLibTag().compareTo(this.getLibTag());
		}
	}

}
