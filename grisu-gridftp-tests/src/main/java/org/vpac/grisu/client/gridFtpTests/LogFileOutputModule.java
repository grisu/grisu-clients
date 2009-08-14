package org.vpac.grisu.client.gridFtpTests;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.vpac.grisu.client.gridFtpTests.testElements.GridFtpTestElement;


public class LogFileOutputModule implements OutputModule {

	private String output;
	
	public LogFileOutputModule(String output) {
		this.output = output;
	}
	
	public void writeTestElement(GridFtpTestElement element, boolean onlyFailed, boolean showStackTrace) {

		StringBuffer outputString = new StringBuffer();

		outputString.append(element.getResultsForThisTest(onlyFailed, showStackTrace));

		try {

			String uFileName = output;
			FileWriter fileWriter = new FileWriter(uFileName, true);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTestsSetup(String setup) {
		try {

			String uFileName = output;
			FileWriter fileWriter = new FileWriter(uFileName, true);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(setup);

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeTestsStatistic(String statistic) {
		try {

			String uFileName = output;
			FileWriter fileWriter = new FileWriter(uFileName, true);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(statistic);

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
