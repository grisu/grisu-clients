package org.vpac.grisu.clients.gridTests;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;

public class OutputModuleHelpers {

	public static StringBuffer createStatisticsString(
			Collection<GridTestElement> elements) {

		StringBuffer statistics = new StringBuffer();
		statistics.append("\nSummary:\n-------------\n");
		int countFailed = 0;
		int countSuccess = 0;
		int countInterrupted = 0;
		StringBuffer failedSubLocs = new StringBuffer();
		for (GridTestElement gte : elements) {
			if (gte.wasInterrupted()) {
				countInterrupted = countInterrupted + 1;
			} else if (gte.failed()) {
				countFailed = countFailed + 1;
				failedSubLocs
						.append("\t" + gte.getSubmissionLocation() + ":\n");
				for (Exception e : gte.getExceptions()) {
					failedSubLocs.append(e.getLocalizedMessage() + "\n");
				}
			} else {
				countSuccess = countSuccess + 1;
			}
		}
		statistics.append("Total jobs:\t" + elements.size() + "\n");
		statistics.append("Successful jobs:\t" + countSuccess + "\n");
		statistics.append("Failed jobs\t: " + countFailed + "\n");
		statistics.append("Interrupted jobs\t: " + countInterrupted + "\n");
		if (countFailed > 0) {
			statistics.append("Failed submission locations: " + "\n");
			statistics.append(failedSubLocs.toString() + "\n");
		}

		statistics.append("Results per test:\n");

		Map<String, Set<GridTestElement>> testMap = getTestElementMap(elements);

		for (String test : testMap.keySet()) {
			statistics.append("Testname: " + test + "\n");
			int failed = 0;
			int success = 0;
			int interrupted = 0;
			for (GridTestElement gte : testMap.get(test)) {
				if (gte.wasInterrupted()) {
					interrupted = interrupted + 1;
				} else if (gte.failed()) {
					failed = failed + 1;
				} else {
					success = success + 1;
				}
			}
			statistics.append("\tTotal jobs:\t" + testMap.get(test).size());
			statistics.append("\tSuccessful jobs:\t" + success);
			statistics.append("\tFailed jobs:\t" + failed);
			statistics.append("\tInterrupted jobs:\t" + interrupted);
			statistics.append("\n");

		}

		return statistics;
	}

	public static StringBuffer createStringReport(GridTestElement gte) {

		StringBuffer outputString = new StringBuffer();

		outputString.append("Test for "
				+ gte.getTestInfo().getApplicationName() + ", version: "
				+ gte.getVersion());

		outputString.append("\nSubmissionLocation: "
				+ gte.getSubmissionLocation() + "\n");

		outputString.append("Fqan: " + gte.getFqan() + "\n");

		String resultString = null;
		if (gte.wasInterrupted()) {
			resultString = "Result: Interrupted.";
		} else if (gte.failed()) {
			resultString = "Result: Failed.";
		} else {
			resultString = "Result: Success.";
		}

		outputString.append(resultString + "\n");

		outputString.append("-------------------------------------------------"
				+ "\n");
		outputString.append("Started: " + gte.getStartDate().toString() + "\n");
		outputString.append("Ended: " + gte.getEndDate().toString() + "\n");
		outputString.append("-------------------------------------------------"
				+ "\n");
		outputString.append(gte.getResultString() + "\n");
		outputString.append("-------------------------------------------------"
				+ "\n");

		return outputString;
	}

	public static StringBuffer createTestSetupString(
			Collection<GridTestElement> elements) {

		StringBuffer setup = new StringBuffer();
		setup.append("Initialized jobs:\n");
		// setup.append(StringUtils.join(gridTestElements.values(), "\n")+"\n");

		Map<String, Set<GridTestElement>> testMap = getTestElementMap(elements);

		for (String testname : testMap.keySet()) {
			setup.append("Testname: " + testname + "\n\n");
			setup.append("Description: "
					+ testMap.get(testname).iterator().next().getTestInfo()
							.getDescription() + "\n\n");
			setup.append("Jobs to run for this test category:\n");
			for (GridTestElement el : testMap.get(testname)) {
				setup.append("\t" + el.toString() + "\n");
				setup.append("\tid: " + el.getTestId() + "\n");
			}
			setup.append("\n\n");
		}

		return setup;
	}

	private static Map<String, Set<GridTestElement>> getTestElementMap(
			Collection<GridTestElement> elements) {

		Map<String, Set<GridTestElement>> testMap = new TreeMap<String, Set<GridTestElement>>();

		for (GridTestElement element : elements) {

			if (testMap.get(element.getTestInfo().getTestname()) == null) {
				Set<GridTestElement> elementList = new TreeSet<GridTestElement>();
				testMap.put(element.getTestInfo().getTestname(), elementList);
			}

			testMap.get(element.getTestInfo().getTestname()).add(element);
		}
		return testMap;
	}

}
