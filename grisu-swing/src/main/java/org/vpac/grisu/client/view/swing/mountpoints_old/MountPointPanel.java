

package org.vpac.grisu.client.view.swing.mountpoints_old;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.model.MountPoint;

public class MountPointPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	static final Logger myLogger = Logger.getLogger(MountPointPanel.class.getName());

	private MountPoint mountpoint = null;

	private JLabel jLabel = null;

	private JLabel jLabel1 = null;

	private JButton jButton = null;

	private JTextField urlField = null;

	private JTextField mountPointField = null;
	
	private EnvironmentManager em  = null;
	
	/**
	 * This is the default constructor
	 */
	public MountPointPanel(EnvironmentManager em, MountPoint mountpoint) {
		super();
		this.em = em;
		this.mountpoint = mountpoint;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = GridBagConstraints.BOTH;
		gridBagConstraints4.gridy = 1;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.insets = new Insets(10, 15, 15, 15);
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.insets = new Insets(10, 15, 0, 15);
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.insets = new Insets(0, 0, 15, 15);
		gridBagConstraints2.gridy = 2;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.anchor = GridBagConstraints.EAST;
		gridBagConstraints1.insets = new Insets(10, 15, 15, 0);
		gridBagConstraints1.gridy = 1;
		jLabel1 = new JLabel();
		jLabel1.setText("mounted on:");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(15, 15, 0, 0);
		gridBagConstraints.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("URL:");
		this.setLayout(new GridBagLayout());
		this.setSize(new Dimension(487, 102));
		this.add(jLabel, gridBagConstraints);
		this.add(jLabel1, gridBagConstraints1);
		this.add(getJButton(), gridBagConstraints2);
		this.add(getUrlField(), gridBagConstraints3);
		this.add(getMountPointField(), gridBagConstraints4);
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Unmount");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					myLogger.debug("Unmounting: "+mountpoint.getAlias());
					em.umount(mountpoint);
					MountPointPanel.this.setVisible(false);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes urlField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getUrlField() {
		if (urlField == null) {
			urlField = new JTextField(mountpoint.getRootUrl());
			urlField.setEditable(false);
		}
		return urlField;
	}

	/**
	 * This method initializes mountPointField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMountPointField() {
		if (mountPointField == null) {
			mountPointField = new JTextField(mountpoint.getAlias());
			mountPointField.setEditable(false);
		}
		return mountPointField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
