import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;

import au.org.arcs.jcommons.constants.Constants;

public class GetArchivedJobExample {

	/**
	 * @param args
	 * @throws LoginException
	 * @throws NoSuchJobException
	 */
	public static void main(String[] args) throws LoginException,
			NoSuchJobException {
		// TODO Auto-generated method stub

		ServiceInterface si = LoginManager.loginCommandline("Local");

		DtoJobs jobs = si.getArchivedJobs(null);

		for (DtoJob job : jobs.getAllJobs()) {

			System.out.println("Job: "
					+ DtoJob.getProperty(job, Constants.JOBNAME_KEY));

			// or, alternatively:
			JobObject jobObject = new JobObject(si, job);
			System.out.println("Job object: " + jobObject.getJobname());
			System.out.println("\t" + jobObject.getApplication());
			System.out.println("\t" + jobObject.getApplicationVersion());

			System.out.println("\t" + jobObject.getJobDirectoryUrl());

		}

	}
}
