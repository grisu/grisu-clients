package org.vpac.grisu.client.jobCreation;

import java.io.Serializable;

public class JobCreationException extends Exception implements Serializable {
	
	public JobCreationException() {
		
	}

	public JobCreationException(String message) {
		super(message);
	}
	
}
