package org.vpac.grisu.client;

import java.util.Map;

import org.vpac.grisu.client.model.GwtGridResourceWrapper;
import org.vpac.grisu.client.model.GwtGrisuCacheFile;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;
import org.vpac.grisu.client.model.GwtMountPointWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GwtServiceInterfaceWrapperAsync {
	public void login(Map loginData, AsyncCallback<Void> callback);
	
	public void getAllFqans(AsyncCallback<String[]> callback);
	
	public void getJobsStatus(AsyncCallback<String> callback);
	
	public void getJobDetails(String jobname, AsyncCallback<Map<String, String>> callback);
	
	public void getAllMountPoints(AsyncCallback<GwtMountPointWrapper[]> callback);
	
	public void getChildren(String url, AsyncCallback<GwtGrisuRemoteFile[]> callback);
	
	public void getAllApplicationsOnTheGrid(AsyncCallback<String[]> callback);
	
	/**
	 * Downloads a file to the local filesystem (serverd by apache or so)
	 * @param path the url to the file
	 * @param callback the callback to return the url where this file can be accessed as well as the mi
	 */
	public void downloadFile(GwtGrisuRemoteFile file, AsyncCallback<GwtGrisuCacheFile> callback);
	
	public void zipFilesAndPrepareForDownload(GwtGrisuRemoteFile[] files, AsyncCallback<String> callback);
	
	public void getAllAvailableVOs(AsyncCallback<String[]> callback);
	
	public void findGridResourcesForVersionsAndFqans(String applicationName, String[] versions, String[] fqans, AsyncCallback<GwtGridResourceWrapper[]> callback);
	
	public void getVersionsOfApplication(String applicationName, AsyncCallback<String[]> callback);
	
	public void getVersionsOfApplicationForVO(String applicationName, String fqan, AsyncCallback<String[]> callback);
	
	public void getApplicationForExecutable(String executable, AsyncCallback<String[]> callback);
	
	public void submitJob(Map<String, String> jobProperties, AsyncCallback<Void> callback);
	
	public void killJob(String[] jobnames, boolean clean, AsyncCallback<Void> callback);
	
	public void checkLogin(AsyncCallback<Boolean> callback);
}
