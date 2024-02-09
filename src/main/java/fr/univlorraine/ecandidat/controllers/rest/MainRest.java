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

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.services.people.PeopleException;

/**
 * Contrôleur REST principal
 */
@RestController
public class MainRest {

	@Resource
	private IndividuController individuController;

	@GetMapping("/sondes/liveness")
	public String getLiveness() {
		return "OK";
	}

	//@PostMapping(value = "/user/create/", consumes = MediaType.APPLICATION_JSON_VALUE)
	//@RequestMapping(value = "/user/create/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PostMapping(value = "/user/create/", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void saveUser(@RequestBody final RestUser user) {
		try {
			individuController.saveInscription(user);
		} catch (final PeopleException e) {
			throw new RuntimeException();
		}
	}
}
