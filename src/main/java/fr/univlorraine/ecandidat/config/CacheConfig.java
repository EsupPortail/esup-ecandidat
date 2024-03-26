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
package fr.univlorraine.ecandidat.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration du cache.
 * @author Kevin Hergalant
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

	//private final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
	public static final String CACHE_MANAGER_NAME = "cacheManager";
	public static final String CACHE_CONF_ETAB = "conf_etab";
	public static final String CACHE_CONF_RESSOURCE = "conf_ressource";

	@Override
	@Bean(name = "cacheManager")
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}

	/**
	 * @see org.springframework.cache.annotation.CachingConfigurerSupport#keyGenerator()
	 */
	@Override
	public KeyGenerator keyGenerator() {
		return (target, method, params) -> {
			final StringBuilder sbKey = new StringBuilder();
			sbKey.append(target.getClass().getName());
			sbKey.append("#" + method.getName());
			for (final Object param : params) {
				if (param != null) {
					sbKey.append("#" + param.toString());
				}
			}
			return sbKey.toString();
		};
	}
}
