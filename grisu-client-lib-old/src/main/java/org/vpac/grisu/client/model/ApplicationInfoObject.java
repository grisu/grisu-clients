package org.vpac.grisu.client.model;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;

import au.org.arcs.jcommons.constants.Constants;

/**
 * Just a wrapper object to make info handling easier for an applications and
 * it's versions.
 * 
 * @author Markus Binsteiner
 * 
 */
public class ApplicationInfoObject implements SubmissionObject, FqanListener,
		MountPointsListener {

	static final Logger myLogger = Logger.getLogger(ApplicationInfoObject.class
			.getName());

	public final static String DEFAULT_VERSION_STRING = "default";

	public final static int DEFAULT_VERSION_MODE = 0;
	public final static int ANY_VERSION_MODE = 1;
	public final static int EXACT_VERSION_MODE = 2;

	private final EnvironmentManager em;
	private final String application;

	private int mode = -1;
	private final int initial_mode;
	private String fixedVersion = null;
	private Set<String> currentlyAvailableVersions = null;
	private Set<SubmissionLocation> currentSubLocs = null;

	private SubmissionLocation currentSubmissionLocation = null;

	private int preferredExecutableType = EXECUTABLE_TYPE_UNDEFINED;

	private Map<String, Map<String, String>> detailsCache = new TreeMap<String, Map<String, String>>();

	// public ApplicationInfoObject(EnvironmentManager em, String app) {
	// this(em, app, ANY_VERSION_MODE);
	// }

	public ApplicationInfoObject(EnvironmentManager em, String app,
			int initial_mode_temp) {

		this.em = em;
		this.application = app;
		this.initial_mode = initial_mode_temp;
		init(initial_mode_temp);
	}

	private void init(int initial_mode_temp) {
		this.mode = initial_mode;

		// fetching all available versions in the background
		new Thread() {
			public void run() {
				getAllAvailableVersions();
			}
		}.start();
	}

	// public Set<String> getCurrentlyAvailableVersions() {
	// return currentlyAvailableVersions;
	// }

	/**
	 * Sets the mode for this info object. Set it to either:
	 * {@link #DEFAULT_VERSION_MODE}, {@link #ANY_VERSION_MODE},
	 * {@link #EXACT_VERSION_MODE}.
	 * 
	 * @param mode
	 *            the mode
	 * @param version
	 *            the version, if the mode is {@link #EXACT_VERSION_MODE}, if
	 *            you use {@link #ANY_VERSION_MODE} you can set a preferred
	 *            version or null, for {@link #DEFAULT_VERSION_MODE} this will
	 *            be ignored.
	 * @throws ModeNotSupportedException
	 *             if there are no submission locations for this combination of
	 *             mode and version
	 */
	public void setMode(int mode, String version)
			throws ModeNotSupportedException {

		Set<SubmissionLocation> tempSubLocs = null;
		this.mode = mode;
		switch (mode) {
		case DEFAULT_VERSION_MODE:
			tempSubLocs = getSubmissionLocationsForDefaultVersion();
			currentlyAvailableVersions = new TreeSet<String>();
			currentlyAvailableVersions.add(DEFAULT_VERSION_STRING);
			setVersion(DEFAULT_VERSION_STRING);
			break;
		case ANY_VERSION_MODE:
			tempSubLocs = getSubmissionLocationsForAnyVersion();
			currentlyAvailableVersions = new TreeSet<String>();
			setVersion(null);
			break;
		case EXACT_VERSION_MODE:
			if ( version == null ) {
				if (this.currentSubmissionLocation != null) {
					try {
						this.fixedVersion = em
								.getAllVersionsForApplicationAtSubmissionLocation(
										application,
										this.currentSubmissionLocation, em
												.getDefaultFqan())[0];
					} catch (Exception e) {
						myLogger
								.warn("No version for application on SubmissionLocation "
										+ currentSubmissionLocation.toString()
										+ " available.");
					}
				}

				if (this.fixedVersion == null) {
					try {
						this.fixedVersion = em
								.getAllAvailableVersionsForApplication(
										application, em.getDefaultFqan())
								.iterator().next();
					} catch (NoSuchElementException e) {
						myLogger.warn("No version for application "
								+ application + " available.");
					}
				}
			}
			tempSubLocs = getSubmissionLocationsForExactVersion(version);
			currentlyAvailableVersions = new TreeSet<String>();
			currentlyAvailableVersions.add(version);
			setVersion(version);
			break;
		default:
			throw new ModeNotSupportedException(mode);
		}

		// if ( tempSubLocs.size() == 0 ) {
		// throw new ModeNotSupportedException(mode);
		// }

		this.currentSubLocs = tempSubLocs;
//		this.mode = mode;

	}

	public String getRecommendedVersionForSubmissionLocation(
			SubmissionLocation subLoc, String fqan) {

		String[] allVersions = em
				.getAllVersionsForApplicationAtSubmissionLocation(
						getCurrentApplicationName(), subLoc, fqan);

		if (allVersions == null || allVersions.length == 0) {
			return null;
		} else {
			String recommendedVersion = allVersions[0];
			// TODO here comes the matchmaker
			return recommendedVersion;
		}
	}

	public void setVersion(String version) {

		this.fixedVersion = version;

		if (this.mode == EXACT_VERSION_MODE
				|| this.mode == DEFAULT_VERSION_MODE) {
			
			currentSubLocs = getSubmissionLocationsForExactVersion(version);
		} else if (this.mode == ANY_VERSION_MODE) {
			fixedVersion = null;
		}
	}

	public Set<SubmissionLocation> getCurrentlyPossibleSubmissionLocations() {
		return currentSubLocs;
	}

	public Set<String> getCurrentlyPossibleSites() {

		Set<String> result = new TreeSet<String>();
		for (SubmissionLocation subLoc : getCurrentlyPossibleSubmissionLocations()) {
			result.add(subLoc.getSite());
		}
		return result;

	}

	public Set<SubmissionLocation> getCurrentlyPossibleSubmissionLocationsForSite(
			String site) {
		Set<SubmissionLocation> result = new TreeSet<SubmissionLocation>();
		for (SubmissionLocation subLoc : getCurrentlyPossibleSubmissionLocations()) {

			if (subLoc.getSite().equals(site)) {
				result.add(subLoc);
			}
		}
		return result;
	}

	private Set<SubmissionLocation> getSubmissionLocationsForAnyVersion() {

		return em.getAllAvailableSubmissionLocationsForApplication(
				getCurrentApplicationName(), em.getDefaultFqan());
	}

	private Set<SubmissionLocation> getSubmissionLocationsForDefaultVersion() {

		return em.getAllAvailableSubmissionLocationsForApplicationAndVersion(
				getCurrentApplicationName(), DEFAULT_VERSION_STRING, em
						.getDefaultFqan());
	}

	private Set<SubmissionLocation> getSubmissionLocationsForExactVersion(
			String version) {
		return em.getAllAvailableSubmissionLocationsForApplicationAndVersion(
				getCurrentApplicationName(), version, em.getDefaultFqan());
	}

	public synchronized Set<String> getAllAvailableVersions() {
		return em.getAllAvailableVersionsForApplication(
				getCurrentApplicationName(), em.getDefaultFqan());
	}

	// submissionObject properties

	public String getCurrentApplicationName() {
		return application;
	}

	public Map<String, String> getCurrentApplicationDetails() {

		String tempCurrentVersion = getCurrentVersion();
		SubmissionLocation tempCurrentLocation = getCurrentSubmissionLocation();

		if (getCurrentVersion() == null || "".equals(tempCurrentVersion)
				|| tempCurrentLocation == null) {
			return null;
		}

		if (detailsCache.get(tempCurrentVersion + "_"
				+ tempCurrentLocation.getLocation()) == null) {
			// lookup executables
			Map<String, String> tempDetails = em.getServiceInterface()
					.getApplicationDetailsForVersionAndSite(getCurrentApplicationName(),
							tempCurrentVersion, tempCurrentLocation.getSite()).getDetailsAsMap();
			detailsCache.put(tempCurrentVersion + "_"
					+ tempCurrentLocation.getLocation(), tempDetails);
		}
		return detailsCache.get(tempCurrentVersion + "_"
				+ tempCurrentLocation.getLocation());

	}

	public String getCurrentVersion() {

		if (this.mode == DEFAULT_VERSION_MODE
				|| this.mode == EXACT_VERSION_MODE || this.fixedVersion == null) {

			return this.fixedVersion;

		} else if (this.mode == ANY_VERSION_MODE) {

			SubmissionLocation tempSubLoc = getCurrentSubmissionLocation();
			if (tempSubLoc == null) {
				return null;
			} else {
				String tempVersion = getRecommendedVersionForSubmissionLocation(
						tempSubLoc, em.getDefaultFqan());
				return tempVersion;
			}
		} else {
			throw new RuntimeException("Mode not supported: " + this.mode);
		}

	}

	public String[] getCurrentExecutables() {
		return getCurrentApplicationDetails().get(
				Constants.MDS_EXECUTABLES_KEY).split(",");
	}

	public SubmissionLocation getCurrentSubmissionLocation() {

		if (this.currentSubmissionLocation == null) {
			if (getCurrentlyPossibleSubmissionLocations() == null
					|| getCurrentlyPossibleSubmissionLocations().size() == 0) {
				// throw new NoPossibleSubmissionLocation(this.application,
				// null, em.getDefaultFqan());
				return null;
			}
			// TODO choose the best option,not just the first one in the list
			this.currentSubmissionLocation = getCurrentlyPossibleSubmissionLocations()
					.iterator().next();
		}
		return this.currentSubmissionLocation;
	}

	public String[] getCurrentModules() {
		return getCurrentApplicationDetails().get(Constants.MDS_MODULES_KEY)
				.split(",");
	}

	public int getPreferredExecutableType() {
		return preferredExecutableType;
	}

	public void setPreferredExecutableType(int type) {
		this.preferredExecutableType = type;
	}

	public void setCurrentSubmissionLocation(SubmissionLocation location)
			throws SubmissionLocationException {

		if (!getCurrentlyPossibleSubmissionLocations().contains(location)) {
			throw new SubmissionLocationException(location);
		} else {
			this.currentSubmissionLocation = location;
		}

	}

	public void fqansChanged(FqanEvent event) {
		clearCalculatedStuff();
	}

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {
		clearCalculatedStuff();
	}

	private void clearCalculatedStuff() {
		init(initial_mode);
	}

}
