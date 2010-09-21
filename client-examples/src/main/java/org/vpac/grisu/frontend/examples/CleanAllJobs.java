package org.vpac.grisu.frontend.examples;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.model.dto.DtoStringList;

public final class CleanAllJobs {

	public static void main(final String[] args)
			throws ServiceInterfaceException, LoginException,
			NoSuchJobException {

		final ServiceInterface si = LoginManager.loginCommandline("Local");

		final DtoStringList allJobnames = si.getAllJobnames(null);

		si.killJobs(allJobnames, true);

	}

	private CleanAllJobs() {
	}

}
