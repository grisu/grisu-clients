package org.vpac.grisu.client.view.swing.fileTransfers;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

public class FileTransferDetailsDialog extends JDialog implements
		FileTransferDetailsPanelHolder {

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			FileTransferDetailsDialog dialog = new FileTransferDetailsDialog();
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private FileTransferDetailsPanel fileTransferDetailsPanel;

	/**
	 * Create the dialog
	 */
	public FileTransferDetailsDialog() {
		super();
		setBounds(100, 100, 500, 375);
		getContentPane().add(getFileTransferDetailsPanel());
		//
	}

	/**
	 * @return
	 */
	protected FileTransferDetailsPanel getFileTransferDetailsPanel() {
		if (fileTransferDetailsPanel == null) {
			fileTransferDetailsPanel = new FileTransferDetailsPanel(this);
		}
		return fileTransferDetailsPanel;
	}

	public void okButtonClicked() {
		this.dispose();
	}

}
