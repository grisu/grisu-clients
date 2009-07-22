package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.files.FileChooserEvent;
import org.vpac.grisu.client.view.swing.files.FileChooserParent;
import org.vpac.grisu.client.view.swing.files.SiteFileChooserDialog;
import org.vpac.historyRepeater.HistoryManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class InputFileOld extends JPanel {

	public static final String COMBOBOX_PANEL = "combobox";
	public static final String TEXTFIELD_PANEL = "textfield";

	private JComboBox comboBox;
	private JLabel requiredLabel;
	private static SiteFileChooserDialog sfcd = null;

	private JButton browseButton;
	private JLabel errorLabel;
	private JTextField textField;
	private TemplateNode templateNode = null;

	private DefaultComboBoxModel comboboxModel = new DefaultComboBoxModel();

	private String renderMode = TEXTFIELD_PANEL;
	private HistoryManager historyManager = null;
	private boolean useHistory = false;
	private boolean lastUsed = false;
	
	private GrisuFileObject currentDirectory = null;

	private String defaultValue = null;
	private String[] prefills = null;
	

	/**
	 * Create the panel
	 */
	public InputFileOld() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("106dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("9dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));

		add(getErrorLabel(), new CellConstraints(2, 2, 3, 1,
				CellConstraints.FILL, CellConstraints.DEFAULT));
		add(getBrowseButton(), new CellConstraints(4, 4, 3, 1));
		add(getRequiredLabel(), new CellConstraints(6, 2,
				CellConstraints.RIGHT, CellConstraints.DEFAULT));

	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {
		this.templateNode = node;
//		this.templateNode.setTemplateNodeValueSetter(this);
//		node.addTemplateNodeListener(this);

		if ("1".equals(this.templateNode.getMultiplicity())) {
			getRequiredLabel().setText("*");
		} else {
			getRequiredLabel().setText("");
		}

		defaultValue = node.getDefaultValue();
		prefills = node.getPrefills();
		
		if ( this.templateNode.getOtherProperties().containsKey(TemplateNode.LAST_USED_PARAMETER) ) {
			lastUsed = true;
			
//			String url = historyManager.getEntries(this.templateNode.getName()+"_"+TemplateNode.LAST_USED_PARAMETER).get(0);
//			URI uri = null;
//			if ( url == null || "".equals(url) ) {
//				File file = new File(System.getProperty("user.home"));
//				uri = file.toURI();
//			} else {
//				if ( url.startsWith("local://") ) {
//					File file = new File(url.substring(8));
//					if ( ! file.exists() ) {
//						file = new File(System.getProperty("user.home"));
//					}
//					uri = file.toURI();
//				} else {
//					try {
//						uri = new URI(url);
//					} catch (URISyntaxException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						File file = new File(System.getProperty("user.home"));
//						uri = file.toURI();
//					}
//				}
//			}
//			
//			currentDirectory = uri;
			
		} else {
			lastUsed = false;
		}

		if (this.templateNode.getOtherProperties().containsKey(
				TemplateNode.USE_HISTORY)) {
			useHistory = true;
			historyManager = this.templateNode.getTemplate()
					.getEnvironmentManager().getHistoryManager();


			String maxString = this.templateNode
					.getOtherProperty(TemplateNode.USE_HISTORY);
			
			
			if (!TemplateNode.NON_MAP_PARAMETER.equals(maxString)) {
				int maxValues = Integer.parseInt(maxString);
				historyManager.setMaxNumberOfEntries(this.templateNode.getName(), maxValues);
			}

		} else {
			useHistory = false;
		}

		try {
			renderMode = this.templateNode.getOtherProperties().get("render");
		} catch (RuntimeException e1) {
			// fallback
			renderMode = COMBOBOX_PANEL;
		}
		if (renderMode == null)
			renderMode = COMBOBOX_PANEL;

		if (TEXTFIELD_PANEL.equals(renderMode)) {
			renderMode = TEXTFIELD_PANEL;

			add(getTextField(), new CellConstraints(2, 4, CellConstraints.FILL,
					CellConstraints.DEFAULT));

			if (this.templateNode.getOtherProperties().containsKey(
					TemplateNode.LOCKED_KEY)) {
			}
			if (defaultValue != null && !"".equals(defaultValue)) {
				getTextField().setText(defaultValue);
			}

		} else {
			renderMode = COMBOBOX_PANEL;

			add(getComboBox(), new CellConstraints(2, 4));

			if (this.templateNode.getOtherProperties().containsKey(
					TemplateNode.LOCKED_KEY)) {
				getComboBox().setEditable(false);
			} else {
				getComboBox().setEditable(true);
			}

			fillComboBox();

		}

		String title = templateNode.getTitle();

		this.setBorder(new TitledBorder(null, title,
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		this.setToolTipText(templateNode.getDescription());

	}
	
	private void fillComboBox() {
		
		comboboxModel.removeAllElements();
		
		if (useHistory && historyManager != null
				&& historyManager.getEntries(this.templateNode.getName()).size() > 0) {
			for (String entry : historyManager.getEntries(this.templateNode.getName())) {
				comboboxModel.insertElementAt(entry, 0);
			}
		}

		if (prefills != null) {
			for (String prefill : prefills) {
				if ( prefill != null && !"".equals(prefill) )
					comboboxModel.addElement(prefill);
			}
		}

		if (defaultValue != null && !"".equals(defaultValue)) {
			comboboxModel.setSelectedItem(defaultValue);
		} else {
			comboboxModel.setSelectedItem(null);

		}
		
	}

	// public void setTemplateNodeValue() {
	//
	// try {
	// this.templateNode.setValue(getTextField().getText());
	// } catch (TemplateValidateException e) {
	// errorLabel.setText(e.getLocalizedMessage());
	// errorLabel.setVisible(true);
	// }
	//		
	// }
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
	protected JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setVisible(false);
			errorLabel.setForeground(Color.RED);
		}
		return errorLabel;
	}

	/**
	 * @return
	 */
	protected JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton();
			browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

//					try {
//
//						getSiteFileChooserDialog().addUserInputListener(
//								InputFile.this);
//						getSiteFileChooserDialog().setVisible(true);
//					} finally {
//						getSiteFileChooserDialog().removeUserInputListener(
//								InputFile.this);
//						
//					}

				}
			});
			browseButton.setText("Browse");
		}
		return browseButton;
	}

	private static SiteFileChooserDialog getSiteFileChooserDialog() {
		if (sfcd == null) {
			sfcd = new SiteFileChooserDialog(null);
			sfcd.setSite(FileConstants.LOCAL_NAME);
		}
		return sfcd;
	}

	public void userInput(FileChooserEvent event) {

		String value = null;
		if (event.getSelectedFile() != null) {
			URI uri = event.getSelectedFile().getURI();
			value = uri.toString();
//			if (uri.getScheme().startsWith("file")) {
//				value = "local://" + uri.getPath();
//			} else {
//				value = uri.toString();
//			}
		}

		if (TEXTFIELD_PANEL.equals(renderMode)) {
			getTextField().setText(value);
		} else {
			getComboBox().setSelectedItem(value);
		}

		getSiteFileChooserDialog().setVisible(false);

	}



	public String getExternalSetValue() {

		if (COMBOBOX_PANEL.equals(renderMode)) {
			return (String) getComboBox().getSelectedItem();
		} else {
			return getTextField().getText();
		}

	}

	/**
	 * @return
	 */
	protected JLabel getRequiredLabel() {
		if (requiredLabel == null) {
			requiredLabel = new JLabel();
		}
		return requiredLabel;
	}

	public void reset() {
		
		String value = getExternalSetValue();
		
		if ( useHistory )
			historyManager.addHistoryEntry(this.templateNode.getName(), value, new Date());
		
		if ( lastUsed )
			historyManager.addHistoryEntry(this.templateNode.getName()+"_"+TemplateNode.LAST_USED_PARAMETER, getSiteFileChooserDialog().getCurrentDirectory().toString());
		
		if (COMBOBOX_PANEL.equals(renderMode)) {
			fillComboBox();
		}
			
	}

	/**
	 * @return
	 */
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(comboboxModel);
		}
		return comboBox;
	}
}
