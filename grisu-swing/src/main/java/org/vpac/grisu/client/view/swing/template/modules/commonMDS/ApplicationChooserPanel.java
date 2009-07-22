package org.vpac.grisu.client.view.swing.template.modules.commonMDS;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.ApplicationObject;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.VersionObject;
import org.vpac.grisu.client.model.template.modules.CommonMDS;
import org.vpac.grisu.client.model.template.modules.TemplateModule;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ApplicationChooserPanel extends JPanel implements
		TreeSelectionListener, FqanListener, MountPointsListener {
	
	static final Logger myLogger = Logger.getLogger(ApplicationChooserPanel.class.getName());

	private JSplitPane splitPane_1;
	private JSplitPane splitPane;
	private JList queueList;
	private JScrollPane queueScrollPane;
	private JList siteList;
	private JScrollPane siteScrollPane;
	private JTree applicationTree;
	private JScrollPane appScrollPane;

	DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			"Available applications");
	private DefaultTreeModel applicationModel = null;
	private DefaultListModel siteModel = new DefaultListModel();
	private DefaultListModel queueModel = new DefaultListModel();

	private String[] allGridApps = null;
	
	private CommonMDS commonMdsModule = null;
	private TemplateNode application = null;
	private TemplateNode hostname = null;
	private TemplateNode executionFileSystem = null;
	private TemplateNode module = null;

	private EnvironmentManager environmentManager = null;
	private ServiceInterface serviceInterface = null;
	
	private ApplicationObject currentApplicationObject = null;
	private VersionObject currentVersionObject = null;
	
	private DefaultTemplateNodeValueSetter executionFileSystemSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter hostnameSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter applicationSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter moduleSetter = new DefaultTemplateNodeValueSetter();
	
	/**
	 * Create the panel
	 */
	public ApplicationChooserPanel() {
		super();

		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("64dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("66dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("34dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getSplitPane(), new CellConstraints(2, 2, 5, 1));
		//
	}

	public void initialize(CommonMDS module) {
		
		if ( environmentManager != null ) {
			environmentManager.removeFqanListener(this);
			environmentManager.removeMountPointListener(this);
		}
		root = new DefaultMutableTreeNode(
		"Available applications");
//		applicationModel.setRoot(root);

		this.commonMdsModule = module;
		
		this.application = module.getTemplateNodes().get("Application");
		this.hostname = module.getTemplateNodes().get("HostName");
		this.executionFileSystem = module.getTemplateNodes().get("ExecutionFileSystem");
		this.module = module.getTemplateNodes().get("Module");;
		
		this.application.setTemplateNodeValueSetter(applicationSetter);
		this.hostname.setTemplateNodeValueSetter(hostnameSetter);
		this.executionFileSystem.setTemplateNodeValueSetter(executionFileSystemSetter);
		this.module.setTemplateNodeValueSetter(moduleSetter);
		
		environmentManager = application.getTemplate().getEnvironmentManager();
		environmentManager.addFqanListener(this);
		environmentManager.addMountPointListener(this);
		serviceInterface = environmentManager.getServiceInterface();

		allGridApps = serviceInterface
		.getAllAvailableApplications(environmentManager
				.getAllOfTheUsersSites().toArray(new String[]{}));

		initTree();
	}
	
	private void initTree() {

		root.removeAllChildren();
//		getApplicationTree().removeTreeSelectionListener(this);

		applicationModel = new DefaultTreeModel(root);

//		getApplicationTree().addTreeSelectionListener(this);

		for (String app : allGridApps) {
			ApplicationObject appObj = new ApplicationObject(app,
					environmentManager.getAllOfTheUsersSites(),
					environmentManager);
			DefaultMutableTreeNode appNode = new DefaultMutableTreeNode(appObj);
			root.add(appNode);
		}

		getApplicationTree().setModel(applicationModel);
	}

	/**
	 * @return
	 */
	protected JScrollPane getAppScrollPane() {
		if (appScrollPane == null) {
			appScrollPane = new JScrollPane();
			appScrollPane.setMinimumSize(new Dimension(150, 0));
			appScrollPane.setViewportView(getApplicationTree());
		}
		return appScrollPane;
	}

	/**
	 * @return
	 */
	protected JTree getApplicationTree() {
		if (applicationTree == null) {
			applicationTree = new JTree();
			applicationTree.setRootVisible(false);
			applicationTree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			applicationTree.addTreeSelectionListener(this);
		}
		return applicationTree;
	}

	/**
	 * @return
	 */
	protected JScrollPane getSiteScrollPane() {
		if (siteScrollPane == null) {
			siteScrollPane = new JScrollPane();
			siteScrollPane.setMinimumSize(new Dimension(120, 0));
			siteScrollPane.setViewportView(getSiteList());
		}
		return siteScrollPane;
	}

	/**
	 * @return
	 */
	protected JList getSiteList() {
		if (siteList == null) {
			siteList = new JList(siteModel);
			siteList.setMinimumSize(new Dimension(150, 0));
			siteList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					
					displayMatchingQueues();
					
					commonMdsModule.submissionObjectChanged(getCurrentlySelectedSubmissionObject());
				}
			});
		}
		return siteList;
	}

	/**
	 * @return
	 */
	protected JScrollPane getQueueScrollPane() {
		if (queueScrollPane == null) {
			queueScrollPane = new JScrollPane();
			queueScrollPane.setViewportView(getQueueList());
		}
		return queueScrollPane;
	}

	/**
	 * @return
	 */
	protected JList getQueueList() {
		if (queueList == null) {
			queueList = new JList(queueModel);
			queueList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					commonMdsModule.submissionObjectChanged(getCurrentlySelectedSubmissionObject());
				}
			});
		}
		return queueList;
	}

	// for tree selection event -- here's where the important stuff happens
	public void valueChanged(TreeSelectionEvent e) {
		
		setCursor(Cursor
				.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		// This method is useful only when the selection model allows a single
		// selection.
		DefaultMutableTreeNode selectedApplication = (DefaultMutableTreeNode) getApplicationTree()
				.getLastSelectedPathComponent();

		if (selectedApplication == null) {
			// Nothing is selected.
			deselectSiteAndQueueLists();
			setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		Object nodeInfo = selectedApplication.getUserObject();

		if (nodeInfo instanceof ApplicationObject) {
			// load versions and display possible sites
			ApplicationObject appObj = (ApplicationObject) nodeInfo;

			if (selectedApplication.getChildCount() == 0) {
				
				VersionObject[] versions = appObj.getVersionsForAllSites(environmentManager.getDefaultFqan());
				
				if (versions.length == 0) {
					// not available for this fqan, do nothing
					siteModel.removeAllElements();
					queueModel.removeAllElements();
					hostnameSetter.setExternalSetValue(null);
					executionFileSystemSetter.setExternalSetValue(null);
					setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

					return;
				}
				
				for (VersionObject version : versions ) {
					DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode(
							version);
					selectedApplication.add(versionNode);
				}
			}
			currentApplicationObject = appObj;
			currentVersionObject = null;
			displayMatchingSites(appObj);

		} else if (nodeInfo instanceof VersionObject) {
			VersionObject versionObj = (VersionObject) nodeInfo;

//			currentApplicationObject = versionObj.getApplication();
			currentApplicationObject = null;
			currentVersionObject = versionObj;
			
			displayMatchingSites(versionObj);
		} else {
			setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			// this should never happen
			throw new RuntimeException("Unexpected object clicked.");
		}
		setCursor(Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		commonMdsModule.submissionObjectChanged(getCurrentlySelectedSubmissionObject());
	}
	
	private void deselectSiteAndQueueLists() {

		queueModel.removeAllElements();
		siteModel.removeAllElements();
		
	}

	private void displayMatchingSites(ApplicationObject appObj) {

		Set<String> availableSites = appObj.getActualAvailableSites(environmentManager.getDefaultFqan());

		String oldSite = (String) getSiteList().getSelectedValue();

		siteModel.removeAllElements();

		for (String site : availableSites) {
			siteModel.addElement(site);
		}

		if (siteModel.size() == 1) {
			getSiteList().setSelectedIndex(0);
			displayMatchingQueues();
		} else if (siteModel.indexOf(oldSite) != -1) {
			getSiteList().setSelectedValue(oldSite, true);
			displayMatchingQueues();
		} else if (siteModel.size() > 0) {
			getSiteList().setSelectedIndex(0);
			displayMatchingQueues();
		} else {
			queueModel.removeAllElements();
		}
	}

	private void displayMatchingSites(VersionObject versionObj) {

		Set<String> availableSites = versionObj.getSitesWhereThisVersionIsAvailable();

		String oldSite = (String) getSiteList().getSelectedValue();

		siteModel.removeAllElements();

		Set<String> allSites = environmentManager.getAllOfTheUsersSites();
		
		for (String site : availableSites) {
			if ( allSites.contains(site) )
				siteModel.addElement(site);
		}

		if (siteModel.size() == 1) {
			getSiteList().setSelectedIndex(0);
			displayMatchingQueues();
		} else if (siteModel.indexOf(oldSite) != -1) {
			getSiteList().setSelectedValue(oldSite, true);
			displayMatchingQueues();
		} else if (siteModel.size() > 0) {
			getSiteList().setSelectedIndex(0);
			displayMatchingQueues();
		} else {
			queueModel.removeAllElements();
		}

	}
	
	private void displayMatchingQueues() {
		
		SubmissionLocation oldQueue = (SubmissionLocation)getQueueList().getSelectedValue();
		String selectedSite = (String)getSiteList().getSelectedValue();

		if ( selectedSite == null || "".equals(selectedSite) ) {
			return;
		}
		
		Set<SubmissionLocation> possibleSubmissionLocations = null;
		
		if ( currentVersionObject == null ) {
			possibleSubmissionLocations = currentApplicationObject.getSubmissionLocationsForSite(selectedSite);
		} else { 
			possibleSubmissionLocations = currentVersionObject.getSubmissionLocationsForSite(selectedSite);
		}
		
		queueModel.removeAllElements();
		
		for ( SubmissionLocation loc : possibleSubmissionLocations ) {
			queueModel.addElement(loc);
		}
		
		if ( queueModel.size() == 1 ) {
			getQueueList().setSelectedIndex(0);
		} else if ( queueModel.indexOf(oldQueue) != -1 ) {
			getQueueList().setSelectedValue(oldQueue, true);
		} else if ( queueModel.size() > 0 ) {
			getQueueList().setSelectedIndex(0);
		}
		
		// now we have to set the three templateNodes for this panel
		
		
		
		
	}
	
	/**
	 * Returns the values for submission location and application/version as currently
	 * selected by the user. If information is missing, null is returned.
	 * @return the SubmissionObject or null
	 */
	public SubmissionObject getCurrentlySelectedSubmissionObject() {

		if ( getQueueList().getSelectedIndex() == -1 ) {
			return null;
		}
		
		SubmissionLocation location = (SubmissionLocation)getQueueList().getSelectedValue();
		
		if ( currentVersionObject == null ) {

			if ( currentApplicationObject == null ) {
				return null;
			}
			try {
				currentApplicationObject.setCurrentSubmissionLocation(location);
			} catch (SubmissionLocationException e) {
				// should never happen
				e.printStackTrace();
				return null;
			}
			
			myLogger.debug("Selected non-version ApplicationObject "+application.getName()+" and submission location: "+location.getLocation());
			
			return currentApplicationObject;
		} else {
			try {
				currentVersionObject.setCurrentSubmissionLocation(location);
			} catch (SubmissionLocationException e) {
				// should never happen
				e.printStackTrace();
				return null;
			}
			myLogger.debug("Selected VersionObject "+application.getName()+", Version: "+currentVersionObject.getCurrentVersion()+" and submission location: "+location.getLocation());
			return currentVersionObject;
		}

	}
	

	/**
	 * @return
	 */
	protected JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getAppScrollPane());
			splitPane.setRightComponent(getSplitPane_1());
		}
		return splitPane;
	}
	/**
	 * @return
	 */
	protected JSplitPane getSplitPane_1() {
		if (splitPane_1 == null) {
			splitPane_1 = new JSplitPane();
			splitPane_1.setMinimumSize(new Dimension(200, 0));
			splitPane_1.setLeftComponent(getSiteScrollPane());
			splitPane_1.setRightComponent(getQueueScrollPane());
		}
		return splitPane_1;
	}

	public void fqansChanged(FqanEvent event) {
		reset();
	}

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {

		reset();
	}

	private void reset() {
//		SubmissionObject oldSo = getCurrentlySelectedSubmissionObject();

		hostnameSetter.setExternalSetValue(null);
		applicationSetter.setExternalSetValue(null);
		moduleSetter.setExternalSetValue(null);
		executionFileSystemSetter.setExternalSetValue(null);

//		applicationTree = null;
		
		initialize(commonMdsModule);
//		initTree();
		//TODO Markus
		
		currentApplicationObject = null;
		currentVersionObject = null;
	}
	
}
