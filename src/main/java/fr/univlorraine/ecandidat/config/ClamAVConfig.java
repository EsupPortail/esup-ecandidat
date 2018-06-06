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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.univlorraine.ecandidat.utils.CustomClamAVClient;

/** Configuration CLamAV
 *
 * @author Kevin Hergalant */
@Configuration
public class ClamAVConfig {

	private Logger logger = LoggerFactory.getLogger(ClamAVConfig.class);

	@Value("${clamAV.ip:}")
	private transient String clamAVHost;

	@Value("${clamAV.port:}")
	private transient Integer clamAVPort;

	@Value("${clamAV.timeout:}")
	private transient Integer clamAVTimeout;

	@Bean
	public CustomClamAVClient clamAVClientScanner() {
		if (clamAVHost == null || clamAVHost.equals("") || clamAVPort == null) {
			return null;
		}
		if (clamAVTimeout == null) {
			logger.info("Configuration de l'antivirus ClamAV : " + clamAVHost + ":" + clamAVPort);
			return new CustomClamAVClient(clamAVHost, clamAVPort);
		} else {
			logger.info("Configuration de l'antivirus ClamAV : " + clamAVHost + ":" + clamAVPort + " , timeout = " + clamAVTimeout + "ms");
			return new CustomClamAVClient(clamAVHost, clamAVPort, clamAVTimeout);
		}
	}
}
