

package org.vpac.grisu.client.model.template.validators;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public abstract class TemplateNodeValidator {
	
	protected TemplateNode templateNode = null;
	
	public TemplateNodeValidator(TemplateNode node) {
		this.templateNode = node;
	}

	abstract public void validate() throws TemplateValidateException;
	
}
