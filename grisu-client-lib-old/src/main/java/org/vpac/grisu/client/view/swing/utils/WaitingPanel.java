package org.vpac.grisu.client.view.swing.utils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class WaitingPanel extends JPanel {

	private JTextField loadingTemplateTextField;
	private JLabel label;
	private JProgressBar progressBar;

	/**
	 * Create the panel
	 */
	public WaitingPanel() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				new ColumnSpec("left:17dlu"),
				new ColumnSpec("default:grow(1.0)"),
				new ColumnSpec("left:16dlu") }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getProgressBar(), new CellConstraints(2, 6,
				CellConstraints.DEFAULT, CellConstraints.FILL));
		add(getLabel(), new CellConstraints(2, 4));
		add(getLoadingTemplateTextField(), new CellConstraints(2, 8));
		//
	}

	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
		}
		return label;
	}

	/**
	 * @return
	 */
	protected JTextField getLoadingTemplateTextField() {
		if (loadingTemplateTextField == null) {
			loadingTemplateTextField = new JTextField();
			loadingTemplateTextField.setEditable(false);
			loadingTemplateTextField
					.setHorizontalAlignment(SwingConstants.CENTER);
			loadingTemplateTextField.setText("loading template...");
		}
		return loadingTemplateTextField;
	}
	/**
	 * @return
	 */

	/**
	 * @return
	 */
	protected JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
		}
		return progressBar;
	}

}
