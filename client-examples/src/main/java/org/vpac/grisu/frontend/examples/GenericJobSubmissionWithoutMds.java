package org.vpac.grisu.frontend.examples;

import java.util.UUID;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public final class GenericJobSubmissionWithoutMds {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {

		final String username = args[0];
		final char[] password = args[1].toCharArray();

		final LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-cxf/services/grisu",
				"http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				// "Local",
				username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		final JobObject job = new JobObject(si);
		// job.setApplication(ServiceInterface.GENERIC_APPLICATION_NAME);
		// job.setApplication("java");
		job.setJobname("generic" + UUID.randomUUID());
		job.setCommandline("echo hello");
		job.addInputFileUrl("/home/markus/test.txt");
		// job.setSubmissionLocation("dque@brecca-m:ng2.vpac.monash.edu.au");
		// job.addModule("java");
		job.createJob("/ARCS/StartUp");

		job.submitJob();

		System.out.println("Main thread finished.");
	}

	private GenericJobSubmissionWithoutMds() {
	}

}
