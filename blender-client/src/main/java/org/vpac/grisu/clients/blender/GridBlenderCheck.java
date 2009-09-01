package org.vpac.grisu.clients.blender;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.MultiPartJobObject;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GridBlenderCheck implements BlenderMode {

	private BlenderCheckCommandLineArgs commandlineArgs = null;
	private final ServiceInterface si;

	public GridBlenderCheck(String[] args) {

		final Cli<BlenderCheckCommandLineArgs> cli = CliFactory
				.createCli(BlenderCheckCommandLineArgs.class);

		try {
			commandlineArgs = cli.parseArguments(args);
			if (commandlineArgs.getHelp()) {
				System.out.println(cli.getHelpMessage());
				System.exit(1);
			}
		} catch (ArgumentValidationException e) {
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
		
		si = GridBlenderUtils.login(commandlineArgs);


	}

	public void execute() {

		System.out.println("Retrieving job "+commandlineArgs.getJobname()+". This might take a while...");
		
		GrisuBlenderJob blenderJob = null;
		try {
			blenderJob = new GrisuBlenderJob(si, commandlineArgs
					.getJobname());
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		
		MultiPartJobObject blenderMultiPartJobObject = blenderJob.getMultiPartJobObject();

		if (commandlineArgs.isDetailed()) {

			System.out.println(blenderMultiPartJobObject.getDetails());
			
		}

		if (commandlineArgs.isStatus()) {
			System.out.println(blenderMultiPartJobObject.getProgress(null));
		}

	}

}
