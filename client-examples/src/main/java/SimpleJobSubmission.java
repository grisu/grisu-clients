import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.control.login.LoginManager;
import org.vpac.grisu.frontend.model.job.JobObject;

public class SimpleJobSubmission {

	public static void main(String[] args) throws Exception {

		// ServiceInterface si = LoginManager.loginCommandline();
		final ServiceInterface si = LoginManager
				.loginCommandline("BeSTGRID-DEV");

		final JobObject job = new JobObject(si);
		job.setApplication("java");
		job.setApplicationVersion("1.6.0-06");
		job.setUniqueJobname("find_job2");
		job.setCommandline("find .");

		job.setWalltimeInSeconds(60);

		// job.addInputFileUrl("gsiftp://ng2hpc.ceres.auckland.ac.nz/home/grid-admin/DC_au_DC_org_DC_arcs_DC_slcs_O_ARCS_IdP_CN_Markus_Binsteiner_wArwA1Ra5jf6RG_HIZy45nORR_g/tmp/");
		job.addInputFileUrl("/home/markus/tmp");

		job.createJob("/ARCS/NGAdmin");
		job.submitJob();

		System.out.println("Main thread finished.");
	}

}
