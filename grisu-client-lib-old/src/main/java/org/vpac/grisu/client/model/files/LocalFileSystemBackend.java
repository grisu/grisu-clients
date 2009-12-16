package org.vpac.grisu.client.model.files;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.utils.FileHelpers;

public class LocalFileSystemBackend implements FileSystemBackend {

	static final Logger myLogger = Logger
			.getLogger(LocalFileSystemBackend.class.getName());

	private String alias = null;
	private URI rootUri = null;

	private EnvironmentManager em = null;

	public LocalFileSystemBackend(EnvironmentManager em, URI rootUri,
			String alias) {
		this.em = em;
		this.alias = alias;
		if (rootUri.toString().equals("/")) {
			this.rootUri = rootUri;
		} else {
			this.rootUri = stripTrailingFileSeperator(rootUri);
			// System.out.println(rootUri.toString());
		}
	}

	// helper methods
	// ------------------------------------------------
	private GrisuFileObject createFromUri(URI uri) {
		String uriString = uri.toString();
		// if ( uriString.indexOf("file:///") == -1 ) {
		// uriString = uriString.replaceFirst("file:/", "file:///");
		// }
		File temp = null;
		URI newURI = null;
		try {
			newURI = new URI(uriString);
			temp = new File(newURI);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int type = FileConstants.TYPE_FILE;
		if (temp.isDirectory())
			type = FileConstants.TYPE_FOLDER;

		GrisuFileObject file = new GrisuFileObject(this, newURI, type);
		return file;
	}

	public boolean deletePossibleLocalCacheFile(GrisuFileObject grisuFileObject) {

		return FileHelpers.deleteDirectory(new File(grisuFileObject.getURI()));
	}

	public boolean exists(GrisuFileObject file) {

		if (!isInFileSystem(file)) {
			return false;
		}

		File temp = new File(file.getURI());
		if (temp.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public String getAlias() {
		return alias;
	}

	public GrisuFileObject getChild(GrisuFileObject grisuFileObject,
			String filename, boolean refresh) {

		File file = getFileObjectFromBackendFileObject(grisuFileObject);

		File newFile = new File(file, filename);

		if (newFile.exists()) {
			return createFromUri(newFile.toURI());
		} else {
			return null;
		}

	}

	public GrisuFileObject[] getChildren(GrisuFileObject folder) {
		return getChildren(folder, false);
	}

	public GrisuFileObject[] getChildren(GrisuFileObject folder, boolean refresh) {

		File file = getFileObjectFromBackendFileObject(folder);

		// System.out.println(file.toURI());
		File[] children = file.listFiles();
		GrisuFileObject[] childrenObjects = new GrisuFileObject[children.length];
		for (int i = 0; i < children.length; i++) {
			URI temp = children[i].toURI();
			childrenObjects[i] = createFromUri(temp);
		}
		return childrenObjects;
	}

	public EnvironmentManager getEnvironmentManager() {
		return em;
	}

	public GrisuFileObject getFileObject(URI uri) {
		if (!uri.toString().startsWith(rootUri.toString())) {
			return null;
		} else {
			return createFromUri(uri);
		}

	}

	private File getFileObjectFromBackendFileObject(
			GrisuFileObject grisuFileObject) {
		// this is pretty dodgy. Windows workaround :-(
		File file = null;
		String path = grisuFileObject.getURI().getPath();
		// file = new File(folder.getURI().getPath());
		if (path.endsWith(":")) {
			file = new File(path + "/");
		} else {
			file = new File(path);
		}
		return file;
	}

	public long getLastModifiedDate(GrisuFileObject file) {
		if (!isInFileSystem(file)) {
			return -1;
		}

		File temp = new File(file.getURI());
		if (temp.exists())
			return temp.lastModified();
		else
			return -1;
	}

	public File getLocalCacheFile(GrisuFileObject file, boolean refresh) {
		return new File(file.getURI());
	}

	public File getLocalCacheFile(String path, boolean refresh) {
		URI uri;
		try {
			uri = new URI(path);
		} catch (URISyntaxException e) {
			try {
				uri = new URI(rootUri.toString() + File.separator + path);
			} catch (URISyntaxException e1) {
				myLogger.error("This should never happen.");
				return null;
			}
		}

		return new File(uri);
	}

	public File getLocalCacheRoot() {
		return new File(rootUri);
	}

	public String getPathRelativeToRoot(GrisuFileObject file) {
		if (isInFileSystem(file)) {
			if (isRoot(file)) {
				return "";
			} else {
				return file.getURI().toString().substring(
						rootUri.toString().length());
			}
		} else {
			return null;
		}
	}

	public GrisuFileObject getRoot() {
		return createFromUri(rootUri);
	}

	public URI getRootUri() {
		return rootUri;
	}

	public String getSite() {
		return FileConstants.LOCAL_NAME;
	}

	public long getSize(GrisuFileObject file) {
		if (!isInFileSystem(file)) {
			return -1;
		}

		File temp = new File(file.getURI());
		if (temp.exists()) {
			return temp.length();
		} else {
			return -1;
		}
	}

	public boolean isInFileSystem(GrisuFileObject file) {
		URI temp = file.getURI();
		return temp.toString().startsWith(rootUri.toString());
	}

	public boolean isRoot(GrisuFileObject file) {
		return file.equals(getRoot());
	}

	public void refresh(GrisuFileObject file) {
		// not necessary for a local file system
	}

	private URI stripTrailingFileSeperator(URI uri) {
		String uri_string = uri.toString();
		if (uri_string.endsWith(File.separator) || uri_string.endsWith("/")) {
			try {
				String newString = uri.toString().substring(0,
						uri_string.length() - 1);
				URI new_uri = new URI(newString);
				return new_uri;
			} catch (URISyntaxException e) {
				// will never happen
			}
		}
		return uri;
	}

}
