package org.vpac.grisu.client.view.swing.mountpoints;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.MountPoint;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MountPointManagementPanel extends JPanel implements MountPointsListener {

	private JMenuItem unmountItem;
	private JPopupMenu popupMenu;
	private AddMountPointPanel addMountPointPanel;
	private JTable table;
	private JScrollPane scrollPane;
	
	
	private EnvironmentManager em = null;
	

	
	private MountPointsTableModel mpModel = null;
	
	/**
	 * Create the panel
	 */
	public MountPointManagementPanel() {
		super();
		setPreferredSize(new Dimension(700, 400));
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getScrollPane(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		add(getAddMountPointPanel(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		//
	}
	
	public void initialize(EnvironmentManager em) {
		this.em = em;
		em.addMountPointListener(this);
		getAddMountPointPanel().initialize(em);
		mpModel = new MountPointsTableModel(em);
		getTable().setModel(mpModel);

	}
	
	private void refresh() {

	}

	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTable());
		}
		return scrollPane;
	}
	/**
	 * @return
	 */
	protected JTable getTable() {
		if (table == null) {
			table = new JTable();
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					int clickedRow = getTable().rowAtPoint(e.getPoint());
					table.setRowSelectionInterval(clickedRow, clickedRow);


				}
			});
			addPopup(table, getPopupMenu());
//			table.setDefaultRenderer(Object.class, new MountPointTableCellRenderer());
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		}
		return table;
	}
	/**
	 * @return
	 */
	protected AddMountPointPanel getAddMountPointPanel() {
		if (addMountPointPanel == null) {
			addMountPointPanel = new AddMountPointPanel();
		}
		return addMountPointPanel;
	}
	/**
	 * @return
	 */
	/**
	 * @return
	 */

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {

		mpModel.refresh();
		

	}
	/**
	 * @return
	 */
	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getUnmountItem());
		}
		return popupMenu;
	}


	/**
	 * WindowBuilder generated method.<br>
	 * Please don't remove this method or its invocations.<br>
	 * It used by WindowBuilder to associate the {@link javax.swing.JPopupMenu} with parent.
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				
				if (e.isPopupTrigger())
					showMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	/**
	 * @return
	 */
	protected JMenuItem getUnmountItem() {
		if (unmountItem == null) {
			unmountItem = new JMenuItem();
			unmountItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					int selRow = getTable().getSelectedRow();
					MountPoint mp = (MountPoint)(getTable().getModel().getValueAt(selRow, 0));
					
					if ( mp.isAutomaticallyMounted() ) {
						JOptionPane.showMessageDialog(MountPointManagementPanel.this,
							    "Could not remove this file share because\nit was automatically added.",
							    "File share not removable",
							    JOptionPane.ERROR_MESSAGE);
						return;
					} else {
						em.umount(mp);
						return;
					}
				}
			});
			unmountItem.setText("Remove");
		}
		return unmountItem;
	}
}
