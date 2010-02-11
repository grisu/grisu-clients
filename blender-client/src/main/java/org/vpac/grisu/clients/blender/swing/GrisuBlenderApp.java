package org.vpac.grisu.clients.blender.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXFrame;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.events.ApplicationEventListener;
import org.vpac.grisu.frontend.view.swing.GrisuMainPanel;
import org.vpac.grisu.frontend.view.swing.login.LoginPanel;

public class GrisuBlenderApp {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		LoginManager.initEnvironment();

		new ApplicationEventListener();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

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

	private GrisuMainPanel mainPanel;

	private LoginPanel lp;

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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().setLayout(new BorderLayout());
		Set<String> apps = new HashSet<String>();
		apps.add("blender");
		mainPanel = new GrisuMainPanel(false, apps);
		mainPanel.addJobCreationPanel(new BlenderJobCreationPanel());
		//		LoginPanel lp = new LoginPanel(mainPanel, true);
		LoginPanel lp = new LoginPanel(mainPanel);
		frame.getContentPane().add(lp, BorderLayout.CENTER);
	}

	public void setServiceInterface(ServiceInterface si) {

		if ( lp == null ) {
			throw new IllegalStateException("LoginPanel not initialized.");
		}

		if ( si == null ) {
			throw new NullPointerException("ServiceInterface can't be null");
		}

		lp.setServiceInterface(si);
	}

}
