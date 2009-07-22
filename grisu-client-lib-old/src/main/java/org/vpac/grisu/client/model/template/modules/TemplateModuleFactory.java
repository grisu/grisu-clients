

package org.vpac.grisu.client.model.template.modules;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.TemplateHelperUtils;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.w3c.dom.NodeList;

import au.org.arcs.mds.JSDLNamespaceContext;


public class TemplateModuleFactory {
	
	static final Logger myLogger = Logger.getLogger(TemplateModuleFactory.class.getName());
	
	private static XPath xpath = getXPath();

	private static final XPath getXPath() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new JSDLNamespaceContext());
		return xpath;
	}
	
	/**
	 * Creates a module of the specified type. After the module is created it gets "populated" with the template nodes
	 * it requires. These get deleted from the provided list. If a template element is missing to create a module,
	 * nothing gets created and null is returned.
	 * @param templateNodes all the (remaining) template nodes for this jsdltemplate
	 * @return the module
	 */
	public static TemplateModule createTemplateModule(JsdlTemplate template, String modulebasePath, String moduleTypeWithOptionalConfig) {
		
		String moduleType = TemplateHelperUtils.getParameterName(moduleTypeWithOptionalConfig);
		Map<String, String> moduleConfig = TemplateHelperUtils.getConfig(moduleTypeWithOptionalConfig);
		
		if ( ! modulebasePath.endsWith(".") ) 
			modulebasePath = modulebasePath+".";
		
		// construct the module
		Class moduleClass = null;
		if ( "".equals(moduleType) )
			return null;
		try {
			if (moduleType.indexOf(".") == -1)
				moduleClass = Class
						.forName(modulebasePath
								+ moduleType);
			else
				moduleClass = Class.forName(moduleType);
		} catch (ClassNotFoundException e) {
			myLogger.warn("Could not find module class for type: "+moduleType);
		}

		Constructor moduleConstructor = null;
		try {
			moduleConstructor = moduleClass
					.getConstructor(JsdlTemplate.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TemplateModule module_instance = null;
		try {
			module_instance = (TemplateModule) moduleConstructor.newInstance(new Object[] { template });
		} catch (Exception e) {
			myLogger.error("Could not create module \""+moduleType+"\" for template "+template.getApplicationName()+": "+e.getLocalizedMessage());
		}
		
		module_instance.configureModule(moduleConfig);
		
		return module_instance;
	}
	
	public static Map<String, TemplateModule> createTemplateModules(JsdlTemplate template, String modulebasePath) {
		
		String[] moduleNamesWithOptionalConfig = null;
		NodeList resultNodes = null;
		String expression = "//@grisu_modules";
		
		Map<String, TemplateModule> modules = new LinkedHashMap<String, TemplateModule>();
		
		try {
			resultNodes = (NodeList)xpath.evaluate(expression, template.getTemplateDocument(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			myLogger.debug("No modules used node in jsdl file.");
			return modules;
		}
		
		if ( resultNodes.getLength() > 1 ) {
			myLogger.error("More than one module specification found. That's not supported right now. Continuing not using modules.");
			return modules;
		}
		
		if ( resultNodes.item(0) != null ) {
			moduleNamesWithOptionalConfig = resultNodes.item(0).getNodeValue().split(",");
		} else {
			moduleNamesWithOptionalConfig = new String[]{}; 
		}
		
//		Attr att = (Attr)resultNodes.item(0);
//		Element el = att.getOwnerElement();
//		el.removeAttribute("grisu_modules");
//		}
//		ArrayList<TemplateNode> copyOfTemplateNodes = (ArrayList<TemplateNode>)template.getTemplateNodes().clone();
		
		List<String> templatenames = new ArrayList<String>(); 
			
		for (String tempName : template.getTemplateNodes().keySet() ) {
			templatenames.add(new String(tempName));
		}
		
		for (String moduleNameWithOptionalConfig : moduleNamesWithOptionalConfig) {

			
			TemplateModule module = TemplateModuleFactory.createTemplateModule(template, modulebasePath, moduleNameWithOptionalConfig);
			if ( module != null ) {
				//fill module with TemplateNodes
				String[] nodes = module.getTemplateNodeNamesThisModuleClaimsResponsibleFor();
				
				ArrayList<TemplateNode> nodesForThisModule = new ArrayList<TemplateNode>();
				for ( String node : nodes ) {
					for ( String tempNodeName : templatenames ) {
						if ( tempNodeName.equals(node) ) {
							// this is one of the templateNodes this module is responsible for
							nodesForThisModule.add(template.getTemplateNodes().get(tempNodeName));
							break;
						}
					}
				}
				
				if ( nodesForThisModule.size() != module.getTemplateNodeNamesThisModuleClaimsResponsibleFor().length ) {
					myLogger.debug("Could not find all required template nodes for module: "+moduleNameWithOptionalConfig+". Not using it because of that.");
				} else {
					module.setTemplateNodes(nodesForThisModule);
					// getting rid of the already taken nodes
					for ( TemplateNode node : nodesForThisModule ) {
						templatenames.remove(node.getName());
					}
					myLogger.debug("Found all template nodes for module: "+moduleNameWithOptionalConfig+". Adding it now.");
					
					modules.put(TemplateHelperUtils.getParameterName(moduleNameWithOptionalConfig), module);
				}
			}
		}
		
		if ( templatenames.size() > 0 ) {
//			TemplateModule rest = new RestModule(template);
			TemplateModule rest = TemplateModuleFactory.createTemplateModule(template, modulebasePath, "Rest");
			ArrayList<TemplateNode> restTemplateNodes = new ArrayList<TemplateNode>();
			for ( String templateName : templatenames ) {
				restTemplateNodes.add(template.getTemplateNodes().get(templateName));
			}
			rest.setTemplateNodes(restTemplateNodes);
			modules.put(Rest.NAME, rest);
		}
		
		return modules;
	}

}
