package org.vpac.grisu.client.control.utils;

import java.util.EventObject;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.model.MountPoint;

public class MountPointEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public final static int MOUNTPOINT_ADDED = 1;
	public final static int MOUNTPOINT_REMOVED = 2;
	public final static int MOUNTPOINTS_REFRESHED = 3;

	private int event_type = -1;
	private MountPoint mp = null;
	private MountPoint[] mps = null;
	private ServiceInterface serviceInterface = null;

	public MountPointEvent(Object source, int event_type, MountPoint mp,
			ServiceInterface serviceInterface) {
		super(source);
		this.event_type = event_type;
		this.mp = mp;
		this.serviceInterface = serviceInterface;
	}

	public MountPointEvent(Object source, MountPoint[] mps,
			ServiceInterface serviceInterface) {
		super(source);
		this.event_type = MOUNTPOINTS_REFRESHED;
		this.mps = mps;
		this.serviceInterface = serviceInterface;
	}

	public int getEventType() {
		return event_type;
	}

	public MountPoint getMountPoint() {
		return mp;
	}

	public MountPoint[] getMountPoints() {
		return mps;
	}

	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}
}
