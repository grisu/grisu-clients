package org.vpac.grisu.clients.blender;

import java.util.List;

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
