package org.vpac.grisu.client.control.template;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchTemplateException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

import au.org.arcs.jcommons.utils.JsdlHelpers;

/**
 * This class manages all templates a user choose to use. There are 2 different
 * kinds of templates. Local ones that reside in $HOME/.grisu/templates and
 * remote ones which are delivered via the {@link ServiceInterface}. Should 2
 * templates with the same name exist, the local one is used. Remote templates
 * are dowloaded every time the grisu client connects to the server/backend.
 * 
 * @author Markus Binsteiner
 * 
 */
public class TemplateManager {

	static final Logger myLogger = Logger.getLogger(TemplateManager.class
			.getName());

	public static final int NO_SPECIFIED_TEMPLATE_LOCATION = -1;

	public static final int LOCAL_TEMPLATE_LOCATION = 0;
	public static final int REMOTE_TEMPLATE_LOCATION = 1;

	public static void main(String[] args) throws ServiceInterfaceException {

		LoginParams params = new LoginParams(ClientPropertiesManager
				.getDefaultServiceInterfaceUrl(), "markus",
				"xxx".toCharArray(), MyProxyServerParams.getMyProxyServer(),
				new Integer(MyProxyServerParams.getMyProxyPort()).toString());

		ServiceInterface serviceInterface = ServiceInterfaceFactoryOld
		.createInterface(params);

		EnvironmentManager em = new EnvironmentManager(serviceInterface);
		// EnvironmentManager.setDefaultServiceInterface(serviceInterface);

		TemplateManager tm = new TemplateManager(em);

		for (String templateName : tm.getLocalTemplates().keySet()) {

			System.out.println("Local template: " + templateName);
			JsdlTemplate template = tm.getTemplate(templateName);
			System.out.println(JsdlHelpers.getDescription(template
					.getTemplateDocument()));
			// System.out.println(template.getTemplateInfo());
			// System.out.println(template.getCommandLineOptions());

		}

		for (String templateName : tm.getServerTemplates().keySet()) {

			System.out.println("Server template: " + templateName);
			JsdlTemplate template = tm.getTemplate(templateName);
			// System.out.println(template.getTemplateInfo());
			// System.out.println(template.getCommandLineOptions());

		}

	}

	private EnvironmentManager em = null;
	private final Map<String, JsdlTemplate> serverTemplates = new TreeMap<String, JsdlTemplate>();

	private Map<String, JsdlTemplate> localTemplates = null;

	public TemplateManager(EnvironmentManager em) {

		this.em = em;

		String[] serverTemplateNames = ClientPropertiesManager
		.getServerTemplates();
		ArrayList<String> failedTemplates = new ArrayList<String>();

		for (String serverTemplateName : serverTemplateNames) {
			try {
				putIntoServerTemplatesMap(serverTemplateName);
			} catch (Exception e) {
				myLogger.error("Could not add server template: "
						+ serverTemplateName);
				failedTemplates.add(serverTemplateName);
			}
		}

		// check whether to autoload a template based on environment variable
		String defaultApplication = System.getProperty("grisu.defaultApplication");
		if ( StringUtils.isNotBlank(defaultApplication) ) {
			addServerTemplate(defaultApplication);
		}

	}

	/**
	 * Copies a local xml file into the template directory and updates the list
	 * of local templates
	 * 
	 * @param templateFile
	 *            the local templates. Has to end with ".xml".
	 * @param overwrite
	 *            whether to overwrite a possibly existing file.
	 * @return the name of the new template (without extension)
	 * @throws IOException
	 *             if the file can't be copied for some reason
	 */
	public String addLocalTemplate(File templateFile, boolean overwrite)
	throws IOException {
		File newFile = new File(LocalTemplateManagement.TEMPLATE_DIRECTORY,
				templateFile.getName());

		if (!newFile.toString().endsWith(".xml")) {
			throw new IOException(
			"File doesn't end with \".xml\". Won't copy it into local template store...");
		}

		if (newFile.exists() && !overwrite) {
			throw new IOException("File " + newFile.toString()
					+ " exists. Won't overwrite.");
		}

		FileUtils.copyFile(templateFile, newFile);

		localTemplates = null;
		return newFile.getName().substring(0, newFile.getName().length() - 4);
	}

	/**
	 * Copies a remote template into the local template store. If a template
	 * with the same name already exists there, a new name is used (name_1.xml,
	 * name_2.xml and so on).
	 * 
	 * @param templateName
	 *            the (remote) template name
	 */
	public String addLocalTemplate(String templateName) {

		// getting remote one
		JsdlTemplate template = getTemplate(templateName);

		String tempDir = LocalTemplateManagement.TEMPLATE_DIRECTORY;

		File newTemp = new File(tempDir, templateName + ".xml");
		int i = 1;
		while ((newTemp == null) || newTemp.exists()) {
			i++;
			newTemp = new File(tempDir, templateName + "_" + i + ".xml");
		}

		LocalTemplateManagement.writeJsdlTemplate(template
				.getTemplateDocument(), newTemp.getName());
		localTemplates = null;
		return newTemp.getName().substring(0, newTemp.getName().length() - 4);
	}

	public void addServerTemplate(String templateName) {

		if (serverTemplates.get(templateName) == null) {

			ClientPropertiesManager.addServerTemplate(templateName);

			try {
				putIntoServerTemplatesMap(templateName);
			} catch (NoSuchTemplateException e) {
				myLogger
				.error("Could not add server template: " + templateName);
			}
		}

	}

	public EnvironmentManager getEnvironmentManager() {
		return em;
	}

	public Map<String, JsdlTemplate> getLocalTemplates() {
		if (localTemplates == null) {
			localTemplates = LocalTemplateManagement
			.getAllTemplatesWithFilenames(em);
		}
		return localTemplates;
	}

	public Map<String, JsdlTemplate> getServerTemplates() {
		return serverTemplates;
	}

	public JsdlTemplate getTemplate(int location, String templateName) {

		if (location == LOCAL_TEMPLATE_LOCATION) {
			return getLocalTemplates().get(templateName);
		} else if (location == REMOTE_TEMPLATE_LOCATION) {
			return serverTemplates.get(templateName);
		} else if (location == NO_SPECIFIED_TEMPLATE_LOCATION) {
			JsdlTemplate temp = getLocalTemplates().get(templateName);
			if (temp == null) {
				temp = serverTemplates.get(templateName);
			}
			return temp;
		} else {
			return null;
		}
	}

	public JsdlTemplate getTemplate(String templateName) {
		return getTemplate(NO_SPECIFIED_TEMPLATE_LOCATION, templateName);
	}

	private void putIntoServerTemplatesMap(String templateName)
	throws NoSuchTemplateException {
		Document jsdlDoc = SeveralXMLHelpers.fromString(em
				.getServiceInterface().getTemplate(templateName));
		serverTemplates.put(templateName, new JsdlTemplate(em, jsdlDoc));
	}

	public void refreshLocalTemplates() {
		localTemplates = null;
		getLocalTemplates();
	}

	public void refreshServerTemplate(String templateName)
	throws NoSuchTemplateException {
		Document jsdlDoc = SeveralXMLHelpers.fromString(em
				.getServiceInterface().getTemplate(templateName));
		serverTemplates.put(templateName, new JsdlTemplate(em, jsdlDoc));
	}

	public void removeLocalTemplate(String templateName) {

		File tempToRemove = new File(
				LocalTemplateManagement.TEMPLATE_DIRECTORY, templateName
				+ ".xml");

		tempToRemove.delete();

	}

	public void removeServerTemplate(String templateName) {

		ClientPropertiesManager.removeServerTemplate(templateName);

	}

}
