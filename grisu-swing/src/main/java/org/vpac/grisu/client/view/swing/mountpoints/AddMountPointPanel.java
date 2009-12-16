package org.vpac.grisu.client.view.swing.mountpoints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

import au.org.arcs.jcommons.constants.Constants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class AddMountPointPanel extends JPanel {

	private JComboBox comboBox;
	private JLabel voLabel;
	private JButton addButton;
	private JTextField textField_1;
	private JTextField textField;
	private JLabel rootUrlLabel;
	private JLabel aliasLabel;

	private EnvironmentManager em = null;
	private DefaultComboBoxModel fqanModel = new DefaultComboBoxModel();

	/**
	 * Create the panel
	 */
	public AddMountPointPanel() {
		super();
		setBorder(new TitledBorder(null, "Add mountpoint manually",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, new RowSpec("default"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getAliasLabel(), new CellConstraints(2, 2, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getRootUrlLabel(), new CellConstraints(2, 4, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getTextField(), new CellConstraints(4, 2));
		add(getTextField_1(), new CellConstraints(4, 4));
		add(getAddButton(), new CellConstraints(4, 8, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getVoLabel(), new CellConstraints(2, 6, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getComboBox(), new CellConstraints(4, 6, CellConstraints.FILL,
				CellConstraints.DEFAULT));
		//
	}

	/**
	 * @return
	 */
	protected JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					String alias = getTextField().getText();
					URI url = null;
					try {
						url = new URI(getTextField_1().getText());
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
					String fqan = (String) getComboBox().getSelectedItem();

					try {
						em.mount(url.toString(), alias, fqan, false);
					} catch (RemoteFileSystemException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}

				}
			});
			addButton.setText("Add");
		}
		return addButton;
	}

	/**
	 * @return
	 */
	protected JLabel getAliasLabel() {
		if (aliasLabel == null) {
			aliasLabel = new JLabel();
			aliasLabel.setText("Alias:");
		}
		return aliasLabel;
	}

	/**
	 * @return
	 */
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(fqanModel);
		}
		return comboBox;
	}

	/**
	 * @return
	 */
	protected JLabel getRootUrlLabel() {
		if (rootUrlLabel == null) {
			rootUrlLabel = new JLabel();
			rootUrlLabel.setText("Root url:");
		}
		return rootUrlLabel;
	}

	/**
	 * @return
	 */
	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
		}
		return textField;
	}

	/**
	 * @return
	 */
	protected JTextField getTextField_1() {
		if (textField_1 == null) {
			textField_1 = new JTextField();
		}
		return textField_1;
	}

	/**
	 * @return
	 */
	protected JLabel getVoLabel() {
		if (voLabel == null) {
			voLabel = new JLabel();
			voLabel.setText("VO:");
		}
		return voLabel;
	}

	public void initialize(EnvironmentManager em) {
		this.em = em;
		fqanModel.removeAllElements();
		fqanModel.addElement(Constants.NON_VO_FQAN);
		for (String fqan : em.getFqans()) {
			fqanModel.addElement(fqan);
		}
	}

}
