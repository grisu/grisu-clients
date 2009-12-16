package org.vpac.grisu.client.model;

import java.util.HashMap;
import java.util.Map;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;

/**
 * This is a very simple implementation of the {@link SubmissionObject}
 * interface. Basically it only holds the submissionlocation that gets set to
 * give it back later.
 * 
 * It uses mds still for lookup of available submission locations, but it
 * doesn't check whether the application is available there. Neither is it able
 * to get executable/modules information from mds. This information has to be
 * set using the @link {@link #setModules(String[])} &
 * {@link #setExecutables(String[])} methods.
 * 
 * @author Markus Binsteiner
 * 
 */
public class NoMDSApplicationObject implements SubmissionObject {

	private EnvironmentManager em = null;
	private String application = null;
	private String[] executables = null;
	private String[] modules = null;

	private int preferredExecutableType = EXECUTABLE_TYPE_UNDEFINED;

	private SubmissionLocation currentSubmissionLocation = null;

	public NoMDSApplicationObject(String application, EnvironmentManager em) {
		this.application = application;
		this.em = em;
	}

	public Map<String, String> getCurrentApplicationDetails() {
		// no way of retrieving application details
		return new HashMap<String, String>();
	}

	public String getCurrentApplicationName() {
		return application;
	}

	public String[] getCurrentExecutables() {
		return executables;
	}

	public String[] getCurrentModules() {
		return modules;
	}

	public SubmissionLocation getCurrentSubmissionLocation() {
		return currentSubmissionLocation;
	}

	public String getCurrentVersion() {
		return null;
	}

	public int getPreferredExecutableType() {
		return preferredExecutableType;
	}

	public void setCurrentSubmissionLocation(SubmissionLocation location)
			throws SubmissionLocationException {
		this.currentSubmissionLocation = location;
	}

	public void setExecutables(String[] executables) {
		this.executables = executables;
	}

	public void setModules(String[] modules) {
		this.modules = modules;
	}

	public void setPreferredExecutableType(int type) {
		this.preferredExecutableType = type;
	}

}
