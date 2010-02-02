package org.vpac.grisu.client.view.swing.template;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.template.TemplateManager;

public interface SubmissionPanelInterface {

	public void addLocalTemplate();

	public JPanel getPanel();

	public void setRemoteApplication(String application);

	public void setTemplateManager(TemplateManager templateManager);

}
