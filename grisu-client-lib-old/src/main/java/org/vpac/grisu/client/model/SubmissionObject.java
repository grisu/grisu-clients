package org.vpac.grisu.client.model;

import java.util.Map;

import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;

/**
 * Just a wrapper object
 * 
 * @author Markus Binsteiner
 *
 */
public interface SubmissionObject {
	
	public static final int EXECUTABLE_TYPE_UNDEFINED = 0;
	public static final int EXECUTABLE_TYPE_SERIAL = 1;
	public static final int EXECUTABLE_TYPE_PARALLEL = 2;

	/**
	 * Sets the internal state of this SubmissionObject to point it to the specified
	 * {@link SubmissionLocationException}.
	 * @param location the locations
	 * @throws SubmissionLocationException if the location is not valid (most likely because there is no staging filesystem for it in the users' environment).
	 */
	public void setCurrentSubmissionLocation(SubmissionLocation location) throws SubmissionLocationException;
	
	public SubmissionLocation getCurrentSubmissionLocation();

//	public ApplicationObject getApplication();
	public String getCurrentApplicationName();
	
	public String[] getCurrentExecutables();
	
	public String[] getCurrentModules();
	
	public String getCurrentVersion();
	
	public int getPreferredExecutableType();
	
	public void setPreferredExecutableType(int type);
	
	public Map<String, String> getCurrentApplicationDetails();
}
