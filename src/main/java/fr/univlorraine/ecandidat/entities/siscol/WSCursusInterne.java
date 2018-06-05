/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.entities.siscol;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class WSCursusInterne {
	@Id
	private String codVet;
	private String libVet;
	private String codAnu;
	private String codMen;
	private String codTre;
	private String notVet;

	public WSCursusInterne() {
		super();
	}

	public WSCursusInterne(final String codVet, final String libVet, final String codAnu,
			final String codMen, final String codTre, final String notVet) {
		super();
		this.codVet = codVet;
		this.libVet = libVet;
		this.codAnu = codAnu;
		this.codMen = codMen;
		this.codTre = codTre;
		this.notVet = notVet;
	}

	public WSCursusInterne(final String codVet, final String libVet, final String codAnu) {
		super();
		this.codVet = codVet;
		this.libVet = libVet;
		this.codAnu = codAnu;
	}
}
