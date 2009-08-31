package org.vpac.grisu.client.gridTests;

import org.vpac.grisu.client.gridTests.testElements.GridTestElement;



public interface OutputModule {
	
	public void writeTestsSetup(String setup);
	
	public void writeTestElement(GridTestElement element);

	public void writeTestsStatistic(String statistic);
	
}
