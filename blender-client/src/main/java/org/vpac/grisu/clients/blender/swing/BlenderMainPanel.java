package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.BatchJobTabbedPane;
import org.vpac.grisu.frontend.view.swing.login.GrisuSwingClient;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

public class BlenderMainPanel extends JPanel implements GrisuSwingClient {
	private JTabbedPane tabbedPane;
	private BlenderJobCreationPanel blenderJobCreationPanel;
	private BatchJobTabbedPane batchJobTabbedPane;

	private ServiceInterface si;
	private LoginPanel lp;

	/**
	 * Create the panel.
	 */
	public BlenderMainPanel() {
		setLayout(new BorderLayout());

	}

	private BatchJobTabbedPane getBatchJobTabbedPane() {
		if (batchJobTabbedPane == null) {
			batchJobTabbedPane = new BatchJobTabbedPane(si, "blender");
		}
		return batchJobTabbedPane;
	}

	private BlenderJobCreationPanel getBlenderJobCreationPanel() {
		if (blenderJobCreationPanel == null) {
			blenderJobCreationPanel = new BlenderJobCreationPanel(si);
		}
		return blenderJobCreationPanel;
	}

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(SwingConstants.LEFT);
			tabbedPane.addTab("Create job", null, getBlenderJobCreationPanel(),
					null);
			tabbedPane.addTab("Monitor", null, getBatchJobTabbedPane(), null);
		}
		return tabbedPane;
	}

	@Override
	public JPanel getRootPanel() {
		return this;
	}

	@Override
	public void setLoginPanel(LoginPanel lp) {
		this.lp = lp;
	}

	@Override
	public void setServiceInterface(ServiceInterface si) {

		this.si = si;
		add(getTabbedPane(), BorderLayout.CENTER);

	}
}
