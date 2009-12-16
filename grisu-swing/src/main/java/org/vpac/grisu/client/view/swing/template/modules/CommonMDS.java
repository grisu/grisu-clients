package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.modules.commonMDS.ApplicationChooserPanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class CommonMDS extends AbstractModulePanel {

	private Email email;
	private WallTime wallTime;
	private CPUs cpus;
	private JobName jobName;
	private ApplicationChooserPanel applicationChooserPanel;

	/**
	 * Create the panel
	 */
	public CommonMDS() {
		super();
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("149dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("67dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("54dlu"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("60dlu"),
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("60dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("106dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("57dlu"),
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getApplicationChooserPanel(), new CellConstraints(2, 6, 5, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		add(getJobName(), new CellConstraints(2, 2, 3, 1,
				CellConstraints.DEFAULT, CellConstraints.FILL));
		add(getCPUs(), new CellConstraints(6, 2, 1, 3, CellConstraints.FILL,
				CellConstraints.FILL));
		add(getWallTime(), new CellConstraints(2, 4, 3, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		add(getEmail(), new CellConstraints(2, 8, 5, 1, CellConstraints.FILL,
				CellConstraints.FILL));
		//
	}

	/**
	 * @return
	 */
	protected ApplicationChooserPanel getApplicationChooserPanel() {
		if (applicationChooserPanel == null) {
			applicationChooserPanel = new ApplicationChooserPanel();
		}
		return applicationChooserPanel;
	}

	/**
	 * @return
	 */
	protected CPUs getCPUs() {
		if (cpus == null) {
			cpus = new CPUs();
		}
		return cpus;
	}

	public SubmissionObject getCurrentlySelectedSubmissionObject() {
		return getApplicationChooserPanel()
				.getCurrentlySelectedSubmissionObject();
	}

	// public void addSubmissionObjectListener(SubmissionObjectListener l) {
	// getApplicationChooserPanel().addSubmissionObjectListener(l);
	// }
	//
	// public void removeSubmissionObjectListener(SubmissionObjectListener l) {
	// getApplicationChooserPanel().removeSubmissionObjectListener(l);
	// }

	/**
	 * @return
	 */
	protected Email getEmail() {
		if (email == null) {
			email = new Email();
		}
		return email;
	}

	/**
	 * @return
	 */
	protected JobName getJobName() {
		if (jobName == null) {
			jobName = new JobName();
		}
		return jobName;
	}

	public JPanel getPanel() {
		return this;
	}

	/**
	 * @return
	 */
	protected WallTime getWallTime() {
		if (wallTime == null) {
			wallTime = new WallTime();
		}
		return wallTime;
	}

	protected void initialize() throws ModuleException {

		try {
			getApplicationChooserPanel()
					.initialize(
							(org.vpac.grisu.client.model.template.modules.CommonMDS) templateModule);

			getJobName().setTemplateNode(
					this.templateModule.getTemplateNodes().get("Jobname"));
			getEmail().setTemplateNode(
					this.templateModule.getTemplateNodes().get("EmailAddress"));
			getWallTime().setTemplateNode(
					this.templateModule.getTemplateNodes().get("Walltime"));
			getCPUs().setTemplateNode(
					this.templateModule.getTemplateNodes().get("CPUs"));
		} catch (TemplateNodePanelException e) {
			throw new ModuleException(this.templateModule, e);
		}

	}
}
