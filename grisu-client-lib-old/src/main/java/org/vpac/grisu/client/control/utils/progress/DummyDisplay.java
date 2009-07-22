

package org.vpac.grisu.client.control.utils.progress;

/**
 * Implementation of {@link ProgressDisplay} that does nothing. It's the default behaviour of this client if no other ProgressDisplay is connected.
 * 
 * @author Markus Binsteiner
 *
 */
public class DummyDisplay implements ProgressDisplay {

	public void close() {
		// TODO Auto-generated method stub

	}

	public void setProgress(int step, String description) {
		// TODO Auto-generated method stub

	}

	public void start(int maxSteps, String description) {
		// TODO Auto-generated method stub

	}

}
