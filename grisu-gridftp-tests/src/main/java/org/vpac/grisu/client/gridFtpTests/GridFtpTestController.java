package org.vpac.grisu.client.gridFtpTests;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.backend.hibernate.HibernateSessionFactory;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.login.LoginHelpers;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.settings.Environment;
import org.vpac.security.light.plainProxy.LocalProxy;

public class GridFtpTestController {
	
	private final ExecutorService actionExecutor;
	
	private final String grisu_base_directory;

	private final File grid_tests_directory;
	
	private ServiceInterface serviceInterface;
	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	private final GrisuRegistry registry;

	private String[] gridtestNames;
	private final String[] fqans;
	private String output = null;
	private String[] excludes;
	private String[] includes;
	
	private Date timeoutDate;
	private final int timeout;
	
	public GridFtpTestController(String[] args, String grisu_base_directory_param) {

		if (StringUtils.isBlank(grisu_base_directory_param)) {
			this.grisu_base_directory = System.getProperty("user.home")
					+ File.separator + "grisu-gridftp-tests";
		} else {
			this.grisu_base_directory = grisu_base_directory_param;
		}

		Environment.setGrisuDirectory(this.grisu_base_directory);
		HibernateSessionFactory
				.setCustomHibernateConfigFile(this.grisu_base_directory
						+ File.separator + "grid-tests-hibernate-file.cfg.xml");

		grid_tests_directory = new File(this.grisu_base_directory, "tests");

		output = grid_tests_directory + File.separator + "testResults_"
				+ new Date().getTime() + ".log";

		GridFtpTestCommandlineOptions options = new GridFtpTestCommandlineOptions(
				args);
		
		int threads = options.getSimultaneousThreads();
		actionExecutor = Executors.newFixedThreadPool(threads);

		
		if (options.getMyproxyUsername() != null
				&& options.getMyproxyUsername().length() != 0) {
			try {
				ConsoleReader consoleReader = new ConsoleReader();
				char[] password = consoleReader.readLine(
						"Please enter your myproxy password: ",
						new Character('*')).toCharArray();

				LoginParams loginParams = new LoginParams(
				// "http://localhost:8080/grisu-ws/services/grisu",
						// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
						"Local", options.getMyproxyUsername(), password);

				serviceInterface = ServiceInterfaceFactory
						.createInterface(loginParams);
			} catch (Exception e) {
				System.out.println("Could not login: "
						+ e.getLocalizedMessage());
				System.exit(1);
			}
		} else {
			// trying to get local proxy

			LoginParams loginParams = new LoginParams("Local", null, null,
					"myproxy2.arcs.org.au", "443");
			try {
				serviceInterface = LoginHelpers.login(loginParams, LocalProxy
						.loadGSSCredential());
			} catch (Exception e) {
				System.out.println("Could not login: "
						+ e.getLocalizedMessage());
				System.exit(1);
			}
		}

		registry = GrisuRegistryManager.getDefault(this.serviceInterface);
		
		
		if ( options.getFqans().length == 0 ) {
			fqans = serviceInterface.getFqans();
		} else {
			fqans = options.getFqans();
		}
		
		if (options.getOutput() != null && options.getOutput().length() > 0) {
			output = options.getOutput();
		}

		timeout = options.getTimeout();
		
		if ( options.listTests() ) {
			
			//TODO
			
			System.exit(0);
		}
		
		gridtestNames = options.getGridTestNames();
		Arrays.sort(gridtestNames);

		excludes = options.getExcludes();
		includes = options.getIncludes();

//		outputModules.add(new LogFileOutputModule(output));
//		outputModules.add(new XmlRpcOutputModule());


	}

	
	public void start() {
		
		Set<MountPoint> mountPointsToUse = new HashSet<MountPoint>();
		
		for ( String fqan : fqans ) {
		
			mountPointsToUse.addAll(registry.getUserEnvironmentManager().getMountPoints(fqan));
		
		}
		
		
		for ( MountPoint mpSource : mountPointsToUse ) {

			String sourceFile = null;
			try {
				sourceFile = serviceInterface.upload(new DataHandler(new FileDataSource(new File("/home/markus/test.txt"))), mpSource.getRootUrl()+"/test.txt", true);
			} catch (RemoteFileSystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for ( MountPoint mpTarget : mountPointsToUse ) {
				
//				GridFtpTestElement gfte = new GridFtpTestElement(serviceInterface,
//						Action.copy, sourceFile, mpTarget.getRootUrl()+"/test.txt");
//				
//				gfte.executeTest();
				
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		
		String name = GridFtpTestController.class.getName();
		name = name.replace('.', '/') + ".class";
		URL url = GridFtpTestController.class.getClassLoader().getResource(name);
		String path = url.getPath();
		// System.out.println("Executable path: "+path);
		String baseDir = null;
		if (url.toString().startsWith("jar:")) {
			baseDir = path.toString().substring(path.indexOf(":") + 1,
					path.indexOf(".jar!"));
			baseDir = baseDir.substring(0, baseDir.lastIndexOf("/"));
		} else {
			baseDir = null;
		}

		System.out.println("Using directory: " + baseDir);

		GridFtpTestController gftc = new GridFtpTestController(args, baseDir);

		gftc.start();
		
	}

}