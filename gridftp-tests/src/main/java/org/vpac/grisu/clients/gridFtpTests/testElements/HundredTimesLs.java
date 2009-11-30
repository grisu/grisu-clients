package org.vpac.grisu.clients.gridFtpTests.testElements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.vpac.grisu.clients.gridFtpTests.GridFtpAction;
import org.vpac.grisu.clients.gridFtpTests.GridFtpActionItem;
import org.vpac.grisu.clients.gridFtpTests.GridFtpTestController;
import org.vpac.grisu.clients.gridFtpTests.GridFtpTestElement;
import org.vpac.grisu.clients.gridFtpTests.TestSetupException;
import org.vpac.grisu.model.MountPoint;

public class HundredTimesLs extends GridFtpTestElement {

	private final String sourceFile;
	private final String targetFolder;

	public HundredTimesLs(GridFtpTestController controller,
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

		// uploading a sourcefile
		DataHandler dh = new DataHandler(new FileDataSource(
				new File(sourceFile)));
		try {
			for (MountPoint mp : mps) {
				controller.getServiceInterface().upload(dh,
						mp.getRootUrl() + "/simpleTestFile.txt");
			}
		} catch (Exception e) {
			throw new TestSetupException("Could not upload source file: "
					+ e.getLocalizedMessage());
		}

	}

	protected LinkedList<List<GridFtpActionItem>> setupGridFtpActionItems() {

		LinkedList<List<GridFtpActionItem>> actionItems = new LinkedList<List<GridFtpActionItem>>();

		GridFtpAction action = new GridFtpAction(GridFtpAction.Action.ls,
				"multiLs", controller);
		List<GridFtpActionItem> list = new LinkedList<GridFtpActionItem>();
		// upload file
		for (MountPoint mp : mountpoints) {

			for (int i = 0; i < 100; i++) {
				GridFtpActionItem item = new GridFtpActionItem(mp.getAlias()
						+ i, action, mp.getRootUrl(),
						null);
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
		return "HundredTimesMultipleLs";
	}

	@Override
	public String getDescription() {
		return "A simple job to do an \"ls\" command 100 times on the same filesystem.";
	}

}




