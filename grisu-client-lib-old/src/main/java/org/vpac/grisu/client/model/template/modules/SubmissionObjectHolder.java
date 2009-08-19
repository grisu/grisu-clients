package org.vpac.grisu.client.model.template.modules;

import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.model.SubmissionObject;

public interface SubmissionObjectHolder {
	
	public void addSubmissionObjectListener(SubmissionObjectListener l);
	
	public void removeSubmissionObjectListener(SubmissionObjectListener l);
	
	public SubmissionObject getCurrentSubmissionObject() throws JobCreationException;
	
}
