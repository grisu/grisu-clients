package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;
import org.vpac.grisu.model.GrisuRegistry;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.GridResourceHelpers;
import au.org.arcs.jcommons.utils.RankedSite;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import org.vpac.grisu.client.view.swing.template.panels.helperPanels.GridResourceInfoPanel;

public class CandidateHost extends JPanel implements TemplateNodePanel,
		ValueListener, FqanListener {

	public CandidateHost() {
		setBorder(new TitledBorder(null, "Submission location",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("200dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(129dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getLblSite(), "2, 1");
		add(getGridResourceInfoPanel(), "4, 1, 1, 7, fill, fill");
		add(getSiteComboBox(), "2, 3, fill, default");
		add(getLblQueue(), "2, 5");
		add(getQueueComboBox(), "2, 7, fill, top");
	}

	static final Logger myLogger = Logger.getLogger(CandidateHost.class
			.getName());

	private String currentSubmissionLocation;
	private String currentVersion;
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

	private DefaultComboBoxModel siteModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel queueModel = new DefaultComboBoxModel();
	private GridResourceInfoPanel gridResourceInfoPanel;

	public JPanel getTemplateNodePanel() {
		return this;
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {

		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);

		this.applicationName = this.templateNode.getTemplate()
				.getApplicationName();

		serviceInterface = node.getTemplate().getEnvironmentManager()
				.getServiceInterface();
		registry = GrisuRegistry.getDefault(serviceInterface);
		
		registry.getUserEnvironmentManager().addFqanListener(this);

		this.currentFqan = registry.getUserEnvironmentManager()
				.getCurrentFqan();
	}

	public String getExternalSetValue() {
		return currentSubmissionLocation;
	}

	public void reset() {
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
	}

	public void setExternalSetValue(String value) {
		myLogger.warn("Not supported yet.");
		throw new RuntimeException("Setting value not supported yet.");
	}

	private void calculateAvailableGridResources() {
		
		siteModel.removeAllElements();

		Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
		jobProperties.put(JobSubmissionProperty.APPLICATIONNAME,
				applicationName);
		jobProperties.put(JobSubmissionProperty.APPLICATIONVERSION,
				currentVersion);

		SortedSet<GridResource> resources = registry
				.getUserApplicationInformation(applicationName)
				.getBestSubmissionLocations(jobProperties, currentFqan);

		currentRankedSites = GridResourceHelpers.asSetOfRankedSites(resources);

		for (RankedSite site : currentRankedSites) {
			siteModel.addElement(site);
		}

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

	public void valueChanged(TemplateNodePanel panel, String newValue) {

		if (panel instanceof ApplicationVersion) {
			this.currentVersion = newValue;
			calculateAvailableGridResources();
			
		}

	}

	public void fqansChanged(FqanEvent event) {
		this.currentFqan = event.getFqan();
		calculateAvailableGridResources();
	}

	public void removeValueListener(ValueListener v) {
	}

	public void addValueListener(ValueListener v) {
	}

	private JLabel getLblSite() {
		if (lblSite == null) {
			lblSite = new JLabel("Site:");
		}
		return lblSite;
	}

	private JLabel getLblQueue() {
		if (lblQueue == null) {
			lblQueue = new JLabel("Queue:");
		}
		return lblQueue;
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
									setCurrentSubmissionLocation((GridResource)o);
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
	
	private void setCurrentSubmissionLocation(GridResource r) {
		currentSubmissionLocation = SubmissionLocationHelpers.createSubmissionLocationString(r);
		getGridResourceInfoPanel().setGridResource(r);
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
							setCurrentSubmissionLocation((GridResource)o);
						}
						

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return siteComboBox;
	}

	private GridResourceInfoPanel getGridResourceInfoPanel() {
		if (gridResourceInfoPanel == null) {
			gridResourceInfoPanel = new GridResourceInfoPanel();
		}
		return gridResourceInfoPanel;
	}
}
