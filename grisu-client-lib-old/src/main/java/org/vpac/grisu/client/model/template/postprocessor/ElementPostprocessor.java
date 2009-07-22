

package org.vpac.grisu.client.model.template.postprocessor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Element;

public abstract class ElementPostprocessor {
	
	protected JsdlTemplate template = null;
	protected Element element = null;
	
	public static final String DEFAULT_CONFIG_VALUE = "default"; 
	
	protected Map<String, String> config = new LinkedHashMap<String, String>();
	
	public ElementPostprocessor(JsdlTemplate template, Element element) {
		this.template = template;
		this.element = element;
	}
	
	public void setConfig(String configuration) {
		if ( configuration != null ) {
			String[] values = configuration.split(",");
			for ( String value : values ) {
				int index = value.indexOf("=");
				if ( index > -1 ) {
					config.put(value.substring(0, index), value.substring(index+1));
				} else {
					config.put(value, DEFAULT_CONFIG_VALUE);
				}
			}
		}
	}
	
	abstract public void process(String fqan) throws PostProcessException;
	
	abstract public boolean processBeforeJobCreation();

}
