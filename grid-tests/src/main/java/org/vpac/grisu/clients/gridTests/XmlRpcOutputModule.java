package org.vpac.grisu.clients.gridTests;

import java.net.URL;
import java.util.Date;

import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.vpac.grisu.clients.gridTests.testElements.GridTestElement;

public class XmlRpcOutputModule implements OutputModule {

	private XmlRpcClient client;
	private final String username;
	private final String password;

	public XmlRpcOutputModule(String username, String password) {

		this.username = username;
		this.password = password;

		ProtocolSocketFactory easy = null;
		try {
			easy = new EasySSLProtocolSocketFactory();

			final Protocol protocol = new Protocol("https", easy, 443);
			Protocol.registerProtocol("https", protocol);

			final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("https://acc.arcs.org.au/xmlrpc/"));
			client = new XmlRpcClient();
			final XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(
					client);
			client.setTransportFactory(factory);
			client.setConfig(config);
		} catch (final Exception e1) {
			System.err.println("Couldn't configure ssl: "
					+ e1.getLocalizedMessage());
			System.exit(1);
		}

	}

	public void writeTestElement(GridTestElement element) {

		final String uuid = element.getTestId();
		final String testname = element.getTestInfo().getTestname();
		final String description = element.getTestInfo().getDescription();
		final String application = element.getTestInfo().getApplicationName();
		final String version = element.getVersion();
		final Date startDate = element.getStartDate();
		final Date endDate = element.getEndDate();
		final String submissionLocation = element.getSubmissionLocation();
		// String submissionHost = SubmissionLocationHelpers
		// .extractHost(submissionLocation);
		// String queue = SubmissionLocationHelpers
		// .extractQueue(submissionLocation);
		int success = 0;
		if (element.wasInterrupted()) {
			success = -1;
		} else if (element.failed()) {
			success = 1;
		}
		final String output = OutputModuleHelpers.createStringReport(element)
				.toString();

		final Object[] params = new Object[] { username, password, uuid,
				testname, description, application, version, startDate,
				endDate, submissionLocation, success, output };

		Integer result;
		try {
			System.out.println("Transferring results for test: " + application
					+ ", " + version + ", " + submissionLocation + "...");
			result = (Integer) client.execute("new_test_result", params);
			System.out.println("Success. Output: " + result);
		} catch (final XmlRpcException e) {
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
