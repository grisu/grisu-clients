package org.vpac.grisu.client.view.swing.template.panels;

import javax.swing.JPanel;
import javax.swing.JTextField;
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

public class ExecutionFileSystem extends JPanel implements TemplateNodePanel,
		ValueListener {

	static final Logger myLogger = Logger.getLogger(ExecutionFileSystem.class
			.getName());

	private JTextField executionFileSystemTextField;
	private String value = null;
	private TemplateNode templateNode = null;

	private SubmissionLocation submissionLocationPanel = null;

	/**
	 * Create the panel
	 */
	public ExecutionFileSystem() {
		super();
		setBorder(new TitledBorder(null, "ExecutionFileSystem",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getExecutionFileSystemTextField(), new CellConstraints(2, 2,
				CellConstraints.FILL, CellConstraints.DEFAULT));
		//
	}

	public void addValueListener(ValueListener v) {
		// not needed here because this panel will never actively change the
		// value. It's all done in the SubmissionLocation panel for now
	}

	/**
	 * @return
	 */
	protected JTextField getExecutionFileSystemTextField() {
		if (executionFileSystemTextField == null) {
			executionFileSystemTextField = new JTextField();
			executionFileSystemTextField
					.setHorizontalAlignment(SwingConstants.CENTER);
			executionFileSystemTextField.setText("n/a");
			executionFileSystemTextField.setEditable(false);
		}
		return executionFileSystemTextField;
	}

	public String getExternalSetValue() {
		return value;
	}

	private SubmissionLocation getSubmissionLocationPanel() {
		if (submissionLocationPanel == null) {
			try {

				// try to find a templateNodevalueSetter that is a
				// SubmissionLocationPanel
				for (TemplateNode node : this.templateNode.getTemplate()
						.getTemplateNodes().values()) {
					if (node.getTemplateNodeValueSetter() instanceof SubmissionLocation) {
						submissionLocationPanel = (SubmissionLocation) node
								.getTemplateNodeValueSetter();
						break;
					}

				}
			} catch (Exception e) {
				myLogger.warn("Could not get submissionLocationPanel yet...");
				submissionLocationPanel = null;
				return null;
			}

		}
		return submissionLocationPanel;
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void removeValueListener(ValueListener v) {
		// not needed here because this panel will never actively change the
		// value. It's all done in the SubmissionLocation panel for now
	}

	public void reset() {
		// not needed
	}

	public void setExternalSetValue(String value) {
		this.value = value;
		getExecutionFileSystemTextField().setText(value);
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);

		if (getSubmissionLocationPanel() != null
				&& getSubmissionLocationPanel().getCurrentExecutionFileSystem() != null) {
			setExternalSetValue(getSubmissionLocationPanel()
					.getCurrentExecutionFileSystem());
		}
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
		// not needed
	}

	public void valueChanged(TemplateNodePanel panel, String newValue) {

		// nothing to do.

	}

}
