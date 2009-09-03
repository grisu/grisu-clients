package org.vpac.grisu.clients.blender;

import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.model.job.MultiPartJobEventListener;
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GridBlenderSubmit implements BlenderMode {

	private BlenderSubmitCommandLineArgs commandlineArgs;
	private final ServiceInterface si;

	public GridBlenderSubmit(String[] args) {
		
		final Cli<BlenderSubmitCommandLineArgs> cli = CliFactory
				.createCli(BlenderSubmitCommandLineArgs.class);

		try {
			commandlineArgs = cli.parseArguments(args);
		} catch (ArgumentValidationException e) {
			System.err.println("Could not start grid-blender:\n"
					+ e.getLocalizedMessage() + "\n");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}
		
		if ( ! commandlineArgs.isJobname() ) {
			System.out.println("Jobname not specified.");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}
		si = GridBlenderUtils.login(commandlineArgs);
	}

	public void execute() {

		String fqan = commandlineArgs.getVo();
		String jobname = commandlineArgs.getJobname();

		GrisuBlenderJob job = null;

		// check whether job with this name already exists
		try {
			job = new GrisuBlenderJob(si, jobname);

			if (commandlineArgs.isForceKill()) {

				try {
					if (commandlineArgs.isVerbose()) {
						System.out.println("Deleting existing multipart job "
								+ jobname + ". This might take a while...");
					}
					si.deleteMultiPartJob(jobname, true);
					if (commandlineArgs.isVerbose()) {
						System.out
								.println("Deleting of existing multipart job "
										+ jobname + " finished.");
					}
				} catch (NoSuchJobException nsje) {
					// that's ok.
				} catch (Exception e) {
					System.out.println("Could not delete existing job "
							+ jobname + ": " + e.getLocalizedMessage());
					System.out
							.println("Handling this is not implemented yet. :-(");
					System.exit(1);
				}

			} else {
				System.out
						.println("Job with jobname "
								+ jobname
								+ " already exists on the backend. Use the --forceKill option to delete it.");
				System.exit(1);
			}

		} catch (Exception e1) {
			// doesn't really matter in that case
		}

		try {
			job = new GrisuBlenderJob(si, jobname, fqan);
		} catch (MultiPartJobException e) {
			System.err.println("Could not create blender job: "
					+ e.getLocalizedMessage());
		}

		job.setVerbose(commandlineArgs.isVerbose());

		job.setBlenderFile(commandlineArgs.getBlendFile());
		job.setFirstFrame(commandlineArgs.getStartFrame());
		job.setLastFrame(commandlineArgs.getEndFrame());
		job.setDefaultWalltimeInSeconds(commandlineArgs.getWalltime() * 60);
		job.setOutputFileName(commandlineArgs.getOutput());
		if (commandlineArgs.isExclude()) {
			job.setSitesToExclude(commandlineArgs.getExclude().toArray(
					new String[] {}));
		}
		if (commandlineArgs.isInclude()) {
			job.setSitesToInclude(commandlineArgs.getInclude().toArray(
					new String[] {}));
		}

		try {
			job.createAndSubmitJobs();
		} catch (JobCreationException e) {
			System.err.println("Could not create blender jobs: "
					+ e.getLocalizedMessage());
			System.exit(1);
		} catch (JobSubmissionException e) {
			System.err.println("Could not submt blender jobs: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		// System.out.println("Blender job submission finished successfully...");

	}

}
