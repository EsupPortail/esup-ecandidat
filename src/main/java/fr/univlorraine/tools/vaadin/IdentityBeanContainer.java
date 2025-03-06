/**
 *
 *  ESUP-Portail MONDOSSIERWEB - Copyright (c) 2016 ESUP-Portail consortium
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
 *
 */
package fr.univlorraine.tools.vaadin;


import com.vaadin.v7.data.util.BeanContainer;

/**
 * BeanContainer utilisant l'IdentityBeanIdResolver.
 * @author Adrien Colson
 * @param <BEANTYPE> type de bean
 */
@SuppressWarnings("serial")
public class IdentityBeanContainer<BEANTYPE> extends BeanContainer<Integer, BEANTYPE> {

	/**
	 * Constructeur.
	 * @param type type de bean
	 */
	public IdentityBeanContainer(final Class<? super BEANTYPE> type) {
		super(type);
		setBeanIdResolver((BeanIdResolver<Integer, BEANTYPE>) new IdentityBeanIdResolver<BEANTYPE>());
	}

}
