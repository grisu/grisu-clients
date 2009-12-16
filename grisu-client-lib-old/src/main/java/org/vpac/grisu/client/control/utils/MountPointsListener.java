package org.vpac.grisu.client.control.utils;

import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public interface MountPointsListener {

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException;

}
