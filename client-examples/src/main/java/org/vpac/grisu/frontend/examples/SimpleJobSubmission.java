package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class SimpleJobSubmission {

	public static void main(String[] args) throws Exception {

		// ServiceInterface si = LoginManager.loginCommandline();
		ServiceInterface si = LoginManager
				.loginCommandline(LoginManager.SERVICEALIASES.get("LOCAL"));

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		for (String subLoc : registry.getResourceInformation()
				.getAllSubmissionLocations()) {
			System.out.println(subLoc);
		}

		// JobListDialog.open(si, null);

		JobObject job = new JobObject(si);
		job.setApplication("java");
		job.setApplicationVersion("1.6.0-06");
		job.setUniqueJobname("java_job2_sfasfsafsadfsadffawefawefwfsdfsdfsdafsdfsdafsdfsd");
		job.setCommandline("java -version");

		job.setWalltimeInSeconds(60);

		job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/text0.txt");
		job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/text1.txt");
		job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/text2.txt");
		job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/text3.txt");
		job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/text4.txt");
		job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/text5.txt");

		job.createJob("/ARCS/NGAdmin");
		job.submitJob();

		System.out.println("Main thread finished.");
	}

}
