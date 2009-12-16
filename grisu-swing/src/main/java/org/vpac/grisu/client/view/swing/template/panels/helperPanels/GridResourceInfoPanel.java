package org.vpac.grisu.client.view.swing.template.panels.helperPanels;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import au.org.arcs.jcommons.interfaces.GridResource;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GridResourceInfoPanel extends JPanel {

	private GridResource resource;
	private JScrollPane scrollPane;
	private JTextPane textPane;

	public GridResourceInfoPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("108px:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("78px:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getScrollPane(), "2, 2, fill, fill");
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTextPane());
		}
		return scrollPane;
	}

	private JTextPane getTextPane() {
		if (textPane == null) {
			textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.setBackground(Color.WHITE);
			textPane.setMargin(new Insets(4, 4, 4, 4));
		}
		return textPane;
	}

	public void setGridResource(GridResource resource) {
		this.resource = resource;

		if (resource == null) {
			getTextPane().setText("");
			return;
		}

		StringBuffer text = new StringBuffer();
		text.append("Free job slots: " + resource.getFreeJobSlots() + "\n");
		text.append("Waiting jobs: " + resource.getWaitingJobs() + "\n");
		text.append("Total jobs: " + resource.getTotalJobs() + "\n");
		text.append("Running jobs: " + resource.getRunningJobs() + "\n");
		text.append("Memory RAM size: " + resource.getMainMemoryRAMSize()
				+ "\n");

		getTextPane().setText(text.toString());
	}
}
