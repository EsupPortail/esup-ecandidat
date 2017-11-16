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

import com.vaadin.ui.ComboBox;

import lombok.Data;

/** Class de presentation des filtres de combo de grid
 * @author Kevin
 *
 */
@Data
public class ComboBoxFilterPresentation {
	private String property;
	private ComboBox cb;
	private String libNull;
	
	public ComboBoxFilterPresentation(String property, ComboBox cb, String libNull) {
		super();
		this.property = property;
		this.cb = cb;
		this.libNull = libNull;
	}
	
	
}
