package org.vpac.grisu.client.view.swing.files;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.view.swing.preview.PreviewPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FilePanelWithOptionalPreview extends JPanel {

	private JButton refreshButton;
	private PreviewPanel previewPanel;
	private JScrollPane scrollPane;
	private JPanel previewPanelHolder;
	private JCheckBox checkBox;
	private JLabel label;
	private JTextField textField;

	private GrisuFileObject file = null;

	private boolean fileChanged = false;

	/**
	 * Create the panel
	 */
	public FilePanelWithOptionalPreview() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getTextField(), new CellConstraints(4, 2, CellConstraints.FILL,
				CellConstraints.DEFAULT));
		add(getLabel(), new CellConstraints(2, 2, CellConstraints.LEFT,
				CellConstraints.DEFAULT));
		add(getCheckBox(), new CellConstraints(2, 4, CellConstraints.LEFT,
				CellConstraints.DEFAULT));
		add(getPreviewPanelHolder(), new CellConstraints(2, 6, 3, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		showPreviewPanel(false);
		add(getRefreshButton(), new CellConstraints(4, 4,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));
		//
	}

	protected JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox();
			checkBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (checkBox.isSelected()) {
						showPreviewPanel(true);
					} else {
						showPreviewPanel(false);
					}
				}
			});
			checkBox.setText("Preview");
		}
		return checkBox;
	}

	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("name");
		}
		return label;
	}

	protected PreviewPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new PreviewPanel();
			previewPanel.setMinimumSize(new Dimension(0, 20));
		}
		return previewPanel;
	}

	protected JPanel getPreviewPanelHolder() {
		if (previewPanelHolder == null) {
			previewPanelHolder = new JPanel();
			previewPanelHolder.setLayout(new BorderLayout());
			previewPanelHolder.add(getScrollPane(), BorderLayout.CENTER);
		}
		return previewPanelHolder;
	}

	protected JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (getPreviewPanel() != null) {
						try {
							FilePanelWithOptionalPreview.this.setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							getPreviewPanel().refresh();
						} finally {
							FilePanelWithOptionalPreview.this
									.setCursor(Cursor
											.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
			});
			URL picURL = getClass().getResource("/images/refresh.png");
			ImageIcon refresh = new ImageIcon(picURL);
			refreshButton.setIcon(refresh);
		}
		return refreshButton;
	}

	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setMinimumSize(new Dimension(0, 0));
			scrollPane.setViewportView(getPreviewPanel());
		}
		return scrollPane;
	}

	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
		}
		return textField;
	}

	public void setFile(GrisuFileObject file) {
		setFile(file.getName(), file);
	}

	public void setFile(String title, GrisuFileObject file) {
		this.file = file;
		fileChanged = true;

		getLabel().setText(title);
		getTextField().setText(file.getURI().toString());
	}

	protected void showPreviewPanel(boolean show) {

		if (show && fileChanged) {
			getPreviewPanel().previewFile(file);
			fileChanged = false;
		}
		getPreviewPanelHolder().setVisible(show);
		getRefreshButton().setVisible(show);
	}

}
