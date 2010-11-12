import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.X;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.model.dto.GridFile;

public class FileListingExample {

	public static void main(String[] args) throws Exception {

		// login
		ServiceInterface si = LoginManager.loginCommandline("LOCAL");
		FileManager fm = GrisuRegistryManager.getDefault(si).getFileManager();
		UserEnvironmentManager uem = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();

		String input = null;
		while (!"exit".equals(input)) {

			System.out.println("Available groups:");
			for (String g : uem.getAllAvailableUniqueGroupnames(true)) {
				System.out.println(g);
			}
			System.out.println();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));

			try {
				input = br.readLine();
				if (StringUtils.isBlank(input)) {
					input = "grid://groups/NGAdmin";
				} else if ("exit".equals(input)) {
					System.exit(0);
				}
			} catch (IOException ioe) {
				System.out.println("IO error trying to read user input!");
				System.exit(1);
			}

			// do the filelisting
			try {
				GridFile f = fm.ls(input);
				for (GridFile c : f.getChildren()) {
					X.p("Child: " + c.getName() + " (" + c.getPath() + ")");
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
			}

		}

	}
}
