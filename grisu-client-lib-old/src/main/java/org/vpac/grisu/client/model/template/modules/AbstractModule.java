package org.vpac.grisu.client.model.template.modules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.vpac.grisu.client.control.exceptions.TemplateException;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

/**
 * This class is not really used on runtime. For each way of rendering a
 * JsdlTemplate you would write your own abstract module (see for example:
 * {@link AbstractModulePanel} for Swing which copies the code from this class
 * into it's own class).
 * 
 * @author Markus Binsteiner
 * 
 */
public abstract class AbstractModule implements TemplateModule {

	protected JsdlTemplate template = null;
	protected Map<String, TemplateNode> templateNodes = new LinkedHashMap<String, TemplateNode>();

	protected Map<String, String> moduleConfig = null;

	public AbstractModule(JsdlTemplate template) {
		this.template = template;
	}

	public void checkWhetherAllTemplateNodesHaveSetter()
			throws TemplateException {
		for (TemplateNode templateNode : templateNodes.values()) {
			if (templateNode.getTemplateNodeValueSetter() == null) {
				throw new TemplateException("Module " + getModuleName()
						+ " missing TemplateNodeValueSetter for templateNode: "
						+ templateNode.getName());
			}
		}
	}

	public void configureModule(Map<String, String> moduleConfiguration) {
		this.moduleConfig = moduleConfiguration;
	}

	public Map<String, String> getConfiguration() {
		return this.moduleConfig;
	}

	// public ArrayList<TemplateNode> getTemplateNodes(String name) {
	// ArrayList<TemplateNode> result = new ArrayList<TemplateNode>();
	// for ( TemplateNode node : getTemplateNodes().values() ) {
	// if ( name.equals(node.getName()) ) {
	// result.add(node);
	// }
	// }
	// if ( result.size() == 0 )
	// return null;
	// else
	// return result;
	// }

	public JsdlTemplate getTemplate() {
		return this.template;
	}

	abstract public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor();

	public Map<String, TemplateNode> getTemplateNodes() {
		return templateNodes;
	}

	/**
	 * Within this methods you can initialize possible module internas. You
	 * won't need to implement anything here in most cases, I assume.
	 * 
	 * @param templateNodes
	 *            all the templateNodes that are assigned to this module
	 */
	abstract public void initializeTemplateNodes(
			Map<String, TemplateNode> templateNodes);

	public void setTemplateNodes(ArrayList<TemplateNode> templateNodes) {

		for (TemplateNode node : templateNodes) {
			this.templateNodes.put(node.getName(), node);
		}
		initializeTemplateNodes(this.templateNodes);
	}

}
