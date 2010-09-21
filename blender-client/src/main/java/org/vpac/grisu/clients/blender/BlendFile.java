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

	private final Map<File, String> referrencedFiles = new HashMap<File, String>();
	private int dirLevelsToInclude = 0;

	private final File fluidsFolder;

	private StringBuffer parseMessage = null;

	public BlendFile(File blendFile, File fluidsFolder)
			throws FileNotFoundException, Exception {
		this.file = blendFile;
		this.fluidsFolder = fluidsFolder;

		if (!this.file.exists()) {
			throw new FileNotFoundException("Couldn't find .blend file.");
		}
		if (!this.file.isFile()) {
			throw new FileNotFoundException(".blend file not a file.");
		}

		if (this.fluidsFolder != null && !this.fluidsFolder.exists()) {
			throw new FileNotFoundException("Couldn't find fluids folder.");
		}
		if (this.fluidsFolder != null && !this.fluidsFolder.isDirectory()) {
			throw new FileNotFoundException("Fluids folder not a directory.");
		}

		getAllReferencedResources();

	}

	private void getAllReferencedResources() throws Exception {

		parseMessage = new StringBuffer();

		final String command = "blender -b " + file.getPath() + " -P "
				+ GrisuBlenderJob.BLENDER_RESOURCE_PYTHYON_SCRIPT.getPath();
		parseMessage.append("Executing: " + command + "\n");
		final Process p = Runtime.getRuntime().exec(command);

		final BufferedReader stdInput = new BufferedReader(
				new InputStreamReader(p.getInputStream()));

		final BufferedReader stdError = new BufferedReader(
				new InputStreamReader(p.getErrorStream()));

		// read the output from the command
		String s;
		while ((s = stdInput.readLine()) != null) {

			if (s.startsWith(STARTFRAME_KEY)) {
				startFrame = Integer.parseInt(s.split(" ")[1]);
				parseMessage.append("Found first frame: " + startFrame + "\n");
			} else if (s.startsWith(ENDFRAME_KEY)) {
				endFrame = Integer.parseInt(s.split(" ")[1]);
				parseMessage.append("Found last frame: " + endFrame + "\n");
			} else if (s.startsWith(RESOURCE_KEY)) {

				final File reference = new File(s.split(" ")[1]);

				final String relPath = getRelativePathToBLendFile(reference);
				referrencedFiles.put(reference, relPath);
				parseMessage.append("Found referenced file: " + relPath + "\n");
			}
		}

		while ((s = stdError.readLine()) != null) {

			System.err.println(s);

		}

		if (fluidsFolder != null) {
			fluidsFolderPath = getRelativePathToBLendFile(fluidsFolder);
		}

		blendFilePath = file.getName();
		blendCacheFolderPath = getBlendCacheFolderName();

		File parent = file.getParentFile();
		// now calculate the folder structure on the resources...
		for (int i = 0; i < dirLevelsToInclude; i++) {
			parent = parent.getParentFile();
			blendFilePath = parent.getName() + "/" + blendFilePath;
			blendCacheFolderPath = parent.getName() + "/"
					+ blendCacheFolderPath;
		}

		for (final File f : referrencedFiles.keySet()) {
			referrencedFiles.put(f,
					referrencedFiles.get(f).replace(".." + File.separator, ""));
		}

		// read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
			parseMessage.append("Error: " + s + "\n");
		}

		stdInput.close();
		stdError.close();

	}

	public File[] getBlendCacheFiles(int userStartFrame, int userEndFrame) {

		if (userStartFrame <= 0) {
			userStartFrame = startFrame;
		}
		if (userEndFrame <= 0) {
			userEndFrame = endFrame;
		}

		if (!hasBlendCacheFolder()) {
			return new File[] {};
		}

		final Set<File> files = new HashSet<File>();
		for (int i = userStartFrame - 2; i <= userEndFrame + 1; i++) {

			final String formatted = String.format("%06d", i);
			for (final File file : getBlendCacheFolder().listFiles()) {
				if (file.getName().indexOf(formatted) >= 0) {
					files.add(file);
				}
			}

		}

		return files.toArray(new File[] {});
	}

	public File getBlendCacheFolder() {

		final File result = new File(file.getParent(),
				getBlendCacheFolderName());

		if (!result.exists()) {
			return null;
		}

		return result;

	}

	public String getBlendCacheFolderName() {
		return "blendcache_"
				+ file.getName().substring(0, file.getName().lastIndexOf("."));
	}

	public int getEndFrame() {
		return endFrame;
	}

	public File getFile() {
		return file;
	}

	public File[] getFluidFiles(int userStartFrame, int userEndFrame) {

		if (userStartFrame <= 0) {
			userStartFrame = startFrame;
		}
		if (userEndFrame <= 0) {
			userEndFrame = endFrame;
		}

		if (fluidsFolder == null) {
			return new File[] {};
		}

		final Set<File> files = new HashSet<File>();
		for (int i = userStartFrame - 2; i <= userEndFrame + 1; i++) {

			final String formatted = String.format("%04d", i);
			for (final File file : fluidsFolder.listFiles()) {
				if (file.getName().indexOf(formatted) >= 0) {
					files.add(file);
				}
			}

		}

		return files.toArray(new File[] {});

	}

	public String getFluidsFolderPath() {
		return this.fluidsFolderPath;
	}

	public String getParseMessage() {
		if (parseMessage == null) {
			return "";
		} else {
			return parseMessage.toString();
		}
	}

	public Map<File, String> getReferrencedFiles() {
		return referrencedFiles;
	}

	public String getRelativeBlendCacheFolderPath() {
		return blendCacheFolderPath;
	}

	public String getRelativeBlendFilePath() {
		return blendFilePath;
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
	public String getRelativePathToBLendFile(File target) throws IOException {
		final String[] baseComponents = file.getParentFile().getCanonicalPath()
				.split(Pattern.quote(File.separator));
		final String[] targetComponents = target.getCanonicalPath().split(
				Pattern.quote(File.separator));

		// skip common components
		int index = 0;
		for (; index < targetComponents.length && index < baseComponents.length; ++index) {
			if (!targetComponents[index].equals(baseComponents[index])) {
				break;
			}
		}
		int tempLevel = 0;
		final StringBuilder result = new StringBuilder();
		if (index != baseComponents.length) {
			// backtrack to base directory
			for (int i = index; i < baseComponents.length; ++i) {
				result.append(".." + File.separator);
				tempLevel = tempLevel + 1;
			}
			if (tempLevel > dirLevelsToInclude) {
				dirLevelsToInclude = tempLevel;
			}
		}
		for (; index < targetComponents.length; ++index) {
			result.append(targetComponents[index] + File.separator);
		}
		if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
			// remove final path separator
			result.delete(result.length() - "/".length(), result.length());
		}
		return result.toString();
	}

	public int getStartFrame() {
		return startFrame;
	}

	public boolean hasBlendCacheFolder() {

		if (getBlendCacheFolder() == null) {
			return false;
		} else {
			return true;
		}
	}

}
