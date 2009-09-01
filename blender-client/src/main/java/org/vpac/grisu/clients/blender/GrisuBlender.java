package org.vpac.grisu.clients.blender;

import org.vpac.grisu.control.ServiceInterface;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.Cli;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

public class GrisuBlender {
	
	public enum MODE {
		SUBMIT,
		CHECK
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String mode = null;
		for ( int i=0; i<args.length; i++ ) {
			
			if ( "-m".equals(args[i]) || "--mode".equals(args[i]) ) {
				mode = args[i+1];
			}
			
		}
		
		BlenderMode blenderMode = null;
		
		if ( MODE.SUBMIT.toString().equalsIgnoreCase(mode) ) {
			blenderMode = new GridBlenderSubmit(args);
		} else if ( MODE.CHECK.toString().equalsIgnoreCase(mode) ) {
			blenderMode = new GridBlenderCheck(args);
		} else {
			System.out.println("Mode \""+mode+"\" not supported.");
			System.exit(1);
		}
		
		blenderMode.execute();

	}

}
