package org.vpac.grisu.frontend.examples;

import java.util.Iterator;
import java.util.Set;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class TestSubmit {

	public static void main(final String[] args) throws Exception {

		final ServiceInterface si = LoginManager.loginCommandline("Local");

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		String subLoc = null;
		Set<String> subLocs = registry.getApplicationInformation(
				"ESyS-Particle")
				.getAvailableSubmissionLocationsForVersionAndFqan("2.0",
						"/ARCS/AuScope");
		Iterator<String> it = subLocs.iterator();
		while (it.hasNext()) {
			String sl = it.next();
			if ("ESSCC".equals(registry.getResourceInformation().getSite(sl))
					&& "workq".equals(SubmissionLocationHelpers
							.extractQueue(sl))) {
				subLoc = sl;
				break;
			}
		}
		if (subLoc == null) {
			throw new Exception("Could not get submission location");
		}

		JobObject jo = new JobObject(si);
		jo.setCpus(2);
		jo.setMemory(1048576L);
		jo.setWalltimeInSeconds(60);
		jo.setSubmissionLocation(subLoc);
		jo.setCommandline("mpipython test.py");
		// jo.setApplication("ESyS-Particle");
		// jo.setApplicationVersion("2.0");
		jo.setApplication("escript");
		jo.setApplicationVersion("3.1");
		jo.setTimestampJobname("essccTest");

		try {
			jo.createJob("/ARCS/AuScope");
			jo.submitJob();
			System.out.println("Job submitted to " + jo.getSubmissionLocation()
					+ ", Dir: " + jo.getJobDirectoryUrl());

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Job to " + jo.getSubmissionLocation() + ": "
					+ e.getLocalizedMessage());
			jo.kill(true);
		}
	}
}
