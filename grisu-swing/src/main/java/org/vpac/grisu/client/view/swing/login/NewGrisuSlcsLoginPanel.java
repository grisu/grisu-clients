package org.vpac.grisu.client.view.swing.login;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.ietf.jgss.GSSCredential;
import org.python.core.PyInstance;
import org.vpac.grisu.client.model.login.LoginPanelsHolder;
import org.vpac.grisu.client.view.swing.utils.Utils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.helpDesk.model.Person;
import org.vpac.helpDesk.model.PersonException;
import org.vpac.security.light.CredentialHelpers;
import org.vpac.security.light.plainProxy.PlainProxy;

import au.org.arcs.auth.shibboleth.ShibListener;
import au.org.arcs.auth.shibboleth.ShibLoginPanel;
import au.org.arcs.auth.shibboleth.Shibboleth;
import au.org.arcs.auth.slcs.SLCS;
import au.org.arcs.jcommons.interfaces.SlcsListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class NewGrisuSlcsLoginPanel extends JPanel implements SlcsListener,
		ShibListener {

	public static final String DEFAULT_SLCS_URL = "https://slcs1.arcs.org.au/SLCS/login";

	private LoginPanelsHolder loginPanelHolder = null;
	private ShibLoginPanel shibLoginPanel;
	private JButton button;
	private SLCS slcs;

	/**
	 * Create the panel.
	 */
	public NewGrisuSlcsLoginPanel() {

		Shibboleth.initDefaultSecurityProvider();

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("450px:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("108px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getShibLoginPanel_1(), "2, 2, fill, fill");
		add(getButton(), "2, 4, right, bottom");

		getShibLoginPanel_1().addShibListener(this);
		slcs = new SLCS(getShibLoginPanel_1());
		slcs.addSlcsListener(this);

	}

	private JButton getButton() {
		if (button == null) {
			button = new JButton("Login");
			button.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					getShibLoginPanel_1().login();

				}
			});
		}
		return button;
	}

	private ShibLoginPanel getShibLoginPanel_1() {
		if (shibLoginPanel == null) {
			shibLoginPanel = new ShibLoginPanel(DEFAULT_SLCS_URL);
		}
		return shibLoginPanel;
	}

	private Person getUser() {

		String username = "anonymous";
		Person user = null;
		try {
			user = new Person(ClientPropertiesManager.getClientConfiguration(),
					username + " Grisu Shibboleth user");
		} catch (Exception e) {
			try {
				user = new Person("Anonymous");
			} catch (PersonException e1) {
				e1.printStackTrace(System.err);
				// this should never happen
			}
		}
		user.setRole(Person.USER_ROLE);
		user.setNickname(username);
		return user;
	}

	private void lockUI(boolean lock) {
		getButton().setEnabled(!lock);
		if (lock) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void setParamsHolder(LoginPanel loginPanel) {

		this.loginPanelHolder = loginPanel;
	}

	public void shibLoginComplete(PyInstance arg0) {

	}

	public void shibLoginFailed(Exception arg0) {

		lockUI(false);
		Utils.showErrorMessage(getUser(), NewGrisuSlcsLoginPanel.this,
				"loginError", arg0);

	}

	public void shibLoginStarted() {
		lockUI(true);
	}

	public void slcsLoginComplete(X509Certificate cert, PrivateKey privateKey) {

		ServiceInterface si;

		try {
			GSSCredential proxy;
			if (loginPanelHolder != null) {
				proxy = PlainProxy.init(slcs.getCertificate(), slcs
						.getPrivateKey(), 24 * 10);
			} else {
				return;
			}

			si = LoginManager.login(CredentialHelpers
					.unwrapGlobusCredential(proxy), null, null, null,
					loginPanelHolder.getLoginParams());

			loginPanelHolder.loggedIn(si);

		} catch (LoginException e1) {
			Utils.showErrorMessage(getUser(), NewGrisuSlcsLoginPanel.this,
					"loginError", e1);
			lockUI(false);
			return;
		} catch (Exception e) {
			Utils.showErrorMessage(getUser(), NewGrisuSlcsLoginPanel.this,
					"pluginError", e);
			lockUI(false);
			return;
		}

		if (si != null) {
			lockUI(false);
			loginPanelHolder.loggedIn(si);
		} else {
			lockUI(false);
			Utils.showErrorMessage(getUser(), NewGrisuSlcsLoginPanel.this,
					"unknownLoginError", null);
		}

	}

	public void slcsLoginFailed(String message, Exception optionalException) {

		lockUI(false);
		Utils.showErrorMessage(getUser(), NewGrisuSlcsLoginPanel.this,
				"loginError", optionalException);

	}

}
