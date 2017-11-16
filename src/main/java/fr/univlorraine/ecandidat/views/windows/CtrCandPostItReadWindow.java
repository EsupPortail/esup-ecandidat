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
package fr.univlorraine.ecandidat.views.windows;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt_;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionCandidatureWindow.ChangeCandidatureWindowListener;

/**
 * Fenêtre de visu des PostIt d'une candidature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandPostItReadWindow extends Window{
	

	/** serialVersionUID **/
	private static final long serialVersionUID = -7776558654950981770L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatureCtrCandController candidatureCtrCandController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient UserController userController;
	
	public static final String[] FIELDS_ORDER = {
		PostIt_.datCrePostIt.getName(),
		PostIt_.userPostIt.getName(),
		PostIt_.messagePostIt.getName()};
	
	/* Composants */

	private OneClickButton btnClose;
	

	/**
	 * Crée une fenêtre de visu de l'histo des décisions d'une candidature
	 * @param candidature la candidature à éditer
	 * @param changeCandidatureWindowListener 
	 */
	public CtrCandPostItReadWindow(Candidature candidature, List<DroitFonctionnalite> listeDroit, ChangeCandidatureWindowListener changeCandidatureWindowListener) {
		/* Style */
		setModal(true);
		setWidth(100,Unit.PERCENTAGE);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("postit.read.window",  new Object[]{candidatController.getLibelleTitle(candidature.getCandidat().getCompteMinima()),candidature.getFormation().getLibForm()}, UI.getCurrent().getLocale()));
		
		BeanItemContainer<PostIt> container = new BeanItemContainer<PostIt>(PostIt.class, candidatureCtrCandController.getPostIt(candidature));
		TableFormating postItTable = new TableFormating(null, container);
		postItTable.setSizeFull();
		postItTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			postItTable.setColumnHeader(fieldName, applicationContext.getMessage("postit.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		postItTable.addGeneratedColumn(PostIt_.messagePostIt.getName(), new ColumnGenerator() {
			/**serialVersionUID*/
			private static final long serialVersionUID = 1293198438034771892L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final PostIt postIt = (PostIt) itemId;
				Label label = new Label(postIt.getMessagePostIt());
				label.setDescription(postIt.getMessagePostIt());
				return label;
			}
		});
		postItTable.setSortContainerPropertyId(PostIt_.datCrePostIt.getName());
		postItTable.setColumnWidth(PostIt_.datCrePostIt.getName(), 180);
		postItTable.setColumnWidth(PostIt_.userPostIt.getName(), 180);
		postItTable.setSortAscending(false);
		postItTable.setColumnCollapsingAllowed(true);
		postItTable.setColumnReorderingAllowed(true);
		postItTable.setSelectable(false);
		postItTable.setImmediate(true);
		layout.addComponent(postItTable);
		layout.setExpandRatio(postItTable, 1);
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);
		
		/*Verification que l'utilisateur a le droit d'ecrire un postit*/		
		if (droitProfilController.hasAccessToFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT, listeDroit, false)){
			OneClickButton btnWrite = new OneClickButton(applicationContext.getMessage("postit.add.button", null, UI.getCurrent().getLocale()), FontAwesome.EDIT);
			btnWrite.addClickListener(e -> {
				CtrCandPostItAddWindow window = new CtrCandPostItAddWindow(new PostIt(userController.getCurrentUserName(), candidature));
				window.addPostItWindowListener(p->{
					container.addItem(p);
					postItTable.sort();
					if (changeCandidatureWindowListener!=null){
						changeCandidatureWindowListener.addPostIt(p);
					}
				});
				UI.getCurrent().addWindow(window);
			});
			buttonsLayout.addComponent(btnWrite);
			buttonsLayout.setComponentAlignment(btnWrite, Alignment.MIDDLE_CENTER);
		}
		
		btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);
		
		/* Centre la fenêtre */
		center();
	}
}
