package org.vpac.grisu.client.view.swing.template;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanel;

public interface ModulePanel {

	public JPanel getPanel();

	public TemplateModule getTemplateModule();
	// public void setValues();

	/**
	 * This method connects the {@link TemplateNode}s to the appropriate
	 * {@link TemplateNodePanel}s.
	 * 
	 * @param module
	 *            the module
	 * @throws ModuleException
	 */
	// public void initialize(TemplateModule module) throws ModuleException;
	public void setTemplateModule(TemplateModule templateModule)
			throws ModuleException;

}
