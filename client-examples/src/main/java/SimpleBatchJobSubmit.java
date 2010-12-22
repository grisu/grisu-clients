import java.util.Date;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.BatchJobObject;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.model.job.JobsException;
import au.org.arcs.jcommons.constants.Constants;

public class SimpleBatchJobSubmit {

	public static void main(final String[] args) throws Exception {

		// logging in..
		final ServiceInterface si = LoginManager
				.loginCommandline("BeSTGRID-DEV");
		// final ServiceInterface si = LoginManager.loginCommandline("Local");

		// how many jobs do we want?
		final int numberOfJobs = 5;

		final Date start = new Date();
		final String multiJobName = "batchExample_" + start.getTime();

		// creating the batch job, not worrying about the version of the
		// application on the backend
		final BatchJobObject batchJob = new BatchJobObject(si, multiJobName,
				"/ARCS/BeSTGRID", "UnixCommands",
				Constants.NO_VERSION_INDICATOR_STRING);

		// now we need to create all the jobs manually
		for (int i = 0; i < numberOfJobs; i++) {

			final int jobNumber = i;

			final JobObject jo = new JobObject(si);
			// the jobname of the single job needs to be unique in your
			// jobnamespace
			jo.setJobname(multiJobName + "_" + jobNumber);

			jo.setCommandline("ls -lah " + batchJob.pathToInputFiles()
					+ "/temp_exampleFolder");

			// now we need to attach the job to the batchjob
			batchJob.addJob(jo);

		}

		batchJob.setConcurrentInputFileUploadThreads(10);

		batchJob.addInputFile("/home/markus/tmp/tmpSmall", "temp_exampleFolder");

		// we can include/exclude sites/gateways if we have a reason to do so
		// (maybe we know that one site is down or so)
		// batchJob.setLocationsToExclude(new String[] { "otago" });

		// if all jobs have the same no of cpus, we can specify it here
		batchJob.setDefaultNoCpus(1);
		// if all jobs have the same walltime, we can specify it here
		batchJob.setDefaultWalltimeInSeconds(310);

		try {
			// now we need to prepare the batchjob on the backend
			// if you use "true" as a parameter, Grisu will distribute the job
			// accross all the sites that support the specified application.
			// if you specify "false", you should have specified a submission
			// location
			// for each sub-job seperately ("true" would overwrite that)
			// this will also upload any possible input files
			batchJob.prepareAndCreateJobs(true);
		} catch (final JobsException e) {
			for (final JobObject job : e.getFailures().keySet()) {
				System.out.println("Creation " + job.getJobname() + " failed: "
						+ e.getFailures().get(job).getLocalizedMessage());
			}
			System.exit(1);
		}

		// this shows how many jobs are submitted where...
		System.out.println("Job distribution:");
		System.out.println(batchJob.getOptimizationResult());

		try {
			// finally, we submit the job
			batchJob.submit(true);
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Submission finished: " + new Date());

		// now we wait for the job to finish
		// in real life, choose a longer period inbetween checks, since this
		// would
		// probably overload the backend
		// if you want to know in more detail what is happening, than you can
		// implement your own "while-job-is-still-running" loop to maybe
		// output how many jobs are already finished or re-submit failed jobs
		// while other jobs are still in the queue, for example
		batchJob.waitForJobToFinish(10);

		// if one or more of the sub-jobs failed, we might need to do something
		// about it, maybe resubmit...
		if (batchJob.failedJobs().size() > 0) {
			System.out.println("Some of the jobs failed :-(");
		}

		// now we want to know the output for every sub-job
		for (final JobObject job : batchJob.getJobs()) {
			System.out.println("-------------------------------");
			System.out.println(job.getJobname() + ": "
					+ job.getStatusString(false));
			System.out.println(job.getStdOutContent());
			System.out.println("-------------------------------");
			System.out.println(job.getStdErrContent());
			System.out.println("-------------------------------");
			System.out.println();
		}

	}

}
