package org.vpac.grisu.client.gridFtpTests;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.backend.hibernate.HibernateSessionFactory;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginHelpers;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.settings.Environment;
import org.vpac.security.light.plainProxy.LocalProxy;

public class GridFtpTestController {

	private List<String> failedTestRunIds = Collections
			.synchronizedList(new LinkedList<String>());

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

	private final int threads;

	private Date timeoutDate;
	private final int timeout;
	private final boolean displayOnlyFailed;
	
	private List<OutputModule> outputModules = new LinkedList<OutputModule>();

	public GridFtpTestController(String[] args,
			String grisu_base_directory_param) {

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

		threads = options.getSimultaneousThreads();

		displayOnlyFailed = options.displayOnlyFailed();
		
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

		if (options.getFqans().length == 0) {
			fqans = serviceInterface.getFqans();
		} else {
			fqans = options.getFqans();
		}

		if (options.getOutput() != null && options.getOutput().length() > 0) {
			output = options.getOutput();
		}

		timeout = options.getTimeout();

		if (options.listTests()) {

			// TODO

			System.exit(0);
		}

		gridtestNames = options.getGridTestNames();
		Arrays.sort(gridtestNames);

		excludes = options.getExcludes();
		includes = options.getIncludes();

		outputModules.add(new LogFileOutputModule(output));
		// outputModules.add(new XmlRpcOutputModule());

	}

	public File getGridTestDirectory() {
		return grid_tests_directory;
	}

	public int getConcurrentThreads() {
		return threads;
	}

	public void start() {

		Set<MountPoint> mountPointsToUse = new HashSet<MountPoint>();

		for (String fqan : fqans) {

			mountPointsToUse.addAll(registry.getUserEnvironmentManager()
					.getMountPoints(fqan));

		}

		List<GridFtpTestElement> elements = GridFtpTestElement
				.generateGridTestInfos(this, gridtestNames, mountPointsToUse);

		// LinkedList<List<GridFtpActionItem>> allActionItems = new
		// LinkedList<List<GridFtpActionItem>>();

		for (GridFtpTestElement element : elements) {

			for (List<GridFtpActionItem> oneTestStage : element
					.getActionItems()) {

				System.out.println("Execute: "
						+ oneTestStage.get(0).getAction().getName());

				ExecutorService actionExecutor = Executors
						.newFixedThreadPool(threads);

				for (GridFtpActionItem item : oneTestStage) {

					if (!failedTestRunIds.contains(item.runId())) {
						actionExecutor.execute(item.createActionThread());
					} else {
						System.out.println("Skipping item: " + item.toString());
					}
				}

				actionExecutor.shutdown();

				try {
					actionExecutor.awaitTermination(3600, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					System.err.println("Executor interrupted...");
					System.exit(1);
				}

			}
		}
		
		for ( OutputModule module : outputModules ) {
			for ( GridFtpTestElement element : elements ) {
				module.writeTestElement(element);
			}
		}

		StringBuffer resultAll = new StringBuffer();
		for (GridFtpTestElement element : elements) {

			resultAll.append(element.getResultsForThisTest(displayOnlyFailed, false, false));
		}

		System.out.println(resultAll);

	}

	public void addFailedTestRunId(String testrunid) {
		failedTestRunIds.add(testrunid);
	}

	public static void main(String[] args) {

		String name = GridFtpTestController.class.getName();
		name = name.replace('.', '/') + ".class";
		URL url = GridFtpTestController.class.getClassLoader()
				.getResource(name);
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
