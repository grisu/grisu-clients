package org.vpac.grisu.client.model.files;

public class FileSystemException extends RuntimeException {

	public FileSystemException(String message) {
		super(message);
	}

	public FileSystemException(String message, Exception e) {
		super(message, e);
	}

}
