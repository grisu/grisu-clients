package org.vpac.grisu.client;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.HorizontalLayout;

public class HomePanel extends Panel {
	private Panel panel;
	private Panel panel_1;

	public HomePanel() {
		setLayout(new HorizontalLayout(5));
		add(getPanel());
		getPanel().setSize("150", "240");
		add(getPanel_1());
		getPanel_1().setSize("100", "400");
	}
	private Panel getPanel() {
		if (panel == null) {
			panel = new Panel("New Panel", "<div>test html</div>");
		}
		return panel;
	}
	private Panel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new Panel("New Panel", "<img src=\"http://localhost/tomcat/testFile.jpg\" alt=\"Test\" />");
		}
		return panel_1;
	}
}
