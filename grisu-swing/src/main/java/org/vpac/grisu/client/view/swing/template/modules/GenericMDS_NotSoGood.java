package org.vpac.grisu.client.view.swing.template.modules;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;

import org.vpac.grisu.client.control.clientexceptions.JobCreationException;
import org.vpac.grisu.client.control.eventStuff.SubmissionObjectListener;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.model.SubmissionObject;
import org.vpac.grisu.client.model.template.modules.SubmissionObjectHolder;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.modules.common.VersionQueuePanel_NotSoGood;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.MemoryInputPanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericMDS_NotSoGood extends AbstractModulePanel {

	private WallTime wallTime;
	private MemoryInputPanel memoryInputPanel;
	private Email email;
	private VersionQueuePanel_NotSoGood versionQueuePanel;
	private CPUs cpus;
	private JobName jobName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3195321397180648037L;

	/**
	 * Create the panel
	 */
	public GenericMDS_NotSoGood() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("140px:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("88dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("113px"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("91px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("84dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		//
	}

	@Override
	protected void initialize() throws ModuleException {
		add(getJobName(), new CellConstraints("2, 2, fill, fill"));
		add(getCPUs(), new CellConstraints("6, 4, fill, fill"));
		add(getVersionQueuePanel(), new CellConstraints("2, 4, 3, 3, fill, fill"));
		add(getEmail(), new CellConstraints(2, 8, 5, 1));
		add(getMemoryInputPanel(), new CellConstraints(6, 6, CellConstraints.DEFAULT, CellConstraints.TOP));
		add(getWallTime(), new CellConstraints(4, 2, 3, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
		
		try {
			getJobName().setTemplateNode(this.templateModule.getTemplateNodes().get("Jobname"));
			getCPUs().setTemplateNode(this.templateModule.getTemplateNodes().get("CPUs"));
			getEmail().setTemplateNode(this.templateModule.getTemplateNodes().get("EmailAddress"));
			getWallTime().setTemplateNode(this.templateModule.getTemplateNodes().get("Walltime"));
			getMemoryInputPanel().setTemplateNode(this.templateModule.getTemplateNodes().get("MinMem"));
		} catch (TemplateNodePanelException e) {

			throw new ModuleException(this.templateModule, e);
		}
	}

	public JPanel getPanel() {
		return this;
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
	/**
	 * @return
	 */
	protected CPUs getCPUs() {
		if (cpus == null) {
			cpus = new CPUs();
		}
		return cpus;
	}
	/**
	 * @return
	 */
	protected VersionQueuePanel_NotSoGood getVersionQueuePanel() {
		if (versionQueuePanel == null) {
			versionQueuePanel = new VersionQueuePanel_NotSoGood(this.templateModule);
		}
		return versionQueuePanel;
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
	protected MemoryInputPanel getMemoryInputPanel() {
		if (memoryInputPanel == null) {
			memoryInputPanel = new MemoryInputPanel();
		}
		return memoryInputPanel;
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
	
}
