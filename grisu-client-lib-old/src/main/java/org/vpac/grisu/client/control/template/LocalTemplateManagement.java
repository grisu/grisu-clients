package org.vpac.grisu.client.control.template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.JsdlTemplateException;
import org.vpac.grisu.settings.Environment;
import org.w3c.dom.Document;

/**
 * Manages all jsdl templates that are in $HOME/.grisu/templates.
 * 
 * @author Markus Binsteiner
 * 
 */
public class LocalTemplateManagement {

	final public static String TEMPLATE_DIRECTORY = Environment
	.getGrisuClientDirectory().getPath()
	+ File.separator + "templates";

	// final public static String AVAILABLE_TEMPLATES_DIRECTORY =
	// Environment.GRISU_DIRECTORY+File.separator+"templates_available";

	// /**
	// * Returns all templates in $HOME/.grisu/templates as xml Documents.
	// * @return all templates
	// */
	// public static Document[] getAllTemplates() {
	//
	// File[] templates = new File(TEMPLATE_DIRECTORY).listFiles();
	// Document[] document_templates = new Document[templates.length];
	//
	// for ( int i=0; i<templates.length; i++ ) {
	// try {
	// document_templates[i] = loadJsdlFile(templates[i]);
	// } catch (JsdlTemplateException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// // do nothing for now
	// }
	// }
	//
	// return document_templates;
	// }

	/**
	 * Returns a map of all templates using the name of the template xml file
	 * (in $HOME/.grisu/templates) without the ".xml" extension as key and the
	 * xml Document itself as value.
	 * 
	 * @return a map of all available templates.
	 */
	public static Map<String, JsdlTemplate> getAllTemplatesWithFilenames(
			EnvironmentManager em) {

		Map<String, JsdlTemplate> result = new HashMap<String, JsdlTemplate>();

		File tempDir = new File(TEMPLATE_DIRECTORY);

		if (!tempDir.exists()) {
			if (!tempDir.mkdirs()) {
				System.out
				.println("Could not create directory $HOME/.grisu/templates. Please create it manually and make it writable by the current user.");
				System.exit(1);
			}
		}

		File[] templates = tempDir.listFiles();

		for (File file : templates) {
			try {
				result.put(file.getName().substring(0,
						file.getName().lastIndexOf(".xml")), new JsdlTemplate(
								em, loadJsdlFile(file)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// do nothing for now
			}
		}

		return result;
	}

	private static Document loadJsdlFile(File file)
	throws JsdlTemplateException {

		Document jsdl = null;

		final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
		final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

		// File schemaFile = new
		// File("/home/markus/workspace/nw-core/jsdl.xsd");

		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory
		.newInstance();
		docBuildFactory.setNamespaceAware(true);
		docBuildFactory.setValidating(false);

		docBuildFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA); // use
		// LANGUAGE
		// here
		// instead
		// of
		// SOURCE
		// docBuildFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);

		try {
			DocumentBuilder documentBuilder = docBuildFactory
			.newDocumentBuilder();
			jsdl = documentBuilder.parse(file);
			// JsdlHelpers.validateJSDL(jsdl);

		} catch (Exception e1) {
			throw new JsdlTemplateException("Could not create JsdlTemplate: "
					+ e1.getLocalizedMessage());
		}

		return jsdl;
	}

	/**
	 * Writes the jsdl template to disk (after it was retrieved from the grisu
	 * ServiceInterface)
	 * 
	 * @param jsdl
	 *            the jsdl template
	 * @param filename
	 *            the name of the file to store it in
	 * @return whether the write process was successful or not
	 */
	public static boolean writeJsdlTemplate(Document jsdl, String filename) {

		if (!filename.endsWith(".xml")) {
			filename = filename + ".xml";
		}

		try {
			// TODO use static transformer to reduce overhead?
			Transformer transformer = TransformerFactory.newInstance()
			.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// initialize StreamResult with InputFile object to save to file
			StreamResult result = null;
			result = new StreamResult(new FileWriter(new File(
					TEMPLATE_DIRECTORY + File.separator + filename)));
			DOMSource source = new DOMSource(jsdl);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
