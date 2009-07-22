

package org.vpac.grisu.client.control.status;

import java.util.Enumeration;
import java.util.Vector;

public class ApplicationStatusManager {
	
	private static ApplicationStatusManager defaultManager = null;
	
	public synchronized static ApplicationStatusManager getDefaultManager() {
		if ( defaultManager == null ) {
			defaultManager = new ApplicationStatusManager();
		} 
		return defaultManager;
	}
	
	private String currentStatus = null;
	
	public String getCurrentStatus() {
		return currentStatus;
	}
	
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
		fireMountPointsEvent(this.currentStatus);
	}
	
	// ---------------------------------------------------------------------------------------
	// Event stuff (MountPoints)
	private Vector<StatusListener> statusListeners;

	private void fireMountPointsEvent(String newStatus) {
		// if we have no mountPointsListeners, do nothing...
		if (statusListeners != null && !statusListeners.isEmpty()) {

			StatusEvent event = new StatusEvent(this, newStatus);
			// make a copy of the listener list in case
			// anyone adds/removes statusListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) statusListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				StatusListener l = (StatusListener) e.nextElement();
				try {
					l.setNewStatus(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// register a listener
	synchronized public void addStatusListener(StatusListener l) {
		if (statusListeners == null)
			statusListeners = new Vector();
		statusListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeStatusListener(StatusListener l) {
		if (statusListeners == null) {
			statusListeners = new Vector<StatusListener>();
		}
		statusListeners.removeElement(l);
	}
	
}
