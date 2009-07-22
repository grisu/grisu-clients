package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import java.text.ParseException;
import java.util.ArrayList;

import org.vpac.grisu.client.control.utils.CommandlineHelpers;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.utils.DebugUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CommandLine extends TemplatePreProcessor {

	ArrayList<Element> addedElements = new ArrayList<Element>();

	Element originalElement = null;

	Element oldParent = null;

	public CommandLine(TemplateNode templateNode) {
		super(templateNode);
		if (originalElement == null) {
			originalElement = (Element) node.getElement().cloneNode(true);
		}
	}

	@Override
	public void process() throws TemplatePreProcessorException {

		DebugUtils.jsdlDebugOutput("before preprocessing node: " + node.getName(),
				node.getTemplate().getTemplateDocument());
		
		
		ArrayList<String> strings = null;
		try {
			strings = CommandlineHelpers.parseString(node.getValue());
		} catch (ParseException e) {
			throw new TemplatePreProcessorException("Could not parse input string.", e);
		}
		
		if ( strings == null || strings.size() < 1 ) {
			throw new TemplatePreProcessorException("Not enough input provided to parse arguments.");
		}

		// the first element stays like it is, only one value is set
		// this is the executable
		node.getElement().setTextContent(strings.get(0));
		
		Element parent = (Element)(node.getElement().getParentNode());
		
		oldParent = parent; 
		
		if (strings.size() > 1) {
			Node posixAppChildNode = parent.getFirstChild();
			int elementCounter = 0;
			// we need to get the child element right after the Executable element
			while (posixAppChildNode != null && elementCounter < 2) {
				if (posixAppChildNode.getNodeType() == Node.ELEMENT_NODE) {					
					elementCounter++;
					if (elementCounter == 2) break;
				}
				posixAppChildNode = posixAppChildNode.getNextSibling();
			}			
			
			// create and insert the cloned elements
			for (int i = 1; i < strings.size(); i++) {
				Element newElement = parent.getOwnerDocument().createElementNS("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix", "Argument");
	
				newElement.setTextContent(strings.get(i));
				
				// just keep on inserting Argument elements..
				parent.insertBefore(newElement, posixAppChildNode);	
				addedElements.add(newElement);	
			}
		}

		DebugUtils.jsdlDebugOutput("after preprocessing node: " + node.getName(),
				node.getTemplate().getTemplateDocument());
	}

	@Override
	protected void resetTemplateNode() {

		DebugUtils.jsdlDebugOutput("before resetting node: " + node.getName(), node
				.getTemplate().getTemplateDocument());

		// remove elements from previous run
		for (Element element : addedElements) {
			try {
				oldParent.removeChild(element);
			} catch (Exception e) {
//				e.printStackTrace();
				myLogger.error("Error when resetting template node: "+e.getLocalizedMessage());
			}
		}

		DebugUtils.jsdlDebugOutput("after resetting node: " + node.getName(), node
				.getTemplate().getTemplateDocument());

	}


	
	public static void main(String[] args) {
		
		ArrayList<String> strings = null;
		try {
			strings = CommandlineHelpers.parseString("cat  hallo \"what is  up\" fellow \" traveller?\"  ");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Parsed strings:");
		for (String string : strings) {
			System.out.println("String: "+string);
		}
		
	}

}
