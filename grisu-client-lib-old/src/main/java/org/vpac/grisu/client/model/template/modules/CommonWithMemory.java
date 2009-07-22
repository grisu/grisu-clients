package org.vpac.grisu.client.model.template.modules;

import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.model.FqanListener;

/**
 * @author Markus Binsteiner
 *
 */
public class CommonWithMemory extends Common implements FqanListener, MountPointsListener, SubmissionObjectHolder {

	static final Logger myLogger = Logger.getLogger(Common.class.getName());
	
	
	public static final String[] MODULES_USED_MEMORY = new String[] { TemplateTagConstants.JOBNAME_TAG_NAME,
		TemplateTagConstants.WALLTIME_TAG_NAME, TemplateTagConstants.CPUS_TAG_NAME, TemplateTagConstants.HOSTNAME_TAG_NAME,
		TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME, TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME,
		TemplateTagConstants.MODULE_TAG_NAME, TemplateTagConstants.MIN_MEM_TAG_NAME };
	
	private DefaultTemplateNodeValueSetter memorySetter = new DefaultTemplateNodeValueSetter();

	public CommonWithMemory(JsdlTemplate template) {
		super(template);
	}
	
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {
		
		initValueSetters(templateNodes);
		
		templateNodes.get(TemplateTagConstants.MIN_MEM_TAG_NAME).setTemplateNodeValueSetter(memorySetter);
		
	}
	
	public void setMemory(long memoryinbytes) {
		String memory = new Long(memoryinbytes).toString();
		templateNodes.get(TemplateTagConstants.MIN_MEM_TAG_NAME).getTemplateNodeValueSetter()
				.setExternalSetValue(memory);
	}
	
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return MODULES_USED_MEMORY;
	}
}
