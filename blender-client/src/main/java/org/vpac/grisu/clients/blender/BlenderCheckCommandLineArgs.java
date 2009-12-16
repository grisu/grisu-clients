package org.vpac.grisu.clients.blender;

import java.io.File;

import uk.co.flamingpenguin.jewel.cli.Option;

public interface BlenderCheckCommandLineArgs extends BlenderCommandLineArgs {

	@Option(shortName = "d", description = "download all output frames when job is finished")
	public File getDownloadResults();

	@Option(shortName = "l", description = "wait for all jobs to finish. you need to specify an check-intervall time in minutes")
	public int getLoopUntilFinished();

	@Option(shortName = "j", description = "get detailed job status")
	public boolean isDetailed();

	public boolean isDownloadResults();

	public boolean isLoopUntilFinished();

	@Option(shortName = "s", description = "get overall status of the multipart job")
	public boolean isStatus();

}
