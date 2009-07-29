package org.vpac.grisu.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

public class LoginPanel extends Composite {
	private Panel panel;
	private FormPanel formPanel;
	private TextField usernameField;
	private TextField passwordField;
	private Button loginButton;
	private LoginHolder loginHolder;
	
	private final GwtServiceInterfaceWrapperAsync service;


	public LoginPanel(GwtServiceInterfaceWrapperAsync service, LoginHolder loginHolder) {

		this.service = service;
		this.loginHolder = loginHolder;
		


		initWidget(getFormPanel());
	}
	
	private void setLoggingInStatus(boolean loggingIn) {
		if ( loggingIn ) { 
			getLoginButton().disable();
			getUsernameField().disable();
			getPasswordField().disable();
//			MessageBox.show(new MessageBoxConfig() {  
//			                     {  
//			                         setMsg("Logging in, please wait...");  
//			                         setProgressText("Connecting...");  
//			                         setWidth(300);  
//			                         setWait(true);  
//			                         setWaitConfig(new WaitConfig() {  
//			                             {  
//			                                 setInterval(200);  
//			                             }  
//			                         });  
//			                         setAnimEl(button.getId());  
//			} 
		} else {
			getLoginButton().enable();
			getUsernameField().enable();
			getPasswordField().enable();
		}
	}

	private FormPanel getFormPanel() {
		if (formPanel == null) {
			formPanel = new FormPanel();
			formPanel.setId("loginForm");
			formPanel.setWidth(350);
			formPanel.setLabelWidth(75);
			formPanel.add(getUsernameField());
			formPanel.add(getPasswordField());
			getLoginButton().addListener(new ButtonListenerAdapter() {
				@Override
				public void onClick(Button button, EventObject e) {
					
					Map loginData = getUserData( formPanel.getForm() );
					setLoggingInStatus(true);
					service.login(loginData, new AsyncCallback<Void>(){

						public void onFailure(Throwable arg0) {
							setLoggingInStatus(false);
							if ( arg0 instanceof LoginException ) {
								arg0.printStackTrace();
					            MessageBox.alert( "Login Error", arg0.getLocalizedMessage() );
							} else {
								arg0.printStackTrace();
					            MessageBox.alert( "Unknown Error", arg0.getLocalizedMessage() );
							}
							

						}

						public void onSuccess(Void result) {
				            // take the result coming from the server
							setLoggingInStatus(false);
				               loginHolder.loggedIn();
						}
						
					});
					
				}
			});
			formPanel.add(getLoginButton());
			formPanel.setPaddings(10);
			formPanel.setBorder(false);
		}
		return formPanel;
	}

	private TextField getUsernameField() {
		if (usernameField == null) {
			usernameField = new TextField("Username", "username", 230);
			usernameField.setAllowBlank(false);
			usernameField.focus();
		}
		return usernameField;
	}

	private TextField getPasswordField() {
		if (passwordField == null) {
			passwordField = new TextField("Password", "password", 230);
			passwordField.setAllowBlank(false);
			passwordField.setInputType("password");
		}
		return passwordField;
	}

	private Button getLoginButton() {
		if (loginButton == null) {
			loginButton = new Button("Login");
			loginButton.setId("loginButton");
		}
		return loginButton;
	}
	
   private Map getUserData( Form form )
   {
	      String formValues = form.getValues();

	      Map loginData = new HashMap();

	      String[] nameValuePairs = formValues.split( "&" );

	      for (int i = 0; i < nameValuePairs.length; i++) {

	         String[] oneItem = nameValuePairs[i].split( "=" );

	         loginData.put( oneItem[0], oneItem[1] );

	      }

	      return loginData;

	   }

}
