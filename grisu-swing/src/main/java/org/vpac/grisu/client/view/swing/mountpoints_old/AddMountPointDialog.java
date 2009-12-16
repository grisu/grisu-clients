package org.vpac.grisu.client.view.swing.mountpoints_old;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class AddMountPointDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static final int CANCEL_OPTION = -1;
	public static final int ADD_MOUNTPOINT_OPTION = 1;

	private JPanel jContentPane = null;

	private JPanel rootPanel = null;

	private JLabel urlLabel = null;

	private JTextField urlTextField = null;

	private JLabel mountPointLabel = null;

	private JTextField mountpointTextField = null;

	private JButton okButton = null;

	private JButton cancelButton = null;

	private JEditorPane advancedHelpEditorPane = null;

	private JLabel titleLabel = null;

	private int option = 0;

	private JPanel advancedPanel = null;

	private JCheckBox advancedConfigCheckBox = null;

	private JPanel SimpleAddMountPointPanel = null;

	private JComboBox siteComboBox = null;

	private JLabel siteLabel = null;

	private JLabel simpleConfigLabel = null;

	private JCheckBox autoHomeDirCheckBox = null;

	private String[] allSites = null;

	private JScrollPane jScrollPane = null;

	private boolean advancedAddMountPoint = false;

	/**
	 * @param owner
	 */
	public AddMountPointDialog(Frame owner, String[] possibleSites) {
		super(owner, true);
		this.allSites = possibleSites;
		initialize();
	}

	private void enableAdvancedPanel(boolean enabled) {
		this.urlLabel.setEnabled(enabled);
		this.urlLabel.setVisible(enabled);
		this.getUrlTextField().setEnabled(enabled);
		this.getUrlTextField().setVisible(enabled);
		this.mountPointLabel.setEnabled(enabled);
		this.mountPointLabel.setVisible(enabled);
		this.getMountpointTextField().setEnabled(enabled);
		this.getMountpointTextField().setVisible(enabled);
		this.getAutoHomeDirCheckBox().setEnabled(enabled);
		this.getAutoHomeDirCheckBox().setVisible(enabled);
		this.getAdvancedHelpEditorPane().setEnabled(enabled);
		this.getAdvancedHelpEditorPane().setVisible(enabled);
		this.siteLabel.setEnabled(!enabled);
		this.siteComboBox.setEnabled(!enabled);
		this.simpleConfigLabel.setEnabled(!enabled);
	}

	/**
	 * This method initializes advancedConfigCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getAdvancedConfigCheckBox() {
		if (advancedConfigCheckBox == null) {
			advancedConfigCheckBox = new JCheckBox();
			advancedConfigCheckBox.setText("Advanced configuration");
			advancedConfigCheckBox.setVisible(true);
			advancedConfigCheckBox
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								advancedAddMountPoint = true;
								enableAdvancedPanel(true);
							} else {
								advancedAddMountPoint = false;
								enableAdvancedPanel(false);
							}
						}
					});
		}
		return advancedConfigCheckBox;
	}

	/**
	 * This method initializes advancedHelpEditorPane
	 * 
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getAdvancedHelpEditorPane() {
		if (advancedHelpEditorPane == null) {
			advancedHelpEditorPane = new JEditorPane();
			advancedHelpEditorPane
					.setText("Please enter the root filesystem and a mountpoint to mount a new filesystem. ");
			advancedHelpEditorPane.setEnabled(false);
			advancedHelpEditorPane.setVisible(false);
		}
		return advancedHelpEditorPane;
	}

	/**
	 * This method initializes advancedPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAdvancedPanel() {
		if (advancedPanel == null) {
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 1;
			gridBagConstraints31.anchor = GridBagConstraints.EAST;
			gridBagConstraints31.insets = new Insets(0, 0, 15, 15);
			gridBagConstraints31.gridy = 3;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridwidth = 2;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.insets = new Insets(10, 10, 15, 0);
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 2;
			gridBagConstraints.insets = new Insets(5, 15, 15, 0);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 2;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(5, 15, 15, 15);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(15, 15, 15, 0);
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridwidth = 1;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(15, 15, 15, 15);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridwidth = 2;
			gridBagConstraints6.gridx = -1;
			gridBagConstraints6.gridy = 4;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.insets = new Insets(0, 15, 15, 15);
			advancedPanel = new JPanel();
			advancedPanel.setLayout(new GridBagLayout());
			advancedPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			advancedPanel.add(getAdvancedHelpEditorPane(), gridBagConstraints6);
			advancedPanel.add(getMountpointTextField(), gridBagConstraints3);
			advancedPanel.add(mountPointLabel, gridBagConstraints2);
			advancedPanel.add(getUrlTextField(), gridBagConstraints1);
			advancedPanel.add(urlLabel, gridBagConstraints);
			advancedPanel.add(getAdvancedConfigCheckBox(), gridBagConstraints8);
			advancedPanel.add(getAutoHomeDirCheckBox(), gridBagConstraints31);
		}
		return advancedPanel;
	}

	/**
	 * This method initializes autoHomeDirCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getAutoHomeDirCheckBox() {
		if (autoHomeDirCheckBox == null) {
			autoHomeDirCheckBox = new JCheckBox();
			autoHomeDirCheckBox
					.setText("Use your home directory on that filesystem");
			autoHomeDirCheckBox.setEnabled(false);
			autoHomeDirCheckBox
					.setHorizontalTextPosition(SwingConstants.LEADING);
			autoHomeDirCheckBox.setVisible(false);
		}
		return autoHomeDirCheckBox;
	}

	public boolean getAutoHomeDirectory() {
		if (getAdvancedConfigCheckBox().isSelected()) {
			return getAutoHomeDirCheckBox().isSelected();
		} else {
			return true;
		}
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AddMountPointDialog.this.option = AddMountPointDialog.CANCEL_OPTION;
					AddMountPointDialog.this.setVisible(false);
				}
			});
		}
		return cancelButton;
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
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getRootPanel());
		}
		return jScrollPane;
	}

	public String getMountPoint() {
		return getMountpointTextField().getText();
	}

	/**
	 * This method initializes mountpointTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getMountpointTextField() {
		if (mountpointTextField == null) {
			mountpointTextField = new JTextField();
			mountpointTextField.setEnabled(false);
			mountpointTextField.setVisible(false);
		}
		return mountpointTextField;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("Add Mountpoint(s)");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AddMountPointDialog.this.option = AddMountPointDialog.ADD_MOUNTPOINT_OPTION;
					AddMountPointDialog.this.setVisible(false);
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes rootPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRootPanel() {
		if (rootPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.insets = new Insets(0, 15, 10, 15);
			gridBagConstraints21.gridwidth = 2;
			gridBagConstraints21.gridy = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new Insets(0, 15, 0, 15);
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(15, 15, 15, 0);
			gridBagConstraints7.gridy = 0;
			titleLabel = new JLabel();
			titleLabel.setText("Mounting a new filesystem");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints5.insets = new Insets(25, 0, 15, 0);
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.gridy = 5;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints4.insets = new Insets(25, 15, 15, 15);
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.gridy = 5;
			mountPointLabel = new JLabel();
			mountPointLabel.setText("Mountpoint:");
			mountPointLabel.setEnabled(false);
			mountPointLabel.setVisible(false);
			urlLabel = new JLabel();
			urlLabel.setText("URL:");
			urlLabel.setEnabled(false);
			urlLabel.setVisible(false);
			rootPanel = new JPanel();
			rootPanel.setLayout(new GridBagLayout());
			rootPanel.add(getOkButton(), gridBagConstraints4);
			rootPanel.add(getCancelButton(), gridBagConstraints5);
			rootPanel.add(titleLabel, gridBagConstraints7);
			rootPanel.add(getAdvancedPanel(), gridBagConstraints11);
			rootPanel.add(getSimpleAddMountPointPanel(), gridBagConstraints21);
		}
		return rootPanel;
	}

	/**
	 * This method initializes SimpleAddMountPointPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSimpleAddMountPointPanel() {
		if (SimpleAddMountPointPanel == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.insets = new Insets(10, 10, 0, 0);
			gridBagConstraints12.gridwidth = 2;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 0;
			simpleConfigLabel = new JLabel();
			simpleConfigLabel.setText("Simple configuration");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.insets = new Insets(0, 15, 0, 0);
			gridBagConstraints10.gridy = 1;
			siteLabel = new JLabel();
			siteLabel.setText("Site to run jobs:");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.insets = new Insets(10, 10, 10, 10);
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.gridy = 1;
			gridBagConstraints9.weightx = 1.0;
			SimpleAddMountPointPanel = new JPanel();
			SimpleAddMountPointPanel.setLayout(new GridBagLayout());
			SimpleAddMountPointPanel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.RAISED));
			SimpleAddMountPointPanel
					.add(getSiteComboBox(), gridBagConstraints9);
			SimpleAddMountPointPanel.add(siteLabel, gridBagConstraints10);
			SimpleAddMountPointPanel.add(simpleConfigLabel,
					gridBagConstraints12);
		}
		return SimpleAddMountPointPanel;
	}

	public String getSite() {
		return (String) getSiteComboBox().getSelectedItem();
	}

	/**
	 * This method initializes siteComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSiteComboBox() {
		if (siteComboBox == null) {
			siteComboBox = new JComboBox(allSites);
		}
		return siteComboBox;
	}

	public String getURL() {
		return getUrlTextField().getText();
	}

	/**
	 * This method initializes urlTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getUrlTextField() {
		if (urlTextField == null) {
			urlTextField = new JTextField();
			urlTextField.setEnabled(false);
			urlTextField.setVisible(false);
		}
		return urlTextField;
	}

	public int getUserOption() {
		return option;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(743, 444);
		this.setContentPane(getJContentPane());
	}

	public boolean isAdvancedAddMountPoint() {
		return advancedAddMountPoint;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
