package org.vpac.grisu.client.view.console;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jline.Completor;
import jline.ConsoleReader;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.ServiceInterfaceFactoryOld;
import org.vpac.grisu.client.model.template.Command;
import org.vpac.grisu.client.model.template.CommandLineParseException;
import org.vpac.grisu.client.model.template.GrisuTemplateCommand;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.ServiceInterfaceException;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.MyProxyServerParams;

public class Grish implements CommandHolder {
	
	static final Logger myLogger = Logger.getLogger(Grish.class
			.getName());

	
	private static ResourceBundle shellMessages = ResourceBundle.getBundle(
			"GrisuShellMessageBundle", java.util.Locale.getDefault());
	
	private ConsoleReader consoleReader = null;
	
	EnvironmentManager environmentManager = null;
	ServiceInterface serviceInterface = null;
	
	Map<String, Command> availableCommands = new TreeMap<String, Command>();

	public Grish() throws IOException {
		
		consoleReader = new ConsoleReader();
		
		String url_to_connect = null;

		do {
			
		printMessage("listPreviouslyUsedServiceInterfaces");
		
		String defaultInterface = ClientPropertiesManager.getDefaultServiceInterfaceUrl();
		int defaultIndex = -1;
		
		int i = 1;
		Map<Integer, String> urlMap = new HashMap<Integer, String>();
		for ( String url : ClientPropertiesManager.getServiceInterfaceUrls() ) {
			printMessage("["+ i +"] "+url);
			urlMap.put(i, url);
			if ( url.equals(defaultInterface) ) {
				defaultIndex = i;
			}
			i++;
		}
		
		if ( i == 1 ) {
			printMessage("[1] "+ ClientPropertiesManager.DEFAULT_SERVICE_INTERFACE);
			urlMap.put(1, ClientPropertiesManager.DEFAULT_SERVICE_INTERFACE);
		}
		
		newLine();
		printMessage("[0] "+shellMessages.getString("exitGrisuShell"));
		newLine();

		String serviceInterfaceUrl = null;
		if ( defaultIndex != -1 ) {
			serviceInterfaceUrl = readUserInput(shellMessages.getString("askServiceInterface")+" ["+defaultIndex+"]");
		} else {
			serviceInterfaceUrl = readUserInput("askServiceInterface");
		}
		
		if ( "".equals(serviceInterfaceUrl) ) {
			serviceInterfaceUrl = new Integer(defaultIndex).toString();
		}
		
		if ( "0".equals(serviceInterfaceUrl.trim()) ) {
			printMessage("exiting");
			System.exit(0);
		}
		
		try {
			int userInput = new Integer(serviceInterfaceUrl);
			if ( userInput > 0 && userInput < i ) {
				url_to_connect = urlMap.get(userInput);
			}
		} catch (NumberFormatException e) {
			if ( serviceInterfaceUrl.startsWith("http://") || serviceInterfaceUrl.startsWith("https://") ) {
				url_to_connect = serviceInterfaceUrl;
			}
		}
		
		if ( url_to_connect == null ) {
			printMessage("noValidInput");
			newLine();
		}
		
		} while ( url_to_connect == null );

		String lastUsername = MyProxyServerParams.loadDefaultMyProxyUsername();
		
		String username = null;
		if ( lastUsername == null || "".equals(lastUsername) ) {
			username = readUserInput("askMyProxyUsername");
		} else {
			username = readUserInput(shellMessages.getString("askMyProxyUsername")+" ["+lastUsername+"]");
		}
		
		if ( "".equals(username) ) {
			username = lastUsername;
		}
		
		char[] password = readSecretUserInput("askMyProxyPassword");
		
		LoginParams params = new LoginParams(
				url_to_connect, username,
				password, "myproxy.arcs.org.au", "443");
		
		try {
			serviceInterface = ServiceInterfaceFactoryOld.createInterface(params);
		} catch (ServiceInterfaceException e1) {
			printErrorMessage(e1.getLocalizedMessage());
			printErrorMessage("exiting");
			System.exit(1);
		}
		
		if ( serviceInterface == null ) {
			printErrorMessage("couldNotConnectToServiceInterface");
			printErrorMessage("exiting");
			System.exit(1);
		}
		
		try {
			serviceInterface.login(username, new String(password));
			printMessage("connected");
			
			MyProxyServerParams.saveDefaultMyProxyUsername(username);
			ClientPropertiesManager.setDefaultServiceInterfaceUrl(url_to_connect);
			
			printMessage("retrievingEnvironment");
//			EnvironmentManager.setDefaultServiceInterface(serviceInterface);
			environmentManager = new EnvironmentManager(serviceInterface);
			
			fillCommands();
			
			consoleReader.setCompletionHandler(new GrisuCompletionHandler());
			
			Completor comp = new GrisuCompletor(this);
			consoleReader.addCompletor(comp);
			
		} catch (Exception e) {
			printErrorMessage("couldNotConnectToServiceInterface");
			printErrorMessage(e.getLocalizedMessage());
			printErrorMessage("exiting");
			System.exit(1);
		}
		
		newLine();
		
		
	}
	
	private void fillCommands() {

		for ( String templateName : environmentManager.getTemplateManager().getServerTemplates().keySet() ) {
			// we'll retrieve the template as we need it later
			availableCommands.put(templateName, null);
		}

		for ( String templateName : environmentManager.getTemplateManager().getLocalTemplates().keySet() ) {
			// we'll retrieve the template as we need it later
			availableCommands.put(templateName, null);
		}
		
		Command ps = new PsCommand(environmentManager);
		availableCommands.put(ps.getName(), ps);
		
		Command exit = new ExitCommand("exit");
		availableCommands.put(exit.getName(), exit);
		
		Command quit = new ExitCommand("quit");
		availableCommands.put(quit.getName(), quit);
		
		Command kill = new KillCommand(environmentManager);
		availableCommands.put(kill.getName(), kill);
		
		for ( String com : availableCommands.keySet() ) {
			myLogger.debug("Added command: "+com);
		}

	}
	
	public SortedSet getAllCommands() {
		return new TreeSet(availableCommands.keySet());
	}
	
	public Command getCommand(String commandName) {
		
		Command command = availableCommands.get(commandName);
		
		if ( command == null ) {

			myLogger.debug("Retrieving command "+commandName);
			
			JsdlTemplate template = environmentManager.getTemplateManager().getTemplate(commandName);

			GrisuTemplateCommand grisuCommand = new GrisuTemplateCommand(commandName, template);

			availableCommands.put(commandName, grisuCommand);
		}
		
		return availableCommands.get(commandName);
	}
	
	/**
	 * Reads input from the commandline
	 * @param message the message to show to the user
	 * @return the userinput of null if the reader could not get the input
	 */
	private String readUserInput(String messagekey) {
		
		String messageToDisplay = null; 
		try {
			messageToDisplay = shellMessages.getString(messagekey);
		} catch (Exception e) {
			messageToDisplay = messagekey;
		}
		
		if ( messageToDisplay == null || "".equals(messageToDisplay) ) {
			messageToDisplay = messagekey;
		}
		
		String userInput;
		try {
			userInput = consoleReader.readLine(messageToDisplay+": ");
		} catch (IOException e) {
			return null;
		}
		return userInput;
	}
	
	private char[] readSecretUserInput(String messagekey) {
		
		String messageToDisplay = null; 
		try {
			messageToDisplay = shellMessages.getString(messagekey);
		} catch (Exception e) {
			messageToDisplay = messagekey;
		}
		
		if ( messageToDisplay == null || "".equals(messageToDisplay) ) {
			messageToDisplay = messagekey;
		}
		
		char[] userInput;
		try {
			userInput = consoleReader.readLine(messageToDisplay+": ", new Character('*')).toCharArray();
		} catch (IOException e) {
			return null;
		}
		return userInput;
	}
	
	public void printMessage(String messageKey) {
		
		String messageToDisplay = null; 
		try {
			messageToDisplay = shellMessages.getString(messageKey);
		} catch (Exception e) {
			messageToDisplay = messageKey;
		}
		
		if ( messageToDisplay == null || "".equals(messageToDisplay) ) {
			messageToDisplay = messageKey;
		}
		System.out.println(messageToDisplay);
	}
	
	
	public void printErrorMessage(String messageKey) {
		
		String messageToDisplay = null; 
		try {
			messageToDisplay = shellMessages.getString(messageKey);
		} catch (Exception e) {
			messageToDisplay = messageKey;
		}
		
		if ( messageToDisplay == null || "".equals(messageToDisplay) ) {
			messageToDisplay = messageKey;
		}
		System.err.println(messageToDisplay);
	}
	private void newLine() {
		System.out.println();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		Grish grisuShell = null;

		try {
			grisuShell = new Grish();
		} catch (IOException e) {
			System.out.println("couldNotInitConsole");
			System.exit(1);
		}
		
		String commandLine = null;
		
		while ( !"exit".equals(commandLine) ) {
			commandLine = grisuShell.readUserInput("# ");
			String[] commandTokens = commandLine.split("\\s");
			if ( commandTokens.length < 1 ) {
				continue;
			} else {
				Command command = grisuShell.getCommand(commandTokens[0].trim());
				if ( command == null ) {
					grisuShell.printErrorMessage(shellMessages.getString("couldNotFindCommand")+": "+commandTokens[0]);
					continue;
				}
				try {
					myLogger.debug("Initializing job for: "+commandLine);
					command.initialize(grisuShell, commandLine);
				} catch (CommandLineParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myLogger.debug("Executing job.");
				command.execute("/APACGrid/NGAdmin");
			}
			
		}

		grisuShell.printMessage("exiting");
	}

}
