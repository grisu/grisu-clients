

package org.vpac.grisu.client.model.template.validators;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class ValidatorFactory {
	
	static final Logger myLogger = Logger.getLogger(ValidatorFactory.class.getName());
	
	public static TemplateNodeValidator createValidator(TemplateNode templateNode,  String validatorName) throws ValidatorNotCreatedException {
	
		TemplateNodeValidator validator = null;
		try {
			
			Class validatorClass = null;
			
			if ( validatorName != null && ! "". equals(validatorName) ) {
			
			if ( validatorName.indexOf(".") == -1 ) {
				//means grisu default type
				validatorClass = Class.forName("org.vpac.grisu.client.model.template.validators."+validatorName);
			} else {
				//means custom type
				validatorClass = Class.forName(validatorName);
			}
			

			Constructor validatorConstructor = null;
			try {
				validatorConstructor = validatorClass
						.getConstructor(TemplateNode.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			validator = (TemplateNodeValidator)validatorConstructor.newInstance(new Object[]{ templateNode });
			}
		} catch (Exception e) {
			myLogger.warn("Could not create validator \""+validatorName+"\": "+e.getLocalizedMessage());
			throw new ValidatorNotCreatedException("Could not create validator \""+validatorName+"\": "+e.getLocalizedMessage());
		}
		
		return validator;
	}

}
