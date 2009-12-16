package org.vpac.grisu.client.view.swing.template.panels;

import java.util.Date;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class InputInteger extends AbstractInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2196333105136254616L;

	public static final String SLIDER_PANEL = "slider";
	public static final String SPINNER_PANEL = "spinner";

	public static final String MIN_VALUE_PROPERTYNAME = "min";
	public static final String MAX_VALUE_PROPERTYNAME = "max";

	public static final String MAJOR_TICKS_PROPERTYNAME = "delta";
	public static final String MINOR_TICKS_PROPERTYNAME = "minorDelta";

	private ComponentHolder compHolder = null;

	private String renderMode = null;

	private int min = 0;

	private int max = 100;

	private int defaultvalue = 0;

	private int minorDelta = 0;
	private int delta = 0;
	@Override
	protected void buttonPressed() {

		// we don't need a button

	}
	@Override
	protected String genericButtonText() {
		// we don't need a button
		return null;
	}
	@Override
	protected ComponentHolder getComponentHolder() {

		if (compHolder == null) {
			prepareOtherProperties();
			if (TEXTFIELD_PANEL.equals(renderMode)) {
				compHolder = new TextFieldHolder(this);
			} else if (SLIDER_PANEL.equals(renderMode)) {
				compHolder = new SliderHolder(this, min, max, defaultvalue,
						delta, minorDelta);
			} else if (SPINNER_PANEL.equals(renderMode)) {
				compHolder = new SpinnerHolder(this, min, max, defaultvalue,
						delta);
			} else {
				compHolder = new ComboBoxHolder(this);
			}
		}
		return compHolder;

	}

	public int getDefaultvalue() {
		return defaultvalue;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	private void prepareOtherProperties() {
		try {
			min = Integer.parseInt(templateNode
					.getOtherProperty(MIN_VALUE_PROPERTYNAME));
		} catch (Exception e) {
			myLogger.warn("Can't parse minimum value "
					+ templateNode.getOtherProperty(MIN_VALUE_PROPERTYNAME)
					+ " as integer. Using default...");
		}
		try {
			max = Integer.parseInt(templateNode
					.getOtherProperty(MAX_VALUE_PROPERTYNAME));
		} catch (Exception e) {
			myLogger.warn("Can't parse maximum value "
					+ templateNode.getOtherProperty(MIN_VALUE_PROPERTYNAME)
					+ " as integer. Using default...");
		}
		try {
			delta = Integer.parseInt(templateNode
					.getOtherProperty(MAJOR_TICKS_PROPERTYNAME));
		} catch (Exception e) {
			myLogger.warn("Can't parse delta value "
					+ templateNode.getOtherProperty(MAJOR_TICKS_PROPERTYNAME)
					+ " as integer. Using default...");
			if (SPINNER_PANEL.equals(renderMode)) {
				delta = 1;
			}
		}
		try {
			minorDelta = Integer.parseInt(templateNode
					.getOtherProperty(MINOR_TICKS_PROPERTYNAME));
		} catch (Exception e) {
			myLogger.warn("Can't parse minorDelta value "
					+ templateNode.getOtherProperty(MINOR_TICKS_PROPERTYNAME)
					+ " as integer. Using default...");
		}
		try {
			defaultvalue = Integer.parseInt(templateNode.getDefaultValue());
		} catch (Exception e) {
			myLogger.warn("Can't parse default value "
					+ templateNode.getDefaultValue()
					+ " as integer. Using default...");
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

		if (!COMBOBOX_PANEL.equals(renderMode)
				&& !TEXTFIELD_PANEL.equals(renderMode)
				&& !SLIDER_PANEL.equals(renderMode)
				&& !SPINNER_PANEL.equals(renderMode)) {
			myLogger.warn("Render mode: " + renderMode
					+ " not supported. Using combobox...");
			renderMode = COMBOBOX_PANEL;
		}

		if (renderMode == null)
			renderMode = COMBOBOX_PANEL;

		setStartValue();
	}

	public void reset() {

		String value = getExternalSetValue();

		if (useLastInput) {
			if (value != null && !"".equals(value.trim())) {
				historyManager.addHistoryEntry(
						this.historyManagerKeyForThisNode
								+ TemplateNode.LAST_USED_PARAMETER, value,
						new Date());
			}
		}
		if (useHistory) {
			if (value != null && !"".equals(value.trim())) {
				historyManager.addHistoryEntry(
						this.historyManagerKeyForThisNode, value, new Date());
			}
		}
		if (COMBOBOX_PANEL.equals(renderMode)) {
			fillComboBox();
		}

		// setDefaultValue();

	}

	private void setStartValue() {

		if (useLastInput) {
			try {
				String startupValue = historyManager.getEntries(
						this.historyManagerKeyForThisNode
								+ TemplateNode.LAST_USED_PARAMETER).get(0);
				if (startupValue != null) {
					getComponentHolder().setComponentField(startupValue);
				}
			} catch (Exception e) {
				try {
					getComponentHolder().setComponentField(
							Integer.toString(defaultvalue));
				} catch (Exception e2) {
					// whatever
				}
			}
		} else {
			try {
				getComponentHolder().setComponentField(
						Integer.toString(defaultvalue));
			} catch (Exception e) {
				// whatever, doesn't matter
			}
		}

	}

	@Override
	protected void setupComponent() {
		// nothing to do here
	}

}
