package org.vpac.grisu.client.view.swing.utils;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ErrorDialog extends JDialog {

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			ErrorDialog dialog = new ErrorDialog();
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
	private JScrollPane scrollPane;

	private JPanel panel;

	/**
	 * Create the dialog
	 */
	public ErrorDialog() {
		super();
		setBounds(100, 100, 500, 375);
		getContentPane().add(getPanel(), BorderLayout.CENTER);
		//
	}

	/**
	 * @return
	 */
	protected JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
					RowSpec.decode("4dlu"),
					RowSpec.decode("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC }));
			panel.add(getScrollPane(), new CellConstraints(2, 2, 3, 1,
					CellConstraints.FILL, CellConstraints.FILL));
		}
		return panel;
	}

	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
		}
		return scrollPane;
	}

}
