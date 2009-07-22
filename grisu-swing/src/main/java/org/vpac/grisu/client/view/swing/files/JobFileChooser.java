

package org.vpac.grisu.client.view.swing.files;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.omg.CORBA.INITIALIZE;
import org.vpac.grisu.client.control.EnvironmentManager;
import org.vpac.grisu.client.control.exceptions.InformationError;
import org.vpac.grisu.client.control.files.FileManagerDeleteHelpers;
import org.vpac.grisu.client.control.files.FileManagerTransferHelpers;
import org.vpac.grisu.client.control.files.FileTransfer;
import org.vpac.grisu.client.control.jobs.JobManager;
import org.vpac.grisu.client.model.files.GrisuFileObject;
import org.vpac.grisu.client.model.files.FileConstants;
import org.vpac.grisu.client.model.files.FileSystemException;
import org.vpac.grisu.client.model.files.FileSystemListFrontend;
import org.vpac.grisu.client.model.jobs.GrisuJobMonitoringObject;
import org.vpac.grisu.client.view.swing.preview.PreviewDialog;
import org.vpac.grisu.client.view.swing.utils.Utils;
import org.vpac.grisu.control.JobConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class JobFileChooser extends JPanel {
	
	static final Logger myLogger = Logger
	.getLogger(JobFileChooser.class.getName());

	private JButton createDirectoryButton;
	private JButton refreshButton;
	private JScrollPane scrollPane;
	private JMenuItem deleteMenuItem;
	private JMenuItem downloadMenuItem;
	private JMenuItem viewMenuItem;
	private JPopupMenu popupMenu;

	private FileSystemListFrontend fileSystem = null;
	private GrisuJobMonitoringObject job = null;

	private JobManager jobManagement = null;
	
	private EnvironmentManager em = null;

	private GrisuFileObject selectedFileForMenu = null;
	private String rememberedDirectory = new File(System
			.getProperty("user.home")).toURI().toString();

	private JList fileList;
	

	/**
	 * Create the panel
	 */
	public JobFileChooser(EnvironmentManager em) {
		super();
		this.em = em;
		this.jobManagement = em.getJobManager();
		initialize();
		//
	}
	
	private void initialize() {
		setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_COLSPEC,
					new ColumnSpec("100dlu"),
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					new RowSpec("default:grow(1.0)"),
					FormFactory.RELATED_GAP_ROWSPEC}));
			add(getScrollPane(), new CellConstraints("2, 4, 3, 1, fill, fill"));
			add(getRefreshButton(), new CellConstraints(4, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			add(getCreateDirectoryButton(), new CellConstraints(2, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
	}
	
	public void displayCreateDirectoryButton(boolean display){
		getCreateDirectoryButton().setVisible(display);
	}

	public GrisuFileObject getSelectedFile() {
		return (GrisuFileObject) getFileList().getSelectedValue();
	}

	public GrisuFileObject[] getSelectedFiles() {
		Object[] objects = getFileList().getSelectedValues();
		GrisuFileObject[] gfos = new GrisuFileObject[objects.length];
		for (int i = 0; i < objects.length; i++) {
			gfos[i] = (GrisuFileObject) objects[i];
		}
		return gfos;
	}
	
	private void userDoubleClicksFile() {
		new Thread() {
			public void run() {
//				JobFileChooser.this
//						.setCursor(Cursor
//								.getPredefinedCursor(Cursor.WAIT_CURSOR));

				setBusy(true);
				
				try {
				GrisuFileObject selected = getSelectedFile();
				if (selected.isRoot()) {
					// this should never happen
					throw new RuntimeException(
							"A job directory should never be of type root.");
				} else if (selected.getType() == FileConstants.TYPE_FOLDER) {
					// TODO check that user does not select
					// directory
					// above this one?
					changeCurrentDirectory(selected);
					fireUserInput(FileChooserEvent.CHANGED_FOLDER, new GrisuFileObject[] { selected });
				} else if (selected.getType() == FileConstants.TYPE_FILE) {
					fireUserInput(FileChooserEvent.SELECTED_FILE, new GrisuFileObject[] { selected });
				} else {
					throw new RuntimeException(
							"File is of no know type. That's a worry. Should never happen...");
				}
				} finally {
					setBusy(false);
				}
//				JobFileChooser.this
//						.setCursor(Cursor
//								.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}.start();
	}

	protected JList getFileList() {
		if (fileList == null) {
			fileList = new JList();
			fileList.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if ( e.getID() != KeyEvent.KEY_TYPED ) {
						if ( e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE ) {
							userDoubleClicksFile();
						}
					}
				}
			});
			fileList.setCellRenderer(new JobDirectoryFileObjectCellRenderer());
			fileList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					// // doubleclick
					if (e.getClickCount() == 2) {
						userDoubleClicksFile();
					}
//					if (e.getClickCount() == 1) {
//						BackendFileObject selected = getSelectedFile();
//					}
				}

				public void mousePressed(final MouseEvent e) {
					// right click
					if (e.getButton() == MouseEvent.BUTTON3) {
						int index = getFileList().locationToIndex(e.getPoint());
						getFileList().setSelectedIndex(index);
						showPopup(e);
					}
				}

			});
		}
		return fileList;
	}

	private void changeCurrentDirectory(GrisuFileObject file) {
		setBusy(true);
		try {
			int size = getFileList().getModel().getSize();
			fileSystem.setCurrentDirectory(file);
			fileSystem.invalidate();
		} catch (FileSystemException e) {
			setBusy(false);
			Utils.showErrorMessage(em, this, "couldNotChangeDirectory", e);
		} finally {
			setBusy(false);
		}
	}

	public GrisuFileObject getCurrentDirectory() {
		return fileSystem.getCurrentDirectory();
	}


	public void setJob(GrisuJobMonitoringObject job) throws FileSystemException {

		this.job = job;
		GrisuFileObject rootDir = em.getJobManager().getJobRootDirectory(job);
		rootDir.refresh();
		if ( rootDir != null )
			myLogger.debug("RootDir for job \""+job.getName()+"\": "+rootDir.getURI().toString());
		else {
			myLogger.error("RootDir for job \""+job.getName()+"\" is null. Not setting anything...");
			throw new RuntimeException("Can't access root directory.");
//			JOptionPane.showMessageDialog(JobFileChooser.this,
//				    "Could not retrieve root directory for job. This is a known bug and is currently under investigation.\nTry to close this dialog and open it again. Or restart Grisu.",
//				    "Connection error",
//				    JOptionPane.ERROR_MESSAGE);
//			return;
		}
		this.fileSystem = new FileSystemListFrontend(rootDir
				.getFileSystemBackend(), rootDir);
		getFileList().setModel(fileSystem);

	}

	private void showPopup(MouseEvent e) {
//		if (e.isPopupTrigger()) {

			selectedFileForMenu = getSelectedFile();
			if (selectedFileForMenu.getType() == FileConstants.TYPE_FOLDER) {
				getViewMenuItem().setEnabled(false);
			} else {
				getViewMenuItem().setEnabled(true);
			}
			try {
				if (selectedFileForMenu.getFileSystemBackend().getSite().equals(FileConstants.LOCAL_NAME)) {
					getDownloadMenuItem().setEnabled(false);
				} else {
					getDownloadMenuItem().setEnabled(true);
				}
			} catch (InformationError e1) {
				myLogger.error(e1.getLocalizedMessage());
				getDownloadMenuItem().setEnabled(false);
			}
			if (selectedFileForMenu.equals(getCurrentDirectory())
					|| JobConstants.translateStatusBack(job.getStatus()) < JobConstants.FINISHED_EITHER_WAY) {
				getDeleteMenuItem().setEnabled(false);
			} else {
				getDeleteMenuItem().setEnabled(true);
			}
			getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
//		}
	}

	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getViewMenuItem());
			popupMenu.add(getDownloadMenuItem());
			popupMenu.add(getDeleteMenuItem());
		}
		return popupMenu;
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	protected JMenuItem getViewMenuItem() {
		if (viewMenuItem == null) {
			viewMenuItem = new JMenuItem();
			viewMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					
					new Thread() {
						public void run() {
					setBusy(true);
					try {
					PreviewDialog pd = new PreviewDialog();
					pd.setFile(selectedFileForMenu);
					setBusy(false);
					pd.setVisible(true);
					} finally {
						setBusy(false);
					}
						}
					}.start();
				}
			});
			viewMenuItem.setText("View");
		}
		return viewMenuItem;
	}

	protected JMenuItem getDownloadMenuItem() {
		if (downloadMenuItem == null) {
			downloadMenuItem = new JMenuItem();
			downloadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {

					GrisuFileObject sourceFile = getSelectedFile();

					String filename = sourceFile.getName();

					JFileChooser chooser = new JFileChooser(new File(
							rememberedDirectory, filename));
					chooser.setDialogType(JFileChooser.SAVE_DIALOG);
					chooser.setSelectedFile(new File(rememberedDirectory,
							filename));
					int returnVal = chooser.showSaveDialog(JobFileChooser.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = chooser.getSelectedFile();

						final GrisuFileObject targetDirectory = em.getFileManager()
								.getFileObject(file.getParentFile().toURI());
						final GrisuFileObject sourceFileTemp = sourceFile;
						new Thread() {
							public void run() {
								try {
									setBusy(true);
//									JobFileChooser.this
//											.setCursor(Cursor
//													.getPredefinedCursor(Cursor.WAIT_CURSOR));
									em.getFileTransferManager().addTransfer(new GrisuFileObject[]{sourceFileTemp}, targetDirectory, FileTransfer.OVERWRITE_EVERYTHING, true);
//									FileManagerTransferHelpers
//											.transferFiles(
//													em.getServiceInterface(),
//													new BackendFileObject[] { sourceFileTemp },
//													targetDirectory, true);

								} catch (Exception e1) {
									e1.printStackTrace();
								} finally {
									setBusy(false);
//									JobFileChooser.this
//											.setCursor(Cursor
//													.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								}
							}
						}.start();
					}
				}
			});
			downloadMenuItem.setText("Download");
		}
		return downloadMenuItem;
	}
	
	public void setBusy(boolean busy) {
		
		if ( busy ) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		getFileList().setEnabled(!busy);
		getRefreshButton().setEnabled(!busy);
		
	}

	protected JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem();
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					new Thread() {
						public void run() {
							try {
//								JobFileChooser.this
//										.setCursor(Cursor
//												.getPredefinedCursor(Cursor.WAIT_CURSOR));
								setBusy(true);
								FileManagerDeleteHelpers
										.deleteFiles(
												em.getServiceInterface(),
												new GrisuFileObject[] { getSelectedFile() },
												true);
								getCurrentDirectory().refresh();
							} finally {
								setBusy(false);
//								JobFileChooser.this
//										.setCursor(Cursor
//												.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

							}
						}
					}.start();
				}
			});
			deleteMenuItem.setText("Delete");
		}
		return deleteMenuItem;
	}

	// ---------------------------------------------------------------------------------------
	// Event stuff
	private Vector<FileChooserParent> actionListeners;

	private void fireUserInput(int type, GrisuFileObject[] objects) {
		// if we have no mountPointsListeners, do nothing...
		if (actionListeners != null && !actionListeners.isEmpty()) {
			// create the event object to send
			FileChooserEvent event = new FileChooserEvent(type, objects);

			// make a copy of the listener list in case
			// anyone adds/removes mountPointsListeners
			Vector<FileChooserParent> targets;
			synchronized (this) {
				targets = (Vector<FileChooserParent>) actionListeners.clone();
			}

			// walk through the listener list and
			// call the userInput method in each
			Enumeration<FileChooserParent> e = targets.elements();
			while (e.hasMoreElements()) {
				FileChooserParent l = (FileChooserParent) e.nextElement();
				try {
					l.userInput(event);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void refreshCurrentDirectory() {

		if (fileSystem != null) {
			setBusy(true);
			try {
			fileSystem.refreshCurrentDirectory();
			} finally {
				setBusy(false);
			}
		}
	}

	// register a listener
	synchronized public void addUserInputListener(FileChooserParent l) {
		if (actionListeners == null)
			actionListeners = new Vector();
		actionListeners.addElement(l);
	}

	// remove a listener
	synchronized public void removeUserInputListener(FileChooserParent l) {
		if (actionListeners == null) {
			actionListeners = new Vector<FileChooserParent>();
		}
		actionListeners.removeElement(l);
	}
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getFileList());
		}
		return scrollPane;
	}
	protected JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent arg0) {
					refreshCurrentDirectory();
				}
			});
			URL picURL = getClass().getResource("/images/refresh.png");
			ImageIcon refresh = new ImageIcon(picURL);
			refreshButton.setIcon(refresh);
			
		}
		return refreshButton;
	}
	protected JButton getCreateDirectoryButton() {
		if (createDirectoryButton == null) {
			createDirectoryButton = new JButton();
			createDirectoryButton.setText("CreateDirectory");
			createDirectoryButton.setVisible(false);
		}
		return createDirectoryButton;
	}

}
