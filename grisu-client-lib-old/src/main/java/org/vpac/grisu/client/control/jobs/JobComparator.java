package org.vpac.grisu.client.control.jobs;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;

/**
 * A class to sort the jobs according to their submission date.
 * 
 * @author Markus Binsteiner
 *
 */
public class JobComparator implements Comparator {
	

	public int compare(Object o1, Object o2) {

		GrisuJobMonitoringObject job1 = (GrisuJobMonitoringObject)o1;
		GrisuJobMonitoringObject job2 = (GrisuJobMonitoringObject)o2;
		
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = JobManager.inputDateFormat.parse(job1.getSubmissionTime());
			date2 = JobManager.inputDateFormat.parse(job2.getSubmissionTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return 0;
		}
		
		return new Long(date1.getTime()/1000 - date2.getTime()/1000).intValue(); 
		
	}

}
