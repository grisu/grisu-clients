package org.vpac.grisu.clients.blender;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;

public class Blender_check {

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
				"https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
//				"http://localhost:8080/enunciate-backend/soap/GrisuService",
//				 "Local",
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
		
		GrisuBlenderJob blenderJob = null;
		try {
			blenderJob = new GrisuBlenderJob(si, "blenderJob1");
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
		
		System.out.println(blenderJob.getProgress());
		
		blenderJob.downloadResult();
		
	}

}
