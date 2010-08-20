package org.vpac.grisu.frontend.examples;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.FileManager;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;

public class Folderdownload {

	public static void main(String[] args) throws Exception {

		File target = new File("/home/markus/Desktop/temp");
		FileUtils.deleteDirectory(target);

		// ServiceInterface si = LoginManager.loginCommandline();
		ServiceInterface si = LoginManager
				.loginCommandline(LoginManager.SERVICEALIASES.get("LOCAL"));

		GrisuRegistry registry = GrisuRegistryManager.getDefault(si);

		FileManager fm = registry.getFileManager();

		fm.downloadUrl("gsiftp://ng2.vpac.org/home/acc004/us", target.toURI()
				.toString(), true);

		System.out.println("Main thread finished.");
	}

}
