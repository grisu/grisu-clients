package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.utils.EmailUtils;

import au.org.arcs.jcommons.constants.Constants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Email extends JPanel implements TemplateNodePanel {
	
	private JLabel label;
	private JCheckBox notifyWhenJobStartsCheckBox;
	static final Logger myLogger = Logger.getLogger(Email.class
			.getName());

	public static final String EMAIL_HISTORY_STRING_KEY = "submissionEmail";
	public static final String EMAIL_ON_JOB_END_HISTORY_CHECKED_KEY = "submissionEmailOnEndChecked";
	public static final String EMAIL_ON_JOB_START_HISTORY_CHECKED_KEY = "submissionEmailOnStartChecked";
	

	private JLabel errorLabel;
	private JTextField textField;
	private JLabel emailLabel;
	private JCheckBox notifyMeWhenCheckBox;
	private TemplateNode templateNode = null;

	/**
	 * Create the panel
	 */
	public Email() {
		super();
		setBorder(new TitledBorder(null, "",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("36dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("57dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("45dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("46dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getNotifyMeWhenCheckBox(), new CellConstraints(8, 2, CellConstraints.RIGHT, CellConstraints.TOP));
		add(getEmailLabel(), new CellConstraints(2, 5, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getTextField(), new CellConstraints(4, 5, 5, 1,
				CellConstraints.FILL, CellConstraints.DEFAULT));
		add(getErrorLabel(), new CellConstraints(2, 3, 7, 1,
				CellConstraints.FILL, CellConstraints.DEFAULT));
		add(getNotifyWhenJobStartsCheckBox(), new CellConstraints(6, 2, CellConstraints.RIGHT, CellConstraints.TOP));
		add(getLabel(), new CellConstraints(2, 2, 3, 1, CellConstraints.LEFT, CellConstraints.TOP));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {
		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		node.addTemplateNodeListener(this);

		String fillValue = null;
		
		if (this.templateNode.hasProperty(TemplateNode.LAST_USED_PARAMETER)) {

			String last = null;
			try {
				last = this.templateNode.getTemplate()
					.getEnvironmentManager().getHistoryManager().getEntries(EMAIL_HISTORY_STRING_KEY).get(0);
			} catch (Exception e) {
				// no previous entry
			}

			if (last != null && !"".equals(last)) {
				fillValue = last;
			}

		}
		
		if (fillValue == null) {
			fillValue = this.templateNode.getDefaultValue();
			if (fillValue == null || "".equals(fillValue))
				fillValue = null;
		}

		String checkedEmailOnEnd = null;
		String checkedEmailOnStart = null;
		if (this.templateNode.hasProperty(TemplateNode.LAST_USED_PARAMETER)) {
		
			try {
				checkedEmailOnEnd = this.templateNode.getTemplate()
				.getEnvironmentManager().getHistoryManager().getEntries(EMAIL_ON_JOB_END_HISTORY_CHECKED_KEY).get(0);
			} catch (Exception e) {
				// no previous entry
				checkedEmailOnEnd = "undetermined";
			}
			
			try {
				checkedEmailOnStart = this.templateNode.getTemplate()
				.getEnvironmentManager().getHistoryManager().getEntries(EMAIL_ON_JOB_START_HISTORY_CHECKED_KEY).get(0);
			} catch (Exception e) {
				checkedEmailOnStart = "undetermined";
			}
		}
		
		if ("true".equals(checkedEmailOnEnd)) {
			getNotifyMeWhenCheckBox().setSelected(true);
		} else if ("false".equals(checkedEmailOnEnd)) {
			getNotifyMeWhenCheckBox().setSelected(false);
		} else {

			if (fillValue != null) {
				getNotifyMeWhenCheckBox().setSelected(true);
			} else {
				getNotifyMeWhenCheckBox().setSelected(false);
			}
		}
		
		if ("true".equals(checkedEmailOnStart)) {
			getNotifyWhenJobStartsCheckBox().setSelected(true);
		} else if ("false".equals(checkedEmailOnStart)) {
			getNotifyWhenJobStartsCheckBox().setSelected(false);
		} else {

			if (fillValue != null) {
				getNotifyWhenJobStartsCheckBox().setSelected(true);
			} else {
				getNotifyWhenJobStartsCheckBox().setSelected(false);
			}
		}

		getTextField().setText(fillValue);
	}

	// public void setTemplateNodeValue() {
	//		
	// if ( getNotifyMeWhenCheckBox().isSelected() ) {
	// try {
	// templateNode.setValue(getTextField().getText());
	// } catch (TemplateValidateException e) {
	// errorLabel.setText(e.getLocalizedMessage());
	// errorLabel.setVisible(true);
	// }
	// }
	//		
	// }

	private void displayEmailField(boolean display) {

		getEmailLabel().setVisible(display);
		getTextField().setVisible(display);

	}

	/**
	 * @return
	 */
	protected JCheckBox getNotifyMeWhenCheckBox() {
		if (notifyMeWhenCheckBox == null) {
			notifyMeWhenCheckBox = new JCheckBox();
			notifyMeWhenCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {

					calculateDisplayingEmailField();
					setTemplateAttributes();
					
				}
			});
			notifyMeWhenCheckBox.setText("finishes");
		}
		return notifyMeWhenCheckBox;
	}

	/**
	 * @return
	 */
	protected JCheckBox getNotifyWhenJobStartsCheckBox() {
		if (notifyWhenJobStartsCheckBox == null) {
			notifyWhenJobStartsCheckBox = new JCheckBox();
			notifyWhenJobStartsCheckBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					calculateDisplayingEmailField();
					setTemplateAttributes();
				}
			});
			notifyWhenJobStartsCheckBox.setText("starts");
		}
		return notifyWhenJobStartsCheckBox;
	}
	
	private void calculateDisplayingEmailField() {
		
		if ( getNotifyMeWhenCheckBox().isSelected() || getNotifyWhenJobStartsCheckBox().isSelected() ) {
			displayEmailField(true);
		} else {
			displayEmailField(false);
		}
		
	}
	
	/**
	 * @return
	 */
	protected JLabel getEmailLabel() {
		if (emailLabel == null) {
			emailLabel = new JLabel();
			emailLabel.setText("Email:");
			emailLabel.setVisible(false);
		}
		return emailLabel;
	}

	/**
	 * @return
	 */
	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setVisible(false);
		}
		return textField;
	}

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

	/**
	 * @return
	 */
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

			String input = getTextField().getText();
			
			if ( getNotifyMeWhenCheckBox().isSelected() && (input != null && EmailUtils.isValid(input)) ) {
				templateNode.getTemplate().getEnvironmentManager().getHistoryManager().addHistoryEntry(EMAIL_HISTORY_STRING_KEY, input, new Date(), 1);
				templateNode.getTemplate().getEnvironmentManager().getHistoryManager().addHistoryEntry(EMAIL_ON_JOB_END_HISTORY_CHECKED_KEY, "true", new Date(), 1);
			} else {
				templateNode.getTemplate().getEnvironmentManager().getHistoryManager().addHistoryEntry(EMAIL_ON_JOB_END_HISTORY_CHECKED_KEY, "false", new Date(), 1);
			}
			
			if ( getNotifyWhenJobStartsCheckBox().isSelected() && (input != null && EmailUtils.isValid(input)) ) {
				templateNode.getTemplate().getEnvironmentManager().getHistoryManager().addHistoryEntry(EMAIL_HISTORY_STRING_KEY, input, new Date(), 1);
				templateNode.getTemplate().getEnvironmentManager().getHistoryManager().addHistoryEntry(EMAIL_ON_JOB_START_HISTORY_CHECKED_KEY, "true", new Date(), 1);
			} else {
				templateNode.getTemplate().getEnvironmentManager().getHistoryManager().addHistoryEntry(EMAIL_ON_JOB_START_HISTORY_CHECKED_KEY, "false", new Date(), 1);
			}

		}

	}

	public String getExternalSetValue() {

		if (getNotifyMeWhenCheckBox().isSelected() || getNotifyWhenJobStartsCheckBox().isSelected()) {
			return getTextField().getText();
		} else {
			return null;
		}
	}
	
	public void setExternalSetValue(String value) {
		
		if ( value != null ) {
			getNotifyMeWhenCheckBox().setSelected(true);
			getTextField().setText(value);
		} else {
			getNotifyMeWhenCheckBox().setSelected(false);
		}
		
	}

	public void reset() {
 
		// doesn't need to be implemented because templateNodeUpdated is overwriteen

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
	
	protected void setTemplateAttributes() {
		
		if ( getNotifyWhenJobStartsCheckBox().isSelected() ) {
			this.templateNode.getElement().setAttribute(Constants.SEND_EMAIL_ON_JOB_START_ATTRIBUTE_KEY, "true");
		} else {
			this.templateNode.getElement().setAttribute(Constants.SEND_EMAIL_ON_JOB_START_ATTRIBUTE_KEY, "false");
		}
		
		if ( getNotifyMeWhenCheckBox().isSelected() ) {
			this.templateNode.getElement().setAttribute(Constants.SEND_EMAIL_ON_JOB_END_ATTRIBUTE_KEY, "true");
		} else {
			this.templateNode.getElement().setAttribute(Constants.SEND_EMAIL_ON_JOB_END_ATTRIBUTE_KEY, "false");
		}
		
	}


	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("Notify me when job:");
		}
		return label;
	}


}
