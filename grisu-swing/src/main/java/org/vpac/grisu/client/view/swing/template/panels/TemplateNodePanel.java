

package org.vpac.grisu.client.view.swing.template.panels;

import javax.swing.JPanel;

import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeListener;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeValueSetter;

public interface TemplateNodePanel extends TemplateNodeListener, TemplateNodeValueSetter {
	
//	public static final String COSTUM_USER_INPUT_DISABLED = "locked";
	
	/**
	 * Sets the {@link TemplateNode} for this panel.
	 * @param node the node
	 * @throws TemplateNodePanelException if something goes wrong (i.e. TemplateNode is not compatible with panel)
	 */
	public void setTemplateNode(TemplateNode node) throws TemplateNodePanelException;
	
	/**
	 * Returns the panel (most of the times "this" object.
	 * @return the panel
	 */
	public JPanel getTemplateNodePanel();
	
	/**
	 * Resets the panel to it's original state. Can also be used to store user input into history
	 * file.
	 */
	public void reset();
	
	/**
	 * Adds a listener that gets notified when the value of this panel is changed
	 * @param v the listener to add
	 */
	public void addValueListener(ValueListener v);
	
	/**
	 * Removes a value listener
	 * @param v the listener to remove
	 */
	public void removeValueListener(ValueListener v);
	
}
