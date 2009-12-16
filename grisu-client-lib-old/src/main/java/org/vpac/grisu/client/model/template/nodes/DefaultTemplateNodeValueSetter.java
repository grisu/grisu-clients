package org.vpac.grisu.client.model.template.nodes;

/**
 * This class is a wrapper for templatenodes within modules to get/set values.
 * 
 * @author Markus Binsteiner
 * 
 */
public class DefaultTemplateNodeValueSetter implements TemplateNodeValueSetter {

	private String value = null;

	public String getExternalSetValue() {
		return value;
	}

	public void setExternalSetValue(String value) {
		this.value = value;
	}

}
