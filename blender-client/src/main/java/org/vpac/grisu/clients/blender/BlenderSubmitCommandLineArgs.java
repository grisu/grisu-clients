package org.vpac.grisu.clients.blender;

import java.util.List;

import uk.co.flamingpenguin.jewel.cli.Option;

public interface BlenderSubmitCommandLineArgs extends BlenderCommandLineArgs {

	@Option(shortName = "b", description = "the blender file")
	public String getBlendFile();
	
	@Option(shortName = "F", description = "set the render format, Valid options are: TGA IRIS HAMX JPEG MOVIE IRIZ RAWTGA AVIRAW AVIJPEG PNG BMP FRAMESERVER")
	public String getRenderFormat();
	
	@Option(shortName = "s", description = "set start to frame <frame>")
	public int getStartFrame();
	
	@Option(shortName = "e", description = "set end to frame <frame> (use before the -a argument)")
	public int getEndFrame();
	
	@Option(shortName = "w", description = "the walltime for a single frame-render job in minutes. Use a high enough number so the job doesn't get killed on the cluster. But don't make it too high because your job might take longer to get started.")
	public int getWalltime();
	
	@Option(shortName = "v", description = "the vo to use to submit this job. Can be omitted if you are only member of one VO")
	public String getVo();
	
	@Option(description = "patterns of sitenames not to submit jobs to. Don't use that in conjunction with --include")
	public List<String> getExclude();
	public boolean isExclude();
	
	@Option(description = "patterns of sitenames to submit jobs to. Don't use that in conjunction with --exclude") 
	public List<String> getInclude();
	public boolean isInclude();

}
