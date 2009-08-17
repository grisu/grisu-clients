package org.vpac.grisu.client.gridFtpTests.testElements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.vpac.grisu.client.gridFtpTests.GridFtpAction;
import org.vpac.grisu.client.gridFtpTests.GridFtpActionItem;
import org.vpac.grisu.client.gridFtpTests.GridFtpTestController;
import org.vpac.grisu.client.gridFtpTests.GridFtpTestElement;
import org.vpac.grisu.client.gridFtpTests.TestSetupException;
import org.vpac.grisu.model.MountPoint;

public class HundredTimesMultipleDownload extends GridFtpTestElement {

	private final String sourceFile;
	private final String targetFolder;

	public HundredTimesMultipleDownload(GridFtpTestController controller,
			Set<MountPoint> mps) throws TestSetupException {

		super(controller, mps);

		targetFolder = controller.getGridTestDirectory() + File.separator
				+ "temp";

		try {
			if (!new File(targetFolder).exists()) {
				if (!new File(targetFolder).mkdirs()) {
					throw new TestSetupException(
							"Could not create temp target directory: "
									+ targetFolder);
				}
			}
		} catch (Exception e) {
			throw new TestSetupException(
					"Could not create temp target directory: "
							+ e.getLocalizedMessage());
		}

		sourceFile = controller.getGridTestDirectory() + File.separator
				+ "simpleTestSource.txt";
		// check whether source file exists...
		if (!new File(sourceFile).exists()) {
			throw new TestSetupException("Source file " + sourceFile
					+ " does not exists.");
		}
		if (!new File(sourceFile).canRead()) {
			throw new TestSetupException("Can't read source file " + sourceFile
					+ ".");
		}

		// uploading the sourcefile
		DataHandler dh = new DataHandler(new FileDataSource(
				new File(sourceFile)));
		try {
			for (MountPoint mp : mps) {
				controller.getServiceInterface().upload(dh,
						mp.getRootUrl() + "/simpleTestFile.txt", true);
			}
		} catch (Exception e) {
			throw new TestSetupException("Could not upload source file: "
					+ e.getLocalizedMessage());
		}

	}

	protected LinkedList<List<GridFtpActionItem>> setupGridFtpActionItems() {

		LinkedList<List<GridFtpActionItem>> actionItems = new LinkedList<List<GridFtpActionItem>>();

		GridFtpAction action = new GridFtpAction(GridFtpAction.Action.download,
				"multiDownload", controller);
		List<GridFtpActionItem> list = new LinkedList<GridFtpActionItem>();
		// upload file
		for (MountPoint mp : mountpoints) {

			for (int i = 0; i < 100; i++) {
				GridFtpActionItem item = new GridFtpActionItem(mp.getAlias()
						+ i, action, mp.getRootUrl() + "/simpleTestFile.txt",
						targetFolder + File.separator + "targetFile_" + i);
				list.add(item);
			}

		}
		actionItems.add(list);

		// action = new GridFtpAction(GridFtpAction.Action.delete, "delete1",
		// controller);
		// list = new LinkedList<GridFtpActionItem>();
		// // delete file
		// for ( MountPoint mp : mountpoints ) {
		//			
		// for ( int i=0; i<controller.getConcurrentThreads(); i++ ) {
		// GridFtpActionItem item = new GridFtpActionItem(mp.getAlias()+i,
		// action, mp.getRootUrl()+"/"+targetFileName+i, null);
		// list.add(item);
		// }
		// }
		// actionItems.add(list);

		return actionItems;

	}

	// @Override
	// public LinkedList<GridFtpAction> getGridFtpActions() {
	//
	// return actions;
	// }

	@Override
	public String getTestName() {
		return "HundredTimesMultipleDownload";
	}

	@Override
	public String getDescription() {
		return "A very simple multiple download of a small text file. This test downloads the file 100 times from the same filesystem.";
	}

}
