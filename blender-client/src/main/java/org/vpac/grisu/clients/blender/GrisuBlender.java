package org.vpac.grisu.clients.blender;

import org.apache.commons.lang.StringUtils;
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
		boolean help = false;
		for ( int i=0; i<args.length; i++ ) {
			
			if ( "-m".equals(args[i]) || "--mode".equals(args[i]) ) {
				mode = args[i+1];
			}
			
			if ( "-h".equals(args[i]) || "--help".equals(args[i]) ) {
				help = true;
			}
			
		}

		if ( mode == null && help == false ) {
			System.out.println("No mode specified. Please start this application with either \"-m submit\" or \"-m check\". To get help for each of those modes, also specify the \"-h\" option.");
			System.exit(1);
		} if ( mode == null && help == true ) {
			System.out.println("Please start this application with either \"-m submit\" or \"-m check\". To get help for each of those modes, also specify the \"-h\" option.");
			System.exit(1);
		} 
		
		BlenderMode blenderMode = null;
		
		if ( MODE.SUBMIT.toString().equalsIgnoreCase(mode) ) {
			blenderMode = new GridBlenderSubmit(args, help);
		} else if ( MODE.CHECK.toString().equalsIgnoreCase(mode) ) {
			blenderMode = new GridBlenderCheck(args, help);
		} else {
			System.out.println("Mode \""+mode+"\" not supported. Please start this application with either \"-m submit\" or \"-m check\". To get help for each of those modes, also specify the \"-h\" option.");
			System.exit(1);
		}
		
		blenderMode.execute();

	}

}
