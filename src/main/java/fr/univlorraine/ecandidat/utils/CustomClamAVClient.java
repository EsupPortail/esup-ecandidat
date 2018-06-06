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
