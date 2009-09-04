package org.vpac.grisu.clients.gridTests;

import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;



public interface OutputModule {
	
	public void writeTestsSetup(String setup);
	
	public void writeTestElement(GridTestElement element);

	public void writeTestsStatistic(String statistic);
	
}
