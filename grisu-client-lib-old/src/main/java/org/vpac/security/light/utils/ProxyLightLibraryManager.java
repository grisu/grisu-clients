package org.vpac.security.light.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This class uses reflection to try to use some methods of the proxy light
 * library.
 * 
 * @author Markus Binsteiner
 */
public class ProxyLightLibraryManager {

	/**
	 * Creates a JDialog that asks the user to upload a proxy to the specified
	 * MyProxy server.
	 * 
	 * @param myproxyServer
	 *            the hostname of the MyProxy server
	 * @param myproxyPort
	 *            the port of the MyProxy server
	 * @param lifetime_in_seconds
	 *            the lifetime of the proxy or -1 if the user should be able to
	 *            specify the lifetime
	 * @param allowed_retrievers
	 *            the allowed retrievers (or null for anonymous access)
	 * @param allowed_renewers
	 *            the allowed renewers (or null for anonymous access)
	 * @return a map with username / password information about the uploaded
	 *         proxy or null if no proxy was uploaded
	 */
	public static Map<String, char[]> createMyProxyInitDialog(
			String myproxyServer, int myproxyPort, int lifetime_in_seconds,
			String allowed_retrievers, String allowed_renewers) {
		Class dialogClass = null;
		try {
			dialogClass = Class
					.forName("org.vpac.security.light.view.swing.MyProxyInitDialog");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}

		Map<String, char[]> result = new HashMap<String, char[]>();
		try {
			Constructor dialogConstructor = dialogClass
					.getConstructor(new Class[] { String.class, int.class,
							int.class, String.class, String.class });

			MyProxyLoginInformationHolder myProxyInitDialog = null;
			myProxyInitDialog = (MyProxyLoginInformationHolder) dialogConstructor
					.newInstance(new Object[] { myproxyServer, myproxyPort,
							lifetime_in_seconds, allowed_retrievers,
							allowed_renewers });

			if (!myProxyInitDialog.proxyCreated()) {
				return null;
			}
			result.put(myProxyInitDialog.getUsername(), myProxyInitDialog
					.getPassword());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return result;
	}

	public static boolean prerequisitesForProxyCreationAvailable() {

		Class directMyProxyUploadClass = null;
		try {
			directMyProxyUploadClass = Class
					.forName("org.vpac.security.light.certificate.CertificateHelper");
		} catch (Exception e1) {
			System.err
					.println("Couldn't find proxy light library. Won't be able to offer grid-proxy-init functionality: "
							+ e1.getLocalizedMessage());
			return false;
		}
		// System.out.println("Found proxy light library. Enabling it.");
		Class bouncyCastleProviderClass = null;
		try {
			bouncyCastleProviderClass = Class
					.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
		} catch (ClassNotFoundException e2) {
			System.err
					.println("Couldn't find bouncy castle library. Won't be able to offer grid-proxy-init functionality: "
							+ e2.getLocalizedMessage());
			return false;
		}

		boolean credsReady = false;

		try {
			Method globusCredsReadyMethod = directMyProxyUploadClass.getMethod(
					"globusCredentialsReady", new Class[] {});
			credsReady = (Boolean) globusCredsReadyMethod.invoke(null,
					new Object[] {});
		} catch (Exception e) {
			System.err
					.println("Couldn't find credentials. Won't be able to offer grid-proxy-init functionality: "
							+ e.getLocalizedMessage());
			return false;
		}
		// System.out.println("Found credentials. Enabling grid-proxy-init functionality.");

		return credsReady;

	}

	public static boolean proxyLightLibraryAvailable() {

		Class directMyProxyUploadClass = null;
		try {
			directMyProxyUploadClass = Class
					.forName("org.vpac.security.light.control.DirectMyProxyUpload");
		} catch (ClassNotFoundException e1) {
			System.err
					.println("Couldn't find proxy light library. Won't be able to offer grid-proxy-init functionality: "
							+ e1.getLocalizedMessage());
			return false;
		}

		Class bouncyCastleProviderClass = null;
		try {
			bouncyCastleProviderClass = Class
					.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
		} catch (ClassNotFoundException e2) {
			System.err
					.println("Couldn't find bouncy castle library. Won't be able to offer grid-proxy-init functionality: "
							+ e2.getLocalizedMessage());
			return false;
		}

		// System.out.println("Found proxy light library. Enabling it.");
		return true;
	}

	// public static void main(String[] args) {
	//		
	// org.vpac.securiWty.light.Init.initBouncyCastle();
	//		
	// }

}
