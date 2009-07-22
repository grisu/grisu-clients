

package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class JobName extends JPanel implements TemplateNodePanel {
	
	static final Logger myLogger = Logger.getLogger(JobName.class
			.getName());
	
	static final String REPLACEMENT_CHARACTERS = "\\s|;|'|\"|,|\\$|\\?|#";
	
	public static final String AUTOSUGGEST_PROPERTY = "autosuggest";
	public static final String NOSUGGESTBUTTON_PROPERTY = "nosuggestbutton";

	private JButton suggestButton;
	private JLabel errorLabel;
	private JTextField textField;
	private TemplateNode templateNode = null;
	
	/**
	 * Create the panel
	 */
	public JobName() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("28dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("43dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		
//		add(getTextField(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.BOTTOM));
		add(getErrorLabel(), new CellConstraints(2, 1, 3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void setTemplateNode(TemplateNode node) throws TemplateNodePanelException {
		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		node.addTemplateNodeListener(this);

//		if ( ! "1".equals(this.templateNode.getMultiplicity()) ) {
//			throw new TemplateNodePanelException("JobName template node has a multiplicity other than one.");
//		}
		
		setDefaultValue();
		
		if ( this.templateNode.hasProperty(NOSUGGESTBUTTON_PROPERTY) ) {
			// hide the suggest button
//			getSuggestButton().setVisible(false);
			add(getTextField(), new CellConstraints(2, 3, 3, 1, CellConstraints.FILL, CellConstraints.BOTTOM));
		} else {
			add(getSuggestButton(), new CellConstraints(4, 4, CellConstraints.FILL, CellConstraints.BOTTOM));
			add(getTextField(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.BOTTOM));
		}
		
		if ( this.templateNode.hasProperty(AUTOSUGGEST_PROPERTY) ) {
			autosuggest();
		}
		
		this.setBorder(new TitledBorder(null, templateNode.getName(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		getTextField().setToolTipText(templateNode.getDescription());
	}
	
	private void autosuggest() {
		
		Set<String> allJobs = this.templateNode.getTemplate().getEnvironmentManager().getJobManager().getAllJobnames(false	);
		
		String currentName = getTextField().getText();
		
		if ( currentName == null || "".equals(currentName) ) {
			currentName = this.templateNode.getName()+"_job";
		}
		
		int i = 1;
		
		String tempName = currentName;
		
		while ( allJobs.contains(tempName) ) {
			if ( i<10 ) {
				tempName = currentName+"_0"+i;
			} else {
				tempName = currentName+"_"+i;
			}
			i++;
		}
		
		getTextField().setText(tempName);
		
	}
	
	public void setDefaultValue() {
		String defaultValue = templateNode.getDefaultValue();
		if ( defaultValue != null && ! "".equals(defaultValue) ) {
			getTextField().setText(defaultValue);
		} else {
			getTextField().setText("");
		}
	}

//	public void setTemplateNodeValue() {
//
//		try {
//			this.templateNode.setValue(getTextField().getText());
//		} catch (TemplateValidateException e) {
//			errorLabel.setText(e.getLocalizedMessage());
//			errorLabel.setVisible(true);
//		}
//		
//	}
	/**
	 * @return
	 */
	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					String input = textField.getText();
					int index = textField.getCaretPosition();
					input = input.replaceAll(REPLACEMENT_CHARACTERS, "_");
					textField.setText(input.trim());
					textField.setCaretPosition(index);
				}
			});



		}
		return textField;
	}
	
//	String input = textField.getText();
//	input = input.replaceAll("\\s", "_");
//	textField.setText(input);
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
				message = TemplateNodeEvent.DEFAULT_PROCESSED_INVALID_MESSAGE;
			
			errorLabel.setText(message);
			errorLabel.setVisible(true);
			
			
		} else if ( event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID ) {
			errorLabel.setVisible(false);
		} else if ( event.getEventType() == TemplateNodeEvent.RESET ) {
			reset();
		}
		
	}

	public String getExternalSetValue() {
		
		if ( this.templateNode.hasProperty(AUTOSUGGEST_PROPERTY) ) {
			autosuggest();
		}
		
		return getTextField().getText();
	}
	
	public void setExternalSetValue(String value) {
		
		if ( value != null ) {
			getTextField().setText(value);
		}
		
	}
	
	public void reset() {

		setDefaultValue();
		
		if ( this.templateNode.hasProperty(AUTOSUGGEST_PROPERTY) ) {
			autosuggest();
		}
		
	}
	/**
	 * @return
	 */
	protected JButton getSuggestButton() {
		if (suggestButton == null) {
			suggestButton = new JButton();
			suggestButton.setToolTipText("Suggests a jobname that is not already taken.");
			suggestButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					autosuggest();
				}
			});
			suggestButton.setText("Suggest");
		}
		return suggestButton;
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
