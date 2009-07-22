package org.vpac.grisu.client.model.template;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.exceptions.JobSubmissionException;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.templatePreProcessor.TemplatePreProcessorException;
import org.vpac.grisu.client.view.console.Grish;

public class GrisuTemplateCommand implements Command, JsdlTemplateListener {
	
	static final Logger myLogger = Logger.getLogger(GrisuTemplateCommand.class
			.getName());
	
	private Grish shell = null;
	private JsdlTemplate template = null;
	private String name = null;
	
	private SortedSet<String> argumentNames = null;
	
	public GrisuTemplateCommand(String name, JsdlTemplate template) {
		
		this.template = template;
		this.name = name;
		
		argumentNames = new TreeSet();
		
		for ( String argName : template.getTemplateNodes().keySet() ) {
			argumentNames.add(Command.ARGUMENT_PREFIX+argName);
		}
	}

	public SortedSet calculateCompletions(String commandSoFar) {
		
		String[] parts = commandSoFar.split("\\s");
		SortedSet result = new TreeSet();
		
		if ( ! name.equals(parts[0]) ) {
			myLogger.warn("This is the wrong command. This shouldn't happen.");
			return new TreeSet();
		}

		if ( commandSoFar.endsWith(" ") ) {
			// means new argument
			myLogger.debug("Last character was whitespace. Returning all arguments for command "+parts[0]);
			
			for ( String arg : argumentNames ) {
				myLogger.debug("Adding argument: "+arg);
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
			
			for ( String arg : argumentNames ) {
				if ( arg.startsWith(lastPart) ) {
					String temp = arg.substring(lastPart.length());
					myLogger.debug("Adding argument: "+temp);
					result.add(commandSoFar+(temp));
				}
			}
			return result;
		}
	}

	public void execute(String fqan) {

		try {
			template.addJsdlTemplateListener(this);
			template.startSubmission(fqan);
		} catch (JobSubmissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				template.reset(true);
			} catch (TemplatePreProcessorException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			} finally {
				template.removeJsdlTemplateListener(this);
		}
		
	}

	public String getName() {
		return name;
	}

	public void initialize(Grish shell, String command) throws CommandLineParseException {
		
		this.shell = shell;
		template.setDefaultTemplateNodeValueSetters();
		
		String[] commandTokens = command.split("\\s");
		
		for ( int i=1; i<commandTokens.length; i++ ) {
			if ( commandTokens[i].trim().startsWith(Command.ARGUMENT_PREFIX) ) {
				commandTokens[i] = commandTokens[i].substring(Command.ARGUMENT_PREFIX.length());
			}
			myLogger.debug("Found token: "+commandTokens[i]);
			TemplateNode node = template.getTemplateNodes().get(commandTokens[i]);
			if ( node == null ) {
				myLogger.warn("Didn't find TemplateNode for token \""+commandTokens[i]+"\". Ignoring input");
				continue;
			}
			try {
				node.getTemplateNodeValueSetter().setExternalSetValue(commandTokens[i+1]);
			} catch (Exception e) {
				throw new CommandLineParseException("Could not set value for argument: "+Command.ARGUMENT_PREFIX+commandTokens[i]);
			}
		}
		
		List<TemplateNode> notReadyNode = new LinkedList<TemplateNode>();
		for ( TemplateNode node : template.getTemplateNodes().values() ) {
			if ( ! node.isReady() ) {
				myLogger.debug("Template node "+node.getName()+" not ready.");
				notReadyNode.add(node);
			}
		}
		
		if ( notReadyNode.size() > 0 ) {
			StringBuffer message = new StringBuffer();
			message.append("Could not submit job. Missing/wrong input for arguments: ");
			for ( TemplateNode node : notReadyNode ) {
				message.append(Command.ARGUMENT_PREFIX+node.getName()+" ");
			}
			throw new CommandLineParseException(message.toString());
		}
		
	}

	public void submissionExceptionOccured(JsdlTemplateEvent event,
			JobSubmissionException exception) {
		// TODO Auto-generated method stub
		
	}

	public void templateStatusChanged(JsdlTemplateEvent event) {
		
		shell.printMessage(event.getMessage());
		
	}

}
