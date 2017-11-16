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
package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;

import lombok.Data;

/**
 * La classe de fonctionnalité pour un user
 * La fonctionnalité peut avoir 3 valeur : n'a pas le droit, a le droit en ecriture, a le droit en lecture seule 
 * @author Kevin Hergalant
 *
 */
@Data
public class SecurityFonc implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = -18564191233882880L;
	
	private Droit droit;
	
	public SecurityFonc(Droit droit) {
		super();
		this.droit = droit;
	}
	
	/**
	 * @return true si la fonctionnalité n'a pas de droits
	 */
	public Boolean hasNoRight(){
		return droit.equals(Droit.NO_RIGHT);
	}
	
	/**
	 * @return true si la fonctionnalité a les droits en ecriture
	 */
	public Boolean isWrite(){
		return droit.equals(Droit.WRITE);
	}
	
	/**
	 * @return true si la fonctionnalité a les droits en lecture seule
	 */
	public Boolean isReadOnly(){
		return droit.equals(Droit.READ_ONLY);
	}
		
	/**
	 * La fonctionnalité peut avoir 3 valeur : 
	 * n'a pas le droit, a le droit en ecriture, a le droit en lecture seule
	 * @author Kevin
	 *
	 */
	public enum Droit {
	    NO_RIGHT, WRITE, READ_ONLY  
	}
}
