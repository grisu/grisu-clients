package org.vpac.grisu.client.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.model.MountPoint;

import au.org.arcs.mds.Constants;
import au.org.arcs.mds.SubmissionLocationHelpers;

/**
 * Just a wrapper object around a String.
 * 
 * @author Markus Binsteiner
 *
 */
public class SubmissionLocation implements Comparable<SubmissionLocation>{
	
	static final Logger myLogger = Logger.getLogger(SubmissionLocation.class.getName());

	private final String location;
	private final EnvironmentManager em;
	private String site = null;
	private String[] stagingFileSystems = null;
	private String hostname = null;
	private String queue = null;
	
	private Map<String, Set<MountPoint>> mountpointsPerFqan = new TreeMap<String, Set<MountPoint>>();
	
	public SubmissionLocation(String location, EnvironmentManager em) {
		this.location = location;
		this.em = em;
	}
	

	public String toString() {
		return getQueue();
	}
	
	public boolean equals(Object other) {
		if ( other instanceof SubmissionLocation ) {
			return location.equals(((SubmissionLocation)other).getLocation());
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return location.hashCode();
	}

	public int compareTo(SubmissionLocation o) {
//		return location.compareTo(o.getLocation());
		return getQueue().compareTo(o.getQueue());
	}

	public String getLocation() {
		return location;
	}
	
	public String getSite() {
		if ( site == null ) {
			site = em.lookupSite(EnvironmentManager.QUEUE_TYPE, location);
		}
		return site;
	}
	
	public String getHostname() {
		
		if ( hostname == null ) {
			int startIndex = location.indexOf(":") + 1;
			if (startIndex == -1)
				startIndex = 0;

			int endIndex = location.indexOf("#");
			if (endIndex == -1)
				endIndex = location.length();

			hostname = location.substring(startIndex, endIndex);
		}
		return hostname;
	}
	
	public String getQueue() {
		
		if ( queue == null ) {
			queue = SubmissionLocationHelpers.extractQueue(location);
		}
		return queue;
	}
	
	public String[] getStagingFileSystems() {
		if ( stagingFileSystems == null ) {
			myLogger.debug("Looking up staging filesystem for: "+location);
			stagingFileSystems = em.lookupStagingFileSystemsForSubmissionLocation(location);
		}
		return stagingFileSystems;
	}
	
	public Set<MountPoint> getAssiciatedMountPointsForFqan(String fqan) {
		
		if ( fqan == null ) {
			fqan = Constants.NON_VO_FQAN;
		}
		
		if ( mountpointsPerFqan.get(fqan) == null ) {
			for ( String stagingFs : getStagingFileSystems() ) {
				mountpointsPerFqan.put(fqan, em.getMountPointsForSubmissionLocationAndFqan(location, fqan));
			}
		}

		if ( mountpointsPerFqan.get(fqan) == null ) {
			return new HashSet<MountPoint>();
		}
		
		return mountpointsPerFqan.get(fqan);
	}

	/**
	 * Calculates the first available
	 * @param fqan
	 * @return
	 */
	public String getFirstStagingFileSystem(String fqan) {

		Set<MountPoint> mps = em.getMountPointsForSubmissionLocationAndFqan(location, fqan);
		
//		for ( MountPoint mp : em.getMountPointsForSubmissionLocationAndFqan(location, fqan) ) {
//			if ( mp.getFqan() == null && fqan == null ) {
//				return mp.getRootUrl();				
//			} else if ( mp.getFqan() != null ) {
//				if ( mp.getFqan().equals(fqan) ) {
//					return mp.getRootUrl();
//				}
//			}
//		}
		
		try {
			String fs = mps.iterator().next().getRootUrl();
			return fs;
		} catch (Exception e) {
			myLogger.debug("No staging filesystem found for fqan: "+fqan);
			return null;
		}
		
	}
}
