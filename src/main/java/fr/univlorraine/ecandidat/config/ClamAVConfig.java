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

import fi.solita.clamav.ClamAVClient;

/**
 * Configuration CLamAV
 * 
 * @author Kevin Hergalant
 */
@Configuration
public class ClamAVConfig {

	private Logger logger = LoggerFactory.getLogger(ClamAVConfig.class);
	
	@Value("${clamAV.ip:}")
	private transient String ip;
	
	@Value("${clamAV.port:}")
	private transient String portContext;
	
	@Value("${clamAV.timeout:}")
	private transient String timeoutContext;
	

	@Bean
	public ClamAVClient clamAVClientScanner() {
		Integer port = null;
		Integer timeout = null;
		if (portContext !=null){
			try{
				port = Integer.valueOf(portContext);			
			}catch (Exception e){}
		}
		if (timeoutContext !=null){
			try{
				timeout = Integer.valueOf(timeoutContext);			
			}catch (Exception e){}
		}
		if (ip==null || ip.equals("") || port==null){
			return null;
		}
		if (timeout==null){
			logger.info("Configuration de l'antivirus ClamAV : "+ip+":"+port);
			return new ClamAVClient(ip, port);
		}else{
			logger.info("Configuration de l'antivirus ClamAV : "+ip+":"+port+" , timeout = "+timeout+"ms");
			return new ClamAVClient(ip, port, timeout);
		}		
	}
}
