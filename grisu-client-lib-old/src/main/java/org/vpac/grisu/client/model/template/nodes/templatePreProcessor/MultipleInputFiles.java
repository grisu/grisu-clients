

package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import java.util.ArrayList;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.utils.DebugUtils;
import org.w3c.dom.Element;

public class MultipleInputFiles extends TemplatePreProcessor {
	
	ArrayList<Element> addedElements = new ArrayList<Element>();
	
	Element originalElement = null;
	
	Element oldParent = null;
	
	public MultipleInputFiles(TemplateNode node) {
		super(node);
		if ( originalElement == null ) {
			originalElement = (Element)node.getElement().cloneNode(true);
		}
	}

	@Override
	public void process() throws TemplatePreProcessorException {
		
		DebugUtils.jsdlDebugOutput("before preprocessing node: "+node.getName(), node.getTemplate().getTemplateDocument());
		
		if ( node.getValue() == null || "".equals(node.getValue()) ) {
			return;
		}
		
		String[] fileUrls = node.getValue().split(";");
		
		if ( fileUrls.length == 0 ) {
			return;
		}

		// the first element stays like it is, only one value is set
		if ( fileUrls[0].equals(JobConstants.DUMMY_STAGE_FILE)) {
			node.getElement().setTextContent(JobConstants.DUMMY_STAGE_FILE);
			return;
//		} else if ( ! fileUrls[0].startsWith("gsiftp:") ) {
//			fileUrls[0] = "file:"+fileUrls[0]; 
//			fileUrls[0] = fileUrls[0]; 
		}
		node.getElement().setTextContent(fileUrls[0]);
		InputFile.calculatePostProcessUploadAttribute(node.getElement().getTextContent(), node.getElement());
		
		Element dataStagingElement = (Element)node.getElement().getParentNode().getParentNode();
		Element parent = (Element)(dataStagingElement.getParentNode());
		oldParent = parent; 
		
		// create and insert the cloned elements
		for (int i=1; i<fileUrls.length; i++) {
			Element newElement = (Element)dataStagingElement.cloneNode(true);
			
			Element uriElement = ((Element)((Element)newElement.getElementsByTagName("Source").item(0)).getElementsByTagName("URI").item(0));
			
//			if ( ! fileUrls[i].startsWith("gsiftp:") )
//				fileUrls[i] = "file:"+fileUrls[i];
			uriElement.setTextContent(fileUrls[i]);
			InputFile.calculatePostProcessUploadAttribute(uriElement.getTextContent(), uriElement);

			parent.appendChild(newElement);

			addedElements.add(newElement);
			
		}
		
		DebugUtils.jsdlDebugOutput("after preprocessing node: "+node.getName(), node.getTemplate().getTemplateDocument());
		
	}

	protected void resetTemplateNode() {
		
		DebugUtils.jsdlDebugOutput("before resetting node: "+node.getName(), node.getTemplate().getTemplateDocument());
		
		// remove elements from previous run
		for ( Element element : addedElements ) {
			try {
				oldParent.removeChild(element);
			} catch (Exception e) {
//				e.printStackTrace();
				myLogger.error("Error when trying to reset MultipleInputFiles: "+e.getLocalizedMessage());
			}
		}

		DebugUtils.jsdlDebugOutput("after resetting node: "+node.getName(), node.getTemplate().getTemplateDocument());
		
	}
	

}
