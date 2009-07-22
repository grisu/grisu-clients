

package org.vpac.grisu.client.model.template.postprocessor;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Element;

public class FillWithJobDirectoryRelativeToExecutionFileSystem extends
		ElementPostprocessor {

	public FillWithJobDirectoryRelativeToExecutionFileSystem(JsdlTemplate template, Element element) {
		super(template, element);
	}
	
	@Override
	public void process(String fqan) throws PostProcessException {
		
		element.setTextContent(template.getCurrentRelativeJobDirectory());
		
	}

	@Override
	public boolean processBeforeJobCreation() {
		return true;
	}

}
