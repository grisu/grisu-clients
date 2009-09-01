package org.vpac.grisu.clients.blender;

import uk.co.flamingpenguin.jewel.cli.Option;

public interface BlenderCommandLineArgs {
	
	@Option(helpRequest = true, description = "this help text")
	boolean getHelp();

	@Option(shortName = "n", description = "the name of the job")
	public String getJobname();
	
	@Option(shortName = "u", description = "either the myproxy or shibboleth username. If the -i option is specified, shib auth is used. If this option is not present, the client tries to use a x509 certificate.")
	public String getUsername();
	public boolean isUsername();
	
	@Option(shortName = "i", description = "the name of the idp if shib auth is used")
	public String getIdp();
	public boolean isIdp();
	
	@Option(shortName = "k", description = "kill and clean possibly existing multijob with the specified jobname")
	public boolean isForceKill();
	
	@Option(description = "saves the credential that is used to login on the local machine so you don't need to provide login credentials for the next 24 hours")
	public boolean isSaveLocalProxy();
	
	@Option(description = "enable verbose log output")
	public boolean isVerbose();

}
