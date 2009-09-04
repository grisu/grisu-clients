package org.vpac.grisu.clients.blender;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.FileTransferException;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.MultiPartJobEventListener;
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class GrisuBlenderJob implements MultiPartJobEventListener {
	
	static final Logger myLogger = Logger.getLogger(GrisuBlenderJob.class.getName());
	
	public static final String BLENDER_APP_NAME = "Blender";
	public static final String BLENDER_DEFAULT_VERSION = "2.49a";
	
	public static final String BLENDER_OUTPUTFILENAME_KEY = "blenderOutputFileNamePattern";
	
	public static final String INPUT_PATH_VARIABLE = "${INPUT_FILE_PATH}";
	
	private final GrisuRegistry registry;
	private final ServiceInterface serviceInterface;
	private final String multiJobName;
	private final MultiPartJobObject multiPartJob;
	
	private final NumberFormat formatter = new DecimalFormat("0000");


	private Map<Integer, Integer> walltimesPerFrame = new HashMap<Integer, Integer>();
	private int noCpus = 1;
	private String version = BLENDER_DEFAULT_VERSION;
	private Set<String> inputFiles = new HashSet<String>();

	private String blenderFile;
	private String outputFileName = "frame_";
	private int firstFrame = 0;
	private int lastFrame = -1;
	
	private RenderFormat format = RenderFormat.PNG;
	
	public boolean verbose = false;
	
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
		this.multiJobName = multiPartJobId;
		this.multiPartJob = new MultiPartJobObject(serviceInterface, this.multiJobName, fqan);
		this.multiPartJob.setDefaultApplication(BLENDER_APP_NAME);
		this.multiPartJob.setDefaultVersion(BLENDER_DEFAULT_VERSION);
		this.multiPartJob.addJobStatusChangeListener(this);

	}
	
	public GrisuBlenderJob(ServiceInterface serviceInterface, String multiPartJobId) throws MultiPartJobException, NoSuchJobException {
		
		this.serviceInterface = serviceInterface;
		this.registry = GrisuRegistryManager.getDefault(serviceInterface);
		this.multiJobName = multiPartJobId;
		this.multiPartJob = new MultiPartJobObject(serviceInterface, this.multiJobName, false);
		this.multiPartJob.addJobStatusChangeListener(this);
		
	}
	
	public MultiPartJobObject getMultiPartJobObject() {
		
		return multiPartJob;
	}
	
//	public SortedSet<GridResource> findBestResources() {
//		
//		Map<JobSubmissionProperty, String> properties = new HashMap<JobSubmissionProperty, String>();
//		
//		properties.put(JobSubmissionProperty.NO_CPUS, new Integer(noCpus).toString());
//		properties.put(JobSubmissionProperty.APPLICATIONVERSION, version);
//		properties.put(JobSubmissionProperty.WALLTIME_IN_MINUTES, new Integer(multiPartJob.getMaxWalltimeInSeconds()/60).toString());
//		
//		SortedSet<GridResource> result = this.userApplicationInfo.getBestSubmissionLocations(properties, multiPartJob.getFqan());
//		return result;
//		
//	}
	
	public void createAndSubmitJobs() throws JobCreationException, JobSubmissionException {
		
		if ( lastFrame < firstFrame ) {
			throw new JobCreationException("Last frame before first frame.");
		}
		
		
		//TODO change that later on so more than one frames can be included in one job
		for ( int i=firstFrame; i<=lastFrame; i++ ) {

			String command = createCommandline(i, i);
			
			addJob(i, command, walltimesPerFrame.get(i));
		}
		
		multiPartJob.fillOrOverwriteSubmissionLocationsUsingMatchmaker();
		
		createAndSubmitBlenderJob();
		
		setOutputFilenameJobProperty();
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
	
	private void addJob(int framenumber, String commandline, int walltimeInSeconds) {
		addJob(framenumber, commandline, null, walltimeInSeconds);
	}
	
	private void addJob(int framenumber, String commandline, String submissionLocation, int walltimeInSeconds) {
		
		commandline = commandline.replace(INPUT_PATH_VARIABLE, multiPartJob.pathToInputFiles());
		
		JobObject jo = new JobObject(serviceInterface);
		String number = formatter.format(framenumber);
		jo.setJobname(multiJobName+"_" + number );
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
			throw new JobSubmissionException("Couldn't preapare or create job(s): "+e.getLocalizedMessage());
		}
		
		try {
			multiPartJob.submit();
		} catch (NoSuchJobException e) {
			throw new JobSubmissionException("Could not submit job(s): "+e.getLocalizedMessage());
		}
		
	}
//	
//	public void downloadResult() throws RemoteFileSystemException, FileTransferException, IOException {
//		
//		multiPartJob.downloadResults(new File("/home/markus/Desktop/blender"), new String[]{"cubes"}, false, false);
//		
//	}
	
	
	
	public int getMaxWalltimeInSeconds() {
		return multiPartJob.getMaxWalltimeInSeconds();
	}
	
	public int getDefaultWalltime() {
		return multiPartJob.getDefaultWalltime();
	}

	public void setDefaultWalltimeInSeconds(int walltimeInSeconds) {
		this.multiPartJob.setDefaultWalltimeInSeconds(walltimeInSeconds);
		createDefaultWalltimeMap();
	}
	
	private void setWalltimeForFrame(int frame, int walltimeInSeconds) {
		this.walltimesPerFrame.put(frame, walltimeInSeconds);
	}
	
	private void createDefaultWalltimeMap() {

		walltimesPerFrame.clear();
		if ( firstFrame <= lastFrame ) {
			for ( int i=firstFrame; i<=lastFrame; i++ ) {
				walltimesPerFrame.put(i, multiPartJob.getDefaultWalltime());
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
		this.multiPartJob.setSitesToInclude(sites);
	}
	
	public void setSitesToExclude(String[] sites) {
		this.multiPartJob.setSitesToExclude(sites);
	}

	public void eventOccured(MultiPartJobObject job, String eventMessage) {

		if (verbose) {
			System.out.println(eventMessage);
		}
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	private void setOutputFilenameJobProperty() {
		multiPartJob.addJobProperty(BLENDER_OUTPUTFILENAME_KEY, outputFileName);
	}
	
	public String getOutputFilenameJobProperty() {
		return multiPartJob.getJobProperty(BLENDER_OUTPUTFILENAME_KEY);
	}


}
