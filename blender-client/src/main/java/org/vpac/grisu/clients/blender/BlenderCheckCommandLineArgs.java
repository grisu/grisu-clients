package org.vpac.grisu.clients.blender;

import uk.co.flamingpenguin.jewel.cli.Option;

public interface BlenderCheckCommandLineArgs extends BlenderCommandLineArgs {
	
	@Option(shortName = "s", description = "get the status of the job")
	public boolean isStatus();

}
