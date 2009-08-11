package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.utils.QueueRenderer;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.info.UserApplicationInformation;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SubmissionLocation extends JPanel implements TemplateNodePanel,
		ValueListener, FqanListener {

	private JComboBox queueComboBox;
	private JComboBox siteComboBox;
	private JLabel label_1;
	private JLabel label;
	static final Logger myLogger = Logger.getLogger(SubmissionLocation.class
			.getName());

	private DefaultComboBoxModel siteModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();

	private TemplateNode templateNode;
	private String applicationName;

	private UserApplicationInformation infoObject = null;
	private Version versionPanel = null;

	Set<String> allSites = null;
	Set<String> allQueues = null;

//	private final ResourceInformation resourceInfo = GrisuRegistry.getDefault()
//			.getResourceInformation();
//	private UserProperties esv = GrisuRegistry.getDefault()
//			.getUserProperties();
//	private final UserInformation userInformation = GrisuRegistry.getDefault()
//			.getUserInformation();
	
	private GrisuRegistry registry;

	private String lastSubmissionLocation = null;

	private ExecutionFileSystem executionFileSystemPanel = null;

	private String currentStagingFilesystem = null;

	private Map<String, String> tempJobProperties = new HashMap<String, String>();

	/**
	 * Create the panel
	 */
	public SubmissionLocation() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("36dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		setBorder(new TitledBorder(null, "Submission location",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		add(getLabel(), new CellConstraints(2, 2, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getLabel_1(), new CellConstraints(2, 4, CellConstraints.RIGHT,
				CellConstraints.DEFAULT));
		add(getSiteComboBox(), new CellConstraints(4, 2));
		add(getQueueComboBox(), new CellConstraints(4, 4));
		//
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		
		registry = GrisuRegistryManager.getDefault(node.getTemplate().getEnvironmentManager().getServiceInterface());

		registry.getUserEnvironmentManager().addFqanListener(this);

		this.applicationName = this.templateNode.getTemplate()
				.getApplicationName();
		this.infoObject = registry.getUserApplicationInformation(applicationName);

		try {
			registry.getHistoryManager()
					.setMaxNumberOfEntries(
							TemplateTagConstants
									.getGlobalLastQueueKey(infoObject
											.getApplicationName()), 1);
			lastSubmissionLocation = registry
					.getHistoryManager().getEntries(
							TemplateTagConstants
									.getGlobalLastQueueKey(infoObject
											.getApplicationName())).get(0);
		} catch (Exception e) {
			lastSubmissionLocation = null;
		}

		// this might be slightly dodgy. But it should always work if a Version
		// template tag is present.
		if (getVersionPanel() != null
				&& getVersionPanel().getCurrentValue() != null) {
			valueChanged(getVersionPanel(), getVersionPanel().getCurrentValue());
		}

		if (lastSubmissionLocation != null) {
			String lastSite = registry
					.getResourceInformation().getSite(lastSubmissionLocation);
			if (siteModel.getIndexOf(lastSite) >= 0) {
				siteModel.setSelectedItem(lastSite);
				if (queueModel.getIndexOf(lastSubmissionLocation) >= 0) {
					queueModel.setSelectedItem(lastSubmissionLocation);
				}
			}
		}

	}

	private ExecutionFileSystem getExecutionFileSystemPanel() {

		if (executionFileSystemPanel == null) {
			try {
				// try to find a templateNodevalueSetter that is a
				// SubmissionLocationPanel
				for (TemplateNode node : this.templateNode.getTemplate()
						.getTemplateNodes().values()) {
					if (node.getTemplateNodeValueSetter() instanceof ExecutionFileSystem) {
						executionFileSystemPanel = (ExecutionFileSystem) node
								.getTemplateNodeValueSetter();
						setStagingFS(getExternalSetValue());
						break;
					}

				}
			} catch (Exception e) {
				myLogger
						.warn("Couldn't retrieve executionFileSystemPanel yet...");
				executionFileSystemPanel = null;
				return null;
			}

		}
		return executionFileSystemPanel;
	}

	private Version getVersionPanel() {

		if (versionPanel == null) {
			try {
				// try to find a templateNodevalueSetter that is a Version panel
				for (TemplateNode node : this.templateNode.getTemplate()
						.getTemplateNodes().values()) {
					if (node.getTemplateNodeValueSetter() instanceof Version) {
						versionPanel = (Version) node
								.getTemplateNodeValueSetter();
						break;
					}

				}
			} catch (Exception e) {
				myLogger.warn("Couldn't initialize version panel yet...");
				versionPanel = null;
				return null;
			}
			if (versionPanel != null) {
				versionPanel.addValueListener(this);
				// remove version panel just in case it's already there...
				removeValueListener(versionPanel);
				addValueListener(versionPanel);
			}
		}
		return versionPanel;
	}

	public void valueChanged(TemplateNodePanel panel, String newValue) {
		// version changed...
		myLogger.debug("SubmissionLocationPanel: Version changed to: "
				+ newValue);

		if (infoObject != null) {

			String oldSite = (String) siteModel.getSelectedItem();
			siteModel.removeAllElements();

			String oldQueue = (String) queueModel.getSelectedItem();

			if (getVersionPanel() != null
					&& getVersionPanel().getMode() == Version.DEFAULT_VERSION_MODE) {
				allQueues = infoObject
						.getAvailableSubmissionLocationsForVersionAndFqan(
								newValue, registry.getUserEnvironmentManager().getCurrentFqan());
				if (allQueues.size() == 0) {
					siteModel.setSelectedItem("Not available.");
					queueModel.setSelectedItem("Not available.");
					return;
				}
			} else if (getVersionPanel() != null
					&& getVersionPanel().getMode() == Version.ANY_VERSION_MODE) {
				allQueues = infoObject
						.getAvailableSubmissionLocationsForFqan(registry.getUserEnvironmentManager()
								.getCurrentFqan());
			} else {
				allQueues = infoObject
						.getAvailableSubmissionLocationsForVersionAndFqan(
								newValue, registry.getUserEnvironmentManager().getCurrentFqan());
			}

			allSites = registry.getResourceInformation()
					.distillSitesFromSubmissionLocations(allQueues);

			for (String tempsite : allSites) {
				siteModel.addElement(tempsite);
			}
			
			if (oldSite != null && siteModel.getIndexOf(oldSite) >= 0) {
				changeToSite(oldSite);
			}

			if (queueModel.getIndexOf(oldQueue) >= 0) {
				queueModel.setSelectedItem(oldQueue);
			}
		}

	}

	private void changeToSite(String site) {

		siteModel.setSelectedItem(site);

	}

	private void repopulateQueueCombobox() {

		String oldQueue = (String) queueModel.getSelectedItem();
		queueModel.removeAllElements();

		String newSite = (String) siteModel.getSelectedItem();
		if (newSite != null && !"".equals(newSite)) {
			for (String queue : registry.getResourceInformation().filterSubmissionLocationsForSite(
					newSite, allQueues)) {
				queueModel.addElement(queue);
			}

		}

	}

	public void templateNodeUpdated(TemplateNodeEvent event) {

	}

	public String getExternalSetValue() {
		return (String) queueModel.getSelectedItem();
	}

	public void setExternalSetValue(String value) {
		// TODO Auto-generated method stub

	}

	// event stuff
	// ========================================================

	private Vector<ValueListener> valueChangedListeners;

	private void fireSubmissionLocationChanged(String newValue) {

		myLogger
				.debug("Fire value changed event from SubmissionLocation: new value: "
						+ newValue);
		// if we have no mountPointsListeners, do nothing...
		if (valueChangedListeners != null && !valueChangedListeners.isEmpty()) {

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<ValueListener> valueChangedTargets;
			synchronized (this) {
				valueChangedTargets = (Vector<ValueListener>) valueChangedListeners
						.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration<ValueListener> e = valueChangedTargets.elements();
			while (e.hasMoreElements()) {
				ValueListener valueChanged_l = (ValueListener) e.nextElement();
				valueChanged_l.valueChanged(this, newValue);
			}
		}
	}

	// register a listener
	synchronized public void addValueListener(ValueListener l) {
		if (valueChangedListeners == null)
			valueChangedListeners = new Vector<ValueListener>();
		valueChangedListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeValueListener(ValueListener l) {
		if (valueChangedListeners == null) {
			valueChangedListeners = new Vector<ValueListener>();
		}
		valueChangedListeners.removeElement(l);
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
	protected JLabel getLabel_1() {
		if (label_1 == null) {
			label_1 = new JLabel();
			label_1.setText("Queue:");
		}
		return label_1;
	}

	/**
	 * @return
	 */
	protected JComboBox getSiteComboBox() {
		if (siteComboBox == null) {
			siteComboBox = new JComboBox(siteModel);
			siteComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						repopulateQueueCombobox();
					}

				}
			});
		}
		return siteComboBox;
	}

	private void setStagingFS(String submissionLocation) {

		MountPoint fs = registry.getUserEnvironmentManager().getRecommendedMountPoint(
				submissionLocation, registry.getUserEnvironmentManager().getCurrentFqan());
		if (getExecutionFileSystemPanel() != null) {
			getExecutionFileSystemPanel().setExternalSetValue(fs.getRootUrl());
		}

		currentStagingFilesystem = fs.getRootUrl();
		myLogger.debug("Set staging fs to: " + fs);

	}

	/**
	 * @return
	 */
	protected JComboBox getQueueComboBox() {
		if (queueComboBox == null) {
			queueComboBox = new JComboBox(queueModel);
			queueComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {

					if (e.getStateChange() == ItemEvent.SELECTED) {
						String temp = ((String) queueModel.getSelectedItem());
						if (temp != null && !"".equals(temp)
								&& !temp.startsWith("Not available.")) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								fireSubmissionLocationChanged(temp);
							}
							setStagingFS(temp);
							registry
									.getHistoryManager()
									.addHistoryEntry(
											TemplateTagConstants
													.getGlobalLastQueueKey(infoObject
															.getApplicationName()),
											(String) queueModel
													.getSelectedItem());
						}
					}
				}
			});
			queueComboBox.setRenderer(new QueueRenderer(queueComboBox
					.getRenderer()));
		}
		return queueComboBox;
	}

	public String getCurrentExecutionFileSystem() {
		return currentStagingFilesystem;
	}

	public void fqansChanged(FqanEvent event) {

		// not necessary, because Version will fire an event
		valueChanged(this, versionPanel.getCurrentValue());

	}


}
