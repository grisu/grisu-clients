package org.vpac.grisu.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GwtMountPointWrapper implements IsSerializable {
	
	private String dn;
	private String fqan;
	private String mountpoint;
	private String rootUrl;
	private String site;
	
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getFqan() {
		return fqan;
	}
	public void setFqan(String fqan) {
		this.fqan = fqan;
	}
	public String getMountpointName() {
		return mountpoint;
	}
	public void setMountpointName(String mountpoint) {
		this.mountpoint = mountpoint;
	}
	public String getRootUrl() {
		return rootUrl;
	}
	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	
	public String toString() {
		return getMountpointName();
	}
	
}

