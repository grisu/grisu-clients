package org.vpac.grisu.client.view.swing.jobs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

public class DefaultJobDetailsPanel extends JPanel {

	private JButton button;
	private JScrollPane scrollPane;
	private GrisuJobMonitoringObject job = null;

	private JEditorPane otherPropertiesPane;
	private JTextField cpusTextField;
	private JTextField queueTextField;
	private JTextField dateTextField;
	private JTextField hostTextField;
	private JTextField fqanTextField;
	private JTextField applicationTextField;
	private JTextField statusTextField;
	private JTextField jobnameTextField;
	private JLabel otherPropertiesLabel;
	private JLabel noCpusLabel;
	private JLabel submissionDateLabel;
	private JLabel submissionQueueLabel;
	private JLabel submissionHostLabel;
	private JLabel fqanLabel;
	private JLabel applicationLabel;
	private JLabel statusLabel;
	private JLabel detailsForJobLabel;

	/**
	 * Create the panel
	 */
	public DefaultJobDetailsPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getDetailsForJobLabel(), new CellConstraints(2, 2,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getStatusLabel(), new CellConstraints(2, 4, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getApplicationLabel(), new CellConstraints(2, 6,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getFqanLabel(), new CellConstraints(2, 8, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getSubmissionHostLabel(), new CellConstraints(2, 10,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getSubmissionQueueLabel(), new CellConstraints(2, 12,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getSubmissionDateLabel(), new CellConstraints(2, 14,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		add(getNoCpusLabel(), new CellConstraints(2, 16, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getOtherPropertiesLabel(), new CellConstraints(2, 18,
				CellConstraints.RIGHT, CellConstraints.TOP));
		add(getJobnameTextField(), new CellConstraints(4, 2, 3, 1));
		add(getStatusTextField(), new CellConstraints(4, 4));
		add(getApplicationTextField(), new CellConstraints(4, 6, 3, 1));
		add(getFqanTextField(), new CellConstraints(4, 8, 3, 1));
		add(getHostTextField(), new CellConstraints(4, 10, 3, 1));
		add(getDateTextField(), new CellConstraints(4, 14, 3, 1));
		add(getQueueTextField(), new CellConstraints(4, 12, 3, 1));
		add(getCpusTextField(), new CellConstraints(4, 16, 3, 1));
		add(getScrollPane(), new CellConstraints("4, 18, 3, 1, default, fill"));
		add(getButton(), new CellConstraints(6, 4));
		//
	}

	private void fillOtherProperties() {
		StringBuffer other = new StringBuffer();
		other.append("<html><body>");
		Map<String, String> otherProperties = job.getOtherProperties();
		for (String key : otherProperties.keySet()) {
			other.append("<b>" + key + ":</b><br>" + otherProperties.get(key)
					+ "<br><br>");
		}
		other.append("</body></html>");
		getOtherPropertiesPane().setText(other.toString());
	}

	protected JLabel getApplicationLabel() {
		if (applicationLabel == null) {
			applicationLabel = new JLabel();
			applicationLabel.setText("Application:");
		}
		return applicationLabel;
	}

	protected JTextField getApplicationTextField() {
		if (applicationTextField == null) {
			applicationTextField = new JTextField();
			applicationTextField.setEditable(false);
		}
		return applicationTextField;
	}

	/**
	 * @return
	 */
	protected JButton getButton() {
		if (button == null) {
			button = new JButton();
			button.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					getStatusTextField().setText(job.getStatus(true));
				}
			});
			button.setIcon(SwingResourceManager.getIcon(
					DefaultJobDetailsPanel.class, "/images/refresh.png"));
		}
		return button;
	}

	protected JTextField getCpusTextField() {
		if (cpusTextField == null) {
			cpusTextField = new JTextField();
			cpusTextField.setEditable(false);
		}
		return cpusTextField;
	}

	protected JTextField getDateTextField() {
		if (dateTextField == null) {
			dateTextField = new JTextField();
			dateTextField.setEditable(false);
		}
		return dateTextField;
	}

	protected JLabel getDetailsForJobLabel() {
		if (detailsForJobLabel == null) {
			detailsForJobLabel = new JLabel();
			detailsForJobLabel.setText("Details for job:");
		}
		return detailsForJobLabel;
	}

	protected JLabel getFqanLabel() {
		if (fqanLabel == null) {
			fqanLabel = new JLabel();
			fqanLabel.setText("Fqan:");
		}
		return fqanLabel;
	}

	protected JTextField getFqanTextField() {
		if (fqanTextField == null) {
			fqanTextField = new JTextField();
			fqanTextField.setEditable(false);
		}
		return fqanTextField;
	}

	protected JTextField getHostTextField() {
		if (hostTextField == null) {
			hostTextField = new JTextField();
			hostTextField.setEditable(false);
		}
		return hostTextField;
	}

	protected JTextField getJobnameTextField() {
		if (jobnameTextField == null) {
			jobnameTextField = new JTextField();
			jobnameTextField.setEditable(false);
		}
		return jobnameTextField;
	}

	protected JLabel getNoCpusLabel() {
		if (noCpusLabel == null) {
			noCpusLabel = new JLabel();
			noCpusLabel.setText("No. cpu's:");
		}
		return noCpusLabel;
	}

	protected JLabel getOtherPropertiesLabel() {
		if (otherPropertiesLabel == null) {
			otherPropertiesLabel = new JLabel();
			otherPropertiesLabel.setText("Other properties:");
		}
		return otherPropertiesLabel;
	}

	protected JEditorPane getOtherPropertiesPane() {
		if (otherPropertiesPane == null) {
			otherPropertiesPane = new JEditorPane();
			otherPropertiesPane.setContentType("text/html");
			otherPropertiesPane.setBackground(Color.WHITE);
		}
		return otherPropertiesPane;
	}

	protected JTextField getQueueTextField() {
		if (queueTextField == null) {
			queueTextField = new JTextField();
			queueTextField.setEditable(false);
		}
		return queueTextField;
	}

	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getOtherPropertiesPane());
		}
		return scrollPane;
	}

	protected JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel();
			statusLabel.setText("Status:");
		}
		return statusLabel;
	}

	protected JTextField getStatusTextField() {
		if (statusTextField == null) {
			statusTextField = new JTextField();
			statusTextField.setEditable(false);
		}
		return statusTextField;
	}

	protected JLabel getSubmissionDateLabel() {
		if (submissionDateLabel == null) {
			submissionDateLabel = new JLabel();
			submissionDateLabel.setText("Submission date:");
		}
		return submissionDateLabel;
	}

	protected JLabel getSubmissionHostLabel() {
		if (submissionHostLabel == null) {
			submissionHostLabel = new JLabel();
			submissionHostLabel.setText("Submission host:");
		}
		return submissionHostLabel;
	}

	protected JLabel getSubmissionQueueLabel() {
		if (submissionQueueLabel == null) {
			submissionQueueLabel = new JLabel();
			submissionQueueLabel.setText("Submission queue:");
		}
		return submissionQueueLabel;
	}

	public void setJob(GrisuJobMonitoringObject job) {

		this.job = job;
		getJobnameTextField().setText(job.getName());
		getStatusTextField().setText(job.getStatus());
		getApplicationTextField().setText(job.getApplicationType());
		getFqanTextField().setText(job.getFqan());
		getHostTextField().setText(job.getSubmissionHost());
		getQueueTextField().setText(job.getSubmissionQueue());
		getDateTextField().setText(job.getSubmissionTime());
		getCpusTextField().setText(job.getCpus());
		fillOtherProperties();
	}

}
