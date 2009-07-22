

package org.vpac.grisu.client.model.template.modules;

import java.util.Map;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class Rest extends AbstractModule {
	
	public static final String NAME = "Job parameters";
	
	public Rest(JsdlTemplate template) {
		super(template);
	}

	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return null;
	}

	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {
		// nothing to do here
	}
	
	public String getModuleName() {
		return NAME;
	}

	public void reset() {
		
		// do whatever your implementation of a module has to do to clean up
		
	}

	public void process() throws TemplateModuleProcessingException {
		// nothing to do here
		
	}

}
