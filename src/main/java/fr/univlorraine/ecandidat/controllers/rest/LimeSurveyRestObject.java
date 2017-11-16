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
package fr.univlorraine.ecandidat.controllers.rest;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/**
 * Ojbet Rest de retour LimeSurvey
 * @author Kevin Hergalant
 *
 */
@Data
public class LimeSurveyRestObject {
	
	private String method;
	private Map<String,Object> params;
	private Integer id;
	
	public LimeSurveyRestObject(String method) {
		super();
		this.method = method;
		this.id = 1;
		params = new LinkedHashMap<String,Object>();
	}
	
	public void addParameter(String key, Object value){
		params.put(key,value);
	}
}
