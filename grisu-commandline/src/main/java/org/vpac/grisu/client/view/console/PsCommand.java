package org.vpac.grisu.client.view.console;

import java.util.SortedSet;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.model.template.Command;
import org.vpac.grisu.client.model.template.CommandLineParseException;

public class PsCommand implements Command {

	private EnvironmentManager em = null;
	private Grish shell = null;
	
	public PsCommand(EnvironmentManager em) {
		this.em = em;
	}
	
	public SortedSet calculateCompletions(String commandSoFar) {
		// nothing to do here
		return null;
	}

	public void execute(String fqan) {
		
		//em.getGlazedJobManagement().refreshAllJobs();

		
		for ( GrisuJobMonitoringObject job : em.getJobManager().getAllJobs(false) ) {
			shell.printMessage(constructJobStatusLine(job));
		}
		
	}

	private String constructJobStatusLine(GrisuJobMonitoringObject job) {
		StringBuffer output = new StringBuffer();
		
		output.append(job.getName()+"\t\t");
		output.append(job.getStatus());
		
		return output.toString();
	}
	
	public String getName() {
		return "ps";
	}

	public void initialize(Grish shell, String command) throws CommandLineParseException {
		this.shell = shell;
	}

}
