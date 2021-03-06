package org.vpac.grisu.frontend.examples;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;

public class JobSubmissionWithDialog {

	public static void main(final String[] args) throws Exception {

		final ExecutorService executor = Executors.newFixedThreadPool(1);

		final String username = args[0];
		final char[] password = args[1].toCharArray();

		final LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/xfire-backend/services/grisu",
		// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
		// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				"Local", username, password);

		final ServiceInterface si = ServiceInterfaceFactory
				.createInterface(loginParams);

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		final ApplicationInformation javaInfo = registry
				.getApplicationInformation("java");

		final Set<String> submissionLocations = javaInfo
				.getAvailableSubmissionLocationsForFqan("/ARCS/NGAdmin");

		// JobListDialog.open(si, "generic");

		// final JobStatusChangeListener jsl = new JobSubmissionNew();
		int i = 0;
		for (final String subLoc : submissionLocations) {
			i = i + 1;
			final int jobnumber = i;
			final Thread subThread = new Thread() {
				@Override
				public void run() {

					final JobObject jo = new JobObject(si);
					jo.setJobname("sleep_" + jobnumber + "_"
							+ UUID.randomUUID());
					jo.setApplication("generic");
					// jo.setModules(new String[]{"java"});
					jo.setCommandline("sleep 120");
					jo.setSubmissionLocation(subLoc);
					jo.setWalltimeInSeconds(60);
					// jo.addInputFileUrl("/home/markus/src/grisu/backend/grisu-core/sample input files/JavaTestJob.jar");
					// jo.addInputFileUrl("gsiftp://ng2.vpac.org/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grisu-local-job-dir/java_job_new/test.jsdl");
					// jo.addJobStatusChangeListener(jsl);

					final String site = registry.getResourceInformation()
							.getSite(subLoc);
					System.out.println("Site is: " + site);
					// if ("tpac".equals(site.toLowerCase())
					// || "ac3".equals(site.toLowerCase())
					// || site.toLowerCase().contains("rses")) {
					// return;
					// }
					try {
						jo.createJob("/ARCS/NGAdmin");
						jo.submitJob();
					} catch (final Exception e) {
						e.printStackTrace();
						System.err.println("Job to "
								+ jo.getSubmissionLocation() + ": "
								+ e.getLocalizedMessage());
						jo.kill(true);
					}
				}

			};

			executor.execute(subThread);
		}

		executor.shutdown();
		System.out.println("Main thread finished.");

	}

}
