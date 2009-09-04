package org.vpac.grisu.clients.gridTests.testElements;

import org.vpac.grisu.clients.gridTests.GridTestInfo;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.frontend.control.clientexceptions.MdsInformationException;
import org.vpac.grisu.frontend.model.job.JobObject;

import au.org.arcs.jcommons.constants.Constants;

public class UnixCommandsGridTestElement extends GridTestElement {

	public UnixCommandsGridTestElement(GridTestInfo info, String version, String submissionLocation, String fqan) throws MdsInformationException {
		super(info, version, submissionLocation, fqan);
	}
	
	@Override
	protected JobObject createJobObject() {
		JobObject jo = new JobObject(serviceInterface);
		
		jo.setApplication(getTestInfo().getApplicationName());
		jo.setApplicationVersion(this.version);
		
		jo.setCommandline("echo hello world");
		
		return jo;
	}

	public static String getApplicationName() {
		return "UnixCommands";
	}
	
	protected boolean checkJobSuccess() {
		
		if ( JobConstants.DONE == this.jobObject.getStatus(true) ) {
			addMessage("Status checked. Equals \"Done\". Good");
			return true;
		} else {
			addMessage("Status checked. Status is \""+jobObject.getStatus(false)+". Not good.");
			return false;
		}
		
		
	}
	
	public static boolean useMDS() {
		return true;
	}

	public static String getTestDescription() {
		return "A simple \"echo hello world\" is run. The tests checks whether the job status equals \"Done\" after the job finished.";
	}
	
	public static String getFixedVersion() {
		return Constants.NO_VERSION_INDICATOR_STRING;
		
	}


}
