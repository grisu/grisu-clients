package org.vpac.grisu.clients.gridTests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;

import au.org.arcs.jcommons.utils.SubmissionLocationHelpers;

public class XmlRpcOutputModule implements OutputModule {

	private XmlRpcClient client;

	public XmlRpcOutputModule() {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL("http://shib-mp.arcs.org.au/xmlrpc/"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		client = new XmlRpcClient();
		client.setConfig(config);

	}

	public void writeTestElement(GridTestElement element) {

		String username = "grisu_test_client";
		String password = "kaiJaej9ieSh";

		String uuid = element.getTestId();
		String testname = element.getTestInfo().getTestname();
		String description = element.getTestInfo().getDescription();
		String application = element.getTestInfo().getApplicationName();
		String version = element.getVersion();
		Date startDate = element.getStartDate();
		Date endDate = element.getEndDate();
		String submissionLocation = element.getSubmissionLocation();
		String submissionHost = SubmissionLocationHelpers
				.extractHost(submissionLocation);
		String queue = SubmissionLocationHelpers
				.extractQueue(submissionLocation);
		boolean success = !element.failed();
		String output = OutputModuleHelpers.createStringReport(element)
				.toString();

		Object[] params = new Object[] { username, password, uuid, testname,
				description, application, version, startDate, endDate,
				submissionHost, queue, success, output };

		Integer result;
		try {
			System.out.println("Transferring results for test: " + application
					+ ", " + version + ", " + submissionLocation + "...");
			result = (Integer) client.execute("new_test_result", params);
			System.out.println("Success. Output: " + result);
		} catch (XmlRpcException e) {
			System.out.println("Couldn't transfer test results for test: "
					+ application + ", " + version + ", " + submissionLocation
					+ " to xmlrpc endpoint: " + e.getLocalizedMessage());
		}

	}

	public void writeTestsSetup(String setup) {
		// not necessary
	}

	public void writeTestsStatistic(String statistic) {
		// not necessary
	}

}
