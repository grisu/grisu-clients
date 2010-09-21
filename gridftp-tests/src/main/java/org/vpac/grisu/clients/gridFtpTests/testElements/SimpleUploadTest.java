package org.vpac.grisu.clients.gridFtpTests.testElements;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.vpac.grisu.clients.gridFtpTests.GridFtpAction;
import org.vpac.grisu.clients.gridFtpTests.GridFtpActionItem;
import org.vpac.grisu.clients.gridFtpTests.GridFtpTestController;
import org.vpac.grisu.clients.gridFtpTests.GridFtpTestElement;
import org.vpac.grisu.clients.gridFtpTests.TestSetupException;
import org.vpac.grisu.model.MountPoint;

public class SimpleUploadTest extends GridFtpTestElement {

	private final String sourceFile;
	private final String targetFileName = "simpleTestTarget.txt";
	private final String targetDownloadFolder;

	public SimpleUploadTest(GridFtpTestController controller,
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

		targetDownloadFolder = System.getProperty("java.io.tmpdir")
				+ File.separator + "downloadTarget";
		FileUtils.deleteQuietly(new File(targetDownloadFolder));
		new File(targetDownloadFolder).mkdirs();
		if (!new File(targetDownloadFolder).getParentFile().canWrite()) {
			throw new TestSetupException("Download target file not writeable: "
					+ targetDownloadFolder + ".");
		}

	}

	@Override
	public String getDescription() {
		return "A very simple upload, download and subsequent remote deletion of a small text file.";
	}

	// @Override
	// public LinkedList<GridFtpAction> getGridFtpActions() {
	//
	// return actions;
	// }

	@Override
	public String getTestName() {
		return "SimpleUploadTest";
	}

	@Override
	protected LinkedList<List<GridFtpActionItem>> setupGridFtpActionItems() {

		final LinkedList<List<GridFtpActionItem>> actionItems = new LinkedList<List<GridFtpActionItem>>();

		GridFtpAction action = new GridFtpAction(GridFtpAction.Action.upload,
				"uploadStage", controller);
		List<GridFtpActionItem> list = new LinkedList<GridFtpActionItem>();
		// upload file
		for (final MountPoint mp : mountpoints) {
			final GridFtpActionItem item = new GridFtpActionItem(mp.getAlias(),
					action, sourceFile, mp.getRootUrl() + "/" + targetFileName);
			list.add(item);
		}
		actionItems.add(list);

		action = new GridFtpAction(GridFtpAction.Action.download,
				"downloadStage", controller);
		list = new LinkedList<GridFtpActionItem>();
		// upload file
		for (final MountPoint mp : mountpoints) {
			final GridFtpActionItem item = new GridFtpActionItem(mp.getAlias(),
					action, mp.getRootUrl() + "/" + targetFileName,
					targetDownloadFolder + File.separator
							+ UUID.randomUUID().toString());
			list.add(item);
		}
		actionItems.add(list);

		action = new GridFtpAction(GridFtpAction.Action.delete, "deleteStage",
				controller);
		list = new LinkedList<GridFtpActionItem>();
		// upload file
		for (final MountPoint mp : mountpoints) {
			final GridFtpActionItem item = new GridFtpActionItem(mp.getAlias(),
					action, mp.getRootUrl() + "/" + targetFileName, null);
			list.add(item);
		}
		actionItems.add(list);

		return actionItems;

	}

}
