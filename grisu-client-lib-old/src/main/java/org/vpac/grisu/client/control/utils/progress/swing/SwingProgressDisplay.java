

package org.vpac.grisu.client.control.utils.progress.swing;

import java.awt.Component;

import org.vpac.grisu.client.control.utils.progress.ProgressDisplay;

/**
 * Implementation of {@link ProgressDisplay} to display a swing progress dialog.
 * 
 * @author Markus Binsteiner
 *
 */
public class SwingProgressDisplay implements ProgressDisplay {
	
	private Component owner = null;
	private ProgressMonitor monitor = null;
	
	public SwingProgressDisplay(Component owner) {
		this.owner = owner;
	}
	
	public void start(int maxSteps, String description) {
		monitor = ProgressUtil.createModalProgressMonitor(owner, maxSteps, false, 100); 
		monitor.start(description); 
	}

	public void setProgress(int step, String description) {
		monitor.setCurrent(description, step); 
	}
	
	public void close() {
        if(monitor.getCurrent()!=monitor.getTotal()) 
            monitor.setCurrent(null, monitor.getTotal()); 
        
        
	}

}
