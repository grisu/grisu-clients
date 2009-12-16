package org.vpac.grisu.client.model.template.validators;

import java.net.URI;
import java.net.URISyntaxException;

import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class InputFile extends TemplateNodeValidator {

	public InputFile(TemplateNode node) {
		super(node);
	}

	@Override
	public void validate() throws TemplateValidateException {

		try {
			String multiplicity = templateNode.getMultiplicity();

			if (templateNode.getValue() == null
					|| "".equals(templateNode.getValue())
					|| "dummyfile".equals(templateNode.getValue())) {
				if (multiplicity == null || multiplicity.equals("1")) {
					throw new TemplateValidateException(
							"No input file specified.");
				} else {
					return;
				}
			}

			String temp = templateNode.getValue();
			String[] files = null;

			if (temp.indexOf(";") >= 0) {
				files = temp.split(";");
			} else {
				files = new String[] { temp };
			}

			for (String ifile : files) {

				GrisuFileObject file = templateNode.getTemplate()
						.getEnvironmentManager().getFileManager()
						.getFileObject(new URI(ifile));
				if (file == null) {
					throw new TemplateValidateException(
							"Input file does not exist: " + ifile);
				}
				if (file.exists()) {
					return;
				} else {
					throw new TemplateValidateException(
							"Input file does not exist: " + ifile);
				}

			}
		} catch (URISyntaxException e) {
			throw new TemplateValidateException(
					"Problem retrieving file object(s): " + e);
		}

	}

}
