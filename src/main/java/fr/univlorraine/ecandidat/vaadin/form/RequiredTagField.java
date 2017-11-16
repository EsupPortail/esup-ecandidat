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

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag_;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.StringColorToHtmlSquareConverter;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;


/**
 * Champs tag
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class RequiredTagField extends CustomField<Tag> implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8563251228788918500L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;
	
	public static final String[] FIELDS_ORDER = {Tag_.libTag.getName(),Tag_.colorTag.getName(),Tag_.tesTag.getName()};
	
	/*Variable pour le champs et les msg d'erreur*/
	private boolean shouldHideError = true;
	private String requieredError;
	
	private Tag tagSelected;
	
	private HorizontalLayout layout;
	
	private Image noTagImg = new Image(null, new ThemeResource("images/icon/Tag-No-tag.png"));
	
	private Label squareLabel = new Label("", ContentMode.HTML);
	
	private TextField txtLabel = new TextField();
	
	private OneClickButton addButton = new OneClickButton(FontAwesome.PLUS);
	private OneClickButton editButton = new OneClickButton(FontAwesome.PENCIL);
	private OneClickButton removeButton = new OneClickButton(FontAwesome.TRASH_O);
	
	/**
	 * Constructeur, initialisation du champs
	 */
	public RequiredTagField() {
		super();
		layout = new HorizontalLayout();
		layout.setSpacing(true);
		
		/*Boutons*/
		addButton.setDescription(applicationContext.getMessage("tag.btn.add", null, UI.getCurrent().getLocale()));
		editButton.setDescription(applicationContext.getMessage("tag.btn.edit", null, UI.getCurrent().getLocale()));
		removeButton.setDescription(applicationContext.getMessage("tag.btn.del", null, UI.getCurrent().getLocale()));
		
		layout.addComponent(noTagImg);
		layout.addComponent(squareLabel);
		layout.addComponent(txtLabel);
		layout.addComponent(addButton);
		layout.addComponent(editButton);
		layout.addComponent(removeButton);
		
		/*Alognement*/
		txtLabel.setWidth(100, Unit.PERCENTAGE);
		squareLabel.setWidth(22, Unit.PIXELS);
		layout.setExpandRatio(txtLabel, 1);
		layout.setComponentAlignment(noTagImg, Alignment.MIDDLE_LEFT);
		layout.setComponentAlignment(squareLabel, Alignment.MIDDLE_LEFT);
		
		List<Tag> listeTag = cacheController.getTagEnService();
		if (listeTag.size() == 0){
			return;
		}
		
		BeanItemContainer<Tag> containerTable = new BeanItemContainer<Tag>(Tag.class, listeTag);
		/*TableFormating table = new TableFormating(containerTable);
		table.setImmediate(true);*/
		Grid grid = new Grid(containerTable);
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setHeightMode(HeightMode.ROW);
		grid.setHeightByRows(listeTag.size());
		grid.setColumnOrder(Tag_.colorTag.getName(), Tag_.libTag.getName());
		grid.sort(Tag_.libTag.getName());
		grid.getColumns().forEach(e->{
			String prop = (String)e.getPropertyId();
			if (!prop.equals(Tag_.libTag.getName()) && !prop.equals(Tag_.colorTag.getName())){
				e.setHidden(true);
			}else{
				e.setHeaderCaption(applicationContext.getMessage("tag.table."+prop, null, UI.getCurrent().getLocale()));
			}			
		});
		grid.getColumn(Tag_.colorTag.getName()).setConverter(new StringColorToHtmlSquareConverter());
		grid.getColumn(Tag_.colorTag.getName()).setRenderer(new HtmlRenderer());
		grid.getColumn(Tag_.colorTag.getName()).setWidth(83);
		
		/* L'affichage est mauvais avec cette methode.., du coup passage en css*/
		//table.setPageLength(listeTag.size());
		
		/*table.setVisibleColumns(new Object[]{Tag_.colorTag.getName(), Tag_.libTag.getName()});
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		
		table.addGeneratedColumn(Tag_.colorTag.getName(), new ColumnGenerator() {
			private static final long serialVersionUID = -1200838100293506069L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Tag tag = (Tag) itemId;
				return new Label(MethodUtils.getHtmlColoredSquare(tag.getColorTag(), tag.getLibTag(), "margin:7px;"), ContentMode.HTML);
			}
		})*/;
		
		Window win = new Window(applicationContext.getMessage("tag.window.action.title", null, UI.getCurrent().getLocale()));
		win.setWidth(550, Unit.PIXELS);
		win.setClosable(true);
		win.setModal(true);
		win.setResizable(true);		
		
		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		win.setContent(layout);
		
		layout.addComponent(grid);
		
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		OneClickButton btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> win.close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_CENTER);
		
		grid.addItemClickListener(e->{
			win.close();
			changeFieldValue((Tag)e.getItemId());
		});
		
		/*PopupView popup = new PopupView(null, table);
		popup.setImmediate(true);
		table.addItemClickListener(e->{
			popup.setPopupVisible(false);
			changeFieldValue((Tag)e.getItemId());
		});
		
		popup.setHideOnMouseOut(false);*/
		
		addButton.addClickListener(e->{
			//popup.setPopupVisible(true);
			UI.getCurrent().addWindow(win);	
			win.center();
		});
		editButton.addClickListener(e->{
			UI.getCurrent().addWindow(win);
			win.center();
		});

		removeButton.addClickListener(e->{
			changeFieldValue(null);
		});
		
		//layout.addComponents(popup);
	}	
	

	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {		
		return layout;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<Tag> getType() {
		return Tag.class;
	}
	
	/** Change la valeur
	 * @param value
	 */
	private void changeFieldValue(Tag tag){
		tagSelected = tag;
		if (tag == null){
			noTagImg.setVisible(true);
			squareLabel.setVisible(false);
			addButton.setVisible(true);
			editButton.setVisible(false);
			removeButton.setVisible(false);
			txtLabel.setReadOnly(false);
			txtLabel.setValue("");
			txtLabel.setReadOnly(true);			
		}else{
			noTagImg.setVisible(false);
			squareLabel.setVisible(true);
			squareLabel.setValue(MethodUtils.getHtmlColoredSquare(tag.getColorTag(), tag.getLibTag(), 20, "margin-top:5px;"));
			addButton.setVisible(false);
			editButton.setVisible(true);
			removeButton.setVisible(true);
			txtLabel.setReadOnly(false);
			txtLabel.setValue(tag.getLibTag());	
			txtLabel.setReadOnly(true);
		}
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	protected void setInternalValue(Tag newFieldValue){
		super.setInternalValue(newFieldValue);
		changeFieldValue(newFieldValue);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Tag newFieldValue) throws ReadOnlyException,
			ConversionException {
		super.setInternalValue(newFieldValue);
		changeFieldValue(newFieldValue);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public Tag getValue() {
		return tagSelected;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected Tag getInternalValue() {
		return tagSelected;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#shouldHideErrors()
	 */
	@Override
	protected boolean shouldHideErrors() {
		Boolean hide = shouldHideError;
		shouldHideError = false;
		return hide;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return tagSelected==null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		shouldHideError = false;
		super.setRequiredError(this.requieredError);
		if (isEmpty()){
			fireValueChange(false);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(Boolean immediate) {
		setImmediate(immediate);
		super.setRequiredError(null);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String)
	 */
	@Override
	public void setRequiredError(String requiredMessage) {
		this.requieredError = requiredMessage;
	}


	/**
	 * Met la largeur du champs a 100%
	 */
	public void setWidthMax() {
		layout.setWidth(100, Unit.PERCENTAGE);
	}
}
