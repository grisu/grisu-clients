

package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Component;

public interface ComponentHolder {

	/**
	 * Returns the awt component that will be rendered.
	 * @return the component
	 */
	public Component getComponent();
	
	/**
	 * Returns the user selected/typed input.
	 * @return the user input
	 */
	public String getExternalSetValue();
	
	
	/**
	 * Sets the value to display as selected input in the rendered
	 * awt component.
	 * @param value the value to display.
	 */
	public void setComponentField(String value);
	
	/**
	 * Returns how much space is needed for this component
	 * @return the space in dlu (formlayout)
	 */
	public int getRowSpec();
	
}
