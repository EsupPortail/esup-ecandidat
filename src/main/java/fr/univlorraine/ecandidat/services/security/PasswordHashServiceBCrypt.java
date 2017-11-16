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
package fr.univlorraine.ecandidat.services.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;

public class PasswordHashServiceBCrypt extends PasswordHashService{
	
	private Logger logger = LoggerFactory.getLogger(PasswordHashServiceBCrypt.class);	

	@Override
	public String createHash(String password) throws CustomException {
		logger.trace("Création du Hash avec BCrypt");
		String pwd = BCrypt.hashpw(password, BCrypt.gensalt());
		if (pwd.length()>150){
			throw new CustomException();
		}
		return pwd;
	}

	@Override
	public boolean validatePassword(String password, String correctHash) throws CustomException {
		logger.trace("Validation du mot de passe avec BCrypt");
		return BCrypt.checkpw(password, correctHash);
	}

	@Override
	public String getType() {
		return ConstanteUtils.GEN_PWD_TYPE_BCRYPT;
	}	
}
