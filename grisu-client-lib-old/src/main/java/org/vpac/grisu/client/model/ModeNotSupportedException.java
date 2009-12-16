package org.vpac.grisu.client.model;

public class ModeNotSupportedException extends Exception {

	private int mode = -1;

	public ModeNotSupportedException(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return this.mode;
	}

}
