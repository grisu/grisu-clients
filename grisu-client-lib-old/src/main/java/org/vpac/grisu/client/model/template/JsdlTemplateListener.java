

package org.vpac.grisu.client.model.template;

import java.util.EventListener;

import org.vpac.grisu.client.control.exceptions.JobSubmissionException;

public interface JsdlTemplateListener extends EventListener {

	public void templateStatusChanged(JsdlTemplateEvent event);
	
	public void submissionExceptionOccured(JsdlTemplateEvent event, JobSubmissionException exception);
	
}
