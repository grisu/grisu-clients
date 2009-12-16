package org.vpac.grisu.client.control.files;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.InformationError;
import org.vpac.grisu.client.control.status.ApplicationStatusManager;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.files.FileSystemBackend;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.LocalFileSystemBackend;
import org.vpac.grisu.client.model.files.RemoteFileSystemBackend;
import org.vpac.grisu.client.model.files.events.FileSystemBackendEvent;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.MountPoint;

// TODO try to include the mountpoint management in here?

/**
 * The FileManager takes care of all the filesystems that are accessible to the
 * user (local as well as remote). It uses the {@link EnvironmentManager} to
 * determine all mounted MountPoints and converts them to
 * {@link FileSystemBackend}s.
 * 
 * It sends out events to it's listeners if any of the filesystems change. This
 * basically means it passes through events from the {@link EnvironmentManager}.
 * 
 * The FileManager is used mostly because it's abilitly to convert Strings/URIs
 * to {@link GrisuFileObject}s. Once you've got one of those, it's pretty easy
 * to get children of folders, copy them to a remote filesystem or get a local
 * representation.
 * 
 * @author Markus Binsteiner
 * 
 */
public class FileManager implements MountPointsListener {

	static final Logger myLogger = Logger
			.getLogger(FileManager.class.getName());

	public static FileSystemBackend createFileSystemBackend(String alias,
			URI rootUri, EnvironmentManager em) throws FileSystemException {

		FileSystemBackend fs = null;
		String scheme = rootUri.getScheme();
		if (scheme.startsWith("gsiftp")) {
			// Remote file system
			fs = new RemoteFileSystemBackend(em, rootUri, alias);
			return fs;
		} else if (scheme.startsWith("file")) {
			// System.out.println(rootUri.toString());
			fs = new LocalFileSystemBackend(em, rootUri, alias);
			return fs;
		} else {
			throw new FileSystemException(
					"Could not determine which type of GrisuFileSystem should be created for uri: "
							+ rootUri.toString());
		}
	}

	ApplicationStatusManager statusManager = null;

	private Map<String, FileSystemBackend> fileSystems = new HashMap<String, FileSystemBackend>();
	private Set<String> sites = new TreeSet<String>();
	private ServiceInterface serviceInterface = null;

	private EnvironmentManager em = null;

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<FileManagerListener> fileManagerListeners;

	public FileManager(MountPoint[] mps, EnvironmentManager em,
			boolean includeLocalFileSystems) throws FileSystemException {

		this.em = em;
		this.serviceInterface = em.getServiceInterface();

		// // add home directory
		// String homeDir = System.getProperty("user.home");
		//		
		// try {
		// addFileSystem(, "file:"+homeDir, null);
		// myLogger.debug("Successfully added home filesystem: "+homeDir);
		// } catch (FileSystemException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		if (includeLocalFileSystems) {
			// list roots
			for (File root : File.listRoots()) {

				try {
					if (FileSystemView.getFileSystemView().isFloppyDrive(root)) {
						// ignore this
						continue;
					}
				} catch (Exception e) {
					myLogger
							.error("Could not find out whether drive is floppy drive. Hoping it is not and continue...");
				}

				try {
					String alias = root.getName();
					if (alias == null || "".equals(alias)) {
						alias = root.getAbsolutePath();
					}
					URI uri = root.toURI();
					addFileSystem(alias, uri.toString(), null);
					// addFileSystem(alias, "file:" + root.getAbsolutePath(),
					// null);
					myLogger.debug("Successfully added filesystem: "
							+ root.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		statusManager = ApplicationStatusManager.getDefaultManager();

		statusManager.setCurrentStatus("Querying and adding your filesystems.");
		for (MountPoint mp : mps) {
			try {
				statusManager.setCurrentStatus("Adding filesystem: "
						+ mp.getAlias());
				addFileSystem(mp.getAlias(), mp.getRootUrl(), serviceInterface);
				myLogger.debug("Successfully added filesytem: "
						+ mp.getRootUrl() + " for fqan: " + mp.getFqan());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				myLogger.error(
						"Couldn't create filesystem: " + mp.getRootUrl(), null);
			}
		}
		statusManager.setCurrentStatus("Added all filesystems.");

	}

	// register a listener
	synchronized public void addFileManagerListener(FileManagerListener l) {
		if (fileManagerListeners == null)
			fileManagerListeners = new Vector();
		fileManagerListeners.addElement(l);
	}

	/**
	 * Adds a filesystem to the users' environment
	 * 
	 * @param fs
	 *            the filesystem to add
	 * @throws InformationError
	 *             if the site of the fs can't be looked up
	 */
	public void addFileSystem(String alias, FileSystemBackend fs)
			throws InformationError {

		fileSystems.put(alias, fs);
		myLogger.debug("Adding filesystem: " + fs.getAlias());
		sites.add(fs.getSite());
		fireFileSystemBackendEvent(fs, FileSystemBackendEvent.FILESYSTEM_ADDED);

	}

	public void addFileSystem(String alias, String rootUri,
			ServiceInterface serviceInterface) throws InformationError {

		URI uri = null;

		try {
			uri = new URI(rootUri);
		} catch (URISyntaxException e) {
			throw new FileSystemException("Could not parse uri: " + rootUri);
		}
		addFileSystem(alias, uri, serviceInterface);
	}

	public void addFileSystem(String alias, URI rootUri,
			ServiceInterface serviceInterface) throws InformationError {

		FileSystemBackend newFs = createFileSystemBackend(alias, rootUri, em);
		addFileSystem(alias, newFs);
	}

	/**
	 * Finds the first filesystem this uri belongs to
	 * 
	 * @param uri
	 *            the uri
	 * @return the first filesystem or null if there is no responsible
	 *         filesystem
	 */
	public FileSystemBackend findResponsibleFileSystem(String uri) {

		for (FileSystemBackend fs : fileSystems.values()) {
			if (uri.startsWith(fs.getRootUri().toString())) {
				// in case it's not initialized yet.
				fs.getRoot();
				return fs;
			}
		}

		return null;
	}

	/**
	 * Finds the first filesystem this uri belongs to
	 * 
	 * @param uri
	 *            the uri
	 * @return the first filesystem or null if there is no responsible
	 *         filesystem
	 */
	public FileSystemBackend findResponsibleFileSystem(URI uri) {
		return findResponsibleFileSystem(uri.toString());
	}

	private void fireFileSystemBackendEvent(FileSystemBackend fsb,
			int event_type) {
		// if we have no mountPointsListeners, do nothing...
		if (fileManagerListeners != null && !fileManagerListeners.isEmpty()) {
			// create the event object to send
			FileSystemBackendEvent event = null;
			event = new FileSystemBackendEvent(this, fsb, event_type);

			// make a copy of the listener list in case
			// anyone adds/removes fileManagerListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) fileManagerListeners.clone();
			}

			// walk through the listener list
			Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				FileManagerListener l = (FileManagerListener) e.nextElement();
				l.fileSystemBackendsChanged(event);
			}
		}
	}

	public GrisuFileObject getFileObject(String uri_string)
			throws URISyntaxException {
		URI uri = new URI(uri_string);
		return getFileObject(uri);
	}

	/**
	 * Returns the specified url as a {@link GrisuFileObject}
	 * 
	 * @param uri
	 *            the url
	 * @return the file object or null if the uri could not be resolved within
	 *         the user's file systems
	 */
	public GrisuFileObject getFileObject(URI uri) {

		FileSystemBackend fs = findResponsibleFileSystem(uri);
		if (fs == null)
			return null;

		return fs.getFileObject(uri);
	}

	public FileSystemBackend getFileSystemBackend(String alias) {
		return fileSystems.get(alias);
	}

	public ArrayList<FileSystemBackend> getFileSystems() {
		ArrayList<FileSystemBackend> allFileSystems = new ArrayList<FileSystemBackend>();
		for (FileSystemBackend fs : fileSystems.values()) {
			allFileSystems.add(fs);
		}
		return allFileSystems;
	}

	/**
	 * Iterates through all file systems and returns all of them for a site.
	 * 
	 * @param site
	 *            the site
	 * @return all filesystems for this site
	 */
	public Map<String, FileSystemBackend> getFileSystems(String site) {
		Map<String, FileSystemBackend> result = new TreeMap<String, FileSystemBackend>();
		for (String alias : fileSystems.keySet()) {
			FileSystemBackend fs = fileSystems.get(alias);
			try {
				if (fs.getSite().equals(site)) {
					result.put(alias, fs);
				}
			} catch (InformationError e) {
				myLogger.error(e.getLocalizedMessage());
			}
		}
		return result;
	}

	/**
	 * Returns all sites a user has got filesystems on.
	 * 
	 * @return all sites
	 */
	public Set<String> getSites() {
		return sites;
	}

	// public void initAllFileSystemsInBackground() {
	//
	// new Thread() {
	// public void run() {
	//
	// Vector<FileSystemBackend> filesystemsCopy = null;
	// synchronized (this) {
	// filesystemsCopy = new Vector(fileSystems.values());
	// }
	//
	// for (FileSystemBackend be : filesystemsCopy) {
	// myLogger.debug("Initializing (if not already) filesystem: "
	// + be.getAlias());
	// be.getRoot().getChildren();
	// }
	// }
	// }.start();
	//
	// myLogger.debug("Finished initializing filesystems.");
	//
	// }

	public synchronized void mountPointsChanged(MountPointEvent mpe) {

		if (mpe.getEventType() == MountPointEvent.MOUNTPOINT_ADDED) {
			try {
				URI rootUri = new URI(mpe.getMountPoint().getRootUrl());
				FileSystemBackend fsbe = createFileSystemBackend(mpe
						.getMountPoint().getAlias(), rootUri, em);
				addFileSystem(mpe.getMountPoint().getAlias(), fsbe);
			} catch (Exception e) {
				// hm. not much I can't do here...
				myLogger.error("Could not add filesystem for root url: "
						+ mpe.getMountPoint().getRootUrl());
				e.printStackTrace();
			}
		} else if (mpe.getEventType() == MountPointEvent.MOUNTPOINT_REMOVED) {
			try {
				removeFileSystem(mpe.getMountPoint().getAlias());
			} catch (InformationError e) {
				e.printStackTrace();
				myLogger.error(e.getLocalizedMessage());
				// TODO don't know what else to do. Let's see whether this
				// happens at all
			}
		} else if (mpe.getEventType() == MountPointEvent.MOUNTPOINTS_REFRESHED) {
			myLogger.debug("MountPoints refreshed.");
			for (MountPoint mp : mpe.getMountPoints()) {
				try {
					URI rootUri = new URI(mp.getRootUrl());
					FileSystemBackend fsbe = createFileSystemBackend(mp
							.getAlias(), rootUri, em);
					addFileSystem(mp.getAlias(), fsbe);
				} catch (Exception e) {
					// hm. not much I can't do here...
					myLogger.error("Could not add filesystem for root url: "
							+ mp.getRootUrl());
					e.printStackTrace();
				}
			}
		}
	}

	// remove a listener
	synchronized public void removeFileManagerListener(FileManagerListener l) {
		if (fileManagerListeners == null) {
			fileManagerListeners = new Vector<FileManagerListener>();
		}
		fileManagerListeners.removeElement(l);
	}

	/**
	 * Removes a filesystem from the users environment
	 * 
	 * @param fs
	 *            the alias of the filesystem to remove
	 * @throws InformationError
	 *             if the site can't be looked up
	 */
	public void removeFileSystem(String alias) throws InformationError {

		String site = fileSystems.get(alias).getSite();
		FileSystemBackend temp = fileSystems.get(alias);
		fileSystems.remove(alias);
		Map currentFS = getFileSystems(site);
		if (currentFS.size() == 0) {
			removeSite(site);
		}

		fireFileSystemBackendEvent(temp,
				FileSystemBackendEvent.FILESYSTEM_REMOVED);
	}

	/**
	 * Removes a site from the list of all the users' sites.
	 * 
	 * @param site
	 *            the site to remove
	 */
	public void removeSite(String site) {
		sites.remove(site);
	}

}
