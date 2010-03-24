package org.vpac.grisu.clients.gridTests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;

public class LogFileOutputModule implements OutputModule {

	private String output;

	public LogFileOutputModule(String output) {
		this.output = output;
		File file = new File(output);
		if ( ! file.getParentFile().exists() ) {
			file.getParentFile().mkdirs();
			if ( ! file.getParentFile().exists() ) {
				System.out.println("Could not create folder: "+file.getParent());
				System.exit(1);
				
			}
		}
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
