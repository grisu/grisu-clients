

package org.vpac.grisu.client.view.swing.files;

public interface FileChooserParent {
	
	/**
	 * This is called if the user selected one or more files or cancelled the file choosing process.
	 * @param event the event
	 */
	public void userInput(FileChooserEvent event);

}
