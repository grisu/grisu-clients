

package org.vpac.grisu.client.model.template.postprocessor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.w3c.dom.Element;

public class PostprocessorFactory {
	
	static final Logger myLogger = Logger.getLogger(PostprocessorFactory.class.getName());
	
	public static ArrayList<ElementPostprocessor> createPostprocessors(JsdlTemplate template, Element element) {
		
		ArrayList<ElementPostprocessor> postprocessors = new ArrayList<ElementPostprocessor>();
		String[] postprocessorNames = element.getAttribute("postprocess").split(",");
		
		for ( String postprocessorValue : postprocessorNames ) {
			String postprocessorName;
			String configText = null;
			int startConfigIndex = postprocessorValue.indexOf("[");
			if ( startConfigIndex > -1 ) {
				int endConfigIndex = postprocessorValue.indexOf("]");
				configText = postprocessorValue.substring(startConfigIndex+1, endConfigIndex);
				postprocessorName = postprocessorValue.substring(0,startConfigIndex);
			} else {
				postprocessorName = postprocessorValue;
			}
			
			try {
			
			Class postprocessorClass = null;
			
			if ( postprocessorName != null && ! "". equals(postprocessorName) ) {
			
			if ( postprocessorName.indexOf(".") == -1 ) {
				//means grisu default type
				postprocessorClass = Class.forName("org.vpac.grisu.client.model.template.postprocessor."+postprocessorName);
			} else {
				//means custom type
				postprocessorClass = Class.forName(postprocessorName);
			}
			

			Constructor validatorConstructor = null;
			try {
				validatorConstructor = postprocessorClass
						.getConstructor(JsdlTemplate.class, Element.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ElementPostprocessor pp = (ElementPostprocessor)validatorConstructor.newInstance(new Object[]{ template, element });
			pp.setConfig(configText);
			postprocessors.add(pp);
			}
		} catch (Exception e) {
			myLogger.warn("Could not create postprocessor \""+postprocessorName+"\": "+e.getLocalizedMessage());
//			return null;
		}
		}
		return postprocessors;
	}

}
