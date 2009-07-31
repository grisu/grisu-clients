

package org.vpac.grisu.client.model.template.nodes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.templatePreProcessor.TemplatePreProcessor;
import org.vpac.grisu.client.model.template.nodes.templatePreProcessor.TemplatePreProcessorException;
import org.vpac.grisu.client.model.template.nodes.templatePreProcessor.TemplateProcessorFactory;
import org.vpac.grisu.client.model.template.validators.TemplateNodeValidator;
import org.vpac.grisu.client.model.template.validators.TemplateValidateException;
import org.vpac.grisu.client.model.template.validators.ValidatorFactory;
import org.vpac.grisu.client.model.template.validators.ValidatorNotCreatedException;
import org.vpac.grisu.control.JobConstants;
import org.w3c.dom.Element;

import au.org.arcs.jcommons.utils.JsdlHelpers;

public class TemplateNode {

	static final Logger myLogger = Logger.getLogger(TemplateNode.class
			.getName());

	public static final String NON_MAP_PARAMETER = "NON_VALUE";
	public static final String LAST_USED_PARAMETER = "useLastValue";
	public static final String USE_HISTORY = "useHistory";
	public static final String HIDE_DESCRIPTION = "hideDescription";
	public static final String HISTORY_KEY = "historyKey";
	public static final String LOCKED_KEY = "locked";
	
	public static final String DEFAULT_HELP_ATTRIBUTE_NAME = "default";
	
	private JsdlTemplate template = null;

	private String name = null;
	
	private Element element = null;
	private String type = null;
	private String defaultValue = null;
	private String[] prefills = null;
	private String title = null;
	private String description = null;
	private String multiplicity = null;
	// these are mostly to tell renderers how to render stuff. They shouldn't contain important information.
	private Map<String, String> otherProperties = new LinkedHashMap<String, String>();
	
	private Map<String, String> infoMap = null;


	private String value = null;

	private Exception possibleProcessingException = null;
	// private boolean valid = false;
	// private Map<String, String> jobProperties = new HashMap<String,
	// String>();
	private TemplateNodeValueSetter setter = null;
	private TemplateNodeValidator validator = null;
	private TemplatePreProcessor preprocessor = null;
	
	public TemplateNode(JsdlTemplate template, Element element) {
		this.template = template;
		this.element = element;
		this.name = element.getAttribute("name").replaceAll("\\s", "");
		this.type = element.getAttribute("template");
		if (name == null || "".equals(name)) {
			name = type;
		}
//		this.value = element.getAttribute("default");
		this.title = element.getAttribute("title");
		if ( this.title == null || "".equals(this.title) )
			this.title = element.getAttribute("name");
		this.description = element.getAttribute("description");
		this.multiplicity = element.getAttribute("multiplicity");
		if (this.multiplicity == null) {
			this.multiplicity = "1";
		}
		this.defaultValue = element.getAttribute("defaultValue");
		this.prefills = element.getAttribute("prefills").split(",");
		
		String validatorName = element.getAttribute("validator");
		if ( validatorName != null && ! "".equals(validatorName) ) {
			try {
				this.validator = ValidatorFactory.createValidator(this, validatorName);
			} catch (ValidatorNotCreatedException e) {
				myLogger.warn(e.getLocalizedMessage()+" Continuing without Validator.");
			}
		}
		
		String other = element.getAttribute("other");
		if ( other != null && !"".equals(other) ) {

			for ( String part : other.split(",") ) {
				if ( part.length() > 0 ) {
					if ( part.indexOf("=") > 0 ) {
						otherProperties.put(part.substring(0,part.indexOf("=")), part.substring(part.indexOf("=")+1));
					} else if ( part.indexOf("=") == -1 ) {
						otherProperties.put(part, NON_MAP_PARAMETER);
					}
					
				}
			}
			
		}
		
		preprocessor = TemplateProcessorFactory
		.createPreprocessor(this);
			
	}
	
	public void setTemplateNodeValueSetter(TemplateNodeValueSetter setter) {
		this.setter = setter;
	}
	
	public TemplateNodeValueSetter getTemplateNodeValueSetter() {
		return this.setter;
	}

	public void process() throws TemplatePreProcessorException {
		


		// if (!inputIsValid()) {
		// throw new TemplateProcessingException(
		// "Input for this node is not valid. Can't proceed.");
		// }

//		if (	// check whether preprocessor should be used
//				( "?".equals(multiplicity) && ( this.value != null && ! "".equals(this.value) ) ) || 
//				"1".equals(this.multiplicity)) {
			element.setTextContent(value);



			if (preprocessor == null) {
				myLogger.debug("No preprocessor for type " + this.getType()
						+ " found. Doing nothing.");
			} else {
				myLogger.debug("Preprocessor found for type " + this.getType()
						+ ". Starting to preprocess.");
				try {
					preprocessor.process();
				} catch (TemplatePreProcessorException tpe) {
					fireTemplateNodeEvent("Error processing input: "
							+ tpe.getLocalizedMessage(),
							TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID);
					possibleProcessingException = tpe;
					throw tpe;
				}
				myLogger.debug("Preprocessing successfull.");
			}

//		} else if ("?".equals(this.multiplicity)) {
//
//		} else if ("*".equals(this.multiplicity)) {
//
//		} else {
//			myLogger
//					.error("Can't determine the multiplicity of this TemplateNode. This has to be an error in the template. Doing nothing.");
//
//		}
//		cleanUpXmlElement();
			possibleProcessingException = null;
		fireTemplateNodeEvent(TemplateNodeEvent.DEFAULT_FILLED_MESSAGE,
				TemplateNodeEvent.TEMPLATE_PROCESSED_VALID);
	}

//	private void cleanUpXmlElement() {
//		element.removeAttribute("template");
//		element.removeAttribute("name");
//		element.removeAttribute("defaultValue");
//		element.removeAttribute("description");
//		element.removeAttribute("multiplicity");
//		element.removeAttribute("prefills");
//	}
	
	/**
	 * This returns the exeption if the process method failed.
	 * @returnthe error or null (if the process didn't fail).
	 */
	public Exception getError() {
		return possibleProcessingException;
	}
	
	public void reset() {
		
		fireTemplateNodeEvent(null, TemplateNodeEvent.RESET);
		if (preprocessor == null) {
			myLogger.debug("No preprocessor for type " + this.getType()
					+ " found. No cleanup.");
		} else {
			myLogger.debug("Preprocessor found for type " + this.getType()
					+ ". Starting to cleanup.");
//			try {
				preprocessor.reset();
//			} catch (TemplatePreProcessorException tpe) {
//				fireTemplateNodeEvent("Error processing input: "
//						+ tpe.getLocalizedMessage(),
//						TemplateNodeEvent.TEMPLATE_PROCESSED_INVALID);
//				throw tpe;
//			}
			myLogger.debug("Cleaning up successful.");
		}
	}


	public boolean isReady() {
		
		if ( this.setter == null ) 
			throw new RuntimeException("No TemplateNodeValueSetter for TemlateNode: "+this.name+". This is most likely a bug in the implementation of the TemplateNode renderer.");
		
		this.value = setter.getExternalSetValue();

		myLogger.debug("Got value \""+this.value+"\" for template node: "+getName());
		// first we check whether user input is required
		if ("?".equals(this.multiplicity) || "*".equals(this.multiplicity)
				|| (this.value != null && !"".equals(this.value))) {

			if ( ( "?".equals(this.multiplicity) || "*".equals(this.multiplicity) ) && (this.value == null || "".equals(this.value)) ) {
				// user did not input anything and input was not required
				return true;
			}
			
			if ( "1".equals(this.multiplicity) && this.value.startsWith(JobConstants.DUMMY_START_STRING) ){
				fireTemplateNodeEvent(TemplateNodeEvent.DEFAULT_REQUIRED_INPUT_EMPTY_MESSAGE, TemplateNodeEvent.TEMPLATE_FILLED_INVALID);
				return false;
			}
			
			// validate input
			if (validator != null) {
				try {
					validator.validate();
					return true;
				} catch (TemplateValidateException tve) {
					fireTemplateNodeEvent(tve.getLocalizedMessage(),
							TemplateNodeEvent.TEMPLATE_FILLED_INVALID);
					return false;
				}
			} else {
				// in this case we accept every user input because there was no validator
				return true;
			}

		} else {
			fireTemplateNodeEvent(TemplateNodeEvent.DEFAULT_REQUIRED_INPUT_EMPTY_MESSAGE, TemplateNodeEvent.TEMPLATE_FILLED_INVALID);
			return false;
		}

	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	
	public Map<String, String> getInfoMap() {

		if ( infoMap == null ) {
			infoMap = JsdlHelpers.getTemplateTagInfoItems(getTemplate().getTemplateDocument(), getName());
			
				if ( infoMap == null ) {
					// so we don't do that everytime
					infoMap = new HashMap<String, String>();
				}
				
			}
		return infoMap;

	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getPrefills() {
		return prefills;
	}

	public void setPrefills(String[] prefills) {
		this.prefills = prefills;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	public JsdlTemplate getTemplate() {
		return template;
	}

	public Element getElement() {
		return element;
	}
	
	public void setElement(Element element) {
		this.element = element;
	}

	// public TemplateNodeValidator getValidator() {
	// return validator;
	// }
	//
	// public boolean inputIsValid() {
	// return valid;
	// }
	//
	// public void setValid(boolean valid) {
	// this.valid = valid;
	// }

	// ---------------------------------------------------------------------------------------
	// Event stuff (TemplateNode)
	private Vector<TemplateNodeListener> templateNodeListeners;

	private void fireTemplateNodeEvent(String message, int event_type) {
		// if we have no mountPointsListeners, do nothing...
		if (templateNodeListeners != null && !templateNodeListeners.isEmpty()) {
			// create the event object to send
			TemplateNodeEvent event = new TemplateNodeEvent(this, message,
					event_type);

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) templateNodeListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				TemplateNodeListener l = (TemplateNodeListener) e.nextElement();
				try {
					l.templateNodeUpdated(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// register a listener
	synchronized public void addTemplateNodeListener(TemplateNodeListener l) {
		if (templateNodeListeners == null)
			templateNodeListeners = new Vector();
		templateNodeListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeTemplateNodeListener(TemplateNodeListener l) {
		if (templateNodeListeners == null) {
			templateNodeListeners = new Vector<TemplateNodeListener>();
		}
		templateNodeListeners.removeElement(l);
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Map<String, String> getOtherProperties() {
		return otherProperties;
	}
	
	public boolean hasProperty(String property) {
		
		return otherProperties.keySet().contains(property);
		
	}
	
	public String getOtherProperty(String property) {
		return otherProperties.get(property);
	}

	// public Map<String, String> getJobProperties() {
	// if ( jobProperties == null ) {
	// jobProperties = new HashMap<String, String>();
	// }
	// return jobProperties;
	// }
	//
	// public void setJobProperties(Map<String, String> jobProperties) {
	// this.jobProperties = jobProperties;
	// }
	//	
	// public void addProperty(String key, String value) {
	// getJobProperties().put(key, value);
	// }
	//	
	// public void removeProperty(String key) {
	// getJobProperties().remove(key);
	// }

	public String toString() {
		return getName();
	}
}
