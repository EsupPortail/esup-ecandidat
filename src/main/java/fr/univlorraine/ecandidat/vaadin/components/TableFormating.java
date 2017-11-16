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
package fr.univlorraine.ecandidat.vaadin.components;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty.MethodException;
import com.vaadin.ui.Table;

/** Table apportant un pattern aux format de date, de double, de boolean
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class TableFormating extends Table{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3460506751703160156L;
	
	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	private NumberFormat integerFormatter = new DecimalFormat("#");

	public TableFormating(String string,Container dataSource,DateTimeFormatter formatterDate, DateTimeFormatter formatterDateTime) {
		super(string,dataSource);
		this.formatterDate = formatterDate;
		this.formatterDateTime = formatterDateTime;
	}
	
	public TableFormating(String string,Container dataSource) {
		super(string,dataSource);
	}
	
	public TableFormating(Container dataSource) {
		super(null,dataSource);
	}
	
	public TableFormating() {
		super();
	}

	@Override
	protected String formatPropertyValue(Object rowId, Object colId,Property<?> property) {		
		Object v;
		try{
			v = property.getValue();
		}catch(MethodException e){
			return "";
		}		
        if (v instanceof LocalDate) {        	
        	LocalDate dateValue = (LocalDate) v;        	
    		return formatterDate.format(dateValue);
        }
        else if (v instanceof LocalDateTime) {
        	LocalDateTime dateValue = (LocalDateTime) v;
    		return formatterDateTime.format(dateValue);
        }
        else if (v instanceof Integer) {
        	return integerFormatter.format(v);
        }
        else if (v instanceof Boolean) {
        	Boolean boolValue = (Boolean) v;
            if (boolValue){
            	return "O";
            }else{
            	return "N";
            }
        }
        return super.formatPropertyValue(rowId, colId, property);
	}
	
	public void addBooleanColumn(String property){
		addBooleanColumn(property,true);
	}
	
	/** Ajoute une case a cocher a la place de O et N
	 * @param property
	 */
	public void addBooleanColumn(String property, Boolean alignCenter){
		addGeneratedColumn(property, new Table.ColumnGenerator() {
            /**serialVersionUID**/
			private static final long serialVersionUID = -3483685206189347289L;

			@Override
            public Object generateCell(Table source, Object itemId, Object columnId) {				
				try {
					Object value = PropertyUtils.getProperty(itemId,(String)columnId);
					if (value instanceof Boolean){
						return new IconLabel((Boolean)value,alignCenter);
					}else{
						return value;
					}
				} catch (Exception e) {
					return null;
				}				
            }            
        });
	}
}
