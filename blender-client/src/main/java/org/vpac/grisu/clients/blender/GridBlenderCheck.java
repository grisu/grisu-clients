package org.vpac.grisu.clients.blender;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.events.BatchJobEvent;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GridBlenderCheck implements BlenderMode,
		EventTopicSubscriber<BatchJobEvent> {

	private BlenderCheckCommandLineArgs commandlineArgs = null;
	private final ServiceInterface si;

	private final String jobname;

	public GridBlenderCheck(String[] args, boolean help) {

		final Cli<BlenderCheckCommandLineArgs> cli = CliFactory
				.createCli(BlenderCheckCommandLineArgs.class);

		if (help) {
			System.out.println(cli.getHelpMessage());
			System.exit(0);
		}

		try {
			commandlineArgs = cli.parseArguments(args);
			if (commandlineArgs.getHelp()) {
				System.out.println(cli.getHelpMessage());
				System.exit(1);
			}
		} catch (final ArgumentValidationException e) {
			System.err.println("Could not start blender-job-monitor:\n"
					+ e.getLocalizedMessage() + "\n");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}

		if (!commandlineArgs.isJobname()) {
			System.out.println("Jobname not specified.");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}

		jobname = commandlineArgs.getJobname();

		EventBus.subscribe(jobname, this);

		if (commandlineArgs.isDownloadResults()) {
			try {
				final File downloadDir = commandlineArgs.getDownloadResults();
				if (downloadDir.exists()) {
					if (!downloadDir.canWrite()) {
						System.out
								.println("Can't write to specified output directory "
										+ downloadDir.toString() + ".");
						System.exit(1);
					}
				}
			} catch (final Exception e) {
				System.out
						.println("Could not access specified download directory.");
			}
		}

		si = GridBlenderUtils.login(commandlineArgs);

	}

	private void downloadCurrentlyFinishedFiles(
			BatchJobObject blenderMultiPartJobObject) {
		final File downloadDirectory = commandlineArgs.getDownloadResults();

		final String pattern = blenderMultiPartJobObject
				.getProperty(GrisuBlenderJob.BLENDER_OUTPUTFILENAME_KEY);
		if (StringUtils.isBlank(pattern)) {
			System.out
					.println("Could not determine output filename. Exiting...");
			System.exit(1);
		}
		final String[] patterns = new String[] { pattern };
		try {
			System.out.println("Downloading output files that match \""
					+ pattern + "\".\n");
			blenderMultiPartJobObject.downloadResults(true, downloadDirectory,
					patterns, false, false);
			System.out.println("Downloads finished.");
		} catch (final Exception e) {
			System.out.println("Could not download results: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}
	}

	@Override
	public void execute() {

		System.out.println("Retrieving job " + commandlineArgs.getJobname()
				+ ". This might take a while...");

		GrisuBlenderJob blenderJob = null;
		try {
			blenderJob = new GrisuBlenderJob(si, commandlineArgs.getJobname());
		} catch (final Exception e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

		final BatchJobObject blenderMultiPartJobObject = blenderJob
				.getMultiPartJobObject();

		boolean firstTime = true;

		if (commandlineArgs.isLoopUntilFinished() || firstTime) {

			int sleepTime = 60 * 1000;
			try {
				sleepTime = commandlineArgs.getLoopUntilFinished() * 60 * 1000;
			} catch (final Exception e) {
				// e.printStackTrace();
				// doesn't matter
			}

			while (!blenderMultiPartJobObject.isFinished(false) || firstTime) {

				if (!firstTime) {
					if (sleepTime / 60000 == 1) {
						System.out.println("Sleeping for " + sleepTime / 60000
								+ " minute.");
					} else {
						System.out.println("Sleeping for " + sleepTime / 60000
								+ " minutes.");
					}

					try {
						Thread.sleep(sleepTime);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}

				firstTime = false;

				blenderMultiPartJobObject.refresh();
				if (commandlineArgs.isDetailed()) {

					System.out.println(blenderMultiPartJobObject.getDetails());

				}

				if (commandlineArgs.isStatus()) {
					System.out.println(blenderMultiPartJobObject.getProgress());
				}

				final boolean finished = blenderMultiPartJobObject
						.isFinished(false);
				final boolean cmdln = commandlineArgs.isLoopUntilFinished();

				if (finished || !cmdln) {
					break;
				}

				if (commandlineArgs.isDownloadResults()) {
					System.out
							.println("Downloading already finished frames to: "
									+ commandlineArgs.getDownloadResults()
											.toString());
					downloadCurrentlyFinishedFiles(blenderMultiPartJobObject);
				}

			}

			if (blenderMultiPartJobObject.isFinished(false)) {

				if (commandlineArgs.isDownloadResults()) {

					downloadCurrentlyFinishedFiles(blenderMultiPartJobObject);

				}
			}

		}

	}

	@Override
	public void onEvent(String arg0, BatchJobEvent arg1) {
		System.out.println(arg1.getMessage());
	}

}
