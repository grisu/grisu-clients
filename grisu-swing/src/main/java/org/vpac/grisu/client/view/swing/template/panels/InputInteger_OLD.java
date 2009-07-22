package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.historyRepeater.model.HistoryNode;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class InputInteger_OLD extends JPanel implements TemplateNodePanel {
	
	private JSpinner spinner;
	static final Logger myLogger = Logger.getLogger(InputInteger_OLD.class
			.getName());

	
	private JLabel valueLabel;
	private JSlider slider;
	private JComboBox comboBox;
	public static final String COMBOBOX_PANEL = "combobox";
	public static final String SLIDER_PANEL = "slider";
	public static final String TEXTFIELD_PANEL = "textfield";
	public static final String SPINNER_PANEL = "spinner";
	
	private JLabel requiredLabel;
	private JLabel errorLabel;
	private JTextField textField;
	private TemplateNode templateNode = null;
	
	private String renderMode = TEXTFIELD_PANEL;
	
	private String defaultValue = null;
	
	private HistoryNode historyNode = null;
	private boolean useHistory = false;
	
	private DefaultComboBoxModel comboboxModel = new DefaultComboBoxModel();
	
	/**
	 * Create the panel
	 */
	public InputInteger_OLD() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("54dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("34dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		
		add(getErrorLabel(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
		add(getRequiredLabel(), new CellConstraints(4, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void setTemplateNode(TemplateNode node) throws TemplateNodePanelException {
		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		node.addTemplateNodeListener(this);
		
		if ( "1".equals(this.templateNode.getMultiplicity()) ) {
			getRequiredLabel().setText("*");
		} else {
			getRequiredLabel().setText("");
		}

		defaultValue = node.getDefaultValue();
		String[] prefills = node.getPrefills();
		
		if ( this.templateNode.getOtherProperties().containsKey(TemplateNode.USE_HISTORY) ) {
			useHistory = true;
		} else {
			useHistory = false;
		}
		if ( useHistory ) ;
			//historyNode = this.templateNode.getTemplate().getEnvironmentManager().getHistoryManager().getHistoryNode(this.templateNode.getName());

		try {
			renderMode = this.templateNode.getOtherProperties().get("render");
		} catch (RuntimeException e1) {
			// fallback
			renderMode = TEXTFIELD_PANEL;
		}
		if (renderMode == null ) 
			renderMode = TEXTFIELD_PANEL;
		
		if ( SPINNER_PANEL.equals(renderMode) ) {

			add(getSpinner(), new CellConstraints(2, 4, 3, 1));
			
			if ( this.templateNode.getOtherProperties().containsKey(TemplateNode.LOCKED_KEY) ) {
				((JSpinner.DefaultEditor)getSpinner().getEditor()).getTextField().setEditable(false);
			}

			try {
				Integer defaultIntValue = Integer.parseInt(defaultValue);
				getSpinner().setValue(defaultIntValue);
			} catch (Exception e) {
				// doesn't matter really
				myLogger.error("Could not set default value for InputInteger "+this.templateNode.getName());
			}
			
		} else if ( SLIDER_PANEL.equals(renderMode) ) {
		
			add(getSlider(), new CellConstraints(2, 4));
			add(getValueLabel(), new CellConstraints(4, 4, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			
			Integer min = 0;
			try {
				min = Integer.parseInt(this.templateNode.getOtherProperties().get("min"));
			} catch (NumberFormatException e) {
				myLogger.error("Could not get minimum for slider. Setting to 0.");
			}
			Integer max = 100;
			try {
				max = Integer.parseInt(this.templateNode.getOtherProperties().get("max"));
			} catch (NumberFormatException e) {
				myLogger.error("Could not get max for slider. Setting to 100.");
			}
			
			Integer delta = -1;
			
			try {
				delta = Integer.parseInt(this.templateNode.getOtherProperties().get("delta"));
			} catch (Exception e) {
				myLogger.error("Could not get max for slider. Don't setting it.");
			}
			
			getSlider().setMinimum(min);
			getSlider().setMaximum(max);

			Hashtable labelTable = new Hashtable();
			labelTable.put(min, new JLabel(min.toString()));
			labelTable.put(max, new JLabel(max.toString()));
			
			if ( delta > 0 ) {
				getSlider().setMajorTickSpacing(delta);
				Integer step = min+delta;
				while ( step < max ) {
					labelTable.put(step, new JLabel(step.toString()));
					step = step+delta;
				}
					
			}
			
			getSlider().setLabelTable(labelTable);
			getSlider().setPaintTicks(true);
			getSlider().setPaintLabels(true);
			
			Integer defaultIntValue = min;
			
			try {
				defaultIntValue = Integer.parseInt(defaultValue);
				if ( min <= defaultIntValue && defaultIntValue <= max )
					getSlider().setValue(defaultIntValue);
			} catch (Exception e) {
				// doesn't matter
				getSlider().setValue(min);
			}
			
			
		}

		if ( COMBOBOX_PANEL.equals(renderMode) || ( prefills != null && prefills.length > 0) ) {
			
			renderMode = COMBOBOX_PANEL;
			// render combobox
			add(getComboBox(), new CellConstraints(2, 4, 3, 1));
			
			if ( this.templateNode.getOtherProperties().containsKey(TemplateNode.LOCKED_KEY) ) {
				getComboBox().setEditable(false);
			} else {
				getComboBox().setEditable(true);
			}
			
			if ( useHistory && historyNode != null && historyNode.getNumberOfEntries() > 0 ) {
				for ( String entry : historyNode.getEntries() ) {
					comboboxModel.addElement(entry);
				}
			}
			
			if ( prefills != null ) {
				for ( String prefill : prefills ) {
					comboboxModel.addElement(prefill);
				}
			}
			if ( defaultValue != null && ! "".equals(defaultValue)) {
				comboboxModel.setSelectedItem(defaultValue);
			} else {
				comboboxModel.setSelectedItem("");
			}
			
			
		} else {

			// render textfield
			add(getTextField(), new CellConstraints(2, 4, 3, 1, CellConstraints.FILL, CellConstraints.BOTTOM));

			if ( this.templateNode.getOtherProperties().containsKey(TemplateNode.LOCKED_KEY) ) {
				getTextField().setEditable(false);
			}
			
			if ( defaultValue != null && ! "".equals(defaultValue) ) {
				getTextField().setText(defaultValue);
			} 
		
		}
		
		this.setBorder(new TitledBorder(null, templateNode.getName(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		this.setToolTipText(templateNode.getDescription());

		
	}


	/**
	 * @return
	 */
	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
		}
		return textField;
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
		
		if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_FILLED_INVALID ) {
			String message = event.getMessage();
			if ( message == null ) 
				message = TemplateNodeEvent.DEFAULT_FILLED_INVALID_MESSAGE;
			
			errorLabel.setText(message);
			errorLabel.setVisible(true);
			
			getRequiredLabel().setForeground(Color.RED);
			
		} else if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID ) {
			errorLabel.setVisible(false);
		} 
		
	}

	public String getExternalSetValue() {
		
		if ( COMBOBOX_PANEL.equals(renderMode) ) {
			return (String)getComboBox().getSelectedItem();
		} else if ( SLIDER_PANEL.equals(renderMode) ) {
			return new Integer(getSlider().getValue()).toString();
		} else if ( SPINNER_PANEL.equals(renderMode) ) {
			return ((Integer)(getSpinner().getValue())).toString();
		} else {
			return getTextField().getText();
		}

	}
	
	public void setExternalSetValue(String value) {
		Integer integer = null;
		try {
			integer = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			myLogger.debug("Can't parse value into integer. Ignoring.");
			return;
		}
		if ( COMBOBOX_PANEL.equals(renderMode) ) {
			getComboBox().setSelectedItem(value);
		} else if ( SLIDER_PANEL.equals(renderMode) ) {
			getSlider().setValue(integer);
		} else if ( SPINNER_PANEL.equals(renderMode) ) {
			getSpinner().setValue(value);
		} else {
			getTextField().setText(value);
		}
	}
	
	/**
	 * @return
	 */
	protected JLabel getRequiredLabel() {
		if (requiredLabel == null) {
			requiredLabel = new JLabel();
		}
		return requiredLabel;
	}
	/**
	 * @return
	 */
	protected JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider();
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent e) {
					
					getValueLabel().setText(new Integer(getSlider().getValue()).toString());
					
				}
			});
		}
		return slider;
	}
	/**
	 * @return
	 */
	protected JLabel getValueLabel() {
		if (valueLabel == null) {
			valueLabel = new JLabel();
		}
		return valueLabel;
	}
	/**
	 * @return
	 */
	protected JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner();
		}
		return spinner;
	}
	
	/**
	 * @return
	 */
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(comboboxModel);
		}
		return comboBox;
	}
	
	public void reset() {
		
		if (useHistory) {
			String value = getExternalSetValue();
			historyNode.addEntry(value, new Date());
			comboboxModel.removeElement(value);
			comboboxModel.insertElementAt(value, 0);
			if ( defaultValue == null || "".equals(defaultValue) )
				comboboxModel.setSelectedItem("");
			else 
				comboboxModel.setSelectedItem(defaultValue);
		}
		
	}

	
	// event stuff
	// ========================================================
	
	private Vector<ValueListener> valueChangedListeners;

	private void fireSitePanelEvent(String newValue) {
		
		myLogger.debug("Fire value changed event: new value: "+newValue);
		// if we have no mountPointsListeners, do nothing...
		if (valueChangedListeners != null && !valueChangedListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<ValueListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<ValueListener>) valueChangedListeners.clone();
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
