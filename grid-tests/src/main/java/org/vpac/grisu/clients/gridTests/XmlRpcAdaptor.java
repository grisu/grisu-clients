package org.vpac.grisu.clients.gridTests;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class XmlRpcAdaptor {

	public static void main(String[] args) throws Exception {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://sys10.in.vpac.org:8000/xmlrpc/"));

		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		Object[] params = new Object[] { new String("software_tests"),
				new String("Eiyuzeash5re"), UUID.randomUUID().toString(), new String("MarkusTest"), new String("Description of the test"), new String("Application name"),
				new String("MarkusVersion"), new Date(), new Date(),
				new String("SubmissionLocation:sdfsdfsd"), 0,
				new String("output")

		};

		Integer result = (Integer) client.execute("new_test_result", params);

		System.out.println("Output: " + result);

	}

}
