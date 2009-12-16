package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class WallTime extends JPanel implements TemplateNodePanel {

	static final Logger myLogger = Logger.getLogger(WallTime.class.getName());

	private JLabel errorLabel;
	private TemplateNode templateNode = null;

	public static final String[] DAYS_DEFAULTS = new String[] { "0", "1", "2",
			"7", "14", "21" };
	public static final String[] HOURS_DEFAULTS = new String[] { "0", "1", "6",
			"12", "18" };
	public static final String[] MINUTES_DEFAULTS = new String[] { "0", "15",
			"30", "45" };

	private JComboBox minutesComboBox;
	private JComboBox hoursComboBox;
	private JComboBox daysComboBox;
	private JLabel minutesLabel;
	private JLabel hoursLabel;
	private JLabel daysLabel;

	private Vector<ValueListener> valueChangedListeners;

	/**
	 * Create the panel
	 */
	public WallTime() {
		super();
		setBorder(new TitledBorder(null, "Walltime",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("26dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("22dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("21dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC }));
		add(getDaysLabel(), new CellConstraints(2, 3, CellConstraints.DEFAULT,
				CellConstraints.BOTTOM));
		add(getHoursLabel(), new CellConstraints(4, 3, CellConstraints.DEFAULT,
				CellConstraints.BOTTOM));
		add(getMinutesLabel(), new CellConstraints(6, 3,
				CellConstraints.DEFAULT, CellConstraints.BOTTOM));
		add(getDaysComboBox(), new CellConstraints(2, 5));
		add(getHoursComboBox(), new CellConstraints(4, 5));
		add(getMinutesComboBox(), new CellConstraints(6, 5));
		add(getErrorLabel(), new CellConstraints(2, 1, 5, 1));
		//
	}

	// register a listener
	synchronized public void addValueListener(ValueListener l) {
		if (valueChangedListeners == null)
			valueChangedListeners = new Vector<ValueListener>();
		valueChangedListeners.addElement(l);
	}

	private Long[] calculateTime(String longAsString) {
		Long time = Long.parseLong(longAsString);

		Long days = time / (3600 * 24);
		Long hours = (time - (days * 3600 * 24)) / 3600;
		Long minutes = (time - ((days * 3600 * 24) + (hours * 3600))) / 60;
		return new Long[] { days, hours, minutes };
	}

	private void fireSitePanelEvent(String newValue) {

		myLogger.debug("Fire value changed event: new value: " + newValue);
		// if we have no mountPointsListeners, do nothing...
		if (valueChangedListeners != null && !valueChangedListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<ValueListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<ValueListener>) valueChangedListeners
						.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration<ValueListener> e = valueChangedTargets.elements();
			while (e.hasMoreElements()) {
				ValueListener valueChanged_l = (ValueListener) e.nextElement();
				valueChanged_l.valueChanged(this, newValue);
			}
		}
	}

	/**
	 * @return
	 */
	protected JComboBox getDaysComboBox() {
		if (daysComboBox == null) {
			daysComboBox = new JComboBox(DAYS_DEFAULTS);
			daysComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {

						fireSitePanelEvent(getExternalSetValue());
					}
				}
			});
			daysComboBox.setEditable(true);
		}
		return daysComboBox;
	}

	/**
	 * @return
	 */
	protected JLabel getDaysLabel() {
		if (daysLabel == null) {
			daysLabel = new JLabel();
			daysLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			daysLabel.setText("Days");
		}
		return daysLabel;
	}

	// public void setTemplateNodeValue() {
	// try {
	// this.templateNode.setValue(new Long(getWallTime()).toString());
	// } catch (TemplateValidateException e) {
	// getErrorLabel().setText(e.getLocalizedMessage());
	// getErrorLabel().setVisible(true);
	// }
	// }
	/**
	 * @return
	 */
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setForeground(Color.RED);
			errorLabel.setVisible(false);
		}
		return errorLabel;
	}

	public String getExternalSetValue() {
		return new Long(getWallTime()).toString();
	}

	/**
	 * @return
	 */
	protected JComboBox getHoursComboBox() {
		if (hoursComboBox == null) {
			hoursComboBox = new JComboBox(HOURS_DEFAULTS);
			hoursComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {

						fireSitePanelEvent(getExternalSetValue());
					}
				}
			});
			hoursComboBox.setEditable(true);
		}
		return hoursComboBox;
	}

	/**
	 * @return
	 */
	protected JLabel getHoursLabel() {
		if (hoursLabel == null) {
			hoursLabel = new JLabel();
			hoursLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			hoursLabel.setText("Hours");
		}
		return hoursLabel;
	}

	/**
	 * @return
	 */
	protected JComboBox getMinutesComboBox() {
		if (minutesComboBox == null) {
			minutesComboBox = new JComboBox(MINUTES_DEFAULTS);
			minutesComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {

						fireSitePanelEvent(getExternalSetValue());
					}
				}
			});
			minutesComboBox.setEditable(true);
		}
		return minutesComboBox;
	}

	/**
	 * @return
	 */
	protected JLabel getMinutesLabel() {
		if (minutesLabel == null) {
			minutesLabel = new JLabel();
			minutesLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			minutesLabel.setText("Minutes");
		}
		return minutesLabel;
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public String getValue() {
		return new Long(getWallTime()).toString();
	}

	/**
	 * Returns the currently selected walltime in seconds.
	 * 
	 * @return the walltime in seconds
	 */
	public long getWallTime() {

		int days = Integer.parseInt((String) getDaysComboBox()
				.getSelectedItem());
		int hours = Integer.parseInt((String) getHoursComboBox()
				.getSelectedItem());
		int minutes = Integer.parseInt((String) getMinutesComboBox()
				.getSelectedItem());

		long walltime = minutes * 60;
		walltime = walltime + hours * 60 * 60;
		walltime = walltime + days * 60 * 60 * 24;

		return walltime;

	}

	// remove a listener
	synchronized public void removeValueListener(ValueListener l) {
		if (valueChangedListeners == null) {
			valueChangedListeners = new Vector<ValueListener>();
		}
		valueChangedListeners.removeElement(l);
	}

	// event stuff
	// ========================================================

	public void reset() {

		// do nothing

	}

	public void setExternalSetValue(String value) {

		try {

			Long[] values = calculateTime(value);

			getDaysComboBox().setSelectedItem(values[0].toString());
			getHoursComboBox().setSelectedItem(values[1].toString());
			getMinutesComboBox().setSelectedItem(values[2].toString());

		} catch (Exception e) {
			// doesn't really matter
			myLogger.warn("Couldn't set walltime: " + e.getLocalizedMessage());
		}

	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {
		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		node.addTemplateNodeListener(this);

		this.setToolTipText(node.getDescription());

		String defaultValue = node.getDefaultValue();

		if (defaultValue != null && !"".equals(defaultValue)) {
			try {

				Long[] values = calculateTime(defaultValue);

				getDaysComboBox().setSelectedItem(values[0].toString());
				getHoursComboBox().setSelectedItem(values[1].toString());
				getMinutesComboBox().setSelectedItem(values[2].toString());

			} catch (Exception e) {
				// doesn't really matter
				myLogger.warn("Couldn't set default walltime: "
						+ e.getLocalizedMessage());
			}
		}

	}

	public void templateNodeUpdated(TemplateNodeEvent event) {

		if (event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID) {
			String message = event.getMessage();
			if (message == null)
				message = TemplateNodeEvent.DEFAULT_PROCESSED_INVALID_MESSAGE;

			errorLabel.setText(message);
			errorLabel.setVisible(true);
		} else if (event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID) {
			errorLabel.setVisible(false);
		}

	}

}
