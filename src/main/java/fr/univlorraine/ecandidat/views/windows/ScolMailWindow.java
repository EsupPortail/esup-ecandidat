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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;

/**
 * Fenêtre d'édition de mail
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ScolMailWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -568671209349208768L;

	public static final String[] MAIL_FIELDS_ORDER = {Mail_.codMail.getName(), Mail_.tesMail.getName(), Mail_.libMail.getName(),Mail_.typeAvis.getName(), Mail_.i18nSujetMail.getName(), Mail_.i18nCorpsMail.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MailController mailController;
	
	private PopupView pupSpec;
	private HorizontalLayout hlVar;

	/* Composants */
	private CustomBeanFieldGroup<Mail> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de mail
	 * @param mail la mail à éditer
	 */
	public ScolMailWindow(Mail mail) {
		/* Style */
		setModal(true);
		setWidth(850,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("mail.window", null, UI.getCurrent().getLocale()));
		
		/*Variables de mail*/
		hlVar = new HorizontalLayout();
		hlVar.setSpacing(true);
		hlVar.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(hlVar);
		
		String varMailGen = mailController.getVarMailCandidature(mail.getCodMail());
		if (varMailGen!=null){
			hlVar.addComponent(new PopupView(applicationContext.getMessage("mail.window.var.title.gen", null, UI.getCurrent().getLocale()),getVarLegendGeneralLayout(varMailGen, true)));
		}
		
		String varMailSpecifiques = mailController.getVarMail(mail);
		if (varMailSpecifiques!=null){
			pupSpec = new PopupView(applicationContext.getMessage("mail.window.var.title.spe", null, UI.getCurrent().getLocale()),getVarLegendGeneralLayout(varMailSpecifiques, false));
			hlVar.addComponent(pupSpec);
		}
		

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Mail.class);
		fieldGroup.setItemDataSource(mail);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		//formLayout.setSizeUndefined();
		for (String fieldName : MAIL_FIELDS_ORDER) {
			String caption = applicationContext.getMessage("mail.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(Mail_.typeAvis.getName())){
				if (mail.getTypeAvis()!=null){
					field = fieldGroup.buildAndBind(caption, fieldName, true);
					if (mail.getTemIsModeleMail()){
						field.setEnabled(false);
					}
					if (mail.getIdMail()!=null){
						field.setEnabled(false);
					}
					formLayout.addComponent(field);
					
					@SuppressWarnings("unchecked")
					RequiredComboBox<TypeAvis> fieldTa = (RequiredComboBox<TypeAvis>)field;
					fieldTa.setImmediate(true);
					fieldTa.addValueChangeListener(e->{
						if (e.getProperty().getValue() instanceof TypeAvis){
							TypeAvis ta = (TypeAvis)e.getProperty().getValue() ;
							mail.setTypeAvis(ta);
							String var = mailController.getVarMail(mail);
							hlVar.removeComponent(pupSpec);
							if (var!=null){								
								pupSpec = new PopupView(applicationContext.getMessage("mail.window.var.title.spe", null, UI.getCurrent().getLocale()),getVarLegendGeneralLayout(var, false));
								hlVar.addComponent(pupSpec);								
							}
							
						}
					});
				}				
			}else{
				field = fieldGroup.buildAndBind(caption, fieldName);
				field.setWidth(100, Unit.PERCENTAGE);
				formLayout.addComponent(field);
			}			
		}

		if (mail.getTemIsModeleMail()){
			fieldGroup.getField(Mail_.codMail.getName()).setEnabled(false);
			fieldGroup.getField(Mail_.libMail.getName()).setEnabled(false);
			fieldGroup.getField(Mail_.tesMail.getName()).setEnabled(false);
		}
		
		((I18nField)fieldGroup.getField(Mail_.i18nSujetMail.getName())).addCenterListener(e-> {if(e){center();}});
		((I18nField)fieldGroup.getField(Mail_.i18nCorpsMail.getName())).addCenterListener(e-> {if(e){center();}});

		layout.addComponent(formLayout);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/*Si le code de profil existe dejà --> erreur*/
				if (!mailController.isCodMailUnique((String) fieldGroup.getField(Mail_.codMail.getName()).getValue(), mail.getIdMail())){
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}				
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la mail saisie */
				mailController.saveMail(mail);
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	/** Layout pour les variables
	 * @param varMail
	 * @return
	 */
	/*private VerticalLayout getVarLegendSpecificLayout(String varMail){
		VerticalLayout vl = new VerticalLayout();
		vl.setWidth(300, Unit.PIXELS);
		vl.setMargin(true);
		vl.setSpacing(true);
		
		Label labelTitle = new Label(applicationContext.getMessage("mail.window.var", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(StyleConstants.VIEW_SUBTITLE);
		vl.addComponent(labelTitle);
		
		String txt = "<ul>";
		String[] tabSplit = varMail.split(";");
		for (String property : tabSplit){
			String propRegEx = "${"+property+"}";
			txt += "<li><input type='text' value='"+propRegEx+"'></li>";
		}
		txt += "</ul>";
		
		Label labelSearch = new Label(txt,ContentMode.HTML);
		
		vl.addComponent(labelSearch);
		return vl;
	}*/
	
	private VerticalLayout getVarLegendGeneralLayout(String varMail, Boolean withTitle){
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		
		Label labelTitle = new Label(applicationContext.getMessage("mail.window.var", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(StyleConstants.VIEW_SUBTITLE);
		vl.addComponent(labelTitle);
		
		HorizontalLayout hlContent = new HorizontalLayout();
		hlContent.setSpacing(true);
		vl.addComponent(hlContent);
		
		List<String> listeGen = new ArrayList<String>();
		List<String> listeCandidat = new ArrayList<String>();
		List<String> listeFormation = new ArrayList<String>();
		List<String> listeCommission = new ArrayList<String>();
		List<String> listeDossier = new ArrayList<String>();
		
		String[] tabSplit = varMail.split(";");
		for (String property : tabSplit){
			if (property.startsWith("candidat.")){
				listeCandidat.add(property);
			}else if (property.startsWith("formation.")){
				listeFormation.add(property);
			}else if (property.startsWith("commission.")){
				listeCommission.add(property);
			}else if (property.startsWith("dossier.")){
				listeDossier.add(property);
			}else{
				listeGen.add(property);
			}
		}
		String titleGen = null;
		
		if (withTitle){
			titleGen = applicationContext.getMessage("mail.window.var.title.general", null, UI.getCurrent().getLocale());
		}else{
			titleGen = applicationContext.getMessage("mail.window.var.title.specifique", null, UI.getCurrent().getLocale());			
		}
		
		getVarLayout(titleGen,listeGen,hlContent);
		getVarLayout(applicationContext.getMessage("mail.window.var.title.candidat", null, UI.getCurrent().getLocale()),listeCandidat,hlContent);
		getVarLayout(applicationContext.getMessage("mail.window.var.title.formation", null, UI.getCurrent().getLocale()),listeFormation,hlContent);
		getVarLayout(applicationContext.getMessage("mail.window.var.title.commission", null, UI.getCurrent().getLocale()),listeCommission,hlContent);
		getVarLayout(applicationContext.getMessage("mail.window.var.title.dossier", null, UI.getCurrent().getLocale()), listeDossier, hlContent);
		return vl;
	}
	
	private void getVarLayout(String title, List<String> liste, HorizontalLayout hlContent){
		if (liste==null || liste.size()==0){
			return;
		}
		
		VerticalLayout vl = new VerticalLayout();
		if (title!=null){
			Label labelTitle = new Label(title);
			vl.addComponent(labelTitle);
			vl.setComponentAlignment(labelTitle, Alignment.MIDDLE_CENTER);
		}
		
		StringBuilder txt = new StringBuilder("<ul>");
		liste.forEach(e->txt.append("<li><input type='text' value='${"+e+"}'></li>"));
		txt.append("</ul>");
		Label labelSearch = new Label(txt.toString(),ContentMode.HTML);
		
		vl.addComponent(labelSearch);
		hlContent.addComponent(vl);
	}
	
}
