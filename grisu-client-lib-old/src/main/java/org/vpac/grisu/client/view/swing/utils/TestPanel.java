package org.vpac.grisu.client.view.swing.utils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class TestPanel extends JPanel {

	private JTextField fssafsadfsTextField;
	private JLabel sdfasfsafsdfsdfsafsfLabel;

	/**
	 * Create the panel
	 */
	public TestPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] { new ColumnSpec("44dlu"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC },
				new RowSpec[] { FormFactory.DEFAULT_ROWSPEC,
						FormFactory.RELATED_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC }));
		add(getSdfasfsafsdfsdfsafsfLabel(), new CellConstraints(1, 1));
		add(getFssafsadfsTextField(), new CellConstraints(1, 3,
				CellConstraints.CENTER, CellConstraints.DEFAULT));
		//
	}

	/**
	 * @return
	 */
	protected JTextField getFssafsadfsTextField() {
		if (fssafsadfsTextField == null) {
			fssafsadfsTextField = new JTextField();
			fssafsadfsTextField.setHorizontalAlignment(SwingConstants.TRAILING);
			fssafsadfsTextField
					.setText("asfsfsdfsdfsdfsdfsdfsdfsdafsdfsdfxxxx1");
		}
		return fssafsadfsTextField;
	}

	/**
	 * @return
	 */
	protected JLabel getSdfasfsafsdfsdfsafsfLabel() {
		if (sdfasfsafsdfsdfsafsfLabel == null) {
			sdfasfsafsdfsdfsafsfLabel = new JLabel();
			sdfasfsafsdfsdfsafsfLabel
					.setHorizontalAlignment(SwingConstants.TRAILING);
			sdfasfsafsdfsdfsafsfLabel.setAutoscrolls(true);
			sdfasfsafsdfsdfsafsfLabel.setText("xxxxxyyyyyyyzzzzzz1");
		}
		return sdfasfsafsdfsdfsafsfLabel;
	}

}
