package org.vpac.grisu.clients.gridFtpTests;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.vpac.grisu.model.MountPoint;

public abstract class GridFtpTestElement {
	
	public static final String[] IMPLEMENTED_TESTS = new String[]{
		"SimpleUploadTest", "FiveTimesMultipleUpload", "SimpleMultipleUpload", "HundredTimesMultipleUpload",
		"HundredTimesMultipleDownload", "SimpleMultipleDownload"
	};
	
	public static final List<GridFtpTestElement> generateGridTestInfos(
			GridFtpTestController controller, String[] testnames, Set<MountPoint> mps) {


		List<GridFtpTestElement> result = new LinkedList<GridFtpTestElement>();
		if ( testnames == null || testnames.length == 0) {
			testnames = IMPLEMENTED_TESTS;
		}

		for (String testname : testnames) {
			
			try {
				Class testElementClass = Class.forName("org.vpac.grisu.clients.gridFtpTests.testElements."+testname);
				
				Constructor testElementConstructor = testElementClass.getConstructor(GridFtpTestController.class, Set.class);
				
				GridFtpTestElement el = (GridFtpTestElement) testElementConstructor.newInstance(controller, mps);
				result.add(el);
			} catch (Exception e) {
				if ( e instanceof InvocationTargetException ) {
 					System.err.println("Couldn't setup test "+testname+": "+((InvocationTargetException) e).getTargetException().getLocalizedMessage());
					System.exit(1);
				} else {
					System.err.println("Couldn't setup test "+testname+": "+e.getLocalizedMessage());
					System.exit(1);
				}
			}

		}

		return result;
	}

	static final Logger myLogger = Logger.getLogger(GridFtpTestElement.class
			.getName());

	protected final GridFtpTestController controller;

	protected final Set<MountPoint> mountpoints;

	protected LinkedList<List<GridFtpActionItem>> actionItems;
	
	public GridFtpTestElement(GridFtpTestController controller,
			Set<MountPoint> mps) {
		this.controller = controller;
		this.mountpoints = mps;
	}

	public LinkedList<List<GridFtpActionItem>> getActionItems() {
		
		if ( this.actionItems == null ) {
			this.actionItems = setupGridFtpActionItems();
		}
		
		return this.actionItems;
	}
	
	abstract protected LinkedList<List<GridFtpActionItem>> setupGridFtpActionItems();

	abstract public String getTestName();
	
	abstract public String getDescription();
	
//	abstract public String getTestSpecificResults();
	
	@Override
	public String toString() {
		return getTestName();
	}
	
	public String getResultsForThisTest(boolean onlyFailed, boolean showStackTrace, boolean shortVersion) {


		StringBuffer result = new StringBuffer();

		result.append("Testname:\t" + getTestName() + "\n");
		result.append("Description:\t" + getDescription() + "\n\n");
		result.append("Result per mountpoint:\n");
		
		int total = 0;
		int failed = 0;
		int success = 0;
		int notExecuted = 0;
		
		for ( List<GridFtpActionItem> list : getActionItems() ) {
			for ( GridFtpActionItem item : list ) {
				total = total + 1;

				if ( item.isExecuted() ) {
					if ( item.isSuccess() ) {
						success = success + 1;
					} else {
						failed = failed + 1;
					}
				} else {
					notExecuted = notExecuted + 1;
				}
			}
		}

		for (MountPoint mp : mountpoints) {
			
						StringBuffer sourceResults = new StringBuffer();
			StringBuffer targetResults = new StringBuffer();
			
			for (List<GridFtpActionItem> list : getActionItems()) {
				for (GridFtpActionItem item : list) {

					if (onlyFailed && item.isSuccess()) {
						continue;
					} else {
						if ((item.getSource() != null && item.getSource()
								.contains(mp.getRootUrl()))) {
							// means mountpoint was used as source
							sourceResults.append(item.getResult(showStackTrace, shortVersion));
						} else if (item.getTarget() != null
								&& item.getTarget().contains(mp.getRootUrl())) {
							// means mountpoint was used as target
							targetResults.append(item.getResult(showStackTrace, shortVersion));
						}
					}
				}
			}
			if ( sourceResults.length() > 0 || targetResults.length() > 0 ) {
				result.append("\tMountPoint: " + mp.getRootUrl() + " (VO: "
						+ mp.getFqan() + ")");
				if ( sourceResults.length() > 0 ) {
					result.append("\t..as source:\n");
					result.append(sourceResults+"\n");
				}
				if ( targetResults.length() > 0 ) {
					result.append("\t..as target:\n");
					result.append(targetResults+"\n\n");
				}
			}
			
			
//			result.append("\nTest specific results:\n\n");
//			result.append(getTestSpecificResults());
			
		}
		result.append("\nTotal number of tests: "+total);
		result.append("\nSuccessful tests: "+success);
		result.append("\nFailed tests: "+failed);
		result.append("\nNot executed tests: "+notExecuted);

		return result.toString();
	}
	

}
