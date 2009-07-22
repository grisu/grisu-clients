package org.vpac.grisu.client.view.console;


import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.Completor;
import jline.FileNameCompletor;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.template.Command;

public class GrisuCompletor implements Completor {
	
	static final Logger myLogger = Logger.getLogger(GrisuCompletor.class
			.getName());
	
	public static final String LOCAL_FILE_INIDICATOR = "file:";

	private CommandHolder holder = null;

	public GrisuCompletor(CommandHolder holder) {

		this.holder = holder;
	}

	public int complete(String buffer, int cursor, List clist) {

		String delimiter = null;

		String start = (buffer == null) ? "" : buffer;

//		int startIndex = -1;

//		try {
//			startIndex = buffer.lastIndexOf(" ");
//		} catch (Exception e) {
//			System.out.println(e.getLocalizedMessage());
//			e.printStackTrace();
//		}
		SortedSet candidates = null;

		if (buffer == null && "".equals(buffer)) {
			candidates = holder.getAllCommands();
		} else {

			String[] userInput = buffer.split("\\s");

			// if first argument not finished yet
			if ( userInput.length == 1 && ! buffer.endsWith(" ") ) {
				candidates = holder.getAllCommands();
			} else {
				candidates = new TreeSet();
				String command = userInput[0];
				String lastArg = userInput[userInput.length-1];
				
				// arguments
				if ( buffer.endsWith(" ") || lastArg.startsWith(Command.ARGUMENT_PREFIX.substring(0,1)) ) {
					candidates.addAll(getArgumentCompletions(command, buffer));
				}
				
				// localFiles
				if ( buffer.endsWith(" ") || couldBeLocalFile(lastArg) ) {
					if ( buffer.endsWith(" ") ) {
						candidates.add(buffer+LOCAL_FILE_INIDICATOR+File.separator);
					} else if ( lastArg.length() <= LOCAL_FILE_INIDICATOR.length() ) {
						candidates.add(buffer.substring(0, buffer.lastIndexOf(" ")+1)+LOCAL_FILE_INIDICATOR+File.separator);
					} else {
						candidates.addAll(getLocalFileCompletions(buffer, lastArg));
					}
				}
				
				
				// remoteFiles
				
				// GrisuCommands
				
//				candidates.addAll(argCandidates);
			}
		}

//		for ( Object cand : candidates ) {
//			myLogger.debug("Candidate after calculating: "+(String)cand);
//		}
		
		SortedSet matches = candidates.tailSet(start);
//		for ( Object cand : candidates ) {
//			myLogger.debug("Candidate after tail set: "+(String)cand);
//		}
		for (Iterator i = matches.iterator(); i.hasNext();) {
			String can = (String) i.next();
//			myLogger.debug("Checking candidate: "+can);
			if (!(can.startsWith(start))) {
//				myLogger.debug("Candidate doesn't start with: "+start+". Not considering it.");
				break;
			}

			if (delimiter != null) {
				int index = can.indexOf(delimiter, cursor);

				if (index != -1) {
					can = can.substring(0, index + 1);
				}
			}
//			myLogger.debug("Adding candidate "+can);
			clist.add(can);
		}

		if (clist.size() == 1) {
			if ( ((String)clist.get(0)).endsWith(java.io.File.separator) ) {
				clist.set(0, ((String) clist.get(0)));
			} else {
				clist.set(0, ((String) clist.get(0)) + " ");
			}
		}

		// the index of the completion is always from the beginning of
		// the buffer.
		return (clist.size() == 0) ? (-1) : 0;

	}
	
	private boolean couldBeLocalFile(String argSoFar) {
		
		int length = argSoFar.length();
		
		if ( length <= LOCAL_FILE_INIDICATOR.length() ) {
			return LOCAL_FILE_INIDICATOR.startsWith(argSoFar);
		} else {
			return argSoFar.startsWith(LOCAL_FILE_INIDICATOR);
		}
		
		
	}
	
	private SortedSet getArgumentCompletions(String command, String buffer) {
		
		Command currentCommand = holder.getCommand(command);
		myLogger.debug("Calculating completions for: "+currentCommand.getName());
		
		SortedSet result = currentCommand.calculateCompletions(buffer);
		
		for (Object arg : result ) {
			myLogger.debug("Adding arg completion: "+(String)arg);
		}
		
		return result;
	}
	
	private SortedSet getLocalFileCompletions(String buffer, String arg) {
		
//		myLogger.debug("Checking completion for buffer: "+buffer+" and argument: "+arg);
		String realArg = arg.substring(LOCAL_FILE_INIDICATOR.length());
		
//		myLogger.debug("Finding local file completions for: "+realArg);
		List temp = new LinkedList();
		new FileNameCompletor().complete(realArg, 0, temp);
		
		SortedSet result = new TreeSet();
		for ( Object cand : temp ) {
			String candidate = ((String) cand).trim();
			
			int index = buffer.lastIndexOf(File.separator);
			if ( index == -1 ) {
				
			} else {
				result.add(buffer.subSequence(0, index+1)+candidate);
			}
			
		}
		
		return result;
		
	}
	
//	private SortedSet getLocalFileCompletions(String buffer, String arg) {
//		
//		myLogger.debug("Checking completion for buffer: "+buffer+" and argument: "+arg);
//		int startFileIndex = -1;
//		
//		if ( arg == null || arg.trim().length() == 0 ) {
//			startFileIndex = buffer.length();
//			arg = LOCAL_FILE_INIDICATOR+"/";
//		} else {
//			if ( buffer.endsWith(arg) ) {
//				startFileIndex = buffer.lastIndexOf(arg);
//			} else {
//				//TODO
//				myLogger.error("Couldn't calculate startFileIndex.");
//				startFileIndex = buffer.length();
//			}
//		}
//		if ( arg.length() <= LOCAL_FILE_INIDICATOR.length() ) {
//			arg = LOCAL_FILE_INIDICATOR+"/";
//		}
//		arg = arg.substring(LOCAL_FILE_INIDICATOR.length());
//		
//		
//		myLogger.debug("Finding local file completions for: "+arg);
//		List temp = new LinkedList();
//		new FileNameCompletor().complete(arg, 0, temp);
//		myLogger.debug("Found "+temp.size()+" local file completions.");
//
//		myLogger.debug("Buffer: "+ buffer);
//		
//		SortedSet result = new TreeSet();
//		for ( Object file : temp ) {
//			String string = buffer.substring(0, startFileIndex)+LOCAL_FILE_INIDICATOR+"/"+((String)file).trim();
////			String string = buffer+(String)file;
//			myLogger.debug("Adding local file completion: "+string);
//			result.add(string);
//		}
//		return result;
//	}

}
