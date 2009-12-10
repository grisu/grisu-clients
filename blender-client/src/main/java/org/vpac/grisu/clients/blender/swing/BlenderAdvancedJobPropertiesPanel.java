package org.vpac.grisu.clients.blender.swing;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class BlenderAdvancedJobPropertiesPanel extends JPanel {
	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public BlenderAdvancedJobPropertiesPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JLabel lblNameOfOutput = new JLabel("Name of output frames");
		add(lblNameOfOutput, "2, 2, right, default");
		
		textField = new JTextField();
		add(textField, "4, 2, fill, default");
		textField.setColumns(10);

	}

}
