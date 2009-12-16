package org.vpac.grisu.client.model.template.nodes.templatePreProcessor;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.plugins.underworld.UnderworldHelpers;
import org.vpac.grisu.utils.FileHelpers;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Element;

public class UnderworldInputFile extends InputFile {

	public UnderworldInputFile(TemplateNode node) {
		super(node);
	}

	private void parseUnderworldInputFile()
			throws TemplatePreProcessorException {

		File underworld_config_file = null;
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
			underworld_config_file = input_fileObject
					.getLocalRepresentation(true);
		} else {
			try {
				underworld_config_file = new File(new URI(input));
			} catch (Exception e) {
				throw new TemplatePreProcessorException(
						"Could not access file.", e);
			}
		}

		org.w3c.dom.Document input_doc = null;
		try {
			input_doc = SeveralXMLHelpers.fromString(FileHelpers
					.readFromFile(underworld_config_file));
		} catch (Exception e) {
			throw new TemplatePreProcessorException(
					"Could not parse input file to get output directory.");
		}

		String output_dir = UnderworldHelpers.getOutputDirectory(input_doc);
		String maxTimesteps = UnderworldHelpers.getMaxTimesteps(input_doc);
		node.getTemplate().getJobProperties().put("underworldOutputDirectory",
				output_dir);
		node.getTemplate().getJobProperties().put("maxTimesteps", maxTimesteps);
	}

	public void process() throws TemplatePreProcessorException {

		if (originalElement == null) {
			originalElement = (Element) node.getElement().cloneNode(true);
		}

		String input = node.getValue();

		calculatePostProcessUploadAttribute(input, node.getElement());

		parseUnderworldInputFile();
	}

}
