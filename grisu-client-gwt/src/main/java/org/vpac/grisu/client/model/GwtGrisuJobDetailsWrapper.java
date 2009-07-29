package org.vpac.grisu.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GwtGrisuJobDetailsWrapper implements IsSerializable {
	
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getNo_cpus() {
		return no_cpus;
	}
	public void setNo_cpus(int no_cpus) {
		this.no_cpus = no_cpus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getWalltime() {
		return walltime;
	}
	public void setWalltime(int walltime) {
		this.walltime = walltime;
	}
	public String getSubmissionHost() {
		return submissionHost;
	}
	public void setSubmissionHost(String submissionHost) {
		this.submissionHost = submissionHost;
	}
	public long getSubmissionTime() {
		return submissionTime;
	}
	public void setSubmissionTime(long submissionTime) {
		this.submissionTime = submissionTime;
	}
	public String getCommandline() {
		return commandline;
	}
	public void setCommandline(String commandline) {
		this.commandline = commandline;
	}
	public String getFqan() {
		return fqan;
	}
	public void setFqan(String fqan) {
		this.fqan = fqan;
	}
	public String getJobDirectory() {
		return jobDirectory;
	}
	public void setJobDirectory(String jobDirectory) {
		this.jobDirectory = jobDirectory;
	}
	public String getStdout() {
		return stdout;
	}
	public void setStdout(String stdout) {
		this.stdout = stdout;
	}
	public String getStderr() {
		return stderr;
	}
	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
	public String[] getInputfiles() {
		return inputfiles;
	}
	public void setInputfiles(String[] inputfiles) {
		this.inputfiles = inputfiles;
	}
	public String[] getOtherProperties() {
		return otherProperties;
	}
	public void setOtherProperties(String[] otherProperties) {
		this.otherProperties = otherProperties;
	}
	public String jobname;
	public String application;
	public String version;
	public int no_cpus;
	public String status;
	public int walltime;
	public String submissionHost;
	public long submissionTime;
	public String commandline;
	public String fqan;
	public String jobDirectory;
	public String stdout;
	public String stderr;
	public String[] inputfiles;
	public String[] otherProperties;
	
	public GwtGrisuJobDetailsWrapper() {
		
	}

}
