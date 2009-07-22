

package org.vpac.grisu.client.control.jobs;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;

/**
 * Helper methods for dealing with jobs.
 * 
 * @author Markus Binsteiner
 */
public class JobHelper {
	
	static final Logger myLogger = Logger.getLogger(JobHelper.class.getName());
	
	/**
	 * Kills the job on the remote server, deletes the remote jobdirectory and the deletes a possible local directory that contains cached 
	 * files for this job.
	 * @param jobname the name of the job
	 * @param serviceInterface the serviceInterface to use to clean/kill the job remotely
	 * @throws RemoteFileSystemException if the remote jobdirectory could not be removed
	 * @throws VomsException if the remote jobdirectory could not be accessed because the right vo is not available
	 * @throws NoSuchJobException 
	 */
//	public static void cleanJob(String jobname, ServiceInterface serviceInterface) throws RemoteFileSystemException, VomsException, NoSuchJobException {
//		
//		GrisuJobMonitoringObject job = EnvironmentManager.getDefaultManager().getJobManagement().getJob(jobname);
//		
//		if ( job != null ) {
//			BackendFileObject root = EnvironmentManager.getDefaultManager().getJobManagement().getJobRootDirectory(job);
//			if ( root != null )
//				root.deleteLocalRepresentation();
//			//FileHelpers.deleteDirectory(root.getLocalRepresentation(false));
//			job.getServiceInterface().kill(jobname, true);
//		}
//		
//		EnvironmentManager.getDefaultManager().getJobManagement().refreshJobList();
//		
//	}
	
	public static void cleanJobs(String[] jobnames, EnvironmentManager em) throws RemoteFileSystemException, NoSuchJobException {
		
		Set<GrisuFileObject> directoriesToInvalidate = new HashSet<GrisuFileObject>();
		for ( String jobname : jobnames ) {
			
			GrisuJobMonitoringObject job = em.getJobManager().getJob(jobname);
			if ( job != null ) {
				try {
				GrisuFileObject root = em.getJobManager().getJobRootDirectory(job);
				directoriesToInvalidate.add(root.getParent());
				if ( root != null )
					root.deleteLocalRepresentation();
				} catch (Exception e) {
					myLogger.warn("Couldn't delete cached file/invalidate directory: "+e.getLocalizedMessage());
				}
				//FileHelpers.deleteDirectory(root.getLocalRepresentation(false));
				job.getServiceInterface().kill(jobname, true);
			}
			
		}
		
		for ( GrisuFileObject folder : directoriesToInvalidate ) {
			folder.refresh();
		}
//		em.getGlazedJobManagement().refreshJobList();
	}
	
	
	public static void killJobs(String[] jobnames, EnvironmentManager em) throws NoSuchJobException {
		
		for ( String jobname : jobnames ) {
			GrisuJobMonitoringObject job = em.getJobManager().getJob(jobname);
			try {
				job.getServiceInterface().kill(jobname, false);
			} catch (RemoteFileSystemException e) {
				// should never happen since the files aren't touched.
				e.printStackTrace();
			}
		}
	}
	

}
