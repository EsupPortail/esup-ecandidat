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
package fr.univlorraine.ecandidat.utils.bean.presentation;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data 
@EqualsAndHashCode(of={"id","type"})
public class SessionPresentation {
	
	public enum SessionType { USER, SESSION, UI, LOCK };
		
	private String id;
	private SessionType type;
	private String title;
	private String info;
	
	private String idParent;
	private SessionType typeParent;
	
	public SessionPresentation(String id, SessionType type) {
		super();
		this.id = id;
		this.type = type;
	}
	
	public SessionPresentation(String id, SessionType type, String idParent, SessionType typeParent) {
		super();
		this.id = id;
		this.type = type;
	}

	public SessionPresentation() {
		super();
	}
}
