package org.vpac.grisu.client.model.template.validators;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class IntegerValidator extends TemplateNodeValidator {

	public IntegerValidator(TemplateNode node) {
		super(node);
	}

	public void validate() throws TemplateValidateException {

		try {
			Integer integer = Integer.parseInt(this.templateNode.getValue());
		} catch (Exception e) {
			throw new TemplateValidateException(
					"Input has to be an integer value.", e);
		}

	}

}
