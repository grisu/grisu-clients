package org.vpac.grisu.client.view.swing.utils;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class MyComboboxListCellRenderer extends DefaultListCellRenderer {
	private int align;

	public MyComboboxListCellRenderer(int align) {
		this.align = align;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// DefaultListCellRenderer uses a JLabel as the rendering component:
		JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		lbl.setHorizontalAlignment(align);
		return lbl;
	}
}
