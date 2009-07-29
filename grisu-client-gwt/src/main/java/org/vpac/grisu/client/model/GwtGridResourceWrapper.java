package org.vpac.grisu.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GwtGridResourceWrapper implements IsSerializable {
	
	public String getApplicationName() {
		return appName;
	}

	public void setApplicationName(String appName) {
		this.appName = appName;
	}

	public String[] getAvailableAppVersion() {
		return availableAppVersion;
	}

	public void setAvailableAppVersion(String[] availableAppVersion) {
		this.availableAppVersion = availableAppVersion;
	}

	public String getContactString() {
		return contactString;
	}

	public void setContactString(String contactString) {
		this.contactString = contactString;
	}

	public int getFreeJobSlots() {
		return freeJobSlots;
	}

	public void setFreeJobSlots(int freeJobSlots) {
		this.freeJobSlots = freeJobSlots;
	}

	public String getJobManager() {
		return jobManager;
	}

	public void setJobManager(String jobManager) {
		this.jobManager = jobManager;
	}

	public int getMainMemoryRamSize() {
		return mainMemoryRamSize;
	}

	public void setMainMemoryRamSize(int mainMemoryRamSize) {
		this.mainMemoryRamSize = mainMemoryRamSize;
	}

	public int getMainMemoryVirtSize() {
		return mainMemoryVirtSize;
	}

	public void setMainMemoryVirtSize(int mainMemoryVirtSize) {
		this.mainMemoryVirtSize = mainMemoryVirtSize;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRunningJobs() {
		return runningJobs;
	}

	public void setRunningJobs(int runningJobs) {
		this.runningJobs = runningJobs;
	}

	public double getSiteLatitude() {
		return siteLatitude;
	}

	public void setSiteLatitude(double siteLatitude) {
		this.siteLatitude = siteLatitude;
	}

	public double getSiteLongitude() {
		return siteLongitude;
	}

	public void setSiteLongitude(double siteLongtitude) {
		this.siteLongitude = siteLongtitude;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public int getSmpSize() {
		return smpSize;
	}

	public void setSmpSize(int smpSize) {
		this.smpSize = smpSize;
	}

	public int getTotalJobs() {
		return totalJobs;
	}

	public void setTotalJobs(int totalJobs) {
		this.totalJobs = totalJobs;
	}

	public int getWaitingJobs() {
		return waitingJobs;
	}

	public void setWaitingJobs(int waitingJobs) {
		this.waitingJobs = waitingJobs;
	}

	public boolean isDesiredSoftwareVersionInstalled() {
		return isDesiredSoftwareVersionInstalled;
	}

	public void setDesiredSoftwareVersionInstalled(
			boolean isDesiredSoftwareVersionInstalled) {
		this.isDesiredSoftwareVersionInstalled = isDesiredSoftwareVersionInstalled;
	}
	
	public String[] getAllExecutables() {
		return this.allExecutables;
	}
	
	public void setAllExecutables(String[] allExecutables) {
		this.allExecutables = allExecutables;
	}

	private String appName = null;

	private String[] availableAppVersion = null;

	private String contactString = null;

	private int freeJobSlots = -1;

	private String jobManager = null;

	private int mainMemoryRamSize = -1;

	private int mainMemoryVirtSize = -1;

	private String queueName = null;

	private int rank = -1;

	private int runningJobs = -1;

	private double siteLatitude = -1;

	private double siteLongitude = -1;

	private String siteName = null;

	private int smpSize = -1;

	private int totalJobs = -1;

	private int waitingJobs = -1;

	private boolean isDesiredSoftwareVersionInstalled = false;
	
	private String[] allExecutables = null;
	
	public GwtGridResourceWrapper() {
		
	}

//	public GwtGridResourceWrapper(
//			String appName,
//			List<String> availableAppVersion,
//			String contactString,
//			int freeJobSlots,
//			String jobManager,
//			int mainMemoryRamSize,
//			int mainMemoryVirtSize,
//			String queueName,
//			int rank,
//			int runningJobs,
//			double siteLatitude,
//			double siteLongtitude,
//			String siteName,
//			int smpSize,
//			int totalJobs,
//			int waitingJobs,
//			boolean isDesiredSoftwareVersionInstalled
//			) {
//		
//		this.appName = appName;
//		this.availableAppVersion = availableAppVersion;
//		this.contactString = contactString;
//		this.freeJobSlots = freeJobSlots;
//		this.jobManager = jobManager;
//		this.mainMemoryRamSize = mainMemoryRamSize;
//		this.mainMemoryVirtSize = mainMemoryVirtSize;
//		this.queueName = queueName;
//		this.rank = rank;
//		this.runningJobs = runningJobs;
//		this.siteLatitude = siteLatitude;
//		this.siteLongitude = siteLongtitude;
//		this.siteName = siteName;
//		this.smpSize = smpSize;
//		this.totalJobs = totalJobs;
//		this.waitingJobs = waitingJobs;
//		this.isDesiredSoftwareVersionInstalled = isDesiredSoftwareVersionInstalled;
//		
//	
//	}


}
