package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.ExecutionFileSystem;
import org.vpac.grisu.client.view.swing.template.panels.GridResourceSuggestionPanel;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.MemoryInputPanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.Version;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GenericAuto extends AbstractModulePanel {
	
	private GridResourceSuggestionPanel gridResourceSuggestionPanel;
	private WallTime wallTime;
	private Version version;
	private Email email;
	private MemoryInputPanel memoryInputPanel;
	private CPUs cpus;
	private JobName jobName;
	
	private ExecutionFileSystem executionFileSystem = new ExecutionFileSystem();
	
	public GenericAuto() {
		super();
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("82dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("43dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("60dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("59dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("48dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("78dlu:grow(1.0)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("60dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getJobName(), new CellConstraints(2, 2, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getCpus(), new CellConstraints(8, 2, 1, 3, CellConstraints.FILL, CellConstraints.FILL));
		add(getMemoryInputPanel(), new CellConstraints(8, 8, CellConstraints.FILL, CellConstraints.FILL));
		add(getEmail(), new CellConstraints(2, 8, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getVersion(), new CellConstraints(2, 6, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getWallTime(), new CellConstraints(6, 6, 3, 1));
		add(getGridResourceSuggestionPanel(), new CellConstraints(2, 4, 5, 1, CellConstraints.FILL, CellConstraints.FILL));
	}

	@Override
	protected void initialize() throws ModuleException {
		
		// needs to be done before template node is set...
//		this.template.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME).setTemplateNodeValueSetter(getVersion());
//		this.template.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME).setTemplateNodeValueSetter(getGridResourceSuggestionPanel());
//		this.template.getTemplateNodes().get(TemplateTagConstants.EXECUTIONFILESYSTEM_TAG_NAME).setTemplateNodeValueSetter(executionFileSystem);
		
		try {
			getGridResourceSuggestionPanel().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME));
			getJobName().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.JOBNAME_TAG_NAME));
			getCpus().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.CPUS_TAG_NAME));
			getEmail().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME));
			getWallTime().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.WALLTIME_TAG_NAME));
			getMemoryInputPanel().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.MIN_MEM_TAG_NAME));
			getVersion().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME));
			
		} catch (TemplateNodePanelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ModuleException(this.getTemplateModule(), e);
		}
		
	}

	public JPanel getPanel() {
		// TODO Auto-generated method stub
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
	protected CPUs getCpus() {
		if (cpus == null) {
			cpus = new CPUs();
		}
		return cpus;
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
	protected WallTime getWallTime() {
		if (wallTime == null) {
			wallTime = new WallTime();
		}
		return wallTime;
	}
	/**
	 * @return
	 */
	protected GridResourceSuggestionPanel getGridResourceSuggestionPanel() {
		if (gridResourceSuggestionPanel == null) {
			gridResourceSuggestionPanel = new GridResourceSuggestionPanel();
		}
		return gridResourceSuggestionPanel;
	}

}
