package org.vpac.grisu.client.view.swing.login;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.vpac.grisu.client.model.login.LoginPanelsHolder;

import au.org.arcs.auth.shibboleth.ShibLoginPanel;
import au.org.arcs.jcommons.interfaces.SlcsListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import au.org.arcs.auth.slcs.SLCS;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class NewGrisuSlcsLoginPanel extends JPanel implements SlcsListener {
	
	public static final String DEFAULT_SLCS_URL = "https://slcs1.arcs.org.au/SLCS/login";

	private LoginPanelsHolder loginPanelHolder = null;
	private ShibLoginPanel shibLoginPanel;
	private JButton button;
	private SLCS slcs;
	
	/**
	 * Create the panel.
	 */
	public NewGrisuSlcsLoginPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("450px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("108px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getShibLoginPanel_1(), "2, 2, fill, fill");
		add(getButton(), "2, 4, right, bottom");
		
		slcs = new SLCS(getShibLoginPanel_1());
		slcs.addSlcsListener(this);

	}
	private ShibLoginPanel getShibLoginPanel_1() {
		if (shibLoginPanel == null) {
			shibLoginPanel = new ShibLoginPanel(DEFAULT_SLCS_URL);
		}
		return shibLoginPanel;
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
	public void slcsLoginComplete(X509Certificate cert, PrivateKey privateKey) {
		// TODO Auto-generated method stub
		
	}
	public void slcsLoginFailed(String message, Exception optionalException) {
		// TODO Auto-generated method stub
		
	}
	public void setParamsHolder(LoginPanel loginPanel) {
		
		this.loginPanelHolder = loginPanel;
	}

}
