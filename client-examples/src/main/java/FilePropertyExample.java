import java.util.Date;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;

public class FilePropertyExample {

	public static void main(String[] args) throws Exception {

		// login
		ServiceInterface si = LoginManager.loginCommandline("Local");

		// getting a filemanager object, which encapsulates file related actions
		FileManager fm = GrisuRegistryManager.getDefault(si).getFileManager();

		// need to know the full url to the source file

		GridFile source = fm
				.createGridFile("gsiftp://ng2.auckland.ac.nz/home/grid-workshop/DC_nz_DC_org_DC_bestgrid_DC_slcs_O_The_University_of_Auckland_CN_Markus_Binsteiner__bK32o4Lh58A3vo9kKBcoKrJ7ZY/700mbFile.bin");

		System.out.println("Filesize: " + source.getSize());
		System.out.println("Last modified: "
				+ new Date(source.getLastModified()).toGMTString());

	}
}
