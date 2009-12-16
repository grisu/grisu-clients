package org.vpac.grisu.client.view.swing.login;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.apache.commons.configuration.ConfigurationException;
import org.vpac.grisu.settings.ClientPropertiesManager;

public class ServiceInterfaceUrlsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel jLabel = null;
	private JComboBox jComboBox = null;
	private DefaultComboBoxModel comboBoxModel = null;

	public static final String DEFAULT_GRISU_WS_SERVICEINTERFACEURL = "https://grisu.vpac.org/grisu-ws/services/grisu";

	/**
	 * This is the default constructor
	 */
	public ServiceInterfaceUrlsPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			comboBoxModel = new DefaultComboBoxModel(ClientPropertiesManager
					.getServiceInterfaceUrls());

			jComboBox = new JComboBox(comboBoxModel);
			this.jComboBox.setEnabled(true);
			jComboBox.setEditable(true);
			jComboBox.setPreferredSize(new Dimension(4, 24));
			try {
				String defaultUrl = System
						.getProperty("grisu.defaultServiceInterface");

				if (defaultUrl == null || "".equals(defaultUrl)) {
					defaultUrl = (String) ClientPropertiesManager
							.getClientConfiguration().getProperty(
									"defaultServiceInterfaceUrl");
				}

				// just so that there is a default
				if (defaultUrl == null || "".equals(defaultUrl)) {
					defaultUrl = DEFAULT_GRISU_WS_SERVICEINTERFACEURL;
				}
				jComboBox.setSelectedItem(defaultUrl);
			} catch (ConfigurationException e) {
				// that's ok.
			}
		}
		return jComboBox;
	}

	public String getServiceInterfaceUrl() {
		return (String) getJComboBox().getSelectedItem();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.insets = new Insets(10, 15, 15, 15);
		gridBagConstraints1.gridx = 0;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(15, 15, 0, 0);
		gridBagConstraints.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("ServiceInterface to connect to:");
		this.setSize(481, 96);
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		this.add(jLabel, gridBagConstraints);
		this.add(getJComboBox(), gridBagConstraints1);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
