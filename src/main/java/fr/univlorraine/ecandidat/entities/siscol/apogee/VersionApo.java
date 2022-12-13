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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 * The persistent class for the VERSION_APO database table.
 */
@Entity
@Table(name = "VERSION_APO")
@Data
@SuppressWarnings("serial")
public class VersionApo implements Serializable {

	@EmbeddedId
	private VersionApoPK id;

	@Temporal(TemporalType.DATE)
	@Column(name = "DAT_CRE")
	private Date datCre;

	@Column(name = "LIB_COM")
	private String libCom;

	@Column(name = "TEM_BASE")
	private String temBase;

	@Column(name = "TEM_EN_SVE_VER")
	private String temEnSveVer;
}
