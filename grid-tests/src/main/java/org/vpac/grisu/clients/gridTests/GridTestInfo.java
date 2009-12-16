package org.vpac.grisu.clients.gridTests;

import java.io.File;
import java.util.List;

import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;
import org.vpac.grisu.frontend.control.clientexceptions.MdsInformationException;

public interface GridTestInfo {

	public abstract GridTestElement createGridTestElement(String version,
			String submissionLocation, String fqan)
			throws MdsInformationException;

	// public abstract List<String> getInputFiles();

	// public abstract List<String> getOutputFiles();

	// public abstract Document getJsdlDoc();

	public abstract List<GridTestElement> generateAllGridTestElements()
			throws MdsInformationException;

	public abstract String getApplicationName();

	public abstract GridTestController getController();

	public abstract String getDescription();

	public abstract String[] getFqans();

	public abstract File getTestBaseDir();

	// public abstract Map<String, Set<String>>
	// getSubmissionLocationsPerVersion();

	public abstract String getTestname();

	public abstract boolean isUseMds();

}