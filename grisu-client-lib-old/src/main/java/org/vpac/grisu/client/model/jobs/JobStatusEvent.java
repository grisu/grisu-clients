

package org.vpac.grisu.client.model.jobs;

import java.util.EventObject;

public class JobStatusEvent extends EventObject {
	
	public static final int NO_CHANGE = 0;
	public static final int JOB_REFRESHED = 1;
	public static final int JOB_FINISHED = 2;
	
	private int eventType = -1;

	public JobStatusEvent(Object source, int eventType) {
		super(source);
		this.eventType = eventType;
	}

	public int getEventType() {
		return eventType;
	}

}
