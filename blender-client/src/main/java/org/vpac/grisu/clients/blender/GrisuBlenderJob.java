package org.vpac.grisu.clients.blender;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.client.control.clientexceptions.FileTransferException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.model.job.JobObject;
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
		this.multiPartJob = new MultiPartJobObject(serviceInterface, this.multiJobName, fqan);

	}
	
	public GrisuBlenderJob(ServiceInterface serviceInterface, String multiPartJobId) throws MultiPartJobException, NoSuchJobException {
		
		this.serviceInterface = serviceInterface;
		this.registry = GrisuRegistryManager.getDefault(serviceInterface);
		this.userApplicationInfo = registry.getUserApplicationInformation(BLENDER_APP_NAME);
		this.multiJobName = multiPartJobId;
		this.multiPartJob = new MultiPartJobObject(serviceInterface, this.multiJobName, false);
		
	}
	
	public String getProgress() {
		return multiPartJob.getProgress(null);
	}
	
	public SortedSet<GridResource> findBestResources() {
		
		Map<JobSubmissionProperty, String> properties = new HashMap<JobSubmissionProperty, String>();
		
		properties.put(JobSubmissionProperty.NO_CPUS, new Integer(noCpus).toString());
		properties.put(JobSubmissionProperty.APPLICATIONVERSION, version);
		properties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES, new Integer(walltimeInSeconds/60).toString());
		
		return this.userApplicationInfo.getBestSubmissionLocations(properties, multiPartJob.getFqan());
		
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
	
	public void createAndSubmitBlenderJob() throws JobSubmissionException {
		
		SortedSet<GridResource> resourcesAvailable = findBestResources();
		
		if ( resourcesAvailable.size() == 0 ) {
			throw new RuntimeException("No resources available for the properties/vo you selected for this job.");
		}
		
		for ( String inputFile : inputFiles ) {
			multiPartJob.addInputFile(inputFile);
		}
		
		try {
			multiPartJob.prepareAndCreateJobs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			multiPartJob.submit();
		} catch (NoSuchJobException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void downloadResult() throws RemoteFileSystemException, FileTransferException, IOException {
		
		multiPartJob.downloadResults(new File("/home/markus/Desktop/blender"), new String[]{"cubes"}, false, false);
		
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
