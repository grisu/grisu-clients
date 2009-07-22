package org.vpac.grisu.client.view.swing.mountpoints;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.client.control.login.LoginException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;

public class MountPointsManagementDialog extends JDialog {

	private MountPointManagementPanel mountPointManagementPanel;
	
	public static EnvironmentManager loginLocalhost(String username, char[] password) throws LoginException, ServiceInterfaceException {

		   EnvironmentManager em = null;

		   // creating an object which holds all the login information. For this example we assume we always use the specified grisu service url and 
		   // myproxy server/port. It's possible to also set a httproxy here.
		   LoginParams loginparams = new LoginParams("http://localhost:8080/grisu-ws/services/grisu", username, password, "myproxy.arcs.org.au", "443");
					
	     // do the login
	     em = ServiceInterfaceFactoryOld.login(loginparams);

	     return em;
	}
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			MountPointsManagementDialog dialog = new MountPointsManagementDialog();
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			dialog.setVisible(true);
			
			EnvironmentManager em = loginLocalhost(args[0], args[1].toCharArray());
			dialog.initialize(em);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MountPointsManagementDialog(EnvironmentManager em) {
		this();
		initialize(em);
	}

	/**
	 * Create the dialog
	 */
	public MountPointsManagementDialog() {
		super();
		this.setModal(true);
		setBounds(100, 100, 752, 496);
		getContentPane().add(getMountPointManagementPanel(), BorderLayout.CENTER);

		//
	}
	
	public void initialize(EnvironmentManager em) {
		getMountPointManagementPanel().initialize(em);
	}
	/**
	 * @return
	 */
	protected MountPointManagementPanel getMountPointManagementPanel() {
		if (mountPointManagementPanel == null) {
			mountPointManagementPanel = new MountPointManagementPanel();
		}
		return mountPointManagementPanel;
	}


}
