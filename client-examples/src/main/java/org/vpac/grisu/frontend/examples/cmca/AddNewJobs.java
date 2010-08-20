package org.vpac.grisu.frontend.examples.cmca;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.NoSuchJobException;
import org.vpac.grisu.frontend.control.login.LoginException;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.BatchJobObject;

public class AddNewJobs {

	/**
	 * @param args
	 * @throws NoSuchJobException
	 * @throws BatchJobException
	 * @throws LoginException
	 */
	public static void main(String[] args) throws BatchJobException, NoSuchJobException, LoginException {

		String batchJobName  =  args[0];

		// display commandline login menu if no local proxy exists
		ServiceInterface si = LoginManager.loginCommandline();

		BatchJobObject batchJob = new BatchJobObject(si, batchJobName, false);

		int start = 51;
		int end = 61;

		String pathToInputFiles = batchJob.pathToInputFiles();

		String inputFile1relPath = pathToInputFiles+"inputFile1.txt ";
		String inputFile2relPath = pathToInputFiles+"inputFile2.txt";

		//		for ( int i = start; i<=end; i++ ) {
		//			// create a unique jobname for every job
		//			String jobname = batchJobName+"_"+ i;
		//
		//			System.out.println("Creating job: "+jobname);
		//
		//			// create the single job
		//			JobObject job = new JobObject(si);
		//			job.setJobname(jobname);
		//			// better to set the application to use explicitely because in that case we don't need to use mds (faster)
		//			job.setApplication("UnixCommands");
		//			job.setCommandline("cat "+ inputFile1relPath + " " + inputFile2relPath);
		//
		//			job.setWalltimeInSeconds(60);
		//			// adding the job to the multijob
		//			batchJob.addJob(job);
		//
		//			// only start the newly added jobs and wait for the restart to finish
		//		}

		try {
			batchJob.restart(false, false, true, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
