

package org.vpac.grisu.client.view.swing.files;

import org.vpac.grisu.client.model.files.GrisuFileObject;

public class FileChooserEvent {
	
	public static final int CANCELLED = -1;
	public static final int SELECTED_FILE = 0;
	public static final int SELECTED_FILES = 1;
	
	public static final int CHANGED_FOLDER = 2;
	
	private int type = -2;
	
	private GrisuFileObject selectedFile = null;
	private GrisuFileObject[] selectedFiles = null;
	
	public FileChooserEvent(int type, GrisuFileObject[] selectedFiles) {
		
		this.type = type;
		
		if ( this.type == CHANGED_FOLDER ) {
			this.selectedFile = selectedFiles[0];
		} else if ( this.type == SELECTED_FILE ) {
			this.selectedFile = selectedFiles[0];
		} else if ( this.type == SELECTED_FILES ) {
			this.selectedFiles = selectedFiles;
		} else {
			this.type = CANCELLED;
		}
		
	}
	
	public FileChooserEvent(GrisuFileObject[] selectedFiles) {
		
		if ( selectedFiles == null || selectedFiles.length == 0 ) {
			this.type = CANCELLED;
		} else if ( selectedFiles.length == 1 ) {
			this.type = SELECTED_FILE;
			this.selectedFile = selectedFiles[0];
		} else  {
			this.type = SELECTED_FILES;
			this.selectedFiles = selectedFiles;
		}
	}
	
	public int getType() {
		return this.type;
	}

	public GrisuFileObject getSelectedFile() {
		return selectedFile;
	}

	public GrisuFileObject[] getSelectedFiles() {
		return selectedFiles;
	}

}
