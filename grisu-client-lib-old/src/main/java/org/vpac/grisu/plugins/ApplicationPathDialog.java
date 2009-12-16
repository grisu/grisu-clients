package org.vpac.grisu.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ApplicationPathDialog extends JDialog {

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			ApplicationPathDialog dialog = new ApplicationPathDialog();
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
	private JButton cancelButton;
	private JTextField pathTextField;
	private JTextField Browse;
	private JButton okButton;
	private JButton button;
	private JEditorPane editorPane;

	private JPanel panel;

	private boolean cancel = false;

	/**
	 * Create the dialog
	 */
	public ApplicationPathDialog() {
		super();
		this.setModal(true);
		setBounds(100, 100, 320, 203);
		getContentPane().add(getPanel(), BorderLayout.CENTER);
		//
	}

	public boolean cancelled() {
		return cancel;
	}

	/**
	 * @return
	 */
	protected JButton getButton() {
		if (button == null) {
			button = new JButton();
			button.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					JFileChooser jfc = new JFileChooser();
					jfc
							.setDialogTitle("Please choose an application to open the file");
					jfc.setDialogType(JFileChooser.OPEN_DIALOG);
					jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					jfc.setMultiSelectionEnabled(false);
					int option = jfc.showOpenDialog(ApplicationPathDialog.this);

					if (option != JFileChooser.CANCEL_OPTION) {
						File selected = jfc.getSelectedFile();
						getPathTextField().setText(selected.getPath());
					}

				}
			});
			button.setText("Browse");
		}
		return button;
	}

	/**
	 * @return
	 */
	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					cancel = true;
					ApplicationPathDialog.this.setVisible(false);
				}
			});
			cancelButton.setText("Cancel");
		}
		return cancelButton;
	}

	/**
	 * @return
	 */
	protected JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setContentType("text/html");
			editorPane.setEditable(false);
			editorPane.setBackground(Color.WHITE);
		}
		return editorPane;
	}

	/**
	 * @return
	 */
	protected JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					cancel = false;
					ApplicationPathDialog.this.setVisible(false);
				}
			});
			okButton.setText("OK");
		}
		return okButton;
	}

	/**
	 * @return
	 */
	protected JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					new RowSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC }));
			panel.add(getEditorPane(), new CellConstraints(2, 2, 3, 1,
					CellConstraints.FILL, CellConstraints.FILL));
			panel.add(getButton(), new CellConstraints(4, 4));
			panel.add(getOkButton(), new CellConstraints(4, 6));
			panel.add(getPathTextField(), new CellConstraints(2, 4));
			panel.add(getCancelButton(), new CellConstraints(2, 6,
					CellConstraints.RIGHT, CellConstraints.DEFAULT));
		}
		return panel;
	}

	public String getPath() {
		String path = getPathTextField().getText();

		if (!new File(path).exists() || new File(path).isDirectory()) {
			return null;
		}

		return path;
	}

	/**
	 * @return
	 */
	protected JTextField getPathTextField() {
		if (pathTextField == null) {
			pathTextField = new JTextField();
		}
		return pathTextField;
	}

	public void setApplication(String title, String description) {
		this.setTitle(title);
		this.getEditorPane().setText(description);
	}

}
