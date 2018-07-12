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
package fr.univlorraine.ecandidat.vaadin.components;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchRun;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/** Bar de gestion de batch run
 *
 * @author Kevin Hergalant */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class BatchRunLayout extends Panel implements EntityPushListener<BatchRun> {

	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient BatchController batchController;
	@Resource
	private transient EntityPusher<BatchRun> batchRunEntityPusher;

	private Label labelInfo = new Label("", ContentMode.HTML);

	public BatchRunLayout() {
		setWidth(100, Unit.PERCENTAGE);

		HorizontalLayout hlInfo = new HorizontalLayout();
		hlInfo.setMargin(true);
		hlInfo.setSpacing(true);

		hlInfo.addComponent(labelInfo);
		hlInfo.setComponentAlignment(labelInfo, Alignment.MIDDLE_LEFT);
		setContent(hlInfo);
		refreshDetail();

		/* Inscrit la vue aux mises à jour de batchs */
		batchRunEntityPusher.registerEntityPushListener(this);
	}

	/** Rafraichi les données de lancement */
	private void refreshDetail() {
		labelInfo.setValue(batchController.getInfoRun());
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		/* Inscrit le panel aux mises à jour de batch run */
		batchRunEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object) */
	@Override
	public void entityPersisted(final BatchRun entity) {
		refreshDetail();
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object) */
	@Override
	public void entityUpdated(final BatchRun entity) {
		refreshDetail();
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object) */
	@Override
	public void entityDeleted(final BatchRun entity) {
	}

}
