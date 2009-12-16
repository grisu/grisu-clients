package org.vpac.grisu.client.view.swing.login;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.vpac.grisu.client.control.login.LoginInterface;
import org.vpac.grisu.client.model.login.LoginPanelsHolder;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.security.light.utils.ProxyLightLibraryManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LoginPanel extends JPanel implements LoginPanelsHolder,
		HttpProxyPanelListener {

	private NewGrisuSlcsLoginPanel grisuSlcsLoginPanel;
	private MyProxyLoginPanel myProxyLoginPanel;
	private HttpProxyPanel httpProxyPanel;
	private ServiceInterfaceUrlsPanel serviceInterfaceUrlsPanel;
	private CertificateLoginPanel certificateLoginPanel;
	private boolean cancelOption = false;

	private JTabbedPane tabbedPane = null;

	private ServiceInterface serviceInterface = null;

	private LoginInterface loginInterface = null;

	/**
	 * Create the panel
	 */
	public LoginPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("132dlu"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("133dlu"),
				RowSpec.decode("top:7dlu"), }));

		tabbedPane = new JTabbedPane();

		add(tabbedPane, new CellConstraints(2, 2, 3, 1,
				CellConstraints.DEFAULT, CellConstraints.FILL));

		tabbedPane.addTab("Shibboleth login", null, getGrisuSlcsLoginPanel(),
				null);

		if (ProxyLightLibraryManager.prerequisitesForProxyCreationAvailable()) {
			tabbedPane.addTab("Standard login", null,
					getCertificateLoginPanel(), null);
		}
		tabbedPane.addTab("MyProxy login", null, getMyProxyLoginPanel(), null);

		add(getServiceInterfaceUrlsPanel(), new CellConstraints(2, 4, 3, 1));
		add(getHttpProxyPanel(), new CellConstraints(2, 6, 3, 1,
				CellConstraints.DEFAULT, CellConstraints.TOP));

		getHttpProxyPanel().addHttpProxyPanelListener(this);

		if (tabbedPane.getTabCount() > 1) {
			int index = ClientPropertiesManager.getLastSelectedTab();
			if (index <= tabbedPane.getTabCount() - 1) {
				tabbedPane.setSelectedIndex(index);
			}
		}

	}

	public void cancelled() {
		this.serviceInterface = null;
		this.cancelOption = true;
		this.loginInterface.setUserCancelledLogin(true);
	}

	protected CertificateLoginPanel getCertificateLoginPanel() {
		if (certificateLoginPanel == null) {
			certificateLoginPanel = new CertificateLoginPanel();
			certificateLoginPanel.setParamsHolder(this);
		}
		return certificateLoginPanel;
	}

	/**
	 * @return
	 */
	protected NewGrisuSlcsLoginPanel getGrisuSlcsLoginPanel() {
		if (grisuSlcsLoginPanel == null) {
			grisuSlcsLoginPanel = new NewGrisuSlcsLoginPanel();
			grisuSlcsLoginPanel.setParamsHolder(this);
		}
		return grisuSlcsLoginPanel;
	}

	protected HttpProxyPanel getHttpProxyPanel() {
		if (httpProxyPanel == null) {
			httpProxyPanel = new HttpProxyPanel();
		}
		return httpProxyPanel;
	}

	public LoginParams getLoginParams() {

		String serviceInterfaceUrl = getServiceInterfaceUrlsPanel()
				.getServiceInterfaceUrl();
		String myProxyUsername = null;
		char[] myProxyPassphrase = null;
		String httpProxy = getHttpProxyPanel().getProxyServer();
		int httpProxyPort = getHttpProxyPanel().getProxyPort();
		String httpProxyUsername = getHttpProxyPanel().getProxyUsername();
		char[] httpProxyPassphrase = getHttpProxyPanel().getProxyPassword();

		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		int myProxyPort = MyProxyServerParams.getMyProxyPort();

		try {
			// this is needed because of a possible round-robin myproxy server
			myProxyServer = InetAddress.getByName(myProxyServer)
					.getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LoginParams params = new LoginParams(serviceInterfaceUrl,
				myProxyUsername, myProxyPassphrase, myProxyServer, new Integer(
						myProxyPort).toString(), httpProxy, httpProxyPort,
				httpProxyUsername, httpProxyPassphrase);

		return params;
	}

	protected MyProxyLoginPanel getMyProxyLoginPanel() {
		if (myProxyLoginPanel == null) {
			myProxyLoginPanel = new MyProxyLoginPanel();
			myProxyLoginPanel.setParamsHolder(this);
		}
		return myProxyLoginPanel;
	}

	public int getSelectedLoginPanel() {
		return tabbedPane.getSelectedIndex();
	}

	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	protected ServiceInterfaceUrlsPanel getServiceInterfaceUrlsPanel() {
		if (serviceInterfaceUrlsPanel == null) {
			serviceInterfaceUrlsPanel = new ServiceInterfaceUrlsPanel();
		}
		return serviceInterfaceUrlsPanel;
	}

	public void httpProxyValueChanged() {

		// getGrisuSlcsLoginPanel().getSlcsLoginPanel().disableLoginButtonUntilIDPRefreshButtonIsPressed();

	}

	public boolean isCancelOption() {
		return cancelOption;
	}

	public void loggedIn(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
		this.cancelOption = false;
		this.loginInterface.saveCurrentConnectionsSettingsAsDefault();
		this.loginInterface.setServiceInterface(this.serviceInterface);
	}

	public void setLoginInterface(LoginInterface li) {
		this.loginInterface = li;
	}

	public void setSelectedLoginPanel(int index) {
		tabbedPane.setSelectedIndex(index);
	}

	public void setServiceInterface(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

}
