package org.vpac.grisu.clients.blender.swing;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BlenderJobCreationPanel extends JPanel {
	private JTextField blendFileTextField;
	private JButton blendFileBrowseButton;
	private JLabel lblBlendFile;
	public BlenderJobCreationPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		add(getLblBlendFile(), "2, 2");
		add(getBlendFileTextField(), "2, 4, fill, default");
		add(getBlendFileBrowseButton(), "4, 4");
	}

	private JTextField getBlendFileTextField() {
		if (blendFileTextField == null) {
			blendFileTextField = new JTextField();
			blendFileTextField.setColumns(10);
		}
		return blendFileTextField;
	}
	private JButton getBlendFileBrowseButton() {
		if (blendFileBrowseButton == null) {
			blendFileBrowseButton = new JButton("Browse");
			blendFileBrowseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
				}
			});
		}
		return blendFileBrowseButton;
	}
	private JLabel getLblBlendFile() {
		if (lblBlendFile == null) {
			lblBlendFile = new JLabel("Blend file");
		}
		return lblBlendFile;
	}
}
