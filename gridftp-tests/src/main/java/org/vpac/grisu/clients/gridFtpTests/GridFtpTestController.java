package org.vpac.grisu.clients.gridFtpTests;

import grith.jgrith.plainProxy.LocalProxy;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.GrisuPluginFilenameFilter;

import au.org.arcs.jcommons.constants.ArcsEnvironment;
import au.org.arcs.jcommons.dependencies.ClasspathHacker;
import au.org.arcs.jcommons.dependencies.Dependency;
import au.org.arcs.jcommons.dependencies.DependencyManager;

public class GridFtpTestController {

	public static void main(String[] args) {

		String name = GridFtpTestController.class.getName();
		name = name.replace('.', '/') + ".class";
		final URL url = GridFtpTestController.class.getClassLoader()
				.getResource(name);
		final String path = url.getPath();
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

		final GridFtpTestController gftc = new GridFtpTestController(args,
				baseDir);

		gftc.start();

	}

	private final List<String> failedTestRunIds = Collections
			.synchronizedList(new LinkedList<String>());

	private final String grisu_base_directory;

	private final File grid_tests_directory;

	private ServiceInterface serviceInterface;

	private final GrisuRegistry registry;

	private String[] gridtestNames;
	private final String[] fqans;
	private String output = null;
	private final String[] excludes;
	private final String[] includes;

	private final int threads;

	private Date timeoutDate;
	private final int timeout;
	private final boolean displayOnlyFailed;

	private final List<OutputModule> outputModules = new LinkedList<OutputModule>();

	public GridFtpTestController(String[] args,
			String grisu_base_directory_param) {

		if (StringUtils.isBlank(grisu_base_directory_param)) {
			this.grisu_base_directory = System.getProperty("user.home")
					+ File.separator + "grisu-gridftp-tests";
		} else {
			this.grisu_base_directory = grisu_base_directory_param;
		}

		Environment.setGrisuDirectory(this.grisu_base_directory);

		final Map<Dependency, String> dependencies = new HashMap<Dependency, String>();

		dependencies.put(Dependency.BOUNCYCASTLE, "jdk15-143");

		DependencyManager.addDependencies(dependencies,
				ArcsEnvironment.getArcsCommonJavaLibDirectory());

		ClasspathHacker.initFolder(Environment.getGrisuPluginDirectory(),
				new GrisuPluginFilenameFilter());

		try {

			// HibernateSessionFactory
			// .setCustomHibernateConfigFile(this.grisu_base_directory
			// + File.separator + "grid-tests-hibernate-file.cfg.xml");

			final Class hsfc = Class
					.forName("org.vpac.grisu.backend.hibernate.HibernateSessionFactory");
			final Method method = hsfc.getMethod(
					"setCustomHibernateConfigFile", String.class);

			method.invoke(null, this.grisu_base_directory + File.separator
					+ "grid-tests-hibernate-file.cfg.xml");

		} catch (final Exception e) {
			// doesn't really matter
		}

		// HibernateSessionFactory
		// .setCustomHibernateConfigFile(this.grisu_base_directory
		// + File.separator + "grid-tests-hibernate-file.cfg.xml");

		grid_tests_directory = new File(this.grisu_base_directory, "tests");

		output = grid_tests_directory + File.separator + "testResults_"
				+ new Date().getTime() + ".log";

		final GridFtpTestCommandlineOptions options = new GridFtpTestCommandlineOptions(
				args);

		threads = options.getSimultaneousThreads();

		displayOnlyFailed = options.displayOnlyFailed();

		if (options.getMyproxyUsername() != null
				&& options.getMyproxyUsername().length() != 0) {
			try {
				final ConsoleReader consoleReader = new ConsoleReader();
				final char[] password = consoleReader.readLine(
						"Please enter your myproxy password: ",
						new Character('*')).toCharArray();

				final LoginParams loginParams = new LoginParams(
				// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
						"Local", options.getMyproxyUsername(), password);

				serviceInterface = LoginManager.login(null, null, null, null,
						loginParams);
			} catch (final Exception e) {
				System.out.println("Could not login: "
						+ e.getLocalizedMessage());
				System.exit(1);
			}
		} else {
			// trying to get local proxy

			final LoginParams loginParams = new LoginParams("Local", null,
					null, "myproxy2.arcs.org.au", "443");
			try {
				serviceInterface = LoginManager.login(
						LocalProxy.loadGlobusCredential(), null, null, null,
						loginParams);

			} catch (final Exception e) {
				System.out.println("Could not login: "
						+ e.getLocalizedMessage());
				System.exit(1);
			}
		}

		registry = GrisuRegistryManager.getDefault(this.serviceInterface);

		if (options.getFqans().length == 0) {
			fqans = serviceInterface.getFqans().asArray();
		} else {
			fqans = options.getFqans();
		}

		if (options.getOutput() != null && options.getOutput().length() > 0) {
			output = options.getOutput();
		}

		timeout = options.getTimeout();

		gridtestNames = options.getGridTestNames();
		Arrays.sort(gridtestNames);

		excludes = options.getExcludes();
		includes = options.getIncludes();

		outputModules.add(new LogFileOutputModule(output));
		// outputModules.add(new XmlRpcOutputModule());

		if (options.listTests()) {

			gridtestNames = null;

			final Set<MountPoint> mountPointsToUse = calculateMountPointsToUse();
			final List<GridFtpTestElement> elements = GridFtpTestElement
					.generateGridTestInfos(this, gridtestNames,
							mountPointsToUse);

			System.out.println("Available tests: ");
			for (final GridFtpTestElement element : elements) {
				System.out.println("Testname: " + element.getTestName());
				System.out.println();
				System.out.println("Description: " + element.getDescription());
				System.out.println();
				System.out.println("Test elements:");
				System.out.println();
				for (final List<GridFtpActionItem> list : element
						.getActionItems()) {
					for (final GridFtpActionItem item : list) {
						System.out.println("\tAction:\t"
								+ item.getAction().getAction().toString()
								+ " (Actionname: " + item.getAction().getName()
								+ ")");
						if (item.getSource() != null) {
							System.out
									.println("\tSource:\t" + item.getSource());
						}
						if (item.getTarget() != null) {
							System.out
									.println("\tTarget:\t" + item.getTarget());
						}
						System.out.println();
					}
					System.out.println();
					System.out.println();
				}
				System.out.println();
			}

			System.exit(0);
		}

	}

	public void addFailedTestRunId(String testrunid) {
		failedTestRunIds.add(testrunid);
	}

	private Set<MountPoint> calculateMountPointsToUse() {

		final Set<MountPoint> mps = new HashSet<MountPoint>();

		for (final String fqan : fqans) {

			for (final MountPoint mp : registry.getUserEnvironmentManager()
					.getMountPoints(fqan)) {

				boolean ignoreThisMountPoint = false;
				if (includes.length == 0) {
					for (final String filter : excludes) {
						if (mp.getRootUrl().indexOf(filter) >= 0) {
							ignoreThisMountPoint = true;
						}
					}
				} else {
					for (final String filter : includes) {
						if (mp.getRootUrl().indexOf(filter) < 0) {
							ignoreThisMountPoint = true;
						}
					}
				}
				if (!ignoreThisMountPoint) {
					mps.add(mp);
				}

			}

		}

		return mps;
	}

	public int getConcurrentThreads() {
		return threads;
	}

	public File getGridTestDirectory() {
		return grid_tests_directory;
	}

	public ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

	public void start() {

		final Set<MountPoint> mountPointsToUse = calculateMountPointsToUse();

		final List<GridFtpTestElement> elements = GridFtpTestElement
				.generateGridTestInfos(this, gridtestNames, mountPointsToUse);

		for (final GridFtpTestElement element : elements) {

			for (final List<GridFtpActionItem> oneTestStage : element
					.getActionItems()) {

				System.out.println("Execute: "
						+ oneTestStage.get(0).getAction().getName());

				final ExecutorService actionExecutor = Executors
						.newFixedThreadPool(threads);

				for (final GridFtpActionItem item : oneTestStage) {

					if (!failedTestRunIds.contains(item.runId())) {
						actionExecutor.execute(item.createActionThread());
					} else {
						System.out.println("Skipping item: " + item.toString());
					}
				}

				actionExecutor.shutdown();

				try {
					actionExecutor.awaitTermination(3600, TimeUnit.SECONDS);
				} catch (final InterruptedException e) {
					System.err.println("Executor interrupted...");
					System.exit(1);
				}

			}
		}

		for (final OutputModule module : outputModules) {
			for (final GridFtpTestElement element : elements) {
				module.writeTestElement(element);
			}
		}

		final StringBuffer resultAll = new StringBuffer();
		for (final GridFtpTestElement element : elements) {

			resultAll.append(element.getResultsForThisTest(displayOnlyFailed,
					false, false));
		}

		System.out.println(resultAll);

	}

}
