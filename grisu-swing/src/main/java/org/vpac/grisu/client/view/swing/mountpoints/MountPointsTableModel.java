package org.vpac.grisu.client.view.swing.mountpoints;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.model.MountPoint;

import au.org.arcs.jcommons.constants.Constants;

public class MountPointsTableModel extends AbstractTableModel {
	
	static final Logger myLogger = Logger.getLogger(MountPointsTableModel.class
			.getName());
	
    public static final int MOUNTPOINT_NAME_INDEX = 0;
    public static final int MOUNTPOINT_ROOT_INDEX = 1;
    public static final int FQAN_INDEX = 2;
    public static final int AUTOMOUNT_INDEX = 3;
    
    public static final String[] columnNames = new String[]{"Alias", "Url", "VO", "Automounted"};
	
//	private Map<MountPoint, Object[]> mountpoints = new TreeMap<MountPoint, Object[]>();
	private Map<Integer, MountPoint> helperMap = new TreeMap<Integer, MountPoint>();
	private EnvironmentManager em = null;
	
	public MountPointsTableModel(EnvironmentManager em) {
		this.em = em;
		refresh();
	}

	public void refresh() {
		int index = 0;
		for ( MountPoint mp : em.getMountPoints() ) {
//			Object[] items = new Object[4];
//			items[0] = new JLabel(mp.getMountpoint());
//			items[1] = new JLabel(mp.getRootUrl());
//			items[2] = new JLabel(mp.getFqan());
//			items[3] = new JCheckBox();
//			mountpoints.put(mp, items);
			helperMap.put(index, mp);
			index++;
		}
		fireTableDataChanged();
	}
	
	public int getColumnCount() {
		return 4;
	}
	
    public String getColumnName(int column) {
        return columnNames[column];
    }


	public int getRowCount() {
		return helperMap.size();
	}
	
	 public boolean isCellEditable(int row, int col) { 
//		 if ( col == 0 )
//			 return true;
//		 else 
			 return false;
	 }


	public Object getValueAt(int rowIndex, int columnIndex) {
		
		MountPoint mp = helperMap.get(rowIndex);
		myLogger.debug("Selected mountpoint: "+mp);
		switch (columnIndex) {
		case MOUNTPOINT_NAME_INDEX: return mp;
		case MOUNTPOINT_ROOT_INDEX: return mp.getRootUrl();
		case FQAN_INDEX: String fqan = mp.getFqan(); if ( fqan == null ) return Constants.NON_VO_FQAN; else return fqan;
		case AUTOMOUNT_INDEX: return mp.isAutomaticallyMounted();
		}
		
		return null;
	}

}
