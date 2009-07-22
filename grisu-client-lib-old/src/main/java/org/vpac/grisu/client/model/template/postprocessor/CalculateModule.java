package org.vpac.grisu.client.model.template.postprocessor;

import java.util.Map;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Element;

public class CalculateModule extends ElementPostprocessor {

	public CalculateModule(JsdlTemplate template, Element element) {
		super(template, element);
	}

	@Override
	public void process(String fqan) throws PostProcessException {
		
		String application = template.getApplicationName();
		String site = template.getCurrentSubmissionSite();
		Map<String, String> appDetails = template.getEnvironmentManager().getServiceInterface().getApplicationDetailsForSite(application, site).getDetailsAsMap();
		
		if ( appDetails.get("Module") != null && !"".equals(appDetails.get("Module")) ) {
			element.setTextContent(appDetails.get("Module"));
		}
	}

	@Override
	public boolean processBeforeJobCreation() {
		return true;
	}

}
