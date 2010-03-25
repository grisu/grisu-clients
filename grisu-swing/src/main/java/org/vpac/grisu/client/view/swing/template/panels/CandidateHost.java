package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.template.panels.helperPanels.GridResourceInfoPanel;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.GridResourceHelpers;
import au.org.arcs.jcommons.utils.RankedSite;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class CandidateHost extends JPanel implements TemplateNodePanel,
ValueListener, FqanListener {

	static final Logger myLogger = Logger.getLogger(CandidateHost.class
			.getName());

	private String currentSubmissionLocation;

	private String currentVersion = null;
	private String currentCpus = null;
	private String currentMemory = null;
	private String currentWalltime = null;
	private String currentFqan;
	private String applicationName;

	private GrisuRegistry registry;

	private ServiceInterface serviceInterface;
	private TemplateNode templateNode;
	private JLabel lblSite;
	private JLabel lblQueue;
	private JComboBox queueComboBox;
	private JComboBox siteComboBox;
	private SortedSet<RankedSite> currentRankedSites;

	private RankedSite currentRankedSite;
	private boolean lockQueueCombo = false;

	private final DefaultComboBoxModel siteModel = new DefaultComboBoxModel();

	private final DefaultComboBoxModel queueModel = new DefaultComboBoxModel();
	private GridResourceInfoPanel gridResourceInfoPanel;
	private JCheckBox checkBox;
	public CandidateHost() {
		setBorder(new TitledBorder(null, "Submission location",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("134dlu:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(95dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getCheckBox(), "2, 1");
		add(getLblSite(), "2, 3");
		add(getGridResourceInfoPanel(), "4, 1, 1, 9, fill, fill");
		add(getSiteComboBox(), "2, 5, fill, default");
		add(getLblQueue(), "2, 7");
		add(getQueueComboBox(), "2, 9, fill, top");
		
		enableManualSelect(false);
	}

	public void addValueListener(ValueListener v) {
	}

	private void calculateAvailableGridResources() {

		siteModel.removeAllElements();

		Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
		jobProperties.put(JobSubmissionProperty.APPLICATIONNAME,
				applicationName);
		jobProperties.put(JobSubmissionProperty.APPLICATIONVERSION,
				currentVersion);
		jobProperties.put(JobSubmissionProperty.MEMORY_IN_B, currentMemory);
		jobProperties.put(JobSubmissionProperty.NO_CPUS, currentCpus);
		jobProperties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES,
				currentWalltime);

		myLogger.debug("Trying to find best resources for jobproperties: "
				+ jobProperties.toString());

		SortedSet<GridResource> resources = registry
		.getUserApplicationInformation(applicationName)
		.getAllSubmissionLocationsAsGridResources(jobProperties,
				currentFqan);

		currentRankedSites = GridResourceHelpers.asSetOfRankedSites(resources);

		for (RankedSite site : currentRankedSites) {
			siteModel.addElement(site);
		}

	}

	private void enableManualSelect(boolean enable) {

		getSiteComboBox().setEnabled(enable);
		getQueueComboBox().setEnabled(enable);

	}

	private void fillQueueCombobox() {

		if (siteModel.getSize() > 0) {

			currentRankedSite = (RankedSite) siteComboBox.getSelectedItem();

			if (currentRankedSite == null) {
				return;
			}

			queueModel.removeAllElements();

			for (GridResource resource : currentRankedSite.getResources()) {
			    queueModel.addElement(resource);
			}

		} else {
			currentRankedSite = null;
			currentSubmissionLocation = null;
			queueModel.removeAllElements();
			getGridResourceInfoPanel().setGridResource(null);
		}

	}


	public void fqansChanged(FqanEvent event) {
		this.currentFqan = event.getFqan();
		calculateAvailableGridResources();
	}

	private JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox("Manually select submission location");
			checkBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					enableManualSelect(checkBox.isSelected());

				}
			});
		}
		return checkBox;
	}

	public String getExternalSetValue() {
		return currentSubmissionLocation;
	}

	private GridResourceInfoPanel getGridResourceInfoPanel() {
		if (gridResourceInfoPanel == null) {
			gridResourceInfoPanel = new GridResourceInfoPanel();
		}
		return gridResourceInfoPanel;
	}

	private JLabel getLblQueue() {
		if (lblQueue == null) {
			lblQueue = new JLabel("Queue:");
		}
		return lblQueue;
	}

	private JLabel getLblSite() {
		if (lblSite == null) {
			lblSite = new JLabel("Site:");
		}
		return lblSite;
	}

	private JComboBox getQueueComboBox() {
		if (queueComboBox == null) {
			queueComboBox = new JComboBox(queueModel);
			queueComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {

					try {

						if (!lockQueueCombo) {
							if (queueModel.getSize() > 0) {
								Object o = queueModel.getSelectedItem();
								if (o == null) {
									return;
								}
								try {
									setCurrentSubmissionLocation((GridResource) o);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return queueComboBox;
	}

	private JComboBox getSiteComboBox() {
		if (siteComboBox == null) {
			siteComboBox = new JComboBox(siteModel);
			siteComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					try {
						lockQueueCombo = true;
						fillQueueCombobox();
						lockQueueCombo = false;
						if (currentRankedSite != null) {
							Object o = queueModel.getSelectedItem();
							setCurrentSubmissionLocation((GridResource) o);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return siteComboBox;
	}

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void removeValueListener(ValueListener v) {
	}

	public void reset() {
	}

	private void setCurrentSubmissionLocation(GridResource r) {
		currentSubmissionLocation = SubmissionLocationHelpers
		.createSubmissionLocationString(r);
		getGridResourceInfoPanel().setGridResource(r);
	}

	public void setExternalSetValue(String value) {
		myLogger.warn("Not supported yet.");
		throw new RuntimeException("Setting value not supported yet.");
	}

	public void setTemplateNode(TemplateNode node)
	throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);

		this.applicationName = this.templateNode.getTemplate()
		.getApplicationName();

		serviceInterface = node.getTemplate().getEnvironmentManager()
		.getServiceInterface();
		registry = GrisuRegistryManager.getDefault(serviceInterface);

		registry.getUserEnvironmentManager().addFqanListener(this);

		this.currentFqan = registry.getUserEnvironmentManager()
		.getCurrentFqan();
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
	}
	public void valueChanged(TemplateNodePanel panel, String newValue) {

		myLogger.debug("New value from " + panel.getClass().toString() + ": "
				+ newValue);

		if (panel instanceof ApplicationVersion) {
			this.currentVersion = newValue;
		} else if (panel instanceof CPUs) {
			this.currentCpus = newValue;
		} else if (panel instanceof MemoryInputPanel) {
			this.currentMemory = newValue;
		} else if (panel instanceof WallTime) {
			this.currentWalltime = new Integer(Integer.parseInt(newValue) / 60)
			.toString();
		}

		calculateAvailableGridResources();

	}
}
