package org.vpac.grisu.client.view.swing.jobs;

import java.awt.Color;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.FileSystemListFrontend;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.files.FilePanelWithOptionalPreview;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@Deprecated
public class DefaultJobFilesPanel extends JPanel {

	private FilePanelWithOptionalPreview stderrPanel;
	private FilePanelWithOptionalPreview stdoutPanel;
	
	private GrisuJobMonitoringObject job = null;
	private FileSystemListFrontend fs = null;
	
	private EnvironmentManager em = null;
	/**
	 * Create the panel
	 */
	public DefaultJobFilesPanel(EnvironmentManager em) {
		super();
		this.em = em;
		setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				new RowSpec("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		add(getStdoutPanel(), new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.TOP));
		add(getStderrPanel(), new CellConstraints(2, 4, CellConstraints.FILL, CellConstraints.TOP));
		//
	}
	protected FilePanelWithOptionalPreview getStdoutPanel() {
		if (stdoutPanel == null) {
			stdoutPanel = new FilePanelWithOptionalPreview();
			stdoutPanel.setBorder(new LineBorder(Color.black, 1, false));
		}
		return stdoutPanel;
	}
	protected FilePanelWithOptionalPreview getStderrPanel() {
		if (stderrPanel == null) {
			stderrPanel = new FilePanelWithOptionalPreview();
			stderrPanel.setBorder(new LineBorder(Color.black, 1, false));
		}
		return stderrPanel;
	}
	
	public void setJob(GrisuJobMonitoringObject job) {
		this.job = job;
		GrisuFileObject jobDir = null;
		try {
			jobDir = em.getFileManager().getFileObject(new URI(job.getJobDirectory()));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.fs = new FileSystemListFrontend(jobDir.getFileSystemBackend(), jobDir); 
		try {
			GrisuFileObject stdout = fs.getFileSystemBackend().getFileObject(new URI(job.getStdout()));
			if ( stdout != null )
				getStdoutPanel().setFile("stdout", stdout);
			GrisuFileObject stderr = fs.getFileSystemBackend().getFileObject(new URI(job.getStderr()));
			if ( stderr != null )
				getStderrPanel().setFile("stderr", stderr);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
