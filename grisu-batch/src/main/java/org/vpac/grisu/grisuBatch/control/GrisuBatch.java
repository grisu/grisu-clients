package org.vpac.grisu.grisuBatch.control;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.generic.GenericJobWrapper;
import org.vpac.grisu.client.control.generic.SimpleJsdlListener;
import org.vpac.grisu.client.control.login.LoginException;
import org.vpac.grisu.client.control.login.LoginHelpers;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.utils.FileHelpers;

public class GrisuBatch {

	static final Logger myLogger = Logger.getLogger(GrisuBatch.class.getName());

	private final static String PARAMETER = "PARAMETER";

	// common options
	public final static String SERVICE_INTERFACE_URL_OPTION = "serviceInterfaceUrl";
	public final static String MODE_OPTION = "mode";
	public final static String SUBMIT_MODE_PARAMETER = "submit";
	public final static String LIST_MODE_PARAMETER = "list";
	public final static String STAGEOUT_MODE_PARAMETER = "stageout";
	public final static String CLEAN_MODE_PARAMETER = "clean";

	public final static String FIRST_OPTION = "first";
	public final static String LAST_OPTION = "last";
	public final static String SWEEP_PARAMETERS_OPTION = "parameters";
	public final static String BASEJOBNAME = "baseJobName";

	// submit options
	public final static String SUBMISSION_LOCATION_OPTION = "submissionLocation";
	public final static String VO_OPTION = "vo";
	public final static String COMMAND_OPTION = "command";
	public final static String WALLTIME_OPTION = "walltime";
	public final static String CPUS_OPTION = "cpus";
	public final static String BASEINPUTFILEPATH_OPTION = "baseInputFilePath";

	// stageout options
	public final static String BASEOUTPUTFILENAMES_OPTION = "baseOutputFileNames";
	public final static String STAGEOUTDIRECTORY_OPTION = "stageOutDirectory";

	private EnvironmentManager em = null;
	private String serviceInterfaceUrl = "https://grisu.vpac.org/grisu-ws/services/grisu";

	private Map<String, Exception> failedJobCreations = null;
	private Map<String, JobSubmissionException> failedJobSubmissions = null;

	private Set<String> failedParameters = null;

	private boolean parsed = false;

	private Options options = null;
	private HelpFormatter formatter = new HelpFormatter();

	private String mode = null;

	// private List<SimpleJsdlListener> submissionListeners = new
	// ArrayList<SimpleJsdlListener>();
	private SimpleJsdlListener listener = new SimpleJsdlListener();

	private GenericJobWrapper job = null;

	private String applicationName = "batch";

	private String baseInputFilePath = null;
	private String[] baseOutputFileNames = null;
	private URI stageOutDirectory = null;

	private String baseCommand = "echo \"Dummy command " + PARAMETER + ".\"";

	private String vo = "/ARCS/StartUp";
	private String submissionLocation = null;
	private String jobname = "batchJob";
	private Integer startRange = null;
	private Integer endRange = null;
	private String[] clSweepParameters = null;

	private Set<String> sweepParamenters = null;

	private int noCPUs = 1;
	private int walltimeInSeconds = 3600;
	private String module = null;

	public GrisuBatch(String[] args) {

		parseCLIargs(args);

	}

	private void createEnvironmentManager() throws LoginException, ServiceInterfaceException {

		LoginParams loginParams = new LoginParams(
				serviceInterfaceUrl, null,
				null, "myproxy.arcs.org.au", "443");

		// LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu", null,
		// null, "myproxy.apac.edu.au", "7512");

		ServiceInterface serviceInterface = LoginHelpers.login(null,
				loginParams);

		em = new EnvironmentManager(serviceInterface);

	}

	public void login() throws LoginException, ServiceInterfaceException {

		System.out.println("Logging in...");
		createEnvironmentManager();
	}

	private void setBaseParameters(String jobname, Set<String> sweepParameters)
			throws InvalidJobnamesException {

		this.sweepParamenters = sweepParameters;

		checkValidJobnames(false);

	}

	public Integer getFirst() {
		return startRange;
	}

	public Integer getLast() {
		return endRange;
	}

	private void setBaseFilePath(String baseFilePath) {
		this.baseInputFilePath = baseFilePath;
	}

	private void setBaseCommand(String baseCommand) {
		this.baseCommand = baseCommand;
	}

	private void createJob() {

		System.out.println("Creating job...");

		job = new GenericJobWrapper(em);

		job.initialize(applicationName);
		job.useMds(false);

		job.addJsdlTemplateListener(listener);
	}

	private void fillJobParameters(String vo, String parameter)
			throws JobCreationException, SubmissionLocationException {

		System.out.println("Filling job " + parameter);
		job.setJobname(jobname + parameter);
		job.setVO(vo);
		job.removeAllInputFiles();
		SubmissionLocation subLoc = em.getSubmissionLocation(submissionLocation);
		
		if ( subLoc == null  ) {
			System.err.println("SubmissionLocation \""+submissionLocation+"\" could not be found. Exiting...");
			System.exit(1);
		}
		
		job.setSubmissionLocation(subLoc);

		job.setNumberOfCpus(noCPUs);
		job.setWalltimeInSeconds(walltimeInSeconds);

		if (baseInputFilePath != null & !"".equals(baseInputFilePath)) {
			String filePath = baseInputFilePath
					.replaceAll(PARAMETER, parameter);
			
			String[] files = filePath.split(",");
			for ( String file : files ) {
			
				job.addInputFile(file);
			}
			//job.setInputFile(filePath);
		}

		String commandline = baseCommand.replaceAll(PARAMETER, parameter);
		System.out.println("Setting commandline: " + commandline);

		job.setCommandLine(commandline);

		if (module != null && !"".equals(module)) {
			job.setModule(module);
		}

		System.out.println("Job creation done.");

	}

	private Set<String> checkValidJobnames(boolean refreshJoblist)
			throws InvalidJobnamesException {

		Set<String> allJobnames = em.getJobManager().getAllJobnames(
				refreshJoblist);
		Set<String> takenJobnames = new HashSet<String>();

		for (String testParameter : sweepParamenters) {
			if (allJobnames.contains(jobname + testParameter)) {
				takenJobnames.add(jobname + testParameter);
			}
		}

		if (takenJobnames.size() > 0) {
			throw new InvalidJobnamesException(takenJobnames);
		}

		return takenJobnames;
	}

	private void submitJob() throws JobSubmissionException {

		System.out.println("Submitting job...");

		job.submit();

		job.waitForSubmissionToFinish();

		if (listener.getSubmissionException() != null) {
			throw listener.getSubmissionException();
		}

		System.out.println("Submission finished.");

		System.out.println("Resetting job parameters.");
		job.reset();

	}

	// public void submitJobs(String[] args) {
	//
	// submitJobs();
	//		
	// Set<String> failedJobs = getFailedParameters();
	//
	// if (failedJobs.size() > 0) {
	// StringBuffer errors = new StringBuffer(
	// "Could not submit the following jobs:\n\n");
	// for (String error : failedJobs) {
	// errors.append(error + "\n");
	// }
	// System.out.println(errors.toString());
	// }
	// System.out.println("Job submission finished.");
	// }

	// private void submitJobs(String vo, String baseName,
	// String[] sweepParameters, String baseCommand, String baseFilePath,
	// int noCpus, int walltimeInSeconds) throws InvalidJobnamesException {

	private Set<String> calculateSweepParameters() {

		Set<String> allParameters = new TreeSet<String>();
		if (startRange != null && endRange != null) {
			int delta = endRange - startRange + 1;
			String[] pars = new String[delta];

			for (int i = 0; i < delta; i++) {
				pars[i] = new Integer(startRange + i).toString();
			}
			allParameters.addAll(Arrays.asList(pars));
		}
		
		if (clSweepParameters != null) {
			allParameters.addAll(Arrays.asList(clSweepParameters));
		}

		return allParameters;
	}

	private void submitJobs() {

		failedParameters = new TreeSet<String>();
		failedJobCreations = new TreeMap<String, Exception>();
		failedJobSubmissions = new TreeMap<String, JobSubmissionException>();

		Set<String> sweepParameters = calculateSweepParameters();

		try {
			setBaseParameters(jobname, sweepParameters);
		} catch (InvalidJobnamesException e1) {
			System.err.println(e1.getLocalizedMessage());
			System.exit(1);
		}
		setBaseCommand(baseCommand);
		setBaseFilePath(baseInputFilePath);

		createJob();

		for (String parameter : sweepParameters) {
			try {
				fillJobParameters(vo, parameter);
			} catch (Exception e) {
				failedParameters.add(parameter);
				failedJobCreations.put(parameter, e);
				continue;
			}

			try {
				submitJob();
			} catch (JobSubmissionException e) {
				failedParameters.add(parameter);
				failedJobSubmissions.put(parameter, e);
			}
		}

	}

	// public void monitorJob() throws NoSuchJobException {
	//
	// Set<String> allJobnames = em.getGlazedJobManagement().getAllJobnames(
	// true);
	//
	// assert allJobnames.contains(jobname);
	// System.out.println("Job now in joblist of the user. Good.");
	//
	// jobMonitoringObject = em.getGlazedJobManagement().getJob(jobname);
	// assert JobConstants
	// .translateStatusBack(jobMonitoringObject.getStatus()) >
	// JobConstants.JOB_CREATED;
	// System.out.println("Job exists and has got status > JOB_CREATED");
	// }

	private Map<String, GrisuJobMonitoringObject> getRelevantJobNames() {

		Map<String, GrisuJobMonitoringObject> result = new TreeMap<String, GrisuJobMonitoringObject>();

		for (String parameter : calculateSweepParameters()) {
			try {
				result.put(parameter, em.getJobManager().getJob(
						jobname + parameter));
			} catch (NoSuchJobException e) {

				result.put(parameter, null);
			}
		}
		return result;
	}

	private void listJobs() {

		Map<String, GrisuJobMonitoringObject> jobs = getRelevantJobNames();

		System.out.println("Status of sub-jobs of batch-job \"" + jobname
				+ "\":");

		for (String parameter : jobs.keySet()) {
			System.out.print(jobname + parameter + ":\t\t");
			if (jobs.get(parameter) == null) {
				System.out.println("Not found (probably already staged out)");
			} else {
				System.out.println(jobs.get(parameter).getStatus(true));
			}
		}

	}
	
	private void cleanJobs() {
		
		Map<String, GrisuJobMonitoringObject> jobs = getRelevantJobNames();

		System.out.println("Cleaning sub-jobs of batch-job \"" + jobname
				+ "\":");

		for (String parameter : jobs.keySet()) {
			System.out.print(jobname + parameter + ":\t\t");
			if (jobs.get(parameter) == null) {
				System.out.println("Not found (probably already cleaned.)");
			} else {
				try {
					jobs.get(parameter).killAndClean();
					System.out.println("Success.");
				} catch (Exception e) {
					System.out.println("Error: "+e.getLocalizedMessage());
				}
			}
		}
		
	}

	private void stageOutResults() {

		Map<String, GrisuJobMonitoringObject> jobs = getRelevantJobNames();

		System.out.println("Staging out files for batch-job \"" + jobname
				+ "\".");

		GrisuFileObject targetDirectory = null;
		targetDirectory = em.getFileManager().getFileObject(stageOutDirectory);

		for (String parameter : jobs.keySet()) {
			// System.out.print(parameter + ":\t\t");
			if (jobs.get(parameter) == null) {
				System.out
						.println("Failed: Job not found (probably already staged out)");
			} else {
				GrisuJobMonitoringObject job = jobs.get(parameter);

				System.out.print(jobname + parameter + ":\t\t");

				// BackendFileObject[] replacedFileNames = new
				// BackendFileObject[baseOutputFileNames.length];

				try {
					System.out.print("Staging out:\t");
					for (int i = 0; i < baseOutputFileNames.length; i++) {

						String newFileName = baseOutputFileNames[i].replaceAll(
								PARAMETER, parameter);

						System.out.print(newFileName + " ");
						String newPath = job.getJobDirectory() + "/"
								+ newFileName;
						String targetFileName = newFileName;
						if (newFileName.equals(baseOutputFileNames[i])) {
							targetFileName = jobname + parameter + "_"
									+ targetFileName;
						}

						try {
							// download the file
							FileHelpers.saveToDisk(em.getServiceInterface()
									.download(newPath).getDataSource(), new File(new File(
									stageOutDirectory), targetFileName));
						} catch (Exception e) {
							System.out.println("Could not download/save file "
									+ newFileName + ": "
									+ e.getLocalizedMessage());
						}

					}

					// FileManagerTransferHelpers.transferFiles(em
					// .getServiceInterface(), replacedFileNames,
					// targetDirectory, false);
					// FileHelpers
					// .saveToDisk(
					// em.getServiceInterface().download(
					// job.getJobDirectory() + "/"
					// + stageOutFileName), new File(
					// "/tmp/" + parameter));
					System.out.println("Success.");
				} catch (Exception e) {
					System.out.println("Failed: " + e.getLocalizedMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private void parseCLIargs(String[] args) {

		// create the parser
		CommandLineParser parser = new GnuParser();
		CommandLine line = null;
		try {
			// parse the command line arguments
			line = parser.parse(getOptions(), args);
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			formatter.printHelp("grisu-batch", getOptions());
			System.exit(1);
		}

		if (!line.hasOption(MODE_OPTION)) {
			System.err
					.println("Please specify the mode you want to use: submit|list|stageout");
			formatter.printHelp("grisu-batch", getOptions());
			System.exit(1);
		}

		// for the different modes
		mode = line.getOptionValue("mode");

		if (SUBMIT_MODE_PARAMETER.equals(mode)) {
			
			if (!line.hasOption(SUBMISSION_LOCATION_OPTION)) {
				System.err.println("Please specify the submission location you want to use.");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

			if (!line.hasOption(VO_OPTION)) {
				System.err.println("Please specify the VO you want to use.");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

			if (!line.hasOption(COMMAND_OPTION)) {
				System.err
						.println("Please specify the comand you want to run.");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

			if (!line.hasOption(WALLTIME_OPTION)) {
				System.err.println("Please specify the walltime.");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

			if (!line.hasOption(CPUS_OPTION)) {
				System.err.println("Please specify the number of cpus.");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

			submissionLocation = line.getOptionValue(SUBMISSION_LOCATION_OPTION);
			
			vo = line.getOptionValue(VO_OPTION);

			baseCommand = line.getOptionValue(COMMAND_OPTION);

			if (line.hasOption(BASEINPUTFILEPATH_OPTION)) {
				baseInputFilePath = line
						.getOptionValue(BASEINPUTFILEPATH_OPTION);
			} else {
				myLogger.debug("No input files to stage for this batch job.");
				baseInputFilePath = null;
			}

			try {
				walltimeInSeconds = Integer.parseInt(line
						.getOptionValue(WALLTIME_OPTION));
			} catch (NumberFormatException e) {
				System.err.println("Please use an integer for the walltime");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

			try {
				noCPUs = Integer.parseInt(line.getOptionValue(CPUS_OPTION));
			} catch (NumberFormatException e) {
				System.err.println("Please use an integer for the no. of cpus");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}

		} else if (LIST_MODE_PARAMETER.equals(mode)) {

		} else if (STAGEOUT_MODE_PARAMETER.equals(mode)) {

			if (!line.hasOption(BASEOUTPUTFILENAMES_OPTION)) {
				myLogger
						.debug("No files to stageout specified. Using \"stdout.txt\"");
				baseOutputFileNames = new String[] { "stdout.txt" };
			} else {
				baseOutputFileNames = line.getOptionValue(
						BASEOUTPUTFILENAMES_OPTION).split(",");
			}

			if (!line.hasOption(STAGEOUTDIRECTORY_OPTION)) {
				myLogger
						.debug("No stageout directory specified. Using current directory.");
				stageOutDirectory = new File(".").toURI();

			} else {
				stageOutDirectory = new File(line
						.getOptionValue(STAGEOUTDIRECTORY_OPTION)).toURI();
			}

		} else if ( CLEAN_MODE_PARAMETER.equals(mode) ) {
			
		} else {
			System.err.println("Mode " + mode + " not supported.");
			formatter.printHelp("grisu-batch", getOptions());
			System.exit(1);
		}

		// common options
		if (!line.hasOption(BASEJOBNAME)) {
			System.err.println("Please specify the base jobname.");
			formatter.printHelp("grisu-batch", getOptions());
			System.exit(1);
		}

		if (!line.hasOption(SWEEP_PARAMETERS_OPTION)) {
			if (!line.hasOption(FIRST_OPTION) || !line.hasOption(LAST_OPTION)) {
				System.err
						.println("You either have to specify a list of parameters or a parameter range (or both).");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}
		}

		if (!line.hasOption(FIRST_OPTION)
				&& !line.hasOption(SWEEP_PARAMETERS_OPTION)) {
			System.err
					.println("Please specify the first Integer in the parameter range.");
			formatter.printHelp("grisu-batch", getOptions());
			System.exit(1);
		}

		if (!line.hasOption(LAST_OPTION)
				&& !line.hasOption(SWEEP_PARAMETERS_OPTION)) {
			System.err
					.println("Please specify the last Integer in the parameter range.");
			formatter.printHelp("grisu-batch", getOptions());
			System.exit(1);
		}

		if ( line.hasOption(SERVICE_INTERFACE_URL_OPTION) ) {
			serviceInterfaceUrl = line.getOptionValue(SERVICE_INTERFACE_URL_OPTION);
		}
		
		jobname = line.getOptionValue(BASEJOBNAME);
		if (line.hasOption(FIRST_OPTION)) {
			try {
				startRange = Integer
						.parseInt(line.getOptionValue(FIRST_OPTION));
			} catch (NumberFormatException e) {
				System.err
						.println("Please use an integer for the first parameter in range");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}
		}
		if (line.hasOption(LAST_OPTION)) {
			try {
				endRange = Integer.parseInt(line.getOptionValue(LAST_OPTION));
			} catch (NumberFormatException e) {
				System.err
						.println("Please use an integer for the last parameter in range");
				formatter.printHelp("grisu-batch", getOptions());
				System.exit(1);
			}
		}

		if (line.hasOption(SWEEP_PARAMETERS_OPTION)) {
			clSweepParameters = line.getOptionValue(SWEEP_PARAMETERS_OPTION)
					.split(",");
		}
		parsed = true;
	}

	private Options getOptions() {

		if (options == null) {

			// common options
			Option serviceInterfaceUrl = OptionBuilder.withArgName(SERVICE_INTERFACE_URL_OPTION).hasArg()
					.withDescription("the serviceinterface to connect to (optional)").create(SERVICE_INTERFACE_URL_OPTION);
			Option mode = OptionBuilder.withArgName(MODE_OPTION).hasArg()
					.withDescription(
							"the mode you want to use: submit|list|stageout")
					.create(MODE_OPTION);
			Option baseName = OptionBuilder.withArgName(BASEJOBNAME).hasArg()
					.withDescription("the base name for the job").create(
							BASEJOBNAME);
			Option first = OptionBuilder
					.withArgName(FIRST_OPTION)
					.hasArg()
					.withDescription("the first Integer in the parameter range")
					.create(FIRST_OPTION);
			Option last = OptionBuilder.withArgName(LAST_OPTION).hasArg()
					.withDescription("the last Integer in the parameter range")
					.create(LAST_OPTION);

			Option sweepParameters = OptionBuilder
					.withArgName(SWEEP_PARAMETERS_OPTION)
					.hasArg()
					.withDescription(
							"A comma seperated list of parameters to iterate through. You can use that instead or in combination with the \"first\" & \"last\" option")
					.create(SWEEP_PARAMETERS_OPTION);

			// submit options
			Option submissionLocation = OptionBuilder.withArgName(SUBMISSION_LOCATION_OPTION).hasArg()
					.withDescription("the submission location (e.g. dque@brecca-m:ng2.vpac.monash.edu.au)").create(SUBMISSION_LOCATION_OPTION);
			Option vo = OptionBuilder.withArgName(VO_OPTION).hasArg()
					.withDescription("the vo to use").create(VO_OPTION);
			Option command = OptionBuilder.withArgName(COMMAND_OPTION).hasArg()
					.withDescription("the commandline to run remotely").create(
							COMMAND_OPTION);
			Option baseInputFilePath = OptionBuilder.withArgName(
					BASEINPUTFILEPATH_OPTION).hasArg().withDescription(
					"the base name of the file").create(
					BASEINPUTFILEPATH_OPTION);
			Option walltime = OptionBuilder.withArgName(WALLTIME_OPTION)
					.hasArg().withDescription("the walltime in seconds")
					.create(WALLTIME_OPTION);
			Option cpus = OptionBuilder.withArgName(CPUS_OPTION).hasArg()
					.withDescription("the number of cpus to run the job with")
					.create(CPUS_OPTION);

			// stageout options
			Option stageOutFileNames = OptionBuilder
					.withArgName(BASEOUTPUTFILENAMES_OPTION)
					.hasArg()
					.withDescription(
							"the filenames to stage out, seperated with commas, relative to the jobdirectory")
					.create(BASEOUTPUTFILENAMES_OPTION);
			Option stageOutDirectory = OptionBuilder.withArgName(
					STAGEOUTDIRECTORY_OPTION).hasArg().withDescription(
					"the directory to stage out the files to").create(
					STAGEOUTDIRECTORY_OPTION);

			options = new Options();
			options.addOption(serviceInterfaceUrl);
			options.addOption(mode);
			options.addOption(submissionLocation);
			options.addOption(vo);
			options.addOption(baseName);
			options.addOption(first);
			options.addOption(last);
			options.addOption(sweepParameters);
			options.addOption(command);
			options.addOption(baseInputFilePath);
			options.addOption(walltime);
			options.addOption(cpus);
			options.addOption(stageOutFileNames);
			options.addOption(stageOutDirectory);
		}

		return options;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		GrisuBatch batch = new GrisuBatch(args);

		batch.login();

		if (SUBMIT_MODE_PARAMETER.equals(batch.getMode())) {

			batch.submitJobs();

		} else if (LIST_MODE_PARAMETER.equals(batch.getMode())) {

			batch.listJobs();

		} else if (STAGEOUT_MODE_PARAMETER.equals(batch.getMode())) {
			batch.stageOutResults();
		} else if (CLEAN_MODE_PARAMETER.equals(batch.getMode())) {
			batch.cleanJobs();
		} else {
			System.err.println("The mode " + batch.getMode()
					+ " is not supported.");
		}

	}

	public Map<String, Exception> getFailedJobCreations() {
		return failedJobCreations;
	}

	public Map<String, JobSubmissionException> getFailedJobSubmissions() {
		return failedJobSubmissions;
	}

	public Set<String> getFailedParameters() {
		return failedParameters;
	}

	public String getMode() {
		return mode;
	}

}
