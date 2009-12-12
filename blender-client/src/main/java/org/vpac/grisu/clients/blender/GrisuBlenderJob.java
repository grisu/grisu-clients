package org.vpac.grisu.clients.blender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.frontend.model.events.BatchJobEvent;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.settings.Environment;

public class GrisuBlenderJob implements EventTopicSubscriber<BatchJobEvent> {
	
	static final Logger myLogger = Logger.getLogger(GrisuBlenderJob.class.getName());
	
	public static final String BLENDER_APP_NAME = "Blender";
	public static final String BLENDER_DEFAULT_VERSION = "2.49a";
	
	public static final String BLENDER_OUTPUTFILENAME_KEY = "blenderOutputFileNamePattern";
	
	public static final String INPUT_PATH_VARIABLE = "${INPUT_FILE_PATH}";
	
	private final GrisuRegistry registry;
	private final ServiceInterface serviceInterface;
	private final String multiJobName;
	private final BatchJobObject multiPartJob;
	
	private static final NumberFormat formatter = new DecimalFormat("0000");
	
	public static final File BLENDER_PLUGIN_DIR = new File(Environment.getGrisuDirectory(), "blender");
	public static final File BLENDER_RESOURCE_PYTHYON_SCRIPT = new File(BLENDER_PLUGIN_DIR, "ListResources.py");
	static {
		if ( ! BLENDER_PLUGIN_DIR.exists() ) {
			BLENDER_PLUGIN_DIR.mkdirs();
		}
		
		InputStream in = GrisuBlenderJob.class.getResourceAsStream("/ListResources.py");
		
		try {
			IOUtils.copy(in, new FileOutputStream(BLENDER_RESOURCE_PYTHYON_SCRIPT));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private Map<Integer, Integer> walltimesPerFrame = new HashMap<Integer, Integer>();
	private int noCpus = 1;
	private String version = BLENDER_DEFAULT_VERSION;
//	private Set<String> inputFiles = new HashSet<String>();

	private BlendFile blendFile;
	private String outputFileName = "frame_";
	private int firstFrame = 0;
	private int lastFrame = -1;
	
	private RenderFormat format = RenderFormat.PNG;
	
	public boolean verbose = false;
	
	private int UPLOAD_THREADS = 10;
	
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
	
	public GrisuBlenderJob(ServiceInterface serviceInterface, String multiPartJobId, String fqan) throws BatchJobException {
		this.serviceInterface = serviceInterface;
		this.registry = GrisuRegistryManager.getDefault(serviceInterface);
		this.multiJobName = multiPartJobId;
		this.multiPartJob = new BatchJobObject(serviceInterface, this.multiJobName, fqan, BLENDER_APP_NAME, BLENDER_DEFAULT_VERSION);
		this.multiPartJob.setConcurrentInputFileUploadThreads(UPLOAD_THREADS);
		EventBus.subscribe(this.multiJobName, this);
	}
	
	public GrisuBlenderJob(ServiceInterface serviceInterface, String multiPartJobId) throws BatchJobException, NoSuchJobException {
		
		this.serviceInterface = serviceInterface;
		this.registry = GrisuRegistryManager.getDefault(serviceInterface);
		this.multiJobName = multiPartJobId;
		this.multiPartJob = new BatchJobObject(serviceInterface, this.multiJobName, false);
		EventBus.subscribe(this.multiJobName, this);
	}
	
	public BatchJobObject getMultiPartJobObject() {
		
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
	
	public void createAndSubmitJobs(boolean waitToFinish) throws JobCreationException, JobSubmissionException, InterruptedException {
		
		if ( lastFrame < firstFrame ) {
			throw new JobCreationException("Last frame before first frame.");
		}
		
		
		//TODO change that later on so more than one frames can be included in one job
		for ( int i=firstFrame; i<=lastFrame; i++ ) {

			String command = createCommandline(i, i);
			
			addJob(i, command, walltimesPerFrame.get(i));
		}
		

		createAndSubmitBlenderJob(waitToFinish);
		
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
		"-b "+multiPartJob.pathToInputFiles()+blendFile.getRelativeBlendFilePath() +
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
	
	private void createAndSubmitBlenderJob(boolean waitToFinish) throws JobSubmissionException, InterruptedException {
		
		multiPartJob.addInputFile(blendFile.getFile().toString(), blendFile.getRelativeBlendFilePath());
		
		for ( File file : blendFile.getReferrencedFiles().keySet() ) {
			//TODO fix this for windows.
			multiPartJob.addInputFile(file.toString(), blendFile.getReferrencedFiles().get(file));
		}
		
		// upload possible physics files
		for ( File file : blendFile.getBlendCacheFiles(firstFrame, lastFrame) ) {
			multiPartJob.addInputFile(file.toString(), blendFile.getRelativeBlendCacheFolderPath()+"/"+file.getName());
		}
		
		// upload possible fluid files
		for ( File file : blendFile.getFluidFiles(firstFrame, lastFrame) ) {
			multiPartJob.addInputFile(file.toString(), blendFile.getFluidsFolderPath()+"/"+file.getName());
		}
		

		try {
			multiPartJob.prepareAndCreateJobs(true);
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JobSubmissionException("Couldn't preapare or create job(s): "+e.getLocalizedMessage());
		}
		
		try {
			multiPartJob.submit(waitToFinish);
		} catch (InterruptedException e) {
			throw e;
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

//	public Set<String> getInputFiles() {
//		return inputFiles;
//	}
//	
//	public void addInputFile(String inputFile) {
//		this.inputFiles.add(inputFile);
//	}
//
//	private void setInputFiles(Set<String> inputFiles) {
//		this.inputFiles = inputFiles;
//	}
	
	public BlendFile getBlendFile() {
		return blendFile;
	}

	public void setBlenderFile(String blenderFile, String fluidFolder) throws FileNotFoundException {
		
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
		
//		this.addInputFile(blenderFile);
		
		File fluidFolderFile = null;
		if ( StringUtils.isNotBlank(fluidFolder) ) {
			fluidFolderFile = registry.getFileManager().getLocalCacheFile(fluidFolder);
		}
		BlendFile file = new BlendFile(registry.getFileManager().getLocalCacheFile(blenderFile), fluidFolderFile);
		setBlenderFile(file);
		
		
	}
	
	public void setBlenderFile(BlendFile file) {
		blendFile = file;
		
		System.out.println("StartFrame: "+blendFile.getStartFrame());
		System.out.println("EndFrame: "+blendFile.getEndFrame());
		System.out.println(StringUtils.join(blendFile.getReferrencedFiles().values(), "\n"));

		setFirstFrame(blendFile.getStartFrame());
		setLastFrame(blendFile.getEndFrame());
		
		
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

	public void setLocationsToInclude(String[] sites) {
		this.multiPartJob.setLocationsToInclude(sites);
	}
	
	public void setLocationsToExclude(String[] sites) {
		this.multiPartJob.setLocationsToExclude(sites);
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

	public void onEvent(String arg0, BatchJobEvent arg1) {

		if (verbose) {
			System.out.println(arg1.getMessage());
		}

	}
	
	public void setJobDistributionMethod(String method) {
		
		getMultiPartJobObject().setJobDistributionMethod(method);
		
	}


}
