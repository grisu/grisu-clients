import java.util.Date;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.dto.GridFile;
import org.vpac.grisu.model.status.StatusObject;

public class FileCopyExample {

	public static void main(String[] args) throws Exception {

		// login
		ServiceInterface si = LoginManager.loginCommandline("Local");

		// getting a filemanager object, which encapsulates file related actions
		FileManager fm = GrisuRegistryManager.getDefault(si).getFileManager();

		// need to know the full url to the source file

		GridFile source = fm
				.createGridFile("gsiftp://ng2.auckland.ac.nz/home/grid-admin/DC_nz_DC_org_DC_bestgrid_DC_slcs_O_The_University_of_Auckland_CN_Markus_Binsteiner__bK32o4Lh58A3vo9kKBcoKrJ7ZY/700mbFile.bin");
		// target file can be specified as either path or url
		GridFile target = fm
				.createGridFile("gsiftp://ng2.auckland.ac.nz/home/grid-workshop/DC_nz_DC_org_DC_bestgrid_DC_slcs_O_The_University_of_Auckland_CN_Markus_Binsteiner__bK32o4Lh58A3vo9kKBcoKrJ7ZY");

		System.out.println("Downloading file from:\n\t" + source.getName()
				+ "\nto\n\t" + target.getName());

		// or just use the urls directly
		fm.cp(source, target, true);

		// StatusObject.wait(si, target.getUrl());
		StatusObject.waitForActionToFinish(si, target.getUrl(), 4, true, true);

		System.out.println("Transfer finished.");

		GridFile file = fm.ls(target.getUrl() + "/" + source.getName());

		System.out.println("Filesize: " + file.getSize());
		System.out.println("Last modified: "
				+ new Date(file.getLastModified()).toGMTString());

	}
}
