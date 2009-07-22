package org.vpac.grisu.client.view.swing.utils;

import java.util.ResourceBundle;

public class Messages {
	
	private static ResourceBundle messages = ResourceBundle.getBundle(
			"GrisuSwingClientMessageBundle", java.util.Locale.getDefault());
	
	public static String getMessage(String key) {
		return messages.getString(key);
	}
	

}
