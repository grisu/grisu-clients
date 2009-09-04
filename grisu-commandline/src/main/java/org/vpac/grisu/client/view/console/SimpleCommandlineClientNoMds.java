package org.vpac.grisu.client.view.console;

import java.io.IOException;
import java.util.Arrays;

import jline.ConsoleReader;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.generic.GenericJobWrapper;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.template.JsdlTemplateEvent;
import org.vpac.grisu.client.model.template.JsdlTemplateListener;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.frontend.control.login.LoginParams;

public class SimpleCommandlineClientNoMds implements JsdlTemplateListener {

	static final Logger myLogger = Logger
			.getLogger(SimpleCommandlineClientNoMds.class.getName());

	public static final String EXIT_COMMAND = "exit";

	private ConsoleReader reader = null;

	public void println() {
		try {
			reader.printNewline();
		} catch (IOException e) {
			// should never happen. well...
			myLogger.error("Could not read input. Exiting...");
			System.exit(1);
		}
	}

	public void print(String message) {
		try {
			reader.printString(message);
		} catch (IOException e) {
			// should never happen. well...
			myLogger.error("Could not read input. Exiting...");
			System.exit(1);
		}
	}

	public int readIntegerInput(String message) {

		Integer inputInt = null;
		String input = null;

		while (inputInt == null || EXIT_COMMAND.equals(input)) {
			input = readStringInput(message);
			if (EXIT_COMMAND.equals(input)) {
				print("Exiting...");
				System.exit(0);
			}
			try {
				inputInt = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				print("This is not an integer. Please try again.");
			}
		}
		return inputInt;
	}

	public Object displayChoices(String message, Object[] choices) {

		Object result = null;

		while (result == null) {
			print(message + "\n");
			for (int i = 1; i < choices.length + 1; i++) {
				print("[" + i + "] " + choices[i - 1] + "\n");
			}
			println();
			print("[0] Exit\n");

			int input = readIntegerInput("Plese select from the above: ");
			if (input == 0) {
				print("Exiting...");
				System.exit(0);
			} else if (input < 0 || input > choices.length + 1) {
				print("This is not a valid input. Please try again.");
			} else {
				result = choices[input - 1];
			}
		}
		return result;
	}

	public char[] readPasswordInput(String message) {
		try {
			return reader.readLine(message + ": ", '*').toCharArray();
		} catch (IOException e) {
			// should never happen. well...
			myLogger.error("Could not read input. Exiting...");
			System.exit(1);
		}
		return null;
	}

	public String readStringInput(String message) {

		try {
			return reader.readLine(message);
		} catch (IOException e) {
			// should never happen. well...
			myLogger.error("Could not read input. Exiting...");
			System.exit(1);
		}
		return null;
	}

	public SimpleCommandlineClientNoMds() {
		try {
			reader = new ConsoleReader();
		} catch (IOException e) {
			myLogger.error("Could not initialize console. Exiting...");
			System.exit(1);
		}
	}

	public String getUniqueJobname(String[] existingjobnames) {

		String jobname = null;
		while (jobname == null && !EXIT_COMMAND.equals(jobname)) {
			jobname = readStringInput("Please specify a unique jobname: ");
			if (EXIT_COMMAND.equals(jobname)) {
				print("Exiting...");
				System.exit(0);
			}
			if (Arrays.binarySearch(existingjobnames, jobname) >= 0) {
				jobname = null;
				print("Jobname already exists. Please try again.\n");
				continue;
			}
			return jobname;
		}
		return null;
	}

	public EnvironmentManager login() {
		EnvironmentManager em = null;
		String username = null;
		while (em == null && !EXIT_COMMAND.equals(username)) {

			username = readStringInput("Please enter your myproxy username (or type \""
					+ EXIT_COMMAND + "\" to exit): ");
			if (EXIT_COMMAND.equals(username)) {
				print("Exiting...");
				System.exit(0);
			}
			char[] password = readPasswordInput("Please enter your myproxy password");
			LoginParams loginparams = new LoginParams(
					"https://grisu.vpac.org/grisu-ws/services/grisu",
					username, password, "myproxy.arcs.org.au", "443");

			try {
				em = ServiceInterfaceFactoryOld.login(loginparams);
			} catch (Exception e) {
				print("Could not login: " + e.getLocalizedMessage());
				print("Please try again.");
			}
		}
		return em;
	}

	public static void main(String[] args) {

		SimpleCommandlineClientNoMds scc = new SimpleCommandlineClientNoMds();

		EnvironmentManager em = scc.login();

		GenericJobWrapper job = new GenericJobWrapper(em);
		// we need to know which application we want to run
		job.initialize("java");

		job.useMds(false);

		// now we need to set a jobname. this will be the handle to access it
		// later. the user has to choose
		// a unique one, otherwise the jobsubmission will fail
		String jobname = scc.getUniqueJobname(job
				.getCurrentlyExistingJobnames());
		job.setJobname(jobname);

		// now ask which VO the user wants to use. the environment manager knows
		// all about them.
		String[] vos = em.getAvailableFqans();
		String vo = (String) scc.displayChoices(
				"Please select the VO you want to use for this job:", vos);
		try {
			job.setVO(vo);
		} catch (JobCreationException e) {
			// this only happens when the application is not set
			scc
					.print("For some reason the application could not be set successfully. Exiting...\n");
			System.exit(1);
		}

		// now we need to specify where we want to submit the job to. The list
		// of possible submission
		// location changes according to which VO is set.
		SubmissionLocation[] possibleSubmissionLocations = job
				.getSubmissionLocations().toArray(new SubmissionLocation[] {});
		SubmissionLocation selectedSubmissionLocation = (SubmissionLocation) scc
				.displayChoices(
						"Please select where you want to submit the job to:",
						possibleSubmissionLocations);

		try {
			job.setSubmissionLocation(selectedSubmissionLocation);
		} catch (SubmissionLocationException e) {
			// submission location not available. this should not happen because
			// we used a value that was
			// returned to us earlier
			scc
					.print("Could not set submission location because it is not available for this application and fqan.\n");
			System.exit(1);
		} catch (JobCreationException e) {
			// this only happens when the application is not set
			scc
					.print("For some reason the application could not be set successfully. Exiting...\n");
			System.exit(1);
		}

		// ok. now we need a few more values like cpus, walltime, email address
		int cpus = scc
				.readIntegerInput("Please specify the no of cpus you want to use: ");
		job.setNumberOfCpus(cpus);

		int walltime = scc
				.readIntegerInput("Please specify the walltime in seconds: ");
		job.setWalltimeInSeconds(walltime);

		scc
				.print("If you want to get notified when the job finishes, please enter your email address here.\n");
		String email = scc.readStringInput("Otherwise, just press enter: ");
		if (email != null && !"".equals(email)) {
			job.setEmailAddress(email);
		}

		// almost done. now we need to get the rest of the commandline
		String commandline = null;

		commandline = scc
				.readStringInput("Please provide the commandline you want to run:\n");

		try {
			job.setCommandLine(commandline);
		} catch (JobCreationException e) {
			// shouldn't happen
			scc.print("Template not initialized. Exiting...\n");
			System.exit(1);
		}

		// and specify a module (if needed)
		String module = null;

		module = scc
				.readStringInput("Please enter the module to load (or press enter if none): ");
		module = module.trim();
		if (module != null && !"".equals(module)) {
			try {
				job.setModule(module);
			} catch (JobCreationException e1) {
				// shouldn't happen
				scc.print("Job not initialized. Exiting...\n");
				System.exit(1);
			}
		}
		// now we would have to specify input files. I'll document that later
		// basically, for local files, you do something like
		// job.addInputFile("/home/markus/test.txt");

		// start the submission. the submission has got it's own thread, so we
		// need
		// to connet a listener
		job.addJsdlTemplateListener(scc);
		try {
			scc.print("Submitting job.");
			job.submit();
			scc.print("Submission started...");
		} catch (JobSubmissionException e) {
			scc.print("Couldn't submit job: " + e.getLocalizedMessage() + "\n");
			scc.print("Exiting...\n");
			System.exit(1);
		}
		job.waitForSubmissionToFinish();
		scc.print("Submission finished.");
	}

	public void submissionExceptionOccured(JsdlTemplateEvent event,
			JobSubmissionException exception) {

		StringBuffer msg = new StringBuffer("[Error]"+event.getMessage());
		if ( exception.getParentException() != null ) {
			msg.append("\n\t[Cause]"+exception.getParentException().getLocalizedMessage());
		}
		print(msg.toString());
		
		//TODO delete the job from the server
	}

	public void templateStatusChanged(JsdlTemplateEvent event) {
		print("[Status changed] " + event.getMessage());
	}

}
