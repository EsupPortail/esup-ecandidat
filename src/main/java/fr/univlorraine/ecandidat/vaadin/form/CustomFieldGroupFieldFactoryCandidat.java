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
package fr.univlorraine.ecandidat.vaadin.form;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxBacOuEqu;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxLangue;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxCommune;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxEtablissement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;


/** FieldGroupFactory utilisé dans l'application pour le candidat
 * Permet d'utiliser le bon composant pour le bon type
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CustomFieldGroupFieldFactoryCandidat extends DefaultFieldGroupFieldFactory{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3606113268042858350L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient MailController mailController;

	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {	
		/*Le type du champs est un TextArea*/
		if (fieldType==RequiredTextArea.class){
			return fieldType.cast(new RequiredTextArea());
		}
		
		/*Le type du champs est un ComboBoxPresentation*/
		else if (fieldType==ComboBoxPresentation.class){			
			return fieldType.cast(new ComboBoxPresentation());
		}
		
		/*La valeur du champs est un LocalDate*/
		else if (dataType==LocalDate.class){
			final RequiredDateField field = new RequiredDateField();
			field.setImmediate(true);
			field.setConverter(new LocalDateToDateConverter());
			return fieldType.cast(field);
		}
		
		/*La valeur est siScolPays*/
		else if (dataType==SiScolPays.class){
			return fieldType.cast(new ComboBoxPays(cacheController.getListePays().stream().filter(e->e.getTemEnSvePay()).collect(Collectors.toList()),applicationContext.getMessage("infoperso.table.siScolPaysNaiss.suggest", null,  UI.getCurrent().getLocale())));
		}
		
		/*La valeur est siScolDepartement*/
		else if (dataType==SiScolDepartement.class){
			return fieldType.cast(new ComboBoxDepartement(cacheController.getListDepartement().stream().filter(e->e.getTemEnSveDep()).collect(Collectors.toList()),applicationContext.getMessage("infoperso.table.siScolDepartement.suggest", null,  UI.getCurrent().getLocale())));
		}
		
		/*La valeur est SiScolCommune*/
		else if (dataType==SiScolCommune.class){
			return fieldType.cast(new ComboBoxCommune(applicationContext.getMessage("adresse.commune.suggest", null,  UI.getCurrent().getLocale())));
		}
		
		/*La valeur est SiScolCommune*/
		else if (dataType==SiScolEtablissement.class){
			return fieldType.cast(new ComboBoxEtablissement(applicationContext.getMessage("infobac.siScolEtablissement.suggest", null,  UI.getCurrent().getLocale())));
		}
		
		/*La valeur est un SiScolBacOuxEqu*/
		else if (dataType==SiScolBacOuxEqu.class){
			return fieldType.cast(new ComboBoxBacOuEqu(cacheController.getListeBacOuxEqu().stream().filter(e->e.getTemEnSveBac()).collect(Collectors.toList())));
		}
		
		/*La valeur est un SiScolDipAutCur*/
		else if (dataType==SiScolDipAutCur.class){
			return fieldType.cast(new RequiredComboBox<SiScolDipAutCur>(cacheController.getListeDipAutCur().stream().filter(e->e.getTemEnSveDac()).collect(Collectors.toList()),SiScolDipAutCur.class));
		}
		
		/*La valeur est un SiScolMention*/
		else if (dataType==SiScolMention.class){
			return fieldType.cast(new RequiredComboBox<SiScolMention>(cacheController.getListeMention().stream().filter(e->e.getTemEnSveMen()).collect(Collectors.toList()),SiScolMention.class));
		}
		
		/*La valeur est un SiScolMentionNivBac*/
		else if (dataType==SiScolMentionNivBac.class){
			return fieldType.cast(new RequiredComboBox<SiScolMentionNivBac>(cacheController.getListeMentionNivBac().stream().filter(e->e.getTemEnSveMnb()).collect(Collectors.toList()),SiScolMentionNivBac.class));
		}
		
		/*La valeur est un type d'avis*/
		else if (dataType==Civilite.class){
			return fieldType.cast(new RequiredComboBox<Civilite>(cacheController.getListeCivilte(),Civilite.class));
		}
		
		/*La valeur est une langue*/
		else if (dataType==Langue.class){
			List<Langue> listeLangue = new ArrayList<Langue>();
			listeLangue.add(cacheController.getLangueDefault());
			listeLangue.addAll(cacheController.getLangueEnServiceWithoutDefault());
			return fieldType.cast(new ComboBoxLangue(listeLangue,true));
		}
		
		/*La valeur du champs est un LocalTime*/
		else if (dataType==LocalTime.class){
			return fieldType.cast(new LocalTimeField());
		}
		
		/*La valeur du champs est un Integer*/
		else if (dataType==Integer.class){		
			return fieldType.cast(new RequiredIntegerField());
		}
		
		/*La valeur du champs est une date*/
		else if (dataType==Date.class){
			return fieldType.cast(new RequiredDateField());
		}
		
		/*La valeur du champs est un Boolean*/
		else if (dataType==Boolean.class){
			return fieldType.cast(new RequiredCheckBox());
		}	
		
		/*Sinon, le champs est un simple TextField*/
		else{	
			return fieldType.cast(new RequiredTextField());
		}
	}
	
}
