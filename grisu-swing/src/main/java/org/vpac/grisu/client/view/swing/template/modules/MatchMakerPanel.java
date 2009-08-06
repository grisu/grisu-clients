package org.vpac.grisu.client.view.swing.template.modules;

import javax.swing.JPanel;

import org.vpac.grisu.client.TemplateTagConstants;
import org.vpac.grisu.client.control.template.ModuleException;
import org.vpac.grisu.client.view.swing.template.AbstractModulePanel;
import org.vpac.grisu.client.view.swing.template.panels.ApplicationVersion;
import org.vpac.grisu.client.view.swing.template.panels.CPUs;
import org.vpac.grisu.client.view.swing.template.panels.CandidateHost;
import org.vpac.grisu.client.view.swing.template.panels.Email;
import org.vpac.grisu.client.view.swing.template.panels.JobName;
import org.vpac.grisu.client.view.swing.template.panels.MemoryInputPanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanel;
import org.vpac.grisu.client.view.swing.template.panels.TemplateNodePanelException;
import org.vpac.grisu.client.view.swing.template.panels.ValueListener;
import org.vpac.grisu.client.view.swing.template.panels.WallTime;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MatchMakerPanel extends AbstractModulePanel implements ValueListener {
	
	private JobName jobName;
	private JobName jobName_1;
	private CPUs us;
	private MemoryInputPanel memoryInputPanel;
	private Email email;
	
	private String currentVersion = null;
	private ApplicationVersion applicationVersion;
	private WallTime wallTime;
	private CandidateHost candidateHost;
	
	public MatchMakerPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(179dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(97dlu;default):grow"),},
			new RowSpec[] {
				RowSpec.decode("max(64dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(37dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(35dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(47dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(72dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(73dlu;default):grow"),}));
		add(getJobName_1(), "1, 1, fill, fill");
		add(getUs(), "3, 1, 1, 3, fill, fill");
		add(getWallTime(), "1, 3, 1, 3, fill, fill");
		add(getApplicationVersion(), "1, 7, fill, fill");
		getApplicationVersion().addValueListener(this);
		add(getMemoryInputPanel(), "3, 5, 1, 3, fill, fill");
		add(getCandidateHost(), "1, 9, 3, 1, fill, fill");
		add(getEmail(), "1, 11, 3, 1, fill, fill");
	}

	@Override
	protected void initialize() throws ModuleException {
//		// TODO Auto-generated method stub
//		// needs to be done before template node is set...
//		this.template.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME).setTemplateNodeValueSetter(getApplicationVersion());
//		this.template.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME).setTemplateNodeValueSetter(getCandidateHost());
		
		try {
			getCandidateHost().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.HOSTNAME_TAG_NAME));
			getJobName_1().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.JOBNAME_TAG_NAME));
			getUs().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.CPUS_TAG_NAME));
			getEmail().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.EMAIL_ADDRESS_TAG_NAME));
			getWallTime().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.WALLTIME_TAG_NAME));
			getMemoryInputPanel().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.MIN_MEM_TAG_NAME));
			getApplicationVersion().setTemplateNode(this.templateModule.getTemplateNodes().get(TemplateTagConstants.VERSION_TAG_NAME));
			
		} catch (TemplateNodePanelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ModuleException(this.getTemplateModule(), e);
		}

	}

	public JPanel getPanel() {
		return this;
	}

	private JobName getJobName_1() {
		if (jobName_1 == null) {
			jobName_1 = new JobName();
		}
		return jobName_1;
	}
	private CPUs getUs() {
		if (us == null) {
			us = new CPUs();
		}
		return us;
	}
	private MemoryInputPanel getMemoryInputPanel() {
		if (memoryInputPanel == null) {
			memoryInputPanel = new MemoryInputPanel();
		}
		return memoryInputPanel;
	}
	private Email getEmail() {
		if (email == null) {
			email = new Email();
		}
		return email;
	}

	public void setCurrentVersion(String version) {
		
		
	}

	public void valueChanged(TemplateNodePanel panel, String newValue) {

		if ( panel instanceof ApplicationVersion ) {
			this.currentVersion = newValue;
			getCandidateHost().valueChanged(panel, newValue);
		
		}
		
	}
	private ApplicationVersion getApplicationVersion() {
		if (applicationVersion == null) {
			applicationVersion = new ApplicationVersion();
		}
		return applicationVersion;
	}
	private WallTime getWallTime() {
		if (wallTime == null) {
			wallTime = new WallTime();
		}
		return wallTime;
	}
	private CandidateHost getCandidateHost() {
		if (candidateHost == null) {
			candidateHost = new CandidateHost();
		}
		return candidateHost;
	}
}
