

package org.vpac.grisu.client.view.swing.template;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.modules.AbstractModule;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

/**
 * Use this as base for a swing modulePanel. I copied the necessary code from {@link AbstractModule} because
 * Java doesn't support multiple inheritance.
 * 
 * @author Markus Binsteiner
 *
 */
abstract public class AbstractModulePanel extends JPanel implements ModulePanel {

	protected JsdlTemplate template = null;
	protected Map<String, TemplateNode> templateNodes = new LinkedHashMap<String, TemplateNode>();
	
	protected TemplateModule templateModule = null;
	
	// for generic submission panel
	public AbstractModulePanel() {
		super();
	}
	
	public void setTemplateModule(TemplateModule templateModule) throws ModuleException {
		this.templateModule = templateModule;
		this.template = templateModule.getTemplate();
		initialize();
	}
	
//	public AbstractModulePanel(TemplateModule templateModule) {
//		super();
//		setTemplateModule(templateModule);
//	}
	
	public TemplateModule getTemplateModule() {
		return this.templateModule;
	}

	public Map<String, TemplateNode> getTemplateNodes() {
		return templateNodes;
	}
	
	public ArrayList<TemplateNode> getTemplateNodes(String name) {
		ArrayList<TemplateNode> result = new ArrayList<TemplateNode>();
		for ( TemplateNode node : getTemplateNodes().values() ) {
			if ( name.equals(node.getName()) ) {
				result.add(node);
			}
		}
		if ( result.size() == 0 ) 
			return null;
		else 
			return result;
	}
	
	public JsdlTemplate getTemplate() {
		return this.template;
	}
	

	
	public void setTemplateNodes(ArrayList<TemplateNode> templateNodes) {
		
		for ( TemplateNode node : templateNodes ) {
			this.templateNodes.put(node.getName(), node);
		}
		this.templateModule.initializeTemplateNodes(this.templateNodes);
	}
	
	abstract protected void initialize() throws ModuleException;

}
