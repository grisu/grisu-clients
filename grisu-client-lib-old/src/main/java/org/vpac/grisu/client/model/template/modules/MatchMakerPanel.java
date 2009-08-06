package org.vpac.grisu.client.model.template.modules;

import java.util.Map;

import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class MatchMakerPanel extends AbstractModule {
	
	public static final String[] TEMPLATE_TAGS_USED = new String[]{
		TemplateTagConstants.JOBNAME_TAG_NAME, 
		TemplateTagConstants.VERSION_TAG_NAME,
		TemplateTagConstants.WALLTIME_TAG_NAME, 
		TemplateTagConstants.CPUS_TAG_NAME,
		TemplateTagConstants.HOSTNAME_TAG_NAME,
		TemplateTagConstants.MIN_MEM_TAG_NAME,
		TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME };
	

	public MatchMakerPanel(JsdlTemplate jsdl) {
		super(jsdl);
	}
	
	@Override
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return TEMPLATE_TAGS_USED;
	}

	@Override
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {
		// nothing to do
	}

	public String getModuleName() {
		return "General";
	}

	public void process() throws TemplateModuleProcessingException {
		// nothing to do
	}

}
