package org.vpac.grisu.client.control.jobs;

import java.util.Comparator;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

public class JobnameComparator implements Comparator {

	public int compare(Object o1, Object o2) {

		GrisuJobMonitoringObject job1 = (GrisuJobMonitoringObject)o1;
		GrisuJobMonitoringObject job2 = (GrisuJobMonitoringObject)o2;
		
		return job1.getName().compareToIgnoreCase(job2.getName());
		
		
	}

}
