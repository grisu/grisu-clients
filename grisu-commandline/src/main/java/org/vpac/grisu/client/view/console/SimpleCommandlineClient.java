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
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.frontend.control.login.LoginHelpers;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.model.dto.DtoStringList;

public class SimpleCommandlineClient implements JsdlTemplateListener {

	static final Logger myLogger = Logger
			.getLogger(SimpleCommandlineClient.class.getName());

	public static final String CERTIFICATE_LOGIN = "With local certificate.";
	public static final String MYPROXY_LOGIN = "With remote myproxy credentials.";

	public static final String EXIT_COMMAND = "exit";

	private ConsoleReader reader = null;

	public void println() {
//		try {
//			reader.printNewline();
//		} catch (IOException e) {
//			// should never happen. well...
//			myLogger.error("Could not read input. Exiting...");
//			System.exit(1);
//		}
		System.out.println();
	}

	public void print(String message) {
//		try {
//			reader.printString(message);
////			System.out.println(message);
//		} catch (IOException e) {
//			// should never happen. well...
//			myLogger.error("Could not read input. Exiting...");
//			System.exit(1);
//		}
		System.out.print(message);
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
				print("This is not an integer. Please try again.\n");
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

	public SimpleCommandlineClient() {
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

		String loginMethod = (String) displayChoices(
				"How do you want to login to the grisu service?", new String[] {
						CERTIFICATE_LOGIN, MYPROXY_LOGIN });

		if (loginMethod.equals(CERTIFICATE_LOGIN)) {

			char[] certPassphrase = readPasswordInput("Please enter your certificate passphrase");

			LoginParams loginparams = new LoginParams(
					"https://grisu.vpac.org/grisu-ws/services/grisu", null,
					null, "myproxy.apac.edu.au", "443");

			ServiceInterface serviceInterface = null;
			while (serviceInterface == null) {
				try {
					serviceInterface = LoginManager.login(null, certPassphrase, null, null, loginparams);
//					serviceInterface = LoginHelpers.localProxyLogin(certPassphrase,	loginparams);
				} catch (Exception e) {
					print("Could not login: " + e.getLocalizedMessage());
					print("Please try again.");
				}
			}
			em = new EnvironmentManager(serviceInterface);
			return em;

		} else if (loginMethod.equals(MYPROXY_LOGIN)) {
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
		return null;
	}

	public static void main(String[] args) {

		SimpleCommandlineClient scc = new SimpleCommandlineClient();

		EnvironmentManager em = scc.login();

		GenericJobWrapper job = new GenericJobWrapper(em);

		// ask about application to use. for that, get information about which
		// applications
		// are available for the sites the user has got access to.
		// this has to be done before anything else
		String[] allApplications = em.getServiceInterface()
				.getAllAvailableApplications(DtoStringList.fromStringColletion(em.getAllOfTheUsersSites())).asArray();

		String application = (String) scc
				.displayChoices(
						"Which application do you want to run. Please select from the following:",
						allApplications);
		// set the job
		job.initialize(application);

		// now we need to set a jobname. this will be the handle to access it
		// later. the user has to choose
		// a unique one, otherwise the jobsubmission will fail
		String jobname = scc.getUniqueJobname(job
				.getCurrentlyExistingJobnames());
		job.setJobname(jobname);
		
		// lets set the stdout & stderr filenames as well. If we wouldn't do that 
		// they just would be "stdout.txt" and "stderr.txt" which is fine most of
		// the times
		try {
			job.setStdout(jobname+"_stdout.txt");
			job.setStderr(jobname+"_stderr.txt");
		} catch (JobCreationException e) {
			// this only happens when the application is not set
			scc
					.print("For some reason the application could not be set successfully. Exiting...\n");
			System.exit(1);
		}

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

		// now we offer the user to select a version if there are more than 2.
		// This is optional
		// since Grisu would just pick one that fits the specified application,
		// fqan and submission location
		// if it is not set.
		String[] versions = job.getVersionsForCurrentState();
		if (versions.length > 1) {
			String version = (String) scc.displayChoices(
					"Please select the version of " + application
							+ " you want to use:", versions);
			try {
				job.setVersion(version);
			} catch (JobCreationException e) {
				// this sholdn't really happen
				scc
						.print("Job not setup correctly or version not available. Exiting...\n");
				System.exit(1);
			}
		} else if (versions.length == 0) {
			// this shouldn't really happen
			scc
					.print("No versions for this application, fqan and submission location found. Exiting...\n");
			System.exit(1);
		} else {
			scc
					.print("Automatically choosing version for this job submission...\n");
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
		// now we may have to select one of the executables if the application
		// offers more of them
		String[] executables = null;
		String executable = null;
		try {
			executables = job.getExecutablesForCurrentState();
		} catch (JobCreationException e) {
			// the internal state of the job is inconclusive because of not
			// everything necessary set.
			// this shouldn't happen in our case
			scc
					.print("Couldn't query executables because of inconclusive job state. Exiting...\n");
			System.exit(1);
		}
		if (executables.length > 1) {
			executable = (String) scc.displayChoices(
					"Please select the executable you want to use:",
					executables);
		} else if (executables.length == 1) {
			executable = executables[0];
			scc.print("Using executable \"" + executable
					+ "\" because it's the only choice.\n");
		} else {
			// no executables published
			// doesn't matter
			scc
					.print("No executable published for this combination of application and submission location. You'll have to specify it yourself later.\n");
		}

		// almost done. now we need to get the rest of the commandline
		String commandline = null;
		if (executable == null) {
			commandline = scc
					.readStringInput("Please provide the commandline you want to run:\n");
		} else {
			scc.print("Please complete the commandline you want to run:\n");
			commandline = executable + " "
					+ scc.readStringInput(executable + " ");
		}

		try {
			job.setCommandLine(commandline);
		} catch (JobCreationException e) {
			// shouldn't happen
			scc.print("Template not initialized. Exiting...\n");
			System.exit(1);
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
	}

	public void templateStatusChanged(JsdlTemplateEvent event) {
		print("[Status changed] " + event.getMessage());
	}

}
