package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import java.text.ParseException;
import java.util.ArrayList;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.utils.DebugUtils;
import org.w3c.dom.Element;

public class MultipleInputString extends TemplatePreProcessor {

	public static void main(String[] args) {

		ArrayList<String> strings = null;
		try {
			strings = parseString("cat  hallo \"what is  up\" fellow \" traveller?\"  ");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Parsed strings:");
		for (String string : strings) {
			System.out.println("String: " + string);
		}

	}

	protected static ArrayList<String> parseString(String string)
			throws ParseException {
		ArrayList<String> strings = new ArrayList<String>();

		boolean lastCharacterIsWhitespace = false;
		boolean inbetweenQuotationMarks = false;
		StringBuffer part = new StringBuffer();

		if (string == null || "".equals(string))
			return strings;

		for (char character : string.toCharArray()) {
			if (Character.isWhitespace(character)) {
				if (!lastCharacterIsWhitespace && !inbetweenQuotationMarks) {
					strings.add(part.toString());
					part = new StringBuffer();
					lastCharacterIsWhitespace = true;
					continue;
				}
				if (inbetweenQuotationMarks) {
					part.append(character);
				} else {
					lastCharacterIsWhitespace = true;
					// strings.add(part.toString());
					// part = new StringBuffer();
					continue;
				}
			} else {
				if (character == '"') {
					if (inbetweenQuotationMarks) {
						strings.add(part.toString());
						part = new StringBuffer();
						inbetweenQuotationMarks = false;
						lastCharacterIsWhitespace = true;
						continue;
					} else {
						inbetweenQuotationMarks = true;
						continue;
					}
				} else {
					part.append(character);
					lastCharacterIsWhitespace = false;
				}
			}

		}
		if (inbetweenQuotationMarks) {
			throw new ParseException("No end quotations marks.", string
					.length() - 1);
		} else {
			if (part.length() > 0)
				strings.add(part.toString());
		}
		return strings;
	}

	ArrayList<Element> addedElements = new ArrayList<Element>();

	Element originalElement = null;

	Element oldParent = null;

	public MultipleInputString(TemplateNode templateNode) {
		super(templateNode);
		if (originalElement == null) {
			originalElement = (Element) node.getElement().cloneNode(true);
		}
	}

	@Override
	public void process() throws TemplatePreProcessorException {

		DebugUtils.jsdlDebugOutput("before preprocessing node: "
				+ node.getName(), node.getTemplate().getTemplateDocument());

		if (node.getValue() == null || "".equals(node.getValue())) {
			// maybe delete the element?
			return;
		}

		ArrayList<String> strings = null;
		try {
			strings = parseString(node.getValue());
		} catch (ParseException e) {
			throw new TemplatePreProcessorException(
					"Could not parse input string.", e);
		}

		if (strings == null || strings.size() < 1) {
			throw new TemplatePreProcessorException(
					"Not enough input provided to parse arguments.");
		}

		// the first element stays like it is, only one value is set
		node.getElement().setTextContent(strings.get(strings.size() - 1));

		Element parent = (Element) (node.getElement().getParentNode());
		oldParent = parent;
		// create and insert the cloned elements
		for (int i = 0; i < strings.size() - 1; i++) {
			Element newElement = (Element) node.getElement().cloneNode(true);

			newElement.setTextContent(strings.get(i));

			parent.insertBefore(newElement, node.getElement());

			addedElements.add(newElement);

		}

		DebugUtils.jsdlDebugOutput("after preprocessing node: "
				+ node.getName(), node.getTemplate().getTemplateDocument());
	}

	@Override
	protected void resetTemplateNode() {

		DebugUtils.jsdlDebugOutput("before resetting node: " + node.getName(),
				node.getTemplate().getTemplateDocument());

		// remove elements from previous run
		for (Element element : addedElements) {
			try {
				oldParent.removeChild(element);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		DebugUtils.jsdlDebugOutput("after resetting node: " + node.getName(),
				node.getTemplate().getTemplateDocument());

	}

}
