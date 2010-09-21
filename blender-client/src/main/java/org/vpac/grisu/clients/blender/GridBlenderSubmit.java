package org.vpac.grisu.clients.blender;

import java.io.FileNotFoundException;
import java.util.Date;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.model.dto.DtoActionStatus;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GridBlenderSubmit implements BlenderMode {

	private BlenderSubmitCommandLineArgs commandlineArgs;
	private final ServiceInterface si;

	public GridBlenderSubmit(String[] args, boolean help) {

		final Cli<BlenderSubmitCommandLineArgs> cli = CliFactory
				.createCli(BlenderSubmitCommandLineArgs.class);

		if (help) {
			System.out.println(cli.getHelpMessage());
			System.exit(0);
		}

		try {
			commandlineArgs = cli.parseArguments(args);
		} catch (final ArgumentValidationException e) {
			System.err.println("Could not start grid-blender:\n"
					+ e.getLocalizedMessage() + "\n");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}

		if (!commandlineArgs.isJobname()) {
			System.out.println("Jobname not specified.");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}
		si = GridBlenderUtils.login(commandlineArgs);
	}

	@Override
	public void execute() {

		final Date start = new Date();

		final String fqan = commandlineArgs.getVo();
		final String jobname = commandlineArgs.getJobname();

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

					try {
						si.kill(jobname, true);

						DtoActionStatus status;
						while (!(status = si.getActionStatus(jobname))
								.isFinished()) {
							final double percentage = status
									.getCurrentElements()
									* 100
									/ status.getTotalElements();
							System.out.println("Deletion " + percentage
									+ "% finished.");
							Thread.sleep(3000);
						}
					} catch (final NoSuchJobException ne) {
						// good
					}

					if (commandlineArgs.isVerbose()) {
						System.out
								.println("Deleting of existing multipart job "
										+ jobname + " finished.");
					}

				} catch (final Exception e) {
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

		} catch (final Exception e1) {
			// doesn't really matter in that case
		}

		try {
			job = new GrisuBlenderJob(si, jobname, fqan);
		} catch (final BatchJobException e) {
			System.err.println("Could not create blender job: "
					+ e.getLocalizedMessage());
		}

		job.setVerbose(commandlineArgs.isVerbose());

		if (commandlineArgs.isExclude()) {
			job.setLocationsToExclude(commandlineArgs.getExclude().toArray(
					new String[] {}));
		} else if (commandlineArgs.isInclude()) {
			job.setLocationsToInclude(commandlineArgs.getInclude().toArray(
					new String[] {}));
		}

		String fluidsFolder = null;
		if (commandlineArgs.isFluidsFolder()) {
			fluidsFolder = commandlineArgs.getFluidsFolder();
		}
		try {
			job.setBlenderFile(commandlineArgs.getBlendFile(), fluidsFolder);
		} catch (final FileNotFoundException e1) {
			System.err.println("Could not create job: "
					+ e1.getLocalizedMessage());
			System.exit(1);
		}

		if (commandlineArgs.isStartFrame()) {
			job.setFirstFrame(commandlineArgs.getStartFrame());
		}
		if (commandlineArgs.isEndFrame()) {
			job.setLastFrame(commandlineArgs.getEndFrame());
		}
		job.setDefaultWalltimeInSeconds(commandlineArgs.getWalltime() * 60);
		job.setOutputFileName(commandlineArgs.getOutput());
		if (commandlineArgs.isExclude()) {
			job.setLocationsToExclude(commandlineArgs.getExclude().toArray(
					new String[] {}));
		}
		if (commandlineArgs.isInclude()) {
			job.setLocationsToInclude(commandlineArgs.getInclude().toArray(
					new String[] {}));
		}

		try {
			job.createAndSubmitJobs(true);
		} catch (final JobCreationException e) {
			System.err.println("Could not create blender jobs: "
					+ e.getLocalizedMessage());
			System.exit(1);
		} catch (final JobSubmissionException e) {
			System.err.println("Could not submt blender jobs: "
					+ e.getLocalizedMessage());
			System.exit(1);
		} catch (final InterruptedException e) {
			System.err.println("Job submission interrupted: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		System.out.println("Blender job submission finished successfully...");

		System.out.println("Submission start: " + start.toString());
		System.out.println("Submission end: " + new Date().toString());

	}

}
