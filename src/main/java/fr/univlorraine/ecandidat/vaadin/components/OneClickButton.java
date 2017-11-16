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

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

public class OneClickButton extends Button {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4545607361772520034L;

	/**
     * Creates a new push button.
     */
    public OneClickButton() {
        super();
        super.setDisableOnClick(true);
        addClickListener(e->setEnabled(true));
    }
    
    /**
     * Creates a new push button with the given caption.
     * 
     * @param caption
     *            the Button caption.
     */
    public OneClickButton(String caption) {
    	super(caption);
    	super.setDisableOnClick(true);
        addClickListener(e->setEnabled(true));
    }
    
    /**
     * Creates a new push button with the given icon.
     * 
     * @param icon
     *            the icon
     */
    public OneClickButton(Resource icon) {
    	super(icon);
    	super.setDisableOnClick(true);
        addClickListener(e->setEnabled(true));
    }

    /**
     * Creates a new push button with the given caption and icon.
     * 
     * @param caption
     *            the caption
     * @param icon
     *            the icon
     */
    public OneClickButton(String caption, Resource icon) {
    	super(caption, icon);
    	super.setDisableOnClick(true);
        addClickListener(e->setEnabled(true));
    }

	@Override
	public void setDisableOnClick(boolean disableOnClick){
	}
}
