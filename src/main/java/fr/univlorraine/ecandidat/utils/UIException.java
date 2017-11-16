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

/**
 * Exception provenant de l'UI
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class UIException extends RuntimeException{
    
    /**
	 * Constructeur
	 */
	public UIException() {
	}

	/** Constructeur avec message
	 * @param message
	 */
	public UIException(String message) {
		super(message);
	}

	/** Constructeur avec cause
	 * @param cause
	 */
	public UIException(Throwable cause) {
		super(cause);
	}

	/** Constructeur avec message et cause
	 * @param message
	 * @param cause
	 */
	public UIException(String message, Throwable cause) {
		super(message, cause);
	}
}
