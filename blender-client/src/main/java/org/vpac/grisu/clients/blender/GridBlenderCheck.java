package org.vpac.grisu.clients.blender;

import org.vpac.grisu.control.ServiceInterface;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GridBlenderCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		final Cli<BlenderCheckCommandLineArgs> cli = CliFactory
				.createCli(BlenderCheckCommandLineArgs.class);

		BlenderCheckCommandLineArgs commandlineArgs = null;
		try {
			commandlineArgs = cli.parseArguments(args);
		} catch (ArgumentValidationException e) {
			System.err.println("Could not start blender-job-monitor:\n"
					+ e.getLocalizedMessage() + "\n");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}

		ServiceInterface si = GridBlenderUtils.login(commandlineArgs);
		
		GrisuBlenderJob blenderJob = null;
		try {
			blenderJob = new GrisuBlenderJob(si, commandlineArgs.getJobname());
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		
		System.out.println(blenderJob.getProgress());

	}

}
