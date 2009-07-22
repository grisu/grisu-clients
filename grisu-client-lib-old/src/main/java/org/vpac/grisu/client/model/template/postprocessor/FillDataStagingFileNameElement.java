

package org.vpac.grisu.client.model.template.postprocessor;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.control.JobConstants;
import org.w3c.dom.Element;

public class FillDataStagingFileNameElement extends ElementPostprocessor {

	static final Logger myLogger = Logger
			.getLogger(FillDataStagingFileNameElement.class.getName());

	public FillDataStagingFileNameElement(JsdlTemplate template, Element element) {
		super(template, element);
	}

	@Override
	public void process(String fqan) throws PostProcessException {

		String basename = element.getTextContent();

		// 2 steps down from URI -> Source -> DataStaging
		Element dataStaging = (Element) element.getParentNode()
		.getParentNode();

		// only 1 FileName element
		Element filename = (Element) dataStaging.getElementsByTagName(
			"FileName").item(0);

		if (basename == null || "".equals(basename)
				|| JobConstants.DUMMY_STAGE_FILE.equals(basename)) {
			
			filename.setTextContent(JobConstants.DUMMY_STAGE_FILE);

			myLogger.debug("No user input. Setting value to DUMMY.");
			
		} else {

			basename = basename.substring(basename.lastIndexOf("/") + 1);

			filename.setTextContent(template.getCurrentRelativeJobDirectory()
					+ "/" + basename);
			myLogger.debug("Set FileName element to: "
					+ filename.getTextContent());
		}

	}

	@Override
	public boolean processBeforeJobCreation() {
		return true;
	}

}
