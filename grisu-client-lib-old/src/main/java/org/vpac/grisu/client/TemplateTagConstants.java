package org.vpac.grisu.client;

public class TemplateTagConstants {
	
	public static final String WALLTIME_TAG_NAME = "Walltime";
	public static final String JOBNAME_TAG_NAME = "Jobname";
	public static final String CPUS_TAG_NAME = "CPUs";
	public static final String EMAIL_ADDRESS_TAG_NAME = "EmailAddress";
	public static final String MIN_MEM_TAG_NAME = "MinMem";
	public static final String EXECUTIONFILESYSTEM_TAG_NAME = "ExecutionFileSystem";
	public static final String HOSTNAME_TAG_NAME = "HostName";
	public static final String MODULE_TAG_NAME = "Module";
	public static final String APPLICATION_TAG_NAME = "Application";
	public static final String VERSION_TAG_NAME = "Version"; 
//	public static final String

	

	
	public static final String GLOBAL_LAST_VERSION_KEY = "GlobalLastVersion";
	public static final String GLOBAL_LAST_VERSION_MODE_KEY = "GlobalLastVersionMode";
	public static final String GLOBAL_LAST_QUEUE_KEY = "GlobalLastQueue";
	
	public static String getGlobalLastVersionKey(String application) {
		return GLOBAL_LAST_VERSION_KEY+"_"+application;
	}

	public static String getGlobalLastVersionModeKey(String application) {
		return GLOBAL_LAST_VERSION_MODE_KEY+"_"+application;
	}

	public static String getGlobalLastQueueKey(String application) {
		return GLOBAL_LAST_QUEUE_KEY+"_"+application;
	}

}
