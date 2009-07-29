package org.vpac.grisu.client.model;

public class GwtJobConstants {
	
	// status
	public static final int LOADING = -1002;
	public static final int NOT_AVAILABLE = -1001;
	public static final int UNDEFINED = -1000;
	public static final int NO_SUCH_JOB = -999;
	public static final int JOB_CREATED = -100;
	public static final int READY_TO_SUBMIT = -4;
	public static final int EXTERNAL_HANDLE_READY = -3;
	public static final int STAGE_IN = -2;
	public static final int UNSUBMITTED = -1;
	public static final int PENDING = 0;
	public static final int ACTIVE = 1;
	public static final int CLEAN_UP = 101;
	public static final int FINISHED_EITHER_WAY = 900;
	public static final int KILLED = 998;
	public static final int FAILED = 999;
	public static final int DONE = 1000;
	
	public static final String LOADING_STRING = "Loading...";
	public static final String NOT_AVAILABLE_STRING = "n/a";
	public static final String UNDEFINED_STRING = "Undefined";
	public static final String NO_SUCH_JOB_STRING = "No such job";
	public static final String JOB_CREATED_STRING = "Job created";
	public static final String READY_TO_SUBMIT_STRING = "Ready to submit";
	public static final String EXTERNAL_HANDLE_READY_STRING = "External handle ready";
	public static final String STAGE_IN_STRING = "Staging in";
	public static final String UNSUBMITTED_STRING = "Unsubmitted";
	public static final String PENDING_STRING = "Pending";
	public static final String ACTIVE_STRING = "Active";
	public static final String CLEAN_UP_STRING = "Cleaning up";
	public static final String FINISHED_EITHER_WAY_STRING = "User should never see that";
	public static final String KILLED_STRING = "Job killed";
	public static final String FAILED_STRING = "Failed";
	public static final String DONE_STRING = "Done";
	
	
	public static String translateStatus(int status_no) {
		

		
		switch (status_no) {
		case DONE: return DONE_STRING;
		case LOADING: return LOADING_STRING;
		case NOT_AVAILABLE: return NOT_AVAILABLE_STRING;
		case UNDEFINED: return UNDEFINED_STRING;
		case NO_SUCH_JOB: return NO_SUCH_JOB_STRING;
		case JOB_CREATED: return JOB_CREATED_STRING;
		case READY_TO_SUBMIT: return READY_TO_SUBMIT_STRING;
		case EXTERNAL_HANDLE_READY: return EXTERNAL_HANDLE_READY_STRING;
		case STAGE_IN: return STAGE_IN_STRING;
		case UNSUBMITTED: return UNSUBMITTED_STRING;
		case PENDING: return PENDING_STRING;
		case ACTIVE: return ACTIVE_STRING;
		case CLEAN_UP: return CLEAN_UP_STRING;
		case KILLED: return KILLED_STRING;
		case FAILED: return FAILED_STRING;
		}
		
		if ( status_no > DONE ) {
			return DONE_STRING+" (ExitCode: "+(status_no-1000)+")";
		}
		
		return UNDEFINED_STRING;
	}
	
	public static int translateStatusBack(String status) {
		
		if ( DONE_STRING.equals(status) ) 
			return DONE;
		else if ( status != null && status.indexOf("(") >= 0 ) {
			int start = status.indexOf("(")+11;
			int end = status.indexOf(")");
			String errorCodeString = status.substring(start,end);
			int errorCode = Integer.parseInt(errorCodeString);
			return DONE+errorCode;
		} else if ( LOADING_STRING.equals(status) )
				return LOADING;
		else if ( NOT_AVAILABLE_STRING.equals(status) ) 
				return NOT_AVAILABLE;
		else if ( UNDEFINED_STRING.equals(status) )
			return UNDEFINED;
		else if ( NO_SUCH_JOB_STRING.equals(status) ) 
			return NO_SUCH_JOB;
		else if ( JOB_CREATED_STRING.equals(status) )
			return JOB_CREATED;
		else if ( READY_TO_SUBMIT_STRING.equals(status) )
			return READY_TO_SUBMIT;
		else if ( EXTERNAL_HANDLE_READY_STRING.equals(status) )
			return EXTERNAL_HANDLE_READY;
		else if ( STAGE_IN_STRING.equals(status) )
			return STAGE_IN;
		else if ( UNSUBMITTED_STRING.equals(status) )
			return UNSUBMITTED;
		else if ( PENDING_STRING.equals(status) )
			return PENDING;
		else if ( ACTIVE_STRING.equals(status) )
			return ACTIVE;
		else if ( CLEAN_UP_STRING.equals(status) )
			return CLEAN_UP;
		else if ( KILLED_STRING.equals(status) )
			return KILLED;
		else if ( FAILED_STRING.equals(status) )
			return FAILED;

		
		return UNDEFINED;
		
	}

}
