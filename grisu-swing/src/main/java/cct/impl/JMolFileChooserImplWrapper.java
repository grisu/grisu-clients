package cct.impl;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.view.swing.files.FileChooserEvent;
import org.vpac.grisu.client.view.swing.files.FileChooserParent;
import org.vpac.grisu.client.view.swing.files.SiteFileChooserDialog;

import cct.interfaces.FileChooserInterface;

/**
 * A wrapper class that tries to connect the existing
 * {@link SiteFileChooserDialog} class with the {@link FileChooserInterface} of
 * the JMolEditor project.
 * 
 * @author Markus Binsteiner
 * 
 */
public class JMolFileChooserImplWrapper implements FileChooserInterface,
		FileChooserParent {

	private EnvironmentManager em = null;
	private SiteFileChooserDialog dialog = null;

	private String oneFileSelected = null;
	private GrisuFileObject[] multipleFilesSelected = null;

	public JMolFileChooserImplWrapper(EnvironmentManager em) {
		this.em = em;
		dialog = new SiteFileChooserDialog(em);
	}

	public String[] getDirectories() {
		return new String[] { getDirectory() };
	}

	public String getDirectory() {
		return dialog.getCurrentDirectory().getURI().toString();
	}

	public String getFile() {
		return oneFileSelected;
	}

	public String[] getFiles() {

		if (multipleFilesSelected == null) {
			return null;
		}
		String[] result = new String[multipleFilesSelected.length];
		for (int i = 0; i < multipleFilesSelected.length; i++) {
			result[i] = multipleFilesSelected[i].getURI().toString();
		}
		return result;
	}

	public String pwd() {
		return dialog.getCurrentDirectory().getURI().toString();
	}

	public void setFileChooserVisible(boolean enable) throws Exception {
		dialog.setVisible(enable);
	}

	public void setSelectionMode(int selectionMode) {
		dialog.setSelectionMode(selectionMode);
	}

	public void userInput(FileChooserEvent event) {

		if (FileChooserEvent.SELECTED_FILE == event.getType()) {
			oneFileSelected = event.getSelectedFile().getURI().toString();
		} else if (FileChooserEvent.SELECTED_FILES == event.getType()) {
			multipleFilesSelected = event.getSelectedFiles();
		}

		if (FileChooserEvent.CANCELLED == event.getType()
				|| FileChooserEvent.SELECTED_FILE == event.getType()
				|| FileChooserEvent.SELECTED_FILES == event.getType()) {
			dialog.setVisible(false);
		}

	}

}
