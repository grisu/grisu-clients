package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MemoryInputPanel extends JPanel implements TemplateNodePanel {
	
	private JLabel errorLabel;
	static final Logger myLogger = Logger.getLogger(MemoryInputPanel.class.getName());

	private JComboBox memComboBox;
	private JLabel minMemoryinLabel;
	
	private TemplateNode templateNode = null;
	
	public static String[] DEFAULT_MEMORY = new String[]{"1024", "2048", "4096", "8192", "16384"};
	
	private DefaultComboBoxModel memComboBoxModel = new DefaultComboBoxModel();
	
	/**
	 * Create the panel
	 */
	public MemoryInputPanel() {
		super();
		setBorder(new TitledBorder(null, "Memory", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("40dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("10dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("4dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getMinMemoryinLabel(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.DEFAULT));
		add(getMemComboBox(), new CellConstraints(2, 4));
		add(getErrorLabel(), new CellConstraints(2, 1));
		//
	}
	/**
	 * @return
	 */
	protected JLabel getMinMemoryinLabel() {
		if (minMemoryinLabel == null) {
			minMemoryinLabel = new JLabel();
			minMemoryinLabel.setText("Min. memory (in MB)");
		}
		return minMemoryinLabel;
	}
	/**
	 * @return
	 */
	protected JComboBox getMemComboBox() {
		if (memComboBox == null) {
			memComboBox = new JComboBox(memComboBoxModel);
			memComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED ) {

//						setExternalSetValue((String)(memComboBox.getSelectedItem()));

						fireSitePanelEvent((String) memComboBox
								.getSelectedItem());
					}
				}
			});
			memComboBox.setEditable(false);
			memComboBox.setEnabled(false);
		}
		return memComboBox;
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void reset() {
		// do nothing
	}
	
	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		
		node.addTemplateNodeListener(this);
		
		String[] defaultValues = node.getPrefills();
		
		if ( defaultValues == null || defaultValues.length == 0 ) {
			defaultValues = DEFAULT_MEMORY;
		}
		
		for ( String value : defaultValues ) {
			memComboBoxModel.addElement(value);
		}
		
		String defaultValue = node.getDefaultValue();
		if ( defaultValue != null && ! "".equals(defaultValue) ) {
			//TODO why is that? I can't remember anymore. Markus
			if ( "1".equals(defaultValue) ) {
				getMemComboBox().setEnabled(false);
			} else {
				getMemComboBox().setEnabled(true);
			}
			memComboBoxModel.setSelectedItem(defaultValue);
		}
		
		if ( node.getOtherProperties().containsKey(TemplateNode.LOCKED_KEY) ) {
			getMemComboBox().setEditable(false);
		} else {
			getMemComboBox().setEditable(true);
		}
		
		fireSitePanelEvent((String) memComboBox
				.getSelectedItem());
		
	}
	public void templateNodeUpdated(TemplateNodeEvent event) {

		if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID ) {
			String message = event.getMessage();
			if ( message == null ) 
				message = TemplateNodeEvent.DEFAULT_PROCESSED_INVALID_MESSAGE;
			
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		} else if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID ) {
			errorLabel.setVisible(false);
		} 
		
	}
	
	public String getExternalSetValue() {
		
		return (String)getMemComboBox().getSelectedItem();
	}
	
	public void setExternalSetValue(String value) {

		if ( value != null ) {
			getMemComboBox().setSelectedItem(value);
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
	/**
	 * @return
	 */
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setText("");
			errorLabel.setVisible(false);
			errorLabel.setForeground(Color.RED);
		}
		return errorLabel;
	}

}
