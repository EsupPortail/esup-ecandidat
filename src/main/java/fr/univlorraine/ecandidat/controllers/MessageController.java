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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Message;
import fr.univlorraine.ecandidat.repositories.MessageRepository;
import fr.univlorraine.ecandidat.views.windows.ScolMessageWindow;

/**
 * Gestion de l'entité message
 * @author Kevin Hergalant
 */
@Component
public class MessageController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;	
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient MessageRepository messageRepository;
	
	/**
	 * @return liste des message
	 */
	public List<Message> getMessagesToCache() {
		return messageRepository.findAll();
	}
	
	/**
	 * @return un message
	 */
	public String getMessage(String code) {
		Optional<Message> msgOpt = cacheController.getMessages().stream().filter(e->e.getTesMsg() && e.getCodMsg().equals(code)).findFirst();
		if (msgOpt.isPresent()){
			Message msg = msgOpt.get();
			String message = i18nController.getI18nTraduction(msg.getI18nValMessage());
			if (message == null || message.equals("")){
				return null;
			}
			return message;
		}
		return null; 
	}
	
	/**
	 * Ouvre une fenêtre d'édition de message.
	 * @param message
	 */
	public void editMessage(Message message) {
		Assert.notNull(message, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(message, null)) {
			return;
		}
		ScolMessageWindow window = new ScolMessageWindow(message);
		window.addCloseListener(e->lockController.releaseLock(message));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un message
	 * @param message
	 */
	public void saveMessage(Message message) {
		Assert.notNull(message, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		

		/* Verrou */
		if (!lockController.getLockOrNotify(message, null)) {
			return;
		}
		message.setI18nValMessage(i18nController.saveI18n(message.getI18nValMessage()));
		message.setDatModMsg(LocalDateTime.now());
		message = messageRepository.saveAndFlush(message);
		cacheController.reloadMessages(true);
		lockController.releaseLock(message);
	}
}
