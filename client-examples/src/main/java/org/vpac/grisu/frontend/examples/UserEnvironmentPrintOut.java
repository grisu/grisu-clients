package org.vpac.grisu.frontend.examples;

import java.util.Date;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.files.FileSystemItem;
import org.vpac.grisu.model.info.ApplicationInformation;

public class UserEnvironmentPrintOut {

	public static void main(final String[] args) throws Exception {

		Date start = new Date();

		final ServiceInterface si = LoginManager.loginCommandline();

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);
		final UserEnvironmentManager uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();

		for (FileSystemItem item : uem.getFileSystems()) {
			System.out.println(item.getAlias());
			System.out.println(item.getRootFile().getUrl());
			System.out.println(item.getType());
		}

		ApplicationInformation info = registry
				.getApplicationInformation("gold");

		for (String subLoc : info.getAvailableAllSubmissionLocations()) {
			System.out.println(subLoc);
		}

		System.out.println("XXXXX");
		for (String subLoc : info
				.getAvailableSubmissionLocationsForFqan("/ARCS/BeSTGRID/Drug_discovery/Local")) {
			System.out.println(subLoc);
		}

		System.out.println("Started: " + start);
		System.out.println("Finished: " + new Date().toString());

	}
}
