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

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import fr.univlorraine.ecandidat.controllers.UserController;


/**
 * UserDetailMapper perso
 * @author Kevin Hergalant
 *
 */
public class SecurityUserDetailMapper implements UserDetailsContextMapper{

	@Resource
	private transient UserController userController;
	
	/**
	 * populate l'utilisateur avec les vues autorisées
	 */
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx,String username, Collection<? extends GrantedAuthority> authorities) {
		return new SecurityUser(username, username,new ArrayList<GrantedAuthority>());
	}
	
	public UserDetails mapUserFromContext(String username) {
	    return userController.getSecurityUser(username);
	}
	
	/**
     * The names of any attributes in the user's  entry which represent application
     * roles. These will be converted to <tt>GrantedAuthority</tt>s and added to the
     * list in the returned LdapUserDetails object. The attribute values must be Strings by default.
     *
     * @param roleAttributes the names of the role attributes.
     */
    public void setRoleAttributes(String[] roleAttributes) {
        //this.roleAttributes = roleAttributes;
    }

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// TODO Auto-generated method stub
		
	}

}
