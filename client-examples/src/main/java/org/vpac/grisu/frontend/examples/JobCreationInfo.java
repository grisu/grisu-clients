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

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
				// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				//				"http://localhost:8080/enunciate-backend/soap/GrisuService",
				"Local",
				username, password);

		ServiceInterface si = null;
		// si = LoginManager.login(null, password, username, "VPAC",
		// loginParams);
		si = LoginManager.login(null, null, null, null, loginParams);

		// DtoJobs test = si.ps(true);

		for (String name : si.getAllBatchJobnames("Blender").asArray()) {
			System.out.println(name);
		}

		System.out.println("--------------------------------------------");

		for (String name : si.getAllJobnames("blender").asArray()) {
			System.out.println(name);
		}

		DtoBatchJob mjob = si.getBatchJob("blenderLogoTest");

		for (DtoJob job : mjob.getJobs().getAllJobs()) {
			System.out.println("Job: " + job.jobname());
			System.out.println(job.logMessagesAsString(true));
			System.out.println("---------------------------------");
		}

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
		registry.getResourceInformation().getAllSubmissionLocations();

		for (String subLoc : registry.getUserEnvironmentManager()
				.getAllAvailableSubmissionLocations()) {
			System.out.println(subLoc);
		}

		ApplicationInformation appInfo = registry
		.getApplicationInformation("java");
		for (String version : appInfo
				.getAllAvailableVersionsForFqan("/ARCS/NGAdmin")) {
			System.out.println(version);
		}

	}

	private JobCreationInfo() {
	}

}
