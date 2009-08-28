package org.vpac.grisu.clients.blender;

import jline.ConsoleReader;

import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.control.exceptions.MultiPartJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GridBlender {

	public static void main(String args[]) {

		final Cli<BlenderCommandLineArgs> cli = CliFactory
				.createCli(BlenderCommandLineArgs.class);

		BlenderCommandLineArgs result = null;
		try {
			result = cli.parseArguments(args);
		} catch (ArgumentValidationException e) {
			System.err.println("Could not start grid-blender:\n"
					+ e.getLocalizedMessage() + "\n");
			System.out.println(cli.getHelpMessage());
			System.exit(1);
		}

		String username = result.getUsername();

		char[] password = null;
		try {
			ConsoleReader consoleReader = new ConsoleReader();
			password = consoleReader.readLine(
					"Please enter your myproxy password: ", new Character('*'))
					.toCharArray();
		} catch (Exception e) {
			System.err.println("Couldn't read password input: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/xfire-backend/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
				// "https://ngportal.vpac.org/grisu-ws/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
				// "http://localhost:8080/enunciate-backend/soap/GrisuService",
				"Local",
				// "Dummy",
				username, password);

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
		} catch (ServiceInterfaceException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

		String fqan = result.getVo();
		String jobname = result.getJobname();

		GrisuBlenderJob job = null;
		try {
			job = new GrisuBlenderJob(si, jobname, fqan);
		} catch (MultiPartJobException e) {
			System.err.println("Could not create blender job: "
					+ e.getLocalizedMessage());
		}

		job.setBlenderFile(result.getBlendFile());
		job.setFirstFrame(result.getStartFrame());
		job.setLastFrame(result.getEndFrame());
		job.setDefaultWalltimeInSeconds(result.getWalltime() * 60);
		if (result.isExclude()) {
			job.setSitesToExclude(result.getExclude().toArray(new String[] {}));
		}
		if (result.isInclude()) {
			job.setSitesToInclude(result.getInclude().toArray(new String[] {}));
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

		System.out.println("Blender job submission finished successfully...");

	}

}
