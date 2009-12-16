package org.vpac.grisu.client.view.swing.mountpoints_old;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.view.swing.utils.Utils;
import org.vpac.grisu.model.MountPoint;

public class MountsDialog extends JDialog implements MountPointsListener {

	static final Logger myLogger = Logger.getLogger(MountsDialog.class
			.getName());

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null; // @jve:decl-index=0:visual-constraint="-661,-211"

	private JScrollPane jScrollPane = null;

	private JPanel mountPointsPanel = null;

	private JPanel jContentPane1 = null;

	private JPanel jPanel = null;

	private JButton addMountPointButton = null;

	private JLabel jLabel = null;

	private EnvironmentManager em = null;

	/**
	 * @param owner
	 */
	public MountsDialog(EnvironmentManager em, Frame owner) {
		super(owner);
		this.em = em;
		this.em.addMountPointListener(this);
		initialize();
	}

	/**
	 * This method initializes addMountPointButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddMountPointButton() {
		if (addMountPointButton == null) {
			addMountPointButton = new JButton();
			addMountPointButton.setText("Add mountpoint");
			addMountPointButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							final AddMountPointDialog mpdialog = new AddMountPointDialog(
									null, em.getServiceInterface()
											.getAllSites().asArray());
							mpdialog.setVisible(true);
							int option = mpdialog.getUserOption();

							if (option == AddMountPointDialog.ADD_MOUNTPOINT_OPTION) {
								try {
									if (mpdialog.isAdvancedAddMountPoint()) {
										em.mount(mpdialog.getURL(), mpdialog
												.getMountPoint(), mpdialog
												.getAutoHomeDirectory());
									} else {
										new Thread(new Runnable() {
											public void run() {
												MountsDialog.this
														.setCursor(Cursor
																.getPredefinedCursor(Cursor.WAIT_CURSOR));
												String site = mpdialog
														.getSite();
												// em.setUpHomeDirectories(new
												// String[]{site});
												MountsDialog.this
														.setCursor(Cursor
																.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
											}
										}).start();
									}
								} catch (Exception e1) {
									Utils.showErrorMessage(em,
											MountsDialog.this, "couldNotMount",
											e1);

								}
							}
						}
					});
		}
		return addMountPointButton;
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
			jContentPane.setSize(new Dimension(282, 101));
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.insets = new Insets(15, 15, 10, 0);
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("All mounted filesystems:");
			jPanel = new JPanel();
			GridBagConstraints panelConstraints = new GridBagConstraints();
			panelConstraints.gridx = 0;
			panelConstraints.fill = GridBagConstraints.BOTH;
			panelConstraints.weightx = 1.0;
			panelConstraints.weighty = 1.0;
			panelConstraints.gridy = 1;
			GridBagConstraints buttonConstraints = new GridBagConstraints();
			buttonConstraints.gridx = 0;
			buttonConstraints.anchor = GridBagConstraints.SOUTHEAST;
			buttonConstraints.insets = new Insets(10, 0, 15, 15);
			buttonConstraints.weighty = 0.0;
			buttonConstraints.gridy = 2;
			jPanel.setLayout(new GridBagLayout());
			jPanel.setPreferredSize(new Dimension(600, 250));
			jPanel.add(getJScrollPane(), panelConstraints);
			jPanel.add(getAddMountPointButton(), buttonConstraints);
			jPanel.add(jLabel, gridBagConstraints);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(120, 50));
			jScrollPane.setViewportView(getMountPointsPanel());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes mountPointsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMountPointsPanel() {
		if (mountPointsPanel == null) {
			mountPointsPanel = new JPanel();
			mountPointsPanel.setLayout(new BoxLayout(mountPointsPanel,
					BoxLayout.PAGE_AXIS));
			MountPoint[] mps = em.getServiceInterface().df().getMountpoints()
					.toArray(new MountPoint[] {});

			for (MountPoint mp : mps) {
				mountPointsPanel.add(new MountPointPanel(em, mp));
			}
		}
		return mountPointsPanel;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(529, 215);
		this.setContentPane(getJContentPane());
	}

	// protected void refreshMountPoints() {
	//		
	// mountPointsPanel.removeAll();
	// mountPointsModel.removeAllElements();
	// EnvironmentManager.getMountPoints(serviceInterface);
	// MountPoint[] mps = serviceInterface.df();
	//		
	// for ( MountPoint mp : mps) {
	// mountPointsPanel.add(new MountPointPanel(mp, serviceInterface));
	// mountPointsModel.addElement(mp.getMountpoint());
	// }
	//
	// // this.getJContentPane().revalidate();
	//		
	// }

	public void mountPointsChanged(MountPointEvent mpe) {

		if (mpe.getEventType() == MountPointEvent.MOUNTPOINTS_REFRESHED) {
			getMountPointsPanel().removeAll();

			for (MountPoint mp : mpe.getMountPoints()) {
				getMountPointsPanel().add(new MountPointPanel(em, mp));
			}

		} else if (mpe.getEventType() == MountPointEvent.MOUNTPOINT_REMOVED) {
			// Nothing to do in that case

		} else if (mpe.getEventType() == MountPointEvent.MOUNTPOINT_ADDED) {
			getMountPointsPanel().add(
					new MountPointPanel(em, mpe.getMountPoint()));
		}
		getMountPointsPanel().revalidate();

	}

} // @jve:decl-index=0:visual-constraint="10,10"
