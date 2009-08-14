package org.vpac.grisu.client.gridFtpTests;

import org.vpac.grisu.client.gridFtpTests.testElements.GridFtpTestElement;



public interface OutputModule {
	
//	public void writeTestsSetup(String setup);
	
	public void writeTestElement(GridFtpTestElement element, boolean onlyFailed, boolean showStackTrace);

//	public void writeTestsStatistic(String statistic);
	
}
