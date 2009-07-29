package org.vpac.grisu.web.login;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.settings.ClientPropertiesManager;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	static final Logger myLogger = Logger
	.getLogger(Login.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Session id login: "+request.getSession().getId());
		
		String username = request.getParameter("username");
		char[] password = request.getParameter("password").toCharArray(); 
		
		request.getSession().setAttribute("username", username);
		request.getSession().setAttribute("password", password);


		myLogger.info("Logging in...");
		
		String serviceInterfaceUrl = ClientPropertiesManager.getDefaultServiceInterfaceUrl();
		
		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				serviceInterfaceUrl, username, password, "myproxy2.arcs.org.au", "7512");

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
