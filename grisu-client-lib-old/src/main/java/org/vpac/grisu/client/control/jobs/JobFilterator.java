package org.vpac.grisu.client.control.jobs;

import java.util.List;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

import ca.odell.glazedlists.TextFilterator;

public class JobFilterator implements TextFilterator {

	public void getFilterStrings(List baseList, Object element) {
		
		GrisuJobMonitoringObject job = (GrisuJobMonitoringObject)element;
		
		baseList.add(job.getName());
		baseList.add(job.getStatus());
		baseList.add(job.getFqan());
		baseList.add(job.getSubmissionHost());
		
	}

}
