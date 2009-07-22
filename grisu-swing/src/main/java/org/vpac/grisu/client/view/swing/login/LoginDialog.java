

package org.vpac.grisu.client.view.swing.login;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.vpac.grisu.client.control.login.LoginInterface;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;

public class LoginDialog extends JDialog implements LoginInterface {

	private LoginPanel loginPanel = null;
	private boolean userCancelledLogin = false;
	private ServiceInterface serviceInterface = null;
	
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			LoginDialog dialog = new LoginDialog();
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
	public LoginDialog() {
		super();
		setModal(true);
		setTitle("Grisu login");
		setBounds(100, 100, 533, 544);

		loginPanel = new LoginPanel();
		loginPanel.setLoginInterface(this);
		getContentPane().add(loginPanel, BorderLayout.CENTER);
		//
	}
	
	public void setServiceInterface(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
		this.setVisible(false);
	}
	
	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	public boolean userCancelledLogin() {
		return userCancelledLogin;
	}

	public void setUserCancelledLogin(boolean cancelled) {
		this.userCancelledLogin = cancelled;
		this.setVisible(false);
	}

	public void saveCurrentConnectionsSettingsAsDefault() {

		LoginParams params = loginPanel.getLoginParams();

		ClientPropertiesManager.setDefaultServiceInterfaceUrl(params.getServiceInterfaceUrl());
		
		if ( params.getHttpProxy() != null && ! "".equals(params.getHttpProxy()) ) {
			ClientPropertiesManager.saveDefaultHttpProxy("true");
			ClientPropertiesManager.saveDefaultHttpProxyServer(params.getHttpProxy());
		} else {
			ClientPropertiesManager.saveDefaultHttpProxy("false");
		}
		
		
		if ( params.getHttpProxyPort() > 0 ) 
			ClientPropertiesManager.saveDefaultHttpProxyPort(new Integer(params.getHttpProxyPort()).toString());
			
		if ( params.getHttpProxyUsername() != null && ! "".equals(params.getHttpProxyUsername()) ) 
			ClientPropertiesManager.saveDefaultHttpProxyUsername(params.getHttpProxyUsername());

		ClientPropertiesManager.saveSelectedTab(loginPanel.getSelectedLoginPanel());
		
	}

}
