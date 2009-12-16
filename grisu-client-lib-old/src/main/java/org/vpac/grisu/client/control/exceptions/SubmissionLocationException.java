package org.vpac.grisu.client.control.exceptions;

import org.vpac.grisu.client.model.SubmissionLocation;

public class SubmissionLocationException extends Exception {

	private SubmissionLocation subLoc = null;

	public SubmissionLocationException(String message) {
		super(message);
	}

	public SubmissionLocationException(SubmissionLocation subLoc) {
		super(
				"SubmissionLocation "
						+ subLoc
						+ " not available for this application/version/fqan combination.");
		this.subLoc = subLoc;
	}

	public SubmissionLocation getSubmissionLocation() {
		return subLoc;
	}

}
