

package org.vpac.grisu.plugins.underworld;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.plugins.PluginPanel;

public class MainPanel implements PluginPanel {

	private UnderworldChartPanel underworldChartPanel;

	public JPanel getJPanel() {
		return underworldChartPanel;
	}
	public void initializePanel(GrisuJobMonitoringObject job) {
		underworldChartPanel = new UnderworldChartPanel();
		underworldChartPanel.setUnderworldJob(job);
	}

}
