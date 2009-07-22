

package org.vpac.grisu.client.control.status;

import java.util.EventObject;

public class StatusEvent extends EventObject {
	
	private String status = null;
	
	public StatusEvent(Object source, String status) {
		super(source);
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

}
