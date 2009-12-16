package org.vpac.grisu.client.view.swing.template.panels;

import javax.swing.JTextField;

public class TextFieldHolder implements ComponentHolder {

	private JTextField textfield = null;

	private AbstractInputPanel parentPanel = null;

	public TextFieldHolder(AbstractInputPanel parent) {
		parentPanel = parent;
	}

	public JTextField getComponent() {
		if (textfield == null) {
			textfield = parentPanel.createJTextField();
		}
		return textfield;
	}

	public String getExternalSetValue() {
		return getComponent().getText();
	}

	public int getRowSpec() {
		return 17;
	}

	public void setComponentField(String value) {
		getComponent().setText(value);
	}

}
