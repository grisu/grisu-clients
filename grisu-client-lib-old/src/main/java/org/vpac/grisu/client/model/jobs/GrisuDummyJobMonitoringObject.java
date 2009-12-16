package org.vpac.grisu.client.model.jobs;

import java.util.Map;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.control.JobConstants;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

public class GrisuDummyJobMonitoringObject implements GrisuJobMonitoringObject {

	private String jobname = null;
	private ServiceInterface serviceInterface = null;
	private EnvironmentManager em = null;

	private String message = null;

	public GrisuDummyJobMonitoringObject(String jobname, EnvironmentManager em,
			String message) {
		this.jobname = null;
		this.em = em;
		this.serviceInterface = em.getServiceInterface();
		this.message = message;
	}

	public void fillJobDetails() throws NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public void fillJobDetails(Map<String, String> jobProperties) {
		// TODO Auto-generated method stub

	}

	public String getApplicationType() {
		return null;
	}

	public String getCpus() {
		return null;
	}

	public EnvironmentManager getEnvironmentManager() {
		return em;
	}

	public String getFqan() {
		return null;
	}

	public String getJobDirectory() {
		return null;
	}

	public GrisuFileObject getJobDirectoryObject() {
		return null;
	}

	public Map<String, String> getJobProperties() {
		return null;
	}

	public String getName() {
		return jobname;
	}

	public Map<String, String> getOtherProperties() {
		return null;
	}

	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	public String getStatus() {
		return message;
	}

	public String getStatus(boolean forceRefresh) {
		return message;
	}

	public int getStatusAsInt() {
		return JobConstants.UNDEFINED;
	}

	public int getStatusAsInt(boolean forceRefresh) {
		return JobConstants.UNDEFINED;
	}

	public String getStderr() {
		return null;
	}

	public String getStdout() {
		return null;
	}

	public String getSubmissionHost() {
		return null;
	}

	public String getSubmissionQueue() {
		return null;
	}

	public String getSubmissionTime() {
		return null;
	}

	public String getWalltime() {
		return null;
	}

	public void kill() throws NoSuchJobException {
		// TODO Auto-generated method stub

	}

	public void killAndClean() throws RemoteFileSystemException,
			NoSuchJobException {
		// TODO Auto-generated method stub

	}

}
