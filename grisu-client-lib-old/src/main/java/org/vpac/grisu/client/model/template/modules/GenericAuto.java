package org.vpac.grisu.client.model.template.modules;

import java.util.Map;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class GenericAuto extends AbstractModule {
	
	public static final String VERSION_TEMPLATE_TAG_NAME = "Version";
	public static final String HOSTNAME_TEMPLATE_TAG_NAME = "HostName";
	
	public static final String[] MODULES_USED = new String[]{
		"Jobname", VERSION_TEMPLATE_TAG_NAME, "Walltime", "CPUs", HOSTNAME_TEMPLATE_TAG_NAME, "MinMem", "ExecutionFileSystem", "EmailAddress" };
	
	public GenericAuto(JsdlTemplate template) {
		super(template);
	}

	@Override
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return MODULES_USED;
	}

	@Override
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {
		// TODO Auto-generated method stub

	}

	public String getModuleName() {
		return "General-Auto";
	}

	public void process() throws TemplateModuleProcessingException {
		// TODO Auto-generated method stub

	}

}
