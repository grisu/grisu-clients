package org.vpac.grisu.frontend.examples;

import java.io.File;
import java.util.Date;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class UploadLocalFolder {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager
				.loginCommandline(LoginManager.SERVICEALIASES.get("LOCAL"));

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		FileManager fm = registry.getFileManager();

		fm.deleteFile("gsiftp://ng2.vpac.org/home/acc004/test/");

		Date start = new Date();

		fm.cp(new File("/home/markus/Workspaces/Wicket"),
				"gsiftp://ng2.vpac.org/home/acc004/test/", true);

		Date end = new Date();

		System.out.println("Time: " + (end.getTime() - start.getTime()));
	}

}
