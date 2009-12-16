package org.vpac.grisu.clients.blender;

import uk.co.flamingpenguin.jewel.cli.Option;

public interface BlenderCommandLineArgs {

	@Option(shortName = "h", helpRequest = true, description = "this help text or the help text for the mode you choose")
	boolean getHelp();

	@Option(shortName = "i", description = "the name of the idp if shib auth is used")
	public String getIdp();

	@Option(shortName = "n", description = "the name of the job")
	public String getJobname();

	@Option(shortName = "m", description = "the mode to use. Choose either: submit|check")
	public String getMode();

	@Option(shortName = "u", description = "either the myproxy or shibboleth username. If the -i option is specified, shib auth is used. If this option is not present, the client tries to use a x509 certificate.")
	public String getUsername();

	@Option(shortName = "k", description = "kill and clean possibly existing multijob with the specified jobname")
	public boolean isForceKill();

	public boolean isIdp();

	public boolean isJobname();

	@Option(description = "saves the credential that is used to login on the local machine so you don't need to provide login credentials for the next 24 hours")
	public boolean isSaveLocalProxy();

	public boolean isUsername();

	@Option(description = "enable verbose log output")
	public boolean isVerbose();

}
