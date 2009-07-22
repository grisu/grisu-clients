package org.vpac.grisu.grisuBatch.control;

import java.util.Set;

public class InvalidJobnamesException extends Exception {
	
	private Set<String> invalidJobnames = null;
	
	public InvalidJobnamesException(Set<String> invalidJobnames) {
		this.invalidJobnames = invalidJobnames;
	}

	public Set<String> getInvalidJobnames() {
		return invalidJobnames;
	}
	
	public String getLocalizedMessage(){
		StringBuffer message = new StringBuffer("The following jobnames are taken: ");
		
		for ( String name : invalidJobnames ) {
			message.append(name+" ");
		}
		return message.toString();
	}

}
