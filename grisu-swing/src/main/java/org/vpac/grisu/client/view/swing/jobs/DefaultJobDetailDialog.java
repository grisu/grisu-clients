package org.vpac.grisu.client.view.swing.jobs;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.preview.JobFileListWithPreviewPanel;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.plugins.PluginPanel;

public class DefaultJobDetailDialog extends JDialog {

	private DefaultJobDetailsPanel defaultJobDetailsPanel;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JobFileListWithPreviewPanel jobFileListWithPreviewPanel;
	static final Logger myLogger = Logger
			.getLogger(DefaultJobDetailDialog.class.getName());

	private DefaultJobFilesPanel defaultJobFilesPanel;
	private JobDirectoryPanel jobDirectoryPanel;
	private JTabbedPane tabbedPane;

	private JPanel applicationSpecificPanel = null;
	// /**
	// * Launch the application
	// * @param args
	// */
	// public static void main(String args[]) {
	// try {
	// DefaultJobDetailDialog dialog = new DefaultJobDetailDialog();
	// dialog.addWindowListener(new WindowAdapter() {
	// public void windowClosing(WindowEvent e) {
	// System.exit(0);
	// }
	// });
	// dialog.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private GrisuJobMonitoringObject job = null;
	private EnvironmentManager em = null;

	// for wbbuilder pro -- don't use
	public DefaultJobDetailDialog() {
		super();
		initialize();
	}

	/**
	 * Create the dialog
	 */
	public DefaultJobDetailDialog(EnvironmentManager em) {
		super();
		this.em = em;
		initialize();
		//
	}

	protected JPanel getApplicationSpecificPanel() {

		if (applicationSpecificPanel == null) {

			Class applicationClass = null;
			String applicationType = job.getApplicationType();

			try {
				applicationClass = Class.forName("org.vpac.grisu.plugins."
						+ applicationType + ".MainPanel");

				PluginPanel pluginPanel = (PluginPanel) applicationClass
						.newInstance();
				pluginPanel.initializePanel(job);

				applicationSpecificPanel = pluginPanel.getJPanel();

			} catch (Exception e) {
				myLogger
						.info("Could not find specific view panel for application: "
								+ applicationType + ": " + e.getMessage());
				// panel = new JobDetailPanel(jobProperties, serviceInterface);
				// e.printStackTrace();
				return null;
			}
		}

		return applicationSpecificPanel;
	}

	/**
	 * @return
	 */
	protected DefaultJobDetailsPanel getDefaultJobDetailsPanel_1() {
		if (defaultJobDetailsPanel == null) {
			defaultJobDetailsPanel = new DefaultJobDetailsPanel();
			defaultJobDetailsPanel.setJob(job);
		}
		return defaultJobDetailsPanel;
	}
	/**
	 * @return
	 */

	protected DefaultJobFilesPanel getDefaultJobFilesPanel() {
		if (defaultJobFilesPanel == null) {
			defaultJobFilesPanel = new DefaultJobFilesPanel(em);
			defaultJobFilesPanel.setJob(job);
		}
		return defaultJobFilesPanel;
	}

	protected JobFileListWithPreviewPanel getJobFileListWithPreviewPanel() {
		if (jobFileListWithPreviewPanel == null) {
			jobFileListWithPreviewPanel = new JobFileListWithPreviewPanel(em);
			jobFileListWithPreviewPanel.setJob(job);
		}
		return jobFileListWithPreviewPanel;
	}

	// protected JobDirectoryPanel getJobDirectoryPanel() {
	// if (jobDirectoryPanel == null) {
	// jobDirectoryPanel = new JobDirectoryPanel(em);
	// jobDirectoryPanel.setJob(job);
	// }
	// return jobDirectoryPanel;
	// }

	protected JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();

		}
		return tabbedPane;
	}

	// protected DefaultJobDetailsPanel getDefaultJobDetailsPanel() {
	// if (defaultJobDetailsPanel == null) {
	// defaultJobDetailsPanel = new DefaultJobDetailsPanel();
	// defaultJobDetailsPanel.setJob(job);
	// }
	// return defaultJobDetailsPanel;
	// }

	private void initialize() {
		setBounds(100, 100, 622, 560);
		getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
	}

	public void setJob(GrisuJobMonitoringObject job) {
		this.job = job;

		if (getApplicationSpecificPanel() != null) {
			tabbedPane.addTab(job.getApplicationType(), null,
					getApplicationSpecificPanel(), null);
		}

		tabbedPane.addTab("Job directory", null,
				getJobFileListWithPreviewPanel(), null);
		tabbedPane.addTab("Job details", null, getDefaultJobDetailsPanel_1(),
				null);
		// tabbedPane.addTab("Output", null, getDefaultJobFilesPanel(), null);
		// tabbedPane.addTab("Job directory", null, getJobDirectoryPanel(),
		// null);
		// tabbedPane.addTab("Job details", null, getDefaultJobDetailsPanel(),
		// null);

	}

	public void setJob(String jobname) throws NoSuchJobException {
		myLogger.debug("Creating job monitoring object for: " + jobname);
		GrisuJobMonitoringObject job = em.getJobManager().getJob(jobname);
		// .getJobManagement().getJob(jobname);
		setJob(job);
	}

}
