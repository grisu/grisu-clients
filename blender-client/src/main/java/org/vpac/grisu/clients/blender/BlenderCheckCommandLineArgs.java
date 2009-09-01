package org.vpac.grisu.clients.blender;

import java.io.File;

import uk.co.flamingpenguin.jewel.cli.Option;

public interface BlenderCheckCommandLineArgs extends BlenderCommandLineArgs {
	
	@Option(shortName = "s", description = "get overall status of the multipart job")
	public boolean isStatus();
	
	@Option(shortName = "j", description = "get detailed job status")
	public boolean isDetailed();

	@Option(shortName = "l", description = "wait for all jobs to finish. you can specify an optional check intervall time in minutes")
	public int getLoopUntilFinished();
	public boolean isLoopUntilFinished();
	
	@Option(shortName = "d", description = "download all output frames when job is finished")
	public File getDownloadResults();
	public boolean isDownloadResults();
	
}
