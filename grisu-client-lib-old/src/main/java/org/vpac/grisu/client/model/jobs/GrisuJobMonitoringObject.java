

package org.vpac.grisu.client.model.jobs;

import java.util.Map;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public interface GrisuJobMonitoringObject {

	public static final long MIN_DELTA_BETWEEN_STATUS_CHECKS = 60000; // in milliseconds

	public abstract ServiceInterface getServiceInterface();
	
	public abstract EnvironmentManager getEnvironmentManager();

	public abstract Map<String, String> getJobProperties();

	public abstract String getName();

	public abstract String getApplicationType();

	public abstract String getWalltime();

	public abstract String getCpus();

	public abstract String getSubmissionHost();

	public abstract String getSubmissionQueue();

	public abstract String getSubmissionTime();

	public abstract String getFqan();

	public abstract String getStatus(boolean forceRefresh);
	
	/**
	 * Returns the status without a forced refresh of the job status
	 * @return the status of the job
	 */
	public abstract String getStatus();
	
	public abstract int getStatusAsInt();
	
	public abstract int getStatusAsInt(boolean forceRefresh);

	public abstract String getJobDirectory();

	public abstract String getStdout();

	public abstract String getStderr();

	public abstract Map<String, String> getOtherProperties();

	public abstract GrisuFileObject getJobDirectoryObject();
	
	public abstract void fillJobDetails() throws NoSuchJobException;
	
	/**
	 * This is just so that you can retrieve the job properties seperately using {@link ServiceInterface#getJob(String)}
	 * in a seperate thread. If you don't know what you are doing, use {@link #fillJobDetails()}.
	 * @param jobProperties the porperties for this job.
	 */
	public abstract void fillJobDetails(Map<String, String> jobProperties);
	
	/**
	 * Kills the current job. Leaves the jobdirectory on the server.
	 * @throws VomsException if the vo to access this job is not available
	 * @throws NoSuchJobException if the job is already killed
	 * @throws BatchJobException 
	 */
	public abstract void kill() throws NoSuchJobException, BatchJobException;
	/**
	 * Kills the current job and deletes the whole jobdirectory on the server.
	 * @throws RemoteFileSystemException if the jobdirectory could not be accessed/deleted.
	 * @throws VomsException if the vo to access this job is not available
	 * @throws NoSuchJobException if the job is already killed
	 */
	public abstract void killAndClean() throws RemoteFileSystemException, NoSuchJobException;

	
	
}