package org.vpac.grisu.clients.gridFtpTests.testElements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.vpac.grisu.clients.gridFtpTests.GridFtpAction;
import org.vpac.grisu.clients.gridFtpTests.GridFtpActionItem;
import org.vpac.grisu.clients.gridFtpTests.GridFtpTestController;
import org.vpac.grisu.clients.gridFtpTests.GridFtpTestElement;
import org.vpac.grisu.clients.gridFtpTests.TestSetupException;
import org.vpac.grisu.model.MountPoint;

public class HundredTimesMultipleUpload extends GridFtpTestElement {

	private final String sourceFile;
	private final String targetFileName = "simpleTestTarget.txt";

	public HundredTimesMultipleUpload(GridFtpTestController controller,
			Set<MountPoint> mps) throws TestSetupException {

		super(controller, mps);

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

	}

	@Override
	public String getDescription() {
		return "A very simple multiple upload of a small text file. This test uploads 100 times to the same filesystem.";

	}

	// @Override
	// public LinkedList<GridFtpAction> getGridFtpActions() {
	//
	// return actions;
	// }

	@Override
	public String getTestName() {
		return "HundredTimesMultipleUpload";
	}

	protected LinkedList<List<GridFtpActionItem>> setupGridFtpActionItems() {

		LinkedList<List<GridFtpActionItem>> actionItems = new LinkedList<List<GridFtpActionItem>>();

		GridFtpAction action = new GridFtpAction(GridFtpAction.Action.upload,
				"multiUpload", controller);
		List<GridFtpActionItem> list = new LinkedList<GridFtpActionItem>();
		// upload file
		for (MountPoint mp : mountpoints) {

			for (int i = 0; i < 100; i++) {
				GridFtpActionItem item = new GridFtpActionItem(mp.getAlias()
						+ i, action, sourceFile, mp.getRootUrl() + "/"
						+ targetFileName + i);
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
		// for ( int i=0; i<100; i++ ) {
		// GridFtpActionItem item = new GridFtpActionItem(mp.getAlias()+i,
		// action, mp.getRootUrl()+"/"+targetFileName+i, null);
		// list.add(item);
		// }
		// }
		// actionItems.add(list);

		return actionItems;

	}

}
