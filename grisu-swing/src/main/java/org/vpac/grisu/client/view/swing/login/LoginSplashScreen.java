package org.vpac.grisu.client.view.swing.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.vpac.grisu.client.control.status.StatusEvent;
import org.vpac.grisu.client.control.status.StatusListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class LoginSplashScreen extends JDialog implements StatusListener {

	private JLabel label_1;
	private JTextField statusTextField;
	private JLabel label;
	private JPanel panel;
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			LoginSplashScreen dialog = new LoginSplashScreen();
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
	public LoginSplashScreen() {
		super();
		setBounds(100, 100, 500, 403);
		getContentPane().add(getPanel(), BorderLayout.CENTER);
		//
	}
	protected JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(
				"left:18dlu, default:grow(1.0), left:18dlu",
				"top:25dlu, default:grow(1.0), top:21dlu, top:22dlu, 33dlu, top:18dlu"));
			panel.add(getLabel(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
			panel.add(getStatusTextField(), new CellConstraints(2, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
			panel.add(getLabel_1(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.FILL));
		}
		return panel;
	}
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setOpaque(true);
			label.setBackground(Color.WHITE);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			URL picURL = getClass().getResource("/images/ARCS_LogoTag_smaller.jpg");
			ImageIcon grisu = new ImageIcon(picURL);
			label.setIcon(grisu);
		}
		return label;
	}
	protected JTextField getStatusTextField() {
		if (statusTextField == null) {
			statusTextField = new JTextField();
			statusTextField.setHorizontalAlignment(SwingConstants.CENTER);
			statusTextField.setEditable(false);
		}
		return statusTextField;
	}
	protected JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel();
			label_1.setFont(new Font("Sans", Font.BOLD, 16));
			label_1.setHorizontalAlignment(SwingConstants.CENTER);
			label_1.setHorizontalTextPosition(SwingConstants.CENTER);
			label_1.setText("Connecting to grisu web service...");
		}
		return label_1;
	}

	public void setNewStatus(StatusEvent e) {
		
		getStatusTextField().setText(e.getStatus());
		
	}

}
