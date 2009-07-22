

package org.vpac.grisu.plugins.namd;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.plugins.PluginPanel;

public class MainPanel implements PluginPanel {
	
	private NamdPanel namdPanel = null;

	public JPanel getJPanel() {
		return namdPanel;
	}

	public void initializePanel(GrisuJobMonitoringObject job) {
		namdPanel = new NamdPanel();
		namdPanel.setNamdJob(job);
	}

}
