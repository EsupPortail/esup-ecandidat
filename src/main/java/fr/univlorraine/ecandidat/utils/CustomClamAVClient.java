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
/**
 *
 */
package fr.univlorraine.ecandidat.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import fi.solita.clamav.ClamAVClient;

/** @author Kevin */
public class CustomClamAVClient extends ClamAVClient {

	/** @param hostName
	 * @param port
	 * @param timeout
	 */
	public CustomClamAVClient(final String hostName, final int port, final int timeout) {
		super(hostName, port, timeout);
	}

	/** @param hostName
	 * @param port
	 */
	public CustomClamAVClient(final String hostName, final int port) {
		super(hostName, port);
	}

	@Override
	public byte[] scan(final byte[] in) throws IOException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(in)) {
			return super.scan(bis);
		}
	}
}
