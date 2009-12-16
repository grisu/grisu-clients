package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.postprocessor.PostprocessorFactory;

/**
 * A TemplateProcessorFacory provides a simple factory method to create template
 * preprocessors via reflection. If you don't provide the full path to your
 * preprocessor class it has to be a class in the
 * org.vpac.grisu.client.model.template.nodes.templatePreProcessor package.
 * 
 * @author Markus Binsteiner
 * 
 */
public class TemplateProcessorFactory {

	static final Logger myLogger = Logger.getLogger(PostprocessorFactory.class
			.getName());

	public static TemplatePreProcessor createPreprocessor(TemplateNode node) {

		TemplatePreProcessor processor = null;
		try {

			Class processorClass = null;

			if (node.getType() != null && !"".equals(node.getType())) {

				if (node.getType().indexOf(".") == -1) {
					// means grisu default type
					processorClass = Class
							.forName("org.vpac.grisu.client.model.template.nodes.templatePreProcessor."
									+ node.getType());
				} else {
					// means custom type
					processorClass = Class.forName(node.getType());
				}

				Constructor processorConstructor = null;
				try {
					processorConstructor = processorClass
							.getConstructor(TemplateNode.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				processor = (TemplatePreProcessor) processorConstructor
						.newInstance(new Object[] { node });
			}
		} catch (Exception e) {
			myLogger.warn("Could not create template processor \""
					+ node.getType() + "\": " + e.getLocalizedMessage());
			return null;
		}

		return processor;
	}

}
