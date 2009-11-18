package org.vpac.grisu.clients.blender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BlendFile {

	public static String STARTFRAME_KEY = "StartFrame";
	public static String ENDFRAME_KEY = "EndFrame";
	public static String RESOURCE_KEY = "Resource";

	public File file = null;

	private int startFrame = 0;
	private int endFrame = -1;

	private Map<File, String> referrencedFiles = new HashMap<File, String>();

	public BlendFile(File blendFile) {

		this.file = blendFile;
		getAllReferencedResources(blendFile);

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

		StringBuilder result = new StringBuilder();
		if (index != baseComponents.length) {
			// backtrack to base directory
			for (int i = index; i < baseComponents.length; ++i)
				result.append(".." + File.separator);
		}
		for (; index < targetComponents.length; ++index)
			result.append(targetComponents[index] + File.separator);
		if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
			// remove final path separator
			result.delete(result.length() - "/".length(), result.length());
		}
		return result.toString();
	}

	private void getAllReferencedResources(File blendFile) {

		try {
			String command = "blender -b " + blendFile.getPath() + " -P "
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

}
