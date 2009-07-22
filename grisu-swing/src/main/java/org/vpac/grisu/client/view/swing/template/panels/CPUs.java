package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.historyRepeater.HistoryManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class CPUs extends JPanel implements TemplateNodePanel {

	static final Logger myLogger = Logger.getLogger(CPUs.class.getName());

	public static final String FORCE_STRING = "force";
	public static final String FORCE_PARALLEL_STRING = "parallel";
	public static final String FORCE_SINGLE_STRING = "single";

	private JLabel errorLabel;
	private TemplateNode templateNode = null;

	public static String[] DEFAULT_CPUS = new String[] { "1", "2", "4", "8",
			"16", "32" };

	private JLabel label;
	private JComboBox comboBox;
	private JCheckBox checkBox;

	private DefaultComboBoxModel cpuComboBoxModel = new DefaultComboBoxModel();

	private boolean forceParallel = false;
	private boolean forceSingle = false;

	protected boolean useLastInput = false;
	protected HistoryManager historyManager = null;
	private String historyManagerKeyForThisNode = null;
	
	private String lastParallelValue = "2";

	/**
	 * Create the panel
	 */
	public CPUs() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		setBorder(new TitledBorder(null, "CPUs",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		add(getCheckBox(), new CellConstraints(2, 1, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getComboBox(), new CellConstraints(2, 7, CellConstraints.FILL,
				CellConstraints.BOTTOM));
		add(getLabel(), new CellConstraints(2, 5, CellConstraints.LEFT,
				CellConstraints.BOTTOM));
		add(getErrorLabel(), new CellConstraints(2, 3));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	// public void setTemplateNodeValue() {
	// try {
	// if ( getCheckBox().isSelected() )
	// this.templateNode.setValue((String)getComboBox().getSelectedItem());
	// else
	// this.templateNode.setValue("1");
	// } catch (TemplateValidateException e) {
	// errorLabel.setText(e.getLocalizedMessage());
	// errorLabel.setToolTipText(e.getLocalizedMessage());
	// errorLabel.setVisible(true);
	// }
	// }
	/**
	 * @return
	 */
	protected JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox();
			checkBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					
					if ( ! checkBox.isSelected() && ! "1".equals(getComboBox().getSelectedItem()) ) {
						setExternalSetValue("1");
					} else if ( checkBox.isSelected() && "1".equals(getComboBox().getSelectedItem()) ) {
						setExternalSetValue(lastParallelValue);
					}
					
				}
			});
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			checkBox.setHorizontalTextPosition(SwingConstants.LEADING);
			checkBox.setText("Parallel");
		}
		return checkBox;
	}

	public boolean isMultiCPUchecked() {
		return getCheckBox().isSelected();
	}

	public int getSelectedNoOfCPUs() {
		return Integer.parseInt((String) cpuComboBoxModel.getSelectedItem());
	}

	/**
	 * @return
	 */
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(cpuComboBoxModel);
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED && ! getExternalSetValue().equals(comboBox.getSelectedItem())) {

						setExternalSetValue((String)(comboBox.getSelectedItem()));

						fireSitePanelEvent((String) cpuComboBoxModel
								.getSelectedItem());
					}
				}
			});
			comboBox.setEditable(true);
		}
		return comboBox;
	}

	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("No. of cpus");
		}
		return label;
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);

		node.addTemplateNodeListener(this);

		String[] defaultValues = node.getPrefills();

		if (defaultValues == null || defaultValues.length == 0) {
			defaultValues = DEFAULT_CPUS;
		}

		String mode = this.templateNode.getOtherProperties().get(FORCE_STRING);

		if (mode != null && !"".equals(mode)) {
			String temp = this.templateNode.getOtherProperty(FORCE_STRING);
			// String temp2 = temp.substring(temp.indexOf("="));

			if (FORCE_PARALLEL_STRING.equals(temp)) {
				forceParallel = true;
				forceSingle = false;
			} else if (FORCE_SINGLE_STRING.equals(temp)) {
				forceSingle = true;
				forceParallel = false;
			}
		}

		historyManager = this.templateNode.getTemplate()
				.getEnvironmentManager().getHistoryManager();

		historyManagerKeyForThisNode = this.templateNode
				.getOtherProperty(TemplateNode.HISTORY_KEY);
		if (historyManagerKeyForThisNode == null) {
			historyManagerKeyForThisNode = this.templateNode.getName();
		}

		if (this.templateNode.getOtherProperties().containsKey(
				TemplateNode.LAST_USED_PARAMETER)) {
			useLastInput = true;
		} else {
			useLastInput = false;
		}

		for (String value : defaultValues) {
			if (forceParallel) {
				if ("1".equals(value)) {
					continue;
				}
			}
			cpuComboBoxModel.addElement(value);
		}

		String lastInput = null;
		if (useLastInput) {
			try {
				lastInput = historyManager.getEntries(
						historyManagerKeyForThisNode).get(0);
			} catch (Exception e) {
				// doesn't matter
			}
		}
		if (lastInput != null) {
			setExternalSetValue(lastInput);
		} else {
			String defaultValue = node.getDefaultValue();
			if (defaultValue != null) {
				setExternalSetValue(defaultValue);
			} else {
				setExternalSetValue("1");
			}
		}

	}

	/**
	 * @return
	 */
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setVisible(false);
			errorLabel.setForeground(Color.RED);
		}
		return errorLabel;
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
		} else if (event.getEventType() == TemplateNodeEvent.RESET) {
			reset();
		}

	}

	public String getExternalSetValue() {

		if (getCheckBox().isSelected())
			return (String) getComboBox().getSelectedItem();
		else
			return "1";

	}
	
	

	public void setExternalSetValue(String value) {

		if ( value == null ) {
			return;
		}
		
		if ( value.equals("") ) {
			value = "1";
		}
		
		if ( !value.equals(getComboBox().getSelectedItem()) ) {
			getComboBox().setSelectedItem(value);
		}
		
		if ("1".equals(value)) {
			getCheckBox().setSelected(false);
			getComboBox().setEnabled(true);
		} else {
			getCheckBox().setSelected(true);
			getComboBox().setEnabled(true);
		}

		if (forceParallel) {
			getCheckBox().setSelected(true);
			getCheckBox().setEnabled(false);
			if (getComboBox().getSelectedItem().equals("1")) {
				getComboBox().setSelectedItem("2");
			}

		} else if (forceSingle) {
			getComboBox().setSelectedItem("1");
			getComboBox().setEnabled(false);
			getCheckBox().setSelected(false);
			getCheckBox().setEnabled(false);
		}
		
		if ( !"1".equals(value) ) {
			lastParallelValue = value;
		}
	}

	public void reset() {

		historyManager.addHistoryEntry(historyManagerKeyForThisNode,
				getExternalSetValue(), new Date(), 1);
	}

	// event stuff
	// ========================================================

	private Vector<ValueListener> valueChangedListeners;

	private void fireSitePanelEvent(String newValue) {

		myLogger.debug("Fire value changed event from CPUs: new value: " + newValue);
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

	// register a listener
	synchronized public void addValueListener(ValueListener l) {
		if (valueChangedListeners == null)
			valueChangedListeners = new Vector<ValueListener>();
		valueChangedListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeValueListener(ValueListener l) {
		if (valueChangedListeners == null) {
			valueChangedListeners = new Vector<ValueListener>();
		}
		valueChangedListeners.removeElement(l);
	}

}
