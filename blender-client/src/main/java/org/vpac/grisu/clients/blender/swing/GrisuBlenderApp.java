package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;

public class GrisuBlenderApp {

	private JFrame frame;
	private ServiceInterface si;

	/**
	 * Launch the application.
	 * @throws LoginException 
	 */
	public static void main(String[] args) throws LoginException {
		
		try {
			UIManager.setLookAndFeel(UIManager
					.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			
		}

		final ServiceInterface si = LoginManager.login();
		
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
		frame = new JFrame();
		frame.setBounds(100, 100, 567, 526);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		BlenderJobCreationPanel blenderJobCreationPanel = new BlenderJobCreationPanel(si);
		frame.getContentPane().add(blenderJobCreationPanel, BorderLayout.CENTER);
	}

}
