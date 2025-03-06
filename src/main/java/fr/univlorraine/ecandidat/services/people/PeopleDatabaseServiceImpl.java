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
package fr.univlorraine.ecandidat.services.people;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.controllers.IndividuController;

/**
 * Implementation du service database de people
 * @author Kevin Hergalant
 */
@Component(value = "peopleDatabaseServiceImpl")
@SuppressWarnings("serial")
public class PeopleDatabaseServiceImpl implements PeopleGenericService<People> {

	@Resource
	private transient IndividuController individuController;

	/**
	 * @see fr.univlorraine.ecandidat.services.people.PeopleGenericService#findByPrimaryKey(java.lang.String)
	 */
	@Override
	public People findByPrimaryKey(final String uid) {
		return null;
	}

	@Override
	public People findByPrimaryKeyWithException(final String uid) throws PeopleException {
		throw new PeopleException("ldap.no.config");
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.people.PeopleGenericService#findEntitiesByFilter(java.lang.String)
	 */
	@Override
	public List<People> findByFilter(final String filter) throws PeopleException {
		final List<People> list = new ArrayList<>();
		list.addAll(individuController.searchIndividuByFilter(filter.toLowerCase()).stream().map(People::new).collect(Collectors.toList()));
		list.addAll(individuController.searchInscriptionByFilter(filter.toLowerCase()).stream().map(People::new).collect(Collectors.toList()));

		return list.stream().distinct().sorted(Comparator.comparing(People::getDisplayName)).collect(Collectors.toList());
	}
}
