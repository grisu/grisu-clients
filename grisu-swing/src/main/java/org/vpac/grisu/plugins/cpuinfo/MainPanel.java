package org.vpac.grisu.plugins.cpuinfo;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.preview.TextViewerPanel;
import org.vpac.grisu.plugins.PluginPanel;

public class MainPanel implements PluginPanel {

	private TextViewerPanel viewerPanel = null;

	public JPanel getJPanel() {
		return viewerPanel;
	}

	public void initializePanel(GrisuJobMonitoringObject job) {
		viewerPanel = new TextViewerPanel();
		GrisuFileObject stdout = null;
		try {
			stdout = job.getJobDirectoryObject().getFileSystemBackend()
					.getFileObject(new URI(job.getStdout()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewerPanel.setFileToPreview(stdout);
	}

}
