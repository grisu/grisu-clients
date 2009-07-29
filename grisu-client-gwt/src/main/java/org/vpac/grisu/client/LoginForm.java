package org.vpac.grisu.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class LoginForm extends Composite {
	private FormPanel formPanel;
	private VerticalPanel verticalPanel;
	private Label ulabel;
	private TextBox textBox;
	private Label plabel;
	private PasswordTextBox passwordTextBox;
	private Button button;

	public LoginForm() {
		initWidget(getFormPanel());
	}
	private FormPanel getFormPanel() {
		if (formPanel == null) {
			formPanel = new FormPanel();
			formPanel.setWidget(getVerticalPanel());
			formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
				
				public void onSubmitComplete(SubmitCompleteEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		return formPanel;
	}
	private VerticalPanel getVerticalPanel() {
		if (verticalPanel == null) {
			verticalPanel = new VerticalPanel();
			verticalPanel.setSize("100%", "100%");
			verticalPanel.add(getUlabel());
			verticalPanel.add(getTextBox());
			verticalPanel.add(getPlabel());
			verticalPanel.add(getPasswordTextBox());
			verticalPanel.add(getButton());
		}
		return verticalPanel;
	}
	private Label getUlabel() {
		if (ulabel == null) {
			ulabel = new Label("Username");
		}
		return ulabel;
	}
	private TextBox getTextBox() {
		if (textBox == null) {
			textBox = new TextBox();
		}
		return textBox;
	}
	private Label getPlabel() {
		if (plabel == null) {
			plabel = new Label("Password");
		}
		return plabel;
	}
	private PasswordTextBox getPasswordTextBox() {
		if (passwordTextBox == null) {
			passwordTextBox = new PasswordTextBox();
		}
		return passwordTextBox;
	}
	private Button getButton() {
		if (button == null) {
			button = new Button("New button");
			button.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					getFormPanel().submit();
				}
			});
			button.setText("Login");
		}
		return button;
	}
}
