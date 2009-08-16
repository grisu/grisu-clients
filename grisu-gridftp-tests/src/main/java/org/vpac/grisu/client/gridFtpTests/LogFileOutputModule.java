package org.vpac.grisu.client.gridFtpTests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;



public class LogFileOutputModule implements OutputModule {

	private String output;
	
	public LogFileOutputModule(String output) {
		this.output = output;
	}
	
	public void writeTestElement(GridFtpTestElement element) {

		File parent = new File(output);
		parent.mkdir();

		StringBuffer outputString = new StringBuffer();

		outputString.append(element.getResultsForThisTest(false, true, false));

		try {

			String uFileName = output+File.separator+element.getTestName()+"_allResults.log";
			FileWriter fileWriter = new FileWriter(uFileName, true);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputString = new StringBuffer();

		outputString.append(element.getResultsForThisTest(true, true, false));

		try {

			String uFileName = output+File.separator+element.getTestName()+"_onlyFailed.log";
			FileWriter fileWriter = new FileWriter(uFileName, true);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(outputString.toString());

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		outputString = new StringBuffer();
		outputString.append(element.getResultsForThisTest(true, false, true));

		try {

			String uFileName = output+File.separator+element.getTestName()+"_shortFailed.log";
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

			File parent = new File(output);
			parent.mkdir();
			String uFileName = output+File.separator+"testSetup.log";
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
			File parent = new File(output);
			parent.mkdir();
			String uFileName = output+File.separator+"testStatistics.log";
			FileWriter fileWriter = new FileWriter(uFileName, true);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			buffWriter.write(statistic);

			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
