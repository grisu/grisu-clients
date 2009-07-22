package org.vpac.grisu.client.view.swing.template;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.JsdlTemplateEvent;
import org.vpac.grisu.client.model.template.JsdlTemplateListener;
import org.vpac.grisu.client.model.template.nodes.templatePreProcessor.TemplatePreProcessorException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class JobStatusPanel extends JPanel implements JsdlTemplateListener {
	
	private JCheckBox deleteCheckBox;
	static final Logger myLogger = Logger.getLogger(JobStatusPanel.class
			.getName());

	private JProgressBar progressBar;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton cancelButton;
	
	private JsdlTemplate template = null;
	
	private int noPostProcessors = -1;
	private int wholeNoOfStepsInSubmission = -1;
	
	/**
	 * Create the panel
	 */
	public JobStatusPanel() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("left:22dlu"),
				ColumnSpec.decode("18dlu:grow(1.0)"),
				ColumnSpec.decode("left:26dlu")},
			new RowSpec[] {
				RowSpec.decode("top:14dlu"),
				RowSpec.decode("17dlu"),
				RowSpec.decode("top:14dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("39dlu:grow(1.0)"),
				RowSpec.decode("top:12dlu"),
				RowSpec.decode("11dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("32dlu"),
				RowSpec.decode("13dlu")}));
		add(getCancelButton(), new CellConstraints(2, 9, CellConstraints.CENTER, CellConstraints.TOP));
		add(getScrollPane(), new CellConstraints(2, 5, CellConstraints.FILL, CellConstraints.FILL));
		add(getProgressBar(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.BOTTOM));
		add(getDeleteCheckBox(), new CellConstraints(2, 7, CellConstraints.CENTER, CellConstraints.TOP));
		//
	}
	
	public void setTemplate(JsdlTemplate template) {
		this.template = template;
		this.template.addJsdlTemplateListener(this);
	}
	
	/**
	 * @return
	 */
	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setPreferredSize(new Dimension(100, 28));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					if ( template.getStatus() == JsdlTemplate.STATUS_JOB_SUBMISSION_SUCCESSFUL ) {
						// reset status of template
						try {
							template.reset(false);
						} catch (TemplatePreProcessorException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else if ( template.getStatus() == JsdlTemplate.STATUS_SUBMISSION_FAILED ) {
						
						try {
							template.reset(!getDeleteCheckBox().isSelected());
						} catch (TemplatePreProcessorException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
					} else if ( template.getStatus() >= JsdlTemplate.STATUS_SUBMISSION_STARTED &&
							template.getStatus() < JsdlTemplate.STATUS_JOB_SUBMISSION_SUCCESSFUL ) {
						// means: cancel the job
						template.cancelSubmission();
						getCancelButton().setEnabled(false);
					}
					
				}
			});
			cancelButton.setText("Cancel");
		}
		return cancelButton;
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
			textArea.setEditable(false);
			textArea.setLineWrap(true);
		}
		return textArea;
	}
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	/**
	 * @return
	 */

	public void submissionExceptionOccured(JsdlTemplateEvent event,
			JobSubmissionException exception) {
		
//		getTextArea().setText("");
		
		addText(event.getMessage());
		
		addText(exception.getLocalizedMessage());


		Exception e = exception.getParentException();
		if ( e != null ) {
			addText(e.getLocalizedMessage());
		}
		
		getCancelButton().setText("Ok");
		getCancelButton().setEnabled(true);
		getDeleteCheckBox().setVisible(true);
		
		getProgressBar().setIndeterminate(false);
		
	}
	
	public void templateStatusChanged(JsdlTemplateEvent event) {
		
		if ( template.getStatus() == JsdlTemplate.STATUS_JOB_SUBMISSION_SUCCESSFUL ) {
			setSubmissionStatus();
			addText(event.getMessage());
			getCancelButton().setText("OK");
			getCancelButton().setEnabled(true);
			getDeleteCheckBox().setVisible(false);
		} else if ( template.getStatus() < JsdlTemplate.STATUS_SUBMISSION_STARTED ){
			getTextArea().setText("");
			getCancelButton().setEnabled(false);
			getDeleteCheckBox().setVisible(false);
		} else {
			// means submission in progress
			getCancelButton().setText("Cancel");
			getCancelButton().setEnabled(true);
			getDeleteCheckBox().setVisible(false);
			String text = event.getMessage();
			addText(text);

			if ( template.getStatus() >= JsdlTemplate.STATUS_JOB_CREATED && noPostProcessors == -1 ) {
				noPostProcessors = template.getNumberOfPostprocessors();
				myLogger.debug("No. of postprocessors: "+noPostProcessors);
				wholeNoOfStepsInSubmission = JsdlTemplate.STATUS_JOB_SUBMISSION_SUCCESSFUL - JsdlTemplate.STATUS_SUBMISSION_STARTED;
				myLogger.debug("Steps without postprocessors: "+wholeNoOfStepsInSubmission);
				wholeNoOfStepsInSubmission = wholeNoOfStepsInSubmission + noPostProcessors;
				getProgressBar().setMaximum(wholeNoOfStepsInSubmission);
				getProgressBar().setIndeterminate(false);
			}
			
			if ( wholeNoOfStepsInSubmission != -1 ) {
				myLogger.debug("Status: "+template.getStatus());
				setSubmissionStatus();
			}
			
		}

	}
	
	private void addText(String text) {
		getTextArea().setText(getTextArea().getText()+text+"\n");
		getTextArea().setCaretPosition(getTextArea().getText().length());
	}
	
	public void setSubmissionStatus() {
		int newStatus = template.getStatus() - JsdlTemplate.STATUS_SUBMISSION_STARTED + noPostProcessors;
		myLogger.debug("Setting status: "+newStatus+"/"+wholeNoOfStepsInSubmission);
		getProgressBar().setValue(newStatus);
	}
	
	/**
	 * @return
	 */
	protected JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
		}
		return progressBar;
	}
	/**
	 * @return
	 */
	protected JCheckBox getDeleteCheckBox() {
		if (deleteCheckBox == null) {
			deleteCheckBox = new JCheckBox();
			deleteCheckBox.setText("Don't delete created jobdirectory");
			deleteCheckBox.setVisible(false);
		}
		return deleteCheckBox;
	}
	/**
	 * @return
	 */
	/**
	 * @return
	 */

}
