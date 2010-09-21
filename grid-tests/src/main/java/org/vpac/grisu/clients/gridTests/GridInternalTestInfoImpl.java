package org.vpac.grisu.clients.gridTests;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;
import org.vpac.grisu.frontend.control.clientexceptions.MdsInformationException;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.info.ApplicationInformation;

import au.org.arcs.jcommons.constants.Constants;

public class GridInternalTestInfoImpl implements GridTestInfo {

	public static final List<GridTestInfo> generateGridTestInfos(
			GridTestController controller, String[] testnames, String[] fqans) {

		final List<GridTestInfo> result = new LinkedList<GridTestInfo>();

		if (testnames.length == 0) {
			testnames = new String[] { "Java", "SimpleCatJob", "Underworld",
					"UnixCommands" };
		}

		for (final String testname : testnames) {
			GridInternalTestInfoImpl info;
			try {
				info = new GridInternalTestInfoImpl(testname, controller, fqans);
				result.add(info);
			} catch (final ClassNotFoundException e) {
				System.out.println("No internal gridtest with the name: "
						+ testname + ". Ignoring it...");
				continue;
			}
		}

		return result;
	}

	private final Class testClass;
	private final String testname;
	private boolean useMds;
	private String applicationName;
	private String versionName;
	private String description;
	private final GridTestController controller;

	// private final Map<String, Set<String>> subLocsPerVersions = new
	// TreeMap<String, Set<String>>();
	private final String[] fqans;

	public GridInternalTestInfoImpl(String testname,
			GridTestController controller, String[] fqans)
			throws ClassNotFoundException {
		this.controller = controller;
		this.testname = testname;
		this.fqans = fqans;

		testClass = Class
				.forName("org.vpac.grisu.clients.gridTests.testElements."
						+ testname + "GridTestElement");

		try {
			final Method useMdsMethod = testClass.getMethod("useMDS");
			useMds = (Boolean) (useMdsMethod.invoke(null));

		} catch (final Exception e) {
			System.err.println("Could not create internal test " + testname
					+ " because the static useMDS method is not implemented: "
					+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		try {
			final Method method = testClass.getMethod("getApplicationName");
			applicationName = (String) (method.invoke(null));

		} catch (final Exception e) {
			System.err
					.println("Could not create internal test "
							+ testname
							+ " because the static getApplicationName method is not implemented: "
							+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		try {
			final Method method = testClass.getMethod("getTestDescription");
			description = (String) (method.invoke(null));

		} catch (final Exception e) {
			System.err
					.println("Could not create internal test "
							+ testname
							+ " because the static getTestDescription method is not implemented: "
							+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		try {
			final Method method = testClass.getMethod("getFixedVersion");
			versionName = (String) (method.invoke(null));

		} catch (final Exception e) {
			System.err
					.println("Could not create internal test "
							+ testname
							+ " because the static getFixedVersion method is not implemented: "
							+ e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

	}

	public GridTestElement createGridTestElement(String version,
			String submissionLocation, String fqan)
			throws MdsInformationException {

		Constructor testConstructor = null;
		try {
			testConstructor = testClass.getConstructor(GridTestInfo.class,
					String.class, String.class, String.class);
		} catch (final Exception e) {
			System.err.println("Could not create internal test " + testname
					+ ": " + e.getLocalizedMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}

		GridTestElement gte = null;
		try {
			gte = (GridTestElement) testConstructor.newInstance(this, version,
					submissionLocation, fqan);
		} catch (final Exception e) {
			System.err.println("Could not create internal test " + testname
					+ ": " + e.getLocalizedMessage());
			System.err.println("Exiting...");
			e.printStackTrace();
			System.exit(1);
		}

		return gte;
	}

	public List<GridTestElement> generateAllGridTestElements()
			throws MdsInformationException {

		final List<GridTestElement> results = new LinkedList<GridTestElement>();

		final ApplicationInformation appInfo = GrisuRegistryManager.getDefault(
				controller.getServiceInterface()).getApplicationInformation(
				applicationName);

		for (final String fqan : fqans) {

			if (useMds) {
				if (StringUtils.isNotBlank(versionName)
						&& !Constants.NO_VERSION_INDICATOR_STRING
								.equals(versionName)) {
					// means only one version
					final Set<String> subLocs = appInfo
							.getAvailableSubmissionLocationsForVersionAndFqan(
									versionName, fqan);
					for (final String subLoc : subLocs) {
						for (int i = 0; i < controller
								.getSameSubmissionLocation(); i++) {
							results.add(createGridTestElement(versionName,
									subLoc, fqan));
						}
					}
				} else {
					// means all versions
					final Set<String> versions = appInfo
							.getAllAvailableVersionsForFqan(fqan);
					for (final String version : versions) {
						final Set<String> subLocs = appInfo
								.getAvailableSubmissionLocationsForVersionAndFqan(
										version, fqan);
						for (final String subLoc : subLocs) {
							for (int i = 0; i < controller
									.getSameSubmissionLocation(); i++) {
								results.add(createGridTestElement(version,
										subLoc, fqan));
							}
						}
					}
				}
			} else {
				final String[] subLocs = GrisuRegistryManager
						.getDefault(controller.getServiceInterface())
						.getResourceInformation()
						.getAllAvailableSubmissionLocations(fqan);
				for (final String subLoc : subLocs) {
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

	public String getApplicationName() {
		return applicationName;
	}

	public GridTestController getController() {
		return controller;
	}

	public String getDescription() {
		return description;
	}

	// public Map<String, Set<String>> getSubmissionLocationsPerVersion() {
	// return subLocsPerVersions;
	// }

	public String[] getFqans() {
		return fqans;
	}

	public File getTestBaseDir() {
		return controller.getGridTestDirectory();
	}

	public String getTestname() {
		return testname;
	}

	public boolean isUseMds() {
		return useMds;
	}

}
