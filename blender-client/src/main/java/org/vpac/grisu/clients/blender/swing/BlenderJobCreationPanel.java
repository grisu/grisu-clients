package org.vpac.grisu.clients.blender.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.vpac.grisu.clients.blender.BlendFile;
import org.vpac.grisu.clients.blender.GrisuBlenderJob;
import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.exceptions.BatchJobException;
import org.vpac.grisu.control.exceptions.JobSubmissionException;
import org.vpac.grisu.frontend.control.clientexceptions.JobCreationException;
import org.vpac.grisu.frontend.model.events.ActionStatusEvent;
import org.vpac.grisu.frontend.model.events.BatchJobEvent;
import org.vpac.grisu.model.GrisuRegistryManager;
import org.vpac.grisu.model.UserEnvironmentManager;
import org.vpac.grisu.settings.ClientPropertiesManager;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import com.jidesoft.swing.FolderChooser;
import com.jidesoft.swing.RangeSlider;
import javax.swing.JTabbedPane;

public class BlenderJobCreationPanel extends JPanel implements
		EventTopicSubscriber {

	public static final String LAST_BLENDER_FILE_DIR = "lastBlenderFileDir";

	public static final FileFilter BLEND_FILE_FILTER = new FileFilter() {

		@Override
		public String getDescription() {
			return "*.blend";
		}

		@Override
		public boolean accept(File f) {
			if (f.isDirectory() || f.getName().endsWith(".blend")) {
				return true;
			} else {
				return false;
			}
		}
	};

	private BlendFile blendFileObject;
	private File dotBlendFile;
	private File fluidsFolder;
	private JButton btnSubmit;
	private JTextArea statusTextArea;

	private final ServiceInterface si;
	private final UserEnvironmentManager em;

	private GrisuBlenderJob job = null;
	private JScrollPane scrollPane;
	private JTabbedPane tabbedPane;
	private BlenderBasicJobPropertiesPanel blenderBasicJobPropertiesPanel;
	
	private String currentJobname = null;
	private BlenderAdvancedJobPropertiesPanel blenderAdvancedJobPropertiesPanel;

	public BlenderJobCreationPanel(ServiceInterface si) {

		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("50dlu", true), Sizes.constant("50dlu", true)), 1),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(59dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("23dlu", true), Sizes.constant("50dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.constant("23dlu", true), Sizes.constant("50dlu", true)), 0),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getTabbedPane(), "2, 2, 15, 1, fill, fill");
		add(getBtnSubmit(), "14, 4, 3, 1");
		add(getScrollPane(), "2, 6, 15, 1, fill, fill");
//		add(getStatusTextArea(), "2, 26, 15, 1, fill, fill");
	}
	
	public String setBlendFile(BlendFile file) {
		this.blendFileObject = file;
		
		String name = file.getFile().getName();
		int i = name.lastIndexOf(".");
		if ( i > 0 ) {
			name = name.substring(0, i);
		}
		setJobname(em.calculateUniqueJobname(name));
		return this.currentJobname;
	}
	
	public void setJobname(String jobname) {
		if ( this.currentJobname != null) {
			EventBus.unsubscribe(this.currentJobname, this);
		}
		this.currentJobname = jobname;
		EventBus.subscribe(this.currentJobname, this);
	}



	private void submitJob() {

		Thread thread = new Thread() {
			public void run() {

				try {
					job = new GrisuBlenderJob(si, currentJobname,
							blenderBasicJobPropertiesPanel.getSelectedFqan());
				} catch (BatchJobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}

				job.setBlenderFile(blendFileObject);

				job.setFirstFrame(blenderBasicJobPropertiesPanel.getFirstFrame());
				job.setLastFrame(blenderBasicJobPropertiesPanel.getLastFrame());

				job.setDefaultWalltimeInSeconds(blenderBasicJobPropertiesPanel.getCurrentWalltimeInSeconds());
				job.setOutputFileName(currentJobname);

//				job.setSitesToInclude(new String[]{"vpac"});

				try {
					job.createAndSubmitJobs();
				} catch (JobCreationException e) {
					e.printStackTrace();
					return;
				} catch (JobSubmissionException e) {
					e.printStackTrace();
					return;
				}

			}
		};
		
		thread.start();

	}

	private JButton getBtnSubmit() {
		if (btnSubmit == null) {
			btnSubmit = new JButton("Submit");
			btnSubmit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					submitJob();

				}
			});
		}
		return btnSubmit;
	}

	private JTextArea getStatusTextArea() {
		if (statusTextArea == null) {
			statusTextArea = new JTextArea();
			statusTextArea.setEditable(false);
		}
		return statusTextArea;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getStatusTextArea());
		}
		return scrollPane;
	}

	@Override
	public void onEvent(String topic, Object data) {

		if ( data instanceof BatchJobEvent ) {
			getStatusTextArea().append(((BatchJobEvent)data).getMessage()+"\n");	
		} else if ( data instanceof ActionStatusEvent ) {
			ActionStatusEvent d = ((ActionStatusEvent)data);
			getStatusTextArea().append(d.getPrefix()+d.getPercentFinished()+"% finished.\n");
		}
		
	}
	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.addTab("Basic properties", null, getBlenderBasicJobPropertiesPanel(), null);
			tabbedPane.addTab("Advanced", null, getBlenderAdvancedJobPropertiesPanel(), null);
		}
		return tabbedPane;
	}
	private BlenderBasicJobPropertiesPanel getBlenderBasicJobPropertiesPanel() {
		if (blenderBasicJobPropertiesPanel == null) {
			blenderBasicJobPropertiesPanel = new BlenderBasicJobPropertiesPanel(this);
		}
		return blenderBasicJobPropertiesPanel;
	}
	
	public String[] getAllFqans() {
		
		return em.getAllAvailableFqans();
		
	}
	private BlenderAdvancedJobPropertiesPanel getBlenderAdvancedJobPropertiesPanel() {
		if (blenderAdvancedJobPropertiesPanel == null) {
			blenderAdvancedJobPropertiesPanel = new BlenderAdvancedJobPropertiesPanel();
		}
		return blenderAdvancedJobPropertiesPanel;
	}
}
