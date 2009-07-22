package org.vpac.grisu.client.view.swing.template.modules.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.model.ApplicationInfoObject;
import org.vpac.grisu.client.model.ModeNotSupportedException;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.template.modules.SubmissionObjectHolder;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.control.exceptions.JobCreationException;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class VersionQueuePanel_NotSoGood extends JPanel implements ActionListener, SubmissionObjectHolder {
	
	static final Logger myLogger = Logger.getLogger(VersionQueuePanel_NotSoGood.class
			.getName());
	
	
	
	public static final String ANY_MODE_STRING = "Any";
	public static final String DEFAULT_MODE_STRING = "Default";
	public static final String EXACT_MODE_STRING = "Exact";
	
	public static final String ANY_MODE_TEMPLATETAG_KEY = "useAny";
	public static final String DEFAULT_MODE_TEMPLATETAG_KEY = "useDefault";
	public static final String EXACT_MODE_TEMPLATETAG_KEY = "useExact";
	
	public static final String STARTUP_MODE_KEY = "startMode";

	private JComboBox queueComboBox;
	private JComboBox siteComboBox;
	private JLabel queueLabel;
	private JLabel siteLabel;
	private JPanel subLocPanel;
	private JComboBox versionComboBox;
	private JRadioButton exactRadioButton;
	private JRadioButton anyRadioButton;
	private JRadioButton defaultRadioButton;
	private JPanel versionPanel;
	
	private ApplicationInfoObject infoObject = null;
	
	private EnvironmentManager em = null;
	private final String application;
	private List<String> versions = null;
	
	private DefaultComboBoxModel versionModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel siteModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();
	
	private TemplateNode version = null;
	private TemplateNode hostname = null;
	private TemplateNode executionFileSystem = null;
	
	
	private boolean anyModeSupported = true;
	private boolean defaultModeSupported = true;
	private boolean exactModeSupported = true;
	private String exactModeVersion = null;
	
	private int currentMode = -1;

	private ButtonGroup modeGroup = new ButtonGroup();
	
	private TemplateNode versionNode = null;
	private TemplateNode hostnameNode = null;
	
	private boolean ignoreVersionComboboxItemChanged = true;
	private boolean ignoreQueueChanges = false;
	
	/**
	 * Create the panel
	 */
	public VersionQueuePanel_NotSoGood(TemplateModule templateModule) {
		
		super();
		try {
			this.addSubmissionObjectListener((SubmissionObjectListener)templateModule);
		} catch (Exception e) {
			throw new RuntimeException("Can't create VersionQueuePanel because the templateModule this is connected to ("+templateModule.getModuleName()+") is not implementing the SubmissionObjectListener interface.");
		}
		this.em = templateModule.getTemplate().getEnvironmentManager();
		
		this.version = templateModule.getTemplateNodes().get("Application");
		this.hostname = templateModule.getTemplateNodes().get("HostName");
		this.executionFileSystem = templateModule.getTemplateNodes().get("ExecutionFileSystem");
		
		
		this.versionNode = templateModule.getTemplateNodes().get(org.vpac.grisu.client.model.template.modules.GenericMDS.VERSION_TEMPLATE_TAG_NAME);
		this.hostnameNode = templateModule.getTemplateNodes().get(org.vpac.grisu.client.model.template.modules.GenericMDS.HOSTNAME_TEMPLATE_TAG_NAME);
		this.application = templateModule.getTemplate().getApplicationName();
		
		this.anyModeSupported = versionNode.getOtherProperties().containsKey(ANY_MODE_TEMPLATETAG_KEY);
		this.defaultModeSupported = versionNode.getOtherProperties().containsKey(DEFAULT_MODE_TEMPLATETAG_KEY);
		this.exactModeSupported = versionNode.getOtherProperties().containsKey(EXACT_MODE_TEMPLATETAG_KEY);
		
		// set startup mode
		String startUpMode = versionNode.getOtherProperties().get(STARTUP_MODE_KEY);
		if ( ANY_MODE_STRING.equals(startUpMode) ) {
			this.currentMode = ApplicationInfoObject.ANY_VERSION_MODE;
			this.anyModeSupported = true;
		} else if ( DEFAULT_MODE_STRING.equals(startUpMode) ) {
			this.currentMode = ApplicationInfoObject.DEFAULT_VERSION_MODE;
			this.defaultModeSupported = true;
		} else if ( EXACT_MODE_STRING.equals(startUpMode) ) {
			this.currentMode = ApplicationInfoObject.EXACT_VERSION_MODE;
			this.exactModeSupported = true;
		} else {
			// default mode
			if ( anyModeSupported ) {
				this.currentMode = ApplicationInfoObject.ANY_VERSION_MODE;
			} else if ( defaultModeSupported ) {
				this.currentMode = ApplicationInfoObject.DEFAULT_VERSION_MODE;
			} else if ( exactModeSupported ) {
				this.currentMode = ApplicationInfoObject.EXACT_VERSION_MODE;
			} else {
				this.currentMode = ApplicationInfoObject.ANY_VERSION_MODE;
				anyModeSupported = true;
			}
		}
				
		
		try {
			this.exactModeVersion = versionNode.getOtherProperties().get(EXACT_MODE_TEMPLATETAG_KEY);

			if ( "NON_VALUE".equals(this.exactModeVersion) ) {
				this.exactModeVersion = null;
			}
		} catch (Exception e) {
			myLogger.debug("No exact mode locked version specified...");
		}
		
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("19dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("58dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("33dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("29dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		
		add(getVersionPanel(), new CellConstraints(2, 2, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getSubLocPanel(), new CellConstraints(2, 4, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		//
		this.infoObject = new ApplicationInfoObject(em, application, this.currentMode);
		
		for ( String version : em.getAllAvailableVersionsForApplication(application, em.getDefaultFqan()) ) {
			versionModel.addElement(version);
		}
		
		if ( this.exactModeVersion != null && em.getAllAvailableVersionsForApplication(application, em.getDefaultFqan()).contains(this.exactModeVersion) ) {
			versionModel.setSelectedItem(this.exactModeVersion);
		}

		this.exactModeVersion = infoObject.getCurrentVersion();
		ignoreVersionComboboxItemChanged = false;
		
		switch (currentMode) {
		case ApplicationInfoObject.ANY_VERSION_MODE:
			getAnyRadioButton().doClick();
			break;
		case ApplicationInfoObject.DEFAULT_VERSION_MODE:
			getDefaultRadioButton().doClick();
			break;
		case ApplicationInfoObject.EXACT_VERSION_MODE:
			getExactRadioButton().doClick();
			break;
		}

					
	}
	/**
	 * @return
	 */
	protected JPanel getVersionPanel() {
		if (versionPanel == null) {
			versionPanel = new JPanel();
			versionPanel.setLayout(new BoxLayout(versionPanel, BoxLayout.X_AXIS));
			versionPanel.setFocusCycleRoot(true);
			versionPanel.setBorder(new TitledBorder(null, "Version", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			if ( anyModeSupported ) {
				versionPanel.add(getAnyRadioButton());
				modeGroup.add(getAnyRadioButton());
				if ( this.currentMode == -1 ) {
					this.currentMode = ApplicationInfoObject.ANY_VERSION_MODE;
				}
			}
			if ( defaultModeSupported ) {
				versionPanel.add(getDefaultRadioButton());
				modeGroup.add(getDefaultRadioButton());
				if ( this.currentMode == -1 ) {
					this.currentMode = ApplicationInfoObject.DEFAULT_VERSION_MODE;
				}
			}
			if ( exactModeSupported ) {
				versionPanel.add(getExactRadioButton());
				modeGroup.add(getExactRadioButton());
				if ( this.currentMode == -1 ) {
					this.currentMode = ApplicationInfoObject.EXACT_VERSION_MODE;
				}
			}
			versionPanel.add(getVersionComboBox());
		}
		return versionPanel;
	}
	/**
	 * @return
	 */
	protected JRadioButton getDefaultRadioButton() {
		if (defaultRadioButton == null) {
			defaultRadioButton = new JRadioButton();
			defaultRadioButton.setText(DEFAULT_MODE_STRING);
			defaultRadioButton.addActionListener(this);
			defaultRadioButton.setActionCommand(DEFAULT_MODE_STRING);
		}
		return defaultRadioButton;
	}
	/**
	 * @return
	 */
	protected JRadioButton getAnyRadioButton() {
		if (anyRadioButton == null) {
			anyRadioButton = new JRadioButton();
			anyRadioButton.setText(ANY_MODE_STRING);
			anyRadioButton.addActionListener(this);
			anyRadioButton.setActionCommand(ANY_MODE_STRING);
		}
		return anyRadioButton;
	}
	/**
	 * @return
	 */
	protected JRadioButton getExactRadioButton() {
		if (exactRadioButton == null) {
			exactRadioButton = new JRadioButton();
			exactRadioButton.setText(EXACT_MODE_STRING);
			exactRadioButton.addActionListener(this);
			exactRadioButton.setActionCommand(EXACT_MODE_STRING);
		}
		return exactRadioButton;
	}
	/**
	 * @return
	 */
	protected JComboBox getVersionComboBox() {
		if (versionComboBox == null) {
			versionComboBox = new JComboBox(versionModel);
			versionComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					recalculateVersionAndSubsequentSubmissionLocationDefaults();
				}
			});
		}
		return versionComboBox;
	}
	
	private void recalculateVersionAndSubsequentSubmissionLocationDefaults() {
		if ( ! ignoreVersionComboboxItemChanged ) {
			
			infoObject.setVersion((String)versionModel.getSelectedItem());
			if ( versionModel.getSize() > 0 ) {
				fillSiteCombobox();
			}
		}
		
		
	}
	
	/**
	 * @return
	 */
	protected JPanel getSubLocPanel() {
		if (subLocPanel == null) {
			subLocPanel = new JPanel();
			subLocPanel.setLayout(new FormLayout(
				new ColumnSpec[] {
					ColumnSpec.decode("37dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("33dlu:grow(1.0)")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC}));
			subLocPanel.setBorder(new TitledBorder(null, "Submission location", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			subLocPanel.add(getSiteLabel(), new CellConstraints(1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			subLocPanel.add(getQueueLabel(), new CellConstraints(1, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			subLocPanel.add(getSiteComboBox(), new CellConstraints(3, 1));
			subLocPanel.add(getQueueComboBox(), new CellConstraints(3, 3));
		}
		return subLocPanel;
	}
	/**
	 * @return
	 */
	protected JLabel getSiteLabel() {
		if (siteLabel == null) {
			siteLabel = new JLabel();
			siteLabel.setText("Site:");
		}
		return siteLabel;
	}
	/**
	 * @return
	 */
	protected JLabel getQueueLabel() {
		if (queueLabel == null) {
			queueLabel = new JLabel();
			queueLabel.setText("Queue:");
		}
		return queueLabel;
	}
	/**
	 * @return
	 */
	protected JComboBox getSiteComboBox() {
		if (siteComboBox == null) {
			siteComboBox = new JComboBox(siteModel);
			siteComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {

					changeSelectionOfQueues(((String)(siteModel.getSelectedItem())));
				}
			});
		}
		return siteComboBox;
	}
	
	private void changeSelectionOfQueues(String site) {

		if ( site != null && !"".equals(site) ) {
		SubmissionLocation oldSubLoc = ((SubmissionLocation)(queueModel.getSelectedItem()));
				
		ignoreQueueChanges = true;
		queueModel.removeAllElements();
		for ( SubmissionLocation subLoc : infoObject.getCurrentlyPossibleSubmissionLocationsForSite(site) ) {
				queueModel.addElement(subLoc);
		}
		
				if ( oldSubLoc != null && queueModel.getIndexOf(oldSubLoc) >= 0 ) {
			queueModel.setSelectedItem(oldSubLoc);
		} else if ( queueModel.getSize() > 0 ) {
			queueModel.setSelectedItem(queueComboBox.getItemAt(0));
		} else {
			myLogger.warn("No queues available...");
		}
		ignoreQueueChanges = false;
		recalculateApplicationInfoObject();
		}
		
		
	}
	
	
	/**
	 * @return
	 */
	protected JComboBox getQueueComboBox() {
		if (queueComboBox == null) {
			queueComboBox = new JComboBox(queueModel);
			queueComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					recalculateApplicationInfoObject();
				}
			});
		}
		return queueComboBox;
	}
	
	private void recalculateApplicationInfoObject() {
		if ( ! ignoreQueueChanges ) {
			SubmissionLocation tempLoc = (SubmissionLocation)(queueModel.getSelectedItem());
			if ( tempLoc != null ) {
			try {
				// check any mode, if, then set the currect version first
				if ( currentMode == ApplicationInfoObject.ANY_VERSION_MODE ) {
					String tempVersion = infoObject.getRecommendedVersionForSubmissionLocation(tempLoc, em.getDefaultFqan());
					infoObject.setVersion(tempVersion);
				}
				
				infoObject.setCurrentSubmissionLocation(tempLoc);
			} catch (SubmissionLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// if mode = any then change the version to the recommended one
			if ( currentMode == ApplicationInfoObject.ANY_VERSION_MODE && tempLoc != null ) {
				ignoreVersionComboboxItemChanged = true;
//				String temp = infoObject.getRecommendedVersionForSubmissionLocation(tempLoc, em.getDefaultFqan());
//				if ( temp == null || "".equals(temp) ) {
//					temp = "n/a";
//				}
				String temp = infoObject.getCurrentVersion();
				versionModel.setSelectedItem(temp);
				ignoreVersionComboboxItemChanged = false;					
			}

			// TODO add exact version here
			fireNewValidSubmissionObjectEvent(infoObject);
			}
			}
	}
	
	private void switchMode(String mode) {
		
		if ( ANY_MODE_STRING.equals(mode) ) {
			switchMode(ApplicationInfoObject.ANY_VERSION_MODE);
			return;
		} else if ( DEFAULT_MODE_STRING.equals(mode) ) {
			switchMode(ApplicationInfoObject.DEFAULT_VERSION_MODE);
			return;
		} else if ( EXACT_MODE_STRING.equals(mode) ) {
			switchMode(ApplicationInfoObject.EXACT_VERSION_MODE);
			return;
		}
		
		myLogger.error("Mode not supported: "+mode);
	}
	
	private void switchMode(int mode) {
		try {

			switch (mode) {
			case ApplicationInfoObject.ANY_VERSION_MODE:
				switchToAnyVersionMode(); break;
			case ApplicationInfoObject.DEFAULT_VERSION_MODE:
				switchToDefaultVersionMode(); break;
			case ApplicationInfoObject.EXACT_VERSION_MODE:
				switchToExactVersionMode(); break;
				default:
					myLogger.error("Can't switch to mode: "+mode+". Not supported.");
			}
			
			fillSiteCombobox();
				
		} catch (ModeNotSupportedException e) {
			myLogger.error("Can't switch to mode: "+mode+". Not supported.");
		}
	}
	
	private void fillSiteCombobox() {
		
		if ( infoObject != null && infoObject.getCurrentlyPossibleSubmissionLocations() != null ) {
			
			SubmissionLocation oldSubLoc = ((SubmissionLocation)(queueModel.getSelectedItem()));
			
			siteModel.removeAllElements();
			
			if ( infoObject.getCurrentlyPossibleSites().size() == 0 ) {
				getSiteComboBox().setEnabled(false);
				getQueueComboBox().setEnabled(false);
			} else {
				getSiteComboBox().setEnabled(true);
				getQueueComboBox().setEnabled(true);
			}
			
			for ( String site : infoObject.getCurrentlyPossibleSites() ) {
			
				siteModel.addElement(site);
			
			}
			
			if ( oldSubLoc != null ) {
				if ( infoObject.getCurrentlyPossibleSites().contains(oldSubLoc.getSite()) ) {
					siteModel.setSelectedItem(oldSubLoc.getSite());
				}
			
				if ( queueModel.getIndexOf(oldSubLoc) >= 0 ) {
					queueModel.setSelectedItem(oldSubLoc);
				}
			}
		}
	}
	
	private void switchToAnyVersionMode() throws ModeNotSupportedException {
		
		String lastVersion = (String)(versionModel.getSelectedItem());
		SubmissionLocation tempSubmissionLocation = ((SubmissionLocation)queueModel.getSelectedItem());
		siteModel.removeAllElements();
		queueModel.removeAllElements();
		getVersionComboBox().setEnabled(false);
		infoObject.setMode(ApplicationInfoObject.ANY_VERSION_MODE, null);
		this.currentMode = ApplicationInfoObject.ANY_VERSION_MODE;
		ignoreVersionComboboxItemChanged = true;
		Set<String> allVersions = em.getAllAvailableVersionsForApplication(application, em.getDefaultFqan());
		for ( String version : allVersions ) {
			versionModel.addElement(version);
		}
		
		if ( allVersions.contains(lastVersion) ) {
			versionModel.setSelectedItem(lastVersion);
		} else {
			if ( versionModel.getSize() > 0 ) {
				versionModel.setSelectedItem(versionModel.getElementAt(0));
			}
		}

		ignoreVersionComboboxItemChanged = false;
		// recalculate sites
		recalculateVersionAndSubsequentSubmissionLocationDefaults();
		
		if ( tempSubmissionLocation != null ) {
				
				if ( siteModel.getIndexOf(tempSubmissionLocation.getSite()) >= 0 ) {
					siteModel.setSelectedItem(tempSubmissionLocation.getSite());
				}
				
				if ( queueModel.getIndexOf(tempSubmissionLocation) >= 0 ) {
					queueModel.setSelectedItem(tempSubmissionLocation);
				}
		}
		
	}
	
	private void switchToDefaultVersionMode() throws ModeNotSupportedException {

		siteModel.removeAllElements();
		queueModel.removeAllElements();
		infoObject.setMode(ApplicationInfoObject.DEFAULT_VERSION_MODE, null);
		this.currentMode = ApplicationInfoObject.DEFAULT_VERSION_MODE;
	}
	
	private void switchToExactVersionMode() throws ModeNotSupportedException {

		infoObject.setMode(ApplicationInfoObject.EXACT_VERSION_MODE, ((String)(versionModel.getSelectedItem())));
		if ( exactModeVersion == null || "".equals(exactModeVersion) ) {
			siteModel.removeAllElements();
			queueModel.removeAllElements();
			versionComboBox.setEnabled(true);
			ignoreVersionComboboxItemChanged = true;
			versionModel.removeAllElements();
			ignoreVersionComboboxItemChanged = false;
		} else {
			ignoreVersionComboboxItemChanged = true;
			getVersionComboBox().setEnabled(false);
			// if the specified version in the template is not available
			if ( ! infoObject.getAllAvailableVersions().contains(exactModeVersion) ) {
				versionModel.removeAllElements();
				versionModel.addElement("Version "+exactModeVersion+" not available.");
				ignoreVersionComboboxItemChanged = false;
			} else {
				ignoreVersionComboboxItemChanged = false;
				versionModel.setSelectedItem(exactModeVersion);
			}

		}
		this.currentMode = ApplicationInfoObject.EXACT_VERSION_MODE;
	}
	
	

	public void actionPerformed(ActionEvent e) {

		switchMode(e.getActionCommand());
		
		
	}
	
	
	// event stuff

	private Vector<SubmissionObjectListener> submissionObjectListeners;
	
	
	private void fireNewValidSubmissionObjectEvent(SubmissionObject newSO) {

		myLogger.debug("Fire submissionPanel event: new site("
				+ newSO.getCurrentSubmissionLocation().getSite() + "), ("
				+ newSO.getCurrentSubmissionLocation().getLocation() + ")"
				+ "\n"+newSO.getCurrentApplicationName()+" (Version: "+newSO.getCurrentVersion()+").");
		// if we have no mountPointsListeners, do nothing...
		if (submissionObjectListeners != null
				&& !submissionObjectListeners.isEmpty()) {
			// create the event object to send

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<SubmissionObjectListener> sitePanelTargets;
			synchronized (this) {
				sitePanelTargets = (Vector<SubmissionObjectListener>) submissionObjectListeners
						.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration<SubmissionObjectListener> e = sitePanelTargets
					.elements();
			while (e.hasMoreElements()) {
				SubmissionObjectListener l = e.nextElement();
				l.submissionObjectChanged(newSO);
			}
		}
	}
	
	public void addSubmissionObjectListener(SubmissionObjectListener l) {
		if (submissionObjectListeners == null)
			submissionObjectListeners = new Vector();
		submissionObjectListeners.addElement(l);
	}
	public SubmissionObject getCurrentSubmissionObject()
			throws JobCreationException {
		// TODO Auto-generated method stub
		return null;
	}
	public void removeSubmissionObjectListener(SubmissionObjectListener l) {
		if (submissionObjectListeners == null) {
			submissionObjectListeners = new Vector<SubmissionObjectListener>();
		}
		submissionObjectListeners.removeElement(l);
	}
	
	public void reset() {

	}

}
