package org.vpac.grisu.client.control.exceptions;

import java.util.Map;

public class FailedDownloadsException extends Exception {

	private Map<String, Exception> failedDownloads = null;

	public FailedDownloadsException(Map<String, Exception> failedDownloads) {
		this.failedDownloads = failedDownloads;
	}

	public Map<String, Exception> getFailedDownloads() {
		return failedDownloads;
	}

}
