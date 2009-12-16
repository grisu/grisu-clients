package org.vpac.grisu.client.model.template;

import java.util.EventObject;

public class JsdlTemplateEvent extends EventObject {

	private String message = null;

	public JsdlTemplateEvent(Object source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
