package org.vpac.grisu.client.model.template.modules;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.template.JsdlTemplate;
import org.vpac.grisu.client.model.template.nodes.TemplateNode;

public class CommonMDS extends AbstractModule implements SubmissionObjectHolder {
	
	static final Logger myLogger = Logger.getLogger(CommonMDS.class.getName());
	
	public static final String[] MODULES_USED = new String[]{
		TemplateTagConstants.JOBNAME_TAG_NAME, TemplateTagConstants.APPLICATION_TAG_NAME,
		TemplateTagConstants.WALLTIME_TAG_NAME, TemplateTagConstants.CPUS_TAG_NAME, TemplateTagConstants.HOSTNAME_TAG_NAME,
		TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME, TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME, TemplateTagConstants.MODULE_TAG_NAME
	};
	
	private SubmissionObject currentlySelecteSubmissionObject = null;
	
	public CommonMDS(JsdlTemplate template) {
		super(template);
	}

	@Override
	public String[] getTemplateNodeNamesThisModuleClaimsResponsibleFor() {
		return MODULES_USED;
	}

	@Override
	public void initializeTemplateNodes(Map<String, TemplateNode> templateNodes) {

		// nothing to do here
	}

	public String getModuleName() {
		return "General";
	}

	public void process() throws TemplateModuleProcessingException {
		// nothing to do here
	}

	public void reset() {
		// nothing to do here
	}

	public SubmissionObject getCurrentSubmissionObject() {
		return currentlySelecteSubmissionObject;
	}
	
	public void setCurrentlySelectedSubmissionObject(SubmissionObject so) {
		this.currentlySelecteSubmissionObject = so;
	}
	
	// -------------------------------------------------------------------
	// EventStuff
	private Vector<SubmissionObjectListener> submissionObjectListener;

	public void submissionObjectChanged(SubmissionObject so) {
		
		if ( so == null ) {
			myLogger.error("SubmissionObject is null. Not firing the event.");
			return;
		}
		
		templateNodes.get("Application").getTemplateNodeValueSetter().setExternalSetValue(so.getCurrentApplicationName());
		templateNodes.get("HostName").getTemplateNodeValueSetter().setExternalSetValue(so.getCurrentSubmissionLocation().getLocation());
		
		String site = template.getEnvironmentManager().lookupSite(EnvironmentManager.QUEUE_TYPE, so.getCurrentSubmissionLocation().getLocation());
		String fqan = template.getEnvironmentManager().getDefaultFqan();
		
//		String[] stagingFS = environmentManager.lookupStagingFileSystemsForQueue(so.getLocation().getLocation());
		
//		MountPoint mp = null;
		
//		for ( String fs : stagingFS ) {
//			mp = environmentManager.getDefaultMountPointForSiteAndFqan(environmentManager.lookupSite(EnvironmentManager.FILE_URL_TYPE, fs), fqan);
//			mp = environmentManager.getMountPointsForSubmissionLocationAndFqan(so.getLocation().getLocation(), fqan).iterator().next();
//			if ( mp != null )
//				break;
//		}
		
		
//		MountPoint mp = environmentManager.getDefaultMountPointForSiteAndFqan(site, fqan);
//		MountPoint mp = null;
//		if ( mp == null ) {
			// means no mountpoint available
////			myLogger.debug("No mountpoint available. Not firing event.");
//			return;
//		}
		
//		myLogger.debug(("Setting mountpoing: "+mp.getMountpoint()+" for submissionQueue: "+so.getLocation().getLocation()));
//		executionFileSystemSetter.setExternalSetValue(mp.getRootUrl());
		
		String stagingFS = so.getCurrentSubmissionLocation().getFirstStagingFileSystem(fqan);
		
		if ( stagingFS == null ) {
			myLogger.warn("Couldn't find staging filesystem for submission queue: "+so.getCurrentSubmissionLocation().getLocation()+". Not setting anything.");
			return;
		}
		
		myLogger.debug(("Using staging filesystem: "+stagingFS+" for submissionQueue: "+so.getCurrentSubmissionLocation().getLocation()));
		templateNodes.get("ExecutionFileSystem").getTemplateNodeValueSetter().setExternalSetValue(stagingFS);
		
		String[] modules = so.getCurrentModules();
		
		if ( modules == null ) {
			myLogger.error("Not setting anything. There seems to be an error somewhere.");
			return;
		}
		
		if ( modules.length == 0 ) {
			templateNodes.get("Module").getTemplateNodeValueSetter().setExternalSetValue("");
		} else {
			//TODO change that later for more modules
			templateNodes.get("Module").getTemplateNodeValueSetter().setExternalSetValue(modules[0]);
		}
		
		// if we have no submissionObjectListeners, do nothing...
		if (submissionObjectListener != null && !submissionObjectListener.isEmpty()) {
			// create the event object to send

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector targets;
			synchronized (this) {
				targets = (Vector) submissionObjectListener.clone();
			}

			// walk through the listener list and
			// call the gridproxychanged method in each
			Enumeration e = targets.elements();
			while (e.hasMoreElements()) {
				SubmissionObjectListener l = (SubmissionObjectListener) e.nextElement();
				l.submissionObjectChanged(so);
			}
			}
		}
	

	// register a listener
	synchronized public void addSubmissionObjectListener(SubmissionObjectListener l) {
		if (submissionObjectListener == null)
			submissionObjectListener = new Vector();
		submissionObjectListener.addElement(l);
	}

	// remove a listener
	synchronized public void removeSubmissionObjectListener(SubmissionObjectListener l) {
		if (submissionObjectListener == null) {
			submissionObjectListener = new Vector<SubmissionObjectListener>();
		}
		submissionObjectListener.removeElement(l);
	}

}
