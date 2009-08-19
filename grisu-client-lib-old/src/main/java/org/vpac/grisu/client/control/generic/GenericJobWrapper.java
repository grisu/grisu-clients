package org.vpac.grisu.client.control.generic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.exceptions.TemplateException;
import org.vpac.grisu.client.control.login.LoginException;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.JsdlTemplateListener;
import org.vpac.grisu.client.model.template.modules.Common;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.templatePreProcessor.TemplatePreProcessorException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

import au.org.arcs.jcommons.utils.JsdlHelpers;


/**
 * GenericJobWrapper is supposed to be an easy-to-use wrapper class that encapsulates
 * functionality to create and submit a job using Grisu.
 * 
 * This class internally wrapps a {@link Common} module to take care of the
 * usual common job attributes.
 * 
 * Have a look at the main methods in {@link SimpleCommandlineClient} or {@link SimpleCommandlineClientNoMds}
 * to see examples on how it is used.
 * 
 * @author Markus Binsteiner
 *
 */
public class GenericJobWrapper {
	
	public static final String INPUT_FILES_TEMPLATE_NODE = "InputFiles";
	public static final String COMMANDLINE_TEMPLATE_NODE = "Commandline";
	public static final String STDOUT_TEMPLATE_NODE = "StdOut";
	public static final String DEFAULT_STDOUT = "stdout.txt";
	public static final String STDERR_TEMPLATE_NODE = "StdErr";
	public static final String DEFAULT_STDERR = "stderr.txt";
	public static final String COMMON_GENERIC_MODULE = "Common";
	
	static final Logger myLogger = Logger.getLogger(GenericJobWrapper.class.getName());
	
	private EnvironmentManager em = null;
	private String application = null;
	private JsdlTemplate template = null;
	
	private DefaultTemplateNodeValueSetter inputFilesSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter commandLineSetter = new DefaultTemplateNodeValueSetter();
	
	private DefaultTemplateNodeValueSetter stdoutSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter stderrSetter = new DefaultTemplateNodeValueSetter();
	
	/**
	 * The constructor for a GenericJobWrapper that uses a cosumized template.
	 * The standard template is src/main/resources/baseTemplate.xml. If you want 
	 * to use your own one, provide it here. Your template has to use the {@link Common} 
	 * module in order for this to work.
	 * Also, you have to have one template node with the name of the {@link #COMMANDLINE_TEMPLATE_NODE}
	 * value and one with the name of the {@link #INPUT_FILES_TEMPLATE_NODE}.
	 * You can have your own additional template nodes which you would have to take
	 * care of manually. Now that I think of it: it probably would be better, 
	 * if you want to use your own template, to use this class as base class and
	 * write your own JobWrapper class that extends it.
	 * @param em the environment manager
	 * @param xmlTemplateDoc the template
	 */
	public GenericJobWrapper(EnvironmentManager em, Document xmlTemplateDoc) {
		this.em = em;
		try {
			initialize(JsdlHelpers.getApplicationName(xmlTemplateDoc), xmlTemplateDoc);
		} catch (TemplateException e) {
			// should never happen. If it does, there's something major wrong with the code/template
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructs a GenericJobWrapper object that uses the base template for the
	 * job description. The base template is located here: src/main/resources/baseTemplate.xml.
	 * @param em the environment manager
	 */
	public GenericJobWrapper(EnvironmentManager em) {
		this.em = em;
	}
	
	/**
	 * This constructor creates a GenericJobWrapper object using the base
	 * template. It also initializes the object with the application you 
	 * provide.
	 * @param em the environment manager
	 * @param application the appliation used for job submission
	 */
	public GenericJobWrapper(EnvironmentManager em, String application) {
		this.em = em;
		initialize(application);
	}

	private Common getCommonGenericModule() {
		return (Common)template.getModule(COMMON_GENERIC_MODULE);
	}
	
	/**
	 * This method is the first one to call (if you didn't use the {@link #GenericJobWrapper(EnvironmentManager, String)}
	 * constructor). It initializes the job wrapper object and it's internal state 
	 * to use the specified application.
	 * 
	 * Don't use this method to change the appliation. Use {@link #changeApplication(String)} for that.
	 * @param application the application for the job
	 */
	public void initialize(String application) {
		InputStream basetemplateStream = null;

		basetemplateStream = GenericJobWrapper.class.getResourceAsStream("/baseTemplate.xml");

		Document xmltemplate = null;
		try {
			xmltemplate = SeveralXMLHelpers.fromInputStream(basetemplateStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		try {
			initialize(application, xmltemplate);
		} catch (TemplateException e) {
			// should never happen. If it does, there's something major wrong with the code/template
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the internal state of the job so the object can be reused again. This should give
	 * improved mds performance over creating a new one.
	 */
	public void reset() {
		try {
			template.reset(false);
		} catch (TemplatePreProcessorException e) {
			myLogger.error("Couldn't reset template: "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * Use this method if a job submission failed. It will delete a possibly created remote
	 * job directory and remove the job handle from the database.
	 */
	public void cleanJob() {
		try {
			template.reset(true);
		}catch (TemplatePreProcessorException e) {
			myLogger.error("Couldn't clean job: "+e.getLocalizedMessage());
		}
	}
	
	private void initialize(String application, Document xmlTemplateDoc) throws TemplateException {
		JsdlHelpers.setApplicationName(xmlTemplateDoc, application);
		template = new JsdlTemplate(this.em, xmlTemplateDoc);
		if ( template == null ) {
			throw new TemplateException("Couldn't create base template.");
		}
		this.application = application;
		
		// connecting the four non-module templatenodes
		template.getTemplateNodes().get(COMMANDLINE_TEMPLATE_NODE).setTemplateNodeValueSetter(commandLineSetter);
		template.getTemplateNodes().get(INPUT_FILES_TEMPLATE_NODE).setTemplateNodeValueSetter(inputFilesSetter);

		TemplateNode stdoutNode = template.getTemplateNodes().get(STDOUT_TEMPLATE_NODE);
		TemplateNode stderrNode = template.getTemplateNodes().get(STDERR_TEMPLATE_NODE);
		stdoutNode.setTemplateNodeValueSetter(stdoutSetter);
		stderrNode.setTemplateNodeValueSetter(stderrSetter);
		// set default values for stdout & stderr
		String stdoutDefault = stdoutNode.getDefaultValue();
		if ( stdoutDefault != null && ! "".equals(stdoutDefault) ) {
			stdoutSetter.setExternalSetValue(stdoutDefault);
		} else {
			stdoutSetter.setExternalSetValue(DEFAULT_STDOUT);
		}

		String stderrDefault = stderrNode.getDefaultValue();
		if ( stderrDefault != null && !"".equals(stderrDefault) ) {
			stderrSetter.setExternalSetValue(stderrDefault);
		} else {
			stderrSetter.setExternalSetValue(DEFAULT_STDERR);
		}
		
		getCommonGenericModule().setApplication(application);

	}
	
	private void check() throws JobCreationException {
		if ( this.template == null ){
			throw new JobCreationException("Template not initialized.");
		}
	}
	
	/**
	 * Switch between using mds for application specific lookups or not.
	 * If you specify true, the object will try to find all submission
	 * locations that are available for the selected VO and the mountpoints
	 * a user has got mounted. It won't display queues of sites where the 
	 * application is not installed for example.
	 * 
	 * If you specify false, Grisu still uses a bit of mds information. But
	 * only to find out which submission locations are available for the user
	 * (according to currently set VO and available mountpoints). The type
	 * of application is not considered here. This is useful if you can't rely
	 * on your application being published.
	 * 
	 * @param useMds whether to use mds or not
	 */
	public void useMds(boolean useMds) {
		getCommonGenericModule().useMds(useMds);
	}
	
	/**
	 * Since the actual job submission runs in it's own thread, here's a convenience
	 * method to wait until the submission is finished.
	 */
	public void waitForSubmissionToFinish() {
		template.waitForSubmissionToFinish();
	}
	
	/**
	 * Add an input file to the job at hand. Input files either are local 
	 * (for example: "/home/markus/test.txt") or remote (like: gsiftp://ngdata.vpac.org/home/markus/test.txt).
	 * If they should be within the file structure of one of the users mountpoints.
	 * Didn't test yet what happens if they are not...
	 * 
	 * You can call this method as often as you like/need.
	 * 
	 * @param inputFileUrl input file url string
	 * @throws JobCreationException if this object is not initialized yet.
	 */
	public void addInputFile(String inputFileUrl) throws JobCreationException {
		
		check();
		
		String currentInputFiles = inputFilesSetter.getExternalSetValue();

		if ( currentInputFiles == null || currentInputFiles.length() == 0 ) {
			inputFilesSetter.setExternalSetValue(inputFileUrl);
			myLogger.debug("New input files: "+inputFileUrl);
		} else {
			inputFilesSetter.setExternalSetValue(currentInputFiles+";"+inputFileUrl);
			myLogger.debug("New input files: "+currentInputFiles+";"+inputFileUrl);
		}
	}
	
	public void removeAllInputFiles() {
		
		inputFilesSetter.setExternalSetValue(null);
		
	}
	
	/**
	 * Much like {@link #addInputFile(String)}. Only files that were previously added are overwritten.
	 * Useful, for example if you called {@link #reset()} and don't want to use the files from the 
	 * last submission.
	 * 
	 * @param inputFileUrl the file
	 * @throws JobCreationException if this object is not initialized yet.
	 */
	public void setInputFile(String inputFileUrl) throws JobCreationException {
		
		check();
		
		inputFilesSetter.setExternalSetValue(inputFileUrl);
		myLogger.debug("Set input file: "+inputFileUrl);
	}
	
	/**
	 * Sets the commandline that is supposed to be executed by the job.
	 * For example, a simple cat job with one input file named test.txt would
	 * need: "cat test.txt". You also would have to add "test.txt" (of course with it's
	 * full path) using the {@link #addInputFile(String)} method.
	 * @param commandLine the commandline to execute
	 * @throws JobCreationException if this object is not initialized yet
	 */
	public void setCommandLine(String commandLine) throws JobCreationException {
		
		check();
		
		commandLineSetter.setExternalSetValue(commandLine);
	}
	
	public void setStdout(String stdout) throws JobCreationException {
		
		check();
		
		stdoutSetter.setExternalSetValue(stdout);
	}
	
	public void setStderr(String stderr) throws JobCreationException {
		
		check();
		
		stderrSetter.setExternalSetValue(stderr);
	}
	
	/**
	 * Sets the module that should be loaded for this job on the remote resource. 
	 * This overrides the calculated module for a mds enabled job until either 
	 * a new VO or SubmissionLocaiton is set. 
	 * For a java job you would set "java" here.
	 * @param module the module
	 * @throws JobCreationException if this object is not initialized yet
	 */
	public void setModule(String module) throws JobCreationException {
		check();
		
		getCommonGenericModule().setModule(module);
	}
	
	/**
	 * Sets the number of cpus that are used for this job.
	 * @param no the amount of cpus to use
	 */
	public void setNumberOfCpus(int no) {
		getCommonGenericModule().setCPUs(no);
	}
	
	/**
	 * If you want to get notified when your job is finished/cancelled,
	 * specify your email address here.
	 * If not, don't use it or set null.
	 * @param email your email address
	 */
	public void setEmailAddress(String email){
		getCommonGenericModule().setEmailAddress(email);
	}
	
	/**
	 * Sets the walltime in seconds for this job. 
	 * @param seconds the walltime
	 */
	public void setWalltimeInSeconds(long seconds) {
		getCommonGenericModule().setWalltime(seconds);
	}
	
	/**
	 * Sets the name for this job. Internally, Grisu uses the jobname
	 * as a handle, so you need to specify a unique name here.
	 * To see what jobnames are in use at the moment, call {@link #getCurrentlyExistingJobnames()}.
	 * A jobname can be reused once the job is deleted.
	 * @param jobname the name of this job.
	 */
	public void setJobname(String jobname) {
		getCommonGenericModule().setJobname(jobname);
	}
	
	/**
	 * Sets the submission location and returns a list of available VOs for the combination of 
	 * selected application and submission location
	 * @param subLoc the submission location
	 * @return possible VOs that can be used with the current combination of application and submission location
	 * @throws SubmissionLocationException if the submission location is not available for this application
	 * @throws JobCreationException if the application is not set yet.
	 */
	public Set<String> setSubmissionLocation(SubmissionLocation subLoc) throws SubmissionLocationException, JobCreationException {
		getCommonGenericModule().setSubmissionLocation(subLoc);
		return getCommonGenericModule().getAvailableVOsForCurrentSubmissionLocation();
	}
	
	/**
	 * Sets the current VO and returns a list of all available submission locations for the 
	 * this VO (which depends on whether {@link #useMds(boolean)} is set or and if so, which
	 * application is set).
	 * @param fqan the VO
	 * @return all available submission locations for the current state (may be empty)
	 * @throws JobCreationException if the application is not set yet.
	 */
	public Set<SubmissionLocation> setVO(String fqan) throws JobCreationException {

		getCommonGenericModule().setVO(fqan);
		return getCommonGenericModule().getAvailableSubmissionLocationsForCurrentVO();
	}
	
	/**
	 * Returns a list of all available submission locations for the 
	 * this VO (which depends on whether {@link #useMds(boolean)} is set or and if so, which
	 * application is set).
	 * @return all available submission locations for the current state (may be empty)
	 */
	public Set<SubmissionLocation> getSubmissionLocations() {
		return getCommonGenericModule().getAvailableSubmissionLocationsForCurrentVO();
	}
	
	/**
	 * Returns all available sites for the currently selected application & VO for
	 * this user.
	 * @return all available sites
	 */
	public Set<String> getCurrentlyAvailableSites() {
		return getCommonGenericModule().getAvailableSitesForCurrentVO().keySet();
	}

	/**
	 * Returns all queues that are available for the specified site in combination
	 * with the currently selected VO and application.
	 * @param site the site
	 * @return all available queues
	 */
	public Set<SubmissionLocation> getAvailableQueuesForSite(String site) {
		
		return getCommonGenericModule().getAvailableQueuesForSite(site);
	}
	
	/**
	 * If {@link #useMds(boolean)} is set to true and executables are published for the 
	 * currently selected application and the currently selected submission location, the 
	 * this returns all available executables for this situation.
	 * 
	 * If the internal state of this job wrapper object is inconclusive (aka either application or submission
	 * location is not set or they don't fit together) then the JobCreationException is thrown.
	 * 
	 * It doesn't make sense to call this method if you don't use mds.
	 * 
	 * @return the executables for the current state of this object
	 * @throws JobCreationException if the internal state of this object is inconclusive
	 */
	public String[] getExecutablesForCurrentState() throws JobCreationException {
		return getCommonGenericModule().getCurrentSubmissionObject().getCurrentExecutables();
	}
	
	/**
	 * If {@link #useMds(boolean)} is set to true all available versions for the currently set
	 * application on the currently set submission location are returned. 
	 * 
	 * @return all available versions for the current state of this object
	 */
	public String[] getVersionsForCurrentState() {
		return getCommonGenericModule().getVersions().toArray(new String[]{});
	}
	
	/**
	 * Here you can set the version of the application you want to use for this job submission.
	 * It is recommended that you choose one of the values that are returned by 
	 * {@link #getVersionsForCurrentState()} otherwise you'll get an exception.
	 * @param version the version to use.
	 * @throws JobCreationException if the version you choose is not supported at the currently selected submission location.
	 */
	public void setVersion(String version) throws JobCreationException {
		getCommonGenericModule().setVersion(version);
	}
	
	/**
	 * Change the type of application you want to use. Since you can reuse this object after
	 * a job submission finished, this makes sense in some cases.
	 * 
	 * @param newApplication the new application type.
	 */
	public void changeApplication(String newApplication) {
		JsdlHelpers.setApplicationName(template.getTemplateDocument(), application);
		getCommonGenericModule().setApplication(newApplication);
	}
	
	/**
	 * Since the actual job submission runs in it's own thread, you've got the opportunity
	 * here to connect a listener to figure out what's going on.
	 * @param l the listener
	 */
	public void addJsdlTemplateListener(JsdlTemplateListener l) {
		template.addJsdlTemplateListener(l);
	}
	
	/**
	 * Remove a jsdl template listener.
	 * @param l the listener
	 */
	public void removeJsdlTemplateListener(JsdlTemplateListener l) {
		template.removeJsdlTemplateListener(l);
	}
	
	/**
	 * Starts the submission. The submission runs in it's own thread, so you might want to connect
	 * a listener ({@link #addJsdlTemplateListener(JsdlTemplateListener)} to monitor the progress 
	 * (of the submission -- not the actual job).
	 * @throws JobSubmissionException if some basic requirements are not fullfilled the job submission
	 * doesn't start in the first place.
	 */
	public void submit() throws JobSubmissionException {

		template.startSubmission(getCommonGenericModule().getCurrentFqan());
//		template.waitForSubmissionToFinish();
	}
	
	/**
	 * A convenience method to get a list of the names of all currently running jobs.
	 * Needed to figure out a jobname for a new job, since a jobname has to be unique.
	 * @return all jobnames
	 */
	public String[] getCurrentlyExistingJobnames() {
		Set<String> jobs = new TreeSet<String>();
		for ( GrisuJobMonitoringObject job : em.getJobManager().getAllJobs(false) ) {
			 jobs.add(job.getName());
		}
		return jobs.toArray(new String[]{});
	}
	
	
	// example
	public static void main(String[] args) {
		
		BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );
		

		
		try {
			LoginParams loginparams = new LoginParams("https://grisu.vpac.org/grisu-ws/services/grisu", "markus", "xxx".toCharArray(), "myproxy.arcs.org.au", "443");
			
			EnvironmentManager em = null;
			try {
				em = ServiceInterfaceFactoryOld.login(loginparams);
			} catch (LoginException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String application = null;
			while ( application == null || "".equals(application) ) {
				System.out.println("Please enter the application you want to use. Press enter to see a list of all available applications.");
				application = in.readLine();
				if ( application == null || "".equals(application) ) {
					StringBuffer allApps = new StringBuffer();
					allApps.append("Available applications: ");
					for ( String name : em.getServiceInterface().getAllAvailableApplications(em.getAllOfTheUsersSites().toArray(new String[]{})) ) {
						allApps.append(name+" ");
					}
					System.out.println(allApps.toString());
				} 
			}
			
			GenericJobWrapper job = new GenericJobWrapper(em, application);
			
			String jobname = null;
			while ( jobname == null || "".equals(jobname) ) {
				System.out.println("Please enter the name of the job. Press enter to see a list of all your current jobnames.");
				jobname = in.readLine();
				if ( jobname == null || "".equals(jobname) ) {
					StringBuffer allJobs = new StringBuffer();
					allJobs.append("Current jobnames: ");
					for ( String name : job.getCurrentlyExistingJobnames() ) {
						allJobs.append(name+" ");
					}
					System.out.println(allJobs.toString());
				} else {
					job.setJobname(jobname);
				}
			}
			
			String fqan = null;
			while ( fqan == null || "".equals(fqan) ) {
				System.out.println("Please enter the VO you want to use. Press enter to see a list of all your VOs.");
				fqan = in.readLine();
				if ( fqan == null || "".equals(fqan) ) {
					StringBuffer allVOs = new StringBuffer();
					allVOs.append("Available VOs: ");
					for ( String name : em.getFqans() ) {
						allVOs.append(name+" ");
					}
					System.out.println(allVOs.toString());
				} 
			}
			job.setVO(fqan);
			
			SubmissionLocation[] submissionLocations = job.getSubmissionLocations().toArray(new SubmissionLocation[]{});
			System.out.println("Plese select the submissionLocation: ");
			for ( int i=1; i<submissionLocations.length+1; i++ ) {
				System.out.println("["+i+"] "+submissionLocations[i-1].getLocation()+" ("+submissionLocations[i-1].getSite()+")");
			}
			
			int input = new Integer(in.readLine());
			
			SubmissionLocation submissionLocation = submissionLocations[input-1];
			
			String[] fqans = job.setSubmissionLocation(submissionLocation).toArray(new String[]{});
//			
//			System.out.println("Please select from one of these VOs: ");
//			for ( int i=1; i<fqans.length+1; i++ ) {
//				System.out.println("["+i+"]"+fqans[i-1]);
//			}
//			
//			input = new Integer(in.readLine());
//			String fqan = fqans[input-1];
//			job.setFqan(fqan);

			String[] versions = job.getVersionsForCurrentState();
			System.out.println("Please select the version you want to use: ");
			for ( int i=1; i<versions.length+1; i++ ) {
				System.out.println("["+i+"]"+versions[i-1]);
			}
			
			input = new Integer(in.readLine());
			String version = versions[input-1];
			job.setVersion(version);
			
			String[] exes = job.getExecutablesForCurrentState();
			
			System.out.println("Please select the executable you want to use: ");
			for ( int i=1; i<exes.length+1; i++ ) {
				System.out.println("["+i+"]"+exes[i-1]);
			}
			
			input = new Integer(in.readLine());
			String exe = exes[input-1];
			
			System.out.println("Please specify the rest of your command: ");
			System.out.print(exe+" ");
			
			String restCommand = in.readLine();
			
			job.setCommandLine(exe+" "+restCommand);
			
			// now add files in some way
			job.addInputFile("/home/markus/Desktop/JavaTestJob.jar");
			
			job.setNumberOfCpus(1);
			job.setWalltimeInSeconds(600);

			job.addJsdlTemplateListener(new SimpleJsdlListener());
			job.submit();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int getSubmissionStatus() {
		return template.getStatus();
	}
}
