package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.view.swing.jobmonitoring.BatchJobTabbedPane;

public class BlenderMainPanel extends JPanel {
	private JTabbedPane tabbedPane;
	private BlenderJobCreationPanel blenderJobCreationPanel;
	private BatchJobTabbedPane batchJobTabbedPane;

	private final ServiceInterface si;

	/**
	 * Create the panel.
	 */
	public BlenderMainPanel(ServiceInterface si) {
		this.si = si;
		setLayout(new BorderLayout(0, 0));
		add(getTabbedPane(), BorderLayout.CENTER);

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
}
