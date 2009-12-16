package org.vpac.grisu.client.control.generic;

import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.model.template.JsdlTemplateEvent;
import org.vpac.grisu.client.model.template.JsdlTemplateListener;

public class SimpleJsdlListener implements JsdlTemplateListener {

	private JobSubmissionException e = null;

	public JobSubmissionException getSubmissionException() {
		return e;
	}

	public void submissionExceptionOccured(JsdlTemplateEvent event,
			JobSubmissionException exception) {
		e = exception;
		System.err.println(event.getMessage() + ": ["
				+ exception.getLocalizedMessage() + "]");
		if (exception.getParentException() != null) {
			System.err.println(event.getMessage() + "Parent exception: "
					+ exception.getParentException().getLocalizedMessage());
		}

	}

	public void templateStatusChanged(JsdlTemplateEvent event) {
		System.out.println(event.getMessage() + ".");
	}

}
