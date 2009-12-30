package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXFrame;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.model.events.ApplicationEventListener;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

import au.org.arcs.auth.shibboleth.Shibboleth;

public class GrisuBlenderApp {

	/**
	 * Launch the application.
	 * 
	 * @throws LoginException
	 */
	public static void main(String[] args) throws LoginException {

		Shibboleth.initDefaultSecurityProvider();
		
		new ApplicationEventListener();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		// final ServiceInterface si = LoginManager.myProxyLogin("Local",
		// args[0],
		// args[1].toCharArray());

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrisuBlenderApp window = new GrisuBlenderApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JXFrame frame;

	/**
	 * Create the application.
	 */
	public GrisuBlenderApp() {
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

		BlenderMainPanel mainPanel = new BlenderMainPanel();
		LoginPanel lp = new LoginPanel(mainPanel);
		frame.getContentPane().add(lp, BorderLayout.CENTER);
	}

}
