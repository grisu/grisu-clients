package org.vpac.grisu.clients.gridTests.testElements;

import java.io.File;
import java.util.Arrays;

import org.vpac.grisu.clients.gridTests.GridTestController;
import org.vpac.grisu.clients.gridTests.GridTestInfo;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.clientexceptions.MdsInformationException;
import org.vpac.grisu.frontend.model.job.JobObject;

import au.org.arcs.jcommons.constants.Constants;

public class UnderworldGridTestElement extends GridTestElement {

	public UnderworldGridTestElement(GridTestInfo info, String version,
			String submissionLocation, String fqan) throws MdsInformationException {
		super(info, version, submissionLocation, fqan);
	}

	
	@Override
	protected boolean checkJobSuccess() {

//		if ( JobConstants.DONE == this.jobObject.getStatus(true) ) {
//			addMessage("Status checked. Equals \"Done\". Good");
//			return true;
//		} else {
//			addMessage("Status checked. Status is \""+jobObject.getStatus(false)+". Not good.");
//			return false;
//		}
		
		String jobDir = null;
		try {
			jobDir = serviceInterface.getJobProperty(jobObject.getJobname(), Constants.JOBDIRECTORY_KEY);
		} catch (NoSuchJobException e) {
			addMessage("Could not find job. This is most likely a globus/grisu problem...");
			setPossibleExceptionForCurrentStage(e);
			return false;
		}
		
		String[] children = null;
		try {
			children = serviceInterface.getChildrenFileNames(jobDir+"/output", false).asArray();
			addMessage("Listing output directory: ");
//			StringBuffer listing = new StringBuffer();
//			for ( String child : children ) {
//				listing.append(child+"\n");
//			}
//			addMessage(listing.toString());
		} catch (Exception e) {
			addMessage("Could not get children of output directory.");
			setPossibleExceptionForCurrentStage(e);
			return false;
		}
		
		if ( Arrays.binarySearch(children, jobDir+"/output/FrequentOutput.dat") >= 0 ) {
			addMessage("\"FrequentOutput.dat\" file found. Good. Means job ran successful.");
			return true;
		} else {
			addMessage("\"FrequentOutput.dat\" file not found. Means job didn't ran successful.");
			
			return false;
		}
		
	}

	@Override
	protected JobObject createJobObject() throws MdsInformationException {

		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(this.getTestInfo().getApplicationName());
		jo.setApplicationVersion(this.version);
		jo.setWalltimeInSeconds(60);
		
		jo.setCommandline("Underworld ./RayleighTaylorBenchmark_1.2.0.xml");
		jo.addInputFileUrl(getTestInfo().getTestBaseDir().getPath()+File.separator+"RayleighTaylorBenchmark_1.2.0.xml");
		
		return jo;
		
	}
	
	public static String getFixedVersion() {
		return Constants.NO_VERSION_INDICATOR_STRING;
		
	}

	public static String getApplicationName() {
		return "Underworld";
	}

	public static boolean useMDS() {
		return true;
	}


	public static String getTestDescription() {
		return "A simple underworld job is run and the output directory is checked whether it contains the file \"FrequentOutput.dat\"";
	}

}
