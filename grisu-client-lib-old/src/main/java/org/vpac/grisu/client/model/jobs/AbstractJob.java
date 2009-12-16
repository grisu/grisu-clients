package org.vpac.grisu.client.model.jobs;

import java.util.Enumeration;
import java.util.Vector;

public abstract class AbstractJob {

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<JobStatusListener> jobStatusListeners;

	// register a listener
	synchronized public void addJobStatusListener(JobStatusListener l) {
		if (jobStatusListeners == null)
			jobStatusListeners = new Vector();
		jobStatusListeners.addElement(l);
	}

	protected void fireJobStatusEvent(int event_type) {
		// if we have no mountPointsListeners, do nothing...
		if (jobStatusListeners != null && !jobStatusListeners.isEmpty()) {
			// create the event object to send
			JobStatusEvent event = new JobStatusEvent(this, event_type);

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) jobStatusListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				JobStatusListener l = (JobStatusListener) e.nextElement();
				try {
					l.jobStatusChanged(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// remove a listener
	synchronized public void removeJobStatusListener(JobStatusListener l) {
		if (jobStatusListeners == null) {
			jobStatusListeners = new Vector<JobStatusListener>();
		}
		jobStatusListeners.removeElement(l);
	}

}
