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
package fr.univlorraine.ecandidat.controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.utils.bean.presentation.SessionPresentation;

/**
 * Gestion des verrous
 * @author Kevin Hergalant
 */
@Component
public class LockController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UiController uiController;

	/** Liste des verrous */
	private Map<Object, UI> locks = new ConcurrentHashMap<>();

	/**
	 * @param ui
	 * @return liste des verrous associés à l'ui
	 */
	public List<Object> getUILocks(UI ui) {		
		return locks.entrySet().stream()
			.filter(e -> e.getValue() == ui)
			.map(Entry::getKey)
			.collect(Collectors.toList());
	}

	/**
	 * Verrouille une ressource pour l'UI courante
	 * @param obj la ressource à verrouiller
	 * @return true si la ressource est bien verrouillée pour l'UI courante, false sinon
	 */
	private boolean getLock(Object obj) {
		Assert.notNull(obj, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		UI lockUI = locks.get(obj);
		if (lockUI instanceof UI && lockUI != UI.getCurrent() && uiController.isUIStillActive(lockUI)) {
			return false;
		}

		locks.put(obj, UI.getCurrent());
		return true;
	}

	/**
	 * Verrouille une ressource pour l'UI courante
	 * @param obj la ressource à verrouiller
	 * @param msgIfAlreadyLocked message affiché si la ressource est déjà verrouillée pour une autre UI. Si cette propriété vaut null, un message par défaut est affiché.
	 * @return true si la ressource est bien verrouillée pour l'UI courante, false sinon
	 */
	public boolean getLockOrNotify(Object obj, String msgIfAlreadyLocked) {
		boolean ret = getLock(obj);
		if (!ret) {
			if (msgIfAlreadyLocked == null || msgIfAlreadyLocked.isEmpty()) {
				msgIfAlreadyLocked = applicationContext.getMessage("lock.alreadyLocked", new Object[] {obj.getClass().getSimpleName(), getUserNameFromLock(obj)}, UI.getCurrent().getLocale());
			}
			Notification.show(msgIfAlreadyLocked, Notification.Type.WARNING_MESSAGE);
		}
		return ret;
	}

	/**
	 * Supprime un verrou
	 * @param obj
	 */
	public void removeLock(Object obj) {
		//UI ui = locks.get(obj);
		locks.remove(obj);
		//uiController.notifyUIUpdated(ui);
	}

	/**
	 * Rend un verrou, après avoir vérifié qu'il appartient à l'UI courante
	 * @param obj
	 */
	public void releaseLock(Object obj) {
		if (locks.get(obj) == UI.getCurrent()) {
			removeLock(obj);
		}
	}

	/**
	 * Retourne le nom de l'utilisateur pour le lock passé en paramètre
	 * @param obj
	 * @return userName
	 */
	private String getUserNameFromLock(Object obj){
		UI lockUi = locks.get(obj);
		if (lockUi != null) {
			SecurityContext securityContext = (SecurityContext) lockUi.getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			return securityContext.getAuthentication().getName();
		}
		return null;
	}

	/**
	 * @param lock
	 * @return un lock
	 */
	public Object getLockBySessionItem(SessionPresentation lock) {
		for(Entry<Object, UI> entry : locks.entrySet()) {
			Object cle = entry.getKey();
			if (lock.getId().equals(String.valueOf(System.identityHashCode(cle)))){
				return cle;
			}
		}
		return null;		
	}
}
