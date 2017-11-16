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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.univlorraine.ecandidat.controllers.UserController;

/**
 * Contrôleur REST principal
 */
@RestController
public class MainRest {

	@Resource
	private UserController userController;

	/**
	 * Affiche un message par défaut
	 * @return un message donnant la liste des services REST disponibles
	 */
	@RequestMapping
	public String getRoot() {
		return "Services REST disponibles : /user";
	}

	/**
	 * Renvoie l'utilisateur courant
	 * @return une réponse au format JSON
	 */
	@RequestMapping("/user")
	public Map<String, String> getUser() {
		Map<String, String> currentUser = new HashMap<>();
		currentUser.put("username", userController.getCurrentUserLogin());
		currentUser.put("roles", userController.getCurrentAuthentication().getAuthorities().toString());
		return currentUser;
	}

}
