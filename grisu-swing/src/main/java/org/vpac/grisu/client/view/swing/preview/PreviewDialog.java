

package org.vpac.grisu.client.view.swing.preview;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.vpac.grisu.client.model.files.GrisuFileObject;

public class PreviewDialog extends JDialog {

	private PreviewPanel previewPanel;
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			PreviewDialog dialog = new PreviewDialog();
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

	/**
	 * Create the dialog
	 */
	public PreviewDialog() {
		super();
		setBounds(100, 100, 500, 375);
		getContentPane().add(getPreviewPanel(), BorderLayout.CENTER);
		//
	}
	protected PreviewPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new PreviewPanel();
		}
		return previewPanel;
	}
	
	public void setFile(GrisuFileObject file) {
		getPreviewPanel().previewFile(file);
	}

}
