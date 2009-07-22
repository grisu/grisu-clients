package org.vpac.grisu.client.view.swing.preview;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.files.FileManagerTransferHelpers;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.files.FileChooserEvent;
import org.vpac.grisu.client.view.swing.files.FileChooserParent;
import org.vpac.grisu.client.view.swing.files.JobFileChooser;
import org.vpac.grisu.client.view.swing.files.SiteFileChooserPanel;
import org.vpac.grisu.client.view.swing.utils.Utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class JobFileListWithPreviewPanel extends JPanel implements
		FileChooserParent {
	
	public static final String PREVIEW_PANEL = "Preview";
	public static final String FILE_PANEL = "Files";

	private JComboBox comboBox;
	private SiteFileChooserPanel rightSiteFileChooserPanel;
	private JButton copyButton;
	private JPanel holderPanel;
	
	private JPanel panel;
	private PreviewPanel previewPanel;
	private JobFileChooser jobFileChooser;
	private JSplitPane splitPane;

	private GrisuJobMonitoringObject job = null;
	private EnvironmentManager em = null;
	
	/**
	 * Create the panel
	 */
	public JobFileListWithPreviewPanel(EnvironmentManager em) {
		super();
		this.em = em;
		setLayout(new BorderLayout());
		add(getSplitPane(), BorderLayout.CENTER);
	}

	protected JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			//
			getJobFileChooser().addUserInputListener(this);
			splitPane.setLeftComponent(getJobFileChooser());
			splitPane.setRightComponent(getPanel());
		}
		return splitPane;
	}

	protected JobFileChooser getJobFileChooser() {
		if (jobFileChooser == null) {
			jobFileChooser = new JobFileChooser(em);
		}
		return jobFileChooser;
	}

	protected PreviewPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new PreviewPanel();
			previewPanel.setName(PREVIEW_PANEL);
		}
		return previewPanel;
	}

	public void setJob(GrisuJobMonitoringObject job) {
		this.job = job;
		getJobFileChooser().setJob(job);
	}

	public void userInput(FileChooserEvent event) {
		
		if ( FileChooserEvent.SELECTED_FILE == event.getType() ) {
			getComboBox().setSelectedItem(PREVIEW_PANEL);
			getPreviewPanel().previewFile(event.getSelectedFile());
		}

		//getPreviewPanel().previewFile(event.getSelectedFile());
		getPreviewPanel().revalidate();

	}


	protected JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("93px:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("45dlu"),
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("90px:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC}));
			panel.add(getHolderPanel(), new CellConstraints(2, 2, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
			panel.add(getCopyButton(), new CellConstraints(2, 4, CellConstraints.LEFT, CellConstraints.DEFAULT));
			panel.add(getComboBox(), new CellConstraints(4, 4, CellConstraints.FILL, CellConstraints.DEFAULT));
		}
		return panel;
	}
	protected JPanel getHolderPanel() {
		if (holderPanel == null) {
			holderPanel = new JPanel();
			holderPanel.setLayout(new CardLayout());
			holderPanel.add(getPreviewPanel(), PREVIEW_PANEL);
			holderPanel.add(getRightSiteFileChooserPanel(), FILE_PANEL);
		}
		return holderPanel;
	}
	protected JButton getCopyButton() {
		if (copyButton == null) {
			copyButton = new JButton();
			copyButton.setEnabled(false);
			copyButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					new Thread() {
						public void run() {
					GrisuFileObject targetDirectory = getRightCurrentDirectory();
					GrisuFileObject[] sourceFiles = getJobFileChooser().getSelectedFiles();
					try {
						JobFileListWithPreviewPanel.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						em.getFileTransferManager().addTransfer(sourceFiles, targetDirectory, FileTransfer.OVERWRITE_EVERYTHING, true);
//						FileManagerTransferHelpers
//								.transferFiles(em.getServiceInterface(),
//										sourceFiles, targetDirectory, true);
						getRightSiteFileChooserPanel().refreshCurrentDirectory();
					} catch (Exception e1) {
						Utils.showErrorMessage(em, JobFileListWithPreviewPanel.this,
								"couldNotTransfer", e1);
					} finally {
						JobFileListWithPreviewPanel.this.setCursor(Cursor
								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
					
						}
					}.start();
					
					
				}
			});
			copyButton.setText("copy ->");
		}
		return copyButton;
	}
	
	public GrisuFileObject getRightCurrentDirectory() {
		return getRightSiteFileChooserPanel().getCurrentDirectory();
	}
	
	protected SiteFileChooserPanel getRightSiteFileChooserPanel() {
		if (rightSiteFileChooserPanel == null) {
			rightSiteFileChooserPanel = new SiteFileChooserPanel(em);
			rightSiteFileChooserPanel.setName("siteFileChooserPanel");
			rightSiteFileChooserPanel.changeToSite(FileConstants.LOCAL_NAME);
			File file = new File(System.getProperty("user.home"));
			rightSiteFileChooserPanel.changeCurrentDirectory(em.getFileManager().getFileObject(file.toURI()));
		}
		return rightSiteFileChooserPanel;
	}
	protected JComboBox getComboBox() {
		if (comboBox == null) {
			comboBox = new JComboBox(new String[]{PREVIEW_PANEL, FILE_PANEL});
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					switchPanels((String)getComboBox().getSelectedItem());
				}
			});
		}
		return comboBox;
	}
	
	public void switchPanels(String panel) {
	    CardLayout cl = (CardLayout)(getHolderPanel().getLayout());
	    cl.show(getHolderPanel(), panel);
	    
	    if ( PREVIEW_PANEL.equals(panel) ) {
	    	// clear panel
	    	getPreviewPanel().previewFile(null);
	    	getCopyButton().setEnabled(false);
	    } else {
	    	getCopyButton().setEnabled(true);
	    }
	    
	}
	
	
}
