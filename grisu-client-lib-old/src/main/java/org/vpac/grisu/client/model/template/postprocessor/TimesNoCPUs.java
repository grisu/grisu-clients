package org.vpac.grisu.client.model.template.postprocessor;

import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Element;

public class TimesNoCPUs extends ElementPostprocessor {

	public TimesNoCPUs(JsdlTemplate template, Element element) {
		super(template, element);
	}

	@Override
	public void process(String fqan) throws PostProcessException {

		String cpus = template.getTemplateNodes().get(
				TemplateTagConstants.CPUS_TAG_NAME).getValue();

		try {
			Integer noCpus = Integer.parseInt(cpus);
			Long wt = Long.parseLong(element.getTextContent());

			Long totalCPUTime = noCpus * wt;
			element.setTextContent(totalCPUTime.toString());
		} catch (NumberFormatException e) {
			throw new PostProcessException("Could not calculate walltime.", e);
		}

	}

	@Override
	public boolean processBeforeJobCreation() {
		return true;
	}

}
