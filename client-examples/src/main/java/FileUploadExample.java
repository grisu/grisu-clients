import java.io.File;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistryManager;

public class FileUploadExample {

	public static void main(String[] args) throws Exception {

		// login
		ServiceInterface si = LoginManager.loginCommandline("BeSTGRID");

		// getting a filemanager object, which encapsulates file related actions
		FileManager fm = GrisuRegistryManager.getDefault(si).getFileManager();

		File fileToUpload = new File("/home/markus/test/700mbFile.bin");
		String targetUrl = "gsiftp://ng2.auckland.ac.nz/home/grid-admin/DC_nz_DC_org_DC_bestgrid_DC_slcs_O_The_University_of_Auckland_CN_Markus_Binsteiner__bK32o4Lh58A3vo9kKBcoKrJ7ZY/700mbFile.bin";

		System.out.println("Uploadin file from:\n\t"
				+ fileToUpload.toURI().toString() + "\nto\n\t" + targetUrl);

		fm.cp(fileToUpload, targetUrl, true);

	}
}
