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
package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;

import com.vaadin.server.FontAwesome;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Objet servant à la présentation basique de donnée
 *
 * @author Kevin Hergalant
 */
@Data
@EqualsAndHashCode(of = {"propertyId"})
@SuppressWarnings("serial")
public class SortOrderPresentation implements Serializable {

	private Integer order;
	private String propertyId;
	private String propertyName;
	private String direction;
	private String exchange;
	private String monte;
	private String descend;
	private String delete;
	private String exchangeDesc;
	private String exchangeAsc;

	public final static String CHAMPS_ORDER = "order";
	public final static String CHAMPS_PROPERTY_ID = "propertyId";
	public final static String CHAMPS_PROPERTY_NAME = "propertyName";
	public final static String CHAMPS_DIRECTION = "direction";
	public final static String CHAMPS_MONTE = "monte";
	public final static String CHAMPS_DESCEND = "descend";
	public final static String CHAMPS_DELETE = "delete";
	public final static String CHAMPS_EXCHANGE = "exchange";
	public final static String CHAMPS_EXCHANGE_DESC = "exchangeDesc";
	public final static String CHAMPS_EXCHANGE_ASC = "exchangeAsc";

	public SortOrderPresentation(final String propertyId) {
		super();
		this.propertyId = propertyId;
	}

	public void setIcons(final String txtMonte, final String txtDescend, final String txtDelete, final String txtExchangeDesc, final String txtExchangeAsc) {
		this.monte = "<span title='" + txtMonte + "'>" + FontAwesome.ARROW_CIRCLE_O_UP.getHtml() + "<span>";
		this.descend = "<span title='" + txtDescend + "'>" + FontAwesome.ARROW_CIRCLE_O_DOWN.getHtml() + "<span>";
		this.delete = "<span title='" + txtDelete + "'>" + FontAwesome.TRASH_O.getHtml() + "<span>";
		this.exchangeDesc = "<span title='" + txtExchangeDesc + "'>" + FontAwesome.SORT_AMOUNT_DESC.getHtml() + "<span>";
		this.exchangeAsc = "<span title='" + txtExchangeAsc + "'>" + FontAwesome.SORT_AMOUNT_ASC.getHtml() + "<span>";
	}

	public void setExchangeDesc() {
		this.exchange = exchangeDesc;
	}

	public void setExchangeAsc() {
		this.exchange = exchangeAsc;
	}
}
