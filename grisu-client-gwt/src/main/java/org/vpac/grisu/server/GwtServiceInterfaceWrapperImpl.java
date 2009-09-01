package org.vpac.grisu.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.vpac.grisu.client.Application;
import org.vpac.grisu.client.GwtServiceInterfaceWrapper;
import org.vpac.grisu.client.LoginException;
import org.vpac.grisu.client.files.FileSystemException;
import org.vpac.grisu.client.jobCreation.JobCreationException;
import org.vpac.grisu.client.jobCreation.MdsJobCreationPanel;
import org.vpac.grisu.client.model.GwtGridResourceWrapper;
import org.vpac.grisu.client.model.GwtGrisuCacheFile;
import org.vpac.grisu.client.model.GwtGrisuRemoteFile;
import org.vpac.grisu.client.model.GwtJobException;
import org.vpac.grisu.client.model.GwtMountPointWrapper;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.control.info.CachedMdsInformationManager;
import org.vpac.grisu.frontend.control.login.LoginParams;
import org.vpac.grisu.frontend.control.login.ServiceInterfaceFactory;
import org.vpac.grisu.model.GrisuRegistryImpl;
import org.vpac.grisu.model.GrisuRegistry;
import org.vpac.grisu.model.MountPoint;
import org.vpac.grisu.model.dto.DtoFile;
import org.vpac.grisu.model.dto.DtoFolder;
import org.vpac.grisu.model.dto.DtoJob;
import org.vpac.grisu.model.dto.DtoJobs;
import org.vpac.grisu.model.job.JobSubmissionObjectImpl;
import org.vpac.grisu.settings.ClientPropertiesManager;
import org.vpac.grisu.settings.Environment;
import org.vpac.grisu.utils.FileHelpers;
import org.vpac.grisu.utils.SeveralXMLHelpers;

import au.org.arcs.grid.grisu.matchmaker.MatchMakerImpl;
import au.org.arcs.grid.sched.MatchMaker;
import au.org.arcs.jcommons.constants.JobSubmissionProperty;
import au.org.arcs.jcommons.interfaces.GridResource;
import au.org.arcs.jcommons.interfaces.InformationManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GwtServiceInterfaceWrapperImpl extends RemoteServiceServlet
		implements GwtServiceInterfaceWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = -808306174995499341L;
	
	static final Logger myLogger = Logger
			.getLogger(GwtServiceInterfaceWrapperImpl.class.getName());

	private MatchMaker matchMaker = null;
	private InformationManager infoManager = null;

	private MatchMaker getMatchMaker() {

		if (matchMaker == null) {
			matchMaker = new MatchMakerImpl(Environment.getGrisuDirectory().toString());
		}
		return matchMaker;
	}

	private InformationManager getInfoManager() {

		if (infoManager == null) {
			infoManager = new CachedMdsInformationManager(Environment.getGrisuDirectory().toString());
		}
		return infoManager;

	}

	public void login(Map loginData) throws LoginException {

		String username = loginData.get("username").toString();

		char[] password = loginData.get("password").toString().toCharArray();
		
		String serviceInterfaceUrl = ClientPropertiesManager.getDefaultServiceInterfaceUrl();
		
		myLogger.info("Logging in...");
		LoginParams loginParams = new LoginParams(
		// "http://localhost:8080/grisu-ws/services/grisu",
				// "https://ngportaldev.vpac.org/grisu-ws/services/grisu",
				serviceInterfaceUrl, username, password, "myproxy2.arcs.org.au", "7512");

		ServiceInterface si = null;
		try {
			si = ServiceInterfaceFactory.createInterface(loginParams);
			si.login(username, new String(password));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LoginException(e.getLocalizedMessage());
		}
		myLogger.info("ServiceInterface created...");
		getSession().setAttribute("serviceInterface", si);

		myLogger.info("Logged in...");
	}

	public String[] getAllFqans() {

		return getServiceInterface().getFqans();
	}

	public String getJobsStatus() {

		DtoJobs result = getServiceInterface().ps(true);

		return SeveralXMLHelpers.toString(JobsToXMLConverter.getJobsInformation(result));
	}

	public GwtMountPointWrapper[] getAllMountPoints() {

		List<MountPoint> allMountPoints = getServiceInterface().df().getMountpoints();

		GwtMountPointWrapper[] result = new GwtMountPointWrapper[allMountPoints.size()];
		Mapper mapper = new DozerBeanMapper();
		int i = 0;
		for (MountPoint resource : allMountPoints) {
			GwtMountPointWrapper temp = mapper.map(resource,
					GwtMountPointWrapper.class);
			String site = getGrisuRegistry().getResourceInformation().getSite(
					temp.getRootUrl());
			temp.setSite(site);
			result[i] = temp;
			i = i + 1;
		}

		return result;
	}

	public GwtGrisuRemoteFile[] getChildren(String url)
			throws FileSystemException {

		DtoFolder remote_root = null;
		try {
			remote_root = getServiceInterface().ls(url, 1);
		} catch (RemoteFileSystemException e) {
			// TODO throw exception
			e.printStackTrace();
			throw new FileSystemException(e.getLocalizedMessage());
		}

//		Element remoteRoot = (Element) remote_root.getFirstChild()
//				.getFirstChild();
		

//		String rootType = remoteRoot.getTagName();
		if (!remote_root.isFolder()) {
			// well, that's of no use
			myLogger
					.debug("Tried to update local xml cache but the path is a file, so I'm doing nothing.");
			return null;
		} else {
			int resultSize = remote_root.listAllChildren().size();
			List<GwtGrisuRemoteFile> children = new LinkedList<GwtGrisuRemoteFile>();
			
			for ( DtoFolder childFolder : remote_root.getChildrenFolders() ) {
				String foldername = childFolder.getName();
				String path = childFolder.getRootUrl();
				myLogger.debug("Adding folder to children array: "
						+ foldername);
				GwtGrisuRemoteFile temp = new GwtGrisuRemoteFile(foldername, path, true,
						GwtGrisuRemoteFile.FILESYSTEM_TYPE, -1);
				children.add(temp);
			}
			
			for ( DtoFile childFile : remote_root.getChildrenFiles() ) {
				
					String filename = childFile.getName();
					String path = childFile.getRootUrl();
					long size = -1;
					try {
						size = childFile.getSize();
					} catch (Exception e) {
						e.printStackTrace();
					}
					myLogger
							.debug("Adding file to children array: " + filename);
					GwtGrisuRemoteFile temp = new GwtGrisuRemoteFile(filename, path, false,
							GwtGrisuRemoteFile.FILESYSTEM_TYPE, size);
					children.add(temp);

			}
			return children.toArray(new GwtGrisuRemoteFile[]{});

		}
	}

	public String[] getAllApplicationsOnTheGrid() {

		return getServiceInterface().getAllAvailableApplications(null);

	}

	public GwtGrisuCacheFile downloadFile(GwtGrisuRemoteFile file) {

		String path = file.getPath();
		DataSource source = null;
		GwtGrisuCacheFile newFile = null;
		try {
			source = getServiceInterface().download(path).getDataSource();
			String filename = path.substring(path.lastIndexOf("/") + 1);
			File tempFile = new File(Application.APACHE_WEB_ROOT + "/"
					+ Application.APACHE_SUBFOLDER, filename);
			FileHelpers.saveToDisk(source, tempFile);
			String localPath = tempFile.getAbsolutePath();
			String url = localPath.substring(Application.APACHE_WEB_ROOT
					.length());
			String mimeType = new MimetypesFileTypeMap()
					.getContentType(tempFile);

			newFile = new GwtGrisuCacheFile(localPath, url, mimeType, path);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newFile;
	}

	public String zipFilesAndPrepareForDownload(GwtGrisuRemoteFile[] files) {

		try {
			int random = 123;// Random.nextInt();
			byte[] buf = new byte[1024];
			String downloadFilename = Application.APACHE_WEB_ROOT + "/"
					+ Application.APACHE_SUBFOLDER + "/" + "download_" + random
					+ ".zip";

			Set<String> localFiles = new HashSet<String>();
			for (GwtGrisuRemoteFile file : files) {
				localFiles.add(downloadFile(file).getLocalPath());
			}

			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					downloadFilename));

			// Compress the files
			for (String source : localFiles) {
				FileInputStream in = new FileInputStream(source);

				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(source));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();

			}
			// Complete the ZIP file
			out.close();

			String url = downloadFilename.substring(Application.APACHE_WEB_ROOT
					.length());

			return url;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getAllAvailableVOs() {

		return getServiceInterface().getFqans();
	}

	public GwtGridResourceWrapper[] findGridResourcesForVersionsAndFqans(
			String application, String[] versions, String[] fqans) {

		Set<GridResource> allResources = new HashSet<GridResource>();

		for (String fqan : fqans) {
			for (String version : versions) {
				Map<JobSubmissionProperty, String> jobProperties = new HashMap<JobSubmissionProperty, String>();
				jobProperties.put(JobSubmissionProperty.APPLICATIONNAME, application);
				jobProperties.put(JobSubmissionProperty.APPLICATIONVERSION, version);
				List<GridResource> resources = getMatchMaker()
						.findAllResources(jobProperties, fqan);
				allResources.addAll(resources);
			}
		}

		GwtGridResourceWrapper[] result = new GwtGridResourceWrapper[allResources
				.size()];
		Mapper mapper = new DozerBeanMapper();
		int i = 0;
		for (GridResource resource : allResources) {
			GwtGridResourceWrapper temp = mapper.map(resource,
					GwtGridResourceWrapper.class);
			result[i] = temp;
			i = i + 1;
		}

		return result;
	}

	public String[] getVersionsOfApplication(String applicationName) {

		String[] result = getInfoManager().getAllVersionsOfApplicationOnGrid(
				applicationName);
		return result;
	}

	public String[] getVersionsOfApplicationForVO(String applicationName,
			String fqan) {

		String[] result = getInfoManager()
				.getAllVersionsOfApplicationOnGridForVO(applicationName, fqan);
		return result;
	}

	public String[] getApplicationForExecutable(String executable) {

		String[] exes = getInfoManager().getApplicationsThatProvideExecutable(
				executable);

		return exes;

	}

	public void submitJob(Map<String, String> jobProperties)
			throws JobCreationException {

		String fqan = jobProperties.get("vo");
		JobSubmissionObjectImpl jso = new JobSubmissionObjectImpl();


			String[] allAvailableApplicationsForSelectedVO = infoManager
					.getAllApplicationsOnGridForVO(fqan);

			jso.setJobname(jobProperties.get(MdsJobCreationPanel.JOBNAME));

			// try {
			// getServiceInterface().createJob(jso.getJobname(),
			// JobConstants.DONT_ACCEPT_NEW_JOB_WITH_EXISTING_JOBNAME);
			// } catch (org.vpac.grisu.control.JobCreationException e1) {
			// throw new
			// JobCreationException("Could not create job with jobname "+jso.getJobname()+": "+e1.getLocalizedMessage());
			// }

			// Application
			if ((jobProperties.get(MdsJobCreationPanel.APPLICATIONNAME) == null)
					&& (jobProperties.get("force_application") != null && jobProperties
							.get("force_application").equals("on"))) {
				throw new JobCreationException("No application provided.");
			} else if (jobProperties.get(MdsJobCreationPanel.COMMANDLINE) == null
					|| jobProperties.get(MdsJobCreationPanel.COMMANDLINE)
							.length() == 0) {
				throw new JobCreationException("No commandline provided.");
			} else if (jobProperties.get(MdsJobCreationPanel.APPLICATIONNAME) != null
					&& "on".equals(jobProperties.get("force_application"))) {
				String application = jobProperties
						.get(MdsJobCreationPanel.APPLICATIONNAME);
				if (Arrays.binarySearch(allAvailableApplicationsForSelectedVO,
						application) < 0) {
					throw new JobCreationException(
							"Selected application is not available for the selected VO.");
				}
				jso.setApplication(application);
			} else {

				String commandline = jobProperties
						.get(MdsJobCreationPanel.COMMANDLINE);

				String executable = null;
				int firstWhitespace = commandline.indexOf(" ");
				if (firstWhitespace == -1) {
					executable = commandline;
				} else {
					executable = commandline.substring(0, firstWhitespace);
				}

				String[] applications = getApplicationForExecutable(executable);

				if (applications == null || applications.length == 0) {
					throw new JobCreationException(
							"Could not find application for executable: "
									+ executable);
				}

				String appToUse = null;
				for (String app : applications) {
					if (Arrays.binarySearch(
							allAvailableApplicationsForSelectedVO, app) >= 0) {
						appToUse = app;
						break;
					}
				}

				if (appToUse == null) {
					throw new JobCreationException(
							"Could not find application for the combination of executable/VO: "
									+ executable + "/" + fqan);
				}

				jso.setApplication(appToUse);

			}

			String commandline = jobProperties
					.get(MdsJobCreationPanel.COMMANDLINE);
			if (commandline == null || commandline.length() == 0) {
				throw new JobCreationException(
						"Commandline parameter is missing.");
			} else {
				jso.setCommandline(commandline);
			}

			// this won't do anything at the moment
			jso.setApplicationVersion(jobProperties
					.get(MdsJobCreationPanel.APPLICATIONVERSION));

			jso.setCpus(Integer.parseInt(jobProperties
					.get(MdsJobCreationPanel.NO_CPUS)));
			jso.setMemory(Long.parseLong(jobProperties
					.get(MdsJobCreationPanel.MEMORY_IN_B)));
			jso.setWalltimeInSeconds(Integer.parseInt(jobProperties
					.get(MdsJobCreationPanel.WALLTIME_IN_MINUTES)) * 60);
			jso.setEmail_address(jobProperties
					.get(MdsJobCreationPanel.EMAIL_ADDRESS));
			if ("on".equals(jobProperties
					.get(MdsJobCreationPanel.EMAIL_ON_START))) {
				jso.setEmail_on_job_start(true);
			} else {
				jso.setEmail_on_job_start(false);
			}
			if ("on".equals(jobProperties
					.get(MdsJobCreationPanel.EMAIL_ON_FINISH))) {
				jso.setEmail_on_job_finish(true);
			} else {
				jso.setEmail_on_job_finish(false);
			}

			if (JobSubmissionProperty.FORCE_SINGLE.toString().equals(
					jobProperties.get("jobType"))) {
				jso.setForce_single(Boolean.parseBoolean(jobProperties
						.get(MdsJobCreationPanel.FORCE_SINGLE)));
			} else if (JobSubmissionProperty.FORCE_MPI.toString().equals(
					jobProperties.get("jobType"))) {
				jso.setForce_mpi(Boolean.parseBoolean(jobProperties
						.get(MdsJobCreationPanel.FORCE_MPI)));
			}

			if (jobProperties.get(MdsJobCreationPanel.INPUT_FILE_URLS) != null
					&& jobProperties.get(MdsJobCreationPanel.INPUT_FILE_URLS)
							.length() > 0) {
				jso.setInputFileUrls(jobProperties.get(
						MdsJobCreationPanel.INPUT_FILE_URLS).split(","));
			}


		try {
			getServiceInterface().createJob(jso.getJobDescriptionDocumentAsString(), fqan, "force-name");
		} catch (Exception e) {
			e.printStackTrace();
			throw new JobCreationException(e.getLocalizedMessage());
		}

		try {
			getServiceInterface().submitJob(jso.getJobname());
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}

	}

	public Map<String, String> getJobDetails(String jobname) {

		// Map<String, String> allJobProperties = null;
		// try {
		// allJobProperties = serviceInterface.getAllJobProperties(jobname);
		// } catch (NoSuchJobException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//		
		try {
			DtoJob result = getServiceInterface()
					.getAllJobProperties(jobname);
			HashMap<String, String> tempMap = new HashMap<String, String>(
					result.propertiesAsMap());
			return tempMap;
		} catch (NoSuchJobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void killJob(String[] jobnames, boolean clean) throws GwtJobException {

		StringBuffer failedKillings = new StringBuffer();

		for (String jobname : jobnames) {
			try {
				getServiceInterface().kill(jobname, clean);
			} catch (Exception e) {
				failedKillings.append(jobname + ": " + e.getLocalizedMessage());
			}
		}
		if (failedKillings.length() > 0) {
			throw new GwtJobException("Could not kill the following jobs: "
					+ failedKillings.toString());
		}
	}

	private ServiceInterface getServiceInterface() {

		ServiceInterface si = (ServiceInterface) (getSession()
				.getAttribute("serviceInterface"));
		if (si == null) {
			myLogger.error("ServiceInterface not in session (yet?).");
			throw new RuntimeException("Not logged in.");
		}
		return si;

	}

	private GrisuRegistry getGrisuRegistry() {
		GrisuRegistry gr = (GrisuRegistry) (getSession()
				.getAttribute("grisuRegistry"));
		if (gr == null) {
			gr = new GrisuRegistryImpl(getServiceInterface());
			getSession().setAttribute("grisuRegistry", gr);
		}
		return gr;
	}

	private HttpSession getSession() {

		// Get the current request and then return its session
		HttpSession session = this.getThreadLocalRequest().getSession();

		return session;
	}

	public boolean checkLogin() {

//		ServletContext otherContext = getServletContext().getContext("/grisu-web-login");
//		if ( otherContext == null ) {
//			System.out.println("Other context is null");
//			return false;
//		}
		
		
		myLogger.debug("Trying to get serviceinterface from session.");
		
		try {
			getServiceInterface();
		} catch (Exception e) {
			myLogger.error("Could not get serviceinterface from session: "+e.getLocalizedMessage());
			return false;
		}
		System.out.println("Serviceinterface in session. All fine...");
		return true;
		
	}

}
