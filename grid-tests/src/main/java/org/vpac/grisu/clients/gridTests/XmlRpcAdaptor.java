package org.vpac.grisu.clients.gridTests;

import java.net.URL;
import java.util.Date;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class XmlRpcAdaptor {

	public static void main(String[] args) throws Exception {
		
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	    config.setServerURL(new URL("http://shib-mp.arcs.org.au/xmlrpc/"));
	    
	    XmlRpcClient client = new XmlRpcClient();
	    client.setConfig(config);
	    
	    Object[] params = new Object[]{
	    		new String("grisu_test_client"),
	    		new String("kaiJaej9ieSh"),
	    		new String("MarkusTest"),
	    		new String("MarkusVersion"),
	    		new Date(),
	    		new Date(),
	    		new String("Submissionhost"),
	    		new String("queue"),
	    		false,
	    		new String("output")
	    		
	    };
	    
	    
	    Integer result = (Integer) client.execute("new_test_result", params);
		
		System.out.println("Output: "+result);
		
	}
	
	
}
