package org.vpac.grisu.clients.blender;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.clientexceptions.FileTransferException;
import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
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
import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class GrisuBlenderJob {
	
	static final Logger myLogger = Logger.getLogger(GrisuBlenderJob.class.getName());
	
	public static final String BLENDER_APP_NAME = "Blender";
	public static final String BLENDER_DEFAULT_VERSION = "2.49a";
	
	public static final String INPUT_PATH_VARIABLE = "${INPUT_FILE_PATH}";
	
	private final UserApplicationInformation userApplicationInfo;
	private final GrisuRegistry registry;
	private final ServiceInterface serviceInterface;
	private final String multiJobName;
	private final MultiPartJobObject multiPartJob;

	private int maxWalltimeInSecondsAcrossFrames = 3600;
	private int defaultWalltime = 3600;
	private Map<Integer, Integer> walltimesPerFrame = new HashMap<Integer, Integer>();
	private int noCpus = 1;
	private String version = BLENDER_DEFAULT_VERSION;
	private Set<String> inputFiles = new HashSet<String>();

	private String blenderFile;
	private String outputFileName = "frame_";
	private int firstFrame = 0;
	private int lastFrame = -1;
	
	private String[] sitesToInclude;
	private String[] sitesToExclude;
	
	private RenderFormat format = RenderFormat.PNG;
	
	public enum RenderFormat {
		
		TGA,
		IRIS,
		HAMX,
		JPEG,
		MOVIE,
		IRIZ,
		RAWTGA,
    	AVIRAW,
    	AVIJPEG,
    	PNG,
    	BMP,
    	FRAMESERVER;
		
		private static Map<String, RenderFormat> fromStringMap;
		
		static {
			fromStringMap = new HashMap<String, RenderFormat>();
			for ( RenderFormat format : RenderFormat.values() ) {
				fromStringMap.put(format.toString(), format);
			}
		};
		

		public static RenderFormat fromString(String name) {
			RenderFormat result = fromStringMap.get(name);
			if ( result == null ) {
				throw new IllegalArgumentException("Render format "+name+" invalid.");
			}
			return result;
		}
		
	}
	
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
		properties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES, new Integer(maxWalltimeInSecondsAcrossFrames/60).toString());
		
		SortedSet<GridResource> result = this.userApplicationInfo.getBestSubmissionLocations(properties, multiPartJob.getFqan());
		return result;
		
	}
	
	public void createAndSubmitJobs() throws JobCreationException, JobSubmissionException {
		
		if ( lastFrame < firstFrame ) {
			throw new JobCreationException("Last frame before first frame.");
		}
		
		Long allWalltime = 0L;
		for ( int i=firstFrame; i<=lastFrame; i++ ) {
			allWalltime = allWalltime + walltimesPerFrame.get(i);
		}
		
		Map<GridResource, Long> resourcesToUse = new TreeMap<GridResource, Long>();
		List<Integer> ranks = new LinkedList<Integer>();
		Long allRanks = 0L;
		for ( GridResource resource : findBestResources() ) {
			
			if ( resource.getQueueName().contains("sque") ) {
				continue;
			}
			if ( sitesToInclude != null ) {
				
				for ( String site : sitesToInclude ) {
					if ( site.equalsIgnoreCase(resource.getSiteName()) ) {
						resourcesToUse.put(resource, new Long(0L));
						ranks.add(resource.getRank());
						allRanks = allRanks + resource.getRank();
					}
				}
				
			} else if ( sitesToExclude != null ) {
				
				for ( String site : sitesToExclude ) {
					if ( ! site.equalsIgnoreCase(resource.getSiteName()) ) {
						resourcesToUse.put(resource, new Long(0L));
						ranks.add(resource.getRank());
						allRanks = allRanks + resource.getRank();
					}
				}
				
			} else {
				resourcesToUse.put(resource, new Long(0L));
				ranks.add(resource.getRank());
				allRanks = allRanks + resource.getRank();
			}
		}
		
		myLogger.debug("Rank summary: "+allRanks);
		myLogger.debug("Walltime summary: "+allWalltime);
		
		//TODO change that later on so more than one frames can be included in one job
		for ( int i=firstFrame; i<=lastFrame; i++ ) {
			
			
			GridResource subLocResource = null;
			long oldWalltimeSummary = 0L;
			for ( GridResource resource : resourcesToUse.keySet() ) {
				
				long rankPercentage = (resource.getRank()*100)/(allRanks);
				long wallTimePercentage = ((walltimesPerFrame.get(i)+resourcesToUse.get(resource))*100)/(allWalltime);
				
				if ( rankPercentage >= wallTimePercentage ) {
					subLocResource = resource;
					oldWalltimeSummary = resourcesToUse.get(subLocResource);
					myLogger.debug("Rank percentage: "+rankPercentage+". Walltime percentage: "+wallTimePercentage+". Using resource: "+resource.getQueueName());
					break;
				} else {
					myLogger.debug("Rank percentage: "+rankPercentage+". Walltime percentage: "+wallTimePercentage+". Not using resource: "+resource.getQueueName());
				}
			}
			
			String command = createCommandline(i, i);
			addJob(command, SubmissionLocationHelpers.createSubmissionLocationString(subLocResource), walltimesPerFrame.get(i));
			resourcesToUse.put(subLocResource, oldWalltimeSummary+walltimesPerFrame.get(i));
		}
		
		createAndSubmitBlenderJob();
		
	}
	

	private String createCommandline(int startFrame, int endFrame) {
		
		String framesToCalculatePart;
		if ( startFrame == endFrame ) {
			framesToCalculatePart = " -f "+startFrame;
		} else {
			framesToCalculatePart = " -s "+startFrame+" -e "+endFrame+" -a";
		}
		String result = "blender "+
		"-b "+multiPartJob.pathToInputFiles()+"/"+registry.getFileManager().getFilename(blenderFile) +
		" -F " + format.toString() +
		" -o " + outputFileName +
		framesToCalculatePart;		
		
		myLogger.debug("Created commandline: "+result);
		
		return result;
	}
	
	private void addJob(JobObject job) {
		multiPartJob.addJob(job);
	}
	
	private void addJob(String commandline, int walltimeInSeconds) {
		addJob(commandline, null, walltimeInSeconds);
	}
	
	private void addJob(String commandline, String submissionLocation, int walltimeInSeconds) {
		
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
	
	private void createAndSubmitBlenderJob() throws JobSubmissionException {
		
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
	
	
	
	public int getMaxWalltimeInSeconds() {
		return maxWalltimeInSecondsAcrossFrames;
	}
	
	public int getDefaultWalltime() {
		return this.defaultWalltime;
	}

	public void setDefaultWalltimeInSeconds(int walltimeInSeconds) {
		this.defaultWalltime = walltimeInSeconds;
		this.maxWalltimeInSecondsAcrossFrames = walltimeInSeconds;
		createDefaultWalltimeMap();
	}
	
	private void setWalltimeForFrame(int frame, int walltimeInSeconds) {
		this.walltimesPerFrame.put(frame, walltimeInSeconds);
		if ( walltimeInSeconds > maxWalltimeInSecondsAcrossFrames ) {
			maxWalltimeInSecondsAcrossFrames = walltimeInSeconds;
		}
	}
	
	private void createDefaultWalltimeMap() {

		walltimesPerFrame.clear();
		if ( firstFrame <= lastFrame ) {
			for ( int i=firstFrame; i<=lastFrame; i++ ) {
				walltimesPerFrame.put(i, defaultWalltime);
			}
		}
		
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
	
	public String getBlenderFile() {
		return blenderFile;
	}

	public void setBlenderFile(String blenderFile) {
		
		if ( StringUtils.isBlank(blenderFile) ) {
			throw new IllegalArgumentException("No blender file specified.");
		} else {
			try {
				if ( ! registry.getFileManager().fileExists(blenderFile) ) {
					throw new IllegalArgumentException("Blender input file "+blenderFile+" doesn't exist.");
				}
			} catch (RemoteFileSystemException e) {
				throw new IllegalArgumentException("Could not check whether blender input file: "+blenderFile+" exists.", e);
			}
		}
		
		this.blenderFile = blenderFile;
		this.addInputFile(blenderFile);
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public int getFirstFrame() {
		return firstFrame;
	}

	public void setFirstFrame(int firstFrame) {
		this.firstFrame = firstFrame;
		createDefaultWalltimeMap();
	}

	public int getLastFrame() {
		return lastFrame;
	}

	public void setLastFrame(int lastFrame) {
		this.lastFrame = lastFrame;
		createDefaultWalltimeMap();
	}

	public String getFormat() {
		return format.toString();
	}

	public void setFormat(String format) {
		this.format = RenderFormat.fromString(format);
	}

	public void setSitesToInclude(String[] sites) {
		this.sitesToInclude = sites;
		this.sitesToExclude = null;
	}
	
	public void setSitesToExclude(String[] sites) {
		this.sitesToExclude = sites;
		this.sitesToInclude = null;
	}

}
