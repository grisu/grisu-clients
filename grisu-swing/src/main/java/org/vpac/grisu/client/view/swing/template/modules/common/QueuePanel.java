package org.vpac.grisu.client.view.swing.template.modules.common;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.VersionObject;
import org.vpac.grisu.client.model.template.modules.Common;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class QueuePanel extends JPanel implements FqanListener, MountPointsListener {

	private JLabel pleaseSpecifyTheLabel;
	private JTextField textField;
	private JPanel modulePanel;
	private JPanel switchPanel;
	static final Logger myLogger = Logger.getLogger(QueuePanel.class.getName());

	public static final String ALL_QUEUES_PARAMETER = "allQueues";
	public static final String HIDE_ALL_QUEUES_CHECKBOX = "hideAllQueuesCheckbox";
	
	public static final String VERSION_PANEL = "versionPanel";
	public static final String MODULE_PANEL = "modulePanel";

	private JComboBox versionCombobox;
	private JCheckBox versionCheckbox;
	private JPanel versionPanel;
	private JCheckBox allQueuesCheckbox;
	private JComboBox queueCombobox;
	private JLabel label_1;
	private JComboBox siteCombobox;
	private JLabel label;

	private DefaultComboBoxModel siteModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel versionModel = new DefaultComboBoxModel();

	private boolean showAllQueuesCheckbox = true;

	private Common commonGenericModule = null;

	// just to get easier to the TemplateNodes
	private TemplateNode hostName = null;
	private TemplateNode executionFileSystem = null;
	private TemplateNode module = null;

//	private DefaultTemplateNodeValueSetter hostnameSetter = new DefaultTemplateNodeValueSetter();
//	private DefaultTemplateNodeValueSetter executionFsSetter = new DefaultTemplateNodeValueSetter();
//	private DefaultTemplateNodeValueSetter moduleSetter = new DefaultTemplateNodeValueSetter();

	private FormLayout layout = null;
	private boolean versionModelLocked = false;
	private boolean queueModelLocked = false;

	/**
	 * Create the panel
	 */
	public QueuePanel() {
		super();
		setBorder(new TitledBorder(null, "Submission details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		layout = new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("131dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("8dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("64dlu"),
				FormFactory.RELATED_GAP_ROWSPEC});
		setLayout(layout);
		add(getLabel(), new CellConstraints(2, 3));
		add(getSiteCombobox(), new CellConstraints(4, 3));
		add(getLabel_1(), new CellConstraints(2, 5));
		add(getQueueCombobox(), new CellConstraints(4, 5));
		add(getAllQueuesCheckbox(), new CellConstraints(4, 1,
				CellConstraints.RIGHT, CellConstraints.BOTTOM));
		add(getSwitchPanel(), new CellConstraints(4, 6));
		//
	}

	public void initialize(Common commonGenericModule) {

		this.commonGenericModule = commonGenericModule;

		this.commonGenericModule.getTemplate().getEnvironmentManager().addFqanListener(this);
		this.commonGenericModule.getTemplate().getEnvironmentManager().addMountPointListener(this);

		hostName = this.commonGenericModule.getTemplateNodes().get(
				TemplateTagConstants.HOSTNAME_TAG_NAME);
		executionFileSystem = this.commonGenericModule.getTemplateNodes().get(
				TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME);
		module = this.commonGenericModule.getTemplateNodes().get(
				TemplateTagConstants.MODULE_TAG_NAME);

		getTextField().setText(module.getDefaultValue());
		commonGenericModule.setModule(getTextField().getText());
		
		if ( commonGenericModule.usesMds() ) {

		if (!hostName.hasProperty(HIDE_ALL_QUEUES_CHECKBOX)) {
			this.showAllQueuesCheckbox = true;
		} else {
			this.showAllQueuesCheckbox = false;
			layout.setRowSpec(2, new RowSpec("0dlu"));
		}

		if (hostName.hasProperty(ALL_QUEUES_PARAMETER)) {
			this.getAllQueuesCheckbox().setSelected(true);
			switchPanel(MODULE_PANEL);
		} else {
			this.getAllQueuesCheckbox().setSelected(false);
			switchPanel(VERSION_PANEL);
		}
		} else {
			this.showAllQueuesCheckbox = false;
			layout.setRowSpec(2, new RowSpec("0dlu"));
			switchPanel(MODULE_PANEL);
		} 

//		if (hostName.hasProperty(HIDE_ALL_QUEUES_CHECKBOX)
//				&& hostName.hasProperty(ALL_QUEUES_PARAMETER)) {
//			myLogger.debug("Version panel will be displayed.");
//		} else {
//
//		}

		fillSiteComboBox();
	}
	

	private void fillSiteComboBox() {

		queueModelLocked = true;

		String oldSite = null;
		oldSite = (String) siteModel.getSelectedItem();

		siteModel.removeAllElements();

		for (String site : this.commonGenericModule
				.getCurrentlyAvailableSites()) {
			siteModel.addElement(site);
		}

		if (siteModel.getIndexOf(oldSite) >= 0) {
			siteModel.setSelectedItem(oldSite);
		} else {
			if (siteModel.getSize() > 0) {
				siteCombobox.setSelectedIndex(0);
			} else {
				queueModel.removeAllElements();
				versionModel.removeAllElements();
			}
		}
		queueModelLocked = false;
		if (siteModel.getSize() > 0) {
			fillQueueCombobox();
		}

	}

	private void fillQueueCombobox() {

		if (!queueModelLocked) {
			SubmissionLocation oldSubLoc = (SubmissionLocation) queueModel
					.getSelectedItem();
			queueModel.removeAllElements();
			versionModelLocked = true;
			String currentSite = (String) siteModel.getSelectedItem();
			for (SubmissionLocation loc : this.commonGenericModule
					.getAvailableQueuesForSite(currentSite)) {
				queueModel.addElement(loc);
			}
			if (queueModel.getIndexOf(oldSubLoc) >= 0) {
				queueModel.setSelectedItem(oldSubLoc);
			} else {
				if (queueModel.getSize() > 0) {
					queueCombobox.setSelectedIndex(0);
					this.commonGenericModule
							.setSubmissionLocation((SubmissionLocation) queueModel
									.getElementAt(0));
				} else {
					versionModel.removeAllElements();
				}
			}
			versionModelLocked = false;
			if (queueModel.getSize() > 0) {
				fillVersionCombobox();
			}
		}
	}

	private void fillVersionCombobox() {

		if (!this.commonGenericModule.usesMds()) {
			lockVersionPanel(true);
			return;
		} else {
			lockVersionPanel(false);
		}

		if (!versionModelLocked) {

			versionModelLocked = true;

			String oldVersion = (String) versionModel.getSelectedItem();

			versionModel.removeAllElements();
			Set<String> versions = this.commonGenericModule.getVersions();
			if (versions != null) {
				for (String version : versions) {
					versionModel.addElement(version);
				}

				if (versionModel.getIndexOf(oldVersion) >= 0) {
					versionModel.setSelectedItem(oldVersion);
				} else {
					if (versionModel.getSize() > 0)
						versionCombobox.setSelectedIndex(0);
				}
				if (versionCheckbox.isSelected()
						&& this.commonGenericModule.usesMds()) {
					getVersionCombobox().setEnabled(true);
				} else if (!versionCheckbox.isSelected()
						&& this.commonGenericModule.usesMds()) {
					getVersionCombobox().setEnabled(false);
					String value = (String) versionModel.getSelectedItem();
					versionModel.setSelectedItem("Auto-select: " + value);
				} else {
					getVersionCombobox().setEnabled(false);
				}
			}
			versionModelLocked = false;
			setApplicationVersion();
		}

	}

	private void lockVersionPanel(boolean lock) {

		getVersionCheckbox().setEnabled(!lock);
		getVersionCombobox().setEnabled(!lock);
		if (lock) {
			versionModel.removeAllElements();
			versionModel.addElement("Not available.");
		}

	}

	/**
	 * @return
	 */
	protected JLabel getLabel() {
		if (label == null) {
			label = new JLabel();
			label.setText("Site:");
		}
		return label;
	}

	/**
	 * @return
	 */
	protected JComboBox getSiteCombobox() {
		if (siteCombobox == null) {
			siteCombobox = new JComboBox(siteModel);
			siteCombobox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent arg0) {
					fillQueueCombobox();
				}
			});
		}
		return siteCombobox;
	}

	/**
	 * @return
	 */
	protected JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel();
			label_1.setText("Queue");
		}
		return label_1;
	}

	/**
	 * @return
	 */
	protected JComboBox getQueueCombobox() {
		if (queueCombobox == null) {
			queueCombobox = new JComboBox(queueModel);
			queueCombobox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					commonGenericModule.setSubmissionLocation((SubmissionLocation)queueModel.getSelectedItem());
					fillVersionCombobox();
				}
			});
		}
		return queueCombobox;
	}

	/**
	 * @return
	 */
	protected JCheckBox getAllQueuesCheckbox() {
		if (allQueuesCheckbox == null) {
			allQueuesCheckbox = new JCheckBox();
			allQueuesCheckbox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent arg0) {
					boolean checked = allQueuesCheckbox.isSelected();
					commonGenericModule.useMds(!checked);
					if ( checked ) {
						switchPanel(MODULE_PANEL);
					} else {
						switchPanel(VERSION_PANEL);
					}
					fillSiteComboBox();
				}
			});
			allQueuesCheckbox.setHorizontalTextPosition(SwingConstants.LEADING);
			allQueuesCheckbox.setText("Display all available Queues");
		}
		return allQueuesCheckbox;
	}

	/**
	 * @return
	 */
	protected JPanel getVersionPanel() {
		if (versionPanel == null) {
			versionPanel = new JPanel();
			versionPanel.setBorder(new TitledBorder(null, "Version", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			versionPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					new RowSpec("12dlu"),
					FormFactory.RELATED_GAP_ROWSPEC,
					new RowSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC}));
			versionPanel.add(getVersionCheckbox(), new CellConstraints(2, 1,
					CellConstraints.RIGHT, CellConstraints.TOP));
			versionPanel.add(getVersionCombobox(), new CellConstraints(2, 3,
					CellConstraints.FILL, CellConstraints.BOTTOM));
		}
		return versionPanel;
	}

	/**
	 * @return
	 */
	protected JCheckBox getVersionCheckbox() {
		if (versionCheckbox == null) {
			versionCheckbox = new JCheckBox();
			versionCheckbox.setHorizontalTextPosition(SwingConstants.LEADING);
			versionCheckbox.setText("Select version");
			versionCheckbox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent arg0) {
					fillVersionCombobox();
				}
			});
		}
		return versionCheckbox;
	}

	private void setApplicationVersion() {
		if (!versionModelLocked) {
			if (this.commonGenericModule.usesMds()) {
				String version = (String) versionModel.getSelectedItem();
				try {
					this.commonGenericModule.setVersion(version);
				} catch (JobCreationException e) {
					// thats most likely ok
					myLogger.debug("Couldn't set version: "+version);
				}
			}
		}
	}

	/**
	 * @return
	 */
	protected JComboBox getVersionCombobox() {
		if (versionCombobox == null) {
			versionCombobox = new JComboBox(versionModel);
			versionCombobox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent arg0) {
					if (!versionModelLocked)
						setApplicationVersion();
				}
			});
		}
		return versionCombobox;
	}

	public void fqansChanged(FqanEvent event) {

		fillSiteComboBox();
	}

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {
		fillSiteComboBox();
	}
	/**
	 * @return
	 */
	protected JPanel getSwitchPanel() {
		if (switchPanel == null) {
			switchPanel = new JPanel();
			switchPanel.setLayout(new CardLayout());
			switchPanel.add(getModulePanel(), MODULE_PANEL);
			switchPanel.add(getVersionPanel(),VERSION_PANEL);

		}
		return switchPanel;
	}
	/**
	 * @return
	 */
	protected JPanel getModulePanel() {
		if (modulePanel == null) {
			modulePanel = new JPanel();
			modulePanel.setBorder(new TitledBorder(null, "Module", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			modulePanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("63dlu:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					RowSpec.decode("8dlu"),
					RowSpec.decode("4dlu"),
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC}));
			modulePanel.setName("panel");
			modulePanel.add(getTextField(), new CellConstraints(2, 3, CellConstraints.FILL, CellConstraints.BOTTOM));
			modulePanel.add(getPleaseSpecifyTheLabel(), new CellConstraints(2, 1, CellConstraints.RIGHT, CellConstraints.BOTTOM));
		}
		return modulePanel;
	}
	/**
	 * @return
	 */
	protected JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setPreferredSize(new Dimension(0, 24));
			textField.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if ( ! commonGenericModule.usesMds() ) {
						commonGenericModule.setModule(getTextField().getText());
						myLogger.debug("Set module to: "+getTextField().getText());
					}
				}
			});

		}
		return textField;
	}
	
	private void switchPanel(String panelName) {
		CardLayout cl = (CardLayout) (getSwitchPanel().getLayout());
		cl.show(getSwitchPanel(), panelName);
	}
	/**
	 * @return
	 */
	protected JLabel getPleaseSpecifyTheLabel() {
		if (pleaseSpecifyTheLabel == null) {
			pleaseSpecifyTheLabel = new JLabel();
			pleaseSpecifyTheLabel.setText("Please specify the module to load");
		}
		return pleaseSpecifyTheLabel;
	}

}
