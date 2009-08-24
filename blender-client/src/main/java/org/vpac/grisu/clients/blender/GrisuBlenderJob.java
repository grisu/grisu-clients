package org.vpac.grisu.clients.blender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.BackendException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobsException;
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.UserApplicationInformation;

import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;

public class GrisuBlenderJob {
	
	public static final String BLENDER_APP_NAME = "Blender";
	public static final String BLENDER_DEFAULT_VERSION = "2.49";
	
	public static final String INPUT_PATH_VARIABLE = "${INPUT_FILE_PATH}";
	
	private final UserApplicationInformation userApplicationInfo;
	private final GrisuRegistry registry;
	private final ServiceInterface serviceInterface;
	private final Set<String> versions;
	private final String fqan;
	private final String multiJobName;
	private final MultiPartJobObject multiPartJob;

	private int walltimeInSeconds = 3600;
	private int noCpus = 1;
	private String version = BLENDER_DEFAULT_VERSION;
	private Set<String> inputFiles = new HashSet<String>();
	
	public GrisuBlenderJob(ServiceInterface serviceInterface, String multiPartJobId, String fqan) throws MultiPartJobException {
		this.serviceInterface = serviceInterface;
		this.registry = GrisuRegistryManager.getDefault(serviceInterface);
		this.userApplicationInfo = registry.getUserApplicationInformation(BLENDER_APP_NAME);
		this.multiJobName = multiPartJobId;
		this.multiPartJob = new MultiPartJobObject(serviceInterface, this.multiJobName, this.fqan);
		this.fqan = fqan;
		this.versions = this.userApplicationInfo.getAllAvailableVersionsForFqan(fqan);
	}
	
	public SortedSet<GridResource> findBestResources() {
		
		Map<JobSubmissionProperty, String> properties = new HashMap<JobSubmissionProperty, String>();
		
		properties.put(JobSubmissionProperty.NO_CPUS, new Integer(noCpus).toString());
		properties.put(JobSubmissionProperty.APPLICATIONVERSION, version);
		properties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES, new Integer(walltimeInSeconds/60).toString());
		
		return this.userApplicationInfo.getBestSubmissionLocations(properties, fqan);
		
	}
	
	public void addJob(String commandline, int walltimeInSeconds) {
		addJob(commandline, null, walltimeInSeconds);
	}
	
	public void addJob(String commandline, String submissionLocation, int walltimeInSeconds) {
		
		commandline = commandline.replace(INPUT_PATH_VARIABLE, multiPartJob.pathToInputFiles());
		
		JobObject jo = new JobObject(serviceInterface);
		jo.setJobname(multiJobName+"_" + multiPartJob.getJobs().size() );
		jo.setApplication("blender");
		jo.setApplicationVersion(version);
		jo.setCommandline(commandline);
		jo.setWalltimeInSeconds(walltimeInSeconds);
		jo.setCpus(noCpus);
		if ( StringUtils.isNotBlank(submissionLocation) ) {
			jo.setSubmissionLocation(submissionLocation);
		}
		multiPartJob.addJob(jo);
		
	}
	
	public void createAndSubmitBlenderJob() throws JobsException, BackendException, JobSubmissionException {
		
		SortedSet<GridResource> resourcesAvailable = findBestResources();
		
		if ( resourcesAvailable.size() == 0 ) {
			throw new RuntimeException("No resources available for the properties/vo you selected for this job.");
		}
		
		for ( String inputFile : inputFiles ) {
			multiPartJob.addInputFile(inputFile);
		}
		
		multiPartJob.prepareAndCreateJobs();
		
		try {
			multiPartJob.submit();
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/xfire-backend/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
//				 "https://ngportal.vpac.org/grisu-ws/services/grisu",
				 "Local",
//				"Dummy",
				username, password);

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory
					.createInterface(loginParams);
		} catch (ServiceInterfaceException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		
		GrisuBlenderJob blenderJob;
		try {
			blenderJob = new GrisuBlenderJob(si, "blenderJob1", "/ARCS/NGAdmin");
		} catch (Exception e) {
			si.deleteMultiPartJob("blenderJob1", true);
			blenderJob = new GrisuBlenderJob(si, "blenderJob1", "/ARCS/NGAdmin");
		}
		
		Set<GridResource> resources = blenderJob.findBestResources();
		
		blenderJob.addInputFile("/home/markus/Desktop/CubesTest.blend");
		
		for ( int i=0; i<10; i++ ) {
			blenderJob.addJob("blender -b "+INPUT_PATH_VARIABLE+"/CubesTest.blend -F PNG -o cubes_ -f "+i, 3600);
		}
		
		blenderJob.createAndSubmitBlenderJob();
		
		System.out.println("Blender job submission finished.");
		
	}
	
	
	
	public int getWalltimeInSeconds() {
		return walltimeInSeconds;
	}

	public void setWalltimeInSeconds(int walltimeInSeconds) {
		this.walltimeInSeconds = walltimeInSeconds;
	}

	public int getNoCpus() {
		return noCpus;
	}

	public void setNoCpus(int noCpus) {
		this.noCpus = noCpus;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMultiJobName() {
		return multiJobName;
	}

	public Set<String> getInputFiles() {
		return inputFiles;
	}
	
	public void addInputFile(String inputFile) {
		this.inputFiles.add(inputFile);
	}

	private void setInputFiles(Set<String> inputFiles) {
		this.inputFiles = inputFiles;
	}


}
