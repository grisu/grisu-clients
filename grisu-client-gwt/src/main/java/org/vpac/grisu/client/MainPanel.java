package org.vpac.grisu.client;

import org.vpac.grisu.client.files.FileManagementPanel;
import org.vpac.grisu.client.jobCreation.JobCreationHolderPanel;
import org.vpac.grisu.client.jobCreation.MdsJobCreationPanel;
import org.vpac.grisu.client.jobMonitoring.JobListPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Hyperlink;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.CardLayout;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

public class MainPanel extends Panel {

	private Panel centerPanel;
	private Panel westPanel;
	private Panel northPanel;
	private Panel southPanel;
	
	private final GwtServiceInterfaceWrapperAsync service;
	private Hyperlink jobsLink;
	private Hyperlink filesLink;
	private Panel cardPanel;
	private HomePanel homePanel;
	private JobListPanel jobListPanel;
	private Hyperlink homeLink;
	private FileManagementPanel fileManagementPanel;
	private Hyperlink hyperlink;
	private JobCreationHolderPanel jobCreationHolderPanel;
	

	public MainPanel(GwtServiceInterfaceWrapperAsync service) {
		this.service = service;
		setLayout(new BorderLayout());
		getCenterPanel().setBorder(false);
		add(getCenterPanel(), new BorderLayoutData(RegionPosition.CENTER));
		getWestPanel().setPaddings(10);
		getWestPanel().setBorder(false);
		add(getWestPanel(), new BorderLayoutData(RegionPosition.WEST));
		getWestPanel().setWidth("174px");
		add(getNorthPanel(), new BorderLayoutData(RegionPosition.NORTH));
		add(getSouthPanel(), new BorderLayoutData(RegionPosition.SOUTH));
	}

	
	
	private Panel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new Panel();
			centerPanel.setLayout(new FitLayout());
			getCardPanel().setBorder(false);
			centerPanel.add(getCardPanel());
		}
		return centerPanel;
	}
	
	public int getCenterPanelHeight() {
		int height = getCenterPanel().getHeight();
		return height;
	}
	
	private Panel getWestPanel() {
		if (westPanel == null) {
			westPanel = new Panel();
			westPanel.setLayout(new VerticalLayout(10));
			westPanel.add(getHomeLink());
			westPanel.add(getHyperlink());
			westPanel.add(getJobsLink());
			westPanel.add(getFilesLink());
		}
		return westPanel;
	}
	private Panel getNorthPanel() {
		if (northPanel == null) {
			northPanel = new Panel("Grisu web client");
		}
		return northPanel;
	}
	private Panel getSouthPanel() {
		if (southPanel == null) {
			southPanel = new Panel("New Panel");
		}
		return southPanel;
	}
	private Hyperlink getJobsLink() {
		if (jobsLink == null) {
			jobsLink = new Hyperlink("Jobs", false, "newHistoryToken");
			jobsLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					getCardPanel().setActiveItem(2);
					getCardPanel().doLayout();
				}
			});
		}
		return jobsLink;
	}
	private Hyperlink getFilesLink() {
		if (filesLink == null) {
			filesLink = new Hyperlink("Files", false, "newHistoryToken");
			filesLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					getCardPanel().setActiveItem(3);
				}
			});
		}
		return filesLink;
	}
	private Panel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new Panel();
			cardPanel.setLayout(new CardLayout(false));
			getHomePanel().setBorder(false);
			getJobListPanel().setBorder(false);
			cardPanel.add(getHomePanel());
//			cardPanel.add(getJobCreationHolderPanel());
			cardPanel.add(new MdsJobCreationPanel(service));
			cardPanel.add(getJobListPanel());
			cardPanel.add(getFileManagementPanel());
			cardPanel.setActiveItem(0);
		}
		return cardPanel;
	}
	private HomePanel getHomePanel() {
		if (homePanel == null) {
			homePanel = new HomePanel();
		}
		return homePanel;
	}
	private JobListPanel getJobListPanel() {
		if ( jobListPanel == null ) {
			jobListPanel = new JobListPanel(service); 
		}
		return jobListPanel;
	}
	
	private FileManagementPanel getFileManagementPanel() {
		
		if ( fileManagementPanel == null ) {
			fileManagementPanel = new FileManagementPanel(service);
		}
		return fileManagementPanel;
	}
	
//	private FileListPanel getFileListPanel() {
//		if ( fileListPanel == null ) {
//			fileListPanel = new FileListPanel(service);
//		}
//		return fileListPanel;
//	}
	
	private Hyperlink getHomeLink() {
		if (homeLink == null) {
			homeLink = new Hyperlink("Home", false, "newHistoryToken");
			homeLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					getCardPanel().setActiveItem(0);
				}
			});
		}
		return homeLink;
	}
	private Hyperlink getHyperlink() {
		if (hyperlink == null) {
			hyperlink = new Hyperlink("New job", false, "newHistoryToken");
			hyperlink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					getCardPanel().setActiveItem(1);
					getCardPanel().doLayout();
				}
			});
		}
		return hyperlink;
	}
	
	private JobCreationHolderPanel getJobCreationHolderPanel() {
		if ( jobCreationHolderPanel == null ) {
			jobCreationHolderPanel = new JobCreationHolderPanel(service);
		}
		return jobCreationHolderPanel;
	}
	
}
