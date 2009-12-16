package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.w3c.dom.Element;

public class InputFile extends TemplatePreProcessor {

	public static void addPostProcessUploadAttribute(Element element) {

		String currentpostprocess = element.getAttribute("postprocess");

		if (currentpostprocess == null || "".equals(currentpostprocess)) {
			// we set a new postprocess attribute
			element.setAttribute("postprocess", "Upload");
		} else if (currentpostprocess.indexOf("Upload") != -1) {
			// do nothing
		} else {
			// we add the upload postprocess to the existing one(s). we push it
			// at front because most likely a substitute postprocess will follow
			String newpostprocess = "Upload," + currentpostprocess;
			element.setAttribute("postprocess", newpostprocess);
		}

	}

	public static void calculatePostProcessUploadAttribute(String url,
			Element element) {

		if (url.startsWith("file:")) {

			addPostProcessUploadAttribute(element);

		} else {

			removePostProcessUploadAttribute(element);

		}

	}

	public static void removePostProcessUploadAttribute(Element element) {
		String currentpostprocess = element.getAttribute("postprocess");
		if (currentpostprocess != null && !"".equals(currentpostprocess)) {
			int startIndex = currentpostprocess.indexOf("Upload");
			if (startIndex != -1) {
				int endIndex = currentpostprocess.indexOf(",", startIndex);
				if (endIndex == -1) {
					// means upload is the only postprocessor
					element.removeAttribute("postprocess");
					return;
				}
				StringBuffer temp = new StringBuffer(currentpostprocess
						.substring(0, startIndex));
				temp.append(currentpostprocess.substring(endIndex + 1));

				element.setAttribute("postprocess", temp.toString());
			}
		}
	}

	protected Element originalElement = null;

	public InputFile(TemplateNode node) {
		super(node);
	}

	@Override
	public void process() throws TemplatePreProcessorException {

		if (originalElement == null) {
			originalElement = (Element) node.getElement().cloneNode(true);
		}

		String input = node.getValue();

		calculatePostProcessUploadAttribute(input, node.getElement());

	}

	@Override
	protected void resetTemplateNode() {
		// nothing to do here
	}

}
