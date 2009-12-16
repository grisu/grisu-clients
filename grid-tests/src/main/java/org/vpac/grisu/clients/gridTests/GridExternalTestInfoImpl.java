package org.vpac.grisu.clients.gridTests;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.clients.gridTests.testElements.ExternalGridTestElement;
import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;
import org.vpac.grisu.frontend.control.clientexceptions.MdsInformationException;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

import au.org.arcs.jcommons.constants.Constants;
import au.org.arcs.jcommons.utils.JsdlHelpers;

public class GridExternalTestInfoImpl implements GridTestInfo {

	public static final String TESTPROPERTIES_FILENAME = "grisu-test.properties";

	public static final List<GridTestInfo> generateGridTestInfos(
			GridTestController controller, String[] testnames, String[] fqans) {

		List<GridTestInfo> result = new LinkedList<GridTestInfo>();
		File baseDir = controller.getGridTestDirectory();

		File[] children = baseDir.listFiles();

		if (children == null) {
			return result;
		}
		for (File child : children) {

			if (child.exists() && child.isDirectory()
					&& !child.getName().startsWith(".")) {
				GridExternalTestInfoImpl info = new GridExternalTestInfoImpl(
						child, controller, fqans);
				if (testnames.length == 0
						|| Arrays.binarySearch(testnames, info.getTestname()) >= 0) {
					result.add(info);
				}
			}

		}

		return result;
	}
	private final String testname;
	private final String description;
	private final boolean useMds;
	private final String applicationName;
	private final String versionName;
	// private final Map<String, Set<String>> subLocsPerVersions = new
	// TreeMap<String, Set<String>>();
	private final File testDir;
	private final File jsdlFile;
	private final List<String> inputFiles;

	private final String[] fqans;

	private final List<String> outputFiles;

	private final String command;

	private final Document jsdlDoc;

	private final GridTestController controller;

	public GridExternalTestInfoImpl(File rootfolder,
			GridTestController controller, String[] fqans) {

		this.controller = controller;
		this.fqans = fqans;

		this.testDir = rootfolder;
		File propertiesFile = new File(testDir, TESTPROPERTIES_FILENAME);
		if (!propertiesFile.exists()) {
			System.err.println("Can't create test for folder "
					+ testDir.getPath() + ". No valid "
					+ TESTPROPERTIES_FILENAME + " file found.");
			System.err.println("Exiting...");
			System.exit(1);
		}
		Properties testProperties = new Properties();
		try {
			testProperties.load(new FileInputStream(propertiesFile));
		} catch (Exception e) {
			System.err.println("Can't create test for folder "
					+ testDir.getPath() + ". No valid "
					+ TESTPROPERTIES_FILENAME + " file found.");
			System.err.println("Exiting...");
			System.exit(1);
		}

		if (StringUtils.isBlank(testProperties.getProperty("jsdlfile"))) {
			// for the compiler
			jsdlFile = null;
			System.err.println("No jsdl file specified. Exiting...");
			System.exit(1);
		} else {
			String jsdlFilename = testProperties.getProperty("jsdlfile");
			jsdlFile = new File(testDir, jsdlFilename);
			if (!jsdlFile.exists()) {
				System.err.println("Specified jsdl file doesn't exist.");
			} else {
				try {
					SeveralXMLHelpers.loadXMLFile(jsdlFile);
				} catch (Exception e) {
					System.err.println("Could not parse jsdl file: "
							+ e.getLocalizedMessage());
					System.err.println("Exiting...");
					System.exit(1);
				}
			}
		}

		String temp = testProperties.getProperty("usemds", "true");
		if ("true".equals(temp.toLowerCase())) {
			useMds = true;
		} else {
			useMds = false;
		}

		jsdlDoc = SeveralXMLHelpers.loadXMLFile(jsdlFile);

		applicationName = JsdlHelpers.getApplicationName(jsdlDoc);
		if (StringUtils.isBlank(applicationName)) {
			System.err.println("No application name specified in jsdl file: "
					+ jsdlFile.getPath());
			System.err.println("Exiting...");
			System.exit(1);
		}
		versionName = JsdlHelpers.getApplicationVersion(jsdlDoc);

		if (StringUtils.isBlank(testProperties.getProperty("testname"))) {
			testname = testDir.getName();
		} else {
			testname = testProperties.getProperty("testname");
		}

		if (StringUtils.isBlank(testProperties.getProperty("description"))) {
			description = "No description.";
		} else {
			description = testProperties.getProperty("description");
		}

		inputFiles = new LinkedList<String>();
		String inputFilesString = testProperties.getProperty("inputfiles");
		if (StringUtils.isNotBlank(inputFilesString)) {
			for (String inputFile : inputFilesString.split(",")) {
				inputFiles.add(inputFile);
			}
		}
		String outputFilesString = testProperties.getProperty("outputfiles");
		outputFiles = new LinkedList<String>();
		if (StringUtils.isNotBlank(outputFilesString)) {
			for (String outputFileName : outputFilesString.split(",")) {
				outputFiles.add(outputFileName);
			}
		}

		command = testProperties.getProperty("command").replaceAll(
				"\\$TEST_DIR", testDir.getPath());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vpac.grisu.client.gridTests.GridTestInfo#createGridTestElement(java
	 * .lang.String, java.lang.String)
	 */
	public GridTestElement createGridTestElement(String version,
			String submissionLocation, String fqan)
			throws MdsInformationException {

		GridTestElement el = new ExternalGridTestElement(this, version,
				submissionLocation, fqan);
		return el;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vpac.grisu.client.gridTests.GridTestInfo#generateAllGridTestElements
	 * ()
	 */
	public final List<GridTestElement> generateAllGridTestElements()
			throws MdsInformationException {

		List<GridTestElement> results = new LinkedList<GridTestElement>();

		ApplicationInformation appInfo = GrisuRegistryManager.getDefault(
				controller.getServiceInterface()).getApplicationInformation(
				applicationName);

		for (String fqan : fqans) {

			if (useMds) {
				if (StringUtils.isNotBlank(versionName)
						&& !Constants.NO_VERSION_INDICATOR_STRING
								.equals(versionName)) {
					// means only one version
					Set<String> subLocs = appInfo
							.getAvailableSubmissionLocationsForVersionAndFqan(
									versionName, fqan);
					for (String subLoc : subLocs) {
						for (int i = 0; i < controller
								.getSameSubmissionLocation(); i++) {
							results.add(createGridTestElement(versionName,
									subLoc, fqan));
						}
					}
				} else {
					// means all versions
					Set<String> versions = appInfo
							.getAllAvailableVersionsForFqan(fqan);
					for (String version : versions) {
						Set<String> subLocs = appInfo
								.getAvailableSubmissionLocationsForVersionAndFqan(
										version, fqan);
						for (String subLoc : subLocs) {
							for (int i = 0; i < controller
									.getSameSubmissionLocation(); i++) {
								results.add(createGridTestElement(version,
										subLoc, fqan));
							}
						}
					}
				}
			} else {
				String[] subLocs = GrisuRegistryManager.getDefault(
						controller.getServiceInterface())
						.getResourceInformation()
						.getAllAvailableSubmissionLocations(fqan);
				for (String subLoc : subLocs) {
					for (int i = 0; i < controller.getSameSubmissionLocation(); i++) {
						results.add(createGridTestElement(
								Constants.NO_VERSION_INDICATOR_STRING, subLoc,
								fqan));
					}
				}
			}

		}

		return results;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getApplicationName()
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getCommand()
	 */
	public String getCommand() {
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getController()
	 */
	public GridTestController getController() {
		return this.controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	public String[] getFqans() {
		return fqans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getInputFiles()
	 */
	public List<String> getInputFiles() {
		return inputFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getJsdlDoc()
	 */
	public Document getJsdlDoc() {
		return jsdlDoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getJsdlFile()
	 */
	public File getJsdlFile() {
		return jsdlFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getOutputFiles()
	 */
	public List<String> getOutputFiles() {
		return outputFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getTestDir()
	 */
	public File getTestBaseDir() {
		return testDir;
	}

	// /* (non-Javadoc)
	// * @see
	// org.vpac.grisu.client.gridTests.GridTestInfo#getSubmissionLocationsPerVersion()
	// */
	// public Map<String, Set<String>> getSubmissionLocationsPerVersion() {
	// return subLocsPerVersions;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#getTestname()
	 */
	public String getTestname() {
		return testname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.vpac.grisu.client.gridTests.GridTestInfo#isUseMds()
	 */
	public boolean isUseMds() {
		return useMds;
	}

}
