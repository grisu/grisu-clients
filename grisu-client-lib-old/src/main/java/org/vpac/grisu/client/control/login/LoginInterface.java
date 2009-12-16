package org.vpac.grisu.client.control.login;

import org.vpac.grisu.control.ServiceInterface;

public interface LoginInterface {

	public ServiceInterface getServiceInterface();

	public void saveCurrentConnectionsSettingsAsDefault();

	public void setServiceInterface(ServiceInterface serviceInterface);

	public void setUserCancelledLogin(boolean cancelled);

	public boolean userCancelledLogin();
}
