

package org.vpac.grisu.client.control.files;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.exceptions.FailedDownloadsException;
import org.vpac.grisu.client.control.exceptions.InformationError;
import org.vpac.grisu.client.control.utils.progress.DummyDisplay;
import org.vpac.grisu.client.control.utils.progress.ProgressDisplay;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.utils.FileHelpers;

/**
 * Some static helper methods to do file transfers and
 * downloads.
 * 
 * @author Markus Binsteiner
 *
 */
public class FileManagerTransferHelpers {

	static final Logger myLogger = Logger
			.getLogger(FileManagerTransferHelpers.class.getName());

	public static final int OVERWRITE_EVERYTHING = 0;
	public static final int OVERWRITE_ONLY_OLDER_FILES = 1;
	public static final int DONT_OVERWRITE = 2;

	// if this is not initialized it will do nothing
	public static ProgressDisplay progressDisplay = new DummyDisplay();

	/**
	 * A convenience method that determines whether the source files and the target
	 * directory are local or remote and then calls the appropriate copy methods.
	 * 
	 * For now I wouldn't really recommend to set "displayProgress" to true since it
	 * isn't tested very well. If you do, you have to initialize the progressDisplay first
	 * with something like this:
	 * 
	 * <code>FileManagerTransferHelpers.progressDisplay = new SwingProgressDisplay(application.getJFrame());</code>
	 * Otherwise you won't see anything. You can implement your own progress viewer with implementing
	 * {@link ProgressDisplay
	 * 
	 * @param serviceInterface the serviceInterface
	 * @param sourceFiles an array of source files
	 * @param targetDirectory the target directory
	 * @param displayProgress whether to display a jdialog that shows the progress or not. 
	 * @throws InformationError if the filesystem of one of the files can't be looked up
	 * @throws FileSystemException 
	 */
	public static void transferFiles(ServiceInterface serviceInterface,
			GrisuFileObject[] sourceFiles, GrisuFileObject targetDirectory,
			boolean displayProgress) throws FileSystemException, InformationError {

		try {

			if (displayProgress)
				progressDisplay.start(sourceFiles.length + 1, "Transfering "
						+ sourceFiles.length + " files...");

			int progress = 0;
			// TODO improve that so all files are transfered at once
			for (GrisuFileObject sourceFile : sourceFiles) {

				if (displayProgress) {
					progress++;
					progressDisplay.setProgress(progress, "Transfering file: "
							+ sourceFile.getName());
				}
				myLogger.debug("Copying: " + sourceFile.getURI().toString()
						+ " to: " + targetDirectory.getURI().toString());

				if (FileConstants.LOCAL_NAME.equals(sourceFile
						.getFileSystemBackend().getSite())
						&& FileConstants.LOCAL_NAME.equals(targetDirectory
								.getFileSystemBackend().getSite())) {
					transferFileFromLocalToLocal(sourceFile, targetDirectory);
				} else if (FileConstants.LOCAL_NAME.equals(sourceFile
						.getFileSystemBackend().getSite())
						&& !FileConstants.LOCAL_NAME.equals(targetDirectory
								.getFileSystemBackend().getSite())) {
					transferFileFromLocalToRemote(serviceInterface, sourceFile,
							targetDirectory);
				} else if (!FileConstants.LOCAL_NAME.equals(sourceFile
						.getFileSystemBackend().getSite())
						&& !FileConstants.LOCAL_NAME.equals(targetDirectory
								.getFileSystemBackend().getSite())) {
					transferFileFromRemoteToRemote(serviceInterface,
							sourceFile, targetDirectory);
				} else if (!FileConstants.LOCAL_NAME.equals(sourceFile
						.getFileSystemBackend().getSite())
						&& FileConstants.LOCAL_NAME.equals(targetDirectory
								.getFileSystemBackend().getSite())) {
					transferFileFromRemoteToLocal(serviceInterface, sourceFile,
							targetDirectory);
				}
			}

		} finally {
			if (displayProgress)
				progressDisplay.close();
		}
		targetDirectory.refresh();

	}

	private static void transferFileFromLocalToLocal(
			GrisuFileObject sourceFile, GrisuFileObject targetDirectory) {

		File localSourceFile = new File(sourceFile.getURI());
		File localTargetDirectory = new File(targetDirectory.getURI());

		if (!localTargetDirectory.isDirectory()) {
			throw new FileSystemException("Local target is not a directory: "
					+ localTargetDirectory.toString());
		}

		FileHelpers.copyFileIntoDirectory(new File[] { new File(sourceFile
				.getURI()) }, new File(targetDirectory.getURI()));

	}

	private static void transferFileFromRemoteToRemote(
			ServiceInterface serviceInterface, GrisuFileObject sourceFile,
			GrisuFileObject targetDirectory) throws FileSystemException {
		try {
			serviceInterface.cp(sourceFile.getURI().toString(), targetDirectory
					.getURI().toString()
					+ "/" + sourceFile.getName(), false, true);
		} catch (Exception e) {
			throw new FileSystemException("Could not copy file: "
					+ sourceFile.getURI().toString() + " to: "
					+ targetDirectory.getURI().toString());
		}
	}

	private static void transferFileFromRemoteToLocal(
			ServiceInterface serviceInterface, GrisuFileObject sourceFile,
			GrisuFileObject targetDirectory) throws FileSystemException {
		try {
			GrisuFileObject parent = sourceFile.getParent();
			download(serviceInterface, new File(targetDirectory.getURI()),
					parent.getURI().toString(),
					new GrisuFileObject[] { sourceFile }, DONT_OVERWRITE,
					false);
		} catch (FailedDownloadsException e) {
			e.printStackTrace();
			throw new FileSystemException("Could not download file: "
					+ sourceFile.getURI().toString());
		}
	}

	private static void transferFileFromLocalToRemote(
			ServiceInterface serviceInterface, GrisuFileObject sourceFile,
			GrisuFileObject targetDirectory) throws FileSystemException {

		if (sourceFile.getType() == FileConstants.TYPE_FOLDER) {

			GrisuFileObject[] children = sourceFile.getFileSystemBackend()
					.getChildren(sourceFile, false);
			for (GrisuFileObject child : children) {
				URI newFolder = null;
				try {
					newFolder = new URI(targetDirectory.getURI().toString()
							+ "/" + sourceFile.getName());
					serviceInterface.mkdir(newFolder.toString());
					targetDirectory.refresh();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				transferFileFromLocalToRemote(serviceInterface, child,
						targetDirectory.getFileSystemBackend().getFileObject(
								newFolder));
			}

		} else {

			DataSource source = new FileDataSource(
					new File(sourceFile.getURI()));
			String filename = sourceFile.getName();
			try {

				serviceInterface.upload(new DataHandler(source), targetDirectory.getURI()
						.toString()
						+ "/" + filename, true);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new FileSystemException("Could not upload file \""
						+ sourceFile.getName() + "\": "
						+ e1.getLocalizedMessage());
			}
		}
	}

	/**
	 * Downloads a bunch of files into a folder on the local harddrive.
	 * 
	 * @param serviceInterface
	 *            the ServiceInterface to use to download the files
	 * @param target_folder
	 *            the folder on the local disk where the files should be stored
	 * @param source_folder
	 *            the remote folder where the source files are located. This is
	 *            used as base folder to determine into which subfolders the
	 *            files should be stored locally
	 * @param source_files
	 *            a list of files to download
	 * @param overwrite_mode
	 *            whether local cache files should be overwritten or not
	 *            (normally you would use OVERWRITE_ONLY_OLDER_FILES, esp if you
	 *            use the grisu internal caching)
	 * @param displayProgress
	 *            whether a progress display should be shown or not
	 * @throws FailedDownloadsException
	 *             if one or more files could not be downloaded. The thrown
	 *             exception contains a list of all failed files.
	 */
	public static void download(ServiceInterface serviceInterface,
			File target_folder, String source_folder,
			GrisuFileObject[] source_files, int overwrite_mode,
			boolean displayProgress) throws FailedDownloadsException {

		Map<String, Exception> failed_downloads = new HashMap<String, Exception>();

		String[] allChilds = new String[0];
		for (GrisuFileObject remoteFile : source_files) {
			String[] childs = null;
			try {
				childs = serviceInterface.getChildrenFileNames(remoteFile.getURI()
						.toString(), false);
			} catch (Exception e) {
				failed_downloads.put(remoteFile.getURI().toString(), e);
				break;
			}
			String[] temp = allChilds;
			allChilds = new String[allChilds.length + childs.length];
			System.arraycopy(temp, 0, allChilds, 0, temp.length);
			System.arraycopy(childs, 0, allChilds, temp.length, childs.length);
		}
		if (displayProgress)
			progressDisplay.start(allChilds.length + 1, "Downloading "
					+ allChilds.length + " files...");

		int progress = 0;

		for (String child : allChilds) {
			try {
				if (displayProgress) {
					progress++;
					progressDisplay.setProgress(progress, "Downloading file: "
							+ child.substring(source_folder.length() + 1));
				}
				download(serviceInterface, target_folder, source_folder, child,
						overwrite_mode);
			} catch (FailedDownloadsException e) {
				failed_downloads.putAll(e.getFailedDownloads());
			}
		}
		if (displayProgress)
			progressDisplay.close();

		if (failed_downloads.size() > 0) {
			throw new FailedDownloadsException(failed_downloads);
		}

	}

	/**
	 * Used to download a single file over a web service interface.
	 * 
	 * @param serviceInterface
	 *            the ServiceInterface to use to download the file
	 * @param target_folder
	 *            the folder on the local disk where the file should be stored
	 * @param source_folder
	 *            the remote folder where the source file is located. This is
	 *            used as base folder to determine into which subfolders the
	 *            file should be stored locally
	 * @param remoteFile
	 *            the file to download
	 * @param overwrite_mode
	 *            whether to overwrite a possible already existing file or not
	 * @throws FailedDownloadsException
	 *             if the file could not be downloaded. The thrown exception
	 *             contains a list with the (single) failed file.
	 */
	private static void download(ServiceInterface serviceInterface,
			File target_folder, String source_folder, String remoteFile,
			int overwrite_mode) throws FailedDownloadsException {

		Map<String, Exception> failed_downloads = new HashMap<String, Exception>();

		boolean isFolder = false;
		try {
			isFolder = serviceInterface.isFolder(remoteFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			failed_downloads.put(remoteFile, e1);
			throw new FailedDownloadsException(failed_downloads);
		}

		if (isFolder) {

			// String[] children = null;
			// try {
			// children = serviceInterface.getChildrenFiles(remoteFile, false);
			// } catch (Exception e1) {
			// failed_downloads.put(remoteFile, e1);
			// throw new FailedDownloadsException(failed_downloads);
			// }

			// String folderName = remoteFile.substring(remoteFile
			// .lastIndexOf("/") + 1);
			// String folderName =
			// folderName_temp.substring(folderName_temp.lastIndexOf("/")+1);

			String folderName = remoteFile.substring(source_folder.length());

			File folder = new File(target_folder, folderName);
			if (!folder.exists()) {
				boolean successFullyCreatedFolder = folder.mkdirs();
				if (successFullyCreatedFolder) {
					myLogger.debug("Successfully created folder: "
							+ folder.toString());
				} else {
					myLogger.error("Could not create folder: "
							+ folder.toString());
				}
			}
			if (!folder.exists()) {
				failed_downloads.put(remoteFile, new IOException(
						"Could not create folder: " + folder.toString()));
				throw new FailedDownloadsException(failed_downloads);
			}
			// for (String file : children) {
			// try {
			// download(serviceInterface, folder, source_folder, file,
			// overwrite_mode);
			// } catch (FailedDownloadsException fde) {
			// failed_downloads.putAll(fde.getFailedDownloads());
			// }
			// }

		} else {
			// downloading of the actual file

			switch (overwrite_mode) {

			case OVERWRITE_EVERYTHING:
				try {
					downloadAndOverwrite(serviceInterface, remoteFile,
							source_folder, target_folder);
				} catch (Exception e) {
					e.printStackTrace();
					failed_downloads.put(remoteFile, e);
				}
				;
				break;
			case OVERWRITE_ONLY_OLDER_FILES:
				try {
					downloadOnlyNewerFiles(serviceInterface, remoteFile,
							source_folder, target_folder);
				} catch (Exception e) {
					e.printStackTrace();
					failed_downloads.put(remoteFile, e);
				}
				;
				break;
			case DONT_OVERWRITE:
				try {
					downloadOnlyNonexistantFiles(serviceInterface, remoteFile,
							source_folder, target_folder);
				} catch (Exception e) {
					e.printStackTrace();
					failed_downloads.put(remoteFile, e);
				}
				;
				break;

			}
		}

		if (failed_downloads.size() > 0) {
			throw new FailedDownloadsException(failed_downloads);
		}
	}

	private static void downloadOnlyNewerFiles(
			ServiceInterface serviceInterface, String remoteFile,
			String source_folder, File target_folder) throws Exception {

		long lastModified = serviceInterface.lastModified(remoteFile);

		String filename = remoteFile.substring(remoteFile.lastIndexOf("/") + 1);
		File target_parent_folder = calculateParentFolderAndCreateItIfNotExistsAlready(
				remoteFile, source_folder, target_folder);

		File target = new File(target_parent_folder, filename);
		if (target.exists()) {
			// check last modified date
			long local_last_modified = target.lastModified();
			myLogger.debug("local file timestamp:\t" + local_last_modified);
			myLogger.debug("remote file timestamp:\t" + lastModified);
			if (local_last_modified >= lastModified) {
				myLogger
						.debug("Local cache file is not older than remote file. Doing nothing...");
				return;
			}
		}

		myLogger
				.debug("Remote file newer than local cache file or not cached yet, downloading new copy.");
		DataSource source = null;
		try {

			source = serviceInterface.download(remoteFile).getDataSource();
		} catch (Exception e) {
			e.printStackTrace();
			myLogger.error("Could not download file: " + remoteFile);
			throw e;
		}

		try {
			File newFile = new File(target_parent_folder, filename);
			FileHelpers.saveToDisk(source, newFile);
			newFile.setLastModified(lastModified);
		} catch (IOException e) {
			myLogger.error("Could not save file: "
					+ remoteFile.lastIndexOf("/") + 1);
			throw e;
		}

	}

	private static File calculateParentFolderAndCreateItIfNotExistsAlready(
			String remoteFile, String source_folder, File target_folder)
			throws IOException {

		File target_parent_folder = target_folder;

		String relativePath = remoteFile.substring(source_folder.length() + 1);
		if (relativePath.indexOf("/") != -1) {
			// means subfolder
			String relativeParentFolderPath = relativePath.substring(0,
					relativePath.lastIndexOf("/"));
			target_parent_folder = new File(target_folder,
					relativeParentFolderPath);
		}
		target_parent_folder.mkdirs();
		if (!target_parent_folder.exists()) {
			myLogger.error("Could not create folder: " + target_parent_folder);
			throw new IOException("Could not create folder: "
					+ target_parent_folder);
		}
		return target_parent_folder;
	}

	private static void downloadAndOverwrite(ServiceInterface serviceInterface,
			String remoteFile, String source_folder, File target_folder)
			throws Exception {

		DataSource source = null;
		long lastModified = -1;
		try {
			lastModified = serviceInterface.lastModified(remoteFile);
			source = serviceInterface.download(remoteFile).getDataSource();
		} catch (Exception e) {
			e.printStackTrace();
			myLogger.error("Could not download file: " + remoteFile);
			throw e;
		}

		String filename = remoteFile.substring(remoteFile.lastIndexOf("/") + 1);

		File target_parent_folder = calculateParentFolderAndCreateItIfNotExistsAlready(
				remoteFile, source_folder, target_folder);

		try {
			File newFile = new File(target_parent_folder, filename);
			FileHelpers.saveToDisk(source, newFile);
			newFile.setLastModified(lastModified);
		} catch (IOException e) {
			myLogger.error("Could not save file: "
					+ remoteFile.lastIndexOf("/") + 1);
			throw e;
		}

	}

	private static void downloadOnlyNonexistantFiles(
			ServiceInterface serviceInterface, String remoteFile,
			String source_folder, File target_folder) throws Exception {

		DataSource source = null;

		String filename = remoteFile.substring(remoteFile.lastIndexOf("/") + 1);

		File target_parent_folder = calculateParentFolderAndCreateItIfNotExistsAlready(
				remoteFile, source_folder, target_folder);
		File newFile = new File(target_parent_folder, filename);

		if (!newFile.exists()) {
			try {
				long lastModified = serviceInterface.lastModified(remoteFile);
				source = serviceInterface.download(remoteFile).getDataSource();
				FileHelpers.saveToDisk(source, newFile);
				newFile.setLastModified(lastModified);
			} catch (IOException e) {
				myLogger.error("Could not save file: "
						+ remoteFile.lastIndexOf("/") + 1);
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

}
