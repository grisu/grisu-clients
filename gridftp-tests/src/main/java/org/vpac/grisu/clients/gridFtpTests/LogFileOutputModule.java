package org.vpac.grisu.clients.gridFtpTests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class LogFileOutputModule implements OutputModule {

	private final String output;

	public LogFileOutputModule(String output) {
		this.output = output;
	}

	public void writeTestElement(GridFtpTestElement element) {

		final File parent = new File(output);
		parent.mkdir();

		StringBuffer outputString = new StringBuffer();

		outputString.append(element.getResultsForThisTest(false, true, false));

		try {

			final String uFileName = output + File.separator
					+ element.getTestName() + "_allResults.log";
			final FileWriter fileWriter = new FileWriter(uFileName, true);
			final BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}

		outputString = new StringBuffer();

		outputString.append(element.getResultsForThisTest(true, true, false));

		try {

			final String uFileName = output + File.separator
					+ element.getTestName() + "_onlyFailed.log";
			final FileWriter fileWriter = new FileWriter(uFileName, true);
			final BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}

		outputString = new StringBuffer();
		outputString.append(element.getResultsForThisTest(true, false, true));

		try {

			final String uFileName = output + File.separator
					+ element.getTestName() + "_shortFailed.log";
			final FileWriter fileWriter = new FileWriter(uFileName, true);
			final BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTestsSetup(String setup) {
		try {

			final File parent = new File(output);
			parent.mkdir();
			final String uFileName = output + File.separator + "testSetup.log";
			final FileWriter fileWriter = new FileWriter(uFileName, true);
			final BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(setup);

			buffWriter.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTestsStatistic(String statistic) {
		try {
			final File parent = new File(output);
			parent.mkdir();
			final String uFileName = output + File.separator
					+ "testStatistics.log";
			final FileWriter fileWriter = new FileWriter(uFileName, true);
			final BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(statistic);

			buffWriter.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
