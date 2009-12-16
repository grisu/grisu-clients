package org.vpac.grisu.client.model.template;

import java.util.ArrayList;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class TemplateFillingException extends Exception {

	private ArrayList<TemplateNode> failedNodes = null;

	public TemplateFillingException(ArrayList<TemplateNode> failedNodes) {
		this.failedNodes = failedNodes;
	}

	public ArrayList<TemplateNode> getFailedNodes() {
		return failedNodes;
	}

	public String getLocalizedMessage() {
		return getMessage();
	}

	public String getMessage() {
		StringBuffer temp = new StringBuffer("Failed nodes: ");
		for (TemplateNode node : failedNodes) {
			Exception e = node.getError();
			if (e == null) {
				temp.append("Error in template node: " + node.getName());
			} else {
				temp.append(node.getName() + ": " + e.getLocalizedMessage()
						+ "\n");
			}
		}
		return temp.toString();
	}

}
