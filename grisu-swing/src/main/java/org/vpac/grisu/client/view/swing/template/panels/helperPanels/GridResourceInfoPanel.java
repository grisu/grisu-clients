package org.vpac.grisu.client.view.swing.template.panels.helperPanels;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextPane;

import au.org.arcs.jcommons.interfaces.GridResource;

public class GridResourceInfoPanel extends JPanel{
	
	private JTextPane textPane;
	
	private GridResource resource;
	
	public GridResourceInfoPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getTextPane(), BorderLayout.CENTER);
	}

	private JTextPane getTextPane() {
		if (textPane == null) {
			textPane = new JTextPane();
		}
		return textPane;
	}
	
	
	public void setGridResource(GridResource resource) {
		this.resource = resource;
		
		StringBuffer text = new StringBuffer();
		text.append("Free job slots: "+resource.getFreeJobSlots()+"\n");
		text.append("Waiting jobs: "+resource.getWaitingJobs()+"\n");
		text.append("Total jobs: "+resource.getTotalJobs()+"\n");
		text.append("Running jobs: "+resource.getRunningJobs()+"\n");
		text.append("Memory RAM size: "+resource.getMainMemoryRAMSize()+"\n");
		
		getTextPane().setText(text.toString());
	}
	
}
