package org.vpac.grisu.client.model.template.nodes;

import java.util.EventObject;

public class TemplateNodeEvent extends EventObject {

	public static final int UNSPECIFIED = -1;
	public static final int RESET = 0;
	public static final int TEMPLATE_FILLED_INVALID = 1;
	public static final int TEMPLATE_PROCESSED_VALID = 2;
	public static final int TEMPLATE_PROCESSED_INVALID = 3;
	public static final int TEMPLATE_VALUE_UPDATED = 4;

	public static final String DEFAULT_FILLED_MESSAGE = "Input filled.";
	public static final String DEFAULT_PROCESSED_VALID_MESSAGE = "Input successfully processed.";
	public static final String DEFAULT_PROCESSED_INVALID_MESSAGE = "Could not process input.";
	public static final String DEFAULT_FILLED_INVALID_MESSAGE = "Input invalid.";
	public static final String DEFAULT_REQUIRED_INPUT_EMPTY_MESSAGE = "Input required.";

	private int event_type = UNSPECIFIED;
	private String message = null;

	public TemplateNodeEvent(TemplateNode source, String message, int event_type) {
		super(source);
		this.message = message;
		this.event_type = event_type;
	}

	public int getEventType() {
		return this.event_type;
	}

	public String getMessage() {
		return this.message;
	}

	public TemplateNode getSource() {
		return (TemplateNode) this.source;
	}

}
