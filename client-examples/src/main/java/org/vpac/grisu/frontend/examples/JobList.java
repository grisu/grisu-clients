package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;

public final class JobList {

	public static void main(final String[] args)
			throws ServiceInterfaceException, LoginException,
			NoSuchJobException {

		ServiceInterface si = LoginManager.loginCommandline("ARCS");

		DtoJobs test = si.ps(null, true);

		System.out.println("ps");
		for (DtoJob job : test.getAllJobs()) {
			System.out.println(job.jobname());
		}

		System.out.println("alljobnames");
		for (String name : si.getAllJobnames(null).asArray()) {
			System.out.println(name);
		}

		System.out.println("all multipartjobnames");
		for (String name : si.getAllBatchJobnames(null).asArray()) {
			System.out.println(name);
		}

		for (int i = 0; i < 20; i++) {
			System.out.println("Rechecking...");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			si.ps(null, true);
		}
	}

	private JobList() {
	}

}
