

package org.vpac.grisu.client.model.files.events;

import java.util.EventObject;

import org.vpac.grisu.client.control.files.FileManager;
import org.vpac.grisu.client.model.files.FileSystemBackend;

public class FileSystemBackendEvent extends EventObject {
	
	public static final int FILESYSTEM_ADDED = 0;
	public static final int FILESYSTEM_REMOVED = -1;
	
	private FileSystemBackend filesystem = null;
	private int type = -1;
	
	public FileSystemBackendEvent(FileManager manager, FileSystemBackend filesystem, int type){
		super(manager);
		this.filesystem = filesystem;
		this.type = type;
	}

	public FileSystemBackend getFilesystem() {
		return filesystem;
	}

	public int getType() {
		return type;
	}

}
