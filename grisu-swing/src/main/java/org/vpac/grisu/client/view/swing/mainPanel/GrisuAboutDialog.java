package org.vpac.grisu.client.view.swing.mainPanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

public class GrisuAboutDialog extends JDialog {

	/**
	 * Launch the application
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrisuAboutDialog dialog = new GrisuAboutDialog();
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
		});
	}
	private JTextArea grisuHomepageTextArea;
	private JScrollPane scrollPane;
	private JLabel version02Label;
	private JLabel grisuLabel;
	private JLabel label;

	private JPanel panel;

	/**
	 * Create the dialog
	 */
	public GrisuAboutDialog() {
		super();
		setTitle("About");
		setBounds(100, 100, 675, 375);
		getContentPane().add(getPanel(), BorderLayout.CENTER);
		//
	}

	/**
	 * @return
	 */
	protected JTextArea getGrisuHomepageTextArea() {
		if (grisuHomepageTextArea == null) {
			grisuHomepageTextArea = new JTextArea();
			grisuHomepageTextArea.setEditable(false);
			grisuHomepageTextArea.setMargin(new Insets(10, 10, 10, 10));
			grisuHomepageTextArea
					.setText("For help, please visit the Grisu manual page on the arcs website:\n\nhttp://www.arcs.org.au/products-services/systems-services/compute-services/using-grisu\n\nFor information about the Grisu project, visit the project website:\n\nhttp://grisu.arcs.org.au");
		}
		return grisuHomepageTextArea;
	}

	/**
	 * @return
	 */
	protected JLabel getGrisuLabel() {
		if (grisuLabel == null) {
			grisuLabel = new JLabel();
			grisuLabel.setHorizontalAlignment(SwingConstants.CENTER);
			grisuLabel.setText("Grisu");
		}
		return grisuLabel;
	}

	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setIcon(SwingResourceManager.getIcon(GrisuAboutDialog.class,
					"/images/ARCS_LogoTag_even_smaller.jpg"));
		}
		return label;
	}

	/**
	 * @return
	 */
	protected JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC }));
			panel.add(getLabel(), new CellConstraints(2, 2));
			panel.add(getGrisuLabel(), new CellConstraints(4, 2));
			panel.add(getVersion02Label(), new CellConstraints(2, 4, 3, 1));
			panel.add(getScrollPane(), new CellConstraints(2, 6, 3, 1,
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
			scrollPane.setViewportView(getGrisuHomepageTextArea());
		}
		return scrollPane;
	}

	/**
	 * @return
	 */
	protected JLabel getVersion02Label() {
		if (version02Label == null) {
			version02Label = new JLabel();
			version02Label.setHorizontalAlignment(SwingConstants.CENTER);
			version02Label.setText("Version 0.2");
		}
		return version02Label;
	}

}
