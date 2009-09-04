package org.vpac.grisu.clients.gridTests;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;
import org.vpac.grisu.frontend.control.clientexceptions.MdsInformationException;
import org.w3c.dom.Document;

public interface GridTestInfo {

	public abstract File getTestBaseDir();

//	public abstract List<String> getInputFiles();

//	public abstract List<String> getOutputFiles();

//	public abstract Document getJsdlDoc();
	

	public abstract List<GridTestElement> generateAllGridTestElements()
			throws MdsInformationException;

	public abstract GridTestController getController();

	public abstract String getTestname();

	public abstract String getDescription();

	public abstract boolean isUseMds();

	public abstract String getApplicationName();

//	public abstract Map<String, Set<String>> getSubmissionLocationsPerVersion();

	public abstract GridTestElement createGridTestElement(String version,
			String submissionLocation, String fqan) throws MdsInformationException;
	
	public abstract String[] getFqans();

}