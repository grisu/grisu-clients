package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.ExecutionFileSystem;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.MemoryInputPanel;
import org.vpac.grisu.client.view.swing.template.panels.SubmissionLocation;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.Version;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericMDS extends AbstractModulePanel {

	private SubmissionLocation submissionLocation;
	private Version version;
	private Email email;
	private MemoryInputPanel memoryInputPanel;
	private CPUs us;
	private WallTime wallTime;
	private JobName jobName;
	
	// will not get displayed
	private ExecutionFileSystem executionFileSystem = new ExecutionFileSystem();
	
	/**
	 * Create the panel
	 */
	public GenericMDS() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("38dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("54dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("102dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("56dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("51dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("35dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("20dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("52dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getJobName(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));
		add(getWallTime(), new CellConstraints(4, 2, 3, 1));
		add(getCPUs(), new CellConstraints(6, 4, 1, 3));
		add(getMemoryInputPanel(), new CellConstraints(6, 8, 1, 5));
		add(getEmail(), new CellConstraints(2, 10, 3, 3, CellConstraints.FILL, CellConstraints.FILL));
		add(getVersion(), new CellConstraints(2, 4, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getSubmissionLocation(), new CellConstraints(2, 6, 3, 3));
		//
	}

	@Override
	protected void initialize() throws ModuleException {
		try {
			
			// needs to be done before template node is set...
			this.template.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME).setTemplateNodeValueSetter(getSubmissionLocation());
			this.template.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME).setTemplateNodeValueSetter(getVersion());
			this.template.getTemplateNodes().get(TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME).setTemplateNodeValueSetter(executionFileSystem);

			
			getJobName().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.JOBNAME_TAG_NAME));
			getCPUs().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.CPUS_TAG_NAME));
			getEmail().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME));
			getWallTime().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.WALLTIME_TAG_NAME));
			getMemoryInputPanel().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.MIN_MEM_TAG_NAME));
			getVersion().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME));
			getSubmissionLocation().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME));
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
	protected WallTime getWallTime() {
		if (wallTime == null) {
			wallTime = new WallTime();
		}
		return wallTime;
	}
	/**
	 * @return
	 */
	protected CPUs getCPUs() {
		if (us == null) {
			us = new CPUs();
		}
		return us;
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
	protected Email getEmail() {
		if (email == null) {
			email = new Email();
		}
		return email;
	}
	/**
	 * @return
	 */
	protected Version getVersion() {
		if (version == null) {
			version = new Version();
		}
		return version;
	}
	/**
	 * @return
	 */
	protected SubmissionLocation getSubmissionLocation() {
		if (submissionLocation == null) {
			submissionLocation = new SubmissionLocation();
		}
		return submissionLocation;
	}

}
