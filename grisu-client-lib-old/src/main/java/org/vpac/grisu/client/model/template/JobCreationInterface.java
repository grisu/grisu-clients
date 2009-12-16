package org.vpac.grisu.client.model.template;

import org.vpac.grisu.client.control.template.TemplateManager;

public interface JobCreationInterface {

	public JsdlTemplate getTemplate();

	/**
	 * Sets (or changes) the fqan which is supposed to be used to submit the job
	 * that is created at the moment.
	 * 
	 * @param fqan
	 *            the fqan
	 */
	public void setSubmissionFQAN(String fqan);

	/**
	 * Sets the template manager which contains all available templates (local &
	 * remote).
	 * 
	 * @param manager
	 *            the {@link TemplateManager}
	 */
	public void setTemplateManager(TemplateManager manager);

}
