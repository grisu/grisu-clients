import java.util.Set;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;

public class ApplicationInfo {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		final ServiceInterface si = LoginManager.loginCommandline("BeSTGRID-DEV");

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		// getting all applications on the grid
		Set<String> apps = registry.getResourceInformation()
				.getAllApplications();

		for (final String app : apps) {

			// for every application, print out some details
			System.out.println("Application: " + app);
			final ApplicationInformation info = registry
					.getApplicationInformation(app);

			for (final String subLoc : info
					.getAvailableAllSubmissionLocations()) {

				System.out.println("\tSubmission location: " + subLoc);
				System.out.println("\t Versions:");
				for (final String version : info.getAvailableVersions(subLoc)) {
					System.out.println("\t\t" + version);
					System.out.println("\t\t\tDetails:");
					for (final String key : info.getApplicationDetails(subLoc,
							version).keySet()) {
						System.out.println("\t\t\t\t"
								+ key
								+ ":\t"
								+ info.getApplicationDetails(subLoc, version)
										.get(key));
					}

				}
				System.out.println();

			}
			System.out.println();
			System.out.println();

		}

	}

}
