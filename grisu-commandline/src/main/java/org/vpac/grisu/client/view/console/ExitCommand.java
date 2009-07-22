package org.vpac.grisu.client.view.console;

import java.util.SortedSet;

import org.vpac.grisu.client.model.template.Command;
import org.vpac.grisu.client.model.template.CommandLineParseException;

public class ExitCommand implements Command {

	private Grish shell = null;
	private String name = null;

	public ExitCommand(String name) {
		this.name = name;
	}
	
	public SortedSet calculateCompletions(String commandSoFar) {
		return null;
	}

	public void execute(String fqan) {
		shell.printMessage("exiting");
		System.exit(0);
	}

	public String getName() {
		return this.name;
	}

	public void initialize(Grish shell, String command)
			throws CommandLineParseException {
		
		this.shell = shell;
	}

}
