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

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** 
 * Configuration Mail
 * 
 * @author Kevin Hergalant
 *
 */
@Configuration 
public class MailConfig {
	
	@Value("${mail.smtpHost:}")
	private transient String smtpHost;
	
	@Value("${mail.smtpPort:}")
	private transient String smtpPort;
	
	/**
	 * @return le service mail d'envoi
	 */
	@Bean
	public JavaMailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(smtpHost);
		String smtpPortContext = smtpPort;
		Integer smtpPort = 25;
		if (smtpPortContext !=null){
			try{
				smtpPort = Integer.valueOf(smtpPortContext);			
			}catch (Exception e){
			}
		}
		javaMailSender.setPort(smtpPort);
		javaMailSender.setJavaMailProperties(getMailProperties());

		return javaMailSender;
	}

	/**
	 * @return les properties d'envoi de mail
	 */
	private Properties getMailProperties() {
		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.smtp.auth", "false");
		properties.setProperty("mail.smtp.starttls.enable", "false");
		properties.setProperty("mail.debug", "false");
		return properties;
	}
}
