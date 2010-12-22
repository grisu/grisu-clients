import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobPropertiesException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;
import org.vpac.grisu.settings.ClientPropertiesManager;

import au.org.arcs.jcommons.constants.Constants;

public class SimpleEndToEndWorkflow {

	/**
	 * @param args
	 * @throws RemoteFileSystemException
	 */
	public static void main(String[] args) throws RemoteFileSystemException {

		ClientPropertiesManager.setConcurrentUploadThreads(10);

		System.out.println("Logging in...");
		ServiceInterface si = null;
		try {
			// si = LoginManager.loginCommandline("BeSTGRID-DEV");
			si = LoginManager.loginCommandline("Local");
		} catch (final LoginException e) {
			System.err.println("Could not login: " + e.getLocalizedMessage());
			System.exit(1);
		}

		System.out.println("Creating job...");
		final JobObject job = new JobObject(si);
		job.setApplication("UnixCommands");
		job.setTimestampJobname("MyFirstJob");

		// job.setSubmissionLocation("batch:ng2maggie.grid.otago.ac.nz");

		job.setSubmissionLocation("route@er171.ceres.auckland.ac.nz:ng2.auckland.ac.nz");

		job.addInputFileUrl("/home/markus/src/grisu-virtscreen/src/main/java",
				"halll    lllo");

		// job.addInputFileUrl("/home/markus/Desktop/src/untit led/test",
		// "hallll llo/another folder");

		System.out.println("Set jobname to be: " + job.getJobname());
		job.setCommandline("find .");

		job.setWalltimeInSeconds(60);

		try {
			System.out.println("Creating job on backend...");
			job.createJob("/ARCS/BeSTGRID");
		} catch (final JobPropertiesException e) {
			System.err.println("Could not create job: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		try {
			System.out.println("Submitting job to the grid...");
			job.submitJob();
		} catch (final JobSubmissionException e) {
			System.err.println("Could not submit job: "
					+ e.getLocalizedMessage());
			System.exit(1);
		} catch (final InterruptedException e) {
			System.err.println("Jobsubmission interrupted: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		System.out.println("Job submission finished.");
		System.out.println("Job submitted to: "
				+ job.getJobProperty(Constants.SUBMISSION_SITE_KEY));

		System.out.println("Waiting for job to finish...");

		// for a real workflow, don't check every 5 seconds since that would
		// put too much load on the backend/gateways
		job.waitForJobToFinish(5);

		System.out.println("List job directory: ");
		for (GridFile file : GrisuRegistryManager.getDefault(si)
				.getFileManager().ls(job.getJobDirectoryUrl()).getChildren()) {
			System.out.println("Name: " + file.getName() + " / URL: "
					+ file.getUrl());
		}

		System.out.println("Job finished with status: "
				+ job.getStatusString(false));

		System.out.println("Stdout: " + job.getStdOutContent());
		System.out.println("Stderr: " + job.getStdErrContent());

		// it's pretty important to shutdown the jvm properly. There might be
		// some executors running in the background
		// and they need to know when to shutdown.
		// Otherwise, your application might not exit.
		System.exit(0);

	}
}
