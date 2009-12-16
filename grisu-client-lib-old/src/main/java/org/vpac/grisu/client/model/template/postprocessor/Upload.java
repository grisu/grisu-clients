package org.vpac.grisu.client.model.template.postprocessor;

import java.io.File;
import java.net.URI;

import javax.activation.DataSource;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.w3c.dom.Element;

import au.org.arcs.jcommons.utils.JsdlHelpers;

/**
 * Uploads the file specified by the element value into the jobdirectory. At the
 * moment there is no configuration option.
 * 
 * @author Markus Binsteiner
 * 
 */
public class Upload extends ElementPostprocessor {

	static final Logger myLogger = Logger.getLogger(Upload.class.getName());

	public Upload(JsdlTemplate template, Element element) {
		super(template, element);
	}

	@Override
	public void process(String fqan) throws PostProcessException {

		if (JobConstants.DUMMY_STAGE_FILE.equals(element.getTextContent())) {
			return;
		}

		DataSource dataSource = null;
		File file;
		try {
			String uri = element.getTextContent();
			file = new File(new URI(uri));
		} catch (Exception e) {
			throw new PostProcessException("Could not access file.", e);
		}

		if (!file.exists()) {
			throw new PostProcessException("File does not exist: "
					+ file.toString(), null);
		}

		if (!file.canRead()) {
			throw new PostProcessException("Can't read file: "
					+ file.toString(), null);
		}

		String target = null;

		ServiceInterface serviceInterface = template.getEnvironmentManager()
				.getServiceInterface();

		target = template.getCurrentRemoteJobDirectory() + "/" + file.getName();
		GrisuFileObject targetDirectory = null;
		try {
			serviceInterface.mkdir(template.getCurrentRemoteJobDirectory());
			targetDirectory = template.getEnvironmentManager().getFileManager()
					.getFileObject(template.getCurrentRemoteJobDirectory());
		} catch (Exception e) {
			throw new PostProcessException(
					"Could not create/access file/folder: " + target, e);
			// e.printStackTrace();
		}

		myLogger.debug("Uploading local file: " + file.toString() + " to: "
				+ target);
		if (file.isDirectory()) {
			template.fireJsdlEvent("Uploading local folder recursively: "
					+ file.toString() + " to: " + target, null);
		} else {
			template.fireJsdlEvent("Uploading local file: " + file.toString()
					+ " to: " + target, null);
		}
		// dataSource = new FileDataSource(file);

		try {
			GrisuFileObject source = template.getEnvironmentManager()
					.getFileManager().getFileObject(file.toURI());
			FileTransfer transfer = template.getEnvironmentManager()
					.getFileTransferManager().addTransfer(
							new GrisuFileObject[] { source }, targetDirectory,
							FileTransfer.OVERWRITE_EVERYTHING, true);

			if (transfer.getStatus() == FileTransfer.FAILED_STATUS) {
				myLogger.debug("File transfer failed.");
				throw new PostProcessException("Could not upload file: "
						+ file.getName(), transfer.getPossibleException());
			}
			// FileManagerTransferHelpers.transferFiles(serviceInterface, new
			// BackendFileObject[]{source},
			// targetDirectory, false);
			// String remote_file = serviceInterface.upload(dataSource,target,
			// true);

			// TODO test whether remote_file == target

			JsdlHelpers.setSourceForStageInElement((Element) element
					.getParentNode().getParentNode(), target);

			// fillFileNameElement((Element)element);

		} catch (Exception e1) {
			if (e1.getLocalizedMessage() == null) {
				throw new PostProcessException("Could not upload file: "
						+ file.getName(), e1.getCause());
			} else {
				throw new PostProcessException("Could not upload file: "
						+ file.getName(), e1);
			}
		}

	}

	@Override
	public boolean processBeforeJobCreation() {
		return false;
	}

	// private void fillFileNameElement(Element uri) {
	//		
	// Element dataStagingElement =
	// (Element)uri.getParentNode().getParentNode();
	// Element fileNameElement =
	// (Element)dataStagingElement.getElementsByTagName("FileName").item(0);
	// String basename =
	// uri.getTextContent().substring(uri.getTextContent().lastIndexOf("/")+1);
	// fileNameElement.setTextContent(template.getCurrentRelativeJobDirectory()+"/"+basename);
	//		
	// }

}
