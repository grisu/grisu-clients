package org.vpac.grisu.client.view.swing.fileTransfers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.control.files.FileTransferComparator;
import org.vpac.grisu.client.control.files.FileTransferEvent;
import org.vpac.grisu.client.control.files.FileTransferListener;
import org.vpac.grisu.client.control.files.FileTransferManager;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileTransferPanel extends JPanel implements FileTransferListener {

	static final Logger myLogger = Logger.getLogger(FileTransferPanel.class
			.getName());

	/**
	 * WindowBuilder generated method.<br>
	 * Please don't remove this method or its invocations.<br>
	 * It used by WindowBuilder to associate the {@link javax.swing.JPopupMenu}
	 * with parent.
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
	private JMenuItem viewDetailsMenuItem;
	private JPopupMenu popupMenu;
	private EventTableModel<FileTransfer> tableModel = null;

	private FileTransferManager fileTransferManager = null;

	private EventSelectionModel<FileTransfer> selectionModel = null;
	private JTable table;

	private JScrollPane scrollPane;

	/**
	 * Create the panel
	 */
	public FileTransferPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC },
				new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow(1.0)"),
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC }));
		add(getScrollPane(), new CellConstraints(2, 2));
		//
	}

	public void fileTransferEventOccured(FileTransferEvent e) {

		tableModel.fireTableDataChanged();

	}

	/**
	 * @return
	 */
	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getViewDetailsMenuItem());
		}
		return popupMenu;
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

					if (e.getClickCount() == 2) {
						showTransferDetails();
					}

				}
			});
			addPopup(table, getPopupMenu());
		}
		return table;
	}

	/**
	 * @return
	 */
	protected JMenuItem getViewDetailsMenuItem() {
		if (viewDetailsMenuItem == null) {
			viewDetailsMenuItem = new JMenuItem();
			viewDetailsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					showTransferDetails();
				}
			});
			viewDetailsMenuItem.setText("View details");
		}
		return viewDetailsMenuItem;
	}

	public void initialize(FileTransferManager ftm) {

		this.fileTransferManager = ftm;
		String[] propertyNames = new String[] { "sourcesString",
				"targetDirectory", "transferStatusString" };
		String[] columnLabels = new String[] { "Sources", "Target", "Status" };

		TableFormat<FileTransfer> tf = GlazedLists.tableFormat(
				FileTransfer.class, propertyNames, columnLabels);
		SortedList<FileTransfer> sortedTransferList = new SortedList<FileTransfer>(
				this.fileTransferManager.getTransferList(),
				new FileTransferComparator());

		// // add filtering to the table
		// FilterList<FileTransfer> textFilteredIssues = new
		// FilterList<FileTransfer>(
		// sortedJobList, new TextComponentMatcherEditor(getFilterField(),new
		// FileTransferFilterator()));

		// jobModel = new
		// EventTableModel<GrisuJobMonitoringObject>(textFilteredIssues, tf);
		tableModel = new EventTableModel<FileTransfer>(sortedTransferList, tf);
		selectionModel = new EventSelectionModel<FileTransfer>(
				sortedTransferList);

		getTable().setModel(tableModel);
		getTable().setSelectionModel(selectionModel);
		ftm.addListener(this);

	}

	private void setPanelBusy(boolean busy) {
		getTable().setEnabled(!busy);
	}

	private void showTransferDetails() {
		new Thread() {
			public void run() {
				setPanelBusy(true);
				try {
					for (FileTransfer transfer : selectionModel.getSelected()) {
						FileTransferDetailsDialog ftdd = new FileTransferDetailsDialog();

						ftdd.getFileTransferDetailsPanel().setFileTransfer(
								transfer);
						setPanelBusy(false);
						ftdd.setVisible(true);
					}
				} catch (Exception e) {
					myLogger.error(e);
					e.printStackTrace();
					// Utils.showErrorMessage(em, GlazedJobMonitorPanel.this,
					// "couldNotFindJobDirecotory", e);
				} finally {
					setPanelBusy(false);
				}
			}

		}.start();

	}
}
