package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.template.TemplateHelperUtils;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.modules.common.QueuePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.ValueListener;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class Common extends AbstractModulePanel implements ValueListener {

	public static final String USE_MDS_CONFIG_OPTION_KEY = "useMds";

	private QueuePanel queuePanel;
	static final Logger myLogger = Logger.getLogger(Common.class.getName());

	private Email email;
	private JobName jobNamePanel;
	private CPUs cpus;
	private WallTime wallTimePanel;

	// for generic panel
	public Common() {
		super();
		buildPanel();
		add(getQueuePanel(), new CellConstraints(2, 4, 5, 1,
				CellConstraints.FILL, CellConstraints.FILL));
	}

	private void buildPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("58dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("54dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("77dlu"),
				FormFactory.RELATED_GAP_COLSPEC }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("42dlu"),
				RowSpec.decode("58dlu"), RowSpec.decode("128dlu"),
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("49dlu"),
				FormFactory.RELATED_GAP_ROWSPEC }));
		add(getWallTimePanel(), new CellConstraints(2, 3, 3, 1));
		add(getCpus(), new CellConstraints(6, 2, 1, 2, CellConstraints.FILL,
				CellConstraints.DEFAULT));
		getCpus().addValueListener(this);
		add(getJobNamePanel(), new CellConstraints(2, 2, 3, 1,
				CellConstraints.FILL, CellConstraints.FILL));
		add(getEmail(), new CellConstraints(2, 6, 5, 1, CellConstraints.FILL,
				CellConstraints.FILL));
	}

	/**
	 * @return
	 */
	protected CPUs getCpus() {
		if (cpus == null) {
			cpus = new CPUs();
		}
		return cpus;
	}

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
	protected JobName getJobNamePanel() {
		if (jobNamePanel == null) {
			jobNamePanel = new JobName();
		}
		return jobNamePanel;
	}

	/**
	 * @return
	 */

	public JPanel getPanel() {
		return this;
	}

	/**
	 * @return
	 */
	protected QueuePanel getQueuePanel() {
		if (queuePanel == null) {
			queuePanel = new QueuePanel();
		}
		return queuePanel;
	}

	/**
	 * @return
	 */
	protected WallTime getWallTimePanel() {
		if (wallTimePanel == null) {
			wallTimePanel = new WallTime();
		}
		return wallTimePanel;
	}

	protected void initialize() throws ModuleException {

		if (this.templateModule == null) {
			throw new RuntimeException(
					"Template not set for this module. This is a programming error.");
		}

		String useMds = ((org.vpac.grisu.client.model.template.modules.Common) templateModule)
				.getConfiguration().get(USE_MDS_CONFIG_OPTION_KEY);

		if (useMds == null || TemplateHelperUtils.NO_VALUE.equals(useMds)) {
			((org.vpac.grisu.client.model.template.modules.Common) templateModule)
					.useMds(true);
		} else {
			((org.vpac.grisu.client.model.template.modules.Common) templateModule)
					.useMds(false);
		}

		// initialize the Common TemplateModule with the application
		((org.vpac.grisu.client.model.template.modules.Common) templateModule)
				.setApplication(template.getApplicationName());
		((org.vpac.grisu.client.model.template.modules.Common) templateModule)
				.setDefaultVO();
		try {
			getWallTimePanel().setTemplateNode(
					this.templateModule.getTemplateNodes().get("Walltime"));
			getJobNamePanel().setTemplateNode(
					this.templateModule.getTemplateNodes().get("Jobname"));
			getCpus().setTemplateNode(
					this.templateModule.getTemplateNodes().get("CPUs"));
			getEmail().setTemplateNode(
					this.templateModule.getTemplateNodes().get("EmailAddress"));
		} catch (TemplateNodePanelException e) {
			myLogger.error("Could not initialize common panel: "
					+ e.getLocalizedMessage());
		}
		getQueuePanel()
				.initialize(
						(org.vpac.grisu.client.model.template.modules.Common) templateModule);

	}

	public void valueChanged(TemplateNodePanel panel, String newValue) {

		if (panel instanceof CPUs) {

		}

	}

}
