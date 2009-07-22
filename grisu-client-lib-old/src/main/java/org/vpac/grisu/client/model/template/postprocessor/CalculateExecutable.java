package org.vpac.grisu.client.model.template.postprocessor;

import java.util.Map;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Element;

public class CalculateExecutable extends ElementPostprocessor {

	public CalculateExecutable(JsdlTemplate template, Element element) {
		super(template, element);
	}

	@Override
	public void process(String fqan) throws PostProcessException {
		
		String application = template.getApplicationName();
		String site = template.getCurrentSubmissionSite();
		Map<String, String> appDetails = template.getEnvironmentManager().getServiceInterface().getApplicationDetailsForSite(application, site).getDetailsAsMap();
		
		if ( appDetails.get("Executables") == null || "".equals(appDetails.get("Executables")) ) {
			// not a great solution, but maybe it'll work
			throw new PostProcessException("Could not calculate executable for application: "+application+".\n"+
					"Please contact the local grid-administrator or help@arcs.org.au and\nask for this information to be added to mds.", null);
//			element.setTextContent(application);
		} else {
			String exe = appDetails.get("Executables").split(",")[0];
			element.setTextContent(exe);
		}
		
	}

	@Override
	public boolean processBeforeJobCreation() {
		return true;
	}
}
