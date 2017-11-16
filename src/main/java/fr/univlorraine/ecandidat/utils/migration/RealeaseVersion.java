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
package fr.univlorraine.ecandidat.utils.migration;

import java.util.StringTokenizer;

/** Classe utilitaire permettant de comparer des versions
 * @author Kevin Hergalant
 *
 */
public class RealeaseVersion implements Comparable<RealeaseVersion>{
	
	private int major = 0;
	private int minor = 0;
	private int micro = 0;
	private int micron = 0;
	
	/**
	 * @param version
	 */
	public RealeaseVersion(String version) {
		if (version == null) {
			throw new IllegalArgumentException("Version can not be null");
		} else if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
			throw new IllegalArgumentException("Invalid version format");
		}

		StringTokenizer st = new StringTokenizer(version,".");
		int i = 0;
		while (st.hasMoreTokens()) {
			Integer value = Integer.valueOf(st.nextToken());
			if (i==0){
				major = value;
			}else if(i==1){
				minor = value;
			}else if(i==2){
				micro = value;
			}else if(i==3){
				micron = value;
			}
			i++;
		}
	}
	
	/**
	 * @param version
	 * @return true si la version est inférieure à celle testée
	 */
	public Boolean isLessThan(RealeaseVersion version){
		if (this.compareTo(version)==-1){
			return true;
		}
		return false;
	}
	
	/**
	 * @param version
	 * @return true si la version est égale à celle testée
	 */
	public Boolean isEqualThan(RealeaseVersion version){
		if (this.compareTo(version)==0){
			return true;
		}
		return false;
	}
	
	/**
	 * @param version
	 * @return true si la version est suppérieur à celle testée
	 */
	public Boolean isGreatherThan(RealeaseVersion version){
		if (this.compareTo(version)==1){
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(RealeaseVersion that) {
		if (that == null){
			return 1;
		}
		
		if (this.major>that.major){
			return 1;
		}else if (this.major==that.major){
			if (this.minor>that.minor){
				return 1;
			}else if (this.minor==that.minor){
				if (this.micro>that.micro){
					return 1;
				}else if (this.micro==that.micro){
					if (this.micron>that.micron){
						return 1;
					}else if (this.micron==that.micron){
						return 0;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		return "RealeaseVersion [major=" + major + ", minor=" + minor + ", micro=" + micro + ", micron=" + micron + "]";
	}
}