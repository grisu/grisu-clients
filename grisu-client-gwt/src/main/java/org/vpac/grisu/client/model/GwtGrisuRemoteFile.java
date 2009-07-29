package org.vpac.grisu.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GwtGrisuRemoteFile implements IsSerializable {
	
	public final static int SITE_TYPE = 0;
	public final static int MOUNTPOINT_TYPE = 1;
	public final static int FILESHARE_TYPE = 2;
	public final static int FILESYSTEM_TYPE = 3;
	
	private String name;
	private String path;
	
	private boolean isMarkedAsParent = false;
	
	public boolean isMarkedAsParent() {
		return isMarkedAsParent;
	}

	public void setMarkedAsParent(boolean isMarkedAsParent) {
		this.isMarkedAsParent = isMarkedAsParent;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	private int type;
	private boolean isFolder;
	private long size;
	
	public GwtGrisuRemoteFile() {
		
	}
	
	public GwtGrisuRemoteFile(String sitename) {
		this.name = sitename;
		this.path = sitename;
		this.isFolder = true;
		this.size = -1;
		this.type = SITE_TYPE;
	}
	
	public GwtGrisuRemoteFile(String name, String path, boolean isFolder, int type, long size) {
		this.name = name;
		this.path = path;
		this.isFolder = isFolder;
		this.size = size;
		this.type = type;
	}
	
	public GwtGrisuRemoteFile(GwtMountPointWrapper gwtMountPointWrapper) {
	
		this.name = gwtMountPointWrapper.getAlias();
		this.path = gwtMountPointWrapper.getRootUrl();
		this.isFolder = true;
		this.size = -1;
		this.type = MOUNTPOINT_TYPE;
	}

	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFolder() {
		return isFolder;
	}
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public boolean equals(Object other) {
		if ( other instanceof GwtGrisuRemoteFile ) {
			try {
				return this.getPath().equals(((GwtGrisuRemoteFile)other).getPath());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return getPath().hashCode();
	}

}
