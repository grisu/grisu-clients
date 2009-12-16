package org.vpac.grisu.client.model.files;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractListModel;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.status.ApplicationStatusManager;

public class FileSystemListFrontend extends AbstractListModel {

	static final Logger myLogger = Logger
			.getLogger(FileSystemListFrontend.class.getName());

	public static final int SORT_BY_TYPE_THEN_NAME = 0;
	public static final int SORT_BY_NAME = 1;
	public static final int SORT_BY_SIZE = 2;
	public static final int SORT_BY_DATE = 3;

	private FileSystemBackend fileSystemBackend = null;

	private boolean inverse_sort = false;
	protected boolean invalidated = false;

	private GrisuFileObject virtualRoot = null;

	private int mode = SORT_BY_TYPE_THEN_NAME;

	private Map<String, GrisuFileObject> cachedCurrentDirectory = null;
	private GrisuFileObject currentDirectory = null;

	public FileSystemListFrontend(FileSystemBackend fsb) {
		ApplicationStatusManager.getDefaultManager().setCurrentStatus(
				"Connecting to filesystem: " + fsb.getAlias());

		this.fileSystemBackend = fsb;
		myLogger.debug("Creating frontend for filesystem backend: "
				+ fsb.getRootUri().toString());

		// fileSystemBackend.getRoot().refresh();

	}

	public FileSystemListFrontend(FileSystemBackend fsb,
			GrisuFileObject virtualRoot) {
		this.fileSystemBackend = fsb;
		// fileSystemBackend.getRoot().refresh();
		this.virtualRoot = virtualRoot;
		this.currentDirectory = virtualRoot;
	}

	private void checkValidity() throws FileSystemException {

		// if changed into another directory or refreshed
		if (invalidated || cachedCurrentDirectory == null) {

			fillCache();
			invalidated = false;
		}

	}

	public boolean currentDirectoryIsOnRoot() {
		// return ( currentDirectory.isRoot() ||
		// currentDirectory.equals(virtualRoot) );
		return getCurrentDirectory().equals(getVirtualRoot());
	}

	public boolean equals(Object otherObject) {

		if (otherObject == null) {
			return false;
		}

		if (otherObject instanceof FileSystemListFrontend) {
			FileSystemListFrontend other = (FileSystemListFrontend) otherObject;

			if (other.getFileSystemBackend()
					.equals(this.getFileSystemBackend())
					&& other.getVirtualRoot().equals(this.getVirtualRoot())) {
				return true;
			}
		}
		return false;
	}

	private void fillCache() {

		cachedCurrentDirectory = new TreeMap<String, GrisuFileObject>();
		GrisuFileObject[] list = getCurrentDirectory().getChildren();
		if (list == null) {
			myLogger.error("Could not list directory: "
					+ getCurrentDirectory().getName());
			return;
		}
		for (GrisuFileObject file : list) {
			String processed = process(file);
			if (cachedCurrentDirectory.get(processed) != null) {
				myLogger.error("That should never happen.");
			}
			cachedCurrentDirectory.put(processed, file);
		}
		myLogger.debug("Filled cache size: " + cachedCurrentDirectory.size());
	}

	public GrisuFileObject getCurrentDirectory() {
		if (currentDirectory == null) {
			this.currentDirectory = fileSystemBackend.getRoot();
		}
		return currentDirectory;
	}

	public String getCurrentDirectoryRelativeToRoot() {
		return fileSystemBackend.getPathRelativeToRoot(getCurrentDirectory());
	}

	public Object getElementAt(int index) {

		checkValidity();

		String[] keys = cachedCurrentDirectory.keySet().toArray(
				new String[cachedCurrentDirectory.keySet().size()]);

		if (currentDirectoryIsOnRoot()) {
			return cachedCurrentDirectory.get(keys[index]);
		} else {
			if (index == 0) {
				return getCurrentDirectory().getParent();
			} else {
				return cachedCurrentDirectory.get(keys[index - 1]);
			}

		}
	}

	public FileSystemBackend getFileSystemBackend() {
		return fileSystemBackend;
	}

	public int getSize() {
		checkValidity();

		if (currentDirectoryIsOnRoot()) {
			return cachedCurrentDirectory.keySet().size();
		} else {
			return cachedCurrentDirectory.keySet().size() + 1;
		}
	}

	public GrisuFileObject getVirtualRoot() {
		if (virtualRoot == null) {
			virtualRoot = fileSystemBackend.getRoot();
		}
		return virtualRoot;
	}

	public int hashCode() {
		return getFileSystemBackend().hashCode() + getVirtualRoot().hashCode();
	}

	public void invalidate() {
		invalidated = true;
	}

	public boolean isVirtualRoot(GrisuFileObject file) {
		return file.equals(getVirtualRoot());
	}

	private String process(GrisuFileObject file) {

		switch (mode) {
		case SORT_BY_TYPE_THEN_NAME:
			String name = null;
			if (FileConstants.TYPE_FOLDER == file.getType()) {
				return "Folder_" + file.getName();
			} else if (FileConstants.TYPE_FILE == file.getType()) {
				return "NoFolder_" + file.getName();
			} else {
				throw new RuntimeException(
						"Filetype not file nor folder nor root. That's bad. Exiting...");
			}
		default:
			throw new RuntimeException("Sort mode not supported. Exiting...");
		}

	}

	public void refreshCurrentDirectory() {
		int index = getSize();
		getCurrentDirectory().refresh();
		invalidate();
		fireContentsChanged(this, 0, index);
	}

	public void setCurrentDirectory(GrisuFileObject file) {
		if (fileSystemBackend.isInFileSystem(file)) {
			int index = getSize();
			if (file.getURI().toString().startsWith(
					getVirtualRoot().getURI().toString())) {
				// means valid directory
				this.currentDirectory = file;
			} else {
				this.currentDirectory = getVirtualRoot();
			}
			invalidate();
			fireContentsChanged(this, 0, index);
		} else {
			// TODO do nothing? or throw exception?
		}
	}

	public String toString() {
		return getFileSystemBackend().getAlias();
	}

}
