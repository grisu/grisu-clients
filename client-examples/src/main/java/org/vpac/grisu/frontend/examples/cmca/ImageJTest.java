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

public class ImageJTest {

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
		int numberOfJobs = 2;

		// the (unique) name of the multijob
		String batchJobName = JobnameHelpers.calculateTimestampedJobname("exampleBatchJob");



		System.out.println("Creating batchjob "+batchJobName);
		// create the multipart job
		BatchJobObject batchJob = new BatchJobObject(si, batchJobName, "/ARCS/NGAdmin", "ImageJ", Constants.NO_VERSION_INDICATOR_STRING);


		for (int i=0; i<=numberOfJobs; i++) {
			// create a unique jobname for every job
			String jobname = batchJobName+"_"+ i;

			System.out.println("Creating job: "+jobname);

			// create the single job
			JobObject job = new JobObject(si);
			job.setJobname(jobname);
			// better to set the application to use explicitely because in that case we don't need to use mds (faster)
			job.setApplication("ImageJ");
			job.setCommandline("echo Hello");
		}

		// this should be set because it's used for the matchmaking/metascheduling
		batchJob.setDefaultNoCpus(1);
		batchJob.setDefaultWalltimeInSeconds(60);



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
