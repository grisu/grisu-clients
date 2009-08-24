package org.vpac.grisu.clients.blender;

import java.util.Set;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;

import au.org.arcs.jcommons.interfaces.GridResource;

public class Blender_submit {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
//				"http://localhost:8080/xfire-backend/services/grisu",
//				"https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
//				 "https://ngportal.vpac.org/grisu-ws/services/grisu",
				 "Local",
//				"Dummy",
				username, password);

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory
					.createInterface(loginParams);
		} catch (ServiceInterfaceException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		
		GrisuBlenderJob blenderJob;
		try {
			blenderJob = new GrisuBlenderJob(si, "blenderJob1", "/ARCS/NGAdmin");
		} catch (Exception e) {
			si.deleteMultiPartJob("blenderJob1", true);
			blenderJob = new GrisuBlenderJob(si, "blenderJob1", "/ARCS/NGAdmin");
		}
		
		
		blenderJob.addInputFile("/home/markus/Desktop/CubesTest.blend");
		
		for ( int i=1; i<11; i++ ) {
			blenderJob.addJob("blender -b "+GrisuBlenderJob.INPUT_PATH_VARIABLE+"/CubesTest.blend -F PNG -o cubes_ -f "+i, 3600);
		}
		
		blenderJob.createAndSubmitBlenderJob();
		
		System.out.println("Blender job submission finished.");
		
	}

}
