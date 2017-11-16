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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class extends the ByteArrayOutputStream by 
 * providing a method that returns a new ByteArrayInputStream
 * which uses the internal byte array buffer. This buffer
 * is not copied, so no additional memory is used. After
 * creating the ByteArrayInputStream the instance of the
 * ByteArrayInOutStream can not be used anymore.
 * <p>
 * The ByteArrayInputStream can be retrieved using <code>getInputStream()</code>.
 * @author Nick Russler
 */
public class ByteArrayInOutStream extends ByteArrayOutputStream {
	
    /**
     * Creates a new ByteArrayInOutStream. The buffer capacity is
     * initially 32 bytes, though its size increases if necessary.
     */
	public ByteArrayInOutStream() {
		super();
	}
	
    /**
     * Creates a new ByteArrayInOutStream, with a buffer capacity of
     * the specified size, in bytes.
     *
     * @param   size   the initial size.
     * @exception  IllegalArgumentException if size is negative.
     */
	public ByteArrayInOutStream(int size) {
		super(size);
	}

	/**
	 * Creates a new ByteArrayInputStream that uses the internal byte array buffer 
	 * of this ByteArrayInOutStream instance as its buffer array. The initial value 
	 * of pos is set to zero and the initial value of count is the number of bytes 
	 * that can be read from the byte array. The buffer array is not copied. This 
	 * instance of ByteArrayInOutStream can not be used anymore after calling this
	 * method.
	 * @return the ByteArrayInputStream instance
	 * @throws IOException 
	 */
	public ByteArrayInputStream getInputStream() throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(this.buf, 0, this.count);
		close();
		this.buf = null;
		return bis;
	}
	
	/**
	 * @return le tableau de Byte (ne l'efface pas)-->utile pour le scan
	 */
	public byte[] getByte(){
		return this.buf;
	}
}