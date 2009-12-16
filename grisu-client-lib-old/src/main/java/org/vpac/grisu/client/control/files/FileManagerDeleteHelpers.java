package org.vpac.grisu.client.control.files;

import java.io.File;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.exceptions.InformationError;
import org.vpac.grisu.client.control.utils.progress.DummyDisplay;
import org.vpac.grisu.client.control.utils.progress.ProgressDisplay;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.utils.FileHelpers;

public class FileManagerDeleteHelpers {

	static final Logger myLogger = Logger
			.getLogger(FileManagerDeleteHelpers.class.getName());

	// if this is not initialized it will do nothing
	public static ProgressDisplay progressDisplay = new DummyDisplay();

	public static boolean deleteFile(ServiceInterface serviceInterface,
			GrisuFileObject file) {

		String site;
		try {
			site = file.getFileSystemBackend().getSite();
		} catch (InformationError e1) {
			myLogger.error(e1.getLocalizedMessage());
			return false;
		}

		if (site.equals(FileConstants.LOCAL_NAME)) {
			File localFile = new File(file.getURI());
			if (localFile.exists()) {
				if (localFile.isDirectory()) {
					return FileHelpers.deleteDirectory(localFile);
				} else {
					return localFile.delete();
				}
			} else {
				return false;
			}
		} else {
			try {
				serviceInterface.deleteFile(file.getURI().toString());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new FileSystemException("Could not delete file: "
						+ file.getURI().toString());
			}
		}

	}

	public static void deleteFiles(ServiceInterface serviceInterface,
			GrisuFileObject[] files, boolean displayProgress) {

		if (displayProgress)
			progressDisplay.start(files.length + 1, "Deleting " + files.length
					+ " files...");

		int progress = 0;
		for (GrisuFileObject file : files) {

			if (displayProgress) {
				progress++;
				progressDisplay.setProgress(progress, "Deleting file: "
						+ file.getName());

				try {
					deleteFile(serviceInterface, file);
				} catch (FileSystemException e) {
					myLogger.error("Could not delete file: "
							+ file.getURI().toString());
				}
			}
			file.deleteLocalRepresentation();
		}

		if (displayProgress)
			progressDisplay.close();

	}

}
