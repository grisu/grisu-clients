package org.vpac.grisu.client.model.template.postprocessor;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import au.org.arcs.jcommons.constants.JSDLNamespaceContext;

/**
 * Substitutes a specified string with the template with the value of this
 * element. For example, if you specify: postprocess=Substitute[name=INPUT_FILE]
 * then all instances of ${INPUT_FILE} in the template are replaced. You can do
 * string manipulation before replacing. The only one supported at the moment is
 * basename: if you have ${INPUT_FILE}[basename] specified for the string you
 * want replaced, this class calculates the filename of the value of this
 * element and fills it in.
 * 
 * @author Markus Binsteiner
 * 
 */
public class Substitute extends ElementPostprocessor {

	private static XPath xpath = getXPath();

	public static final String BASENAME_KEY = "basename";

	// public static final String BASENAME_RELATIVE_TO_EXECUTION_HOST_FS_KEY =
	// "basenameRelativeToExecutionHostFs";
	public static final String PREFIX_KEY = "prefix";
	public static final String BASENAMEPREFIX_KEY = "basenameprefix";
	public static final String FILEEXTENSION_KEY = "basenamechangeextension";
	public static final String NAME_KEY = "name";
	public static final String SUBSTITUTE_VARIABLE_ATTRIBUTE_NAME = "substitute";

	static final Logger myLogger = Logger.getLogger(Substitute.class.getName());

	private static final XPath getXPath() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new JSDLNamespaceContext());
		return xpath;
	}

	public Substitute(JsdlTemplate template, Element element) {
		super(template, element);
	}

	private String basename(String replacement) {
		String basename = replacement
				.substring(replacement.lastIndexOf("/") + 1);
		return basename;
	}

	private String calculateReplacementString(String replacement, String config) {

		if (BASENAME_KEY.equals(config)) {

			String basename = basename(replacement);
			myLogger.debug("Calculated replacement for \"" + replacement
					+ "\" using method \"" + config + "\": " + basename);
			return basename;

			// } else if (
			// BASENAME_RELATIVE_TO_EXECUTION_HOST_FS_KEY.equals(cobnfig) ) {
			//			
			// String basename = basename(replacement);
			// String directory = template.getCurrentRelativeJobDirectory();
			//			
			// if ( directory.endsWith("/") ) {
			// return directory+basename;
			// } else {
			// return directory+"/"+basename;
			// }
			//			
		} else if (config.startsWith(PREFIX_KEY)) {

			int start = config.indexOf("(");
			int end = config.lastIndexOf(")");
			String prefix = config.substring(start + 1, end);

			String prefixed = prefix + replacement;
			myLogger.debug("Calculated replacement for \"" + replacement
					+ "\" using method \"" + config + "\": " + prefixed);
			return prefixed;

		} else if (config.startsWith(BASENAMEPREFIX_KEY)) {

			String basename = basename(replacement);

			int start = config.indexOf("(");
			int end = config.lastIndexOf(")");
			String prefix = config.substring(start + 1, end);

			String prefixed = prefix + basename;
			return prefixed;

		}
		if (config.startsWith(FILEEXTENSION_KEY)) {

			String basename = basename(replacement);

			int start = config.indexOf("(");
			int end = config.lastIndexOf(")");
			String extension = config.substring(start + 1, end);

			String base_basename = basename.substring(0, basename
					.lastIndexOf("."));
			return base_basename + "." + extension;

		} else {
			// do nothing
			myLogger
					.debug("Could not find config for replacement. Not replaceing anything.");
			return replacement;
		}

	}

	@Override
	public void process(String fqan) throws PostProcessException {

		String variableName = config.get(NAME_KEY);
		if (variableName == null || "".equals(variableName))
			throw new PostProcessException(
					"No name specified to for replacement. Aborting postprocessing.",
					null);

		substituteVariable(template.getTemplateDocument(), variableName);

	}

	@Override
	public boolean processBeforeJobCreation() {
		return true;
	}

	private void substituteVariable(Document jsdl, String name)
			throws PostProcessException {

		String jsdl_string;
		// try {
		// jsdl_string = SeveralXMLHelpers.toString(jsdl);
		// myLogger.debug("JSDL before substitution:\n------------------------\n\n"+jsdl_string);
		// } catch (Exception e) {
		// myLogger.error("Could not transform jsdl to String: "
		// + e.getMessage());
		// throw new PostProcessException("Could not transform jsdl to string.",
		// e);
		// }

		String expression = "//@substitute";
		NodeList resultNodes = null;
		try {
			resultNodes = (NodeList) xpath.evaluate(expression, jsdl,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			myLogger
					.warn("No substitute attributes for this variable in jsdl file.");
			return;
		}

		ArrayList<Element> substituteElements = new ArrayList<Element>();
		for (int i = 0; i < resultNodes.getLength(); i++) {

			Attr node = (Attr) resultNodes.item(i);

			Element element = node.getOwnerElement();
			substituteElements.add(element);
		}

		for (Element substituteElement : substituteElements) {

			String variableName = substituteElement
					.getAttribute(SUBSTITUTE_VARIABLE_ATTRIBUTE_NAME);
			String configText = null;
			int configStartIndex = variableName.indexOf("[");
			if (configStartIndex != -1) {
				int configEndIndex = variableName.indexOf("]");
				if (configEndIndex == -1)
					throw new PostProcessException(
							"Could not find end tag (\"]\") for substitute variable: "
									+ variableName, null);
				configText = variableName.substring(configStartIndex + 1,
						configEndIndex);
				variableName = variableName.substring(0, configStartIndex);
				myLogger.debug("Config string for substitute variable \""
						+ variableName + "\": " + configText);
			}

			if (name.equals(variableName)) {
				// do the substitution
				try {
					String calculatedReplacement = calculateReplacementString(
							element.getTextContent(), configText);

					substituteElement.setTextContent(calculatedReplacement);
				} catch (Exception e) {
					throw new PostProcessException(
							"Could not calculate replacement string for string \""
									+ element.getTextContent()
									+ "\" using method \"" + configText + "\"",
							e);
				}
				// // clean up substitute attribute
				// substituteElement.removeAttribute(SUBSTITUTE_VARIABLE_ATTRIBUTE_NAME);

			}
		}

	}

}
