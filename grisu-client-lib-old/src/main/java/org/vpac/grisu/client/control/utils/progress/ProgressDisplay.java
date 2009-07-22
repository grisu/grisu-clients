

package org.vpac.grisu.client.control.utils.progress;

/**
 * This interface has to be implemented when you want to display the progress of certain actions of a grisu client
 * 
 * @author Markus Binsteiner
 *
 */
public interface ProgressDisplay {
	
	
	/**
	 * This method has to be called when the action starts.
	 * @param maxSteps the max amount of steps until the action is finished
	 * @param description a description of the action
	 */
	public void start(int maxSteps, String description);
	
	/**
	 * This indicates where the action is at the moment.
	 * 
	 * @param step the current step
	 * @param description a description of the current (next, actually) action
	 */
	public void setProgress(int step, String description);
	
	/**
	 * Call this one when the action is finished. Esp. important if the max amount of steps was not necessary (maybe because the action was interrupted).
	 */
	public void close();
	
}
