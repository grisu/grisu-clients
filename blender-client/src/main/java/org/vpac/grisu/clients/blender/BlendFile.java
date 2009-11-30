package org.vpac.grisu.clients.blender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class BlendFile {

	public static String STARTFRAME_KEY = "StartFrame";
	public static String ENDFRAME_KEY = "EndFrame";
	public static String RESOURCE_KEY = "Resource";

	private File file = null;
	private String blendFilePath;
	private String blendCacheFolderPath;
	private String fluidsFolderPath;

	private int startFrame = 0;
	private int endFrame = -1;

	private Map<File, String> referrencedFiles = new HashMap<File, String>();
	private int dirLevelsToInclude = 0;
	
	private File fluidsFolder;
	
	public BlendFile(File blendFile, File fluidsFolder) throws FileNotFoundException {

		this.file = blendFile;
		this.fluidsFolder = fluidsFolder;
		
		if ( ! this.file.exists() ) {
			throw new FileNotFoundException("Couldn't find .blend file.");
		}
		if ( ! this.file.isFile() ) {
			throw new FileNotFoundException(".blend file not a file.");
		}
		
		if ( this.fluidsFolder != null && ! this.fluidsFolder.exists() ) {
			throw new FileNotFoundException("Couldn't find fluids folder.");
		}
		if ( this.fluidsFolder != null && ! this.fluidsFolder.isDirectory() ) {
			throw new FileNotFoundException("Fluids folder not a directory.");
		}
		
		getAllReferencedResources();

	}

	/**
	 * Returns the path of one File relative to another.
	 * 
	 * @param target
	 *            the target directory
	 * @param base
	 *            the base directory
	 * @return target's path relative to the base directory
	 * @throws IOException
	 *             if an error occurs while resolving the files' canonical names
	 */
	public String getRelativePathToBLendFile(File target)
			throws IOException {
		String[] baseComponents = file.getParentFile().getCanonicalPath().split(
				Pattern.quote(File.separator));
		String[] targetComponents = target.getCanonicalPath().split(
				Pattern.quote(File.separator));

		// skip common components
		int index = 0;
		for (; index < targetComponents.length && index < baseComponents.length; ++index) {
			if (!targetComponents[index].equals(baseComponents[index]))
				break;
		}
		int tempLevel = 0;
		StringBuilder result = new StringBuilder();
		if (index != baseComponents.length) {
			// backtrack to base directory
			for (int i = index; i < baseComponents.length; ++i) {
				result.append(".." + File.separator);
				tempLevel = tempLevel + 1;
			}
			if ( tempLevel > dirLevelsToInclude ) {
				dirLevelsToInclude = tempLevel;
			}
		}
		for (; index < targetComponents.length; ++index)
			result.append(targetComponents[index] + File.separator);
		if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
			// remove final path separator
			result.delete(result.length() - "/".length(), result.length());
		}
		return result.toString();
	}

	private void getAllReferencedResources() {

		try {
			String command = "blender -b " + file.getPath() + " -P "
					+ GrisuBlenderJob.BLENDER_RESOURCE_PYTHYON_SCRIPT.getPath();
			System.out.println("Executing: " + command);
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read the output from the command
			String s;
			while ((s = stdInput.readLine()) != null) {

				if (s.startsWith(STARTFRAME_KEY)) {
					startFrame = Integer.parseInt(s.split(" ")[1]);
				} else if (s.startsWith(ENDFRAME_KEY)) {
					endFrame = Integer.parseInt(s.split(" ")[1]);
				} else if (s.startsWith(RESOURCE_KEY)) {

					File reference = new File(s.split(" ")[1]);

					String relPath = getRelativePathToBLendFile(reference);
					referrencedFiles.put(reference, relPath);
				}
			}
			
			if ( fluidsFolder != null ) {
				fluidsFolderPath = getRelativePathToBLendFile(fluidsFolder);
			}			
			
			blendFilePath = file.getName(); 
			blendCacheFolderPath = getBlendCacheFolderName();

			File parent = file.getParentFile();
			// now calculate the folder structure on the resources...
			for ( int i=0; i<dirLevelsToInclude; i++ ) {
				parent = parent.getParentFile();
				blendFilePath = parent.getName() + "/" + blendFilePath;
				blendCacheFolderPath = parent.getName() + "/" + blendCacheFolderPath;
			}
			
			
			for ( File f : referrencedFiles.keySet() ) {
				//TODO fix for windows 
				referrencedFiles.put(f, referrencedFiles.get(f).replace("../", ""));
			}
			

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			stdInput.close();
			stdError.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public File getFile() {
		return file;
	}

	public int getStartFrame() {
		return startFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public Map<File, String> getReferrencedFiles() {
		return referrencedFiles;
	}
	

	public String getRelativeBlendFilePath() {
		return blendFilePath;
	}
	
	public String getRelativeBlendCacheFolderPath() {
		return blendCacheFolderPath;
	}
	
	public String getBlendCacheFolderName() {
		return "blendcache_"+file.getName().substring(0, file.getName().lastIndexOf("."));
	}
	
	public File getBlendCacheFolder() {
		
		File result = new File(file.getParent(), getBlendCacheFolderName());
		
		if ( ! result.exists() ) {
			return null;
		}
		
		return result;
		
	}
	
	public boolean hasBlendCacheFolder() {
		
		if ( getBlendCacheFolder() == null ) {
			return false;
		} else {
			return true;
		}
	}
	
	public String getFluidsFolderPath() {
		return this.fluidsFolderPath;
	}
	
	public File[] getFluidFiles(int userStartFrame, int userEndFrame) {
		
		if ( userStartFrame <= 0 ) {
			userStartFrame = startFrame;
		}
		if ( userEndFrame <= 0 ) {
			userEndFrame = endFrame;
		}
		
		if ( fluidsFolder == null ) {
			return new File[]{};
		}
		
		Set<File> files = new HashSet<File>();
		for ( int i=userStartFrame-2; i<=userEndFrame+1; i++ ) {
			
			String formatted = String.format("%04d", i);
			for ( File file : fluidsFolder.listFiles() ) {
				if ( file.getName().indexOf(formatted) >= 0 ) {
					files.add(file);
				}
			}
			
		}
		
		return files.toArray(new File[]{});
		
	}
	
	public File[] getBlendCacheFiles(int userStartFrame, int userEndFrame) {
		
		if ( userStartFrame <= 0 ) {
			userStartFrame = startFrame;
		}
		if ( userEndFrame <= 0 ) {
			userEndFrame = endFrame;
		}
		
		if ( ! hasBlendCacheFolder() ) {
			return new File[]{};
		}

		Set<File> files = new HashSet<File>();
		for ( int i=userStartFrame-2; i<=userEndFrame+1; i++ ) {
			
			String formatted = String.format("%06d", i);
			for ( File file : getBlendCacheFolder().listFiles() ) {
				if ( file.getName().indexOf(formatted) >= 0 ) {
					files.add(file);
				}
			}
			
		}
		
		return files.toArray(new File[]{});
	}

}
