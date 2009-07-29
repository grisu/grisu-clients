package org.vpac.grisu.web.login;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.MyProxyServerParams;
import org.vpac.security.light.control.DirectMyProxyUpload;

/**
 * Servlet implementation class CertLogin
 */
public class CertLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static final Logger myLogger = Logger.getLogger(Login.class.getName());
	
	private static final BouncyCastleCertProcessingFactory factory = BouncyCastleCertProcessingFactory.getDefault();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CertLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("Session id login: "+request.getSession().getId());
		
		String certData = request.getParameter("cert_data");
		String keyData = request.getParameter("key_data");
		
		X509Certificate cert = null;
		try {
			myLogger.debug("Loading certificate...");
			cert = CertUtil.loadCertificate( new ByteArrayInputStream( certData.getBytes() ) );
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
		
		OpenSSLKey key = null;
		try {
			myLogger.debug("Loading key...");
			key = new BouncyCastleOpenSSLKey( new ByteArrayInputStream( keyData.getBytes() ) );
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException(e1);
		} 

		GlobusCredential globusCred = null;
		long lifetimeInSeconds = -1;
		try {
			myLogger.debug("Creating credential...");
			globusCred = factory.createCredential(new X509Certificate[] { cert },
					key.getPrivateKey(), 1024,
					0, GSIConstants.GSI_2_PROXY);
			lifetimeInSeconds = globusCred.getTimeLeft();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}
		
		String username = "grisu-web_"+globusCred.getSubject();
		char[] password = UUID.randomUUID().toString().toCharArray();
		
		myLogger.debug("Uploading proxy...");
		DirectMyProxyUpload.init(globusCred, MyProxyServerParams.DEFAULT_MYPROXY_SERVER, MyProxyServerParams.DEFAULT_MYPROXY_PORT, 
				username, password, null, null, null, null, (int)lifetimeInSeconds);
		
		
		myLogger.debug("Adding username/password to session...");
		request.getSession().setAttribute("username", username);
		request.getSession().setAttribute("password", password);

		String serviceInterfaceUrl = ClientPropertiesManager.getDefaultServiceInterfaceUrl();

		myLogger.info("Logging in...");
		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				serviceInterfaceUrl, username, password, MyProxyServerParams.DEFAULT_MYPROXY_SERVER, new Integer(MyProxyServerParams.DEFAULT_MYPROXY_PORT).toString());

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
			si.login(username, new String(password));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServletException(e.getLocalizedMessage());
		}
		myLogger.info("ServiceInterface created...");
		request.getSession().setAttribute("serviceInterface", si);

		myLogger.info("Logged in...");
		
		
		
		
//        response.sendRedirect("/grisu-web/Application.html");
//        RequestDispatcher rd = request.getRequestDispatcher("http://localhost:8080/grisu-web/Application.html");
        RequestDispatcher rd = request.getRequestDispatcher("/Application.html");
        rd.forward(request, response);
	}

}
