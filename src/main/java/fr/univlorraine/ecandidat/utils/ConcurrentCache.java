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
package fr.univlorraine.ecandidat.utils;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentCache extends ConcurrentHashMap<String, Object>{

	/**
	 * 6147316839935933575L
	 */
	private static final long serialVersionUID = 6147316839935933575L;
	
	private Logger logger = LoggerFactory.getLogger(ConcurrentCache.class);

	public <T> void putToCache(String key, T value, Class<T> valueType) {
		logger.trace("Ajout des données de cache pour "+key);
		if (value!=null){
			put(key, value);
		}else{
			remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getFromCache(String key, Class<T> valueType) {
		logger.trace("Récupération des données de cache pour "+key);
		return (T) get(key);
	}
}
