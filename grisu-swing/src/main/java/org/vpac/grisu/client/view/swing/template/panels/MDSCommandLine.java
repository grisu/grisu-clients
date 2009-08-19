package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.template.modules.SubmissionObjectHolder;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.utils.HelpDialog;
import org.vpac.historyRepeater.HistoryManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MDSCommandLine extends JPanel implements TemplateNodePanel, SubmissionObjectListener {

	private JLabel label;
	static final Logger myLogger = Logger.getLogger(MDSCommandLine.class
			.getName());
	
	
	
	public static final String EXECUTABLE_HISTORY_PREFIX = "exe_";
	
	private JLabel argumentsLabel;
	private JLabel executableLabel;
	private JLabel errorLabel;
	private JLabel requiredLabel;
	private JComboBox argumentsCombobox;
	private JComboBox executableCombobox;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	private DefaultComboBoxModel executableModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel argumentsModel = new DefaultComboBoxModel();
	
	protected boolean useHistory = false;
	protected boolean useLastInput = false;
	
    private	FormLayout layout = null;

	protected HistoryManager historyManager = null;
	String historyManagerKeyForThisNode = null;

	protected TemplateNode templateNode = null;
	private SubmissionObjectHolder submissionObjectHolder = null;
	
	
	/**
	 * Create the panel
	 */
	public MDSCommandLine() {
		super();
		layout = new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("42dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("38dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("30dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("top:1dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC});
		
		setLayout(layout);
		//
//		add(getScrollPane(), new CellConstraints(2, 2, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getExecutableCombobox(), new CellConstraints(2, 6, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getArgumentsCombobox(), new CellConstraints(6, 6, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getRequiredLabel(), new CellConstraints(8, 4, CellConstraints.DEFAULT, CellConstraints.TOP));
		add(getErrorLabel(), new CellConstraints(2, 4, 7, 1));

	}

	public JPanel getTemplateNodePanel() {
		return this;
	}
	
	private void fillArgumentsCombobox() {
		
		argumentsModel.removeAllElements();
		
		LinkedList<String> prefillStrings = new LinkedList<String>();

		if (useHistory
				&& historyManager != null
				&& historyManager.getEntries(historyManagerKeyForThisNode)
						.size() > 0) {
			for (String entry : historyManager.getEntries(historyManagerKeyForThisNode)) {
				prefillStrings.addFirst(entry);
			}
		}
		
		if (useHistory
				&& historyManager != null
				&& historyManager.getEntries(EXECUTABLE_HISTORY_PREFIX+historyManagerKeyForThisNode).size() > 0 ) {
					String exeValue = historyManager.getEntries(EXECUTABLE_HISTORY_PREFIX+historyManagerKeyForThisNode).get(0);
					for ( int i=0; i<executableModel.getSize(); i++ ) {
						if ( ((String)executableModel.getElementAt(i)).equals(exeValue) ) {
							executableModel.setSelectedItem(exeValue);
							break;
						}
					}
				}

		if (templateNode.getPrefills() != null) {
			for (String prefill : templateNode.getPrefills()) {
				if (prefill != null && !"".equals(prefill))
					prefillStrings.add(prefill);
			}
		}

		for ( String prefillString : prefillStrings ) {
			argumentsModel.addElement(prefillString);
		}
		getArgumentsCombobox().setSelectedItem("");
	}

	public void reset() {
		
		String value = (String)getArgumentsCombobox().getSelectedItem();
		String exeValue = (String)getExecutableCombobox().getSelectedItem();
		
		if (useHistory && value != null && !"".equals(value)) {
			historyManager.addHistoryEntry(historyManagerKeyForThisNode, value,
					new Date());
			historyManager.addHistoryEntry(EXECUTABLE_HISTORY_PREFIX+historyManagerKeyForThisNode, exeValue);
		}


		fillArgumentsCombobox();

		
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		node.addTemplateNodeListener(this);
		
		try {
		   // this bit is a little hairy. We need to be sure that the CommonMDS module is there
			TemplateModule submissionObjectHolderModule = node.getTemplate().getModule("Common");
			if ( submissionObjectHolderModule == null ) {
				submissionObjectHolderModule = node.getTemplate().getModule("CommonMDS");
			}
			if ( submissionObjectHolderModule == null ) {
				throw new TemplateNodePanelException("Could not find a \"Common\" or \"CommonMDS\" module for the MDSCommandline template panel.");
			}
			
		   submissionObjectHolder = (SubmissionObjectHolder)submissionObjectHolderModule;
		} catch (Exception e) {
			throw new TemplateNodePanelException("Can't render this template for an unknow reason.", e);
		}
		if ( submissionObjectHolder == null ) 
			throw new TemplateNodePanelException("Can't create submission holder object. Can't render this template.");
		
		
		
		setBorder(new TitledBorder(null, this.templateNode.getTitle(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

		
		String description = this.templateNode.getDescription();
		if ( ! this.templateNode.hasProperty(TemplateNode.HIDE_DESCRIPTION) && description != null && !"".equals(description) ) {
			getTextArea().setText(description);
			add(getScrollPane(), new CellConstraints(2, 2, 7, 1, CellConstraints.FILL, CellConstraints.FILL));
		} else {
			// don't know
			layout.setRowSpec(1, new RowSpec("0dlu"));
			layout.setRowSpec(2, new RowSpec("0dlu"));
		}
		
		historyManager = this.templateNode.getTemplate()
		.getEnvironmentManager().getHistoryManager();
		
		historyManagerKeyForThisNode = this.templateNode.getOtherProperty(TemplateNode.HISTORY_KEY);
		if ( historyManagerKeyForThisNode == null ) {
			historyManagerKeyForThisNode = this.templateNode.getName();
		}
		
		if ( this.templateNode.getOtherProperties().containsKey(TemplateNode.LAST_USED_PARAMETER) ) {
			useLastInput = true;
		} else {
			useLastInput = false;
		}
		
		if (this.templateNode.getOtherProperties().containsKey(
				TemplateNode.USE_HISTORY)) {
			useHistory = true;


			String maxString = this.templateNode
					.getOtherProperty(TemplateNode.USE_HISTORY);
			
			
			if (!TemplateNode.NON_MAP_PARAMETER.equals(maxString)) {
				int maxValues = Integer.parseInt(maxString);
				historyManager.setMaxNumberOfEntries(historyManagerKeyForThisNode, maxValues);
			}

		} else {
			useHistory = false;
		}
		
		try {
			submissionObjectChanged(submissionObjectHolder.getCurrentSubmissionObject());
		} catch (JobCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		submissionObjectHolder.addSubmissionObjectListener(this);
		add(getExecutableLabel(), new CellConstraints(2, 8));
		add(getArgumentsLabel(), new CellConstraints(6, 8, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		
		fillArgumentsCombobox();
		
		if ( this.templateNode.getInfoMap().size() > 0 ) {
			add(getLabel(), new CellConstraints(4, 8, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		}
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {

		if (event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID
				|| event.getEventType() == TemplateNodeEvent.TEMPLATE_FILLED_INVALID) {
			String message = event.getMessage();
			if (message == null)
				message = TemplateNodeEvent.DEFAULT_PROCESSED_INVALID_MESSAGE;
			layout.setRowSpec(4, new RowSpec("10dlu"));
			errorLabel.setText(message);
			errorLabel.setVisible(true);

			getRequiredLabel().setForeground(Color.RED);

		} else if (event.getEventType() == TemplateNodeEvent.TEMPLATE_PROCESSED_VALID) {
			errorLabel.setVisible(false);
			layout.setRowSpec(4, new RowSpec("4dlu"));
			getRequiredLabel().setForeground(Color.BLACK);
		} else if ( event.getEventType() == TemplateNodeEvent.RESET ) {
			reset();
		}
	}
	
	public void setExternalSetValue(String value) {
		
		if ( value != null ) {
			int index = value.indexOf(" ");
			if ( index == -1 ) {
				getExecutableCombobox().setSelectedItem(value);
			} else {
				getExecutableCombobox().setSelectedItem(value.substring(0, index-1));
				getArgumentsCombobox().setSelectedItem(value.substring(index));
			}
		}
		
	}

	public String getExternalSetValue() {
		String exe = (String)getExecutableCombobox().getSelectedItem();
		String arguments = (String)getArgumentsCombobox().getSelectedItem();
		
		return exe + " " + arguments;
	}

	public void submissionObjectChanged(SubmissionObject so) {
		
		String oldExe = (String)getExecutableCombobox().getSelectedItem();
		
		executableModel.removeAllElements();
		
		if ( so == null ) {
			return;
		}
		
		Map<String, String> details = so.getCurrentApplicationDetails();
		
		
		if ( so.getCurrentExecutables() == null ) {
			return;
		}
		for ( String exe : so.getCurrentExecutables() ) {
			executableModel.addElement(exe);
		}
		
//		if ( executableModel.getSize() <= 0 ) {
//			getExecutableCombobox().setEditable(true);
//		} else {
//			getExecutableCombobox().setEditable(false);
//		}
		
		if ( executableModel.getIndexOf(oldExe) != -1 ) {
			getExecutableCombobox().setSelectedItem(oldExe);
		}
		
	}
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextArea());
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setLineWrap(true);
		}
		return textArea;
	}
	/**
	 * @return
	 */
	protected JComboBox getExecutableCombobox() {
		if (executableCombobox == null) {
			executableCombobox = new JComboBox(executableModel);
			executableCombobox.setEditable(true);
		}
		return executableCombobox;
	}
	/**
	 * @return
	 */
	protected JComboBox getArgumentsCombobox() {
		if (argumentsCombobox == null) {
			argumentsCombobox = new JComboBox(argumentsModel);
			argumentsCombobox.setEditable(true);
		}
		return argumentsCombobox;
	}
	/**
	 * @return
	 */
	protected JLabel getRequiredLabel() {
		if (requiredLabel == null) {
			requiredLabel = new JLabel();
			requiredLabel.setText("*");
		}
		return requiredLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setForeground(Color.RED);
		}
		return errorLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getExecutableLabel() {
		if (executableLabel == null) {
			executableLabel = new JLabel();
			executableLabel.setFont(new Font("Sans", Font.PLAIN, 8));
			executableLabel.setText("Executable");
		}
		return executableLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getArgumentsLabel() {
		if (argumentsLabel == null) {
			argumentsLabel = new JLabel();
			argumentsLabel.setFont(new Font("Sans", Font.PLAIN, 8));
			argumentsLabel.setText("Arguments");
		}
		return argumentsLabel;
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
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent arg0) {
					
					String selectedApplication = (String)executableModel.getSelectedItem();
					
					HelpDialog hd = new HelpDialog(getHelpText(selectedApplication));
					hd.setVisible(true);
				}
			});
			label.setForeground(Color.BLUE);
			label.setText("Help");
			label.setFont(new Font("Sans", Font.PLAIN, 8));
		}
		return label;
	}

	private String getHelpText(String key) {
		
		String help = this.templateNode.getInfoMap().get(key);
		
		if (help == null || "".equals(help)) {
			help = this.templateNode.getInfoMap().get(TemplateNode.DEFAULT_HELP_ATTRIBUTE_NAME);
			if (help == null || "".equals(help)) {
				help = "No help available for this command.";
			}
		} 
		return help;
	}


}
