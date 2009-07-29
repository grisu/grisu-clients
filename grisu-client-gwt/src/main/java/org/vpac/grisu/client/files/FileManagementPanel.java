package org.vpac.grisu.client.files;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;
import org.vpac.grisu.client.model.GwtGrisuCacheFile;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.CycleButton;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.CycleButtonListenerAdapter;
import com.gwtext.client.widgets.layout.CardLayout;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;
import com.gwtext.client.widgets.menu.CheckItem;

public class FileManagementPanel extends Panel implements ValueChangeHandler<GwtGrisuRemoteFile> {
	
	public static final String FILE_MODE = "Files";
	public static final String PREVIEW_MODE = "Preview";
	
	
	private FileListPanel leftFileListPanel;
	private String forcedLeftRootDirectory;
	private final GwtServiceInterfaceWrapperAsync service;
	private FileListPanel fileListPanel_1;
	private Panel rightCardPanel;
	private Panel cardPanel;
	private Panel containerPanel;
	private FileViewerPanel fileViewerPanel;
	private Panel bottomPanel;
	private Panel leftBottomPanel;
	private Panel rightBottomPanel;
	private CycleButton cycleButton;
	
	private final CheckItem fileItem = new CheckItem(FILE_MODE, true);  
	private final CheckItem previewItem = new CheckItem(PREVIEW_MODE, false);  
	private Button button;
	private Button btnRefresh;
	private Button button_1;
	
	// this is for when you want to use a filemanagementpanel as a job detail file panel. 
	// You don't want to show the list of sites in this case...
	final private boolean loadSitesOnLeftPanel;

	public FileManagementPanel(GwtServiceInterfaceWrapperAsync service) {
		this(service, true);
	}
	
	public FileManagementPanel(GwtServiceInterfaceWrapperAsync service, boolean loadSitesOnLeftPanel) {
		setBorder(false);
		this.loadSitesOnLeftPanel = loadSitesOnLeftPanel;
		this.service = service;
		setLayout(new RowLayout());
		getContainerPanel().setPaddings(10);
		add(getContainerPanel(), new RowLayoutData("100%"));
		add(getBottomPanel());

	}
	
	public void setForcedLeftRootDirectory(String forcedLeftRootDirectory) {
		
		this.forcedLeftRootDirectory = forcedLeftRootDirectory;
		
		getLeftFileListPanel().setForcedRootDirectory(forcedLeftRootDirectory);
		
	}
	
	private Panel getBottomPanel() {
		if ( bottomPanel == null ) {
			bottomPanel = new Panel();
			bottomPanel.setLayout(new ColumnLayout());
			bottomPanel.setHeight(40);
			bottomPanel.setBorder(false);
			getLeftBottomPanel().setPaddings(10);
			bottomPanel.add(getLeftBottomPanel());
			getRightBottomPanel().setPaddings(10);
			bottomPanel.add(getRightBottomPanel(), new ColumnLayoutData(1));
		}
		return bottomPanel;
	}
	
	private Panel getLeftBottomPanel() {
		if ( leftBottomPanel == null ) {
			leftBottomPanel = new Panel();
			leftBottomPanel.setLayout(new HorizontalLayout(10));
			leftBottomPanel.setBorder(false);
			leftBottomPanel.setHeight(40);
			leftBottomPanel.setWidth(370);
			getButton().setIcon("js/ext/resources/images/default/grid/refresh.gif");
			leftBottomPanel.add(getButton());
			getButton_1().addListener(new ButtonListenerAdapter() {
				public void onClick(Button button, EventObject e) {
					
					downloadFiles();
					
				}
			});
			leftBottomPanel.add(getButton_1());
		}
		return leftBottomPanel;
	}
	
	public void downloadFiles() {
		
		final GwtGrisuRemoteFile[] selectedFiles = getLeftFileListPanel().getCurrentlySelectedFiles();
		 
		 if ( selectedFiles == null || selectedFiles.length == 0 ) {
			 Window.alert("No files selected.");
			 return;
		 }

		 if ( selectedFiles.length == 1 ) {
			 getLeftFileListPanel().setLoading(true);
			 service.downloadFile(selectedFiles[0], new AsyncCallback<GwtGrisuCacheFile>(){

				public void onFailure(Throwable arg0) {
					getLeftFileListPanel().setLoading(false);
					arg0.printStackTrace();
					Window.alert("Could not download file: "+selectedFiles[0].getName());
				}

				public void onSuccess(GwtGrisuCacheFile arg0) {
					getLeftFileListPanel().setLoading(false);
					Window.open(arg0.getPublicUrl(),
							"_self", ""); 
					
				}
				 
				 
			 });
			 
		 } else {

			 getLeftFileListPanel().setLoading(true);
			 service.zipFilesAndPrepareForDownload(selectedFiles, new AsyncCallback<String>(){

				public void onFailure(Throwable arg0) {
					getLeftFileListPanel().setLoading(false);
					arg0.printStackTrace();
					Window.alert("Could not zip and prepare files.");
				}

				public void onSuccess(String arg0) {
					getLeftFileListPanel().setLoading(false);
					Window.open(arg0, "_self", ""); 

				}
				 
			 });
			 
		 }
		
		
	}
	
	private Panel getRightBottomPanel() {
		if ( rightBottomPanel == null ) {
			rightBottomPanel = new Panel();
			rightBottomPanel.setHeight(40);
			rightBottomPanel.setBorder(false);
			rightBottomPanel.setLayout(new HorizontalLayout(10));
			getBtnRefresh().setIcon("js/ext/resources/images/default/grid/refresh.gif");
			rightBottomPanel.add(getBtnRefresh());
			rightBottomPanel.add(getPreviewButton());
		}
		return rightBottomPanel;
	}
	
	private CycleButton getPreviewButton() {
		if ( cycleButton == null ) {
			cycleButton = new CycleButton();  
			cycleButton.setShowText(true);  
			cycleButton.setPrependText("Mode: ");
			cycleButton.addItem(fileItem);  
			cycleButton.addItem(previewItem);  
			cycleButton.addListener(new CycleButtonListenerAdapter() {
				public void onChange(CycleButton self, CheckItem item) {  
				       if ( FILE_MODE.equals(item.getText())) {
				    	   cardPanel.setActiveItem(0);
				       } else if ( PREVIEW_MODE.equals(item.getText()) ) {
				    	   GwtGrisuRemoteFile file = getLeftFileListPanel().getCurrentlySelectedFile();
			    		   displayCurrentFile(file);
				       } else {
				    	   throw new RuntimeException("Mode: "+item.getText()+" not supported...");
				       }
				}  
			});
		}
		return cycleButton;
	}

	private Panel getContainerPanel() {
		if ( containerPanel == null ) {
			containerPanel = new Panel();
			containerPanel.setLayout(new ColumnLayout());
			getLeftFileListPanel().setBorder(false);
			containerPanel.add(getLeftFileListPanel());
			containerPanel.add(getRightCardPanel(), new ColumnLayoutData(1));
		}
		return containerPanel;
	}
	
	private FileListPanel getLeftFileListPanel() {
		if (leftFileListPanel == null) {
			leftFileListPanel = new FileListPanel(service, loadSitesOnLeftPanel);
			leftFileListPanel.setWidth(350);
			leftFileListPanel.setHeight(500);
			leftFileListPanel.addValueChangeHandler(this);
		}
		return leftFileListPanel;
	}
	private FileListPanel getFileListPanel_1() {
		if (fileListPanel_1 == null) {
			fileListPanel_1 = new FileListPanel(service);
		}
		return fileListPanel_1;
	}
	private Panel getRightCardPanel() {
		if (rightCardPanel == null) {
			cardPanel = new Panel();
			cardPanel.setLayout(new CardLayout());
			cardPanel.setHeight(500);
			getFileListPanel_1().setBorder(false);
//			rightCardPanel.add(cardPanel);
			cardPanel.add(getFileListPanel_1());
			cardPanel.add(getFileViewerPanel());
			cardPanel.setActiveItem(0);
			rightCardPanel = new PaddedPanel(cardPanel, 0, 10, 0, 0);
			rightCardPanel.setLayout(new FitLayout());
			rightCardPanel.setBorder(false);
			rightCardPanel.setHeight(500);
		}
		return rightCardPanel;
	}
	
	private FileViewerPanel getFileViewerPanel() {
		if (fileViewerPanel == null) {
			fileViewerPanel = new FileViewerPanel(service);
			fileViewerPanel.setHeight(500);
		}
		return fileViewerPanel;
	}

	public void onValueChange(ValueChangeEvent<GwtGrisuRemoteFile> arg0) {

		getPreviewButton().setActiveItem(previewItem);
//		displayCurrentFile(arg0.getValue());

		System.out.println("Clicked: "+arg0.getValue().getName()+" "+arg0.getValue().getPath());
		
	}
	
	private void displayCurrentFile(GwtGrisuRemoteFile file) {
 	   cardPanel.setActiveItem(1); 
 	   if ( file != null ) {
 		   getFileViewerPanel().displayFile(file);
 	   }
	}




	private Button getButton() {
		if (button == null) {
			button = new Button("Refresh");
		}
		return button;
	}
	private Button getBtnRefresh() {
		if (btnRefresh == null) {
			btnRefresh = new Button("Refresh");
		}
		return btnRefresh;
	}
	private Button getButton_1() {
		if (button_1 == null) {
			button_1 = new Button("Download");
		}
		return button_1;
	}
}
