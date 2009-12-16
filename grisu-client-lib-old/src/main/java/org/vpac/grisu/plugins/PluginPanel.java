package org.vpac.grisu.plugins;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

/**
 * Wrapper interface that provides the panel which is created via reflection
 * whenever a class is found that follows this pattern:
 * org.vpac.grisu.plugins.[applicationname].MainPanel
 * 
 * @author markus
 * 
 */
public interface PluginPanel {

	public JPanel getJPanel();

	public void initializePanel(GrisuJobMonitoringObject job);

}
