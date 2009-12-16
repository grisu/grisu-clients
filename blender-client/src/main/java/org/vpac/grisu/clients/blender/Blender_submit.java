package org.vpac.grisu.clients.blender;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;

public class Blender_submit {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String username = args[0];
		char[] password = args[1].toCharArray();

		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/xfire-backend/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
				// "https://ngportal.vpac.org/grisu-ws/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
				// "http://localhost:8080/enunciate-backend/soap/GrisuService",
				"Local",
				// "Dummy",
				username, password);

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
		} catch (ServiceInterfaceException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

		si.kill("grisutest3", true);
		System.exit(1);

		GrisuBlenderJob blenderJob;
		try {
			blenderJob = new GrisuBlenderJob(si, "blenderJob1", "/ARCS/NGAdmin");
		} catch (Exception e) {
			si.kill("blenderJob1", true);
			blenderJob = new GrisuBlenderJob(si, "blenderJob1", "/ARCS/NGAdmin");
		}

		// int walltime = 3600;
		//		
		// for ( int i=1; i<=10; i++ ) {
		// int w = walltime;
		//			
		// if ( i > 70 ) {
		// w = walltime * 2;
		// }
		// blenderJob.addJob("blender -b "+GrisuBlenderJob.INPUT_PATH_VARIABLE+"/CubesTest.blend -F PNG -o cubes_ -f "+i,
		// w);
		// }

		blenderJob.setBlenderFile("/home/markus/Desktop/CubesTest.blend", null);
		blenderJob.setFirstFrame(1);
		blenderJob.setLastFrame(150);
		blenderJob.setDefaultWalltimeInSeconds(3600);
		// blenderJob.setSitesToExclude(new String[]{"vpac", "ersa"});
		// blenderJob.setSitesToInclude(new String[]{"anter"});

		blenderJob.createAndSubmitJobs(true);

		System.out.println("Blender job submission finished.");

	}

}
