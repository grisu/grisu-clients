package org.vpac.grisu.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.widgets.Viewport;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application
    implements EntryPoint, LoginHolder
{
	
	private final GwtServiceInterfaceWrapperAsync loginService = (GwtServiceInterfaceWrapperAsync) GWT
	.create(GwtServiceInterfaceWrapper.class);
	
	public static final String APACHE_WEB_ROOT = "/var/www/html";
	public static final String APACHE_SUBFOLDER = "tomcat";
	
	private LoginPanel loginComposite;
	
	private Viewport viewport = new Viewport();
	
  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
	  
		ServiceDefTarget endpoint = (ServiceDefTarget) loginService;

		String moduleRelativeURL = GWT.getModuleBaseURL() + "serviceInterface";

		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		loginService.checkLogin(new AsyncCallback<Boolean>() {
			
			public void onSuccess(Boolean arg0) {
				// login
				setLoading(false);
				
				if ( arg0 ) {
					System.out.println("Logging in...");
					loggedIn();
				} else {
				     RootPanel rootPanel = RootPanel.get();
				     rootPanel.add(getLoginComposite());
				}
			}
			
			public void onFailure(Throwable arg0) {
				// do nothing
				setLoading(false);
			     RootPanel rootPanel = RootPanel.get();
			     rootPanel.add(getLoginComposite());
			}
		});
     
  }
  
  private void setLoading(boolean loading) {
	  
  }
	private LoginPanel getLoginComposite() {
		if (loginComposite == null) {
			loginComposite = new LoginPanel(loginService, this);
		}
		return loginComposite;
	}
	public void loggedIn() {

		new Viewport(new MainPanel(loginService));
		
	}
}
