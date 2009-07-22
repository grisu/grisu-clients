

package org.vpac.grisu.client.model.files;

import java.io.File;
import java.net.URI;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.InformationError;

/**
 * @author markus
 *
 */
public interface FileSystemBackend {
	
	public EnvironmentManager getEnvironmentManager();
	
	/**
	 * Tries to figure out which site this filesystem belongs to.
	 * @return the name of the site or "Local" if Grisu thinks this file system is on the local hard disk
	 * @throws InformationError if no site can be found for this filesystem.
	 */
	public String getSite() throws InformationError;
	
	/**
	 * Returns the internal alias (aka mountpoint) of this filesystem
	 * @return the alias
	 */
	public String getAlias();
	
	/**
	 * Translates a uri to a BackendFileObject.
	 * @param uri the uri (which should be within this file system)
	 * @return the BackendFileObject or null if the uri is not within this file system
	 */
	public GrisuFileObject getFileObject(URI uri);
	
	/**
	 * Returns all the children of the specified file object. Does not refresh the folder beforehand.
	 * @param folder the folder
	 * @return an array of children BackendfileObject s or null if the folder is not a ...folder
	 */
	public GrisuFileObject[] getChildren(GrisuFileObject folder);
	
	/**
	 * Returns all the children of the specified file object and lets the user specify whether he wants to refresh the 
	 * parent folder beforehand.
	 * @param folder the folder
	 * @param refresh whether to refresh the folder beforehand
	 * @return an array of children BackendfileObject s or null if the folder is not a ...folder
	 */
	public GrisuFileObject[] getChildren(GrisuFileObject folder, boolean refresh);
	
	/**
	 * Refreshes a possible underlying cache of this file system.
	 * @param file the root of the branch of the file system that should be refreshed
	 */
	public void refresh(GrisuFileObject file);
	
	/**
	 * Returns the root BackendFileObject of this file system
	 * @return the root object
	 */
	public GrisuFileObject getRoot();
	
	/**
	 * Returns the uri of the root of this file system
	 * @return the root uri
	 */
	public URI getRootUri();
	
	/**
	 * Checks whether the specified file is the root of this filesystem.
	 * @param file the file
	 * @return whether the file is root or not
	 */
	public boolean isRoot(GrisuFileObject file);
	
	/**
	 * This one returns the root folder for the local representation of this file system. If the file system is
	 * local anyway it can just return the normal root folder. If it is remote it would return the root folder of the 
	 * cache directory where the file system is (partially) mirrored.
	 * @return the root folder of the local representation of this file system
	 */
	public File getLocalCacheRoot();
	
	/**
	 * Returns the locally mirrored representation of the specified file. If it is not mirrored yet it will get downloaded from
	 * it's original location. Also, if the remote file is newer than the locally cached one, it get's downloaded as well if you
	 * specify the refresh variable
	 * @param path file
	 * @param refresh whether to check if a newer version exists remotely or not
	 * @return the local representation of this file
	 */
	public File getLocalCacheFile(GrisuFileObject file, boolean refresh);
	
	/**
	 * Returns the locally mirrored representation of the specified file. If it is not mirrored yet it will get downloaded from
	 * it's original location. Also, if the remote file is newer than the locally cached one, it get's downloaded as well if you
	 * specify the refresh variable
	 * @param path the path of the file either relative to the root folder (e.g. folder/file) or absolute (gsiftp://blahblah/file)
	 * @param refresh whether to check if a newer version exists remotely or not
	 * @return the local representation of this file
	 */
	public File getLocalCacheFile(String path, boolean refresh);
	

	/**
	 * Deletes a possible locally cached file that represents this file object.
	 * @param grisuFileObject the file
	 * @return whether a local representation was deleted or not
	 */
	public boolean deletePossibleLocalCacheFile(GrisuFileObject grisuFileObject);
	
	/**
	 * Checks whether the specified file is within the file system
	 * @param file the file
	 * @return true - if it is; false - if not
	 */
	public boolean isInFileSystem(GrisuFileObject file);
	
	/**
	 * Calculates the relative path to the root of this filesystem
	 * @param file the file
	 * @return the relative path or null if this file is not within the filesystem
	 */
	public String getPathRelativeToRoot(GrisuFileObject file);
	
	/**
	 * Queries for the size of the specified file.
	 * @param file the file 
	 * @return the size in bytes or -1 if the size could not be retrieved
	 */
	public long getSize(GrisuFileObject file);

	/**
	 * Queries for the last modified date of the specified file.
	 * @param file the file
	 * @return the last modified date (in unix-time) or -1 if the date could not be retrieved
	 */
	public long getLastModifiedDate(GrisuFileObject file);
	
	public boolean equals(Object other);
	public int hashCode();

	/**
	 * Returns the BackendFileObject with the specified filename
	 * @param grisuFileObject the parent folder
	 * @param filename the filename 
	 * @param refresh whether to refresh the parent
	 * @return the child BackendFileObject or null if there is no file with that filename in that folder
	 */
	public GrisuFileObject getChild(GrisuFileObject grisuFileObject, String filename, boolean refresh);

	
	/**
	 * Returns whether the file exists or not.
	 * @return true if the file exists. False if it doesn't exist or is not in this filesystem.
	 */
	public boolean exists(GrisuFileObject file);

}
