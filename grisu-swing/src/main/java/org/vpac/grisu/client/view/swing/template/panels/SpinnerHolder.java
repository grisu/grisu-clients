package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;

public class SpinnerHolder extends JPanel implements ComponentHolder {

	static final Logger myLogger = Logger.getLogger(SliderHolder.class
			.getName());

	private JSpinner spinner;

	private int min;
	private int max;
	private int defaultValue;
	private int delta;

	private AbstractInputPanel parentPanel = null;

	private SpinnerModel spinnerModel = null;

	/**
	 * Create the panel
	 */
	public SpinnerHolder(AbstractInputPanel parent, int min, int max,
			int defaultValue, int delta) {
		super();
		this.parentPanel = parent;
		this.min = min;
		this.max = max;
		this.defaultValue = defaultValue;
		this.delta = delta;

		this.spinnerModel = new SpinnerNumberModel(defaultValue, min, max,
				delta);

		setLayout(new BorderLayout());
		add(getSpinner());
		//
	}

	public Component getComponent() {
		return this;
	}

	public String getExternalSetValue() {
		return ((Integer) (getSpinner().getValue())).toString();
	}

	public int getRowSpec() {
		return 15;
	}

	/**
	 * @return
	 */
	protected JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner(spinnerModel);
		}
		return spinner;
	}

	public void setComponentField(String value) {
		try {
			getSpinner().setValue(Integer.parseInt(value));
		} catch (Exception e) {
			myLogger.warn("Couldn't set value of spinner to: " + value + ". "
					+ e.getLocalizedMessage());
		}
	}

}
