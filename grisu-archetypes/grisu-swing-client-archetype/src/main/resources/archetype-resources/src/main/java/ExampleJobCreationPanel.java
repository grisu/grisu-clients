package ${groupId};

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.frontend.model.job.JobObject;
import org.vpac.grisu.frontend.view.swing.jobcreation.JobCreationPanel;
import org.vpac.grisu.frontend.view.swing.jobcreation.widgets.SubmissionLogPanel;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ExampleJobCreationPanel extends JPanel implements JobCreationPanel {

	// just a label to display something
	private JLabel lblDummyJobSubmission;
	// the button to submit a job
	private JButton btnSubmit;
	// a ready-made widget that, once connected to a JobObject, tracks the
	// progress of a job submission...
	private SubmissionLogPanel submissionLogPanel;

	// the serviceinterface. gets set when the setServiceInterface method is
	// called. we need it in order to submit the job and do everything else...
	private ServiceInterface si;

	/**
	 * Create the panel.
	 */
	public ExampleJobCreationPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC, }));
		add(getLblDummyJobSubmission(), "2, 2");
		add(getBtnSubmit(), "2, 5, right, default");
		add(getSubmissionLogPanel(), "2, 7, fill, fill");

	}

	public boolean createsBatchJob() {
		// we are only creating a single job with this panel
		return false;
	}

	public boolean createsSingleJob() {
		// yep
		return true;
	}

	// creating the submit button and connecting it with the submit action
	private JButton getBtnSubmit() {
		if (btnSubmit == null) {
			btnSubmit = new JButton("Submit");
			btnSubmit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					submitJob();
				}
			});
		}
		return btnSubmit;
	}

	private JLabel getLblDummyJobSubmission() {
		if (lblDummyJobSubmission == null) {
			lblDummyJobSubmission = new JLabel("Dummy job submission");
		}
		return lblDummyJobSubmission;
	}

	// usually you just return the object itself
	public JPanel getPanel() {
		return this;
	}

	// this is what is going to be displayed in the client on the upper left
	// side
	public String getPanelName() {
		return "Example";
	}

	// creating the log panel
	private SubmissionLogPanel getSubmissionLogPanel() {
		if (submissionLogPanel == null) {
			submissionLogPanel = new SubmissionLogPanel();
		}
		return submissionLogPanel;
	}

	// here you return the name of the application package that your client
	// submits jobs for.
	// e.g. Java, Python, MrBayes, ....
	public String getSupportedApplication() {
		return "UnixCommands";
	}

	// convenience method to disable and re-enable the button while the
	// submission is ongoing
	private void lockUI(final boolean lock) {

		SwingUtilities.invokeLater(new Thread() {

			@Override
			public void run() {
				getBtnSubmit().setEnabled(!lock);
			}

		});

	}

	// here we collect the reference to the serviceinterface. Also, usually
	// here's where you do some
	// initialization of your panel (if you need to). E.g. populating a combobox
	// with a list of submission locations or versions of an application
	// package...
	public void setServiceInterface(ServiceInterface si) {
		this.si = si;
	}

	// the method to actually submit the job
	private void submitJob() {

		// we submit the job in it's own thread in order to not block the swing
		// ui
		new Thread() {
			@Override
			public void run() {
				try {
					// disable the submit button so the user can't inadvertently
					// submit 2 jobs in a row
					lockUI(true);

					// now, let's create the job
					JobObject job = new JobObject(si);
					// ... and connect it to the submission log panel so the
					// user can see that there's something going on...
					getSubmissionLogPanel().setJobObject(job);
					// .. every job needs its own unique jobname. You can either
					// hardcode it (but then you'll only be able to submit one
					// job.
					// or you use the setUniqueJobname or setTimestampJobname
					// methods, which will do it for you. Or you ask the user...
					job.setTimestampJobname("helloworldJob");
					// this is required and the most important part of the job
					// creation process. Grisu needs to know what job you want
					// to submit, after all...
					job.setCommandline("echo hello gridworld!");
					// setting the walltime of a job is also important
					job.setWalltimeInSeconds(60);

					// the next step is optional, but it is recommended to set
					// the application if you know it. jobsubmission will be
					// faster, because otherwise Grisu has to figure out which
					// application package the executable you are using belongs
					// to...
					job.setApplication("UnixCommands");

					// this will only work if you are in the BeSTGRID VO.
					// job.createJob("/ARCS/BeSTGRID");
					// this uses the default (or last-used) VO. Better to ask
					// the user, though. Or hardcode, if there is only one VO...
					job.createJob();
					// last not least, we stage in files and submit the job
					job.submitJob();

				} catch (Exception e) {
					// if something goes wrong, we want to show the user
					ErrorInfo info = new ErrorInfo("Job submission error",
							"Can't submit job:\n\n" + e.getLocalizedMessage(),
							null, "Error", e, Level.SEVERE, null);

					JXErrorPane pane = new JXErrorPane();
					pane.setErrorInfo(info);
					// the following line can be used to show a button to submit
					// a bug/ticket to a bug tracking system
					// pane.setErrorReporter(new GrisuErrorReporter());

					JXErrorPane.showDialog(
							ExampleJobCreationPanel.this.getRootPane(), pane);
				} finally {
					// enable the submit button again
					lockUI(false);
				}

			}
		}.start();
	}
}