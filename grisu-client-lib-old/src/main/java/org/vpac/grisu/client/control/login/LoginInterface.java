

package org.vpac.grisu.client.control.login;

import org.vpac.grisu.control.ServiceInterface;

public interface LoginInterface {
	
	public boolean userCancelledLogin();
	public ServiceInterface getServiceInterface();
	public void setUserCancelledLogin(boolean cancelled);
	public void setServiceInterface(ServiceInterface serviceInterface);

	public void saveCurrentConnectionsSettingsAsDefault();
}
