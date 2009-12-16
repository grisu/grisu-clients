package org.vpac.grisu.client.view.swing.template;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

public class TemplateParentPanel extends JPanel {

	private JPanel panel;
	private JsdlTemplate template = null;

	private JTabbedPane tabbedPane;

	/**
	 * Create the panel
	 */
	public TemplateParentPanel() {
		super();
		setLayout(new BorderLayout());
		add(getTabbedPane(), BorderLayout.CENTER);
		//

		// for testing/developing
		Document xmlTemplate = SeveralXMLHelpers.loadXMLFile(new File(
				"/home/markus/.grisu/templates/diff.xml"));
		setJsdlTemplate(new JsdlTemplate(null, xmlTemplate));

	}

	/**
	 * @return
	 */
	private ModulePanel createModulePanel(TemplateModule module) {

		return null;

	}

	/**
	 * @return
	 */
	protected JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabPlacement(SwingConstants.RIGHT);
			// tabbedPane.addTab("New tab", null, createModulePanel(), null);
		}
		return tabbedPane;
	}

	public void setJsdlTemplate(JsdlTemplate template) {

		this.template = template;

		for (String moduleName : template.getModules().keySet()) {

		}

	}

}
