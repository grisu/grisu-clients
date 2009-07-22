package org.vpac.grisu.client.model.template;

import java.util.SortedSet;

import org.vpac.grisu.client.view.console.Grish;

public interface Command {
	
	public static final String ARGUMENT_PREFIX = "--";

	public void initialize(Grish shell, String command) throws CommandLineParseException;
	
	public void execute(String fqan);
	
	public String getName();
	
	public SortedSet calculateCompletions(String commandSoFar);
	
}
