package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.w3c.dom.Element;

public class NamdConfigInputFile extends InputFile {

	public NamdConfigInputFile(TemplateNode node) {
		super(node);
	}

	private void parseNamdInputFile() throws TemplatePreProcessorException {

		File namd_config_file = null;
		String input = node.getValue();

		if (!input.startsWith("file:")) {
			String remote_file = input;

			GrisuFileObject input_fileObject;
			try {
				input_fileObject = node.getTemplate().getEnvironmentManager()
						.getFileManager().getFileObject(new URI(remote_file));
				if (input_fileObject == null) {
					throw new TemplatePreProcessorException(
							(new StringBuilder()).append(
									"Could not retrieve remote file: ").append(
									remote_file).toString());
				}
			} catch (URISyntaxException e) {
				throw new TemplatePreProcessorException(
						(new StringBuilder())
								.append(
										"Could not create temp file for element because of wrong uri syntax: ")
								.append(remote_file).toString());
			}
			namd_config_file = input_fileObject.getLocalRepresentation(true);
		} else {
			try {
				namd_config_file = new File(new URI(input));
			} catch (Exception e) {
				throw new TemplatePreProcessorException(
						"Could not access file.", e);
			}
		}
		List lines = null;
		try {
			lines = FileUtils.readLines(namd_config_file);
		} catch (IOException e) {
			throw new TemplatePreProcessorException((new StringBuilder())
					.append("Could not read namd config file: ").append(
							namd_config_file.toString()).toString());
		}
		Map propertiesToParse = new HashMap();
		propertiesToParse.put("minimize", null);
		propertiesToParse.put("run", null);
		propertiesToParse.put("numsteps", null);
		Iterator i$ = lines.iterator();
		do {
			if (!i$.hasNext())
				break;
			Object line_object = i$.next();
			String line = ((String) line_object).trim();
			String keyValue[] = line.split("\\s+");
			if (propertiesToParse.keySet().contains(keyValue[0]))
				propertiesToParse.put(keyValue[0], keyValue[1]);
		} while (true);
		int minimize = Integer.parseInt((String) propertiesToParse
				.get("minimize"));
		int run = 0;
		int numsteps = 0;
		if (propertiesToParse.get("run") != null)
			run = Integer.parseInt((String) propertiesToParse.get("run"));
		if (propertiesToParse.get("numsteps") != null)
			numsteps = Integer.parseInt((String) propertiesToParse
					.get("numsteps"));
		int steps = minimize + run + numsteps;
		Map<String, String> result = new HashMap();
		node.getTemplate().getJobProperties().put("totalSteps",
				(new Integer(steps)).toString());
		node.getTemplate().getJobProperties().put("minimize",
				(new Integer(minimize)).toString());
		node.getTemplate().getJobProperties().put("numsteps",
				(new Integer(run + numsteps)).toString());

	}

	public void process() throws TemplatePreProcessorException {

		if (originalElement == null) {
			originalElement = (Element) node.getElement().cloneNode(true);
		}

		String input = node.getValue();

		calculatePostProcessUploadAttribute(input, node.getElement());

		parseNamdInputFile();

	}

}
