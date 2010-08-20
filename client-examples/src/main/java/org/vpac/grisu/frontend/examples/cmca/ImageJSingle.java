package org.vpac.grisu.frontend.examples.cmca;

import org.vpac.grisu.control.JobnameHelpers;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.BackendException;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;

public class ImageJSingle {

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


		ApplicationInformation appInfo = GrisuRegistryManager.getDefault(si).getApplicationInformation("ImageJ");
		appInfo.getAvailableAllSubmissionLocations();

		System.out.println("Appname: "+appInfo.getApplicationName());

		for (String subloc : appInfo.getAvailableAllSubmissionLocations()) {
			System.out.println(subloc);
		}

		//		// create the single job
		//		JobObject job = new JobObject(si);
		//		job.setTimestampJobname("imageJ");
		//		// better to set the application to use explicitely because in that case we don't need to use mds (faster)
		//		job.setApplication("ImageJ");
		//		job.setCommandline("echo Hello");
		//
		//		job.setSubmissionLocation("normal:ng2.ivec.org");
		//		try {
		//			job.createJob("/ARCS/StartUp");
		//		} catch (JobPropertiesException e) {
		//			e.printStackTrace();
		//		}
		//		job.submitJob();
		//
		//		job.waitForJobToFinish(3);
		//
		//		System.out.println("Stdout: "+job.getStdOutContent());
		//		System.out.println("Stderr: "+job.getStdErrContent());



	}

}
