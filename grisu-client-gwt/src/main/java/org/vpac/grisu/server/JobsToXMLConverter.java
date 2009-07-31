package org.vpac.grisu.server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.org.arcs.jcommons.constants.Constants;

/**
 * This one gathers all information of a job and converts it into a xml
 * document.
 * 
 * It will be replaced soonish with a plain Map based job information converter.
 * 
 * @author Markus Binsteiner
 * 
 */
public final class JobsToXMLConverter {
	
	private JobsToXMLConverter() {
	}

	private static DocumentBuilder docBuilder = null;

	private static DocumentBuilder getDocumentBuilder() {

		if (docBuilder == null) {
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
		return docBuilder;

	}

	public static Document getJobsInformation(final DtoJobs jobs) {

		Document output = null;

		output = getDocumentBuilder().newDocument();

		Element root = output.createElement("jobs");
		output.appendChild(root);

		for (DtoJob job : jobs.getAllJobs()) {
			root.appendChild(createJobElementNew(output, job));
		}

		return output;
	}

	public static Element createJobElementNew(final Document doc, final DtoJob job) {

		Element jobElement = doc.createElement("job");

		Element jobname = doc.createElement("jobname");
		jobname.setTextContent(job.getPropertiesAsMap().get(Constants.JOBNAME_KEY));
		jobElement.appendChild(jobname);

		Element status = doc.createElement("status");
		status.setTextContent(new Integer(job.getStatus()).toString());
		jobElement.appendChild(status);

		String host = job.getPropertiesAsMap().get(Constants.SUBMISSION_HOST_KEY);
		if (host != null && !"".equals(host)) {
			Element hostElement = doc.createElement("host");
			hostElement.setTextContent(host);
			jobElement.appendChild(hostElement);
		}

		String fqan = job.getPropertiesAsMap().get(Constants.FQAN_KEY);
		if (fqan != null && !"".equals(fqan)) {
			Element fqanElement = doc.createElement("fqan");
			fqanElement.setTextContent(fqan);
			jobElement.appendChild(fqanElement);
		}

		String submissionTime = job.getPropertiesAsMap().get(Constants.SUBMISSION_TIME_KEY);
		if (submissionTime != null && !"".equals(submissionTime)) {
			Element submissionTimeElement = doc.createElement("submissionTime");
			submissionTimeElement.setTextContent(submissionTime);
			jobElement.appendChild(submissionTimeElement);
		}

		return jobElement;

	}

//	public static Element createJobElement(final Document doc, final Job job) {
//
//		Element jobElement = doc.createElement("job");
//
//		Attr jobname = doc.createAttribute("jobname");
//		jobname.setValue(job.getJobname());
//		jobElement.setAttributeNode(jobname);
//
//		Attr status = doc.createAttribute("status");
//		status.setValue(new Integer(job.getStatus()).toString());
//		jobElement.setAttributeNode(status);
//
//		String host = job.getSubmissionHost();
//		if (host != null && !"".equals(host)) {
//			Attr host_attr = doc.createAttribute("host");
//			host_attr.setValue(host);
//			jobElement.setAttributeNode(host_attr);
//		}
//
//		String fqan = job.getFqan();
//		if (fqan != null && !"".equals(fqan)) {
//			Attr fqan_attr = doc.createAttribute("fqan");
//			fqan_attr.setValue(fqan);
//			jobElement.setAttributeNode(fqan_attr);
//		}
//
//		String submissionTime = job.getJobProperty("submissionTime");
//		if (submissionTime != null && !"".equals(submissionTime)) {
//			Attr submissionTime_attr = doc.createAttribute("submissionTime");
//			submissionTime_attr.setValue(submissionTime);
//			jobElement.setAttributeNode(submissionTime_attr);
//		}
//
//		return jobElement;
//	}

//	public static Document getDetailedJobInformation(final Job job) {
//
//		Document doc = null;
//
//		try {
//			DocumentBuilderFactory docFactory = DocumentBuilderFactory
//					.newInstance();
//			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//			doc = docBuilder.newDocument();
//		} catch (ParserConfigurationException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			return null;
//		}
//
//		Element root = doc.createElement("jobs");
//		doc.appendChild(root);
//
//		Element jobElement = doc.createElement("job");
//
//		root.appendChild(jobElement);
//
//		Attr jobname = doc.createAttribute("jobname");
//		jobname.setValue(job.getJobname());
//		jobElement.setAttributeNode(jobname);
//
//		Attr app = doc.createAttribute("application");
//		app.setValue(job.getJobProperty(Constants.APPLICATIONNAME_KEY));
//		jobElement.setAttributeNode(app);
//
//		Attr status = doc.createAttribute("status");
//		status.setValue(new Integer(job.getStatus()).toString());
//		jobElement.setAttributeNode(status);
//
//		String host = job.getSubmissionHost();
//		if (host != null && !"".equals(host)) {
//			Attr host_attr = doc.createAttribute("host");
//			host_attr.setValue(host);
//			jobElement.setAttributeNode(host_attr);
//		}
//
//		String fqan = job.getFqan();
//		if (fqan != null && !"".equals(fqan)) {
//			Attr fqan_attr = doc.createAttribute("fqan");
//			fqan_attr.setValue(fqan);
//			jobElement.setAttributeNode(fqan_attr);
//		}
//
//		Element files = doc.createElement("files");
//		files.setAttribute("job_directory", job.getJobProperty(Constants.JOBDIRECTORY_KEY));
//		root.appendChild(files);
//
//		Element stdout = doc.createElement("file");
//		stdout.setAttribute("name", "stdout");
//		stdout.setTextContent(job.getJobProperty(Constants.STDOUT_KEY));
//		files.appendChild(stdout);
//
//		Element stderr = doc.createElement("file");
//		stderr.setAttribute("name", "stderr");
//		stderr.setTextContent(job.getJobProperty(Constants.STDERR_KEY));
//		files.appendChild(stderr);
//
//		Element descriptions = doc.createElement("descriptions");
//		root.appendChild(descriptions);
//
//		Element jsdl = doc.createElement("description");
//		jsdl.setAttribute("type", "jsdl");
//		try {
//			jsdl.setTextContent(SeveralXMLHelpers.toString(job
//					.getJobDescription()));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		descriptions.appendChild(jsdl);
//
//		Element rsl = doc.createElement("description");
//		rsl.setAttribute("type", "rsl");
//		rsl.setTextContent(job.getSubmittedJobDescription());
//		descriptions.appendChild(rsl);
//
//		return doc;
//	}
}
