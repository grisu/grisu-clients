package org.vpac.grisu.client.control;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.files.FileManager;
import org.vpac.grisu.client.control.files.FileTransferManager;
import org.vpac.grisu.client.control.files.MountPointHelpers;
import org.vpac.grisu.client.control.jobs.JobManager;
import org.vpac.grisu.client.control.status.ApplicationStatusManager;
import org.vpac.grisu.client.control.template.TemplateManager;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.helpDesk.model.Person;
import org.vpac.helpDesk.model.PersonException;
import org.vpac.historyRepeater.DummyHistoryManager;
import org.vpac.historyRepeater.HistoryManager;

import au.org.arcs.jcommons.constants.Constants;

/**
 * This class manages the important properties of the user like MountPoints &
 * fqans. For example, there is a method to probe one (or more) site(s) for
 * usable VOs/VO file shares.
 * 
 * Also, it manages all listeners that may be interested when a mountpoint/fqan
 * is added/removed/changed.
 * 
 * @author Markus Binsteiner
 * 
 */
public class EnvironmentManager implements MountPointsListener, UserEnvironmentManager {
	
	public String getCurrentFqan() {
		return getDefaultFqan();
	}

	public void setCurrentFqan(String currentFqan) {
		setDefaultFqan(currentFqan);
	}

	static final Logger myLogger = Logger.getLogger(EnvironmentManager.class
			.getName());

	ApplicationStatusManager statusManager = ApplicationStatusManager
			.getDefaultManager();

	public static final String GRISU_HISTORY_FILENAME = "grisu.history";

	public static final int QUEUE_TYPE = 0;
	public static final int FILE_URL_TYPE = 1;

	private Map<String, String> alreadyQueriedHosts = new HashMap<String, String>();
	private Map<String, String[]> alreadyQueriedQueues = new HashMap<String, String[]>();
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerSubmissionLocation = new TreeMap<String, Set<MountPoint>>();
	private Map<String, Set<MountPoint>> alreadyQueriedMountPointsPerFqan = new TreeMap<String, Set<MountPoint>>();

	private Map<String, SubmissionLocation> allSubmissionLocations = null;
	private Map<String, Set<SubmissionLocation>> allAvailableSubmissionLocationsPerFqan = new HashMap<String, Set<SubmissionLocation>>();
	private Map<String, Set<SubmissionLocation>> allAvailableSubmissionLocationsForApplication = new TreeMap<String, Set<SubmissionLocation>>();
	private Map<String, Set<SubmissionLocation>> allAvailableSubmissionLocationsForApplicationNew = new TreeMap<String, Set<SubmissionLocation>>();
	private Map<String, Set<SubmissionLocation>> allAvailableSubmissionLocationsForApplicationAndVersion = new TreeMap<String, Set<SubmissionLocation>>();
	private Map<String, Set<SubmissionLocation>> allAvailableSubmissionLocationsForApplicationAndVersionNew = new TreeMap<String, Set<SubmissionLocation>>();
	private Map<String, Set<String>> allAvailableVersionsForApplication = new HashMap<String, Set<String>>();
	private Map<String, String[]> allAvailableVersionsForApplicationPerSubmissionLocation = new HashMap<String, String[]>();

	// if this is not initialized nothing is gonna be displayed
	// public static ProgressDisplay progressDisplay = new DummyDisplay();

	private Person user = null;

	// all fqans of the user
	private String[] fqans = null;

	// all fqans that actually are used (aka have got a mountpoint)
	private Set<String> allUsedFqans = new TreeSet<String>();

	// the currently selected fqan
	private String defaultFqan = null;

	// all mountpoints of the user
	private MountPoint[] mountPoints = null;
	private SortedSet<String> sites = null;

	// all sites of the grid. This info is not really useful since it doesn't
	// tell you whether the user has got access to all of them or not...
	private String[] allGridSites = null;

	// the ServiceInterface to use
	private ServiceInterface serviceInterface = null;

	// the filesystems (the cached, local version as xml documents)
	private FileManager fileManager = null;

	// private JobManagementInterface jobManagement = null;

	private JobManager glazedJobManagement = null;

	private TemplateManager templateManager = null;

	private HistoryManager historyManager = new DummyHistoryManager();

	private FileTransferManager fileTransferManager = new FileTransferManager();

	// the "system-wide" serviceInterface
	// private static ServiceInterface defaultServiceInterface = null;

	// the "system-wide" EnvironmentManager
	// private static EnvironmentManager defaultManager = null;

	/**
	 * After a user successfully logs into a grisu web service this
	 * ServiceInterace has to be set in order to do grid stuff using grisu.
	 * 
	 * @param serviceInterface
	 *            the right ServiceInterface
	 */
	// public static void setDefaultServiceInterface(
	// ServiceInterface serviceInterface) {
	// defaultServiceInterface = serviceInterface;
	// }
	// /**
	// * Gets the {@link ServiceInterface} that is used in this session.
	// *
	// * @return the ServiceInterface
	// */
	// public static ServiceInterface getDefaultServiceInterface() {
	// return defaultServiceInterface;
	// }
	public void initializeHistoryManager() {

//		File historyFile = new File(Environment.GRISU_DIRECTORY,
//				GRISU_HISTORY_FILENAME);
//		if (!historyFile.exists()) {
//			try {
//				historyFile.createNewFile();
//
//			} catch (IOException e) {
//				// well
//			}
//		}
//		if (!historyFile.exists()) {
//			setHistoryManager(new DummyHistoryManager());
//		} else {
//			setHistoryManager(new SimpleHistoryManager(historyFile));
//		}
		
		setHistoryManager(GrisuRegistryManager.getDefault(serviceInterface).getHistoryManager());
	}

	/**
	 * The EnvironmentManager that is used in this session
	 * 
	 * @return the EnvironmentManager
	 */
	// synchronized public static EnvironmentManager getDefaultManager() {
	//
	// if (defaultManager == null) {
	// defaultManager = new EnvironmentManager(defaultServiceInterface);
	// // try {
	// // defaultManager.setFileManager(new FileManager(defaultManager
	// // .getMountPoints(), defaultServiceInterface));
	// // } catch (Exception e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // defaultManager.addMountPointListener(defaultManager
	// // .getFileManager());
	// //
	// // // set the default jobmanager
	// // defaultManager.setJobManagement(new JobManagement(
	// // defaultManager));
	// //
	// // defaultManager.setTemplateManager(new
	// // TemplateManager(defaultManager));
	//
	// // defaultManager.setHistoryManager(new DummyHistoryManager());
	// File historyFile = new File(Environment.GRISU_DIRECTORY,
	// GRISU_HISTORY_FILENAME);
	// if (!historyFile.exists()) {
	// try {
	// historyFile.createNewFile();
	//
	// } catch (IOException e) {
	// // well
	// }
	// }
	// if (!historyFile.exists()) {
	// defaultManager.setHistoryManager(new DummyHistoryManager());
	// } else {
	// defaultManager.setHistoryManager(new SimpleHistoryManager(
	// historyFile));
	// }
	//
	// }
	// return defaultManager;
	// }
	public EnvironmentManager(ServiceInterface serviceInterface) {
		this(serviceInterface, true);
	}

	/**
	 * The default constructor
	 * 
	 * @param serviceInterface
	 *            the ServiceInterface for this session
	 */
	public EnvironmentManager(ServiceInterface serviceInterface,
			boolean includeLocalFileSystems) {

		statusManager
				.setCurrentStatus("Getting all hostnames within the grid to build info cache...");
		alreadyQueriedHosts = serviceInterface.getAllHosts().asMap();

		statusManager.setCurrentStatus("Connecting service interface...");
		this.serviceInterface = serviceInterface;
		setDefaultFqan(ClientPropertiesManager.getLastUsedFqan());
		statusManager.setCurrentStatus("Setting up job management...");
		this.setJobManager(new JobManager(this));
		statusManager.setCurrentStatus("Setting up file management...");
		this.setFileManager(new FileManager(this.getMountPoints(), this,
				includeLocalFileSystems));
		// this.getFileManager().initAllFileSystemsInBackground();
		this.addMountPointListener(this);
		this.addMountPointListener(this.getFileManager());

		// this.setJobManagement(new SimpleJobManagementImpl(this));
		statusManager.setCurrentStatus("Setting up template management...");
		this.setTemplateManager(new TemplateManager(this));

	}

	public Person getUser() {
		if (user == null) {
			String dn = null;
			String realname = null;
			try {
				realname = ClientPropertiesManager.getClientConfiguration()
						.getString(Person.REALNAME_KEY);
			} catch (ConfigurationException e2) {
				// doesn't matter
				myLogger.warn("Could not read config file: "
						+ e2.getLocalizedMessage());
			}
			if (realname == null || "".equals(realname)) {
				dn = getServiceInterface().getDN();
				int indexcn = dn.toLowerCase().indexOf("cn=");

				if (indexcn == -1) {
					realname = dn.replace("/", "_").replace("=", "_");
				} else {
					realname = dn.substring(indexcn + 3);
				}
			}
			try {
				user = new Person(ClientPropertiesManager
						.getClientConfiguration(), realname);
			} catch (Exception e) {
				try {
					user = new Person("Anonymous");
				} catch (PersonException e1) {
					// this should never happen
					e1.printStackTrace();
				}
			}
			user.setRole(Person.USER_ROLE);
			if (user.getDn() == null) {
				user.setDn(getServiceInterface().getDN());
			}
		}
		return user;
	}

	/**
	 * Converts a url like "/home.vpac.no_vo/file.txt" to something like
	 * "gsiftp://ngdata.vpac.org/home/san04/markus/file.txt" by checking all the
	 * users' {@link MountPoint}s.
	 * 
	 * @param file
	 *            the files' "relative" url
	 * @return the files' "absolute" url
	 */
	public String convertToAbsoluteUrl(String file) {

		for (MountPoint mp : getMountPoints()) {
			if (mp.isResponsibleForUserSpaceFile(file)) {
				return mp.replaceMountpointWithAbsoluteUrl(file);
			}
		}
		return null;
	}

	/**
	 * Unmounts the specified {@link MountPoint}.
	 * 
	 * @param mp
	 *            the mountpoint
	 */
	public synchronized void umount(MountPoint mp) {
		serviceInterface.umount(mp.getAlias());

		fireMountPointsEvent(MountPointEvent.MOUNTPOINT_REMOVED,
				new MountPoint[] { mp });
	}

	/**
	 * Mounts the specified url to an easy-to-read name (called mountpoint).
	 * 
	 * @param url
	 *            the url (e.g.: gsiftp://ngdata.vpac.org/home/san04/markus)
	 * @param mountPoint
	 *            (e.g.: /home.vpac.no_vo)
	 * @param useHomeDirectoryOnThisFileSystemIfPossible
	 *            if true the web service tries to automatically determine the
	 *            users home directory on this filesystem and use it as url
	 * @throws RemoteFileSystemException
	 *             if the remote filesystem can't be accessed
	 */
	public synchronized void mount(String url, String mountPoint,
			boolean useHomeDirectoryOnThisFileSystemIfPossible)
			throws RemoteFileSystemException {

		MountPoint mp = serviceInterface.mountWithoutFqan(url, mountPoint,
				useHomeDirectoryOnThisFileSystemIfPossible);

		fireMountPointsEvent(MountPointEvent.MOUNTPOINT_ADDED,
				new MountPoint[] { mp });
	}

	public synchronized void mount(String url, String mountPoint, String fqan,
			boolean useHomeDirectoryOnThisFileSystemIfPossible)
			throws RemoteFileSystemException {
		if (Constants.NON_VO_FQAN.equals(fqan)) {
			fqan = null;
		}
		MountPoint mp = serviceInterface.mount(url, mountPoint, fqan,
				useHomeDirectoryOnThisFileSystemIfPossible);

		fireMountPointsEvent(MountPointEvent.MOUNTPOINT_ADDED,
				new MountPoint[] { mp });
	}

	// /**
	// * This methods goes through the provided list of sites and tries to check
	// * every possible fqan of the user to access the default filesystem of the
	// * site. If that is successful, the default filesystem (using the users
	// home
	// * directory as endpoint) is mounted for the user.
	// * @deprecated use setUpMappedHomeDirectories from now on
	// *
	// * @param sites
	// */
	// public void setUpHomeDirectories(String[] sites) {
	//
	// String[] fqans = serviceInterface.getFqans();
	//
	// progressDisplay.start(sites.length * (fqans.length + 2),
	// "Probing sites...");
	// for (int i = 0; i < sites.length; i++) {
	// setUpHomeDirectories(sites[i], fqans, i);
	// }
	// progressDisplay.close();
	//
	// }

	// public void setUpMappedHomeDirectories(String[] sites) {
	//		
	// getAllSubmissionLocations()
	//		
	// }

	// helper method for the above one. it exists mainly to enable the
	// progressDisplay to work properly...
	// private void setUpHomeDirectories(String site, String[] fqans,
	// int currentSite) {
	//
	// // first, try the plain proxy
	// try {
	// progressDisplay.setProgress(currentSite * (fqans.length + 1) + 1,
	// "Site: " + site + ". Probing plain proxy.");
	// // mount the homedirectory of the group to something like
	// // /vpac.home.NGAdmin
	// serviceInterface.mount(serviceInterface.getStagingFileSystem(site),
	// "/" + site.toLowerCase() + ".home." + "no_vo", true);
	// } catch (Exception e3) {
	// // not that bad. that only means this particular vo does not have a
	// // homedirectory
	// myLogger.error("Could not mount homedirectory for plain proxy.");
	// }
	//
	// // the following is not necessary anymore because we will use mds
	// // for that now..
	//		
	// // try to mount every filesystem of every vo.
	// // while we're at it, let's repopulate the fqans of the gui
	// int progress = 0;
	// for (String fqan : fqans) {
	// // gui housekeeping
	// try {
	// progress++;
	// // mount the homedirectory of the group to something like
	// // /vpac.home.NGAdmin
	// progressDisplay.setProgress(currentSite * (fqans.length + 1)
	// + 1 + progress, "Site: " + site
	// + ". Probing VOMS proxy for: " + fqan);
	// serviceInterface.mount(serviceInterface
	// .getStagingFileSystem(site), "/" + site.toLowerCase()
	// + ".home." + FqanHelpers.getLastGroupPart(fqan), fqan,
	// true);
	// } catch (Exception e3) {
	// // not that bad. that only means this particular vo does not
	// // have a homedirectory
	// myLogger.error("Could not mount homedirectory for fqan: "
	// + fqan);
	// }
	// }
	// mountPoints = null;
	//
	// progressDisplay.setProgress(currentSite * (fqans.length + 1) + 1
	// + progress + 1, "Site: " + site
	// + ". Checking for duplicate mountpoints.");
	// // now check for duplicate mountpoints
	// for (MountPoint mp : getMountPoints()) {
	//
	// if (MountPointHelpers.getSiteFromMountPointUrl(
	// mp.getRootUrl().toLowerCase()).equals(site.toLowerCase())
	// && mp.getFqan() == null) {
	//
	// ArrayList<MountPoint> duplicates = new ArrayList<MountPoint>();
	//
	// for (MountPoint mp_compare : getMountPoints()) {
	// if (!mp.equals(mp_compare)) {
	//
	// if (mp_compare.getRootUrl().startsWith(mp.getRootUrl())) {
	// serviceInterface.umount(mp.getMountpoint());
	// break;
	// }
	// }
	// }
	//
	// }
	// }
	//
	// fireMountPointsEvent(MountPointEvent.MOUNTPOINTS_REFRESHED,
	// getMountPoints());
	// }

	/**
	 * Get all the users' mountpoints.
	 * 
	 * @return all mountpoints
	 */
	public synchronized MountPoint[] getMountPoints() {
		if (mountPoints == null) {
			mountPoints = serviceInterface.df().getMountpoints().toArray(new MountPoint[]{});
		}
		return mountPoints;
	}

	/**
	 * Gets all the mountpoints for this particular VO
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public Set<MountPoint> getMountPoints(String fqan) {

		if (fqan == null) {
			fqan = Constants.NON_VO_FQAN;
		}

		synchronized (fqan) {

			if (alreadyQueriedMountPointsPerFqan.get(fqan) == null) {

				Set<MountPoint> mps = new HashSet<MountPoint>();
				for (MountPoint mp : getMountPoints()) {
					if (mp.getFqan() == null
							|| mp.getFqan().equals(Constants.NON_VO_FQAN)) {
						if (fqan == null
								|| fqan.equals(Constants.NON_VO_FQAN)) {
							mps.add(mp);
							continue;
						} else {
							continue;
						}
					} else {
						if (mp.getFqan().equals(fqan)) {
							mps.add(mp);
							continue;
						}
					}
				}
				alreadyQueriedMountPointsPerFqan.put(fqan, mps);
			}
			return alreadyQueriedMountPointsPerFqan.get(fqan);
		}
	}

	/**
	 * Returns all of the users sites that are accessible with this fqan
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the sites
	 */
	public String[] getAllOfTheUsersSites(String fqan) {

		Set<String> allSitesTemp = new TreeSet<String>();
		for (MountPoint mp : getMountPoints(fqan)) {
			String site = lookupSite(FILE_URL_TYPE, mp.getRootUrl());
			if (site == null) {
				myLogger.warn("Can't find site for url: " + mp.getRootUrl()
						+ ". Not adding it.");
			} else {
				allSitesTemp.add(site);
			}
		}

		String[] result = allSitesTemp.toArray(new String[] {});
		StringBuffer temp = new StringBuffer();
		for (String resultPart : result) {
			temp.append(resultPart + " ");
		}
		myLogger.debug("Found these user sites for " + fqan + ": "
				+ temp.toString());
		return result;

	}

	public synchronized SortedSet<String> getAllOfTheUsersSites() {

		if (sites == null) {
			myLogger.debug("Benchmarking getting of all of the users sites: ");
			StringBuffer temp = new StringBuffer();
			Date start = new Date();
			SortedSet<String> allSitesTemp = new TreeSet<String>();
			for (MountPoint mp : getMountPoints()) {
				String site = lookupSite(FILE_URL_TYPE, mp.getRootUrl());
				if (site == null) {
					myLogger.warn("Can't find site for url: " + mp.getRootUrl()
							+ ". Not adding it.");
				} else {
					allSitesTemp.add(site);
					temp.append(site + " ");
					String fqan = mp.getFqan();
					if (fqan == null) {
						fqan = Constants.NON_VO_FQAN;
					}
					allUsedFqans.add(fqan);
				}
			}
			sites = allSitesTemp;
			Date end = new Date();
			myLogger.debug("Found these user sites: " + temp.toString());
			myLogger.debug("[BENCHMARK] Getting all of the users sites time: "
					+ (end.getTime() - start.getTime()));
		}
		return sites;

	}

	public Set<String> getAllUsedFqans() {
		getAllOfTheUsersSites();
		return allUsedFqans;
	}

	/**
	 * Calculates all submissionlocation the user has got access to with the
	 * selected fqan
	 * 
	 * @param fqan
	 *            the fqan
	 * @return the submission locations
	 */
	public Set<SubmissionLocation> getAllAvailableSubmissionLocationsForFqan(
			String fqan) {

		if (fqan == null) {
			fqan = Constants.NON_VO_FQAN;
		}

		synchronized (fqan) {

			if (allAvailableSubmissionLocationsPerFqan.get(fqan) == null) {
				// Set<SubmissionLocation> result = new
				// HashSet<SubmissionLocation>();

				// synchronized (fqan) {
				myLogger
						.debug("Measuring duration of getting submission locations for fqan: "
								+ fqan);
				Date start = new Date();

				String[] tempSubLocsString = serviceInterface
						.getAllSubmissionLocationsForFqan(fqan).asSubmissionLocationStrings();

				// SubmissionLocation[] tempSubLocs = new
				// SubmissionLocation[tempSubLocsString.length];
				Set<SubmissionLocation> tempSubLocs = new HashSet<SubmissionLocation>();

				for (int i = 0; i < tempSubLocsString.length; i++) {
					SubmissionLocation tempSubLoc = getSubmissionLocation(tempSubLocsString[i]);
					if (tempSubLoc != null) {
						tempSubLocs.add(tempSubLoc);
					} else {
						myLogger
								.error("Null pointer for submission location string: "
										+ tempSubLocsString[i]
										+ ". Not so good.");
					}
				}
				// for ( MountPoint mp : getMountPoints(fqan) ) {
				// for ( SubmissionLocation loc :
				// getAllSubmissionLocations().values() ) {
				// if ( loc.getAssiciatedMountPointsForFqan(fqan).contains(mp) )
				// {
				// result.add(loc);
				// }
				// }
				// }

				// for (SubmissionLocation loc : getAllSubmissionLocations()
				// .values()) {

				// Date start2 = new Date();
				// if ((Arrays.binarySearch(getAllOfTheUsersSites(fqan), loc
				// .getSite()) >= 0)
				// && loc.getAssiciatedMountPointsForFqan(fqan).size() > 0) {
				// result.add(loc);
				// }
				// Date end2 = new Date();
				// myLogger
				// .debug("[BENCHMARK] checking whether submission location is available: "
				// + loc.getLocation()
				// + " and "
				// + fqan
				// + ": "
				// + (end2.getTime() - start2.getTime()));
				// // Set<MountPoint> mps =
				// // getMountPointsForSubmissionLocationAndFqan(
				// // loc.getLocation(), fqan);
				// // if (mps != null && mps.size() > 0) {
				// // result.add(loc);
				// // }
				// }
				Date end = new Date();
				myLogger
						.debug("[BENCHMARK] Submission location lookup time for "
								+ fqan
								+ ": "
								+ (end.getTime() - start.getTime()));
				allAvailableSubmissionLocationsPerFqan.put(fqan, tempSubLocs);
			}
		}

		return allAvailableSubmissionLocationsPerFqan.get(fqan);
	}

	/**
	 * Returns all submissionLocations that are available for the user with any
	 * one of his VO.
	 * 
	 * @return the submission locations
	 */
	public synchronized Map<String, SubmissionLocation> getAllSubmissionLocations() {

		if (allSubmissionLocations == null) {
			myLogger
					.debug("Getting all submission locations grid wide and caching them.");
			Date start = new Date();
			allSubmissionLocations = new TreeMap<String, SubmissionLocation>();

			for (String subLoc : getServiceInterface()
					.getAllSubmissionLocations().asSubmissionLocationStrings()) {
				Date start2 = new Date();
				myLogger.debug("Testing: " + subLoc);
				SubmissionLocation temp = new SubmissionLocation(subLoc, this);
				if (getAllOfTheUsersSites().contains(temp.getSite())) {
					allSubmissionLocations.put(subLoc, temp);
				}
				Date end2 = new Date();
				myLogger.debug("Time for " + subLoc + ": "
						+ (end2.getTime() - start2.getTime()));
			}
			Date end = new Date();
			myLogger
					.debug("[BENCHMARK] Retrieving submission locations duration: "
							+ (end.getTime() - start.getTime()));
		}
		return allSubmissionLocations;
	}
	
	
	public String[] getAllVersionsForApplicationAtSubmissionLocation(String application, SubmissionLocation subLoc, String fqan) {
		
		if ( allAvailableVersionsForApplicationPerSubmissionLocation.get(fqan+"_"+subLoc.getLocation()+"_"+application) == null ) {
			
			String [] allVersions = getServiceInterface().getVersionsOfApplicationOnSubmissionLocation(application, subLoc.getLocation()).asArray();
			
			allAvailableVersionsForApplicationPerSubmissionLocation.put(fqan+"_"+subLoc.getLocation()+"_"+application, allVersions);
		}
		return allAvailableVersionsForApplicationPerSubmissionLocation.get(fqan+"_"+subLoc.getLocation()+"_"+application);

		
	}

	/**
	 * Returns all available versions for the application on sites the user has
	 * access to
	 * 
	 * @param applicationName
	 *            the name of the application
	 * @param fqan the fqan you want to use to submit the job
	 * @return all version strings
	 */
	public Set<String> getAllAvailableVersionsForApplication(
			String applicationName, String fqan) {

		if (allAvailableVersionsForApplication.get(applicationName+"_"+fqan) == null) {

			myLogger.debug("Getting all available versions for application "
					+ applicationName);

			Date start = new Date();
			Set<String> result = new TreeSet<String>();
			for (SubmissionLocation subLoc : getAllAvailableSubmissionLocationsForFqan(fqan) ) {
				result
						.addAll(serviceInterface
								.getVersionsOfApplicationOnSubmissionLocation(
										applicationName, subLoc.getLocation()).getStringList());
			}
			Date end = new Date();
			myLogger.debug("[BENCHMARK] Getting all version locations for "
					+ applicationName + " duration: "
					+ (end.getTime() - start.getTime()));
			allAvailableVersionsForApplication.put(applicationName+"_"+fqan, result);
		}
		return allAvailableVersionsForApplication.get(applicationName+"_"+fqan);
	}
	
	public Set<SubmissionLocation> getAllAvailableSubmissionLocationsForApplicationAndVersion(String appname, String version, String fqan) {
		
		if (allAvailableSubmissionLocationsForApplicationAndVersionNew
				.get(fqan+"_"+appname + "_" + version) == null) {
			myLogger
					.debug("Getting all available submission locations for application "
							+ appname + " and version " + version);

			Date start = new Date();

			String[] subLocs = serviceInterface
					.getSubmissionLocationsForApplicationAndVersion(appname,
							version).asSubmissionLocationStrings();
			Set<SubmissionLocation> result = new TreeSet<SubmissionLocation>();

			for (String subLoc : subLocs) {
				SubmissionLocation tempLoc = getSubmissionLocation(subLoc);

				if (getAllAvailableSubmissionLocationsForFqan(fqan).contains(tempLoc)) {
					// if (tempLoc.getStagingFileSystems() != null
					// && tempLoc.getStagingFileSystems().length > 0) {
					result.add(tempLoc);
				}
			}
			Date end = new Date();
			myLogger.debug("[BENCHMARK] Getting all submission locations for "
					+ appname + " duration: "
					+ (end.getTime() - start.getTime()));
			allAvailableSubmissionLocationsForApplicationAndVersionNew.put(fqan + "_" +
					appname + "_" + version, result);

		}

		return allAvailableSubmissionLocationsForApplicationAndVersionNew
				.get(fqan + "_" + appname + "_" + version);
	}
	
	/**
	 * Returns all submissionlocations that are available for the user for this
	 * application.
	 * 
	 * @param applicationName
	 *            the application
	 * @return all submissionLocations for all fqans
	 */
	public Set<SubmissionLocation> getAllAvailableSubmissionLocationsForApplication(
			String applicationName, String fqan) {

		if (allAvailableSubmissionLocationsForApplicationNew.get(fqan+"_"+applicationName) == null) {
			myLogger
					.debug("Getting all available submission locations for application: "
							+ applicationName);
			Date start = new Date();
			Set<SubmissionLocation> result = new TreeSet<SubmissionLocation>();
			String[] subLocs = serviceInterface
					.getSubmissionLocationsForApplication(applicationName).asSubmissionLocationStrings();
			for (String subLoc : subLocs) {
				SubmissionLocation tempLoc = getSubmissionLocation(subLoc);

				if (getAllAvailableSubmissionLocationsForFqan(fqan).contains(tempLoc)) {
					// if (tempLoc.getStagingFileSystems() != null
					// && tempLoc.getStagingFileSystems().length > 0) {
					result.add(tempLoc);
				}
			}
			Date end = new Date();
			myLogger.debug("[BENCHMARK] Getting all submission locations for "
					+ applicationName + " duration: "
					+ (end.getTime() - start.getTime()));
			allAvailableSubmissionLocationsForApplicationNew.put(fqan+"_"+applicationName,
					result);
		}
		return allAvailableSubmissionLocationsForApplicationNew
				.get(fqan+"_"+applicationName);
	}

	/**
	 * Returns all submissionlocations that are available for the user for this
	 * application.
	 * 
	 * @param applicationName
	 *            the application
	 * @return all submissionLocations for all fqans
	 * @deprecated don't use that anymore
	 */
	public Set<SubmissionLocation> getAllAvailableSubmissionLocationsForApplication(
			String applicationName) {

		if (allAvailableSubmissionLocationsForApplication.get(applicationName) == null) {
			myLogger
					.debug("Getting all available submission locations for application: "
							+ applicationName);
			Date start = new Date();
			Set<SubmissionLocation> result = new HashSet<SubmissionLocation>();
			String[] subLocs = serviceInterface
					.getSubmissionLocationsForApplication(applicationName).asSubmissionLocationStrings();
			for (String subLoc : subLocs) {
				SubmissionLocation tempLoc = getSubmissionLocation(subLoc);

				if (getAllSubmissionLocations().values().contains(tempLoc)) {
					// if (tempLoc.getStagingFileSystems() != null
					// && tempLoc.getStagingFileSystems().length > 0) {
					result.add(tempLoc);
				}
			}
			Date end = new Date();
			myLogger.debug("[BENCHMARK] Getting all submission locations for "
					+ applicationName + " duration: "
					+ (end.getTime() - start.getTime()));
			allAvailableSubmissionLocationsForApplication.put(applicationName,
					result);
		}
		return allAvailableSubmissionLocationsForApplication
				.get(applicationName);
	}

	/**
	 * @param applicationName
	 * @param version
	 * @return
	 * @deprecated don't use that anymore, doesn't take into acount default fqan
	 */
	public Set<SubmissionLocation> getAllAvailableSubmissionLocationsForApplicationAndVersion(
			String applicationName, String version) {
			
		if (allAvailableSubmissionLocationsForApplicationAndVersion
				.get(applicationName + "_" + version) == null) {
			myLogger
					.debug("Getting all available submission locations for application "
							+ applicationName + " and version " + version);

			Date start = new Date();

			String[] subLocs = serviceInterface
					.getSubmissionLocationsForApplicationAndVersion(applicationName,
							version).asSubmissionLocationStrings();
			Set<SubmissionLocation> result = new HashSet<SubmissionLocation>();

			for (String subLoc : subLocs) {
				SubmissionLocation tempLoc = getSubmissionLocation(subLoc);

				if (getAllSubmissionLocations().values().contains(tempLoc)) {
					// if (tempLoc.getStagingFileSystems() != null
					// && tempLoc.getStagingFileSystems().length > 0) {
					result.add(tempLoc);
				}
			}
			Date end = new Date();
			myLogger.debug("[BENCHMARK] Getting all submission locations for "
					+ applicationName + " duration: "
					+ (end.getTime() - start.getTime()));
			allAvailableSubmissionLocationsForApplicationAndVersion.put(
					applicationName + "_" + version, result);

		}

		return allAvailableSubmissionLocationsForApplicationAndVersion
				.get(applicationName + "_" + version);
	}

	/**
	 * Returns the {@link SubmissionLocation} object for this submission
	 * location string
	 * 
	 * @param subLoc
	 *            the submission location string
	 * @return the object or null if the string is not amoung the currently
	 *         available submission locations
	 */
	public SubmissionLocation getSubmissionLocation(String subLoc) {

		SubmissionLocation result = getAllSubmissionLocations().get(subLoc);

		if (result == null && subLoc.toLowerCase().indexOf("#pbs") >= 0) {
			String tempLoc = subLoc.substring(0, subLoc.toLowerCase().indexOf(
					"#pbs"));
			return getAllSubmissionLocations().get(tempLoc);
		} else {
			return result;
		}
	}

	public Set<String> getPossibleFqansForSubmissionLocation(
			SubmissionLocation location) {
		if (location == null) {
			return null;
		}
		Set<String> fqans = new TreeSet<String>();

		for (String fs : location.getStagingFileSystems()) {

			// TODO fix that workaround
			int index = fs.indexOf(":");
			if (index != -1) {
				fs = fs.substring(0, index - 1);
			}

			for (MountPoint mp : getMountPoints()) {
				if (mp.getRootUrl().startsWith(fs)) {
					String fqan = mp.getFqan();
					if (fqan == null) {
						fqan = Constants.NON_VO_FQAN;
					}
					myLogger.debug("Adding possible fqan: " + fqan
							+ " for current submission location.");
					fqans.add(fqan);
				}
			}
		}

		return fqans;
	}

	/**
	 * Returns the all mountpoints in the environment managers list that are
	 * located on the specified site and are accessable with a voms proxy that
	 * has the specified fqan.
	 * 
	 * 
	 * @param site
	 *            the site
	 * @param fqan
	 *            the fqan
	 * @return the mountpoints
	 */
	public Set<MountPoint> getMountPointsForSiteAndFqan(String site, String fqan) {

		myLogger.debug("Checking mountpoints for site: " + site);
		Set<MountPoint> result = new TreeSet<MountPoint>();
		for (MountPoint mp : getMountPoints()) {

			String mpSite = lookupSite(FILE_URL_TYPE, mp.getRootUrl());
			if (site.equals(mpSite)) {
				if (mp.getFqan() == null
						|| mp.getFqan().equals(Constants.NON_VO_FQAN)) {
					if (fqan == null || fqan.equals(Constants.NON_VO_FQAN)) {
						result.add(mp);
					}
				} else {
					if (mp.getFqan().equals(fqan)) {
						result.add(mp);
					}
				}
			}
		}
		return result;
	}

	public synchronized Set<MountPoint> getMountPointsForSubmissionLocation(
			String submissionLocation) {

		if (alreadyQueriedMountPointsPerSubmissionLocation
				.get(submissionLocation) == null) {

			String[] urls = serviceInterface
					.getStagingFileSystemForSubmissionLocation(submissionLocation).asArray();

			Set<MountPoint> result = new TreeSet<MountPoint>();
			for (String url : urls) {

				try {
					URI uri = new URI(url);
					String host = uri.getHost();
					String protocol = uri.getScheme();

					for (MountPoint mp : getMountPoints()) {

						if (mp.getRootUrl().indexOf(host) != -1
								&& mp.getRootUrl().indexOf(protocol) != -1) {
							result.add(mp);
						}

					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			alreadyQueriedMountPointsPerSubmissionLocation.put(
					submissionLocation, result);
		}
		return alreadyQueriedMountPointsPerSubmissionLocation
				.get(submissionLocation);
	}

	public Set<MountPoint> getMountPointsForSubmissionLocationAndFqan(
			String submissionLocation, String fqan) {

		String[] urls = serviceInterface
				.getStagingFileSystemForSubmissionLocation(submissionLocation).asArray();

		Set<MountPoint> result = new TreeSet<MountPoint>();

		for (String url : urls) {

			try {
				URI uri = new URI(url);
				String host = uri.getHost();
				String protocol = uri.getScheme();

				for (MountPoint mp : getMountPoints(fqan)) {

					if (mp.getRootUrl().indexOf(host) != -1
							&& mp.getRootUrl().indexOf(protocol) != -1) {
						result.add(mp);
					}

				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		return result;

	}

	/**
	 * Checks whether any of the users' mountpoints contain the specified file.
	 * 
	 * @param file
	 *            the file
	 * @return the mountpoint or null if the file is not on any of the
	 *         mountpoints
	 */
	public MountPoint getResponsibleMountpointForFile(String url) {

		URI uri = null;
		try {
			// just to get rid of a possible port number
			uri = new URI(url);
			String path = uri.getPath();
			String protocol = uri.getScheme();
			String host = uri.getHost();
			String newUrl = protocol + "://" + host + path;
			url = newUrl;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (url.startsWith("/")) {
			// mounted file
			for (MountPoint mountpoint : getMountPoints()) {
				if (mountpoint.isResponsibleForUserSpaceFile(url)) {
					return mountpoint;
				}
			}
			return null;
		} else {
			// mounted file
			for (MountPoint mountpoint : getMountPoints()) {
				if (mountpoint.isResponsibleForAbsoluteFile(url)) {
					return mountpoint;
				}
			}
			return null;
		}
	}

	/**
	 * Get all the users' fqans
	 * 
	 * @return the fqans
	 */
	public synchronized String[] getFqans() {
		if (fqans == null) {
			fqans = serviceInterface.getFqans().asArray();
			fireFqanEvent(FqanEvent.FQANS_REFRESHED, fqans);
		}
		return fqans;
	}

	// just for now
	public String[] getAvailableFqans() {
		return getAllUsedFqans().toArray(new String[] {});
	}

	// /**
	// * Returns only these fqans that are usable (because the user has got
	// * mountpoints that use them)
	// *
	// * @return all "useful" fqans of the user
	// */
	// public String[] getAvailableFqans() {
	//
	// SortedSet<String> fqansavailable = new TreeSet<String>();
	//
	// for (String fqan : getFqans()) {
	//
	// for (MountPoint mp : getMountPoints()) {
	// // not really necessary
	// if (fqan == null && mp.getFqan() == null) {
	// fqansavailable.add(NON_VO_FQAN);
	// } else if (fqan == null) {
	// continue;
	// } else if (fqan.equals(mp.getFqan())) {
	// fqansavailable.add(fqan);
	// }
	// }
	//
	// }
	//
	// int n = 0;
	//
	// // try non-vo proxy
	// for (MountPoint mp : getMountPoints()) {
	// if (mp.getFqan() == null)
	// n = 1;
	// }
	//
	// String[] result = new String[fqansavailable.size() + n];
	//
	// if (n == 1) {
	// result[0] = NON_VO_FQAN;
	// }
	// Iterator<String> iter = fqansavailable.iterator();
	// for (int i = n; i < result.length; i++) {
	// result[i] = iter.next();
	// }
	//
	// return result;
	// }

	/**
	 * This tries to figure out what the default site of the user is. To set
	 * start directories and such..
	 * 
	 * @return the default site
	 */
	public String getDefaultSite() {

		String defaultUrl = null;
		String site = FileConstants.LOCAL_NAME;
		try {
			defaultUrl = (String) ClientPropertiesManager
					.getClientConfiguration().getProperty(
							"defaultServiceInterfaceUrl");
			URI uri = new URI(defaultUrl);
			site = "vpac";
			site = MountPointHelpers.getSiteFromMountPointUrl(uri.getHost());
			// for the lazy developer
		} catch (Exception e) {
			// well...
		}
		return site;
	}

	/**
	 * Returns the currently selected/active fqan
	 * 
	 * @return the active fqan
	 */
	public String getDefaultFqan() {
		// if (defaultFqan == null) {
		// defaultFqan = ClientPropertiesManager.getLastUsedFqan();
		//			
		// setDefaultFqan(defaultFqan);
		// }
		return defaultFqan;
	}

	/**
	 * Sets the currently selected/active fqan. Only allows the setting of
	 * available fqans (which means that there has to be a file share for this
	 * VO).
	 * 
	 * @param defaultFqan
	 *            the active fqan
	 */
	public synchronized void setDefaultFqan(String defaultFqan) {

		if (defaultFqan == null) {
			defaultFqan = Constants.NON_VO_FQAN;
		}
		Set<String> allFqansNew = getAllUsedFqans();

		if (allFqansNew == null || allFqansNew.size() == 0) {
			defaultFqan = Constants.NON_VO_FQAN;
			return;
		}

		if (!allFqansNew.contains(defaultFqan)) {
			String newFqan;
			try {
				newFqan = allFqansNew.iterator().next();
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			this.defaultFqan = newFqan;
		} else {
			this.defaultFqan = defaultFqan;
		}
		ClientPropertiesManager.saveLastUsedFqan(this.defaultFqan);
		fireFqanEvent(FqanEvent.DEFAULT_FQAN_CHANGED,
				new String[] { defaultFqan });
	}

	public String[] lookupStagingFileSystemsForSubmissionLocation(
			String submissionLocation) {

		String[] fs = alreadyQueriedQueues.get(submissionLocation);
		if (fs == null || "".equals(submissionLocation)) {
			fs = serviceInterface
					.getStagingFileSystemForSubmissionLocation(submissionLocation).asArray();
			alreadyQueriedQueues.put(submissionLocation, fs);
		}
		return fs;
	}

	public Map<String, String> lookupAllSites() {

		return null;
	}

	public String lookupSite(int urlType, String url) {

		String hostname = null;

		if (urlType == QUEUE_TYPE) {

			int startIndex = url.indexOf(":") + 1;
			if (startIndex == -1)
				startIndex = 0;

			int endIndex = url.indexOf("#");
			if (endIndex == -1)
				endIndex = url.length();

			hostname = url.substring(startIndex, endIndex);
		} else if (urlType == FILE_URL_TYPE) {

			MountPoint mp = getResponsibleMountpointForFile(url);
			if (mp != null) {
				url = mp.getRootUrl();
			}

			URI address;
			try {
				// dodgy, I know
				address = new URI(url);
			} catch (Exception e) {
				myLogger.error("Couldn't create url from: " + url);
				throw new RuntimeException("Couldn't create url from: " + url);
			}
			if (address.getHost() == null) {
				hostname = url;
			} else {
				hostname = address.getHost();
			}
		}

			if (alreadyQueriedHosts.get(hostname) == null) {
				String site = getServiceInterface().getSite(hostname);

				if (site != null) {
					alreadyQueriedHosts.put(hostname, site);
				}
				return site;
			} else {
				return alreadyQueriedHosts.get(hostname);
			}

	}

	// ---------------------------------------------------------------------------------------
	// Event stuff (MountPoints)
	private Vector<MountPointsListener> mountPointsListeners;

	private void fireMountPointsEvent(int event_type, MountPoint[] mp) {

		// if we have no mountPointsListeners, do nothing...
		if (mountPointsListeners != null && !mountPointsListeners.isEmpty()) {
			// create the event object to send
			MountPointEvent event = null;
			if (event_type == MountPointEvent.MOUNTPOINTS_REFRESHED)
				event = new MountPointEvent(this, mp, serviceInterface);
			else
				event = new MountPointEvent(this, event_type, mp[0],
						serviceInterface);

			// firstly call the mountpointschanged method of this class to be
			// sure we
			// have all the information right when another listener asks for it
			try {
				mountPointsChanged(event);
			} catch (RemoteFileSystemException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) mountPointsListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				MountPointsListener l = (MountPointsListener) e.nextElement();
				try {
					l.mountPointsChanged(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	// register a listener
	synchronized public void addMountPointListener(MountPointsListener l) {
		if (mountPointsListeners == null)
			mountPointsListeners = new Vector();
		mountPointsListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeMountPointListener(MountPointsListener l) {
		if (mountPointsListeners == null) {
			mountPointsListeners = new Vector<MountPointsListener>();
		}
		mountPointsListeners.removeElement(l);
	}

	// ---------------------------------------------------------------------------------------
	// Event stuff (Fqan)
	private Vector<FqanListener> fqanListeners;

	private void fireFqanEvent(int event_type, String[] fqans) {
		// if we have no mountPointsListeners, do nothing...
		if (fqanListeners != null && !fqanListeners.isEmpty()) {
			// create the event object to send
			FqanEvent event = null;
			if (event_type == FqanEvent.FQANS_REFRESHED)
				event = new FqanEvent(this, fqans);
			else
				event = new FqanEvent(this, event_type, fqans[0]);

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector fqantargets;
			synchronized (this) {
				fqantargets = (Vector) fqanListeners.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration e = fqantargets.elements();
			while (e.hasMoreElements()) {
				FqanListener fqan_l = (FqanListener) e.nextElement();
				myLogger.debug("Firing fqan event to: "
						+ fqan_l.getClass().toString());
				Date start = new Date();
				fqan_l.fqansChanged(event);
				Date end = new Date();
				myLogger.debug("[BENCHMARK] Firing fqan event to "
						+ fqan_l.getClass().toString() + " duration: "
						+ (end.getTime() - start.getTime()));
			}
		}
	}

	// register a listener
	synchronized public void addFqanListener(FqanListener l) {
		if (fqanListeners == null)
			fqanListeners = new Vector();
		fqanListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeFqanListener(FqanListener l) {
		if (fqanListeners == null) {
			fqanListeners = new Vector<FqanListener>();
		}
		fqanListeners.removeElement(l);
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public FileTransferManager getFileTransferManager() {
		return fileTransferManager;
	}

	// public JobManagementInterface getJobManagement() {
	// return jobManagement;
	// }

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	// public void setJobManagement(JobManagementInterface jobManagement) {
	// this.jobManagement = jobManagement;
	// }

	// public static void setDefaultManager(EnvironmentManager defaultManager) {
	// EnvironmentManager.defaultManager = defaultManager;
	// }

	public TemplateManager getTemplateManager() {
		return templateManager;
	}

	public void setTemplateManager(TemplateManager templateManager) {
		this.templateManager = templateManager;
	}

	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(ServiceInterface serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public HistoryManager getHistoryManager() {
		return historyManager;
	}

	public void setHistoryManager(HistoryManager historyManager) {
		this.historyManager = historyManager;
	}

	/**
	 * builds submission location cache in the background
	 */
	public void buildInfoCacheInBackground() {

		myLogger
				.debug("Starting to build submissionLocation cache in background.");
		new Thread() {
			public void run() {
				buildInfoCache();
			}
		}.start();

	}

	private void buildInfoCache() {
		if (sites == null) {
			myLogger.debug("Building info cache.");
			getAllOfTheUsersSites();

			getAllAvailableSubmissionLocationsForFqan(getDefaultFqan());

			// for (String fqan : getAllUsedFqans()) {
			// getAllAvailableSubmissionLocationsForFqan(fqan);
			// }
		}
	}

	private synchronized void invalidateMountPointCaches()
			throws RemoteFileSystemException {
		mountPoints = null;
		sites = null;
		allUsedFqans = new TreeSet<String>();
		allSubmissionLocations = null;
		alreadyQueriedMountPointsPerSubmissionLocation = new HashMap<String, Set<MountPoint>>();
		alreadyQueriedMountPointsPerFqan = new HashMap<String, Set<MountPoint>>();
		allAvailableSubmissionLocationsPerFqan = new HashMap<String, Set<SubmissionLocation>>();
		allAvailableSubmissionLocationsForApplication = new HashMap<String, Set<SubmissionLocation>>();
		allAvailableSubmissionLocationsForApplicationNew = new HashMap<String, Set<SubmissionLocation>>();
		allAvailableSubmissionLocationsForApplicationAndVersion = new HashMap<String, Set<SubmissionLocation>>();
		allAvailableSubmissionLocationsForApplicationAndVersionNew = new HashMap<String, Set<SubmissionLocation>>();
		allAvailableVersionsForApplication = new HashMap<String, Set<String>>();
		allAvailableVersionsForApplicationPerSubmissionLocation = new HashMap<String, String[]>();
		buildInfoCache();
	}

	public synchronized void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {

		String oldFqan = getDefaultFqan();
		invalidateMountPointCaches();
		setDefaultFqan(oldFqan);
	}

	public JobManager getJobManager() {
		return glazedJobManagement;
	}

	public void setJobManager(JobManager glazedJobManagement) {
		this.glazedJobManagement = glazedJobManagement;
	}

	// these are not really important

	/**
	 * Returns all sites of the grid. This info is not really usefull because it
	 * doesn't tell you whether the user has got access to all of them or not...
	 * 
	 * @return all Sites gridwise
	 */
	public String[] getAllSitesOfTheGrid() {
		return getServiceInterface().getAllSites().asArray();
	}

	public String[] getAllAvailableFqans() {
		return getFqans();
	}

	public SortedSet<String> getAllAvailableSites() {

		return getAllOfTheUsersSites();
	}

	public Set<String> getAllAvailableSubmissionLocations() {
		return new HashSet(getAllSubmissionLocations().values());
	}

	public MountPoint getMountPointForUrl(String url) {
		return getResponsibleMountpointForFile(url);
	}

	public MountPoint getRecommendedMountPoint(String submissionLocation,
			String fqan) {
		
		Set<MountPoint> mps = getMountPointsForSubmissionLocationAndFqan(submissionLocation, fqan);
		if ( mps.size() > 0 ) {
			return mps.iterator().next();
		} else {
			return null;
		}
	}

	public SortedSet<MountPoint> getMountPointsForSite(String site) {

		throw new RuntimeException("Not implemented for this class.");
		
	}

	public boolean isMountPointAlias(String string) {
		throw new RuntimeException("Not implemented for this class.");

	}

	public MountPoint getMountPointForAlias(String url) {
		throw new RuntimeException("Not implemented for this class.");
	}

	public boolean isMountPointRoot(String rootUrl) {
		throw new RuntimeException("Not implemented for this class.");
	}

	public String getProperty(String key) {
		throw new RuntimeException("Not implemented for this class.");
	}

	public void setProperty(String key, String value) {
		throw new RuntimeException("Not implemented for this class.");
	}

	public Map<String, String> getBookmarks() {
		throw new RuntimeException("Not implemented for this class.");
	}

	public List<FileSystemItem> getBookmarksFilesystems() {
		throw new RuntimeException("Not implemented for this class.");

	}

	public FileSystemItem getFileSystemForUrl(String url) {
		throw new RuntimeException("Not implemented for this class.");

	}

	public List<FileSystemItem> getFileSystems() {

		throw new RuntimeException("Not implemented for this class.");
	}

	public List<FileSystemItem> getLocalFileSystems() {
		throw new RuntimeException("Not implemented for this class.");

	}

	public List<FileSystemItem> getRemoteSites() {
		throw new RuntimeException("Not implemented for this class.");

	}

	public void setBookmark(String alias, String url) {
		throw new RuntimeException("Not implemented for this class.");
		
	}

	public String calculateUniqueJobname(String name) {
		throw new RuntimeException("Not implemented for this class.");

	}

	public SortedSet<String> getCurrentBatchJobnames() {
		throw new RuntimeException("Not implemented for this class.");
	}

	public SortedSet<String> getCurrentJobnames() {
		throw new RuntimeException("Not implemented for this class.");
	}

	public DtoBatchJob getBatchJob(String jobname, boolean refreshBatchJob)
			throws NoSuchJobException {
		throw new RuntimeException("Not implemented for this class.");
	}

	public SortedSet<DtoBatchJob> getBatchJobs(String application,
			boolean refresh) {
		throw new RuntimeException("Not implemented for this class.");
	}

	public SortedSet<String> getCurrentBatchJobnames(String application,
			boolean refresh) {
		throw new RuntimeException("Not implemented for this class.");
	}

	public SortedSet<String> getCurrentJobnames(String application,
			boolean refresh) {
		throw new RuntimeException("Not implemented for this class.");
	}



}
