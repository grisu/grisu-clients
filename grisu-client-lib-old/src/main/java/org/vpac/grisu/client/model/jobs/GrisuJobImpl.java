package org.vpac.grisu.client.model.jobs;

import java.io.File;
import java.net.URISyntaxException;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.utils.SeveralXMLHelpers;
import org.w3c.dom.Document;

public class GrisuJobImpl {
	
	JsdlTemplate template = null;
	EnvironmentManager em = null;
	
	public GrisuJobImpl(EnvironmentManager em) {
		this.em = em;
		File baseTemplateFile = null;
		try {
			baseTemplateFile = new File(this.getClass().getClassLoader().getResource("baseTemplate.xml").toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Document baseTemplate = SeveralXMLHelpers.loadXMLFile(baseTemplateFile);
		template = new JsdlTemplate(em, baseTemplate);
	}

}
