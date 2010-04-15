package org.vpac.grisu.clients.gridTests;

import java.net.MalformedURLException;
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

	private final XmlRpcClient client;
	private final String username;
	private final String password;

	public XmlRpcOutputModule(String username, String password) {

		ProtocolSocketFactory easy = null;
		try {
			easy = new EasySSLProtocolSocketFactory();
		} catch (Exception e1) {
			System.err.println("Couldn't configure ssl: "+e1.getLocalizedMessage());
			System.exit(1);
		}
		Protocol protocol = new Protocol("https", easy, 443);
		Protocol.registerProtocol("https", protocol);

		this.username = username;
		this.password = password;

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL("http://sys10.in.vpac.org:8000/xmlrpc/"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		client = new XmlRpcClient();
		XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(client);
		client.setTransportFactory(factory);
		client.setConfig(config);

	}

	public void writeTestElement(GridTestElement element) {

		String uuid = element.getTestId();
		String testname = element.getTestInfo().getTestname();
		String description = element.getTestInfo().getDescription();
		String application = element.getTestInfo().getApplicationName();
		String version = element.getVersion();
		Date startDate = element.getStartDate();
		Date endDate = element.getEndDate();
		String submissionLocation = element.getSubmissionLocation();
		//		String submissionHost = SubmissionLocationHelpers
		//				.extractHost(submissionLocation);
		//		String queue = SubmissionLocationHelpers
		//				.extractQueue(submissionLocation);
		int success = 0;
		if ( element.wasInterrupted() ) {
			success = -1;
		} else if ( element.failed() ) {
			success = 1;
		}
		String output = OutputModuleHelpers.createStringReport(element)
		.toString();

		Object[] params = new Object[] { username, password, uuid, testname,
				description, application, version, startDate, endDate,
				submissionLocation, success, output };

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
