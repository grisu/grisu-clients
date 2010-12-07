import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;

import au.org.arcs.jcommons.utils.HttpProxyManager;

public class MountPointInfo {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		HttpProxyManager.setHttpProxy("127.0.0.1", 3128, "", "".toCharArray());

		ServiceInterface si = LoginManager.loginCommandline("BeSTGRID-DEV");

		// final ServiceInterface si = LoginManager.shiblogin("mbin029",
		// "m2ell;;2".toCharArray(), "The University of Auckland",
		// "BeSTGRID-DEV", false);
		// .loginCommandline("BeSTGRID-DEV");

		final GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		// getting all mountpoints for the user
		UserEnvironmentManager uem = registry.getUserEnvironmentManager();

		System.out.println("All available mountpoints:\n");
		for (MountPoint mp : uem.getMountPoints()) {
			System.out.println("\t" + mp.getAlias() + "\t" + mp.getFqan()
					+ "\t\t" + mp.getRootUrl());
		}

	}
}
