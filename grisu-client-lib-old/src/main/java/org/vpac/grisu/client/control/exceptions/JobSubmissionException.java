package org.vpac.grisu.client.control.exceptions;

public class JobSubmissionException extends Exception {

	private Exception parentException = null;
	private String string1 = null;
	private String string2 = null;

	public JobSubmissionException(String message, Exception e) {
		super(message);
		string1 = message;
		parentException = e;
	}

	public JobSubmissionException(String message1, String message2, Exception e) {
		super(message1);
		string1 = message1;
		string2 = message2;
		parentException = e;
	}

	public Exception getParentException() {
		return parentException;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}

}
