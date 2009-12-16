package org.vpac.grisu.client.view.swing.preview;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.vpac.grisu.client.model.files.GrisuFileObject;

public class TextPreviewDialog extends JDialog {

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			TextPreviewDialog dialog = new TextPreviewDialog();
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

	private TextViewerPanel textViewerPanel;

	/**
	 * Create the dialog
	 */
	public TextPreviewDialog() {
		super();
		setBounds(100, 100, 500, 375);
		getContentPane().add(getTextViewerPanel());
		//
	}

	/**
	 * @return
	 */
	protected TextViewerPanel getTextViewerPanel() {
		if (textViewerPanel == null) {
			textViewerPanel = new TextViewerPanel();
		}
		return textViewerPanel;
	}

	public void setFile(GrisuFileObject file) {

		getTextViewerPanel().setFileToPreview(file);

	}

}
