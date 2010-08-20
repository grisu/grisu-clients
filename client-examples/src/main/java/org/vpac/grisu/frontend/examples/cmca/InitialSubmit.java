package org.vpac.grisu.frontend.examples.cmca;

import org.vpac.grisu.control.JobnameHelpers;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.BackendException;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobsException;

import au.org.arcs.jcommons.constants.Constants;

public class InitialSubmit {

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws BackendException
	 * @throws LoginException
	 * @throws BatchJobException
	 * @throws NoSuchJobException
	 * @throws JobSubmissionException
	 */
	public static void main(String[] args) throws BackendException, InterruptedException, LoginException, BatchJobException, JobSubmissionException, NoSuchJobException {

		// display commandline login menu if no local proxy exists
		ServiceInterface si = LoginManager.loginCommandline();

		// how many jobs do we want
		int numberOfJobs = 20;

		// the (unique) name of the multijob
		String batchJobName = JobnameHelpers.calculateTimestampedJobname("exampleBatchJob");



		System.out.println("Creating batchjob "+batchJobName);
		// create the multipart job
		BatchJobObject batchJob = new BatchJobObject(si, batchJobName, "/ARCS/NGAdmin", "UnixCommands", Constants.NO_VERSION_INDICATOR_STRING);

		// now we can calculate the relative path (from every job directory) to the common input file folder
		String pathToInputFiles = batchJob.pathToInputFiles();

		String inputFile1Url = "/home/markus/test/inputFile1.txt";
		String inputFile1relPath = pathToInputFiles+"inputFile1.txt";

		String inputFile2Url = "gsiftp://ng2.vpac.org/home/grid-vpac/DC_au_DC_org_DC_arcs_DC_slcs_O_VPAC_CN_Markus_Binsteiner_qTrDzHY7L1aKo3WSy8623-7bjgM/inputFile2.txt";
		String inputFile2relPath = pathToInputFiles+"inputFile2.txt";

		String inputFile3Url = "/home/markus/test/errorFile.txt";
		String inputFile3relPath = pathToInputFiles + "errorFile.txt";

		for (int i=0; i<=numberOfJobs; i++) {
			// create a unique jobname for every job
			String jobname = batchJobName+"_"+ i;

			System.out.println("Creating job: "+jobname);

			// create the single job
			JobObject job = new JobObject(si);
			job.setJobname(jobname);
			// better to set the application to use explicitely because in that case we don't need to use mds (faster)
			job.setApplication("UnixCommands");
			if ( (i == 3) || (i == 13) ) {
				// this is just to demonstrate how to restart a failed job later on
				job.setCommandline("cat "+inputFile3relPath);
			} else {
				job.setCommandline("cat "+ inputFile1relPath + " " + inputFile2relPath);

				job.setWalltimeInSeconds(60);
				// adding the job to the multijob
				batchJob.addJob(job);
			}
		}

		// this should be set because it's used for the matchmaking/metascheduling
		batchJob.setDefaultNoCpus(1);
		batchJob.setDefaultWalltimeInSeconds(60);

		// now we add an input file that is common to all jobs
		batchJob.addInputFile(inputFile1Url);
		batchJob.addInputFile(inputFile2Url);
		batchJob.addInputFile(inputFile3Url);
		// we don't want to submit to tpac because it doesn't work
		//multiPartJob.setSitesToExclude(["uq", "hpsc", "auckland", "canterbury"]);

		try {
			System.out.println( "Creating jobs on the backend and staging files...");
			batchJob.prepareAndCreateJobs(true);
		} catch (JobsException e) {

			for ( JobObject job : e.getFailures().keySet() ) {
				System.out.println("Job: "+job.getJobname()+", Error: "+e.getFailures().get(job).getLocalizedMessage());
			}
			System.exit(1);
		}

		// this is not really needed
		System.out.println( "Job distribution:");
		System.out.println( batchJob.getOptimizationResult() );

		System.out.println( "Submitting jobs..." );
		batchJob.submit(true);

		System.out.println("Submission finished.");
		System.out.println("Name of submitted batchjob: "+batchJobName);


	}

}
