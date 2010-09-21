package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.DtoActionStatus;
import org.vpac.grisu.model.dto.DtoStringList;

public class FileCopyTest {

	public static void main(final String[] args) throws Exception {

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
		final FileManager fileManager = registry.getFileManager();

		final String target = "gsiftp://ng2.vpac.org/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner";
		// DtoFolder folder =
		// si.ls("gsiftp://ng2.vpac.org/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner",
		// 0);

		final String[] sources = new String[] {
				// "gsiftp://ng2.canterbury.ac.nz/home/grid-cloud/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/grisu-multijob-dir/longJob",
				"gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/simpleTestTarget.txt0",
				"gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/simpleTestTarget.txt1",
				"gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/simpleTestTarget.txt2",
				"gsiftp://ng2.canterbury.ac.nz/home/grid-admin/C_AU_O_APACGrid_OU_VPAC_CN_Markus_Binsteiner/simpleTestTarget.txt3" };

		final String handle = si.cp(DtoStringList.fromStringArray(sources),
				target, true, false);
		DtoActionStatus status;
		do {
			status = si.getActionStatus(handle);

			for (int i = 0; i < status.getLog().size(); i++) {
				System.out.println(status.getLog().get(i).getLogMessage());
			}
			// System.out.println("Elements after: "+status.getLog().size());
			Thread.sleep(1000);
			// System.out.println("\n\n\n");

		} while (!status.isFinished());

	}

}
