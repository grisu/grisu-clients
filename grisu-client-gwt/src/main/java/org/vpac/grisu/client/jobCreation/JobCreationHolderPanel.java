package org.vpac.grisu.client.jobCreation;

import org.vpac.grisu.client.GwtServiceInterfaceWrapperAsync;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.FitLayout;

public class JobCreationHolderPanel extends Panel {
	
	private final GwtServiceInterfaceWrapperAsync service;

	public JobCreationHolderPanel(GwtServiceInterfaceWrapperAsync service) {
		this.service = service;
		setLayout(new FitLayout());
		setBorder(false);
		MdsJobCreationPanel mdsJobCreationPanel = new MdsJobCreationPanel(service);
		add(mdsJobCreationPanel);
		
	}
	


}
