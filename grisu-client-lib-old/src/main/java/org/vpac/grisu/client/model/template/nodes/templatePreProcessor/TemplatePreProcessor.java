package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.w3c.dom.Element;

/**
 * A TemplatePreProcessor takes a filled {@link TemplateNode} and processes the
 * input before the job is submitted. This is useful for example if an input
 * file has to be staged in from the desktop machine. In this case the
 * {@link InputFile} processor tags the {@link TemplateNode} with a postprocess
 * tag.
 * 
 * Be aware, if you implement a TemplatePreProcessor, that you check every time
 * whether the TemplateNode was already preprocessed (with your
 * TemplatePreProcessor) because it is possible that a job submission failed
 * after preprocessing and now the user tries to submit the same template again.
 * Have a look at the {@link InputFile} preprocessor source code for an example.
 * This one tags the TemplateNodeElement with the postprocess attribute "Upload"
 * if it contains a local (desktop) file. If there was no check, the postprocess
 * attribute would look like: postprocess="Upload:Upload:Substitute" if the
 * first job submission failed...
 * 
 * That's why I also added the reset() method now. This get's called whenever a
 * jobsubmission failed or finished successfully.
 * 
 * @author Markus Binsteiner
 * 
 */
abstract public class TemplatePreProcessor {

	static final Logger myLogger = Logger.getLogger(TemplatePreProcessor.class
			.getName());

	protected TemplateNode node = null;

	protected Element originalElement = null;

	public TemplatePreProcessor(TemplateNode node) {
		this.node = node;
		if (originalElement == null) {
			originalElement = (Element) node.getElement().cloneNode(true);
		}
	}

	/**
	 * Processes this preprocessor. This is used for example to multiply
	 * elements in the jsdl document or to tag postprocessors.
	 * 
	 * @throws TemplatePreProcessorException
	 */
	abstract public void process() throws TemplatePreProcessorException;

	/**
	 * Restors the original state of the element of this template node after a
	 * failed or successfully finished job submission.
	 * 
	 * @throws TemplatePreProcessorException
	 */
	public void reset() {

		resetTemplateNode();

		// Utils.jsdlDebugOutput("before resetting node: "+node.getName(),
		// node.getTemplate().getTemplateDocument());
		myLogger.debug("Replacing changed element with original one for node: "
				+ node.getName());
		((Element) node.getElement().getParentNode()).replaceChild(
				originalElement, node.getElement());

		node.setElement(originalElement);

		// Utils.jsdlDebugOutput("after resetting node: "+node.getName(),
		// node.getTemplate().getTemplateDocument());

	}

	/**
	 * This cleans up after a failed or successfully finished job submission.
	 * You have to restore the status of the Template to what it was before
	 * process(). Used at the moment to clean the jsdl document from up clones
	 * of the element.
	 * 
	 * May get deprecated later...
	 * 
	 * @throws TemplatePreProcessorException
	 */
	abstract protected void resetTemplateNode();
}
