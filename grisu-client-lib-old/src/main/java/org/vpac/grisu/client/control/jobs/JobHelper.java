package org.vpac.grisu.client.control.jobs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.control.exceptions.RemoteFileSystemException;
import org.vpac.security.light.vomsProxy.VomsException;

/**
 * Helper methods for dealing with jobs.
 * 
 * @author Markus Binsteiner
 */
public class JobHelper {

	static final Logger myLogger = Logger.getLogger(JobHelper.class.getName());

	/**
	 * Kills the job on the remote server, deletes the remote jobdirectory and
	 * the deletes a possible local directory that contains cached files for
	 * this job.
	 * 
	 * @param jobname
	 *            the name of the job
	 * @param serviceInterface
	 *            the serviceInterface to use to clean/kill the job remotely
	 * @throws RemoteFileSystemException
	 *             if the remote jobdirectory could not be removed
	 * @throws VomsException
	 *             if the remote jobdirectory could not be accessed because the
	 *             right vo is not available
	 * @throws NoSuchJobException
	 */
	// public static void cleanJob(String jobname, ServiceInterface
	// serviceInterface) throws RemoteFileSystemException, VomsException,
	// NoSuchJobException {
	//		
	// GrisuJobMonitoringObject job =
	// EnvironmentManager.getDefaultManager().getJobManagement().getJob(jobname);
	//		
	// if ( job != null ) {
	// BackendFileObject root =
	// EnvironmentManager.getDefaultManager().getJobManagement().getJobRootDirectory(job);
	// if ( root != null )
	// root.deleteLocalRepresentation();
	// //FileHelpers.deleteDirectory(root.getLocalRepresentation(false));
	// job.getServiceInterface().kill(jobname, true);
	// }
	//		
	// EnvironmentManager.getDefaultManager().getJobManagement().refreshJobList();
	//		
	// }

	public static void cleanJobs(String[] jobnames, EnvironmentManager em,
			int concurrentThreads) throws RemoteFileSystemException,
			NoSuchJobException {

		ExecutorService killJobExecutor = Executors
				.newFixedThreadPool(concurrentThreads);

		final JobManager jobmanager = em.getJobManager();
		final ServiceInterface serviceInterface = em.getServiceInterface();
		final Set<GrisuFileObject> directoriesToInvalidate = Collections
				.synchronizedSet(new HashSet<GrisuFileObject>());

		for (final String jobname : jobnames) {

			Thread killAndCleanThread = new Thread() {
				public void run() {

					try {
						final GrisuJobMonitoringObject job = jobmanager
								.getJob(jobname);
						if (job != null) {
							try {
								GrisuFileObject root = jobmanager
										.getJobRootDirectory(job);
								directoriesToInvalidate.add(root.getParent());
								if (root != null)
									root.deleteLocalRepresentation();
							} catch (Exception e) {
								myLogger
										.warn("Couldn't delete cached file/invalidate directory: "
												+ e.getLocalizedMessage());
							}
							// FileHelpers.deleteDirectory(root.getLocalRepresentation(false));
							serviceInterface.kill(jobname, true);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			};
			killJobExecutor.execute(killAndCleanThread);
		}

		killJobExecutor.shutdown();

		try {
			killJobExecutor.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (GrisuFileObject folder : directoriesToInvalidate) {
			folder.refresh();
		}
		// em.getGlazedJobManagement().refreshJobList();
	}

	public static void killJobs(String[] jobnames, EnvironmentManager em,
			int simultaneousThreads) throws NoSuchJobException {

		ExecutorService killJobExecutor = Executors
				.newFixedThreadPool(simultaneousThreads);

		final ServiceInterface si = em.getServiceInterface();

		for (final String jobname : jobnames) {
			// final GrisuJobMonitoringObject job =
			// em.getJobManager().getJob(jobname);
			Thread killThread = new Thread() {
				public void run() {

					try {
						si.kill(jobname, false);
					} catch (Exception e) {
						// should never happen since the files aren't touched.
						e.printStackTrace();
					}
				}
			};
			killJobExecutor.execute(killThread);

		}

		killJobExecutor.shutdown();

		try {
			killJobExecutor.awaitTermination(3600, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
