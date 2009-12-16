package org.vpac.grisu.client.model.login;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;

/**
 * Holds the login panels.
 * 
 * @author Markus Binsteiner
 * 
 */
public interface LoginPanelsHolder {

	public void cancelled();

	public LoginParams getLoginParams();

	// public void setServiceInterface(ServiceInterface serviceInterface);

	public ServiceInterface getServiceInterface();

	public void loggedIn(ServiceInterface serviceInterface);

}
