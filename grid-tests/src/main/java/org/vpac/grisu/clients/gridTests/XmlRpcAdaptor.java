package org.vpac.grisu.clients.gridTests;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.httpclient.contrib.ssl.AuthSSLProtocolSocketFactory;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

public class XmlRpcAdaptor {

	public static void main(String[] args) throws Exception {

		final ProtocolSocketFactory easy = new EasySSLProtocolSocketFactory();
		final Protocol protocol = new Protocol("https", easy, 443);
		Protocol.registerProtocol("https", protocol);

		// Protocol authhttps = new Protocol("https",
		// new AuthSSLProtocolSocketFactory(
		// new File("/home/markus/.globus/usercert.jks").toURL(),
		// "0istbesserals00",
		// new File("/home/markus/Desktop/certstuff/my.truststore").toURL(),
		// "geheim"), 443);

		// HttpClient client = new HttpClient();
		// client.getHostConfiguration().setHost("localhost", 443, authhttps);
		// // use relative url only
		// GetMethod httpget = new GetMethod("/");
		// client.executeMethod(httpget);

		final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("https://acc.arcs.org.au/xmlrpc/"));
		// config.setServerURL(new
		// URL("http://sys10.in.vpac.org:8000/xmlrpc/"));

		final AuthSSLProtocolSocketFactory test;

		final XmlRpcClient client = new XmlRpcClient();
		final XmlRpcCommonsTransportFactory factory = new XmlRpcCommonsTransportFactory(
				client);
		// HttpClient httpclient = new HttpClient();
		// // httpclient.getHostConfiguration().setHost("acc.arcs.org.au", 443,
		// authhttps);
		// factory.setHttpClient(httpclient);
		client.setTransportFactory(factory);
		client.setConfig(config);

		final Object[] params = new Object[] { new String("software_tests"),
				new String("Is1eedaixeed2noa"), UUID.randomUUID().toString(),
				new String("MarkusTest"),
				new String("Description of the test"),
				new String("Application name"), new String("MarkusVersion"),
				new Date(), new Date(),
				new String("SubmissionLocation:sdfsdfsd"), 0,
				new String("output")

		};

		final Integer result = (Integer) client.execute("new_test_result",
				params);

		System.out.println("Output: " + result);

	}

}
