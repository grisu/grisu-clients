package org.vpac.grisu.client.control.files;

import org.vpac.grisu.client.model.files.events.FileSystemBackendEvent;

public interface FileManagerListener {

	public void fileSystemBackendsChanged(FileSystemBackendEvent event);

}
