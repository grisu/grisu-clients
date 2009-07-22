package org.vpac.grisu.client.view.swing.jobs;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.files.FileManagerTransferHelpers;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.files.JobFileChooser;
import org.vpac.grisu.client.view.swing.utils.Utils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class JobDirectoryPanel extends JPanel {
	
	private JButton externalButton;
	private JButton viewButton;
	private JButton downloadButton;
	private GrisuJobMonitoringObject job = null;
	
	private String rememberedDirectory = null;
	
	private EnvironmentManager em = null;

	private JobFileChooser jobFileChooser;
	
	// for wbbuilder pro -- don't use
	public JobDirectoryPanel() {
		super();
		initialize();
	}
	
	private void initialize() {
		setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("22px:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					new RowSpec("375px"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC}));
			add(getJobFileChooser(), new CellConstraints("2, 1, 5, 1, fill, fill"));
			add(getDownloadButton(), new CellConstraints(6, 3));
			add(getViewButton(), new CellConstraints(4, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			add(getExternalButton(), new CellConstraints(2, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
			//
	}
	
	/**
	 * Create the panel
	 */
	public JobDirectoryPanel(EnvironmentManager em) {
		super();
		this.em = em;
		initialize();
	}
	protected JobFileChooser getJobFileChooser() {
		if (jobFileChooser == null) {
			jobFileChooser = new JobFileChooser(em);
		}
		return jobFileChooser;
	}
	
	public void setJob(GrisuJobMonitoringObject job) {
		this.job = job;
		try {
			getJobFileChooser().setJob(job);
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected JButton getDownloadButton() {
		if (downloadButton == null) {
			downloadButton = new JButton();
			downloadButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						public void run() {

							JobDirectoryPanel.this
									.setCursor(Cursor
											.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
							getDownloadButton().setEnabled(false);
							getViewButton().setEnabled(false);
							GrisuFileObject[] selectedFiles = null;

							selectedFiles = getJobFileChooser().getSelectedFiles();


							if (selectedFiles.length < 1) {
								getDownloadButton().setEnabled(true);
								getViewButton().setEnabled(true);
								JobDirectoryPanel.this
								.setCursor(Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								return;
							}

							if (rememberedDirectory == null)
								rememberedDirectory = System
										.getProperty("user.home");

							JFileChooser chooser = new JFileChooser(
									new File(rememberedDirectory));
							chooser
									.setDialogType(JFileChooser.SAVE_DIALOG);
							chooser
									.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int returnVal = chooser
									.showSaveDialog(JobDirectoryPanel.this);

							if (returnVal == JFileChooser.CANCEL_OPTION) {
								getDownloadButton().setEnabled(true);
								getViewButton().setEnabled(true);
								JobDirectoryPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								return;
							}

							
							try {
								String currentFolder = getJobFileChooser().getCurrentDirectory().getURI().toString();
//								if ( currentFolder.startsWith("/") ) {
//									String currentFolder_temp = EnvironmentManager.getDefaultManager().convertToAbsoluteUrl(currentFolder);
//									if ( currentFolder == null ) {
//										throw new RemoteFileSystemException("Can't find file under any of the mountpoints: "+currentFolder_temp);
//									}
//									currentFolder = currentFolder_temp;
//								}

								em.getFileTransferManager().addDownload(selectedFiles, em.getFileManager().getFileObject(chooser.getSelectedFile().toURI()),
										currentFolder, FileManagerTransferHelpers.OVERWRITE_EVERYTHING, true);
//								FileManagerTransferHelpers.download(
//										job.getServiceInterface(), chooser
//												.getSelectedFile(),
//										currentFolder,
//										selectedFiles, FileManagerTransferHelpers.OVERWRITE_EVERYTHING, true);
							} catch (Exception e1) {
								Utils.showErrorMessage(em, 
										JobDirectoryPanel.this,
										"failedDownloads", e1);
							} finally {
								getDownloadButton().setEnabled(true);
								getViewButton().setEnabled(true);
								JobDirectoryPanel.this
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							}
							
							getDownloadButton().setEnabled(true);
							getViewButton().setEnabled(true);

							JobDirectoryPanel.this
									.setCursor(Cursor
											.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

						}
					}).start();

					// if(returnVal == JFileChooser.APPROVE_OPTION) {
					// // File file = chooser.getSelectedFile();
					// if ( file.isDirectory() )
					// file = new File(file, filename);
					// try {
					// FileHelpers.saveToDisk(source, file);
					// rememberedDirectory =
					// file.getParent().toString();
					// } catch (IOException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					// }
				}
			});
			downloadButton.setText("Download");
		}
		return downloadButton;
	}
	protected JButton getViewButton() {
		if (viewButton == null) {
			viewButton = new JButton();
			viewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					
					
				}
			});
			viewButton.setText("View");
		}
		return viewButton;
	}
	protected JButton getExternalButton() {
		if (externalButton == null) {
			externalButton = new JButton();
			externalButton.setText("View using external viewer");
			externalButton.setEnabled(false);
		}
		return externalButton;
	}

}
