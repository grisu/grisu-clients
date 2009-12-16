package org.vpac.grisu.client.model.files;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.InformationError;
import org.vpac.grisu.client.control.files.FileManagerTransferHelpers;
import org.vpac.grisu.client.control.files.MountPointHelpers;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.FileHelpers;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RemoteFileSystemBackend implements FileSystemBackend {

	static final Logger myLogger = Logger
			.getLogger(RemoteFileSystemBackend.class.getName());

	final public static String REMOTE_FILE_TAGNAME = "File";
	final public static String REMOTE_FOLDER_TAGNAME = "Directory";
	final public static String LOCAL_FILE_TAGNAME = "File";
	final public static String LOCAL_FOLDER_TAGNAME = "Folder";
	final public static String LOCAL_DOCUMENTROOT_TAGNAME = "FileSystem";

	// --------------------------------------------------------------------------------
	// the xml stuff
	private static DocumentBuilder docBuilder = null;

	private final static XPath xpath = getXPath();

	public static DocumentBuilder getDocBuilder() {

		if (docBuilder == null) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			try {
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return docBuilder;
	}

	private final static XPath getXPath() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		return xpath;
	}

	private String site = null;

	private String alias = null;

	private URI rootUri = null;

	private File localCacheRoot = null;

	private Document fileStructure = null;

	private ServiceInterface serviceInterface = null;

	private EnvironmentManager em = null;

	private Element root = null;

	private GrisuFileObject rootObject = null;

	public RemoteFileSystemBackend(EnvironmentManager em, URI rootUri,
			String alias) {

		this.em = em;
		this.serviceInterface = em.getServiceInterface();
		this.rootUri = stripTrailingFileSeperator(rootUri);
		this.alias = alias;

		// create xml root
		fileStructure = getDocBuilder().newDocument();
	}

	// end xml stuff
	// ---------------------------------------------------------------------------------------

	/**
	 * Adds a file element with the specified file name as textcontent to the
	 * element in the xml cache document
	 * 
	 * @param element
	 *            element the element where the file element should be added as
	 *            child
	 * @param filename
	 *            the name of the file to be added
	 */
	private void addFile(Element element, String filename) {
		addFile(element, filename, (Map<String, String>) null);
	}
	/**
	 * Adds a file element with the specified properties (attribute-key/value
	 * pairs) to the element in the xml cache document
	 * 
	 * @param element
	 *            the element where the file element should be added as child
	 * @param filename
	 *            the name of the file to be added
	 * @param fileProperties
	 *            properties to add as attributes to the new element
	 */
	private void addFile(Element element, String filename,
			Map<String, String> fileProperties) {

		Element fileElement = fileStructure.createElement(LOCAL_FILE_TAGNAME);

		if (fileProperties != null) {
			for (String key : fileProperties.keySet()) {
				fileElement.setAttribute(key, fileProperties.get(key));
				// Attr fileProperty = fileStructure.createAttribute(key);
				// fileProperty.setTextContent(fileProperties.get(key));
				// fileElement.appendChild(fileProperty);
			}
		}
		fileElement.setTextContent(filename);
		element.appendChild(fileElement);
	}
	private void addFile(Element element, String filename, String size) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("size", size);
		addFile(element, filename, properties);
	}

	/**
	 * Adds a folder with the specified path attribute to the element in the xml
	 * cache document
	 * 
	 * @param element
	 *            the element where the folder element should be added as child
	 * @param path
	 *            the path attribute of the new folder element
	 */
	private void addFolder(Element element, String path) {
		addFolder(element, path, null);
	}

	/**
	 * Adds a folder element with the specified properties (attribute-key/value
	 * pairs) to the element in the xml cache document
	 * 
	 * @param element
	 *            the element where the folder element should be added as child
	 * @param path
	 *            the path attribute of the new folder element
	 * @param folderProperties
	 *            properties to add as attributes to the new element
	 */
	private void addFolder(Element element, String path,
			Map<String, String> folderProperties) {
		Element folderElement = fileStructure
				.createElement(LOCAL_FOLDER_TAGNAME);
		Attr path_attribute = fileStructure.createAttribute("path");
		path_attribute.setTextContent(path);
		folderElement.setAttributeNode(path_attribute);

		if (folderProperties != null) {
			for (String key : folderProperties.keySet()) {
				folderElement.setAttribute(key, folderProperties.get(key));
				// Attr fileProperty = fileStructure.createAttribute(key);
				// fileProperty.setTextContent(folderProperties.get(key));
				// folderElement.appendChild(fileProperty);
			}
		}

		element.appendChild(folderElement);
	}
	private GrisuFileObject createFromXmlElement(Element element)
			throws FileSystemException {

		// System.out.println(SeveralXMLHelpers.convertToString(element));

		// Document doc = ((Document)element.getParentNode());
		//		
		// System.out.println(SeveralXMLHelpers.toStringWithoutAnnoyingExceptions(doc));

		// filetype
		String type = element.getTagName();
		if (LOCAL_FILE_TAGNAME.equals(type)) {
			String name = element.getTextContent();
			myLogger.debug("creating file object for node: "
					+ element.getTextContent());
			if (element.getParentNode() instanceof Document) {
				myLogger
						.error("Element is of wrong type. This is a debug message to find out what's wrong.");
				try {
					myLogger.debug(SeveralXMLHelpers
							.toString((Document) element.getParentNode()));
					myLogger.debug("------");
					myLogger.error("Element is of wrong type. Returning null");
					throw new FileSystemException(
							"Gridftp server returned wrong format. Exiting.");
				} catch (TransformerFactoryConfigurationError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String uri_string = null;
			try {
				uri_string = ((Element) (element.getParentNode()))
						.getAttribute("path")
						+ "/" + name;
			} catch (RuntimeException e) {
				// hm. not sure what happens here
				e.printStackTrace();
				throw (RuntimeException) e;
			}
			URI uri = null;
			try {
				// dodgy, your name is markus
				uri_string = uri_string.replaceAll(" ", "%20");
				uri = new URI(uri_string);
			} catch (URISyntaxException e) {
				myLogger
						.error("Could not parse uri string. This should never happen: "
								+ uri_string);
				throw new RuntimeException(
						"Could not parse uri string. This should never happen: "
								+ uri_string);
			}
			GrisuFileObject fileObject = new GrisuFileObject(this, uri,
					FileConstants.TYPE_FILE);
			long size = -1;
			try {
				size = Long.parseLong(element.getAttribute("size"));
			} catch (NumberFormatException e) {
				// whatever
			}
			if (size != -1)
				fileObject.setSize(size);

			return fileObject;

		} else if (LOCAL_FOLDER_TAGNAME.equals(type)) {
			String uri_string = ((Element) element).getAttribute("path");
			URI uri = null;
			try {
				uri_string = uri_string.replaceAll(" ", "%20");
				uri = new URI(uri_string);
			} catch (URISyntaxException e) {
				myLogger
						.error("Could not parse uri string. This should never happen: "
								+ uri_string);
				throw new RuntimeException(
						"Could not parse uri string. This should never happen: "
								+ uri_string);
			}
			return new GrisuFileObject(this, uri, FileConstants.TYPE_FOLDER);
		} else {
			throw new RuntimeException(
					"File type seems to be neither file nor folder. That's bad...");
		}

	}
	public boolean deletePossibleLocalCacheFile(GrisuFileObject grisuFileObject) {

		String relativePath = getPathRelativeToRoot(grisuFileObject);
		if (relativePath == null) {
			return false;
		}

		relativePath = relativePath.replace('/', File.separator.charAt(0));

		File cachedFile = new File(getLocalCacheRoot(), relativePath);

		if (cachedFile.exists()) {
			return FileHelpers.deleteDirectory(cachedFile);
		} else {
			return false;
		}

	}
	/**
	 * Downloads a remote file if there is no copy with the same timestamp in
	 * the local cache.
	 * 
	 * @param url
	 *            the url of the file. Has to be within one of the user's
	 *            filesystems
	 * @return the file object which is located in the local cache (in
	 *         .grisu/cache)
	 * @throws Exception
	 *             if something goes wrong
	 */
	public File downloadAndPutIntoCache(GrisuFileObject file) throws Exception {

		try {
			FileManagerTransferHelpers.download(serviceInterface,
					getLocalCacheRoot(), getRootUri().toString(),
					new GrisuFileObject[] { file },
					FileManagerTransferHelpers.OVERWRITE_ONLY_OLDER_FILES,
					false);

			// test whether everything worked
			String relativePath = file.getPathRelativeToRootOfFileSystem();
			String cacheFilePath = getLocalCacheRoot().toString()
					+ File.separator + relativePath;

			File cacheFile = new File(cacheFilePath);
			if (!cacheFile.exists()) {
				throw new Exception(
						"Something went wrong that should not have... Can't locate file in cache.");
			}
			return cacheFile;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	public boolean equals(Object other) {
		if (other instanceof RemoteFileSystemBackend) {
			RemoteFileSystemBackend otherFS = (RemoteFileSystemBackend) other;

			if (otherFS.getAlias().equals(getAlias())
					&& otherFS.getRoot().equals(getRoot())) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	public boolean exists(GrisuFileObject file) {

		if (!isInFileSystem(file))
			return false;

		try {
			serviceInterface.fileExists(file.getURI().toString());
		} catch (Exception e) {
			myLogger
					.error("Could not connect to filesystem to establish whether file exists or not: "
							+ file.getURI().toString());
			return false;
		}

		return true;
	}

	private Element findElementInCache(URI uri) {

		myLogger.debug("Finding node: " + uri.toString());

		Element node = getElementFromXmlCache(uri);
		if (node == null) {
			try {
				String parent_uri_string = uri.toString().substring(0,
						uri.toString().lastIndexOf("/"));
				URI parent_uri = new URI(parent_uri_string);
				myLogger
						.debug("Trying to retrieve possible existing parent node...");
				refreshNode(parent_uri);
				myLogger.debug("Ok. Refreshed parent: " + parent_uri_string);
				node = getElementFromXmlCache(uri);
				if (node == null) {
					myLogger
							.debug("Damn. Still no luck. Throwing exception...");
					throw new FileSystemException(
							"Could not create local cache to mirror remote filesystem for uri: "
									+ uri.toString());
				} else {
					myLogger
							.debug("Great. After refreshing the parent there is now a local cache element for: "
									+ uri.toString());
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			myLogger.debug("Found matching element. Good. I'll use that.");
		}
		return node;
	}

	public String getAlias() {
		return alias;
	}

	public GrisuFileObject getChild(GrisuFileObject folder, String filename,
			boolean refresh) {

		if (filename == null || filename.length() == 0) {
			return null;
		}

		NodeList children = getChildNodesList(folder, refresh);

		for (int i = 0; i < children.getLength(); i++) {
			String name = ((Element) children.item(i)).getTextContent();
			if (filename.equals(name)) {
				return createFromXmlElement((Element) children.item(i));
			}
		}
		return null;
	}

	private NodeList getChildNodesList(GrisuFileObject folder, boolean refresh) {
		Element node = null;
		try {
			node = findElementInCache(folder.getURI());
		} catch (Exception e) {
			myLogger.error("Could not retrieve object for uri "
					+ folder.getURI().toString() + ": "
					+ e.getLocalizedMessage());
			return null;
		}

		if (node == null || node.getChildNodes().getLength() == 0 || refresh) {
			node = refreshNode(folder.getURI());
		}
		NodeList children = node.getChildNodes();
		return children;
	}

	public GrisuFileObject[] getChildren(GrisuFileObject folder) {
		return getChildren(folder, false);
	}

	public GrisuFileObject[] getChildren(GrisuFileObject folder, boolean refresh)
			throws FileSystemException {

		NodeList children = getChildNodesList(folder, refresh);

		GrisuFileObject[] childObjects = new GrisuFileObject[children
				.getLength()];
		for (int i = 0; i < children.getLength(); i++) {
			childObjects[i] = createFromXmlElement((Element) children.item(i));
		}
		return childObjects;
	}

	/**
	 * Tries to find the node within the local cache xml document that mirrors
	 * the remote url. If the url is the root url of this fileSystem, it will
	 * actually return the first child of the root xml element.
	 * 
	 * @param uri
	 *            the remote url
	 * @return the node or null if the remote url is not cached (yet).
	 * @throws FileSystemException
	 *             if there is more than one node in the xml document that seems
	 *             to mirror the remote url, which would be bad and should never
	 *             happen
	 */
	private Element getElementFromXmlCache(URI uri) throws FileSystemException {
		String path = uri.toString();

		if (stripTrailingFileSeperator(uri).equals(
				stripTrailingFileSeperator(rootUri))) {
			myLogger
					.debug("Url is root of this filesystem. Using root node...)");
			return root;
		}

		myLogger.debug("Document before searching for uri: " + uri.toString());

		String expression = "//Folder[@path='" + path + "']";
		NodeList resultNodes = null;
		myLogger
				.debug("Trying to get proper xml element from xml cache tree...");
		try {
			resultNodes = (NodeList) xpath.evaluate(expression, fileStructure,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (resultNodes.getLength() == 0) {
			myLogger.debug("No matching folder element found. Trying file...");
			int index = path.lastIndexOf("/");
			String filename = path.substring(index + 1);
			path = path.substring(0, index);
			expression = "//*[local-name()='Folder'][@path='" + path
					+ "']/child::node()[local-name()='File'][text()='"
					+ filename + "']";
			try {
				resultNodes = (NodeList) xpath.evaluate(expression,
						fileStructure, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}

			if (resultNodes.getLength() == 0) {
				// well, nothing found
				return null;
			} else if (resultNodes.getLength() == 1) {
				myLogger.debug("Found exactly one cached xml element.");
				return (Element) resultNodes.item(0);
			} else {
				throw new FileSystemException(
						"No unique folder found with the path: "
								+ uri.toString());
			}

		} else if (resultNodes.getLength() == 1) {
			myLogger.debug("Found exactly one cached xml element.");
			return (Element) resultNodes.item(0);
		} else {
			myLogger.debug("Crap. Something's really wrong.");
			throw new FileSystemException(
					"No unique folder found with the path: " + uri.toString());
		}
	}

	public EnvironmentManager getEnvironmentManager() {
		return em;
	}

	public GrisuFileObject getFileObject(URI uri) {
		Element node = null;
		try {
			node = findElementInCache(stripTrailingFileSeperator(uri));
		} catch (Exception e) {
			myLogger.error("Could not retrieve object for uri " + uri + ": "
					+ e.getLocalizedMessage());
			return null;
		}
		return createFromXmlElement(node);
	}

	public long getLastModifiedDate(GrisuFileObject file) {

		if (!isInFileSystem(file)) {
			return -1;
		}
		long date = -1;

		try {
			date = serviceInterface.lastModified(file.getURI().toString());
		} catch (Exception e) {
			myLogger.error("Could not retrieve last modified dte for file: "
					+ file.getURI().toString());
			return -1;
		}
		return date;
	}

	public File getLocalCacheFile(GrisuFileObject file, boolean refresh) {

		String relativePath = getPathRelativeToRoot(file);
		if (relativePath == null) {
			return null;
		}

		relativePath = relativePath.replace('/', File.separator.charAt(0));

		File cachedFile = new File(getLocalCacheRoot(), relativePath);

		if (refresh || !cachedFile.exists()) {
			try {
				downloadAndPutIntoCache(file);
			} catch (Exception e) {
				e.printStackTrace();
				throw new FileSystemException(
						"Could not download file and put into cache: "
								+ file.getURI().toString(), e);
			}
		}

		return cachedFile;

	}

	public File getLocalCacheFile(String path, boolean refresh) {
		URI uri = null;
		try {
			uri = new URI(path);
		} catch (URISyntaxException e) {
			try {
				uri = new URI(rootUri.toString() + "/" + path);
			} catch (URISyntaxException e1) {
				return null;
			}
		}
		return getLocalCacheFile(getFileObject(uri), refresh);
	}

	public File getLocalCacheRoot() {
		if (localCacheRoot == null) {
			localCacheRoot = new File(Environment.getLocalJobCacheDirectory(),
					alias);
		}
		return localCacheRoot;
	}

	public String getPathRelativeToRoot(GrisuFileObject file) {
		if (isInFileSystem(file)) {
			if (isRoot(file)) {
				return "";
			} else {
				// return
				// file.getURI().toString().substring(rootUri.toString().length()+1);
				return file.getURI().toString().substring(
						rootUri.toString().length() + 1);
			}
		} else {
			return null;
		}
	}

	public synchronized GrisuFileObject getRoot() {
		if (rootObject == null) {
			if (root == null) {
				initFileSystemBackendRoot();
			}
			rootObject = createFromXmlElement(root);
		}
		return rootObject;
	}

	public URI getRootUri() {
		return rootUri;
	}

	/**
	 * Tries to extract the site from the root url of this FileSystem. This is
	 * not a really exact science, so if it does not work for you, have a look
	 * at the {@link MountPointHelpers#getSiteFromMountPointUrl(String)} method
	 * and reimplement/improve it.
	 * 
	 * @return the site of this file system (like: vpac, sapac,...)
	 * @throws InformationError
	 */
	public String getSite() throws InformationError {
		if (site == null) {
			// site =
			// MountPointHelpers.getSiteFromMountPointUrl(rootUri.toString());
			site = em.lookupSite(EnvironmentManager.FILE_URL_TYPE, rootUri
					.toString());
			if (site == null) {
				throw new InformationError("Can't find site for url: "
						+ rootUri);
			}
			myLogger
					.debug("Looked up site for \"" + getAlias() + "\": " + site);
		}
		return site;
	}

	public long getSize(GrisuFileObject file) {

		if (!isInFileSystem(file))
			return -1;

		long size = -1;

		try {
			size = serviceInterface.getFileSize(file.getURI().toString());
		} catch (Exception e) {
			myLogger.error("Could not retrieve filesize for file: "
					+ file.getURI().toString());
			return -1;
		}

		return size;
	}

	public int hashcode() {
		return getAlias().hashCode() + getRootUri().hashCode();
	}

	private void initFileSystemBackendRoot() {
		myLogger.debug("Created filesystem root for filesystem: "
				+ rootUri.toString());

		boolean isDirectory = true;
		if (serviceInterface == null || "file".equals(rootUri.getScheme())) {
			myLogger.debug("Filesystem is local.");
			throw new FileSystemException("Could not init remote file system "
					+ rootUri.toString());
		} else {
			myLogger
					.debug("Filesystem is remote. Good. Now checking whether the root is a folder.");
			// remote ServiceInterface

			try {
				isDirectory = serviceInterface.isFolder(rootUri.toString());
			} catch (Exception e) {
				myLogger.error("Could not check whether root url is folder: "
						+ e.getLocalizedMessage());
				throw new FileSystemException(
						"Could not check whether root url is folder." + e);
			}
		}
		if (!isDirectory) {
			try {
				myLogger.debug("Creating folder: " + rootUri.toString());
				serviceInterface.mkdir(rootUri.toString());
				isDirectory = serviceInterface.isFolder(rootUri.toString());
			} catch (Exception e) {
				myLogger.error("Could not create/query folder: "
						+ rootUri.toString());
				e.printStackTrace();
			}
		}

		if (isDirectory) {
			myLogger.debug("Filesystem root is directory.");
			root = fileStructure.createElement(LOCAL_FOLDER_TAGNAME);
			Attr path_attribute = fileStructure.createAttribute("path");
			path_attribute.setTextContent(rootUri.toString());
			root.setAttributeNode(path_attribute);
			fileStructure.appendChild(root);
		} else {
			// well, that does not make much sense, does it? Anyway. Creating
			// xml document.
			myLogger.debug("Filesystem root is file.");
			root = fileStructure.createElement(LOCAL_FILE_TAGNAME);
			root.setTextContent(root.toString());
			fileStructure.appendChild(root);
		}
	}

	public boolean isInFileSystem(GrisuFileObject file) {
		return file.getURI().toString().startsWith(rootUri.toString());
	}

	public boolean isRoot(GrisuFileObject file) {
		return file.equals(getRoot());
	}

	/**
	 * Sorts the result of the remote ls command into the local xml cache
	 * document
	 * 
	 * @param localNode
	 *            the node that represents the cached local root of the ls
	 *            command
	 * @param remoteLsResult
	 *            the result of the remote ls command
	 */
	private void putInRemoteFileStructureInXml(Element localNode,
			Document remoteLsResult) {

		Element remoteRoot = (Element) remoteLsResult.getFirstChild()
				.getFirstChild();

		String rootType = remoteRoot.getTagName();
		if (REMOTE_FILE_TAGNAME.equals(rootType)) {
			// well, that's of no use
			myLogger
					.debug("Tried to update local xml cache but the path is a file, so I'm doing nothing.");
			return;
		} else {
			String local_path = stripTrailingFileSeperator(localNode
					.getAttribute("path"));
			String remote_path = stripTrailingFileSeperator(remoteRoot
					.getAttribute("path"));
			myLogger.debug("Updating local xml cache for local path: "
					+ local_path + " and remote path: " + remote_path);
			if (!local_path.equals(remote_path)) {
				if (!local_path.replace(":2811", "").equals(remote_path)) {
					if (!local_path
							.equals(em.convertToAbsoluteUrl(remote_path))) {
						myLogger
								.error("Remote and local cache path or the element are not the same. Don't do anything...");
						return;
					}
				}
			}
		}

		// ok. now we can remove the old childs and add the new ones.
		while (localNode.getFirstChild() != null) {
			localNode.removeChild(localNode.getFirstChild());
		}

		myLogger.debug("Getting remote children...");
		NodeList remoteChildren = remoteRoot.getChildNodes();
		if (remoteChildren.getLength() == 0) {
			myLogger
					.debug("No remote children. Not putting anything into cache.");
			return;
		}

		// now insert all remote childrenanObject
		for (int i = 0; i < remoteChildren.getLength(); i++) {
			Element remoteChild = (Element) remoteChildren.item(i);
			String remoteFileType = remoteChild.getTagName();
			if (REMOTE_FILE_TAGNAME.equals(remoteFileType)) {
				String filename = remoteChild.getAttribute("name");
				String size = remoteChild.getAttribute("size");
				myLogger.debug("Adding file to xml cache: " + filename);
				addFile(localNode, filename, size);
			} else {
				String foldername = remoteChild.getAttribute("path");
				myLogger.debug("Adding folder to xml cache: " + foldername);
				addFolder(localNode, foldername);
			}
		}
	}

	public void refresh(GrisuFileObject file) {

		refreshNode(file.getURI());

	}

	private Element refreshNode(URI uri) throws FileSystemException {

		myLogger.debug("Refreshing node: " + uri.toString());

		Element node = findElementInCache(stripTrailingFileSeperator(uri));
		myLogger
				.debug("Now getting a list of the childs of: " + uri.toString());
		DtoFolder remoteChildren;
		try {
			remoteChildren = serviceInterface.ls(uri.toString(), 1);
		} catch (RemoteFileSystemException e) {
			throw new FileSystemException("Could not list directory: "
					+ uri.toString(), e);
		}
		Document xml = DtoFolderToXmlConverter.convert(remoteChildren, 1);
		putInRemoteFileStructureInXml(node, xml);

		return node;
	}

	private String stripTrailingFileSeperator(String uri_string) {
		if (uri_string.endsWith("/") || uri_string.endsWith(File.separator)) {
			uri_string = uri_string.substring(0, uri_string.length() - 1);
		}
		return uri_string;
	}

	// helper methods
	// --------------
	private URI stripTrailingFileSeperator(URI uri) {
		String new_uri_string = stripTrailingFileSeperator(uri.toString());

		URI new_URI = null;
		try {
			new_URI = new URI(new_uri_string);
		} catch (URISyntaxException e) {
			// will never happen
		}
		return new_URI;
	}

}
