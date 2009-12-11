package org.vpac.grisu.clients.blender.swing;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
		
		add(getOutputNameTextField(), "4, 2, fill, default");

	}

	private JTextField getOutputNameTextField() {
		if ( textField == null ) {
			textField = new JTextField();
			textField.setColumns(10);
		}
		return textField;
	}
	
	public void lockUI(final boolean lock) {
		
		SwingUtilities.invokeLater(new Thread() {
			
			public void run() {
				getOutputNameTextField().setEnabled(!lock);
			}
		});
	}

}
