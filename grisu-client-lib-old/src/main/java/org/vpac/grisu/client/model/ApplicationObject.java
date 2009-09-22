package org.vpac.grisu.client.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;

import au.org.arcs.jcommons.constants.Constants;

public class ApplicationObject implements SubmissionObject, FqanListener,
		MountPointsListener {

	static final Logger myLogger = Logger.getLogger(ApplicationObject.class
			.getName());


	
	private String applicationName = null;
	private Set<String> sitesInQuestion = null;
	private Set<String> actualAvailableSites = null;
	private Map<String, Set<SubmissionLocation>> actualAvailableSubmissionLocations = new TreeMap<String, Set<SubmissionLocation>>();
	private Map<String, Set<SubmissionLocation>> submissionLocationsPerSite = new HashMap<String, Set<SubmissionLocation>>();
	private Map<SubmissionLocation, Set<VersionObject>> versions = null;
	private Map<String, String> locationsPerVersion = null;
	private EnvironmentManager em = null;
	private ServiceInterface serviceInterface = null;

	private int preferredExecutableType = EXECUTABLE_TYPE_UNDEFINED;
	
//	private String[] submissionLocations = null;

	private SubmissionLocation currentSubmissionLocation = null;
	// private String currentVersion = null;

	private Map<String, Map<String, String>> detailsCache = new TreeMap<String, Map<String, String>>();

	public ApplicationObject(String appName, Set<String> sitesInQuestion,
			EnvironmentManager em) {
		this.applicationName = appName;
		this.sitesInQuestion = sitesInQuestion;
		this.em = em;
		this.serviceInterface = em.getServiceInterface();
		this.em.addFqanListener(this);
		this.em.addMountPointListener(this);
	}

	public String toString() {
		return applicationName;
	}

	/**
	 * Returns all versions for all of the users sites.
	 * 
	 * @return all versions of this application
	 */
	public VersionObject[] getVersionsForAllSites() {

		if (versions == null) {
			calculateVersions();
		}

		Set<VersionObject> allVersions = new TreeSet<VersionObject>();
		// for (String site : versions.keySet() ) {
		// for (Set<VersionObject> versionList : versions.get(site).values()) {
		// if (versionList != null && versionList.size() > 0) {
		// for (VersionObject version : versionList) {
		// allVersions.add(version);
		// }
		// }
		// }
		// }

		for (String ver : locationsPerVersion.keySet()) {
			// for every location
			for (String loc : locationsPerVersion.get(ver).split(",")) {
				if (sitesInQuestion.contains(em.getSubmissionLocation(loc)
						.getSite())) {
					allVersions.add(new VersionObject(this, ver));
				}
			}
		}

		return allVersions.toArray(new VersionObject[] {});
	}

	/**
	 * Returns all versions for all of the users site that are accessible with
	 * this fqan
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the versions
	 */
	public VersionObject[] getVersionsForAllSites(String fqan) {

		if (versions == null) {
			calculateVersions();
		}

		String[] allAvailSitesForFqan = em.getAllOfTheUsersSites(fqan);

		Set<VersionObject> allVersionsForVO = new TreeSet<VersionObject>();

		for (String ver : locationsPerVersion.keySet()) {
			for (String loc : locationsPerVersion.get(ver).split(",")) {
				String site = em.lookupSite(EnvironmentManager.QUEUE_TYPE, loc);
				if ( site != null && !"".equals(site) ) {
				if (Arrays.binarySearch(allAvailSitesForFqan, site) >= 0) {
					allVersionsForVO.add(new VersionObject(this, ver));
					continue;
				}
				}
			}
		}

		// for ( String site : versions.keySet() ) {
		// for ( SubmissionLocation subLoc : versions.get(site).keySet() ) {
		// if ( Arrays.binarySearch(allAvailSitesForFqan, subLoc.getSite()) >= 0
		// ) {
		// allVersionsForVO.addAll(versions.get(site).get(subLoc));
		// }
		// }
		// }
		return allVersionsForVO.toArray(new VersionObject[] {});
	}

	private void calculateVersions() {

		versions = new TreeMap<SubmissionLocation, Set<VersionObject>>();
		actualAvailableSites = new TreeSet<String>();

		locationsPerVersion = serviceInterface
				.getSubmissionLocationsPerVersionOfApplication(applicationName).getSubmissionLocationsPerVersionMap();

		for (String ver : locationsPerVersion.keySet()) {

			for (String loc : locationsPerVersion.get(ver).split(",")) {
				
				if ( loc != null && !"".equals(loc) ) {
				SubmissionLocation tempSubLoc = em.getSubmissionLocation(loc);
				if (tempSubLoc != null) {
					Set<VersionObject> tempVerList = versions.get(tempSubLoc);
					if (tempVerList == null) {
						tempVerList = new TreeSet<VersionObject>();
						versions.put(tempSubLoc, tempVerList);
					}

					tempVerList.add(new VersionObject(this, ver));
				}
				}
			}

		}

	}

	public Set<VersionObject> getVersionsForSite(String site) {

		if (versions == null) {
			calculateVersions();
		}

		Set<VersionObject> result = new TreeSet<VersionObject>();

		for (SubmissionLocation subLoc : versions.keySet()) {
			if (em.lookupSite(EnvironmentManager.QUEUE_TYPE, subLoc.getSite())
					.equals(site)) {
				result.addAll(versions.get(subLoc));
			}
		}

		return result;

	}

	public Set<VersionObject> getVersionsForSubmissionLocation(
			SubmissionLocation subLoc) {

		if (versions == null) {
			calculateVersions();
		}

		return versions.get(subLoc);
	}

	// public Set<String> getSitesForVersion(VersionObject versionObj) {
	//		
	// if ( versions == null ) {
	// calculateVersions();
	// }
	// Set<String> sites = new TreeSet<String>();
	//		
	// for ( String site : versions.keySet() ) {
	// if ( versions.get(site).contains(versionObj) ) {
	// sites.add(site);
	// }
	// }
	// return sites;
	// }

	public String getCurrentApplicationName() {
		return applicationName;
	}

	public Set<String> getSitesInQuestion() {
		return sitesInQuestion;
	}

	public Map<SubmissionLocation, Set<VersionObject>> getVersions() {
		return versions;
	}

	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	// public String[] getSubmissionLocations() {
	//
	// if (versions == null) {
	// calculateVersions();
	// }
	//
	// if (submissionLocations == null) {
	// submissionLocations = serviceInterface
	// .getSubmissionLocationsForApplication(applicationName);
	// }
	// return submissionLocations;
	// }

	public Set<SubmissionLocation> getSubmissionLocationsForSite(String site) {
		
		if ( submissionLocationsPerSite.get(site) == null ) {

		Set<SubmissionLocation> locations = new TreeSet<SubmissionLocation>();

		for (SubmissionLocation subLoc : em
				.getAllAvailableSubmissionLocationsForApplication(applicationName)) {
			if (site.equals(subLoc.getSite())) {
				if (isAvailable(subLoc)) {
					locations.add(subLoc);
				}

			}
		}

			submissionLocationsPerSite.put(site, locations);
		}
		return submissionLocationsPerSite.get(site);
	}
	
	public int getPreferredExecutableType() {
		return preferredExecutableType;
	}

	public void setPreferredExecutableType(int type) {
		this.preferredExecutableType = type;
	}

	// that should be enough, hopefully
	public boolean equals(Object otherObject) {
		if (otherObject instanceof ApplicationObject) {
			ApplicationObject other = (ApplicationObject) otherObject;
			if (applicationName.equals(applicationName)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// that should be enough, hopefully
	public int hashCode() {
		return applicationName.hashCode();
	}

	// public Set<String> getActualAvailableSites() {
	// if (versions == null ) {
	// calculateVersions();
	// }
	// return actualAvailableSites;
	// }

	/**
	 * Calculates all available submissionlocations for the current application
	 * and specified fqan
	 * 
	 * @param fqan
	 *            the fqan
	 * @return a set of possible submission locations
	 */
	public Set<SubmissionLocation> getActualAvailableSubmissionLocations(
			String fqan) {
		if (versions == null) {
			calculateVersions();
		}
		
		if ( fqan == null ) {
			fqan = Constants.NON_VO_FQAN;
		}

		if (actualAvailableSubmissionLocations.get(fqan) == null) {
			Set<SubmissionLocation> tempSet = em
					.getAllAvailableSubmissionLocationsForApplication(applicationName);
			Set<SubmissionLocation> result = new TreeSet<SubmissionLocation>();
			for (SubmissionLocation subLoc : tempSet) {
				Set<SubmissionLocation> tempLocs = em.getAllAvailableSubmissionLocationsForFqan(fqan);
				if ( tempLocs.contains(subLoc) ) {
//				if (em.getAllAvailableSubmissionLocationsForFqan(fqan)
//						.contains(subLoc)) {
					actualAvailableSites.add(subLoc.getSite());
					result.add(subLoc);
				}
			}
			actualAvailableSubmissionLocations.put(fqan, result);
		}

		return actualAvailableSubmissionLocations.get(fqan);
	}

	public Set<String> getActualAvailableSites(String fqan) {
		if (versions == null) {
			calculateVersions();
		}

		// just to be sure it the cache is filled for this fqan
		getActualAvailableSubmissionLocations(fqan);

		Set<String> result = new TreeSet<String>();
		String[] availSites = em.getAllOfTheUsersSites(fqan);

		for (String site : actualAvailableSites) {
			if (Arrays.binarySearch(availSites, site) >= 0) {
				result.add(site);
			}
		}
		return result;
	}

	public EnvironmentManager getEnvironmentManager() {
		return em;
	}

	public SubmissionLocation getCurrentSubmissionLocation() {
		return currentSubmissionLocation;
	}

	public void setCurrentSubmissionLocation(
			SubmissionLocation currentSubmissionLocation)
			throws SubmissionLocationException {

		if (versions == null) {
			calculateVersions();
		}

		if (isAvailable(currentSubmissionLocation)) {
			this.currentSubmissionLocation = currentSubmissionLocation;
		} else {
			throw new SubmissionLocationException("SubmissionLocation "
					+ currentSubmissionLocation.getLocation()
					+ " doesn't support application: " + applicationName);
		}
	}

	private boolean isAvailable(SubmissionLocation loc) {

		for (String fqan : actualAvailableSubmissionLocations.keySet()) {
			if (actualAvailableSubmissionLocations.get(fqan).contains(loc)) {
				return true;
			}
		}
		return false;
	}

	// public SubmissionLocation submissionLocation

	// public void setCurrentVersion(String version) {
	// this.currentVersion = version;
	// }

	public String getCurrentVersion() {

		if (versions == null) {
			calculateVersions();
		}

		if (this.currentSubmissionLocation == null)
			return null;

		// calculate which version to use for this location
		// String currentSite = currentSubmissionLocation.getSite();

		// Map<SubmissionLocation, Set<VersionObject>> tempMap =
		// versions.get(this.currentSubmissionLocation);

		Set<VersionObject> resultSet = versions.get(currentSubmissionLocation);

		if (resultSet == null) {
			myLogger.error("Couldn't find versions for " + applicationName
					+ " for submissionLocation: " + currentSubmissionLocation);
			return null;
		}

		myLogger.debug("Calculated version to use: "
				+ resultSet.iterator().next().getCurrentVersion());
	
		return resultSet.iterator().next().getCurrentVersion();
	}

	public Map<String, String> getDetails(SubmissionLocation loc, String version) {
		if (detailsCache.get(version + loc.getLocation()) == null) {
			// lookup executables
			Map<String, String> tempDetails = serviceInterface
					.getApplicationDetailsForVersionAndSubmissionLocation(applicationName, version, loc
							.getLocation()).getDetailsAsMap();
			detailsCache.put(version + loc.getLocation(), tempDetails);
		}

		return detailsCache.get(version + loc.getLocation());
	}
	
	public Map<String, String> getCurrentApplicationDetails(){
		
		if (versions == null) {
			calculateVersions();
		}

		if (this.currentSubmissionLocation == null)
			return null;
		return getDetails(this.currentSubmissionLocation, this.getCurrentVersion());
	}

	public String[] getCurrentExecutables() {

		if (this.currentSubmissionLocation == null) {
			myLogger.error("No submission location selected. Returning null.");
			return null;
		}

		String version = getCurrentVersion();
		if (version == null) {
			myLogger.error("No version found. Returning null.");
			return null;
		}

		return getDetails(this.currentSubmissionLocation, version).get(
				Constants.MDS_EXECUTABLES_KEY).split(",");
	}

	// public ApplicationObject getApplication() {
	// return this;
	// }

	public String[] getCurrentModules() {

		if (this.currentSubmissionLocation == null) {
			myLogger.error("No submission location selected. Returning null.");
			return null;
		}

		String version = getCurrentVersion();
		if (version == null) {
			myLogger.error("No version found. Returning null.");
			return null;
		}

		return getDetails(this.currentSubmissionLocation, version).get(
				Constants.MDS_MODULES_KEY).split(",");
	}

	public void fqansChanged(FqanEvent event) {
		// this.versions = null;
		this.currentSubmissionLocation = null;
	}

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {
		this.versions = null;
		this.currentSubmissionLocation = null;
		this.submissionLocationsPerSite.clear();
		this.actualAvailableSubmissionLocations.clear();
		calculateVersions();
	}



}
