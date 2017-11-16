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

import java.security.SecureRandom;
import java.util.Random;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;

public abstract class PasswordHashService {	
	
	/**
	 * @return l'implémentation courante
	 */
	public static PasswordHashService getCurrentImplementation(){
		return new PasswordHashServiceBCrypt();
	}
	
	/**
	 * @param type
	 * @return l'implementation par son code
	 */
	public static PasswordHashService getImplementation(String type){
		if (type.equals(ConstanteUtils.GEN_PWD_TYPE_PBKDF2)){
			return new PasswordHashServicePBKDF2();
		}else if (type.equals(ConstanteUtils.GEN_PWD_TYPE_BCRYPT)){
			return new PasswordHashServiceBCrypt();
		}
		return null;
	}
	
	/**
	 * @param length
	 * @param letters
	 * @return le pwd
	 */
	public String generateRandomPassword(Integer length, String letters){
		Random RANDOM = new SecureRandom();
		String pw = "";
		for (int i = 0; i < length; i++) {
			int index = (int) (RANDOM.nextDouble() * letters.length());
			pw += letters.substring(index, index + 1);
		}
		return pw;
	}
	
	/**
	 * @param password
	 * @return le hash
	 * @throws CustomException
	 */
	public abstract String createHash(String password) throws CustomException;
	
	/**
	 * @param password
	 * @param correctHash
	 * @return true si le pwd est correct
	 * @throws CustomException
	 */
	public abstract boolean validatePassword(String password, String correctHash) throws CustomException;
	
	/**
	 * @return le type d'implementation
	 */
	public abstract String getType();
}
