

package org.vpac.grisu.client.view.swing.mountpoints_old;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.vpac.grisu.client.control.EnvironmentManager;

public class SiteWizard extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private SiteWizardPanel siteWizardPanel = null;
	private EnvironmentManager em = null;

	/**
	 * @param owner
	 */
	public SiteWizard(EnvironmentManager em, Frame owner) {
		super(owner, true);
		this.em = em;
		initialize();
		this.setLocationByPlatform(true);

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 380);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getSiteWizardPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes siteWizardPanel	
	 * 	
	 * @return org.vpac.grisu.client.view.swing.mountpoints.SiteWizardPanel	
	 */
	private SiteWizardPanel getSiteWizardPanel() {
		if (siteWizardPanel == null) {
			siteWizardPanel = new SiteWizardPanel(em, this);
		}
		return siteWizardPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
