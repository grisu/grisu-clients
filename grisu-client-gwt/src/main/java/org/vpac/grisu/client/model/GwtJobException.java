package org.vpac.grisu.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GwtJobException extends Exception implements IsSerializable {
	
	public GwtJobException() {
		
	}
	
	public GwtJobException(String message) {
		super(message);
	}

}
