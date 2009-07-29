package org.vpac.grisu.client;

import java.util.Map;

import org.vpac.grisu.client.files.FileSystemException;
import org.vpac.grisu.client.jobCreation.JobCreationException;
import org.vpac.grisu.client.model.GwtGridResourceWrapper;
import org.vpac.grisu.client.model.GwtGrisuCacheFile;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;
import org.vpac.grisu.client.model.GwtJobException;
import org.vpac.grisu.client.model.GwtMountPointWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GwtServiceInterfaceWrapper")
public interface GwtServiceInterfaceWrapper extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static GwtServiceInterfaceWrapperAsync instance;
		public static GwtServiceInterfaceWrapperAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(GwtServiceInterfaceWrapper.class);
			}
			return instance;
		}
	}
	
	public boolean checkLogin();
	
	public void login(Map loginData) throws LoginException;
	
	public String[] getAllFqans();
	
	public String getJobsStatus();
	
	public Map<String, String> getJobDetails(String jobname);
	
	public GwtMountPointWrapper[] getAllMountPoints();
	
	public GwtGrisuRemoteFile[] getChildren(String url) throws FileSystemException;
	
	public String[] getAllApplicationsOnTheGrid();
	
	/**
	 * Downloads a file to the local filesystem (serverd by apache or so)
	 * @param path the url to the file
	 * @return the url where this file can be accessed as well as the mi
	 */
	public GwtGrisuCacheFile downloadFile(GwtGrisuRemoteFile file);
	
	public String zipFilesAndPrepareForDownload(GwtGrisuRemoteFile[] files);
	
	public String[] getAllAvailableVOs();
	
	public GwtGridResourceWrapper[] findGridResourcesForVersionsAndFqans(String applicationName, String[] versions, String[] fqans);
	
	public String[] getVersionsOfApplication(String applicationName);
	
	public String[] getVersionsOfApplicationForVO(String applicationName, String fqan);
	
	public String[] getApplicationForExecutable(String executable);
	
	public void submitJob(Map<String, String> jobProperties) throws JobCreationException;
	
	public void killJob(String[] jobnames, boolean clean) throws GwtJobException;
}
