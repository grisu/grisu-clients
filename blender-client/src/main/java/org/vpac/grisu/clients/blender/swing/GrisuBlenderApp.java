package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXFrame;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;

public class GrisuBlenderApp {

	/**
	 * Launch the application.
	 * 
	 * @throws LoginException
	 */
	public static void main(String[] args) throws LoginException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		final ServiceInterface si = LoginManager.myProxyLogin("Local", args[0],
				args[1].toCharArray());

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrisuBlenderApp window = new GrisuBlenderApp(si);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JXFrame frame;

	private final ServiceInterface si;

	/**
	 * Create the application.
	 */
	public GrisuBlenderApp(ServiceInterface si) {
		this.si = si;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JXFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		BlenderMainPanel mainPanel = new BlenderMainPanel(si);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

}
