

package org.vpac.grisu.plugins.namd;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.AbstractJob;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.model.jobs.JobStatusEvent;

public class NamdJob extends AbstractJob {
	
	static final Logger myLogger = Logger.getLogger(NamdJob.class.getName());
	
	public static final String TOTAL_SERIES = "Total";
	public static final String TEMP_SERIES = "Temperature";
	public static final String PRESSURE_SERIES = "Pressure";
	
	private GrisuJobMonitoringObject job = null;
	private GrisuFileObject stdout = null;
	
	private int minimize = -1;
	private int numsteps = -1;
	
	private File stdout_cache = null;
	
	private Map<String, XYSeries> data = new HashMap<String, XYSeries>();
	
	int cpus = 0;
	double s_seconds = 0;
	double days_ns = 0;
	double memory = 0;
	int counter = 0;
	
	private int lastIteration = 0;
	
	public NamdJob(GrisuJobMonitoringObject job) {
		this.job = job;
		try {
			stdout = job.getEnvironmentManager().getFileManager().getFileObject(new URI(job.getStdout()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		minimize = Integer.parseInt(job.getJobProperties().get("minimize"));
		numsteps = Integer.parseInt(job.getJobProperties().get("numsteps"));
		createAndAddSeriesToData(TOTAL_SERIES);
		createAndAddSeriesToData(TEMP_SERIES);
		createAndAddSeriesToData(PRESSURE_SERIES);
		stdout_cache = stdout.getLocalRepresentation(false);
		parse();
	}
	
	private void createAndAddSeriesToData(String name) {
		
		XYSeries temp = new XYSeries(name, true, false);
		
		data.put(name, temp);
	}
	
	public void refresh() {
		long size = stdout_cache.lastModified();
		stdout_cache = stdout.getLocalRepresentation(true);
		if ( size == stdout_cache.lastModified()) {
			// do nothing
			fireJobStatusEvent(JobStatusEvent.NO_CHANGE);
			return;
		}
		parse();
		fireJobStatusEvent(JobStatusEvent.JOB_REFRESHED);
	}
	
	private void parse() {
		stdout_cache = stdout.getLocalRepresentation(false);
		
		List lines = null;
		try {
			lines = FileUtils.readLines(stdout_cache);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		for (Object lineObject : lines) {
			String line = ((String) lineObject).trim();
			
			if ( line.startsWith("Info: Benchmark time:") ) {
				String[] lineParts = line.split("\\s++");
				cpus += Integer.parseInt(lineParts[3]);
				s_seconds += Double.parseDouble(lineParts[5]);
				days_ns += Double.parseDouble(lineParts[7]);
				memory += Double.parseDouble(lineParts[9]);
				counter++;
			}

			if ( ! line.startsWith("ENERGY:") ) {
				continue;
			}
			
			String[] lineParts = line.split("\\s++");
			if (lineParts.length != 21) {
				continue;
			}
			
			Integer timestep_i = Integer.parseInt(lineParts[1]);
			
			if ( timestep_i <= minimize ) {
				if ( lastIteration < timestep_i )
					lastIteration = timestep_i;
				continue;
			}
			
			if ( timestep_i <= lastIteration ) {
				continue;
			}
			
			Double total_d = Double.parseDouble(lineParts[11]);
			Double temp_d = Double.parseDouble(lineParts[12]);
			Double pressure_d = Double.parseDouble(lineParts[16]);
			myLogger.debug("Adding timestep "+timestep_i+" to series.");
			data.get(TOTAL_SERIES).add(new XYDataItem(timestep_i, total_d));
			data.get(TEMP_SERIES).add(new XYDataItem(timestep_i, temp_d));
			data.get(PRESSURE_SERIES).add(new XYDataItem(timestep_i, pressure_d));
			
			lastIteration = timestep_i;
		}
		
	}
	
	public int getCPUAvg() {
		return cpus/counter;
	}
	
	public double getS_SecondsAvg() {
		return s_seconds/counter;
	}
	
	public double getDays_nsAvg() {
		return days_ns/counter;
	}
	
	public double getMemoryAvg() {
		return memory/counter;
	}
	
	public int getLastIteration() {
		return lastIteration;
	}
	
	public int getEstimatedTimeRemainingInSeconds() {
		return new Double((getTotalSteps() - lastIteration) * getS_SecondsAvg()).intValue(); 
	}
	
	public String getEstimatedTimeString() {
		int seconds = getEstimatedTimeRemainingInSeconds();
		
		int hours = seconds/3600;
		
		if ( hours == 1 ) {
			return "appr. 1 hour";
		} else {
			return "appr. "+hours+" hours";
		}
		
//		int days = 0;
//		days = seconds%(60*60*24)/(60*60*24);
//		
//		int hours = (seconds-(days*60*60*24)) % (60*60)/(60*60);
//		int minutes = (seconds - ((days*60*60*24)+(hours*60*60))) % 60/60;
//		
//		StringBuffer result = new StringBuffer();
//		if ( days > 0 ) {
//			result.append(days+" days, ");
//		}
//		result.append(hours+" hours, ");
//
//		result.append(minutes+" min");
//		
//		return result.toString();
	}

	public XYSeries getSeries(String seriesName) {
		return data.get(seriesName);
	}


	public int getMinimize() {
		return minimize;
	}

	public int getNumsteps() {
		return numsteps;
	}
	
	public int getTotalSteps() {
		return numsteps+minimize;
	}

	public Map<String, XYSeries> getData() {
		return data;
	}

	public GrisuJobMonitoringObject getJob() {
		return job;
	}
	


}
