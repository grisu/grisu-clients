package org.vpac.grisu.plugins.fluent;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.plugins.PluginPanel;

public class MainPanel implements PluginPanel {

	private FluentIterationChartPanel fluentIterationChartPanel;

	public JPanel getJPanel() {
		return fluentIterationChartPanel;
	}

	public void initializePanel(GrisuJobMonitoringObject job) {
		fluentIterationChartPanel = new FluentIterationChartPanel();
		fluentIterationChartPanel.setFluentJob(job);
	}

}
