package org.vpac.grisu.client.view.swing.template.panels;

import java.util.Date;

public class InputString extends AbstractInputPanel {

	protected String renderMode = null;

	/**
	 * Create the panel
	 */
	public InputString() {
		super();
		//
	}

	@Override
	protected void buttonPressed() {
		// nothing to do, no button here
	}

	@Override
	protected String genericButtonText() {
		// don't want a button at all
		return null;
	}

	@Override
	protected ComponentHolder getComponentHolder() {

		if (TEXTFIELD_PANEL.equals(renderMode)) {
			return new TextFieldHolder(this);
		} else {
			return new ComboBoxHolder(this);
		}
	}

	@Override
	protected void preparePanel() {
		try {
			renderMode = this.templateNode.getOtherProperties().get("render");
		} catch (RuntimeException e1) {
			// fallback
			renderMode = COMBOBOX_PANEL;
		}
		if (renderMode == null)
			renderMode = COMBOBOX_PANEL;

	}

	public void reset() {
		String value = getExternalSetValue();

		if (useHistory)
			historyManager.addHistoryEntry(this.historyManagerKeyForThisNode,
					value, new Date());

		if (COMBOBOX_PANEL.equals(renderMode)) {
			fillComboBox();
		}

		setDefaultValue();
	}

	@Override
	protected void setupComponent() {
		// nothing to do here
	}

}
