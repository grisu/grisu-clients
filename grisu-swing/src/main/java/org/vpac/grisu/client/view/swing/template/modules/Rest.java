

package org.vpac.grisu.client.view.swing.template.modules;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.modules.TemplateModuleProcessingException;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;

public class Rest extends AbstractModulePanel {
	
	private JLabel label;
	public static final String NAME = "Other input";
	
	private JPanel parentPanel;
	private JScrollPane scrollPane;
	static final Logger myLogger = Logger.getLogger(Rest.class.getName());

	private Map<String, TemplateNodePanel> templateNodePanels = new TreeMap<String, TemplateNodePanel>();
	
	/**
	 * Create the panel
	 */
	public Rest() {
		super();
		setLayout(new BorderLayout());
		add(getScrollPane());
		//
	}

	public TemplateModule getModule() {
		return this.templateModule;
	}

	public JPanel getPanel() {
		return this;
	}

	protected void initialize() throws ModuleException {
		
		myLogger.debug("Initializing rest swing module panel...");
		for ( String name : this.templateModule.getTemplateNodes().keySet() ) {
			
			TemplateNode node = this.templateModule.getTemplateNodes().get(name);
			
			String panelClassName = null;
			if ( node.getType().contains(".") ) {
				// load class from external package
				panelClassName = node.getType();
			} else {
				panelClassName = "org.vpac.grisu.client.view.swing.template.panels."+node.getType();
			}
			
			myLogger.debug("Creating template node panel: "+panelClassName);
			
			TemplateNodePanel panel = null;
			
			try {
				
				Class panelClass = Class.forName(panelClassName);
				
				panel = (TemplateNodePanel)panelClass.newInstance();
				
			} catch (Exception e) {
				myLogger.error("Could not create template node panel for "+node.getType()+": "+e.getLocalizedMessage());
				throw new ModuleException(this.templateModule, e);
			}
			
			try {
				panel.setTemplateNode(node);
			} catch (TemplateNodePanelException e) {
				myLogger.error("Could not initialize template node panel for "+node.getType()+": "+e.getLocalizedMessage());
				throw new ModuleException(this.templateModule, e);
			}
			
			templateNodePanels.put(name, panel);
		}
		
		// ok. no we've got all panel created. Let's throw them into the parent panel.
		int y=0;
		for ( TemplateNodePanel panel : templateNodePanels.values() ) {
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = ++y;
			constraints.weightx = 1;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.insets = new Insets(15, 15, 10, 15);
			this.getParentPanel().add(panel.getTemplateNodePanel(), constraints);
		}
		
		final GridBagConstraints gridBagConstraintsRequiredLabel = new GridBagConstraints();
		gridBagConstraintsRequiredLabel.weighty = 1.0;
		gridBagConstraintsRequiredLabel.weightx = 1.0;
		gridBagConstraintsRequiredLabel.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraintsRequiredLabel.gridy = ++y;
		gridBagConstraintsRequiredLabel.gridx = 0;
		gridBagConstraintsRequiredLabel.insets = new Insets(10,10,10,10);
		parentPanel.add(getLabel(), gridBagConstraintsRequiredLabel);
	}

//	public void setValues() {
//		// TODO Auto-generated method stub
//		
//	}
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setViewportView(getParentPanel());
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JPanel getParentPanel() {
		if (parentPanel == null) {
			parentPanel = new JPanel();
			parentPanel.setLayout(new GridBagLayout());
			parentPanel.setOpaque(true);

		}
		return parentPanel;
	}

	
//	public String getModuleName() {
//		return "Hello";
//	}

//	public void reset() {
//		
//		for ( TemplateNodePanel panel : templateNodePanels.values() ) {
//			panel.reset();
//		}
//	}
	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("* required");
		}
		return label;
	}

	public void process() throws TemplateModuleProcessingException {
		// TODO Auto-generated method stub
		
	}

}
