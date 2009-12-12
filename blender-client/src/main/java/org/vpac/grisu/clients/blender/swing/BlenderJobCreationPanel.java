package org.vpac.grisu.clients.blender.swing;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
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
import org.vpac.grisu.model.info.ApplicationInformation;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;

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
	private final ApplicationInformation ai;

	private GrisuBlenderJob job = null;
	private JScrollPane scrollPane;
	private JTabbedPane tabbedPane;
	private BlenderBasicJobPropertiesPanel blenderBasicJobPropertiesPanel;
	
	private String currentJobname = null;
	private String currentFqan = null;
	private BlenderAdvancedJobPropertiesPanel blenderAdvancedJobPropertiesPanel;
	
	private Thread submissionThread;
	

	public BlenderJobCreationPanel(ServiceInterface si) {

		this.si = si;
		this.em = GrisuRegistryManager.getDefault(si)
				.getUserEnvironmentManager();
		this.ai = GrisuRegistryManager.getDefault(si).getApplicationInformation("blender");


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
				RowSpec.decode("max(173dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		add(getTabbedPane(), "2, 2, 15, 1, fill, fill");
		add(getBtnSubmit(), "14, 4, 3, 1");
		add(getScrollPane(), "2, 6, 15, 1, fill, fill");
//		add(getStatusTextArea(), "2, 26, 15, 1, fill, fill");
		
		setFqan(getBlenderBasicJobPropertiesPanel().getSelectedFqan());
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

		submissionThread = new Thread() {
			public void run() {

				getBlenderBasicJobPropertiesPanel().lockUI(true);
				getBlenderAdvancedJobPropertiesPanel().lockUI(true);
				
				try {
				
				try {
					job = new GrisuBlenderJob(si, currentJobname,
							blenderBasicJobPropertiesPanel.getSelectedFqan());
				} catch (BatchJobException e) {
					EventBus.unsubscribe(currentJobname, this);
					addMessage(e.getLocalizedMessage()+"\n");
					return;
				}

				job.setBlenderFile(blendFileObject);

				job.setFirstFrame(blenderBasicJobPropertiesPanel.getFirstFrame());
				job.setLastFrame(blenderBasicJobPropertiesPanel.getLastFrame());

				job.setDefaultWalltimeInSeconds(blenderBasicJobPropertiesPanel.getCurrentWalltimeInSeconds());
				String outfilename = getBlenderAdvancedJobPropertiesPanel().getOutputFilename();
				if ( StringUtils.isBlank(outfilename) ) {
					outfilename = currentJobname;
				}
				job.setOutputFileName(currentJobname);

				if ( getBlenderAdvancedJobPropertiesPanel().getSubLocsToInclude() != null ) {
					job.setLocationsToInclude(getBlenderAdvancedJobPropertiesPanel().getSubLocsToInclude().toArray(new String[]{}));
				}
				if ( getBlenderAdvancedJobPropertiesPanel().getSubLocsToExclude() != null ) {
					job.setLocationsToExclude(getBlenderAdvancedJobPropertiesPanel().getSubLocsToExclude().toArray(new String[]{}));
				}
				
				
				try {
					job.createAndSubmitJobs(true);
				} catch (JobCreationException e) {
					EventBus.unsubscribe(currentJobname, this);
					addMessage(e.getLocalizedMessage()+"\n");
					return;
				} catch (JobSubmissionException e) {
					EventBus.unsubscribe(currentJobname, this);
					addMessage(e.getLocalizedMessage()+"\n");
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
					EventBus.unsubscribe(currentJobname, this);
					addMessage(e.getLocalizedMessage()+"\n");
					return;
				}
				
				addMessage("Job "+currentJobname+" submitted successfully.\n");
				
				} finally {
					getBlenderBasicJobPropertiesPanel().lockUI(false);
					getBlenderAdvancedJobPropertiesPanel().lockUI(false);
					setButtonToCancel(false);
				}
				
			}
		};
		
		submissionThread.start();

	}
	
	private void setButtonToCancel(final boolean cancel) {
		
		SwingUtilities.invokeLater(new Thread() {
			
			public void run() {
				if ( cancel ) {
					getBtnSubmit().setText("Cancel");
				} else {
					getBtnSubmit().setText("Submit");
				}
			}
			
		});
	}
	
	private void cancelJobSubmission() {
		
		if ( submissionThread != null ) {
			addMessage("Cancelling job submission.\n");
			submissionThread.interrupt();
			
		}
		
		lockUI(false);
		
		
	}

	private JButton getBtnSubmit() {
		if (btnSubmit == null) {
			btnSubmit = new JButton("Submit");
			btnSubmit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if ( "Submit".equals(btnSubmit.getText()) ) {
						setButtonToCancel(true);
						submitJob();
					} else {
						setButtonToCancel(false);
						cancelJobSubmission();
					}

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
	
	public void addMessage(final String message){
		SwingUtilities.invokeLater(new Thread() {
			
			public void run() {
				getStatusTextArea().append(message);
				getStatusTextArea().setCaretPosition(getStatusTextArea().getText().length());
			}
			
		});
	}
	
	public void lockUI(final boolean lock) {

			SwingUtilities.invokeLater(new Thread() {
				
				public void run() {
					if ( lock ) {
						Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
						BlenderJobCreationPanel.this.getTopLevelAncestor().setCursor(waitCursor);
					} else {
						Cursor defaultCursor = Cursor.getDefaultCursor();
						BlenderJobCreationPanel.this.getTopLevelAncestor().setCursor(defaultCursor);
					}
					getBtnSubmit().setEnabled(!lock);
					getBlenderBasicJobPropertiesPanel().lockUI(lock);
					getBlenderAdvancedJobPropertiesPanel().lockUI(lock);
				}
				
			});
	}

	@Override
	public void onEvent(String topic, Object data) {

		if ( data instanceof BatchJobEvent ) {
			addMessage(((BatchJobEvent)data).getMessage()+"\n");
		} else if ( data instanceof ActionStatusEvent ) {
			ActionStatusEvent d = ((ActionStatusEvent)data);
			addMessage(d.getPrefix()+d.getPercentFinished()+"% finished.\n");
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
	
	public void setFqan(String fqan) {
		
		currentFqan = fqan;
		getBlenderAdvancedJobPropertiesPanel().setAvailableSubLocs();
		
	}
	
	private BlenderAdvancedJobPropertiesPanel getBlenderAdvancedJobPropertiesPanel() {
		if (blenderAdvancedJobPropertiesPanel == null) {
			blenderAdvancedJobPropertiesPanel = new BlenderAdvancedJobPropertiesPanel(this);
		}
		return blenderAdvancedJobPropertiesPanel;
	}

	public Set<String> getAllPossibleSubmissionLocations() {
		
		return ai.getAvailableSubmissionLocationsForVersionAndFqan(GrisuBlenderJob.BLENDER_DEFAULT_VERSION, currentFqan);
		
	}
}
