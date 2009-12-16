package org.vpac.grisu.client.model.files;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class GrisuFileObject implements Comparable {

	static final Logger myLogger = Logger.getLogger(GrisuFileObject.class
			.getName());

	private Map<String, String> properties = new HashMap<String, String>();
	private URI uri = null;
	private FileSystemBackend fileSystemBackend = null;
	private int type = -1;
	private long size = -1;
	private long lastModified = -1;

	public GrisuFileObject(FileSystemBackend fileSystemBackend, URI uri,
			int type) {
		this.fileSystemBackend = fileSystemBackend;

		this.uri = stripTrailingFileSeperator(uri);
		this.type = type;
	}

	public GrisuFileObject(FileSystemBackend fileSystemBackend, URI uri,
			int type, Map<String, String> properties) {
		this.fileSystemBackend = fileSystemBackend;
		this.uri = stripTrailingFileSeperator(uri);
		this.type = type;
		this.properties = properties;
	}

	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	// helper methods
	// --------------
	private int calculateLastFileSeperator(String uri_string) {

		int index = uri_string.lastIndexOf("/");
		int index2 = uri_string.lastIndexOf(File.separator);
		int finalIndex = index2;
		if (index > index2)
			finalIndex = index;

		return finalIndex;
	}

	private URI calculateParent(URI uri) {

		String uri_string = stripTrailingFileSeperator(uri).toString();
		int finalIndex = calculateLastFileSeperator(uri_string);

		URI uri_new = null;
		try {
			String uri_string_new = uri_string.substring(0, finalIndex);
			if (uri_string_new.equals("file:")) {
				return new URI("file:/");
			}
			uri_new = new URI(uri_string.substring(0, finalIndex));
			myLogger.debug("Calculated parent: " + uri_new.toString());
		} catch (URISyntaxException e) {
			// will never happen
		}
		return uri_new;

	}

	public int compareTo(Object o) {

		if (o instanceof GrisuFileObject) {
			GrisuFileObject otherFile = (GrisuFileObject) o;
			return this.getName().compareTo(otherFile.getName());
		}
		return 0;
	}

	public boolean deleteLocalRepresentation() {
		return getFileSystemBackend().deletePossibleLocalCacheFile(this);
	}

	public boolean equals(Object other) {
		if (other instanceof GrisuFileObject) {
			GrisuFileObject otherFile = (GrisuFileObject) other;
			if (otherFile.getURI().equals(getURI())
					&& otherFile.getType() == otherFile.getType()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public boolean exists() {
		return fileSystemBackend.exists(this);
	}

	public GrisuFileObject getChild(String filename, boolean refresh) {
		if (type != FileConstants.TYPE_FOLDER)
			return null;

		return fileSystemBackend.getChild(this, filename, refresh);

	}

	public GrisuFileObject[] getChildren() {
		if (type != FileConstants.TYPE_FOLDER)
			return null;

		// System.out.println(this.getURI().toString());
		GrisuFileObject[] result = null;
		try {
			result = fileSystemBackend.getChildren(this);
		} catch (RuntimeException e) {
			myLogger.error(e.getLocalizedMessage());
			e.printStackTrace();
			return new GrisuFileObject[] {};
		}
		return result;
	}

	public GrisuFileObject[] getChildren(boolean refresh) {
		if (type != FileConstants.TYPE_FOLDER)
			return null;

		return fileSystemBackend.getChildren(this, refresh);
	}

	public FileSystemBackend getFileSystemBackend() {
		return fileSystemBackend;
	}

	public long getLastModified(boolean refresh) {
		if (lastModified == -1 || refresh) {
			lastModified = fileSystemBackend.getLastModifiedDate(this);
		}
		return lastModified;
	}

	/**
	 * Returns the locally mirrored representation of the specified file. If it
	 * is not mirrored yet it will get downloaded from it's original location.
	 * Also, if the remote file is newer than the locally cached one, it get's
	 * downloaded as well if you specify the refresh variable
	 * 
	 * @param refresh
	 *            whether to check if a newer version exists remotely or not
	 * @return the local representation of this file
	 */
	public File getLocalRepresentation(boolean refresh) {
		return getFileSystemBackend().getLocalCacheFile(this, refresh);
	}

	public String getName() {
		return getURI().toString().substring(
				calculateLastFileSeperator(getURI().toString()) + 1);
	}

	public GrisuFileObject getParent() {
		if (isRoot()) {
			return null;
		} else {
			return fileSystemBackend.getFileObject(calculateParent(uri));
		}
	}

	public String getPathRelativeToRootOfFileSystem() {
		return getFileSystemBackend().getPathRelativeToRoot(this);
	}

	/**
	 * Gets the file size.
	 * 
	 * @param refresh
	 *            whether to check whether the filesize is changed since checked
	 *            last or not. Only makes sense if the job is still running
	 * @return the filesize in bytes or -1 if the filesize could not be
	 *         retrieved.
	 */
	public long getSize(boolean refresh) {
		if (size == -1 || refresh) {
			size = fileSystemBackend.getSize(this);
		}
		return size;
	}

	public int getType() {
		return type;
	}

	public URI getURI() {
		return uri;
	}

	public int hashCode() {
		return getURI().hashCode() + getType()
				+ getFileSystemBackend().hashCode();
	}

	public boolean isRoot() {
		if (this.fileSystemBackend.getRootUri().equals(uri)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Refreshes a possible underlying cache of this file system.
	 * 
	 * @param file
	 *            the root of the branch of the file system that should be
	 *            refreshed
	 */
	public void refresh() {
		getFileSystemBackend().refresh(this);
	}

	public void removeProperty(String key) {
		properties.remove(key);
	}

	public void setSize(long size) {
		this.size = size;
	}

	private URI stripTrailingFileSeperator(URI uri) {
		String uri_string = uri.toString();
		if (uri_string.endsWith(File.separator) || uri_string.endsWith("/")) {
			try {
				URI new_uri = new URI(uri.toString().substring(0,
						uri_string.length() - 1));
				return new_uri;
			} catch (URISyntaxException e) {
				// will never happen
			}
		}
		return uri;
	}

	public String toString() {
		return getName();
	}

}
