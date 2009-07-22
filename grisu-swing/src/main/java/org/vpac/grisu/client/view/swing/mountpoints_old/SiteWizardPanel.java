

package org.vpac.grisu.client.view.swing.mountpoints_old;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.control.ServiceInterface;

public class SiteWizardPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList jList = null;

	private JTextPane jTextPane = null;

	private ServiceInterface serviceInterface = null;
	private EnvironmentManager em = null;

	private JButton jButton = null;

	private JDialog parent_dialog = null;

	/**
	 * This is the default constructor
	 */
	public SiteWizardPanel(EnvironmentManager em, JDialog parent_dialog) {
		super(true);
		this.em = em;
		this.parent_dialog = parent_dialog;
		this.serviceInterface = em.getServiceInterface();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.insets = new Insets(0, 0, 10, 9);
		gridBagConstraints11.anchor = GridBagConstraints.EAST;
		gridBagConstraints11.gridy = 2;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.insets = new Insets(10, 10, 0, 10);
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.gridx = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 0;
		this.setSize(300, 377);
		this.setLayout(new GridBagLayout());
		this.add(getJList(), gridBagConstraints);
		this.add(getJTextPane(), gridBagConstraints1);
		this.add(getJButton(), gridBagConstraints11);
	}

	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJList() {
		if (jList == null) {
			String[] allSites = serviceInterface.getAllSites();
			jList = new JList(allSites);
			if (jList.getModel().getSize() > 0)
				jList.setSelectedIndex(0);
		}
		return jList;
	}

	/**
	 * This method initializes jTextPane
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane
					.setText("In order to submit jobs to the grid you have to configure filesystems where you can stage in and stage out files. This wizard can do that for you. Just select the sites where you intend to run jobs and click the \"OK\" button.");
			jTextPane.setContentType("text/plain");
		}
		return jTextPane;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Ok");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					new Thread(new Runnable() {
						public void run() {
							parent_dialog.setVisible(false);
							getJButton().setEnabled(false);
							getJList().setEnabled(false);
							Object[] sites = getJList().getSelectedValues();
							String[] sites_array = new String[sites.length];
							for ( int i=0; i<sites.length; i++ ) {
								sites_array[i] = (String) sites[i];
							}
							SiteWizardPanel.this.setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
//							em.setUpHomeDirectories(sites_array);

							SiteWizardPanel.this
									.setCursor(Cursor
											.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							getJButton().setEnabled(true);
							getJList().setEnabled(true);
							parent_dialog.dispose();
						}
					}).start();
				}
			});
		}
		return jButton;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
