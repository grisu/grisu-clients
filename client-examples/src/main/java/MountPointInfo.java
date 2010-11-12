import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.UserEnvironmentManager;

public class MountPointInfo {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		final ServiceInterface si = LoginManager.loginCommandline("BeSTGRID");

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
