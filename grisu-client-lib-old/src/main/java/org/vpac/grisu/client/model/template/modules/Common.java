package org.vpac.grisu.client.model.template.modules;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.control.exceptions.SubmissionLocationException;
import org.vpac.grisu.client.control.utils.MountPointEvent;
import org.vpac.grisu.client.control.utils.MountPointsListener;
import org.vpac.grisu.client.model.ApplicationObject;
import org.vpac.grisu.client.model.NoMDSApplicationObject;
import org.vpac.grisu.client.model.SubmissionLocation;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.VersionObject;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.DefaultTemplateNodeValueSetter;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;
import org.vpac.grisu.client.model.template.nodes.TemplateNodeValueSetter;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.model.FqanEvent;
import org.vpac.grisu.model.FqanListener;

import au.org.arcs.jcommons.utils.JsdlHelpers;

/**
 * @author Markus Binsteiner
 * 
 */
public class Common extends AbstractModule implements FqanListener,
		MountPointsListener, SubmissionObjectHolder {

	static final Logger myLogger = Logger.getLogger(Common.class.getName());

	public static final String TEMPLATE_MODULE_NAME = "Basic job properties";

	public static final String[] MODULES_USED = new String[] {
			TemplateTagConstants.JOBNAME_TAG_NAME,
			TemplateTagConstants.WALLTIME_TAG_NAME,
			TemplateTagConstants.CPUS_TAG_NAME,
			TemplateTagConstants.HOSTNAME_TAG_NAME,
			TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME,
			TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME,
			TemplateTagConstants.MODULE_TAG_NAME };

	private DefaultTemplateNodeValueSetter hostnameSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter executionFsSetter = new DefaultTemplateNodeValueSetter();
	private DefaultTemplateNodeValueSetter moduleSetter = new DefaultTemplateNodeValueSetter();

	private EnvironmentManager em = null;
	private String application = null;
	private ApplicationObject currentApplication = null;
	private NoMDSApplicationObject currentNonMDSApplicationObject = null;
	private SubmissionLocation currentSubmissionLocation = null;
	private SubmissionObject currentSubmissionObject = null;
	private String currentFqan = null;
	private String currentStagingFS = null;

	private boolean useMDS = true;

	private int preferredExecutableType = ApplicationObject.EXECUTABLE_TYPE_UNDEFINED;

	// just for caching
	private Map<String, Set<SubmissionLocation>> currentSitesNonMDS = null;
	private Map<String, Set<SubmissionLocation>> currentSitesMDS = null;

	private boolean validSubmissionObject = false;

	// -----------------------------------------------------------
	// Event stuff
	private Vector<SubmissionObjectListener> submissionObjectListeners;

	/**
		 * This generic module stores a state internally that is dependent on
		 * setting a combination of application, submission location and fqan. If
		 * you set either application, submission location or fqan you might end up
		 * with an internal state that is inconclusive.
		 * 
		 * So, what you do is: first set the application.
		 * 
		 * Then either set the fqan and use
		 * {@link #getAvailableSubmissionLocationsForCurrentVO()} to find out which
		 * submissionlocations are available or set the submissionLocation and use
		 * {@link #getAvailableVOsForCurrentSubmissionLocation()} to find out which
		 * fqans are available (the latter doesn't really make sense in a normal
		 * workflow).
		 * 
		 * In a normal situation you would call the constructor
		 * {@link #Common(JsdlTemplate)}, setApplication and setVO and the
		 * use {@link #getAvailableSubmissionLocationsForCurrentVO()} and use one of
		 * the returned values with
		 * {@link #setSubmissionLocation(SubmissionLocation).
		 * 
		 * @param template
		 *            the template
		 */
		public Common(JsdlTemplate template) {
			super(template);
			em = template.getEnvironmentManager();
			em.addFqanListener(this);
			em.addMountPointListener(this);
		}

	// register a listener
	synchronized public void addSubmissionObjectListener(
			SubmissionObjectListener l) {
		if (submissionObjectListeners == null)
			submissionObjectListeners = new Vector<SubmissionObjectListener>();
		submissionObjectListeners.addElement(l);
	}

	private void calculateSubmissionObject() {

		if (application == null) {
			invalidateSubmissionObject();
			return;
		}
		if (currentSubmissionLocation == null) {
			invalidateSubmissionObject();
			return;
		}

		if (useMDS) {
			try {
				getCurrentApplication().setCurrentSubmissionLocation(
						currentSubmissionLocation);
			} catch (SubmissionLocationException e) {
				// e.printStackTrace();
				invalidateSubmissionObject();
				return;
			}
		} else {
			try {
				getCurrentNonMDSApplicationObject()
						.setCurrentSubmissionLocation(currentSubmissionLocation);
			} catch (SubmissionLocationException e) {
				e.printStackTrace();
				invalidateSubmissionObject();
				return;
			}
		}

		// em.getServiceInterface().get
		currentStagingFS = currentSubmissionLocation
				.getFirstStagingFileSystem(currentFqan);
		if (currentStagingFS == null || "".equals(currentStagingFS)) {
			invalidateSubmissionObject();
			return;
		}

		validSubmissionObject = true;

		if (useMDS) {
			currentSubmissionObject = getCurrentApplication();
		} else {
			currentSubmissionObject = getCurrentNonMDSApplicationObject();
		}

		currentSubmissionObject
				.setPreferredExecutableType(preferredExecutableType);

		hostnameSetter.setExternalSetValue(currentSubmissionObject
				.getCurrentSubmissionLocation().getLocation());
		executionFsSetter.setExternalSetValue(currentStagingFS);

		if (useMDS) {
			String[] modules = currentSubmissionObject.getCurrentModules();
			if (modules != null && modules.length >= 1) {
				moduleSetter.setExternalSetValue(modules[0]);
			}
		}

		fireNewValidSubmissionObjectEvent(currentSubmissionObject);
	}

private void fireNewValidSubmissionObjectEvent(SubmissionObject newSO) {

	myLogger.debug("Fire submissionPanel event: new site("
			+ newSO.getCurrentSubmissionLocation().getSite() + "), ("
			+ newSO.getCurrentSubmissionLocation().getLocation() + ")"
			+ "\n" + newSO.getCurrentApplicationName() + " (Version: "
			+ newSO.getCurrentVersion() + ").");
	// if we have no mountPointsListeners, do nothing...
	if (submissionObjectListeners != null
			&& !submissionObjectListeners.isEmpty()) {
		// create the event object to send

		// make a copy of the listener list in case
		// anyone adds/removes mountPointsListeners
		Vector<SubmissionObjectListener> sitePanelTargets;
		synchronized (this) {
			sitePanelTargets = (Vector<SubmissionObjectListener>) submissionObjectListeners
					.clone();
		}

		// walk through the listener list and
		// call the gridproxychanged method in each
		Enumeration<SubmissionObjectListener> e = sitePanelTargets
				.elements();
		while (e.hasMoreElements()) {
			SubmissionObjectListener l = e.nextElement();
			l.submissionObjectChanged(newSO);
		}
	}
}

	public void fqansChanged(FqanEvent event) {

		invalidateCache();
		if (FqanEvent.DEFAULT_FQAN_CHANGED == event.getEvent_type()) {
			setVO(event.getFqan());
		}

	}

	public Set<SubmissionLocation> getAvailableQueuesForSite(String site) {

		return getAvailableSitesForCurrentVO().get(site);
	}

	public Map<String, Set<SubmissionLocation>> getAvailableSitesForCurrentVO() {

		if (useMDS) {
			if (currentSitesMDS == null) {
				currentSitesMDS = new TreeMap<String, Set<SubmissionLocation>>();
				for (SubmissionLocation loc : getAvailableSubmissionLocationsForCurrentVO()) {
					String site = loc.getSite();
					Set<SubmissionLocation> siteLocs = currentSitesMDS
							.get(site);
					if (siteLocs == null) {
						siteLocs = new TreeSet<SubmissionLocation>();
						currentSitesMDS.put(site, siteLocs);
					}
					siteLocs.add(loc);
				}
			}
			return currentSitesMDS;
		} else {
			if (currentSitesNonMDS == null) {
				currentSitesNonMDS = new TreeMap<String, Set<SubmissionLocation>>();
				Set<SubmissionLocation> allAvailSubLocs = getAvailableSubmissionLocationsForCurrentVO();
				for (SubmissionLocation loc : allAvailSubLocs) {
					String site = loc.getSite();
					Set<SubmissionLocation> siteLocs = currentSitesNonMDS
							.get(site);
					if (siteLocs == null) {
						siteLocs = new TreeSet<SubmissionLocation>();
						currentSitesNonMDS.put(site, siteLocs);
					}
					siteLocs.add(loc);
				}
			}
			return currentSitesNonMDS;
		}

	}

	/**
	 * Returns all submissionlocations for the set fqan and application.
	 * 
	 * @return the submissionLocations or null if the application is not set
	 */
	public Set<SubmissionLocation> getAvailableSubmissionLocationsForCurrentVO() {

		if (useMDS) {
			return getCurrentApplication()
					.getActualAvailableSubmissionLocations(currentFqan);
		} else {

			return em.getAllAvailableSubmissionLocationsForFqan(currentFqan);
		}

	}

	/**
	 * Returns all fqans that are possible for the set submissionlocation and
	 * application.
	 * 
	 * @return the fqans
	 * @throws SubmissionLocationException
	 *             if there is no valid submission location set
	 * @throws JobCreationException
	 *             if the application is not set
	 */
	public Set<String> getAvailableVOsForCurrentSubmissionLocation()
			throws SubmissionLocationException, JobCreationException {

		if (application == null) {
			throw new JobCreationException("No application set.");
		}

		if (useMDS) {
			if (currentSubmissionLocation == null
					|| getCurrentApplication().getCurrentSubmissionLocation() == null) {
				throw new SubmissionLocationException(
						"No submissionLocation set.");
			}
		} else {
			if (currentSubmissionLocation == null
					|| getCurrentNonMDSApplicationObject() == null) {
				throw new SubmissionLocationException(
						"No submissionLocation set.");
			}
		}
		return em
				.getPossibleFqansForSubmissionLocation(currentSubmissionLocation);

	}

	private ApplicationObject getCurrentApplication() {
		if (currentApplication == null) {
			this.currentApplication = new ApplicationObject(application, em
					.getAllOfTheUsersSites(), em);
		}
		return currentApplication;
	}

	public String getCurrentFqan() {
		return currentFqan;
	}

	public Set<String> getCurrentlyAvailableSites() {
		return getAvailableSitesForCurrentVO().keySet();
	}

	private NoMDSApplicationObject getCurrentNonMDSApplicationObject() {
		if (currentNonMDSApplicationObject == null) {
			this.currentNonMDSApplicationObject = new NoMDSApplicationObject(
					application, em);
		}
		return currentNonMDSApplicationObject;
	}

	/**
	 * Returns a submission location if the objects internal state is
	 * conclusive.
	 * 
	 * @return a submission object which can be used to submit a job.
	 * @throws JobCreationException
	 *             if the objects internal stat is inconclusive.
	 */
	public SubmissionObject getCurrentSubmissionObject()
			throws JobCreationException {
		if (!validSubmissionObject) {
			throw new JobCreationException(
					"Not enough input to calculate submission object in module "
							+ this.getModuleName(), null);
		}

		return currentSubmissionObject;
	}

	// not to confuse with the calculated "Module" for the application. This one
	// is Grisu internal.
	public String getModuleName() {
		return TEMPLATE_MODULE_NAME;
	}

	/**
	 * Returns whether there exists a preferred executable type. TemplatePanels
	 * for example can then determine which executable to use. Returns either
	 * {@link #EXECUTABLE_TYPE_UNDEFINED}, {@link #EXECUTABLE_TYPE_SERIAL} or
	 * {@link #EXECUTABLE_TYPE_PARALLEL}
	 * 
	 * @return the preferred executable type
	 */
	public int getPreferredExecutableType() {
		return preferredExecutableType;
	}

	@Override
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return MODULES_USED;
	}

	/**
	 * Returns all available Versions for the current
	 * application/submissionLocation combination. Only makes sense if
	 * useMDS=true.
	 * 
	 * @return all versions or null if the information is not available (for
	 *         example because useMDS=false is set or not enough values are set
	 *         to be able to calculate them).
	 */
	public Set<String> getVersions() {

		if (useMDS) {
			if (!isReadyToSubmit()) {
				return null;
			}

			Set<VersionObject> versions = getCurrentApplication()
					.getVersionsForSubmissionLocation(currentSubmissionLocation);
			Set<String> result = new TreeSet<String>();

			for (VersionObject version : versions) {
				result.add(version.getCurrentVersion());
			}
			return result;
		} else {
			return null;
		}
	}

	@Override
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {

		initValueSetters(templateNodes);

	}

	protected void initValueSetters(Map<String, TemplateNode> templateNodes) {

		// initialize email just in case because it's optional
		TemplateNodeValueSetter tempSetter = templateNodes.get(
				TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME)
				.getTemplateNodeValueSetter();
		if (tempSetter == null) {
			templateNodes.get(TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME)
					.setTemplateNodeValueSetter(
							new DefaultTemplateNodeValueSetter());
		}

		templateNodes.get(TemplateTagConstants.HOSTNAME_TAG_NAME)
				.setTemplateNodeValueSetter(hostnameSetter);
		templateNodes.get(TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME)
				.setTemplateNodeValueSetter(executionFsSetter);
		templateNodes.get(TemplateTagConstants.MODULE_TAG_NAME)
				.setTemplateNodeValueSetter(moduleSetter);

		// we connect these ones just in case they are not connected to later on
		templateNodes.get(TemplateTagConstants.JOBNAME_TAG_NAME)
				.setTemplateNodeValueSetter(
						new DefaultTemplateNodeValueSetter());
		templateNodes.get(TemplateTagConstants.WALLTIME_TAG_NAME)
				.setTemplateNodeValueSetter(
						new DefaultTemplateNodeValueSetter());
		templateNodes.get(TemplateTagConstants.CPUS_TAG_NAME)
				.setTemplateNodeValueSetter(
						new DefaultTemplateNodeValueSetter());
		templateNodes.get(TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME)
				.setTemplateNodeValueSetter(
						new DefaultTemplateNodeValueSetter());

	}

	private void invalidateCache() {
		currentSitesNonMDS = null;
		currentSitesMDS = null;
		currentApplication = null;
	}

	private void invalidateSubmissionObject() {
		validSubmissionObject = false;
		currentStagingFS = null;
		currentSubmissionObject = null;
		hostnameSetter.setExternalSetValue(null);
		executionFsSetter.setExternalSetValue(null);
		// module should be ok
	}

	public boolean isReadyToSubmit() {
		return validSubmissionObject;
	}

	public void mountPointsChanged(MountPointEvent mpe)
			throws RemoteFileSystemException {
		invalidateCache();
		invalidateSubmissionObject();
		calculateSubmissionObject();
	}

	public void process() throws TemplateModuleProcessingException {
		// nothing to do here
	}

	// remove a listener
	synchronized public void removeSubmissionObjectListener(
			SubmissionObjectListener l) {
		if (submissionObjectListeners == null) {
			submissionObjectListeners = new Vector<SubmissionObjectListener>();
		}
		submissionObjectListeners.removeElement(l);
	}

	public void setApplication(String application) {

		currentSitesMDS = null;

		if (application != null && !"".equals(application)) {
			JsdlHelpers.setApplicationName(template.getTemplateDocument(),
					application);
			this.application = application;

			calculateSubmissionObject();
		}
	}

	public void setCPUs(int no_of_cpus) {
		String cpus = new Integer(no_of_cpus).toString();
		templateNodes.get(TemplateTagConstants.CPUS_TAG_NAME)
				.getTemplateNodeValueSetter().setExternalSetValue(cpus);
	}

	/**
	 * Sets the applicationwide default VO
	 */
	public void setDefaultVO() {
		setVO(em.getDefaultFqan());
	}

	public void setEmailAddress(String email) {
		templateNodes.get(TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME)
				.getTemplateNodeValueSetter().setExternalSetValue(email);
	}

	public void setJobname(String jobname) {
		templateNodes.get(TemplateTagConstants.JOBNAME_TAG_NAME)
				.getTemplateNodeValueSetter().setExternalSetValue(jobname);
	}

	/**
	 * Sets the module for this job. Makes only sense if useMds=false. If
	 * useMds=true and this value is set, then the calculated module is
	 * overwritten until a new subissionlocation, fqan or application.
	 * 
	 * @param module
	 *            the module to load.
	 */
	public void setModule(String module) {
		moduleSetter.setExternalSetValue(module);
	}

	public void setPreferredExecutableType(int type) {
		preferredExecutableType = type;

		calculateSubmissionObject();

	}

	/**
	 * Sets the submission location for the current application and the current
	 * fqan. This deletes any possibly set versions.
	 * 
	 * @param subLoc
	 *            the location
	 */
	public void setSubmissionLocation(SubmissionLocation subLoc) {

		this.currentSubmissionLocation = subLoc;
		calculateSubmissionObject();
	}

	/**
	 * Sets the version to use for submission. Only call this after you set up
	 * application, VO and submissionlocation. This makes only sense if you use
	 * mds for the module.
	 * 
	 * @param version
	 *            the version
	 * @throws JobCreationException
	 *             if the job isn't set up or if the version is not available.
	 */
	public void setVersion(String version) throws JobCreationException {

		if (useMDS) {
			if (!isReadyToSubmit()) {
				throw new JobCreationException(
						"Couldn't set version because of inconclusive internal job state.");
			}
			Set<VersionObject> versions = getCurrentApplication()
					.getVersionsForSubmissionLocation(currentSubmissionLocation);

			for (VersionObject versionObject : versions) {
				if (versionObject.getCurrentVersion().equals(version)) {
					currentSubmissionObject = versionObject;
					try {
						currentSubmissionObject
								.setCurrentSubmissionLocation(currentSubmissionLocation);
						String[] modules = currentSubmissionObject
								.getCurrentModules();
						if (modules != null && modules.length > 0) {
							moduleSetter.setExternalSetValue(modules[0]);
						}
						fireNewValidSubmissionObjectEvent(currentSubmissionObject);
					} catch (SubmissionLocationException e) {
						throw new JobCreationException(
								"Could not set submission location for version.");
					}
					return;
				}
			}
			throw new JobCreationException(
					"Could not set version because it is not available at the current submission location.");
		}
	}

	public void setVO(String fqan) {

		if ((this.currentFqan != null && !this.currentFqan.equals(fqan))
				|| (this.currentFqan == null && fqan == null)) {
			currentSitesMDS = null;
			currentSitesNonMDS = null;
		}

		// if (application == null) {
		// throw new JobCreationException("Application is not set yet.");
		// }

		this.currentFqan = fqan;
		calculateSubmissionObject();
	}

	public void setWalltime(long walltimeInSeconds) {
		String walltime = new Long(walltimeInSeconds).toString();
		templateNodes.get(TemplateTagConstants.WALLTIME_TAG_NAME)
				.getTemplateNodeValueSetter().setExternalSetValue(walltime);
	}

	/**
	 * Sets whether to try to get application details from mds or whether to
	 * display all available submission queues and trust the user to know where
	 * he is able to submit the job to...
	 * 
	 * @param useMds
	 *            whether to use mds or not
	 */
	public void useMds(boolean useMds) {
		this.useMDS = useMds;
		calculateSubmissionObject();
	}

	/**
	 * Inidicates whether this module uses mds to get application details or not
	 * 
	 * @return true if mds is used -- false if not
	 */
	public boolean usesMds() {
		return this.useMDS;
	}

}
