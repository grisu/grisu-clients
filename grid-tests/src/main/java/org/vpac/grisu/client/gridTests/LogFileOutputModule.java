package org.vpac.grisu.client.gridTests;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.vpac.grisu.client.gridTests.testElements.GridTestElement;


public class LogFileOutputModule implements OutputModule {

	private String output;
	
	public LogFileOutputModule(String output) {
		this.output = output;
	}
	
	public void writeTestElement(GridTestElement element) {

		StringBuffer outputString = new StringBuffer();

		
		outputString.append(OutputModuleHelpers.createStringReport(element));

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
