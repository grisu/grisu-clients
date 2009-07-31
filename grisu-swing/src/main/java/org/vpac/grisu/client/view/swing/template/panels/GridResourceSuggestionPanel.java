package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeEvent;
import org.vpac.grisu.client.view.swing.template.modules.genericAuto.GridResourceDisplayPanel;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.info.UserApplicationInformation;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GridResourceSuggestionPanel extends JPanel implements TemplateNodePanel, ValueListener {
	
	private LineBorder selectedBorder = new LineBorder(Color.black, 5, false);
	private LineBorder deselectedBorder = new LineBorder(Color.black, 1, false);
	
	private JScrollPane scrollPane;
	private JPanel containerPanel;
	static final Logger myLogger = Logger.getLogger(GridResourceSuggestionPanel.class.getName());
	
	private UserApplicationInformation infoObject = null;
//	private UserProperties esv;
	private GrisuRegistry registry;

	
	private Version versionPanel = null;
	private TemplateNode templateNode = null;
	private ExecutionFileSystem executionFileSystemPanel = null;
	private String currentStagingFilesystem = null;
	
	private UserEnvironmentManager userInformation;
	
	private List<GridResource> currentBestGridResources = null;
	private GridResource selectedResource = null;
	
	/**
	 * Create the panel
	 */
	public GridResourceSuggestionPanel() {
		super();
		setBorder(new TitledBorder(null, "Available submission locations", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("69px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("79dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		//
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
			}
		}
		return versionPanel;
	}
	public void valueChanged(TemplateNodePanel panel, String newValue) {

		System.out.println("Value changed: "+newValue);
		
		containerPanel.removeAll();
		selectedResource = null;
//		containerPanel.repaint();
		
		Map<JobSubmissionProperty, String> tempJobProperties = new HashMap<JobSubmissionProperty, String>();
		tempJobProperties.put(JobSubmissionProperty.APPLICATIONNAME, newValue);
		currentBestGridResources = infoObject.getBestSubmissionLocations(tempJobProperties, registry.getUserEnvironmentManager().getCurrentFqan());
		 
		try {
			
			if ( currentBestGridResources.size() == 0 ) {
				myLogger.warn("No grid resources. This should not happen...");
				return;
			}
			
			String bestSubLoc = SubmissionLocationHelpers.createSubmissionLocationString(currentBestGridResources.get(0));
			setStagingFS(bestSubLoc);
			
			int max = 5;
			
			if ( currentBestGridResources.size() < 5 ) {
				max = currentBestGridResources.size();
			}
			
			for ( int i=0; i<max; i++ ) {
				
				GridResourceDisplayPanel resdisplaypanel = new GridResourceDisplayPanel(this, currentBestGridResources.get(i));
				containerPanel.add(resdisplaypanel);
				if ( i == 0 ) {
					setCurrentlySelectedResourcePanel(resdisplaypanel);
				}
			}
			containerPanel.repaint();
			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void addValueListener(ValueListener v) {
		// TODO Auto-generated method stub
		
	}

	public JPanel getTemplateNodePanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeValueListener(ValueListener v) {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void setTemplateNode(TemplateNode node)
			throws TemplateNodePanelException {
		this.templateNode = node;
		this.templateNode.setTemplateNodeValueSetter(this);
		registry = GrisuRegistry.getDefault(node.getTemplate().getEnvironmentManager().getServiceInterface());
		
		this.userInformation = GrisuRegistry.getDefault(node.getTemplate().getEnvironmentManager().getServiceInterface()).getUserEnvironmentManager();
		this.infoObject = GrisuRegistry.getDefault(node.getTemplate().getEnvironmentManager().getServiceInterface())
		.getUserApplicationInformation(templateNode.getTemplate().getApplicationName());
		getVersionPanel();
		add(getScrollPane(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		
	}

	public void templateNodeUpdated(TemplateNodeEvent event) {
		// TODO Auto-generated method stub
		
	}

	public String getExternalSetValue() {
		
		if ( selectedResource != null ) {
			return SubmissionLocationHelpers.createSubmissionLocationString(selectedResource);
		}
		
		if ( currentBestGridResources == null || currentBestGridResources.size() == 0 ) {
			return null;
		} else {
			return SubmissionLocationHelpers.createSubmissionLocationString(currentBestGridResources.get(0));
		}
		
	}

	public void setExternalSetValue(String value) {

		// not supported
		throw new RuntimeException("Setting the value is not supported for the GridResourceSuggestionPanel.");
	}
	
	private void setStagingFS(String submissionLocation) {

		MountPoint fs = userInformation.getRecommendedMountPoint(
				submissionLocation, registry.getUserEnvironmentManager().getCurrentFqan());
		if (getExecutionFileSystemPanel() != null) {
			getExecutionFileSystemPanel().setExternalSetValue(fs.getRootUrl());
		}

		currentStagingFilesystem = fs.getRootUrl();
		myLogger.debug("Set staging fs to: " + fs);

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
	/**
	 * @return
	 */
	protected JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel();
			containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.X_AXIS));
			final FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			containerPanel.setLayout(flowLayout);
		}
		return containerPanel;
	}
	/**
	 * @return
	 */
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getContainerPanel());
		}
		return scrollPane;
	}
	
	public void setCurrentlySelectedResourcePanel(GridResourceDisplayPanel selectedDisplayPanel) {
	
		for ( Component child : containerPanel.getComponents() ) {
			GridResourceDisplayPanel displaypanel = (GridResourceDisplayPanel)child;
			
			// comparing references is ok in this case
			if ( displaypanel == selectedDisplayPanel ) {
				displaypanel.setBorder(selectedBorder);
				selectedResource = displaypanel.getResource();
			} else {
				displaypanel.setBorder(deselectedBorder);
			}
		}
		
	}
	

}
