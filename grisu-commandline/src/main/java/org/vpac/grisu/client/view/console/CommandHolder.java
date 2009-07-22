package org.vpac.grisu.client.view.console;


import java.util.SortedSet;

import org.vpac.grisu.client.model.template.Command;

public interface CommandHolder {
	
	public SortedSet getAllCommands();
	
	public Command getCommand(String commandName);

}
