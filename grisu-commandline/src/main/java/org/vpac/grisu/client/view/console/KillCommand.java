package org.vpac.grisu.client.view.console;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.template.Command;
import org.vpac.grisu.client.model.template.CommandLineParseException;

public class KillCommand implements Command {
	
	private EnvironmentManager em = null;
	
	public KillCommand(EnvironmentManager em) {
		this.em = em;
	}

	public SortedSet calculateCompletions(String commandSoFar) {
		
		
		String[] parts = commandSoFar.split("\\s");
		SortedSet result = new TreeSet();
		
		Set<String> runningJobs = em.getJobManager().getAllJobnames(false);

		if ( commandSoFar.endsWith(" ") ) {
			for ( String arg : runningJobs ) {
				result.add(commandSoFar+(arg));
			}
			return result;
		} else {			
			
			int lastIndex = parts.length-1;
			String lastPart = parts[lastIndex];
			
//			if ( arguments.indexOf(lastPart) != -1 ) {
//				myLogger.debug("Last part is complete argument.");
//				//TODO handle that
//			}
			
			for ( String arg : runningJobs ) {
				if ( arg.startsWith(lastPart) ) {
					String temp = arg.substring(lastPart.length());
//					myLogger.debug("Adding argument: "+temp);
					result.add(commandSoFar+(temp));
				}
			}
			return result;
			
		}
		
	}

	public void execute(String fqan) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "kill";
	}

	public void initialize(Grish shell, String command)
			throws CommandLineParseException {
		// TODO Auto-generated method stub

	}

}
