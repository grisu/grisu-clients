package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoBatchJob;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.info.ApplicationInformation;

public final class JobCreationInfo {

	public static void main(final String[] args)
			throws ServiceInterfaceException, LoginException,
			NoSuchJobException {

		final String username = args[0];
		final char[] password = args[1].toCharArray();

		final LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
		// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
		// "http://localhost:8080/enunciate-backend/soap/GrisuService",
				"Local", username, password);

		ServiceInterface si = null;
		// si = LoginManager.login(null, password, username, "VPAC",
		// loginParams);
		si = LoginManager.login(null, null, null, null, loginParams);

		// DtoJobs test = si.ps(true);

		for (final String name : si.getAllBatchJobnames("Blender").asArray()) {
			System.out.println(name);
		}

		System.out.println("--------------------------------------------");

		for (final String name : si.getAllJobnames("blender").asArray()) {
			System.out.println(name);
		}

		final DtoBatchJob mjob = si.getBatchJob("blenderLogoTest");

		for (final DtoJob job : mjob.getJobs().getAllJobs()) {
			System.out.println("Job: " + job.jobname());
			System.out.println(job.logMessagesAsString(true));
			System.out.println("---------------------------------");
		}

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
		registry.getResourceInformation().getAllSubmissionLocations();

		for (final String subLoc : registry.getUserEnvironmentManager()
				.getAllAvailableSubmissionLocations()) {
			System.out.println(subLoc);
		}

		final ApplicationInformation appInfo = registry
				.getApplicationInformation("java");
		for (final String version : appInfo
				.getAllAvailableVersionsForFqan("/ARCS/NGAdmin")) {
			System.out.println(version);
		}

	}

	private JobCreationInfo() {
	}

}
