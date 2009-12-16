package org.vpac.grisu.client.view.swing.files;

import javax.swing.JMenu;

import org.vpac.grisu.client.model.files.GrisuFileObject;

public class BackendFileObjectMenu extends JMenu {

	private GrisuFileObject file = null;

	public BackendFileObjectMenu(GrisuFileObject file) {
		this.file = file;
	}

}
