package org.vpac.grisu.client.jobMonitoring;

import java.util.Map;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;
import org.vpac.grisu.client.files.FileManagementPanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public class JobDetailsPanel extends Panel {
	private Panel pnlJobDetails;
	private FileManagementPanel fileManagementPanel;
	
	private final GwtServiceInterfaceWrapperAsync service;
	private final String jobname;
	
	private TabPanel tabPanel;


	public JobDetailsPanel(GwtServiceInterfaceWrapperAsync service, String jobname) {
		this.service = service;
		this.jobname = jobname;

//		setAutoScroll(true);
		setClosable(true);
		setLayout(new FitLayout());
//		add(getPnlJobDetails());
		add(getTabPanel());
		
		loadJobDetails();
	}
	
	private TabPanel getTabPanel() {
		if ( tabPanel == null ) {
			tabPanel = new TabPanel();
			tabPanel.setTabPosition(Position.BOTTOM);
			tabPanel.add(getFileManagementPanel());
			tabPanel.add(getPnlJobDetails());
		}
		return tabPanel;
	}
	
	
	
	private void loadJobDetails() {
		
		service.getJobDetails(jobname, new AsyncCallback<Map<String,String>>(){

			public void onFailure(Throwable arg0) {
				arg0.printStackTrace();
				Window.alert("Could not get jobdetails for job "+jobname+": "+arg0.getLocalizedMessage());
			}

			public void onSuccess(Map<String, String> arg0) {

				getFileManagementPanel().setForcedLeftRootDirectory(arg0.get("jobDirectory"));
				
				for ( String key : arg0.keySet() ) {
					getPnlJobDetails().add(getPaddedPanel(key, arg0.get(key)), new AnchorLayoutData("100%"));
				}
				
				doLayout();
			}
			
			
		});
	}
	
	private FileManagementPanel getFileManagementPanel() {
		if ( fileManagementPanel == null ) {
			fileManagementPanel = new FileManagementPanel(service, false);
			fileManagementPanel.setTitle("   Job directory   ");
		}
		return fileManagementPanel;
	}

	private Panel getPnlJobDetails() {
		if (pnlJobDetails == null) {
			pnlJobDetails = new Panel("   Job details   ");
			pnlJobDetails.setAutoScroll(true);
			pnlJobDetails.setLayout(new AnchorLayout());
//			pnlJobDetails.add(getPaddedPanel("Jobname", jobname));
		}
		return pnlJobDetails;
	}
	private PaddedPanel getPaddedPanel(String key, String value) {
			
		Panel panel = new Panel();
		panel.setBorder(false);
//		panel.setSize(300, 40);
		panel.setLayout(new ColumnLayout());
		Label label = new Label();
		label.setText(key);
		label.setWidth(200);
		panel.add(label);
		Label valueLabel = new Label();
		valueLabel.setText(value);
		
		panel.add(valueLabel, new ColumnLayoutData(1));
		PaddedPanel paddedPanel = new PaddedPanel(panel, 5);
		return paddedPanel;
			
	}

}
