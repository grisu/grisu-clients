

package org.vpac.grisu.client.view.swing.template.panels;

import javax.swing.JComboBox;


public class ComboBoxHolder implements ComponentHolder {
	
	private JComboBox combobox = null;
	
	private AbstractInputPanel parentPanel = null;

	public ComboBoxHolder(AbstractInputPanel parent) {
		parentPanel = parent;
	}
	
	public JComboBox getComponent() {
		if ( combobox == null ) {
			combobox = parentPanel.createJComboBox();
		}
		return combobox;
	}

	public String getExternalSetValue() {
		return (String)getComponent().getSelectedItem();
	}

	public void setComponentField(String value) {
		getComponent().setSelectedItem(value);
	}

	public int getRowSpec() {
		return 17;
	}
	

}
