

package org.vpac.grisu.client.control.login;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.globus.gsi.GlobusCredential;
import org.ietf.jgss.GSSCredential;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoValidCredentialException;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.MyProxyServerParams;

/**
 * Some easy-to-use methods to login to a Grisu web service.
 * 
 * @author Markus Binsteiner
 *
 */
public class LoginHelpers {
	
	static final Logger myLogger = Logger.getLogger(LoginHelpers.class
			.getName());
	
	/**
	 * Use this if you want to login to the Grisu web service.
	 * 
	 * @param loginParams the details about the Grisu web service and connection properties. 
	 * @return the ServiceInterface
	 * @throws LoginException 
	 * @throws ServiceInterfaceException 
	 */
	public static ServiceInterface login(LoginParams loginParams) throws LoginException, ServiceInterfaceException {
		
		ServiceInterface si = ServiceInterfaceFactoryOld.createInterface(loginParams.getServiceInterfaceUrl(), loginParams.getMyProxyUsername(), loginParams.getMyProxyPassphrase(), loginParams.getMyProxyServer(), loginParams.getMyProxyPort(), loginParams.getHttpProxy(), loginParams.getHttpProxyPort(), loginParams.getHttpProxyUsername(), loginParams.getHttpProxyPassphrase());
		try {
			si.login(loginParams.getMyProxyUsername(), new String(loginParams.getMyProxyPassphrase()));
		} catch (NoValidCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LoginException("Could not create & upload proxy to the myproxy server. Probably because of a wrong private key passphrase or network problems.");
		} catch (Exception e1) {
			throw new LoginException("Could not login. Unspecified error: "+e1.getLocalizedMessage());
		}
		loginParams.clearPasswords();
		
		return si;
	}
	
	public static ServiceInterface login(LoginParams loginParams, GlobusCredential proxy) throws LoginException, ServiceInterfaceException {
		ServiceInterface serviceInterface = null;
		
		Class directMyProxyUploadClass = null;
		try {
			directMyProxyUploadClass = Class.forName("org.vpac.security.light.control.DirectMyProxyUpload");
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException("Proxy_light library not in path. Can't create proxy.");
		}

		try {
			Method myProxyUploadMethod = directMyProxyUploadClass.getMethod("init", 
				new Class[]{GlobusCredential.class, String.class, int.class, String.class, char[].class, String.class, String.class, String.class, String.class, int.class}
		);
		
		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		int myProxyPort = MyProxyServerParams.getMyProxyPort();
		
		try {
			myProxyServer = InetAddress.getByName(myProxyServer).getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		myLogger.debug("Contacting myProxyServer: "+myProxyServer);
		
		Map<String, char[]> myproxyDetails = null;
		myproxyDetails = (Map<String, char[]>)myProxyUploadMethod.invoke(null, 
				new Object[]{ proxy, myProxyServer, myProxyPort, loginParams.getMyProxyUsername(), loginParams.getMyProxyPassphrase(), null, null, null, null, 3600*24*4 }
			); 

		
		String myproxyusername = myproxyDetails.keySet().iterator().next();
		loginParams.setMyProxyUsername(myproxyusername);
		loginParams.setMyProxyPassphrase(myproxyDetails.get(myproxyusername));
		loginParams.setMyProxyServer(myProxyServer);
		loginParams.setMyProxyPort(new Integer(myProxyPort).toString());

		serviceInterface = login(loginParams);
		serviceInterface.login(myproxyusername, new String(myproxyDetails.get(myproxyusername)));
	} catch (InvocationTargetException re) {
		re.printStackTrace();
		throw new LoginException("Could not create & upload proxy to the myproxy server. Probably because of a wrong private key passphrase or network problems.");
	} catch (ServiceInterfaceException e) {
		throw e;
	} catch (Exception e) {
		e.printStackTrace();
		throw new LoginException("Can't login to web service: "+e.getMessage());
	}
	
	return serviceInterface;
	}
	
	/**
	 * Logs in using a GSSCredential and the loginParams. Uses the dn as MyProxy username & a random password if not specified.
	 * @param cred the credential
	 * @param loginParams the login parameters
	 * @return the serviceInterface
	 * @throws LoginException if somethings gone wrong (i.e. wrong private key passphrase)
	 * @throws ServiceInterfaceException 
	 */
	public static ServiceInterface login(LoginParams loginParams, GSSCredential cred) throws LoginException, ServiceInterfaceException {
		ServiceInterface serviceInterface = null;
		
		Class directMyProxyUploadClass = null;
		try {
			directMyProxyUploadClass = Class.forName("org.vpac.security.light.control.DirectMyProxyUpload");
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException("Proxy_light library not in path. Can't create proxy.");
		}

		try {
			Method myProxyUploadMethod = directMyProxyUploadClass.getMethod("init", 
				new Class[]{GSSCredential.class, String.class, int.class, String.class, char[].class, String.class, String.class, String.class, String.class, int.class}
		);
		
		String myProxyServer = MyProxyServerParams.getMyProxyServer();
		int myProxyPort = MyProxyServerParams.getMyProxyPort();
		
		try {
			myProxyServer = InetAddress.getByName(myProxyServer).getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		myLogger.debug("Contacting myProxyServer: "+myProxyServer);
		
		Map<String, char[]> myproxyDetails = null;
		myproxyDetails = (Map<String, char[]>)myProxyUploadMethod.invoke(null, 
				new Object[]{ cred, myProxyServer, myProxyPort, loginParams.getMyProxyUsername(), loginParams.getMyProxyPassphrase(), null, null, null, null, 3600*24*4 }
			); 

		
		String myproxyusername = myproxyDetails.keySet().iterator().next();
		loginParams.setMyProxyUsername(myproxyusername);
		loginParams.setMyProxyPassphrase(myproxyDetails.get(myproxyusername));
		loginParams.setMyProxyServer(myProxyServer);
		loginParams.setMyProxyPort(new Integer(myProxyPort).toString());

		serviceInterface = login(loginParams);
		serviceInterface.login(myproxyusername, new String(myproxyDetails.get(myproxyusername)));
	} catch (InvocationTargetException re) {
		re.printStackTrace();
		throw new LoginException("Could not create & upload proxy to the myproxy server. Probably because of a wrong private key passphrase or network problems.");
	} catch (ServiceInterfaceException e) {
		throw e;
	} catch (Exception e) {
		e.printStackTrace();
		throw new LoginException("Can't login to web service: "+e.getMessage());
	}
	
	return serviceInterface;
	}

	/**
	 * Use this if you want to upload a proxy to a MyProxy server and login to a grisu web service in one step.
	 * 
	 * @param privateKeyPassphrase the passphrase of the private key or null if there is already a local proxy on the default location
	 * @param loginParams the details about the grisu web service and connection properties. If the MyProxy username is null the dn of the user is used as username. If the MyProxy password is null a random one is selected.
	 * @return the ServiceInterface
	 * @throws LoginException if somethings gone wrong (i.e. wrong private key passphrase)
	 * @throws ServiceInterfaceException 
	 */
	public static ServiceInterface login(char[] privateKeyPassphrase, LoginParams loginParams) throws LoginException, ServiceInterfaceException {
			
		ServiceInterface serviceInterface = null;
		
			Class directMyProxyUploadClass = null;
			try {
				directMyProxyUploadClass = Class.forName("org.vpac.security.light.control.DirectMyProxyUpload");
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException("Proxy_light library not in path. Can't create proxy.");
			}

			try {
				Method myProxyUploadMethod = directMyProxyUploadClass.getMethod("init", 
					new Class[]{char[].class, String.class, int.class, String.class, char[].class, String.class, String.class, String.class, String.class, int.class}
			);
			
			String myProxyServer = MyProxyServerParams.getMyProxyServer();
			int myProxyPort = MyProxyServerParams.getMyProxyPort();
			
			try {
				myProxyServer = InetAddress.getByName(myProxyServer).getHostAddress();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			myLogger.debug("Contacting myProxyServer: "+myProxyServer);
			
			Map<String, char[]> myproxyDetails = null;
			myproxyDetails = (Map<String, char[]>)myProxyUploadMethod.invoke(null, 
				new Object[]{ privateKeyPassphrase, myProxyServer, myProxyPort, loginParams.getMyProxyUsername(), loginParams.getMyProxyPassphrase(), null, null, null, null, 3600*24*4 }
				);

			
			String myproxyusername = myproxyDetails.keySet().iterator().next();
			loginParams.setMyProxyUsername(myproxyusername);
			loginParams.setMyProxyPassphrase(myproxyDetails.get(myproxyusername));
			loginParams.setMyProxyServer(myProxyServer);
			loginParams.setMyProxyPort(new Integer(myProxyPort).toString());

			serviceInterface = login(loginParams);
			serviceInterface.login(myproxyusername, new String(myproxyDetails.get(myproxyusername)));
		} catch (InvocationTargetException re) {
			Throwable t = re.getCause();
//			re.printStackTrace();
			throw new LoginException(t.getLocalizedMessage());
		} catch (ServiceInterfaceException e) {
			throw e;
		} catch (Exception e) {
//			e.printStackTrace();
			throw new LoginException("Can't login to web service: "+e.getMessage());
		}
		
		return serviceInterface;
	}

}
