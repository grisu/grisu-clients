package org.vpac.grisu.client.view.swing.mountpoints;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.vpac.grisu.model.MountPoint;

public class MountPointTableCellRenderer implements TableCellRenderer {

	public static final int MOUNTPOINT_NAME_INDEX = 1;
	public static final int MOUNTPOINT_ROOT_INDEX = 2;
	public static final int FQAN_INDEX = 3;
	public static final int CHECK_INDEX = 0;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		MountPoint mp = (MountPoint) value;

		if (column == CHECK_INDEX) {
			JCheckBox result = new JCheckBox();
			return result;
		} else {
			JLabel result = new JLabel();
			switch (column) {
			case MOUNTPOINT_NAME_INDEX:
				result.setText(mp.getAlias());
				break;
			case MOUNTPOINT_ROOT_INDEX:
				result.setText(mp.getRootUrl());
				break;
			case FQAN_INDEX:
				result.setText(mp.getFqan());
				break;
			}
			return result;
		}

	}

}
