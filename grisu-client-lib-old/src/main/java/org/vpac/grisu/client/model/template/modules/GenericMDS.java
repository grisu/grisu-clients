package org.vpac.grisu.client.model.template.modules;

import java.util.Map;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

/**
 * This is a generic job properties module that focuses on the version of the
 * application instead of where you want to submit your job. So you select the
 * version of the application first (or tell Grisu you don't care about the
 * version) and then you decide where to submit the job to.
 * 
 * It implements the SubmissionObjectHolder interface so panels like the
 * MDSCommandLine one can connect to it.
 * 
 * @author Markus Binsteiner
 * 
 */
public class GenericMDS extends AbstractModule {

	static final Logger myLogger = Logger.getLogger(GenericMDS.class.getName());

	public static final String VERSION_TEMPLATE_TAG_NAME = "Version";
	public static final String HOSTNAME_TEMPLATE_TAG_NAME = "HostName";

	public static final String[] MODULES_USED = new String[] { "Jobname",
			VERSION_TEMPLATE_TAG_NAME, "Walltime", "CPUs",
			HOSTNAME_TEMPLATE_TAG_NAME, "MinMem", "ExecutionFileSystem",
			"EmailAddress" };

	private SubmissionObject currentlySelecteSubmissionObject = null;

	// private DefaultTemplateNodeValueSetter executionFileSystemSetter = new
	// DefaultTemplateNodeValueSetter();
	// private DefaultTemplateNodeValueSetter hostnameSetter = new
	// DefaultTemplateNodeValueSetter();
	// private DefaultTemplateNodeValueSetter versionSetter = new
	// DefaultTemplateNodeValueSetter();

	public GenericMDS(JsdlTemplate template) {
		super(template);

		// template.getTemplateNodes().get(TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME).setTemplateNodeValueSetter(executionFileSystemSetter);
		// template.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME).setTemplateNodeValueSetter(hostnameSetter);
		// template.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME).setTemplateNodeValueSetter(versionSetter);
	}

	public SubmissionObject getCurrentSubmissionObject() {
		return currentlySelecteSubmissionObject;
	}

	public String getModuleName() {
		return "General";
	}

	@Override
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return MODULES_USED;
	}

	@Override
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {

		// nothing to do here
	}

	public void process() throws TemplateModuleProcessingException {
		// nothing to do here
	}

	public void reset() {
		// nothing to do here
	}

	public void setCurrentlySelectedSubmissionObject(SubmissionObject so) {
		this.currentlySelecteSubmissionObject = so;
	}

	// // -------------------------------------------------------------------
	// // EventStuff
	// private Vector<SubmissionObjectListener> submissionObjectListener;
	//
	// // change the template and forward this to possible other listeners
	// public void submissionObjectChanged(SubmissionObject so) {
	//		
	// String site =
	// template.getEnvironmentManager().lookupSite(EnvironmentManager.QUEUE_TYPE,
	// so.getCurrentSubmissionLocation().getLocation());
	// String fqan = template.getEnvironmentManager().getDefaultFqan();
	//		
	// String stagingFS =
	// so.getCurrentSubmissionLocation().getFirstStagingFileSystem(fqan);
	//		
	// if ( stagingFS == null ) {
	// myLogger.warn("Couldn't find staging filesystem for submission queue: "+so.getCurrentSubmissionLocation().getLocation()+". Not setting anything.");
	// return;
	// }
	//		
	// myLogger.debug(("Using staging filesystem: "+stagingFS+" for submissionQueue: "+so.getCurrentSubmissionLocation().getLocation()));
	//		
	// executionFileSystemSetter.setExternalSetValue(stagingFS);
	//		
	// versionSetter.setExternalSetValue(so.getCurrentVersion());
	// hostnameSetter.setExternalSetValue(so.getCurrentSubmissionLocation().getLocation());
	//		
	// myLogger.debug("New submissionLocation: "+so.getCurrentSubmissionLocation().getLocation()+". Staging filesystem: "+stagingFS+". Version to use: "+so.getCurrentVersion());
	// // if we have no submissionObjectListeners, do nothing...
	// if (submissionObjectListener != null &&
	// !submissionObjectListener.isEmpty()) {
	// // create the event object to send
	//
	// // make a copy of the listener list in case
	// // anyone adds/removes mountPointsListeners
	// Vector targets;
	// synchronized (this) {
	// targets = (Vector) submissionObjectListener.clone();
	// }
	//
	// // walk through the listener list and
	// // call the gridproxychanged method in each
	// Enumeration e = targets.elements();
	// while (e.hasMoreElements()) {
	// SubmissionObjectListener l = (SubmissionObjectListener) e.nextElement();
	// l.submissionObjectChanged(so);
	// }
	// }
	// }
	//	
	//
	// // register a listener
	// synchronized public void
	// addSubmissionObjectListener(SubmissionObjectListener l) {
	// if (submissionObjectListener == null)
	// submissionObjectListener = new Vector();
	// submissionObjectListener.addElement(l);
	// }
	//
	// // remove a listener
	// synchronized public void
	// removeSubmissionObjectListener(SubmissionObjectListener l) {
	// if (submissionObjectListener == null) {
	// submissionObjectListener = new Vector<SubmissionObjectListener>();
	// }
	// submissionObjectListener.removeElement(l);
	// }

}
